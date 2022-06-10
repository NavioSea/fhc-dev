package org.fh.controller.course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import org.fh.util.FileDownload;
import org.fh.util.FileUpload;
import org.fh.util.Jurisdiction;
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.entity.system.Role;
import org.fh.service.course.PdfService;
import org.fh.service.system.RoleService;

/** 
 * 说明：PDF课程
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/pdf")
public class PdfController extends BaseController {
	
	@Autowired
	private PdfService pdfService;
	@Autowired
    private RoleService roleService;
	
	/**上传PDF
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/upload")
	@RequiresPermissions("pdf:add")
	@ResponseBody
	public Object add(
			@RequestParam(value="FFILE",required=false) MultipartFile file,
			@RequestParam(value="TITE",required=false) String TITE,
			@RequestParam(value="SUBJECT",required=false) String SUBJECT,
			@RequestParam(value="CLASSHOUR",required=false) String CLASSHOUR,
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
			pd.put("PDFPATH", Const.FILEPATHFILE + ffile + "/" + fileName);				//文件路径
			pd.put("TITE", TITE);				//标题
			pd.put("SUBJECT", SUBJECT);			//科目
			pd.put("CLASSHOUR", CLASSHOUR);		//课时
			pd.put("AUTHORIZED", AUTHORIZED);	//授权对象
			pd.put("PERSONNELID", PERSONNELID);	//人员ID
			pd.put("PERSONNEL", PERSONNEL);		//人员姓名
			pd.put("REMARKS", REMARKS);			//备注说明
			pd.put("PDF_ID", this.get32UUID());			//主键
			pdfService.save(pd);							//存入数据库表
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
	@RequiresPermissions("pdf:del")
	@ResponseBody
	public Object delete(@RequestParam String PDF_ID,@RequestParam String PDFPATH) throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("PDF_ID", PDF_ID);
		if(Tools.notEmpty(PDFPATH)){
			DelFileUtil.delFolder(PathUtil.getProjectpath()+ PDFPATH.trim()); //删除文件
		}
		pdfService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("pdf:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pdfService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("pdf:list")
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
		List<PageData>	varList = pdfService.list(page);	//列出Pdf列表
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
	@RequiresPermissions("pdf:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd);	//列出所有系统用户角色
		pd = pdfService.findById(pd);								//根据ID读取
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
	
	
	/**下载
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="/download")
	public void download(HttpServletResponse response)throws NofileException {
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			pd = pdfService.findById(pd);
			String FILEPATH = pd.getString("PDFPATH");
			String fileName = pd.getString("TITE");
			FileDownload.fileDownload(response, PathUtil.getProjectpath() + FILEPATH, fileName+FILEPATH.substring(58, FILEPATH.length()));
		} catch (Exception e) {
			throw new NofileException("=========要下载的文件已经没有了=========");
		}
	}
	
}


/*
 * 自定义异常类
 */
class NofileException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NofileException() {
	    super();
	  }

	  public NofileException(String message) {
	    super(message);
	  }

	  public NofileException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public NofileException(Throwable cause) {
	    super(cause);
	  }
	  
}
