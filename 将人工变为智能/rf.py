# -*- coding: utf-8 -*-
# author:luyh
# contact: luyh@bingosoft.net
# datetime:2020/6/5 0:09
# software: PyCharm
import os
import joblib
import pandas as pd
from sklearn.ensemble import RandomForestClassifier


if __name__ == '__main__':
    # 读取数据
    dataset_path = 'trainset.csv'
    df_train = pd.read_csv(dataset_path)

    # PassengerId 是主键，没有训练意义，舍弃该特征


    y = df_train.get('SalePrice')

    X = df_train.drop('SalePrice', axis=1)

    rf = RandomForestClassifier()

    # 拟合
    model = rf.fit(X, y)

    model_path = 'model/rf.pkl'

    joblib.dump(model, model_path, compress=3)