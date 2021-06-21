import java.util.Properties

import com.bingocloud.{ClientConfiguration, Protocol}
import com.bingocloud.auth.BasicAWSCredentials
import com.bingocloud.services.s3.AmazonS3Client
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.nlpcn.commons.lang.util.IOUtil

object kuozhan3 {

  //kafka参数
  val topic = "wfz_kuozhan3_4"
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val s3Content = readFileFromSql()
    println(s3Content)
    produceToKafka(s3Content)
  }

  /**
   * 从 mysql 中读取数据
   *
   * @return
   */
  def readFileFromSql(): String = {
    import java.sql.DriverManager
    val url = "jdbc:mysql://bigdata130.depts.bingosoft.net:24306/user11_db"
    val properties = new Properties()
    properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver")
    properties.setProperty("user", "user11")
    properties.setProperty("password", "pass@bingo11")
    val connection = DriverManager.getConnection(url, properties)
    val statement = connection.createStatement()
    val sql = "show tables"
    val resultSet = statement.executeQuery(sql)
    var content = new StringBuilder()
    val columnCount = resultSet.getMetaData.getColumnCount

    while(resultSet.next){
      for(i <- 1 to columnCount){
        content.append(resultSet.getString(i)+"\t")
      }
      content.append("\n")
    }
    content.toString()
  }

  /**
   * 把数据写入到kafka中
   *
   * @param sqlContent 要写入的内容
   */
  def produceToKafka(sqlContent: String): Unit = {
    val props = new Properties
    props.put("bootstrap.servers", bootstrapServers)
    props.put("acks", "all")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    val dataArr = sqlContent.split("\n")
    for (s <- dataArr) {
      if (!s.trim.isEmpty) {
        val record = new ProducerRecord[String, String](topic, null, s)
        producer.send(record)
      }
    }
    producer.flush()
    producer.close()
  }
}