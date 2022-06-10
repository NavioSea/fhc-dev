package org.fh.controller.exam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.shiro.session.Session;
import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.exam.TestPaperMxService;
import org.fh.service.exam.TestPaperService;

/** 
 * 说明：试卷管理(明细)
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/testpapermx")
public class TestPaperMxController extends BaseController {
	
	@Autowired
	private TestPaperService testpaperService;
	
	@Autowired
	private TestPaperMxService testpapermxService;
	
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
		pd.put("TESTPAPERMX_ID", this.get32UUID());	//主键
		testpapermxService.save(pd);
		pd.put("TOTALSCORE", Double.parseDouble(testpapermxService.findZFraction(pd).get("zf").toString()));//获取某题型总分值
		testpaperService.editTot(pd);			//更新书卷总分
		map.put("result", errInfo);				//返回结果
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
		testpapermxService.delete(pd);
		Session session = Jurisdiction.getSession();
		pd.put("TESTPAPER_ID",session.getAttribute("TESTPAPER_ID").toString());
		PageData zpd = new PageData();
		zpd = testpapermxService.findZFraction(pd);
		if(null != zpd) {
			pd.put("TOTALSCORE", Double.parseDouble(zpd.get("zf").toString()));//获取某题型总分值
		}else {
			pd.put("TOTALSCORE", 0);
		}
		testpaperService.editTot(pd);			//更新书卷总分
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
		testpapermxService.edit(pd);
		Session session = Jurisdiction.getSession();
		pd.put("TESTPAPER_ID",session.getAttribute("TESTPAPER_ID").toString());
		pd.put("TOTALSCORE", Double.parseDouble(testpapermxService.findZFraction(pd).get("zf").toString()));//获取某题型总分值
		testpaperService.editTot(pd);			//更新书卷总分
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
		Session session = Jurisdiction.getSession();
		session.setAttribute("TESTPAPER_ID", pd.getString("TESTPAPER_ID")); //TESTPAPER_ID放入session
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData>	varList = testpapermxService.list(page);		//列出TestPaperMx列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);										//返回结果
		return map;
	}
	
	/**随机获取试题
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/automatic")
	@ResponseBody
	public Object automatic() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String TABN = pd.getString("TABN");
		pd.put("TABN",TABN+"FH");
		pd.put("QNUMBER", Integer.parseInt(pd.getString("QNUMBER")));
		List<PageData>	varList = testpapermxService.listAutomatic(pd);		//列出TestPaperMx列表
		StringBuffer fid = new StringBuffer();
		StringBuffer fan = new StringBuffer();
		for(int i=0;i<varList.size();i++) {
			if(i == (varList.size()-1)){
				fid.append(varList.get(i).getString("FID"));
			}else {
				fid.append(varList.get(i).getString("FID")).append(",");
			}
			if(TABN.equals("A") || TABN.equals("B") || TABN.equals("C")){	// ABC 是指 单选，多选，判断题， 其它题不需要把答案的数据带过来
				if(i == (varList.size()-1)){
					fan.append(varList.get(i).getString("ANSWER"));
				}else {
					fan.append(varList.get(i).getString("ANSWER")).append(",");
				}
			}else {
				fan.append("");
			}
		}
		map.put("fid", fid.toString());
		map.put("fan", fan.toString());									
		map.put("result", errInfo);											//返回结果
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
		pd = testpapermxService.findById(pd);	//根据ID读取
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
			testpapermxService.deleteAll(ArrayDATA_IDS);
			Session session = Jurisdiction.getSession();
			pd.put("TESTPAPER_ID",session.getAttribute("TESTPAPER_ID").toString());
			PageData zpd = new PageData();
			zpd = testpapermxService.findZFraction(pd);
			if(null != zpd) {
				pd.put("TOTALSCORE", Double.parseDouble(zpd.get("zf").toString()));//获取某题型总分值
			}else {
				pd.put("TOTALSCORE", 0);
			}
			testpaperService.editTot(pd);			//更新书卷总分
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}

}
