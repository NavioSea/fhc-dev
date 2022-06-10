package org.fh.controller.system;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.fh.controller.base.BaseController;
import org.fh.service.system.FhsmsService;
import org.fh.service.system.PhotoService;
import org.fh.service.system.RoleService;
import org.fh.service.system.UsersService;
import org.fh.util.AuthService;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.FaceMatch;
import org.fh.util.IniFileUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.util.mail.SimpleMailSender;
import org.fh.entity.PageData;
import org.fh.entity.system.Role;
import org.fh.entity.system.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 说明：系统设置、初始信息等
 * 作者：FH Admin Q 31 3596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/head")
public class HeadController extends BaseController {
	
	@Autowired
    private UsersService usersService;
	@Autowired
    private RoleService roleService;
	@Autowired
    private PhotoService photoService;
	@Autowired
	private FhsmsService fhsmsService;
	
	/**保存用户皮肤
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/saveSkin", produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object saveSkin() throws Exception{
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put(Const.SESSION_USERNAME, Jurisdiction.getUsername());//当前登录者用户名
		usersService.saveSkin(pd);
		Session session = Jurisdiction.getSession();
		session.setAttribute(Const.SKIN, pd.getString("SKIN"));
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("result", "success");
		return map;
	}
	
	/**获取基本信息
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="/getInfo", produces="application/json;charset=UTF-8")
	@ResponseBody
	public Object getList() throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		PageData pd = new PageData();
		String errInfo = "success";
		Session session = Jurisdiction.getSession();
		User user = (User)session.getAttribute(Const.SESSION_USERROL);
		if(null != user){
			pd.put(Const.SESSION_USERNAME, user.getUSERNAME());
			PageData pdPhoto;
			pdPhoto = photoService.findById(pd);
			map.put("userPhoto", null == pdPhoto?"assets/images/user/avatar-2.jpg":pdPhoto.getString("PHOTO2"));//用户头像
			map.put("NAME", user.getNAME());
			map.put("USERNAME", user.getUSERNAME());
			map.put("ROLE_NAME", user.getRole().getROLE_NAME());
			
			String infFilePath = PathUtil.getClasspath()+Const.SYSSET;								//配置文件路径
			if(null == session.getAttribute(Const.SHOWCOUNT)){
				session.setAttribute(Const.SHOWCOUNT, IniFileUtil.readCfgValue(infFilePath, "SysSet1", Const.SHOWCOUNT, "10"));//初始系统带分页的列表每页显示条数
			}
			
			String onlineIp = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "onlineIp", "127.0.0.1");		//在线管理IP
			String onlinePort = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "onlinePort", "8869");			//在线管理端口
			map.put("onlineAdress", onlineIp+":"+onlinePort);	//在线管理websocket地址
			
			String imIp = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "imIp", "127.0.0.1");				//即时聊天IP
			String imPort = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "imPort", "8869");					//即时聊天端口
			String IMSTATE = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "IMSTATE", "true");				//即时聊天状态
			if("true".equals(IMSTATE)){
				map.put("wimadress", imIp+":"+imPort);	//即时聊天websocket地址
			}else {
				map.put("wimadress", "");				//禁用时,地址为空
			}
			
			String sysName = IniFileUtil.readCfgValue(infFilePath, "SysSet1", Const.SYSNAME, "FH Admin");		//系统名称
			map.put(Const.SYSNAME, sysName);
			
			map.put("fhsmsCount", fhsmsService.findFhsmsCount(Jurisdiction.getUsername()).get("fhsmsCount").toString());//站内信未读总数
			map.put("fhsmsSound", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "fhsmsSound", "1"));					//信息提示音
			
			Object RNUMBERS = session.getAttribute(Const.SESSION_RNUMBERS);
			if(null == RNUMBERS){
				session.setAttribute(Const.SESSION_RNUMBERS, getRnumbers()); //把当前用户的角色编码放入session
			}
			errInfo = "success";
		}else {
			errInfo = "error";
		}
		map.put("result", errInfo);
		return map;
	}
	
	/**获取基本信息用作加入线管理(手机端用)
	 * @return
	 */
	@RequestMapping(value="/getDataToOnline")
	@ResponseBody
	public Object getDataToOnline()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		map.put("USERNAME", Jurisdiction.getUsername());
		String infFilePath = PathUtil.getClasspath()+Const.SYSSET;											//配置文件路径
		String onlineIp = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "onlineIp", "127.0.0.1");		//在线管理IP
		String onlinePort = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "onlinePort", "8869");			//在线管理端口
		map.put("fhsmsSound", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "fhsmsSound", "1"));			//信息提示音
		map.put("onlineAdress", onlineIp+":"+onlinePort);	//在线管理websocket地址
		map.put("result", errInfo);
		return map;
	}
	
	/**判断当前用户角色编码符合过滤条件(用于新任务消息通知)
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/isNowRole")
	@ResponseBody
	public Object isNowRole() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		if(Jurisdiction.getRnumbers().indexOf(pd.getString("RNUMBER"))!=-1){
			map.put("msg", "yes");
		}else{
			map.put("msg", "no");
		}
		return map;
	}
	
	/**根据按钮权限标识，判断是否有此按钮权限，用于前端页面是否显示按钮
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/hasButton")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object hasButton() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String keys[] = pd.getString("keys").split(",");
		Session session = Jurisdiction.getSession();
		Collection<String> shiroSet = (Collection<String>)session.getAttribute(Jurisdiction.getUsername() + Const.SHIROSET);
		for(String key : keys){  
			map.put(key.replace(":", "fhadmin"), shiroSet.contains(key));
		}
		map.put("result", errInfo);
		return map;
	}
	
	/**通过角色ID数组获取角色列表拼接角色编码
	 * @return
	 * @throws Exception
	 */
	public String getRnumbers() throws Exception{
		PageData userpd = new PageData();
		userpd.put(Const.SESSION_USERNAME, Jurisdiction.getUsername());
		userpd = usersService.findByUsername(userpd);		//通过用户名获取用户信息
		String ZROLE_ID = userpd.get("ROLE_ID").toString()+",fh,"+userpd.getString("ROLE_IDS");
		String arryROLE_ID[] = ZROLE_ID.split(",fh,");
		List<Role> rlist = roleService.getRoleByArryROLE_ID(arryROLE_ID);
		StringBuffer RNUMBERS = new StringBuffer();
		RNUMBERS.append("(");
		for(Role role:rlist){
			RNUMBERS.append("'"+role.getRNUMBER()+"',");
		}
		RNUMBERS.append("'fhadmin')");
		return RNUMBERS.toString();
	}
	
	/**人脸识别
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/faceRecognition")
	@ResponseBody
	public Object faceRecognition() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "error";
		PageData pd = new PageData();
		pd = this.getPageData();
		String imgData = pd.getString("PHOTODATA").replace("data:image/jpeg;base64,", "").replace("data:image/png;base64,", "");
		String times = pd.getString("times");
		String USERNAME = pd.getString("USERNAME");
		pd.put("USERNAME", Tools.notEmpty(USERNAME)?USERNAME:Jurisdiction.getUsername());
		PageData upd = new PageData();
		upd = photoService.findFace(pd);
		if("1".equals(times)) {				//第一次存，第二次根据两个base64进行头像识别
			pd.put("PHOTO", imgData);		//把base64编码存到PHOTO0字段里面
			pd.put("STATE", "yes");
			if(null == upd) {
				photoService.addFace(pd);
			}else {
				photoService.editFace(pd);
			}
			errInfo = "success";
		}else {
			PageData tpd = new PageData();
			tpd.put("USERNAME", "FaceAccessToken");	//用户名为 FaceAccessToken 的在用户头像表SYS_USERPHOTO里，PHOTO2 存的是更新Token的时间，PHOTO3存的是Token的值，此数据所有用户共享
			tpd = photoService.findById(tpd);
			String accessToken = tpd.getString("PHOTO3");
			if(DateUtil.getDaySub(tpd.getString("PHOTO2"),DateUtil.getDay()) > 29) { //access_token有过期时间为30天，过期后重新获取。
				accessToken = AuthService.getAuth();
				tpd.put("PHOTO2", DateUtil.getDay());
				tpd.put("PHOTO3", accessToken);
				photoService.edit(tpd);	
			}
			if(FaceMatch.getScore(upd.getString("PHOTO"),imgData,accessToken)) {
				 errInfo = "success";
				 upd.put("STATE", "yes");
				 photoService.editFaceState(upd);//更新识别状态
			}		
		}
		map.put("result", errInfo);
		return map;
	}
	
	/**更新识别状态
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/updateFacestate")
	@ResponseBody
	public Object updateFacestate() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd.put("USERNAME", Jurisdiction.getUsername());
		pd = photoService.hasFace(pd);
		if(null != pd) {
			pd.put("STATE", "no");
			photoService.editFaceState(pd);//更新识别状态
		}
		map.put("result", errInfo);
		return map;
	}
	
	/**获取识别状态
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getFacestate")
	@ResponseBody
	public Object getFacestate() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "error";
		PageData pd = new PageData();
		pd.put("USERNAME", Jurisdiction.getUsername());
		pd = photoService.findFace(pd);
		if(null != pd) {
			if("yes".equals(pd.getString("STATE"))) {
				errInfo = "success";
			}
		}
		map.put("result", errInfo);
		return map;
	}
	
	/**去系统设置页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/sysSet")
	@ResponseBody
	public Object sysSet() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		String infFilePath = PathUtil.getClasspath()+Const.SYSSET;	//配置文件路径
		
		pd.put("sysName", IniFileUtil.readCfgValue(infFilePath, "SysSet1", Const.SYSNAME, "FH Admin"));			//系统名称
		pd.put("showCount", IniFileUtil.readCfgValue(infFilePath, "SysSet1", Const.SHOWCOUNT, "10"));			//每页显示条数
		pd.put("onlineIp", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "onlineIp", "127.0.0.1"));			//在线管理IP
		pd.put("onlinePort", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "onlinePort", "8869"));			//在线管理端口
		pd.put("imIp", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "imIp", "127.0.0.1"));					//即时聊天IP
		pd.put("imPort", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "imPort", "8879"));					//即时聊天端口
		pd.put("IMSTATE", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "IMSTATE", "true"));					//即时聊天状态
		pd.put("bcIp", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "bcIp", "127.0.0.1"));					//视频弹幕IP
		pd.put("bcPort", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "bcPort", "8859"));					//视频弹幕端口
		pd.put("fhsmsSound", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "fhsmsSound", "m1"));				//信息提示音
		pd.put("SMTP", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "SMTP", "smtp.qq.com"));				//邮箱服务器SMTP
		pd.put("PORT", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "PORT", "465"));						//邮箱服务器端口
		pd.put("EMAIL", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "EMAIL", "emailtest@fhadmin.org"));	//邮箱服务器邮箱
		pd.put("PAW", IniFileUtil.readCfgValue(infFilePath, "SysSet1", "PAW", "Fh123456"));						//邮箱服务器密码
		
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**
	 * 保存系统设置
	 */
	@RequestMapping(value="/saveSysSet")
	@ResponseBody
	public Object saveSysSet() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		Session session = Jurisdiction.getSession();
		String infFilePath = PathUtil.getClasspath()+Const.SYSSET;	//配置文件路径
		PageData pd = new PageData();
		pd = this.getPageData();
		
		String sysName = pd.getString("sysName");						//系统名称
		String showCount = pd.getString("showCount");					//每页显示条数
		String onlineIp = pd.getString("onlineIp");						//在线管理IP
		String onlinePort = pd.getString("onlinePort");					//在线管理端口
		String imIp = pd.getString("imIp");								//即时聊天IP
		String imPort = pd.getString("imPort");							//即时聊天端口
		String IMSTATE = pd.getString("IMSTATE");						//即时聊天状态
		String bcIp = pd.getString("bcIp");								//视频弹幕IP
		String bcPort = pd.getString("bcPort");							//视频弹幕端口
		String fhsmsSound = pd.getString("fhsmsSound");					//信息提示音
		String SMTP = pd.getString("SMTP");								//邮箱服务器SMTP
		String PORT = pd.getString("PORT");								//邮箱服务器端口
		String EMAIL = pd.getString("EMAIL");							//邮箱服务器邮箱
		String PAW = pd.getString("PAW");								//邮箱服务器密码
		
		
		/*每页显示条数放入session,其它不需要放,因为这个参数调用频繁*/
		session.setAttribute(Const.SHOWCOUNT, showCount);
		
		/*写入配置文件*/
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", Const.SYSNAME, sysName);		
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", Const.SHOWCOUNT, showCount);	
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "onlineIp", onlineIp);
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "onlinePort", onlinePort);
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "imIp", imIp);
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "imPort", imPort);
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "IMSTATE", IMSTATE);
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "bcIp", bcIp);
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "bcPort", bcPort);
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "fhsmsSound", fhsmsSound);
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "SMTP", SMTP);				
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "PORT", PORT);						
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "EMAIL", EMAIL);	
		IniFileUtil.writeCfgValue(infFilePath, "SysSet1", "PAW", PAW);						
		
		map.put("result", errInfo);
		return map;
	}
	
	/**发送电子邮件
	 * @return
	 */
	@RequestMapping(value="/sendEmail")
	@ResponseBody
	@RequiresPermissions("email")
	public Object sendEmail(){
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success"; //发送状态
		int count = 0;				//统计发送成功条数
		int zcount = 0;				//理论条数
		String infFilePath = PathUtil.getClasspath()+Const.SYSSET;	//配置文件路径
		String SMTP;
		try {
			SMTP = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "SMTP", "smtp.qq.com");						//邮箱服务器SMTP
			String PORT = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "PORT", "465");						//邮箱服务器端口
			String EMAIL = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "EMAIL", "emailtest@fhadmin.org");	//邮箱服务器邮箱
			String PAW = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "PAW", "Fh123456");					//邮箱服务器密码
		
			String toEMAIL = pd.getString("EMAIL");					//对方邮箱
			String TITLE = pd.getString("TITLE");					//标题
			String CONTENT = pd.getString("CONTENT");				//内容
			String TYPE = pd.getString("TYPE");						//类型
	
			toEMAIL = toEMAIL.replaceAll("；", ";");
			toEMAIL = toEMAIL.replaceAll(" ", "");
			String[] arrTITLE = toEMAIL.split(";");
			zcount = arrTITLE.length;
			
			try {
				for(int i=0;i<arrTITLE.length;i++){
					if(Tools.checkEmail(arrTITLE[i])){		//邮箱格式不对就跳过
						SimpleMailSender.sendEmail(SMTP, PORT, EMAIL, PAW, toEMAIL, TITLE, CONTENT, TYPE);//调用发送邮件函数
						count++;
					}else{
						continue;
					}
				}
				errInfo = "success";
			} catch (Exception e) {
				errInfo = "error";
			} 
		} catch (IOException e1) {
		}
		map.put("result", errInfo);
		map.put("count", count);						//成功数
		map.put("ecount", zcount-count);				//失败数
		return map;
	}
	
}
