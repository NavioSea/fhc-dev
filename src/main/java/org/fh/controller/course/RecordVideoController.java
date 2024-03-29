package org.fh.controller.course;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.DateUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.course.RecordVideoService;

/** 
 * 说明：视频课程浏览记录
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/recordvideo")
public class RecordVideoController extends BaseController {
	
	@Autowired
	private RecordVideoService recordvideoService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String RECORDVIDEO_ID = this.get32UUID();
		pd.put("RECORDVIDEO_ID", RECORDVIDEO_ID);		//主键
		pd.put("USERNAME", Jurisdiction.getUsername());	//用户名
		pd.put("UNAME", Jurisdiction.getName());		//姓名
		pd.put("CTIME", DateUtil.date2Str(new Date()));	//观看时间
		pd.put("DURATION", "0");	//时长
		pd.put("STATE", "");		//状态
		pd.put("REMARKS", "");		//备注
		pd.put("POSITION", "0");	//当前播放位置
		recordvideoService.save(pd);
		map.put("result", errInfo);
		map.put("RECORDVIDEO_ID", RECORDVIDEO_ID);
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("recordvideo:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		recordvideoService.delete(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		recordvideoService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("recordvideo:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData>	varList = recordvideoService.list(page);	//列出RecordVideo列表
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
	@RequiresPermissions("recordvideo:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = recordvideoService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("recordvideo:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			recordvideoService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	@RequiresPermissions("toExcel")
	public ModelAndView exportExcel() throws Exception{
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("用户名");	//1
		titles.add("姓名");	//2
		titles.add("视频ID");	//3
		titles.add("视频标题");	//4
		titles.add("观看时间");	//5
		titles.add("时长");	//6
		titles.add("状态");	//7
		titles.add("备注");	//8
		titles.add("当前播放位置");	//9
		dataMap.put("titles", titles);
		List<PageData> varOList = recordvideoService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("USERNAME"));	    //1
			vpd.put("var2", varOList.get(i).getString("UNAME"));	    //2
			vpd.put("var3", varOList.get(i).getString("VIDEO_ID"));	    //3
			vpd.put("var4", varOList.get(i).getString("VIDEO_TITE"));	    //4
			vpd.put("var5", varOList.get(i).getString("CTIME"));	    //5
			vpd.put("var6", varOList.get(i).getString("DURATION"));	    //6
			vpd.put("var7", varOList.get(i).getString("STATE"));	    //7
			vpd.put("var8", varOList.get(i).getString("REMARKS"));	    //8
			vpd.put("var9", varOList.get(i).getString("POSITION"));	    //9
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
