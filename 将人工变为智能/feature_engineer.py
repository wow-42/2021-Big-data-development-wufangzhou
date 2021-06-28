# -*- coding: utf-8 -*-
# author:luyh
# contact: luyh@bingosoft.net
# datetime:2020/6/4 21:46
# software: PyCharm
import os
import pandas as pd
import re
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import OneHotEncoder

if __name__ == '__main__':
    # 读取数据
    dataset_path = os.path.join('../zuoye', 'preprocess_train_data.csv')
    df_train = pd.read_csv(dataset_path)
    dataset_path = os.path.join('../zuoye', 'preprocess_test_data.csv')
    df_test = pd.read_csv(dataset_path)

    # 显示所有列
    pd.set_option('display.max_columns', None)

    # 前十条示例数据
    print('示例数据：')
    print(df_train.head(10))
    print('-'*40)
    print(df_test.head(10))
    print('-' * 40)

    # 特征提取
    # print('类型')
    # print(type(df_train.get('Condition1')))


    # 统计计数
    #print(df_train.get('Condition1').value_counts())

    # 定义 Condition1 社会地位字典，减少分类数;'HouseStyle','ExterQual','ExterCond','HeatingQC','CentralAir','KitchenQual','PavedDrive'映射为数字
    Condition1_dict = {
        "Artery": "adStreet",
        "Feedr": "adStreet",
        "Norm": "adNormal",
        "RRNn": "adRail",
        "RRAn": "adRail",
        "RRNe": "adRail",
        "RRAe": "adRail",
        "PosN": "adPos",
        "PosA": "adPos"
    }

    HouseStyle_dict = {
        "1Story": 1,
        "1.5Fin": 1.5,
        "1.5Unf": 1.5,
        "2Story": 2,
        "2.5Fin": 2.5,
        "2.5Unf": 2.5,
        "SFoyer": 1.5,
        "SLvl": 1.5
    }

    CentralAir_dict = {
        "N": 0,
        "Y": 1
    }
    Qual_dict = {
        "Ex": 5,
        "Gd": 4,
        "TA": 3,
        "Fa": 2,
        "Po": 1
    }
    PavedDrive_dict = {
        "Y": 3,
        "P": 2,
        "N": 1
    }



    df_train['Condition1'] = df_train.get('Condition1').map(Condition1_dict)

    df_train['HouseStyle'] = df_train.get('HouseStyle').map(HouseStyle_dict)
    df_train['ExterQual'] = df_train.get('ExterQual').map(Qual_dict)
    df_train['ExterCond'] = df_train.get('ExterCond').map(Qual_dict)
    df_train['HeatingQC'] = df_train.get('HeatingQC').map(Qual_dict)
    df_train['CentralAir'] = df_train.get('CentralAir').map(CentralAir_dict)
    df_train['KitchenQual'] = df_train.get('KitchenQual').map(Qual_dict)
    df_train['PavedDrive'] = df_train.get('PavedDrive').map(PavedDrive_dict)


    df_test['Condition1'] = df_test.get('Condition1').map(Condition1_dict)

    df_test['HouseStyle'] = df_test.get('HouseStyle').map(HouseStyle_dict)
    df_test['ExterQual'] = df_test.get('ExterQual').map(Qual_dict)
    df_test['ExterCond'] = df_test.get('ExterCond').map(Qual_dict)
    df_test['HeatingQC'] = df_test.get('HeatingQC').map(Qual_dict)
    df_test['CentralAir'] = df_test.get('CentralAir').map(CentralAir_dict)
    df_test['KitchenQual'] = df_test.get('KitchenQual').map(Qual_dict)
    df_test['PavedDrive'] = df_test.get('PavedDrive').map(PavedDrive_dict)


    print(df_train.get('Condition1').value_counts())


    #print(df_train.info())
    df_num = df_train.select_dtypes(include=[np.number])
    df_cat = df_train.select_dtypes(exclude=[np.number])
    # 独热编码
    enc = OneHotEncoder(handle_unknown='ignore')
    cat_enc_data = enc.fit_transform(df_cat).toarray()
    df_cat_enc = pd.DataFrame(data=cat_enc_data, columns=enc.get_feature_names(df_cat.columns))
    print(df_cat_enc)
    # 合并数值类型和分类类型
    df_train = pd.merge(df_num, df_cat_enc, left_index=True, right_index=True)


    df_num = df_test.select_dtypes(include=[np.number])
    df_cat = df_test.select_dtypes(exclude=[np.number])
    # 独热编码
    enc = OneHotEncoder(handle_unknown='ignore')
    cat_enc_data = enc.fit_transform(df_cat).toarray()
    df_cat_enc = pd.DataFrame(data=cat_enc_data, columns=enc.get_feature_names(df_cat.columns))
    print(df_cat_enc)
    # 合并数值类型和分类类型
    df_test = pd.merge(df_num, df_cat_enc, left_index=True, right_index=True)

    print(df_train)
    print(df_test)



    df_train.to_csv("trainset.csv", index=False)
    df_test.to_csv("testset.csv", index=False)
