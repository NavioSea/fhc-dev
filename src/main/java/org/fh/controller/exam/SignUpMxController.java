package org.fh.controller.exam;

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
import org.fh.service.exam.SignUpMxService;

/** 
 * 说明：报名管理(明细)
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/signupmx")
public class SignUpMxController extends BaseController {
	
	@Autowired
	private SignUpMxService signupmxService;
	
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
		pd.put("SIGNUPMX_ID", this.get32UUID());		//主键
		pd.put("CTIME", DateUtil.date2Str(new Date()));	//报名时间
		pd.put("USERNAME", Jurisdiction.getUsername());	//用户名
		pd.put("TICKET", DateUtil.getSdfTimes()+Tools.getRandomNum4());	//准考证号
		signupmxService.save(pd);
		map.put("result", errInfo);						//返回结果
		return map;
	}
	
	/**检查是否报过名
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/hasSignupp")
	@ResponseBody
	public Object hasSignupp() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("USERNAME", Jurisdiction.getUsername());
		pd = signupmxService.findById(pd);	//根据ID读取
		if(null != pd) {
			errInfo = "error";
		}
		map.put("result", errInfo);			//返回结果
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		signupmxService.delete(pd);
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
		signupmxService.edit(pd);
		map.put("result", errInfo);				//返回结果
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
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		String msg = pd.getString("msg");	
		if(Tools.notEmpty(msg))pd.put("USERNAME", Jurisdiction.getUsername());	//用户名
		page.setPd(pd);
		List<PageData>	varList = signupmxService.list(page);	//列出SignUpMx列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);									//返回结果
		return map;
	}
	
	 /**去修改页面
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
		pd = signupmxService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);						//返回结果
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			signupmxService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	 /**批量审核
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/shAll")
	@ResponseBody
	public Object shAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		String STATE = pd.getString("STATE");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			if("yes".equals(STATE)) {
				signupmxService.updateYesAll(ArrayDATA_IDS);
			}else {
				signupmxService.updateNoAll(ArrayDATA_IDS);
			}
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**获取当前姓名
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getUname")
	@ResponseBody
	public Object getUname() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("NAME", Jurisdiction.getName());				//返回登录姓名
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
		titles.add("性别");	//3
		titles.add("身份证号");	//4
		titles.add("照片");	//5
		titles.add("其它");	//6
		titles.add("状态");	//7
		titles.add("考场");	//8
		titles.add("座号");	//9
		titles.add("报名时间");	//10
		titles.add("准考证号");	//11
		dataMap.put("titles", titles);
		List<PageData> varOList = signupmxService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("USERNAME"));	//1
			vpd.put("var2", varOList.get(i).getString("NAME"));	    //2
			vpd.put("var3", varOList.get(i).getString("SEX"));	    //3
			vpd.put("var4", varOList.get(i).getString("SFID"));	    //4
			vpd.put("var5", varOList.get(i).getString("PHOTO"));	//5
			vpd.put("var6", varOList.get(i).getString("OTHERS"));	//6
			vpd.put("var7", varOList.get(i).getString("STATE"));	//7
			vpd.put("var8", varOList.get(i).getString("ROOM"));	    //8
			vpd.put("var9", varOList.get(i).getString("SEAT"));	    //9
			vpd.put("var10", varOList.get(i).getString("CTIME"));	//10
			vpd.put("var11", varOList.get(i).getString("TICKET"));	//11
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}

}
