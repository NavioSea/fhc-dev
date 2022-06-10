package org.fh.controller.exam;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.DateUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.entity.system.Role;
import org.fh.service.exam.TestPaperService;
import org.fh.service.system.RoleService;
import org.fh.service.exam.AchievementService;
import org.fh.service.exam.CompletionService;
import org.fh.service.exam.CompoundQuestionService;
import org.fh.service.exam.ExaminationRecordService;
import org.fh.service.exam.JudgementQuestionService;
import org.fh.service.exam.LargeQuestionService;
import org.fh.service.exam.MultipleSelectionService;
import org.fh.service.exam.SingleElectionService;
import org.fh.service.exam.TestPaperMxService;

/** 
 * 说明：试卷管理
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/testpaper")
public class TestPaperController extends BaseController {
	
	@Autowired
	private TestPaperService testpaperService;
	@Autowired
	private TestPaperMxService testpapermxService;
	@Autowired
    private RoleService roleService;
	@Autowired
	private SingleElectionService singleelectionService;
	@Autowired
	private MultipleSelectionService multipleselectionService;
	@Autowired
	private JudgementQuestionService judgementquestionService;
	@Autowired
	private CompletionService completionService;
	@Autowired
	private LargeQuestionService largequestionService;
	@Autowired
	private CompoundQuestionService compoundquestionService;
	@Autowired
	private ExaminationRecordService examinationrecordService;
	@Autowired
	private AchievementService achievementService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/add")
	@RequiresPermissions("testpaper:add")
	@ResponseBody
	public Object add() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String OTIME = pd.getString("OTIME");
		if(Tools.notEmpty(OTIME)) {
			pd.put("OTIME", OTIME.trim());
			pd.put("FOTIME", OTIME.replaceAll("-", "").trim());
		}
		pd.put("TESTPAPER_ID", this.get32UUID());		//主键
		pd.put("STATE", "edit");						//试卷状态(edit:编辑状态, release: 正在考试, end: 历史考试)
		pd.put("ETIME", DateUtil.date2Str(new Date()));	//编辑时间
		testpaperService.save(pd);
		map.put("TESTPAPER_ID", pd.getString("TESTPAPER_ID"));
		map.put("result", errInfo);						//返回结果
		return map;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("testpaper:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String STATE = pd.getString("STATE");
		try{
			if(Integer.parseInt(testpapermxService.findCount(pd).get("zs").toString()) > 0 && !"end".equals(STATE)){
				errInfo = "error";
			}else{
				testpaperService.delete(pd);			//删除试卷
				testpapermxService.deleteByParId(pd);	//删除试题
				achievementService.delete(pd);			//删除成绩
				examinationrecordService.delete(pd);	//删除考试记录
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
	@RequiresPermissions("testpaper:edit")
	@ResponseBody
	public Object edit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String OTIME = pd.getString("OTIME");
		if(Tools.notEmpty(OTIME)) {
			pd.put("OTIME", OTIME.trim());
			pd.put("FOTIME", OTIME.replaceAll("-", "").trim());
		}
		testpaperService.edit(pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**修改试卷状态
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/editState")
	@ResponseBody
	public Object editState() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ETIME", DateUtil.date2Str(new Date()));	//编辑时间
		testpaperService.editState(pd);
		map.put("result", errInfo);						//返回结果
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("testpaper:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		page.setPd(pd);
		List<PageData> varList = testpaperService.list(page);	//列出TestPaper列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**列表(我可参加的考试)
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/my")
	@RequiresPermissions("testpaper:my")
	@ResponseBody
	public Object myList(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ROLE_ID", Jurisdiction.getRoleid());			//当前登录用户的主职角色ID
		pd.put("USERNAME", Jurisdiction.getUsername());			//用户名
		page.setPd(pd);
		List<PageData> varList = testpaperService.listMy(page);	//列出TestPaper列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**列表（成绩管理里面，检索条件中，下拉列表处调用）
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/achList")
	@ResponseBody
	public Object achList() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd.put("TESTLIST", "true");
		List<PageData> varList = testpaperService.listAll(pd);	//列出TestPaper列表
		map.put("varList", varList);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**列表（报名管理里面，添加报名主题，下拉列表处调用）
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/nograntList")
	@ResponseBody
	public Object nograntList() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd.put("TESTLIST", "nogrant"); //未发放的试卷
		List<PageData> varList = testpaperService.listAll(pd);	//列出TestPaper列表
		map.put("varList", varList);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	 /**去修改页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@RequiresPermissions("testpaper:edit")
	@ResponseBody
	public Object goEdit()throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ROLE_ID", "1");
		List<Role> roleList = roleService.listAllRolesByPId(pd);	//列出所有系统用户角色
		pd = testpaperService.findById(pd);	//根据ID读取
		String EXAMINEE = pd.getString("EXAMINEE");					//角色ID
		if(Tools.notEmpty(EXAMINEE)){
			String arryROLE_ID[] = EXAMINEE.split(",");
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
		map.put("result", errInfo);						//返回结果
		return map;
	}
	
	/**试卷预览(读取试题)
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/view")
	@ResponseBody
	public Object view() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String EXAMINATIONRECORD_ID = pd.getString("EXAMINATIONRECORD_ID");
		pd = testpaperService.findById(pd);	//根据ID读取
		Boolean fhb = !"true".equals(pd.getString("FHORDER"));	//题目顺序，相同或者随机
		List<PageData> varList = testpapermxService.listAll(pd);
		for(int i = 0; i < varList.size(); i++) {
			String ArrayDATA_IDS[] = varList.get(i).getString("SELECTEDTOPICS").split(",");
			String TYPE = varList.get(i).getString("TYPE");
			switch(TYPE){
			    case "A" :
			    	List<PageData> tqLista = singleelectionService.getListByIDS(ArrayDATA_IDS);
			    	if(fhb)Collections.shuffle(tqLista);
			    	varList.get(i).put("tqList", tqLista);
			    	break;
			    case "B" :
			    	List<PageData> tqListb = multipleselectionService.getListByIDS(ArrayDATA_IDS);
			    	if(fhb)Collections.shuffle(tqListb);
			    	varList.get(i).put("tqList", tqListb);
			       break;
			    case "C" :
			    	List<PageData> tqListv = judgementquestionService.getListByIDS(ArrayDATA_IDS);
			    	if(fhb)	Collections.shuffle(tqListv);
			    	varList.get(i).put("tqList", tqListv);
			       break;
			    case "D" :
			    	List<PageData> tqListd = completionService.getListByIDS(ArrayDATA_IDS);
			    	if(fhb)Collections.shuffle(tqListd);
			    	varList.get(i).put("tqList", tqListd);
			       break;
			    case "E" :
			    	List<PageData> tqListe = largequestionService.getListByIDS(ArrayDATA_IDS);
			    	if(fhb)Collections.shuffle(tqListe);
			    	varList.get(i).put("tqList", tqListe);
			       break;
			    case "F" :
			    	List<PageData> tqListf = compoundquestionService.getListByIDS(ArrayDATA_IDS);
			    	if(fhb)Collections.shuffle(tqListf);
			    	varList.get(i).put("tqList", tqListf);
			       break;
			}
		}
		int dtime = 0;
		if(null != EXAMINATIONRECORD_ID) {//处理开始考试时，刷新考试页面，避免计算器重新计时
			Date NTIME = new Date();
			Session session = Jurisdiction.getSession();
			Object EX = session.getAttribute(EXAMINATIONRECORD_ID);
			if(null == EX){
				session.setAttribute(EXAMINATIONRECORD_ID, NTIME); //记录开始考试时间(开始考试时用)
			}else {
				Date STIME = (Date)Jurisdiction.getSession().getAttribute(EXAMINATIONRECORD_ID);
				long ns = 60;
				dtime = new Long(DateUtil.getSecondPoor(STIME, NTIME)/ns).intValue();	//已用时(分钟)
				dtime = dtime == 0 ? 1:dtime;
				
			}
		}
		map.put("varList", varList);
		map.put("pd", pd);
		map.put("DTIME", dtime);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**复制试卷
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/copy")
	@ResponseBody
	public Object copy() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = testpaperService.findById(pd);	//根据ID读取
		List<PageData> varList = testpapermxService.listAll(pd);
		String TESTPAPER_ID = this.get32UUID();
		for(int i = 0; i < varList.size(); i++) {
			varList.get(i).put("TESTPAPER_ID", TESTPAPER_ID);		//主表ID
			varList.get(i).put("TESTPAPERMX_ID", this.get32UUID());	//主键
			testpapermxService.save(varList.get(i));
		}
		/* 复制试卷主表 */
		pd.put("TESTPAPER_ID", TESTPAPER_ID);			//主键
		pd.put("STATE", "edit");						//试卷状态
		pd.put("ETIME", DateUtil.date2Str(new Date()));	//编辑时间
		testpaperService.save(pd);
		map.put("result", errInfo);						//返回结果
		return map;
	}
	
}
