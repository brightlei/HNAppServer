--sqlite数据库建表语句--
--创建用户信息表--
create table T_USER(
	userid integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	username varchar(20) not NULL,--登录用户名
	userpwd varchar(30) not null,--登录用户密码
	nickname varchar(50) not null,--用户昵称
	pwd_question varchar(60),--找回密码问题
	pwd_answer varchar(60),--找回密码问题答案
	userimg varchar(500),--用户头像地址
	user_roleid int default 0,--用户角色，默认：普通用户
	last_usetime varchar(20),--最后使用时间
	createtime TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	edittime TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--创建站点信息表
create table T_STATION(
	id varchar(20) primary key,--站点ID
	name varchar(20) not null,--站点名称
	ename varchar(50) not null,--站点拼音
	sno varchar(10) not null,--站点编号
	soil_no varchar(10) not null,--土壤湿度站点编号
	
)
--创建用户关注的站点信息表
create table T_USER_STATION(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	username varchar(20) not null,--用户名
	stationid varchar(20) not null,--关注站点ID
	
)

--创建字典分类信息表--
create table T_DIC(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	code varchar(20) not NULL,--字典分类编码
	name varchar(50) not NULL,--字典分类名称
	createtime TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	edittime TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);
--创建字典信息表--
create table T_DICDATA(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	diccode varchar(20) not NULL,--字典分类编码
	code varchar(20) not NULL,--字典编码
	name varchar(30) not null,--字典名称
	maxno int not null default 0,--字典分类最大编号，用于生成字典编码
	rank int not null default 0,--排序
	createtime TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	edittime TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);

--创建组织机构信息表--
create table T_DEPT(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，部门ID
	code varchar(20) not null,--组织机构编码
	name varchar(50) not null,--组织机构名称
	pid int not null,--父组织机构ID
	rank int not null default 0,--排序
	createtime TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	edittime TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);

--创建人员信息表--
create table T_PERSON(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，部门ID
	name varchar(50) not null,--人员姓名
	sex varchar(2) not null,--性别
	phone varchar(20) null,--手机号
	gwjb varchar(20) null,--岗位级别(数据字典)
	zzmm varchar(20) null,--政治面貌(数据字典)
	deptid int not null,--组织机构ID
	description varchar(300) null,--描述信息
	headimg varchar(500) null,--人员头像
	createtime TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	edittime TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);
insert into T_PERSON(name,sex,phone,deptid) values('管理员','男','',0);

--创建权限信息表--
create table T_ROLERIGHT(
	id integer primary key AUTOINCREMENT NOT NULL,--主键
	name varchar(50) not null,--菜单名称
	pid int not null,--父菜单ID
	pageurl varchar(600) null,--菜单页面
	createtime TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	edittime TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
insert into T_ROLERIGHT(name,pid,pageurl) values('防护员积分管理系统',0,'');

--创建用户角色信息表--
create table T_ROLE (
	id integer primary key AUTOINCREMENT NOT NULL,--主键
	name varchar(30) not null,--角色名称
	pageright varchar(1000),--菜单页面访问权限编码
	dataright varchar(1000),--数据访问权限编码
	description varchar(2000),--描述信息
	createtime TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	edittime TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);
insert into t_role(name) values('超级管理员');

--创建系统用户数据表--
create table T_USER(
	id integer primary key AUTOINCREMENT NOT NULL,--主键
	username varchar(30) not null,--用户名
	userpwd varchar(20) not null,--用户密码
	personid int not null,--人员编号
	roleid int not null,--角色ID
	createtime TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	edittime TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);
insert into t_user(username,userpwd,personid) values('admin','admin',0);

--系统访问日志信息表
create table T_VISIT_LOG(
	id integer primary key AUTOINCREMENT NOT NULL,--主键
	ip varchar(20) not null,--IP地址
	sessionid varchar(36) not null,--会话ID
	createtime TIMESTAMP default (datetime('now', 'localtime')), --创建时间
	leavetime TIMESTAMP default (datetime('now', 'localtime'))--离开时间
)

--创建用户操作日志数据表
create table T_USER_OPLOG(
	id integer primary key AUTOINCREMENT NOT NULL,--主键
	ip varchar(20) not null,--IP地址
	name varchar(200) not null,--业务模块名称
	username varchar(30) not null,--操作人
	optype varchar(50) not null,--操作类型
	opcontent varchar(4000),--操作内容
	createtime TIMESTAMP default (datetime('now', 'localtime')) --创建时间
)

--创建防护员表
create table T_FHY(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	xm varchar(10) not null,--姓名
	cj varchar(30) not null,--车间
	gq varchar(30) not null,--工区
	xb varchar(2) not null default 男,--性别
	age int null,--年龄
	sjh varchar(20) null,--手机号
	zzmm varchar(20) null,--政治面貌
	dqfhzg varchar(50) null,--当前防护资格
	cjpxsj varchar(20) null,--参加培训时间
	cjsj TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	xgsj TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
);

--创建违章违纪信息表
create table T_WZWJ(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	wzwjbh varchar(36) not null,--违章违纪编号
	wzwjrq varchar(20) not null,--违章违纪日期
	fxdw varchar(200) not null,--发现单位
	fxr varchar(100) not null,--发现人
	wzlb varchar(50) not null,--违章类别
	wzxz varchar(50) not null,--违章性质
	wznr varchar(2000) not null,--违章内容
	zrcj varchar(200) not null,--责任车间
	zrgq varchar(200) not null,--责任工区
	dnw varchar(50) not null,--点内外
	yjbt varchar(50) not null,--夜间白天
	zyzrr varchar(20) not null, --主要责任人
	opuser varchar(30) not null,--当前操作人
	cjsj TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	xgsj TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)

--违章违纪信息发现人关联表
create table T_WZWJ_FXR(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	wzwjbh varchar(36) not null,--违章违纪编号
	fxdwid int,
	fxrid int
)
--违章违纪防护员减分值
create table T_WZWJ_FHY(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	wzwjbh varchar(36) not null,--违章违纪编号
	wzwjrq varchar(20) not null,--违章违纪日期
	fhyxm varchar(20) not null, --防护员姓名
	score number --减分值
)

--全过程监控信息表
create table T_QGCJK(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	qgcjkbh varchar(30) not null,--全过程监控记录编号
	zyrq varchar(20) not null,--作业日期
	cj varchar(50) not null,--车间
	gq varchar(50) not null,--工区
	jhly varchar(50) not null,--计划来源：数据字典项
	hangb varchar(50) not null,--行别：数据字典项
	dnw varchar(50) not null,--点内外：数据字典项
	bgdd varchar(100) null,--报工地点
	zyxm varchar(200) null,--作业项目
	phtzs varchar(100) null,--配合通知书
	phry varchar(30) null,--配合人员
	sfyzjhxf varchar(30) null,--是否与周计划相符：数据字典项
	bzxx varchar(300) null,--备注信息
	dbr varchar(50) null,--带班人
	lwg varchar(50) null,--劳务工
	zgcqrs int default 0,--职工出勤人数
	zgcql number null,--职工出勤率
	shyjsl int default 0,--手机应交数量
	shsjsl int default 0,--手机实交数量
	sdrs int default 0,--上道人数
	xdrs int default 0,--下道人数
	sdl number,--上道率
	zz varchar(20),--驻站
	xc varchar(20),--现场
	yd varchar(20),--远端
	fxyd varchar(20),--反向远端
	jwmlh varchar(50),--进网命令号
	cwmlh varchar(50),--出网命令号
	wksbqk varchar(50),--网口锁闭情况：数据字典
	gwjjsyqk varchar(200),--高危机具使用情况
	zzdgsj varchar(20),--驻站到岗时间
	zzlgsj varchar(20),--驻站离岗时间
	sjrwsj varchar(20),--实际入网时间
	sjcwsj varchar(20),--实际出网时间
	tdtc varchar(100),--图定天窗
	tckssj varchar(20),--开窗开始时间
	tcsjsj varchar(20),--开窗结束时间
	sjzydd varchar(100),--实际作业地点
	wcgzl varchar(200),--完成工作量
	jcwjjsfyz varchar(20),--进出网机具是否一致
	zbrs int,--值班人数
	czwt varchar(500),--存在问题
	clyj varchar(500),--处理意见
	opuser varchar(30) not null,--当前操作人
	cjsj TIMESTAMP default (datetime('now', 'localtime')),--创建时间
	xgsj TIMESTAMP default (datetime('now', 'localtime')),--修改时间
	state int default 1 --记录状态:0-已删除，1-正常
)
--全过程防护员加分项
create table T_QGCJK_FHY(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	qgcjkid int not null,--全过程监控记录ID
	zyrq varchar(20) not null,--作业日期
	cj varchar(50) not null,--车间
	gq varchar(50) not null,--工区
	fhylb varchar(20),--驻站|现场|远端|反向远端
	fhyxm varchar(20) not null, --防护员姓名
	stime varchar(20) ,--开始时间
	etime varchar(20),--结束时间
	score number --减分值
)

create table T_FHY_SCORE(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	year varchar(4) not null,--年份
	month varchar(2) not null,--月份
	date varchar(2) not null,--日期
	fhyxm varchar(20) not null,--防护员姓名
	jiafen number,--加分
	jianfen number --减分
)

create table T_FHY_MONTH_MONEY(
	id integer primary key AUTOINCREMENT NOT NULL,--主键，唯一标识
	year varchar(4) not null,--年份
	month varchar(2) not null,--月份
	fhyxm varchar(20) not null,--防护员姓名
	dyjl number default 0,--当月奖励
	yyjfjl number default 0,--信用积分奖励
	cjsj TIMESTAMP default (datetime('now', 'localtime')) --创建时间
)


