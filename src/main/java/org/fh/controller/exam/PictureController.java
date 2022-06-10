package org.fh.controller.exam;

import net.sf.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
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
import org.fh.util.PathUtil;
import org.fh.util.Tools;
import org.fh.util.Const;
import org.fh.util.DateUtil;
import org.fh.util.DelFileUtil;
import org.fh.util.FileDownload;
import org.fh.util.FileUpload;
import org.fh.util.FileUtil;
import org.fh.util.Jurisdiction;
import org.fh.entity.PageData;
import org.fh.service.exam.PictureService;

/** 
 * 说明：图片管理
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/picture")
public class PictureController extends BaseController {
	
	@Autowired
	private PictureService pictureService;
	
	/**创建目录
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("picture:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("PICTURE_ID", this.get32UUID());			//主键
		pd.put("FILEPATH", "");							//路径
		pd.put("CTIME", DateUtil.date2Str(new Date()));	//创建时间
		pd.put("UNAME", Jurisdiction.getName());		//上传者
		pd.put("MASTER", Jurisdiction.getUsername());	//所属人
		pd.put("FILESIZE", "");	
		pictureService.save(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**上传文件
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/upload")
	@RequiresPermissions("picture:add")
	@ResponseBody
	public Object add(
			@RequestParam(value="FFILE",required=false) MultipartFile file,
			@RequestParam(value="NAME",required=false) String NAME,
			@RequestParam(value="PARENT_ID",required=false) String PARENT_ID,
			@RequestParam(value="REMARKS",required=false) String REMARKS
			) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		String  ffile = DateUtil.getDays(), fileName = "";
		if (null != file && !file.isEmpty()) {
			String filePath = PathUtil.getProjectpath() + Const.FILEPATHIMG + ffile;	//文件上传路径
			fileName = FileUpload.fileUp(file, filePath, this.get32UUID());				//执行上传
			pd.put("FILEPATH", Const.FILEPATHIMG + ffile + "/" + fileName);			//文件路径
			pd.put("NAME", NAME);							//文件名
			pd.put("PARENT_ID", PARENT_ID);					//目录ID
			pd.put("CTIME", DateUtil.date2Str(new Date()));	//创建时间
			pd.put("UNAME", Jurisdiction.getName());		//上传者,当前用户的姓名
			pd.put("MASTER", Jurisdiction.getUsername());	//用户名
			pd.put("FILESIZE", FileUtil.getFilesize(filePath + "/" + fileName));	//文件大小
			pd.put("REMARKS", REMARKS);						//备注
			pd.put("PICTURE_ID", this.get32UUID());			//主键
			pictureService.save(pd);						//存入数据库表
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**上传文件
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/uploadAll")
	@ResponseBody
	public Object uploadAll(
			@RequestParam(value="file",required=false) MultipartFile file,
			@RequestParam(value="FH_ID",required=false) String PICTURE_ID
			) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		String  ffile = DateUtil.getDays(), fileName = "";
		if (null != file && !file.isEmpty()) {
			String filePath = PathUtil.getProjectpath() + Const.FILEPATHIMG + ffile;	//文件上传路径
			fileName = FileUpload.fileUp(file, filePath, this.get32UUID());				//执行上传
			pd.put("FILEPATH", Const.FILEPATHIMG + ffile + "/" + fileName);				//文件路径
			pd.put("NAME", (file.getOriginalFilename()).split("\\.")[0]);				//文件名
			pd.put("PARENT_ID", PICTURE_ID);				//目录ID
			pd.put("CTIME", DateUtil.date2Str(new Date()));	//创建时间
			pd.put("UNAME", Jurisdiction.getName());		//上传者,当前用户的姓名
			pd.put("MASTER", Jurisdiction.getUsername());	//用户名
			pd.put("FILESIZE", FileUtil.getFilesize(filePath + "/" + fileName));	//文件大小
			pd.put("REMARKS", "无");							//备注
			pd.put("PICTURE_ID", this.get32UUID());			//主键
			pictureService.save(pd);						//存入数据库表
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
	@RequiresPermissions("picture:del")
	@ResponseBody
	public Object delete(@RequestParam String PICTURE_ID,@RequestParam String FILEPATH) throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd.put("PICTURE_ID", PICTURE_ID);
		if(pictureService.listByParentId(PICTURE_ID).size() > 0){		//判断是否有子级，是：不允许删除
			errInfo = "error";
		}else{
			if(Tools.notEmpty(FILEPATH)){
				DelFileUtil.delFolder(PathUtil.getProjectpath()+ FILEPATH.trim()); //删除文件
			}
			pictureService.delete(pd);
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("picture:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pictureService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");								//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		String PICTURE_ID = null == pd.get("PICTURE_ID")?"":pd.get("PICTURE_ID").toString();
		pd.put("PICTURE_ID", PICTURE_ID);										//当作上级ID
		page.setPd(pd);
		List<PageData>	varList = pictureService.list(page);					//列出picture列表
		if("".equals(PICTURE_ID) || "0".equals(PICTURE_ID)) {
			map.put("PARENT_ID", "0");											//上级ID
		}else {
			map.put("PARENT_ID", pictureService.findById(pd).getString("PARENT_ID"));	//上级ID
		}
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}

	/**
	 * 显示列表ztree
	 * @return
	 */
	@RequestMapping(value="/listTree")
	@ResponseBody
	public Object listTree()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		JSONArray arr = JSONArray.fromObject(pictureService.listTree("0"));
		String json = arr.toString();
		json = json.replaceAll("PICTURE_ID", "id").replaceAll("PARENT_ID", "pId").replaceAll("NAME", "name").replaceAll("subPicture", "nodes").replaceAll("hasPicture", "checked").replaceAll("treeurl", "url");
		map.put("zTreeNodes", json);
		map.put("result", errInfo);
		return map;
	}
	
	 /**批量操作
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/makeAll")
	@RequiresPermissions("picture:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		List<PageData> pathList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			pathList = pictureService.getAllById(ArrayDATA_IDS);
			for(int i=0;i<pathList.size();i++){
				if(Tools.notEmpty(pathList.get(i).getString("FILEPATH").trim())){
					DelFileUtil.delFolder(PathUtil.getProjectpath()+ pathList.get(i).getString("FILEPATH").trim()); //删除硬盘上的图片
				}
			}
			pictureService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
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
			pd = pictureService.findById(pd);
			String FILEPATH = pd.getString("FILEPATH");
			String fileName = pd.getString("NAME");
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
