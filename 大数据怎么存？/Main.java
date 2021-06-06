package main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.ListBucketsRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Tag;

public class Main {
	private final static String bucketName = "sync-bucket";
	private final static String accessKey = "6983BF707B2751F695BC";
	private final static String secretKey = "WzFGMDFDRkVGQTUxRjhENEMxMzlGODkzNTJEREY3";
	private final static String serviceEndpoint = "http://10.16.0.1:81";
	private final static String signingRegion = "";
	private final static String savePath = "D:\\shixun\\folder\\share";
	private static long PARTSIZE = 20 <<20;//ԭ����Ϊ���Σ�Ӧ�в��ı��ȫ�ֱ���
	private static int period=5;//ɨ����
	
	

	public static void main(String[] args) throws IOException, InterruptedException, ParseException {
		final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		final ClientConfiguration ccfg = new ClientConfiguration().withUseExpectContinue(false);

		final EndpointConfiguration endpoint = new EndpointConfiguration(serviceEndpoint, signingRegion);

		final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withClientConfiguration(ccfg)
				.withEndpointConfiguration(endpoint)
				.withPathStyleAccessEnabled(true)
				.build();		
		//����Ϊ������������
		



		
//		//��������ʱ��bucket�е��ļ�ͬ�������أ��������ز����ڵ��ļ��ͽ��ϵ��ļ�

		
		List<S3ObjectSummary> listResult = getBucketFiles(s3, bucketName);
		System.out.println("���ڴ�bucketͬ���ļ������ء�����");
		int down=0;
		for(S3ObjectSummary file: listResult) {
			File localFile = new File(savePath+"\\"+file.getKey());
			if(!localFile.exists()) {
				System.out.println("�ļ�"+file.getKey()+"�ڱ��ز����ڣ���Ҫ����");				
				if(file.getKey().endsWith("/"))
					localFile.mkdirs();
				else
					downloadFile(s3, bucketName, file.getKey(), savePath);
					++down;
			}
			else if( localFile.isFile() ){
				//System.out.println("111111"+s3.getObjectMetadata(bucketName, file.getKey()).getUserMetaDataOf("addedModifiedTime"));
				long s3ModifiedTime =Stringtolong(s3.getObjectMetadata(bucketName, file.getKey()).getUserMetaDataOf("addedModifiedTime")) ;
				long localModifiedTime = localFile.lastModified()/1000*1000;
				
				
//				System.out.println(s3ModifiedTime);
//				System.out.println(localModifiedTime);
//				System.out.println(longtoString(s3ModifiedTime));
//				System.out.println(longtoString(localModifiedTime));
				
				if(localModifiedTime < s3ModifiedTime) {
					System.out.println("�ļ�"+file.getKey()+"�ڱ��صİ汾���ϣ���Ҫ���ظ���");
					downloadFile(s3,bucketName,file.getKey(),savePath);
					down++;
				}
			}				
		}
		if(down==0)
			System.out.println("����ͬ��"+"\n");
		else
			System.out.println("ͬ�����"+"\n");

		
		//������ӡ��޸ġ�ɾ�����ļ���bucket����ͬ���Ĳ���
		System.out.println("| ��ʼɨ�� |");
		long startTime;
		long endTime;
		int scannum=0;
		do {
			System.out.printf("��%d��ɨ�迪ʼ��\n",scannum);
			startTime = System.currentTimeMillis();
			List<String> localFiles = new ArrayList<>();
			List<S3ObjectSummary> S3Files = getBucketFiles(s3,bucketName);
			getLocalFiles(new File(savePath), localFiles,"");
			for(String file : localFiles) {
				//System.out.println("�ļ���"+file);
				File localFile = new File(savePath+"/"+file);
				if(localFile.exists()) {
					if(!s3.doesObjectExist(bucketName, file) ) {
						System.out.println("s3�������ļ�"+file+"����Ҫ�ϴ�");
						if(localFile.isFile())
							uploadFile(s3,bucketName,file,localFile);
						else {
							s3.putObject(bucketName, file, "");
						}
					}else if( localFile.isFile() ){
						long s3ModifiedTime = Stringtolong(s3.getObjectMetadata(bucketName, file).getUserMetaDataOf("addedModifiedTime")) ;						
//						System.out.println(localFile.lastModified());
//						System.out.println(s3ModifiedTime);
						if(localFile.lastModified()/1000*1000 > s3ModifiedTime) {
							
							System.out.println(file+"�ļ���s3�İ汾���ϣ���Ҫ�ϴ�����");
							uploadFile(s3,bucketName,file,localFile);
						}

					}	
				}
			}
			
			for(S3ObjectSummary S3File: S3Files) {
				
				if(!localFiles.contains(S3File.getKey())) {
					System.out.println("���ز������ļ�"+S3File.getKey()+"��s3Ӧͬ��ɾ��");
					s3.deleteObject(bucketName, S3File.getKey());
				}
				
			}
			endTime = System.currentTimeMillis();
			if((endTime - startTime) < (1000*period)) {
				System.out.printf("����ɨ������´�ɨ�轫��%d���ʼ\n\n",period);
				++scannum;
				Thread.sleep(1000*period*1 - (endTime - startTime));
			}
		}while(true);
	}
	
