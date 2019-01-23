--SQLServer数据库建表语句--


--创建用户信息表--
create table T_USER(
	userid int identity primary key,--主键，唯一标识
	username varchar(20) not NULL,--登录用户名
	userpwd varchar(36) not null,--登录用户密码
	nickname varchar(50) not null,--用户昵称
	pwd_question varchar(100),--找回密码问题
	pwd_answer varchar(60),--找回密码问题答案
	userimg varchar(500),--用户头像地址
	user_roleid int default 0,--用户角色，默认：普通用户
	last_usetime varchar(20),--最后使用时间
	createtime varchar(19) default CONVERT(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--插入超级管理员账号信息
insert into T_USER(username,userpwd,nickname,pwd_question,pwd_answer,user_roleid) values('admin','2d37b86e51cbd0667e3526f67c085e03','超级管理员','我的手机号后4位？','9822',1);

--创建站点信息表
create table T_STATION(
	id varchar(36) primary key,--站点ID
	areacode varchar(10),--站点行政区划编码
	name varchar(20) not null,--站点名称
	ename varchar(50) not null,--站点拼音
	station_no varchar(10) not null,--站点编号
	soil_no varchar(10) not null,--土壤湿度站点编号
	x decimal(8, 4),--位置信息：站点X坐标-经度
	y decimal(8, 4),--位置信息：站点Y坐标-纬度
	createtime varchar(19) default CONVERT(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建用户关注的站点信息表
create table T_USER_STATION(
	id int identity primary key,--主键，唯一标识
	username varchar(20) not null,--用户名
	stationId varchar(36) not null,--关注站点ID
	createtime varchar(19) default CONVERT(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建字典分类信息表--
create table T_DIC(
	id int identity primary key,--主键，唯一标识
	code varchar(20) not NULL,--字典分类编码
	name varchar(50) not NULL,--字典分类名称
	createtime varchar(19) default CONVERT(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);
--创建字典信息表--
create table T_DICDATA(
	id int identity primary key,--主键，唯一标识
	diccode varchar(20) not NULL,--字典分类编码
	code varchar(20) not NULL,--字典编码
	name varchar(30) not null,--字典名称
	maxno int not null default 0,--字典分类最大编号，用于生成字典编码
	rank int not null default 0,--排序
	createtime varchar(19) default CONVERT(varchar, getdate(), 120),--创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);

--创建天气预报信息表
create table T_WEATHER_NOW(
	id int identity primary key,--主键，唯一标识
	stationId varchar(36) not null,--城市ID
	datetime varchar(20) not NULL,--日期YYYY-MM-DD
	week varchar(80) not NULL,--星期
	weather varchar(50),--天气
	icon varchar(200),--天气图标地址
	temp decimal(3,0),--当前温度
	lotemp decimal(3,0),--低温
	hitemp decimal(3,0),--高温
	humi decimal(3,0),--湿度
	windf varchar(50),--风向
	winds varchar(50),--风力
	kqzl varchar(50),--空气质量
	zwx varchar(50),--紫外线
	pm decimal(3,0),--PM值
	rc varchar(50),--日出
	rl varchar(50),--日落
	updatetime varchar(10),--更新时间
	createtime varchar(19) default CONVERT(varchar, getdate(), 120),--创建时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建15天天气预报数据表
create table T_WEATHER_DAY15(
	id int identity primary key,--主键，唯一标识
	stationId varchar(36) not null,--城市ID
	datetime varchar(20) not NULL,--日期YYYY-MM-DD
	jsondata varchar(4000),--15天预报数据json串
	updatetime varchar(10),--更新时间
	createtime varchar(19) default CONVERT(varchar, getdate(), 120)--创建时间
)

--创建站点格点产品信息表
create table T_STATION_GRDDATA(
	id int identity primary key,--主键，唯一标识
	stationId varchar(36) not null,--站点ID
	datetime varchar(20) not NULL,--日期YYYY-MM-DD
	grd_name varchar(30) not null,--格点文件名称
	group_dir varchar(30) not null,--格点产品目录
	crop_code varchar(10) not null,--作物编码
	element_time varchar(10) not null,--格点预报时次
	element_name varchar(20) not null,--格点产品要素
	element_value varchar(200),--格点产品要素值
	updatetime varchar(10),--更新时间
	createtime varchar(19) default CONVERT(varchar, getdate(), 120),--创建时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建天气预报预警信息表
create table T_WEATHER_ALARM(
	id int identity primary key,--主键，唯一标识
	stationId varchar(36) not null,--站点ID
	station_no varchar(10) not null,--站点编号
	title varchar(100) not null,--预警标题
	alarmicon varchar(500),--预警图标
	alarmstate varchar(10) not null,--预警状态：发布|解除
	content varchar(2000) not null,--预警内容
	defense_guide varchar(2000),--防御指南
	alermtime varchar(20),--预警时间
	createtime varchar(19) default CONVERT(varchar, getdate(), 120) --创建时间
)

--创建天气预警采集信息表
create table T_ALARM_RECORD(
	id int identity primary key,--主键，唯一标识
	title varchar(100) not null,--预警标题
	pageurl varchar(500) not null, --页面地址
	alermtime varchar(20),--预警时间
	createtime varchar(19) default CONVERT(varchar, getdate(), 120) --创建时间
)

--创建病虫害作物分类信息表20181128
create table T_CROP(
	id int identity primary key,--主键，唯一标识
	code varchar(20) not null,--作物分类编码
	name varchar(20) not null,--作物名称
	createtime varchar(19) default CONVERT(varchar, getdate(), 120), --创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120), --修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
--初始化数据
insert into T_CROP(code,name) values('10101','蚕豆');
insert into T_CROP(code,name) values('10604','大豆');
insert into T_CROP(code,name) values('10102','甘薯');
insert into T_CROP(code,name) values('10103','高梁');
insert into T_CROP(code,name) values('10104','高粱');
insert into T_CROP(code,name) values('10106','谷子');
insert into T_CROP(code,name) values('10107','果树');
insert into T_CROP(code,name) values('10108','花生');
insert into T_CROP(code,name) values('10109','绿豆');
insert into T_CROP(code,name) values('10502','棉花');
insert into T_CROP(code,name) values('10110','树木');
insert into T_CROP(code,name) values('10105','水稻');
insert into T_CROP(code,name) values('10111','豌豆');
insert into T_CROP(code,name) values('10112','向日葵');
insert into T_CROP(code,name) values('10302','小麦');
insert into T_CROP(code,name) values('10602','油菜');
insert into T_CROP(code,name) values('10405','玉米');
insert into T_CROP(code,name) values('10113','芝麻');
insert into T_CROP(code,name) values('10114','贮粮');

--创建作物病虫害防治信息表20181128
create table T_CROP_DISEASE(
	id int identity primary key,--主键，唯一标识
	cropcode varchar(20) not null,--作物分类编码
	diseasename varchar(30) not null,--病虫害名称
	byname varchar(30) not null,--灾害别名
	maincrop varchar(20) not null,--主要危害农作物
	othercrop varchar(50),--其他危害农作物
	distributing varchar(80),--分布区域
	harm varchar(100),--危害
	characters varchar(2000),--特性
	disciplinarian varchar(2000),--规律
	countermeasure varchar(2000),--对策
	disasterpicture varchar(500),--病虫害图片
	remark varchar(800),--评论
	createtime varchar(19) default CONVERT(varchar, getdate(), 120), --创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120), --修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建农场信息表
create table T_FARM(
	id int identity primary key,--主键，唯一标识
	name varchar(50) not null,--农场名称
	area decimal(8,3) not null,--农场面积(亩)
	cropcode varchar(20),--农作物代码
	cropname varchar(30),--农作物名称
	address varchar(200),--农场地址
	x decimal(8,4),--X坐标
	y decimal(8,4),--Y坐标
	description varchar(500),--农场简介
	username varchar(30),--创建人
	createtime varchar(19) default CONVERT(varchar, getdate(), 120), --创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120), --修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
--创建农场作物信息表
create table T_FARM_CROP(
	id int identity primary key,--主键，唯一标识
	cropcode varchar(20) not null,--农作物代码
	cropname varchar(30) not null,--农作物名称
	area decimal(8,3),--种植面积(亩)
	yield decimal(8,3),--理论产量(斤)
	address varchar(200),--种植地址
	x decimal(8,4),--X坐标
	y decimal(8,4),--Y坐标
	sowdate date not null,--播种日期
	remark varchar(500),--备注信息
	username varchar(30) not null,--创建人
	createtime varchar(19) default CONVERT(varchar, getdate(), 120), --创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120), --修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
-创建预警等级信息表
create table T_ALARM_LEVEL(
	id int identity primary key,--主键，唯一标识
	name varchar(60) not null,--预警名称
	standard varchar(300) not null,--等级标准
	cropcode varchar(20),--农作物代码
	cropname varchar(30),--农作物名称
	address varchar(200),--农场地址
	x decimal(8,4),--X坐标
	y decimal(8,4),--Y坐标
	description varchar(500),--农场简介
	username varchar(30),--创建人
	createtime varchar(19) default CONVERT(varchar, getdate(), 120), --创建时间
	edittime varchar(19) default CONVERT(varchar, getdate(), 120), --修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建农产品价格信息表
create table T_PRODUCT_PRICE(
	id int identity primary key,--主键，唯一标识
	datatime varchar(20) not null,--采集时间
	dataurl varchar(300) not null,--采集页面地址
	pagedata text,--采集数据JSON字符串
	createtime varchar(19) default CONVERT(varchar, getdate(), 120), --采集时间
	state int default 1 --记录状态:0-已删除，1-正常
)


--创建用户使用App记录表
create table T_USER_LOGIN_LOG(
	id int identity primary key,--主键，唯一标识
	username varchar(30) not null,--用户账号
	clientip varchar(20),--客户端IP地址
	deviceId varchar(50),--设备ID
	brand varchar(50),--手机品牌
	model varchar(60),--手机型号
	os varchar(20),--系统名称
	version varchar(20),--系统版本
	createtime varchar(19) default CONVERT(varchar, getdate(), 120), --操作时间
	state int default 1 --记录状态:0-已删除，1-正常
)











