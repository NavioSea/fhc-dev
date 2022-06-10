package org.fh.controller.course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.DelFileUtil;
import org.fh.util.FileUpload;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.entity.system.Role;
import org.fh.service.course.VideoService;
import org.fh.service.system.RoleService;

/** 
 * 说明：视频课程
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/video")
public class VideoController extends BaseController {
	
	@Autowired
	private VideoService videoService;
	@Autowired
    private RoleService roleService;
	
	/**上传视频
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/upload")
	@RequiresPermissions("video:add")
	@ResponseBody
	public Object add(
			@RequestParam(value="FFILE",required=false) MultipartFile file,
			@RequestParam(value="TITE",required=false) String TITE,
			@RequestParam(value="FILEPATH",required=false) String FILEPATH,
			@RequestParam(value="SUBJECT",required=false) String SUBJECT,
			@RequestParam(value="CLASSHOUR",required=false) String CLASSHOUR,
			@RequestParam(value="DURATION",required=false) String DURATION,
			@RequestParam(value="AUTHORIZED",required=false) String AUTHORIZED,
			@RequestParam(value="PERSONNELID",required=false) String PERSONNELID,
			@RequestParam(value="PERSONNEL",required=false) String PERSONNEL,
			@RequestParam(value="REMARKS",required=false) String REMARKS
			) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		String  ffile = DateUtil.getDays(), fileName = "";
		if (null != file && !file.isEmpty()) {
			String filePath = PathUtil.getProjectpath() + Const.FILEPATHFILE + ffile;	//文件上传路径
			fileName = FileUpload.fileUp(file, filePath, this.get32UUID());				//执行上传
			pd.put("VIDEOPATH", Const.FILEPATHFILE + ffile + "/" + fileName);			//文件路径
			pd.put("TITE", TITE);				//标题
			pd.put("FILEPATH", FILEPATH);		//封面图
			pd.put("SUBJECT", SUBJECT);			//科目
			pd.put("CLASSHOUR", CLASSHOUR);		//课时
			pd.put("DURATION", DURATION);		//时长
			pd.put("AUTHORIZED", AUTHORIZED);	//授权对象
			pd.put("PERSONNELID", PERSONNELID);	//人员ID
			pd.put("PERSONNEL", PERSONNEL);		//人员姓名
			pd.put("REMARKS", REMARKS);			//备注说明
			pd.put("VIDEO_ID", this.get32UUID());			//主键
			videoService.save(pd);							//存入数据库表
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("video:del")
	@ResponseBody
	public Object delete(@RequestParam String VIDEO_ID,@RequestParam String VIDEOPATH) throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("VIDEO_ID", VIDEO_ID);
		if(Tools.notEmpty(VIDEOPATH)){
			DelFileUtil.delFolder(PathUtil.getProjectpath()+ VIDEOPATH.trim()); //删除文件
		}
		videoService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("video:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		videoService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("video:list")
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
		List<PageData>	varList = videoService.list(page);	//列出Video列表
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
		pd = videoService.findById(pd);								//根据ID读取
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
		map.put("pd", pd);map.put("roleList", roleList);
		map.put("result", errInfo);
		return map;
	}	
	
}