	//����ͷ���չʾ��ʱ��stringת��
	public static String longtoString(long time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return dateFormat.format(cal.getTime());
	}
	
	public static long Stringtolong(String time) throws ParseException {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse(time);
		return date.getTime();
	}

	//��ȡ�����ļ�list����
	public static void getLocalFiles(File dir, List<String> lists, String prefix) throws IOException {
		if(dir.exists()) {
			for(File file : dir.listFiles()) {
				//System.out.println(file.getName());
				if(file.isDirectory()) {
					lists.add(prefix+file.getName()+"/");
					getLocalFiles(file,lists,prefix+file.getName()+"/");
				}
				else {
					lists.add(prefix+file.getName());
				}
			}
		}
	}
	
	//��ȡbucket�ļ�list����
	public static List<S3ObjectSummary> getBucketFiles( AmazonS3 s3, String bucketName) {
		List<S3ObjectSummary> listResult = new ArrayList<S3ObjectSummary>();
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketName);
		listObjectsRequest.setMaxKeys(50);
		ObjectListing listObjects = s3.listObjects(listObjectsRequest);
		do {
		  listResult.addAll(listObjects.getObjectSummaries());
		  if (listObjects.isTruncated()) {
		    ListObjectsRequest request = new ListObjectsRequest();
		    request.setBucketName(listObjectsRequest.getBucketName());
		    request.setMarker(listObjects.getNextMarker());
		    listObjects =  s3.listObjects(request);
		  } else {
		    break;
		  }
		} while (listObjects != null);
		
