import pandas as pd
import os

from pandas_profiling import ProfileReport
from sklearn.preprocessing import StandardScaler
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from scipy.stats import norm
from sklearn.preprocessing import StandardScaler
from scipy import stats
from sklearn.preprocessing import LabelEncoder



if __name__ == '__main__':
    # 读取数据
    dataset_path = os.path.join('~/s3data/dataset', 'HousePricePredict', 'train.csv')
    df_train = pd.read_csv(dataset_path)
    dataset_path = os.path.join('~/s3data/dataset', 'HousePricePredict', 'test.csv')
    df_test = pd.read_csv(dataset_path)
    dataset_path = os.path.join('~/s3data/dataset', 'HousePricePredict', 'sample_submission.csv')
    result = pd.read_csv(dataset_path)

    # 显示所有列
    pd.set_option('display.max_columns', None)
    # 显示所有行
    # pd.set_option('display.max_rows', None)
    # 设置value的显示长度为100，默认为50
    # pd.set_option('max_colwidth', 100)

    # 前十条示例数据

    # 显示数据
    #print(df_train) #1460*81
    #print(df_test) #1459*80
    # 数据基本信息
    print("数据基本信息：")
    #print(df_train.info())
    print("-" * 40)


    # 数据分析
    print("数据简要分析：")
    #print(df_train.describe(include='all'))
    print("-" * 40)

    #关联图
    print("关联图：")
    corrmat = df_train.corr()
    #f, ax = plt.subplots(figsize=(12, 9))
    #sns.heatmap(corrmat, vmax=.8, square=True,annot=True)
    # plt.show()
    print("-" * 40)


    # 缺失值处理
    print("缺失值数量及百分比：")
    total = df_train.isnull().sum().sort_values(ascending=False)
    percent = (df_train.isnull().sum() / df_train.isnull().count()).sort_values(ascending=False)
    missing_data = pd.concat([total, percent], axis=1, keys=['Total', 'Percent'])
    # print(missing_data.head(20))
    print("-" * 40)
    # PoolQC,MiscFeature,Alley,Fence,FireplaceQu,LotFrontage 缺失大量数据，舍弃这些特征
    # GarageX和BsmtX数据的信息由其中一个包括，舍弃
    # “MasVnrArea”和“MasVnrType“与”YearBuilt“和”OverallQual“高度相关，舍弃
    df_train = df_train.drop((missing_data[missing_data['Total'] > 1]).index, 1)
    df_test = df_test.drop((missing_data[missing_data['Total'] > 1]).index, 1)

    # 少数缺失值，可由众数填充
    print("填充:")
    df_train.get('Electrical').fillna(df_train.get('Electrical').mode()[0], inplace=True)

    total = df_test.isnull().sum().sort_values(ascending=False)
    percent = (df_test.isnull().sum() / df_test.isnull().count()).sort_values(ascending=False)
    missing_data = pd.concat([total, percent], axis=1, keys=['Total', 'Percent'])
    # print(missing_data.head(20))
    tc = (missing_data[missing_data['Total'] > 0]).index
    for i in tc:
        df_test.get(i).fillna(df_test.get(i).mode()[0], inplace=True)

    print("填充完毕:")
    # print(df_train.isnull().sum().max())
    # print(df_test.isnull().sum().max())

    # print(df_train) #1460*63
    # print(df_test) #1459*62
    print("-" * 40)



    # 关联性
    print("关联性：")
    corrmat = df_train.corr(method='pearson')
    # print(corrmat)
    # 特征太多，去掉关联性低的

    #type(corrmat)为 35*35 df 舍弃24个

    print("去除相关性较低的列：")
    df_train = df_train.drop(corrmat[abs(corrmat['SalePrice']) < 0.5].index, 1)
    #print(df_train) #1460*39
    df_test = df_test.drop(corrmat[abs(corrmat['SalePrice']) < 0.5].index, 1)
    #print(df_test) #1459*38
    # 28个离散特征，10个连续特征







    print('分析离散型数据：')
    # # 生成详细分析报告，选中practice2目录，标题栏点击Tools->Deployment->Download from xxx 即可同步服务器的文件
    # profile = ProfileReport(df_test, title="Pandas Profiling Report")
    # # 右键点击analysis.html，点击Open in Browser，点击浏览器，即可打开分析报告
    # profile.to_file("analysis_AD_test.html")

    for i in range(df_train.shape[1]):
        if (isinstance(df_train.iloc[0, i], str)):
            print(df_train.columns[i])
    # 离散数据处理,去除没有意义的
    # 几乎同一种：'Street','Utilities','Condition2','RoofMatl','Heating'
    # 几乎同一种且已被包含：'Exterior1st','Exterior2nd'
    # 不方便特征提取，关联性较低：'Neighborhood','RoofStyle','Functional','SaleType'
    # 多去掉几行！!!!!!
    drop_list=['Street','Utilities','Condition2','RoofMatl','Heating','Exterior1st','Exterior2nd','Neighborhood','RoofStyle','Functional','SaleType']
    df_train = df_train.drop(drop_list, 1)
    df_test = df_test.drop(drop_list, 1)

    print('-'*40)
    for i in range(df_train.shape[1]):
        if (isinstance(df_train.iloc[0, i], str)):
            print(df_train.columns[i])



    print("离散数据处理后：")
    # print(df_train)# 1460*32
    # print(df_test)


    # 标准化
    print("标准化处理后：")
    ss = StandardScaler()
    print(df_train.shape[1])
    for i in range(df_train.shape[1]-1):
        if (isinstance(df_train.iloc[0, i], str)):
            continue
        else:
            df_train[df_train.columns[i]] = ss.fit_transform(df_train[[df_train.columns[i]]])
            df_test[df_test.columns[i]] = ss.fit_transform(df_test[[df_test.columns[i]]])
    # print(df_train)
    # print(df_test)



    # 把房价离散化，高于平均值180000的为1，反之为0
    for i in range(df_train.shape[0]):
        if(df_train.iloc[i,df_train.shape[1]-1]>180000):
            df_train.iloc[i,df_train.shape[1]-1]=1
        else:
            df_train.iloc[i,df_train.shape[1]-1]=0
    print(df_train)

    for i in range(result.shape[0]):
        if(result.iloc[i,result.shape[1]-1]>180000):
            result.iloc[i,result.shape[1]-1]=1
        else:
            result.iloc[i,result.shape[1]-1]=0
    print('result')
    result=result.drop('Id',1)
    print(result)

    # 输出结果
    path = 'preprocess_train_data.csv'
    df_train.to_csv(path, index=False)
    path = 'preprocess_test_data.csv'
    df_test.to_csv(path, index=False)
    path = 'result.csv'
    result.to_csv(path, index=False)












