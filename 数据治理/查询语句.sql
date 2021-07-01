--按月查看总体销售额
select year_month, sum(sales)
from e_shop.orders
group by year_month;

--按月查看新增注册人数
select register_year_month, count(*) 
from e_shop.users
group by register_year_month;

--按月查看城市的销售额
select year_month, receive_city, sum(sales)
from e_shop.orders
group by year_month,receive_city;

--按月查看城市、商品类别的销售额
select year_month, receive_city, goods_category,sum(sales) 
from e_shop.orders inner join e_shop.goods on e_shop.orders.goods_id=e_shop.goods.goods_id
group by year_month,receive_city,goods_category;

--按月查看性别、商品类别的销售额
select year_month, sex, goods_category,sum(sales)
from e_shop.orders inner join e_shop.goods on e_shop.orders.goods_id=e_shop.goods.goods_id
inner join e_shop.users on e_shop.orders.user_id=e_shop.users.user_id
group by year_month,sex,goods_category;