		return listResult;
	}
	
	
	public static void downloadFile( AmazonS3 s3,String bucketName, String keyName, String savePath) {
		final String filePath = Paths.get(savePath, keyName).toString();
		File file = new File(filePath);
		
		S3Object o = null;
		
		S3ObjectInputStream s3is = null;
		FileOutputStream fos = null;
		
		try {
			// Step 1: Initialize.
			ObjectMetadata oMetaData = s3.getObjectMetadata(bucketName, keyName);
			final long contentLength = oMetaData.getContentLength();
			final GetObjectRequest downloadRequest = 
					new GetObjectRequest(bucketName, keyName);

			fos = new FileOutputStream(file);

			// Step 2: Download parts.
			long filePosition = 0;
			for (int i = 1; filePosition < contentLength; i++) {
				// Last part can be less than partSize MB. Adjust part size.
				long partSize = Math.min(PARTSIZE, contentLength - filePosition);

				// Create request to download a part.
				downloadRequest.setRange(filePosition, filePosition + partSize);
				o = s3.getObject(downloadRequest);

				// download part and save to local file.
				System.out.format("Downloading part %d\n", i);
				
				filePosition += partSize+1;
				s3is = o.getObjectContent();
				byte[] read_buf = new byte[64 * 1024];
				int read_len = 0;
				while ((read_len = s3is.read(read_buf)) > 0) {
					fos.write(read_buf, 0, read_len);
				}
			}

			// Step 3: Complete.
			System.out.println("Completing download");

			System.out.format("save %s to %s\n", keyName, filePath);			
			
			
			ObjectMetadata om  = s3.getObjectMetadata(bucketName, keyName);			
			
			long modifiedTime =  Stringtolong(om.getUserMetaDataOf("addedModifiedTime"));
			file.setLastModified(modifiedTime);//���ı����ļ����޸�ʱ����ͬ��s3�ļ��Զ�����޸�ʱ��header
			
		} catch (Exception e) {
			System.err.println(e.toString());
			
			System.exit(1);
		} finally {
			if (s3is != null) try { s3is.close(); } catch (IOException e) { }
			if (fos != null) try { fos.close(); } catch (IOException e) { }
		}
		System.out.println("Done!");
	}
	
	

	
	public static void uploadFile(AmazonS3 s3, String bucketName, String keyName, File file) {		
		//System.out.println(longtoString(file.lastModified()));
		// Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
		ArrayList<PartETag> partETags = new ArrayList<PartETag>();
		
		long contentLength = file.length();
		String uploadId = null;
		
		try {
			// Step 1: Initialize.
			InitiateMultipartUploadRequest initRequest = 
					new InitiateMultipartUploadRequest(bucketName, keyName);
			uploadId = s3.initiateMultipartUpload(initRequest).getUploadId();
			System.out.format("Created upload ID was %s\n", uploadId);

			// Step 2: Upload parts.
			long filePosition = 0;
			for (int i = 1; filePosition < contentLength; i++) {
				// Last part can be less than PARTSIZE B. Adjust part size.
				long partSize = Math.min(PARTSIZE, contentLength - filePosition);

				// Create request to upload a part.
				UploadPartRequest uploadRequest = new UploadPartRequest()
						.withBucketName(bucketName)
						.withKey(keyName)
						.withUploadId(uploadId)
						.withPartNumber(i)
						.withFileOffset(filePosition)
						.withFile(file)
						.withPartSize(partSize);		

				// Upload part and add response to our list.
				System.out.format("Uploading part %d\n", i);
				partETags.add(s3.uploadPart(uploadRequest).getPartETag());

				filePosition += partSize;
			}

			// Step 3: Complete.
			System.out.println("Completing upload");
			CompleteMultipartUploadRequest compRequest = 
					new CompleteMultipartUploadRequest(bucketName, keyName, uploadId, partETags);

			s3.completeMultipartUpload(compRequest);
			
			//System.out.println(longtoString(file.lastModified()));
			
			ObjectMetadata om = new ObjectMetadata();
			
			
			
			om.addUserMetadata("addedModifiedTime",longtoString(file.lastModified()));//��һ��header��¼�ļ�ʵ���ڱ��ص��޸�ʱ�䣬���Ǽ������ϴ�������ʱ����Դ���header Last-Modified
			CopyObjectRequest request = new CopyObjectRequest(bucketName,keyName,bucketName,keyName);
			request.setNewObjectMetadata(om);
			try {
				s3.copyObject(request);
			} catch (AmazonClientException e) {
				System.err.println(e.toString());
				System.exit(1);
			}

			
		} catch (Exception e) {
			System.err.println(e.toString());
			if (uploadId != null && !uploadId.isEmpty()) {
				// Cancel when error occurred
				System.out.println("Aborting upload");
				s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, keyName, uploadId));
			}
			System.exit(1);
		}
		System.out.println("Done!");
	}
	
}
