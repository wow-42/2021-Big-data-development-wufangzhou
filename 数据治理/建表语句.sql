-- drop schema if exists e_shop CASCADE;
-- create schema e_shop;

-- 用户
drop table if exists e_shop.users;
create table e_shop.users
(
  "user_id" text COLLATE "pg_catalog"."default" primary key,
  "name" text COLLATE "pg_catalog"."default",
  "sex" text COLLATE "pg_catalog"."default",
  " register_year_month " text COLLATE "pg_catalog"."default",
  "phone_number" text COLLATE "pg_catalog"."default",
  "age" numeric(10),
  "e_mail" text COLLATE "pg_catalog"."default"  
);
-- Add comments to the table 
comment on table e_shop.users
  is '用户';
-- Add comments to the columns 
comment on column e_shop.users.user_id
  is '用户id';
comment on column e_shop.users.name
  is '姓名';
comment on column e_shop.users.sex
  is '性别';
comment on column e_shop.users.register_year_month
  is '月份';
 comment on column e_shop.users.phone_number
  is '手机号';
 comment on column e_shop.users.age
  is '年龄';
 comment on column e_shop.users.e_mail
  is '电子邮件';
 
 
-- 订单
drop table if exists e_shop.orders;
create table e_shop.orders
(
  "order_id" text COLLATE "pg_catalog"."default" primary key,
  "goods_id" text COLLATE "pg_catalog"."default",
  "shop_id" text COLLATE "pg_catalog"."default",
  "user_id" text COLLATE "pg_catalog"."default",
  "year_month" text COLLATE "pg_catalog"."default",  
  "sales" numeric(10),
  "receive_city" text COLLATE "pg_catalog"."default"
);
-- Add comments to the table 
comment on table e_shop.orders
  is '订单';
-- Add comments to the columns 
comment on column e_shop.orders.order_id
  is '订单id';
comment on column e_shop.orders.goods_id
  is '商品id';
comment on column e_shop.orders.shop_id
  is '商家id';
comment on column e_shop.orders.user_id
  is '用户id';
 comment on column e_shop.orders.sales
  is '销售额';
 comment on column e_shop.orders.receive_city
  is '收货城市';
 comment on column e_shop.orders.year_month
  is '月份';
 

 

-- 商品
drop table if exists e_shop.goods;
create table e_shop.goods
(
  "goods_id" text COLLATE "pg_catalog"."default" primary key,
  "goods_category" text COLLATE "pg_catalog"."default"
);
-- Add comments to the table 
comment on table e_shop.goods
  is '商品';
-- Add comments to the columns 
comment on column e_shop.goods.goods_id
  is '商品id';
comment on column e_shop.goods.goods_category
  is '商品类别';

 
 
-- 商家
drop table if exists e_shop.shops;
create table e_shop.shops
(
  "shop_id" text COLLATE "pg_catalog"."default" primary key,
  "shop_name" text COLLATE "pg_catalog"."default"
);
-- Add comments to the table 
comment on table e_shop.shops
  is '商家';
-- Add comments to the columns 
comment on column e_shop.shops.shop_id
  is '商家id';
comment on column e_shop.shops.shop_name
  is '商家名称';

-- 用户登录表
drop table if exists e_shop.signin;
create table e_shop.signin
(
  "user_id" text COLLATE "pg_catalog"."default" primary key,
  "account" text COLLATE "pg_catalog"."default",
  "password" text COLLATE "pg_catalog"."default"  
);
-- Add comments to the table 
comment on table e_shop.signin
  is '用户登录表';
-- Add comments to the columns 
comment on column e_shop.signin.user_id
  is '用户id';
comment on column e_shop.signin.account
  is '账号';
comment on column e_shop.signin.password
  is '密码';

 
 -- 商家上架商品表
drop table if exists e_shop.shop_goods;
create table e_shop.shop_goods
(
  "shop_id" text COLLATE "pg_catalog"."default" not null,
  "goods_id" text COLLATE "pg_catalog"."default" not null
);
-- Add comments to the table 
comment on table e_shop.shop_goods
  is '商家上架商品表';
-- Add comments to the columns 
comment on column e_shop.shop_goods.shop_id
  is '商家id';
comment on column e_shop.shop_goods.goods_id
  is '商品id';

