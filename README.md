# flying2
flying框架2.0版本

<pre>
功能特点：
1、轻量级，相对于SpringCloud而言，引用了少数的几个jar，调试过程5秒钟完成启动
2、模块化，一个模块一个领域根对象，满足DDD
3、基于接口，六边形架构的最好支持者
4、组件丰富
5、可以引入Spring，使用Spring生态构建
6、分布式，可以引用远程模块，远程模块本地化访问，让分布式更简单

--插件式模块--
		&lt;module id="cms" version="1" locate="remote" sort="2"&gt;
			http://192.168.1.1:8080/remoting
		&lt;/module>
		&lt;module id="pas" version="1" locate="local" sort="3"&gt;
			C:\flying\pas\WebRoot
		&lt;/module&gt;

--组件化开发--
	@MethodInfo("创建新用户")
	@DaoCreate(entity="security.user")
	public User create(
			@Param(value="username", required=true,maxlength=30, desc="登录用户名")String username,
			@Param(value="password",required=true, desc="密码")String password,
			@Param(value="org_name",required=true, desc="组织机构")String org_name,
			@Param(value="mail",required=false, desc="邮箱")String mail,
			@Param(value="real_name",required=true, maxlength=30, desc="用户实名")String real_name) throws Exception {
		return ModelFactory.createModelInstance(User.class, "password", MD5.encode(password));
	}

</pre>
