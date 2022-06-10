package org.fh.controller.exam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.exam.SignUpService;
import org.fh.service.exam.SignUpMxService;

/** 
 * 说明：报名管理
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/signup")
public class SignUpController extends BaseController {
	
	@Autowired
	private SignUpService signupService;
	
	@Autowired
	private SignUpMxService signupmxService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("signup:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String SIGNUP_ID = this.get32UUID();
		pd.put("SIGNUP_ID", SIGNUP_ID);	//主键
		signupService.save(pd);
		String TESTPAPER_ID = pd.getString("TESTPAPER_ID");
		String[] TESTPAPER_IDS = TESTPAPER_ID.split(",");
		for (int i = 0; i < TESTPAPER_IDS.length; i++) {
			PageData tpd = new PageData();
			tpd.put("EXAM_SIGNUP_TEST_ID", this.get32UUID());	//主键
			tpd.put("SIGNUP_ID", SIGNUP_ID);
			tpd.put("TESTPAPER_ID", TESTPAPER_IDS[i]);
			signupService.addSg(tpd);		//添加报名的试卷
		}
		pd = signupService.findById(pd);	//根据ID读取
		map.put("result", errInfo);			//返回结果
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("signup:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		try{
			if(Integer.parseInt(signupmxService.findCount(pd).get("zs").toString()) > 0){
				errInfo = "error";
			}else{
				signupService.delete(pd);
				signupService.deleteTest(pd);
			}
		} catch(Exception e){
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	@RequiresPermissions("signup:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		signupService.edit(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("signup:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData> varList = signupService.list(page);	//列出SignUp列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	 /**去修改页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@ResponseBody
	public Object goEdit()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = signupService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);						//返回结果
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
		titles.add("试卷");	//1
		titles.add("考试主题");	//2
		titles.add("考点名称");	//3
		titles.add("考点地址");	//4
		titles.add("报考专业");	//5
		titles.add("考试说明");	//6
		titles.add("注意事项");	//7
		titles.add("考场规则");	//8
		titles.add("需要审核");	//9
		dataMap.put("titles", titles);
		List<PageData> varOList = signupService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("TESTPAPER_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("KSTITLE"));	    //2
			vpd.put("var3", varOList.get(i).getString("KDNAME"));	    //3
			vpd.put("var4", varOList.get(i).getString("ADDRESS"));	    //4
			vpd.put("var5", varOList.get(i).getString("MAJOR"));	    //5
			vpd.put("var6", varOList.get(i).getString("EXPLAIN"));	    //6
			vpd.put("var7", varOList.get(i).getString("MATTER"));	    //7
			vpd.put("var8", varOList.get(i).getString("RULE"));	    //8
			vpd.put("var9", varOList.get(i).getString("EXAMINE"));	    //9
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
