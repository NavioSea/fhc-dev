package org.fh.controller.course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.Const;
import org.fh.util.IniFileUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.entity.system.Role;
import org.fh.service.course.LivebroadcastService;
import org.fh.service.system.PhotoService;
import org.fh.service.system.RoleService;

/** 
 * 说明：直播课程
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/livebroadcast")
public class LivebroadcastController extends BaseController {
	
	@Autowired
	private LivebroadcastService livebroadcastService;
	@Autowired
    private RoleService roleService;
	@Autowired
    private PhotoService photoService;
	
	/**获取视频弹幕服务信息
	 * @throws Exception
	 */
	@RequestMapping(value="/getMsg")
	@ResponseBody
	public Object getMsg() throws Exception{
		PageData pd = new PageData();
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		String infFilePath = PathUtil.getClasspath()+Const.SYSSET;									//配置文件路径
		String bcIp = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "bcIp", "127.0.0.1");		//视频弹幕IP
		String bcPort = IniFileUtil.readCfgValue(infFilePath, "SysSet1", "bcPort", "8859");			//视频弹幕端口
		map.put("bcadress", bcIp+":"+bcPort);	//视频弹幕websocket地址
		map.put(Const.SESSION_USERNAME, Jurisdiction.getUsername());
		map.put("NAME", Jurisdiction.getName());
		pd.put(Const.SESSION_USERNAME, Jurisdiction.getUsername());
		PageData pdPhoto;
		pdPhoto = photoService.findById(pd);
		map.put("userPhoto", null == pdPhoto?"assets/images/user/avatar-2.jpg":pdPhoto.getString("PHOTO2"));//用户头像
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("livebroadcast:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("LIVEBROADCAST_ID", this.get32UUID());	//主键
		livebroadcastService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("livebroadcast:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		livebroadcastService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("livebroadcast:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		livebroadcastService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("livebroadcast:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		pd.put("ROLE_ID", Jurisdiction.getRoleid());					//当前登录用户的主职角色ID
		pd.put("USERNAME", Jurisdiction.getUsername());					//用户名
		page.setPd(pd);
		List<PageData>	varList = livebroadcastService.list(page);		//列出Livebroadcast列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	
	 /**去修改页面获取数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd);	//列出所有系统用户角色
		pd = livebroadcastService.findById(pd);						//根据ID读取
		String AUTHORIZED = pd.getString("AUTHORIZED");				//角色ID
		if(Tools.notEmpty(AUTHORIZED)){
			String arryROLE_ID[] = AUTHORIZED.split(",");
			for(int i=0;i<roleList.size();i++){
				Role role = roleList.get(i);
				String roleId = role.getROLE_ID();
				for(int n=0;n<arryROLE_ID.length;n++){
					if(arryROLE_ID[n].equals(roleId)){
						role.setRIGHTS("1");	//此时的目的是为了修改信息上，角色能看到哪些被选中
						break;
					}
				}
			}
		}
		map.put("pd", pd);
		map.put("roleList", roleList);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("livebroadcast:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			livebroadcastService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
}
