# -*- coding: utf-8 -*-
# author:luyh
# contact: luyh@bingosoft.net
# datetime:2020/6/5 10:29
# software: PyCharm
import os

import joblib
import pandas as pd
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix, classification_report, \
    accuracy_score
from yellowbrick import ROCAUC
from yellowbrick.classifier import ClassPredictionError, ClassificationReport, ConfusionMatrix, DiscriminationThreshold
from yellowbrick.model_selection import LearningCurve, CVScores, FeatureImportances, RFECV, ValidationCurve
import numpy as np

if __name__ == '__main__':
    # 显示所有行
    pd.set_option('display.max_rows', None)

    train_data_path = 'trainset.csv'
    test_data_path = 'testset.csv'
    result_path='result.csv'

    # 读取数据
    train_df = pd.read_csv(train_data_path)
    y_train = train_df.get('SalePrice')
    X_train = train_df.drop('SalePrice', axis=1)


    test_df = pd.read_csv(test_data_path)

    result=pd.read_csv(result_path)
    y_test = result.get('SalePrice')
    X_test = test_df

    # print('-'*40)
    # print([y for y in X_train.columns if y not in X_test.columns])# Electrical_Mix
    # #print(type(X_train.columns))
    # #print(X_test.columns)
    # print('-' * 40)
    list=[0]*X_test.shape[0]
    X_test['Electrical_Mix']=list


    # PassengerId
    id_test = result

    model_path = 'model/rf.pkl'
    model = joblib.load(model_path)

    y_pred = model.predict(X_test)
    y_pred=y_pred.astype(float)
    id_test['prediction'] = y_pred

    print('typeeeeeeeeeeeeeeee')
    print(type(id_test))




    # 查看预测结果
    print(f"预测结果:{id_test}")
    print('-'*40)
    print(type(y_test))
    print(type(y_pred))

    # 评估
    # 准确率
    accuracy_score_value = accuracy_score(y_test, y_pred)
    print(f"准确率:{accuracy_score_value}")

    precision_score_value = precision_score(y_test, y_pred)
    print(f"精确率:{precision_score_value}")

    recall_score_value = recall_score(y_test, y_pred)
    print(f"召回率:{recall_score_value}")

    f1_score_value = f1_score(y_test, y_pred)
    print(f"f1值:{f1_score_value}")

    confusion_matrix_value = confusion_matrix(y_test, y_pred)
    print(f"混淆矩阵:{confusion_matrix_value}")

    report = classification_report(y_test, y_pred)
    print(f"分类报告:{report}")

    # 可视化
    # ROCAUC
    visualizer = ROCAUC(model)
    visualizer.score(X_test, y_test)
    visualizer.show('')

    # 分类预测
    visualizer = ClassPredictionError(model)
    visualizer.score(X_test, y_test)
    visualizer.show('')

    # 分类报告
    visualizer = ClassificationReport(model)
    visualizer.score(X_test, y_test)
    visualizer.show('')

    # 混淆矩阵
    visualizer = ConfusionMatrix(model)
    visualizer.score(X_test, y_test)
    visualizer.show()

    # 阈值选择
    visualizer = DiscriminationThreshold(model)
    visualizer.fit(X_train, y_train)
    visualizer.show()

    # 学习率
    visualizer = LearningCurve(
        model, scoring='f1_weighted'
    )
    visualizer.fit(X_train, y_train)
    visualizer.show()

    # 交叉验证
    visualizer = CVScores(model, cv=5, scoring='f1_weighted')
    visualizer.fit(X_train, y_train)
    visualizer.show()

    # 特征重要性
    visualizer = FeatureImportances(model)
    visualizer.fit(X_train, y_train)
    visualizer.show()



    # # 特征递归消减
    # visualizer = RFECV(model, cv=5, scoring='f1_weighted')
    # visualizer.fit(X_train, y_train)
    # visualizer.show()
    #
    # # 特征选择
    # visualizer = ValidationCurve(
    #     model, param_name="max_depth",
    #     param_range=np.arange(1, 11), cv=5, scoring="f1_weighted"
    # )
    # visualizer.fit(X_train, y_train)
    # visualizer.show()
