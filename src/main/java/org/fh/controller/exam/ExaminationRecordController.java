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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.fh.controller.base.BaseController;
import org.fh.entity.Page;
import org.fh.util.DateUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.exam.AchievementService;
import org.fh.service.exam.BigQuestionFoService;
import org.fh.service.exam.BigQuestionTeService;
import org.fh.service.exam.CompletionService;
import org.fh.service.exam.CompoundQuestionService;
import org.fh.service.exam.ExaminationRecordService;
import org.fh.service.exam.JudgementQuestionService;
import org.fh.service.exam.LargeQuestionService;
import org.fh.service.exam.MultipleSelectionService;
import org.fh.service.exam.SingleElectionService;
import org.fh.service.exam.SmallQuestionFoService;
import org.fh.service.exam.SmallQuestionTeService;
import org.fh.service.exam.TestPaperMxService;
import org.fh.service.exam.TestPaperService;

/** 
 * 说明：考试记录
 * 作者：FH Admin QQ 313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/examinationrecord")
public class ExaminationRecordController extends BaseController {
	
	@Autowired
	private ExaminationRecordService examinationrecordService;
	@Autowired
	private TestPaperService testpaperService;
	@Autowired
	private TestPaperMxService testpapermxService;
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
	private SmallQuestionTeService smallquestionteService;
	@Autowired
	private BigQuestionTeService bigquestionteService;
	@Autowired
	private BigQuestionFoService bigquestionfoService;
	@Autowired
	private SmallQuestionFoService smallquestionfoService;
	@Autowired
	private AchievementService achievementService;
	
	
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
		String EXAMINATIONRECORD_ID = this.get32UUID();
		pd.put("EXAMINATIONRECORD_ID", EXAMINATIONRECORD_ID);	//主键
		pd.put("USERNAME", Jurisdiction.getUsername());			//用户名
		pd.put("CTIME", DateUtil.date2Str(new Date()));			//答题时间
		pd.put("ETIME", "");		//交卷时间
		pd.put("STATE", "edit");	//状态
		examinationrecordService.save(pd);
		this.addQuestionte(pd);
		map.put("EXAMINATIONRECORD_ID", EXAMINATIONRECORD_ID);
		map.put("result", errInfo);
		return map;
	}
	
	/**初始作答
	 * @param
	 * @throws Exception
	 */
	public void addQuestionte(PageData tpd) throws Exception{
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = testpaperService.findById(pd);	//根据ID读取
		List<PageData> varList = testpapermxService.listAll(pd);
		for(int i = 0; i < varList.size(); i++) {
			String ArrayDATA_IDS[] = varList.get(i).getString("SELECTEDTOPICS").split(",");
			List<PageData> tqList = new ArrayList<PageData>();
			String TYPE = varList.get(i).getString("TYPE");
			switch(TYPE){
			    case "A" :
			    	tqList = singleelectionService.getListByIDS(ArrayDATA_IDS);
			    	break;
			    case "B" :
			    	tqList = multipleselectionService.getListByIDS(ArrayDATA_IDS);
			       break;
			    case "C" :
			    	tqList = judgementquestionService.getListByIDS(ArrayDATA_IDS);
			       break;
			    case "D" :
			    	tqList = completionService.getListByIDS(ArrayDATA_IDS);
			       break;
			    case "E" :
			    	tqList = largequestionService.getListByIDS(ArrayDATA_IDS);
			       break;
			    case "F" :
			    	tqList = compoundquestionService.getListByIDS(ArrayDATA_IDS);
			       break;
			}
			for (int j = 0; j < tqList.size(); j++) {
				PageData qpd = new PageData();
				qpd.put("TESTPAPER_ID", tpd.getString("TESTPAPER_ID"));	//试卷ID
				qpd.put("EXAMINATIONRECORD_ID", tpd.getString("EXAMINATIONRECORD_ID"));	//记录ID
				String QUESTIONS_ID = "";
				switch(TYPE){
			    case "A" :
			    	QUESTIONS_ID = tqList.get(j).getString("SINGLEELECTION_ID");
			    	break;
			    case "B" :
			    	QUESTIONS_ID = tqList.get(j).getString("MULTIPLESELECTION_ID");
			       break;
			    case "C" :
			    	QUESTIONS_ID = tqList.get(j).getString("JUDGEMENTQUESTION_ID");
			       break;
			    case "D" :
			    	QUESTIONS_ID = tqList.get(j).getString("COMPLETION_ID");
			       break;
			    case "E" :
			    	QUESTIONS_ID = tqList.get(j).getString("LARGEQUESTION_ID");
			       break;
			    case "F" :
			    	QUESTIONS_ID = tqList.get(j).getString("COMPOUNDQUESTION_ID");
			       break;
				}
				qpd.put("QUESTIONS_ID", QUESTIONS_ID);			//试题ID
				qpd.put("USERNAME", tpd.getString("USERNAME"));	//用户名
				qpd.put("USERANSWER", "");	//考生答案
				qpd.put("RESULT", "");		//作答结果
				qpd.put("SCORE", varList.get(i).get("FRACTION").toString());		//得分
				qpd.put("TYPE", TYPE);		//试题类型
				if("A".equals(TYPE) || "B".equals(TYPE) || "C".equals(TYPE)) {
					qpd.put("SMALLQUESTIONTE_ID", this.get32UUID());				//主键
					qpd.put("CORRECTANSWER", tqList.get(j).getString("ANSWER"));	//正确答案
					smallquestionteService.save(qpd);
				}else {
					qpd.put("BIGQUESTIONTE_ID", this.get32UUID());		//主键
					qpd.put("CORRECTANSWER", "");						//正确答案
					bigquestionteService.save(qpd);
				}
			}
		}
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("examinationrecord:del")
	@ResponseBody
	public Object delete() throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		achievementService.delete(pd);			//删除成绩
		examinationrecordService.delete(pd);	//删除考试记录（这里设计的是，删除考试记录也会删除成绩，反过来也一样）
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	/**提交试卷
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
		String EXAMINATIONRECORD_ID = pd.getString("EXAMINATIONRECORD_ID");
		String DURATION = pd.getString("DURATION");
		Date STIME = (Date)Jurisdiction.getSession().getAttribute(EXAMINATIONRECORD_ID);
		Date ETIME = new Date();
		long di = DateUtil.getSecondPoor(STIME, ETIME);
		long dr = Long.parseLong(DURATION) * 60;
		String STATE = (dr-di)<0?"fail":"complete";
		pd.put("ETIME", DateUtil.date2Str(ETIME));			//交卷时间
		pd.put("STATE", STATE);								//状态  fail:超时未提交，complete:成功提交完成考试
		examinationrecordService.edit(pd);
		if("complete".equals(STATE)) {			//当complete时，清除 作答试题的缓存表，
			bigquestionfoService.save(pd);		//复制临时表数据到正式表中（大题）
			smallquestionfoService.save(pd);	//复制临时表数据到正式表中（小题）
			bigquestionteService.delete(pd);	//清除临时表数据，根据作答ID EXAMINATIONRECORD_ID（大题）
			smallquestionteService.delete(pd);	//清除临时表数据，根据作答ID EXAMINATIONRECORD_ID（小题）
			pd = testpaperService.findById(pd);	//根据ID读取试卷信息
			String EVALUATIONRESULTS = pd.getString("EVALUATIONRESULTS");
			/* 添加成绩单 start */
			pd.put("EXAMINATIONRECORD_ID", EXAMINATIONRECORD_ID);	//记录ID
			int bigCount = Integer.parseInt(bigquestionfoService.findCount(pd).get("zs").toString());		//大题总数	（填空，问答，复合）
			int smallCount = Integer.parseInt(smallquestionfoService.findCount(pd).get("zs").toString());	//小题总数	（单选，多选，判断）
			pd.put("QTOTAL", bigCount+smallCount);					//总题数
			pd.put("ACHIEVEMENT_ID", this.get32UUID());				//主键
			pd.put("USERNAME", Jurisdiction.getUsername());			//用户名
			pd.put("SCORE", 0);			//得分
			pd.put("PASSONOT", "未出");	//是否及格  yes：及格，no:不及格
			pd.put("CORRECT", 0);		//答对
			pd.put("ERROR", 0);			//答错
			pd.put("NOREV", 0);			//未评
			pd.put("STATE", "notout");	//状态
			pd.put("CTIME", DateUtil.date2Str(new Date()));	//时间
			if("A".equals(EVALUATIONRESULTS)) {				// A: 考完直接出成绩 B：人工阅卷后出成绩
				pd.put("FR", "true");
				int TRCount = Integer.parseInt(smallquestionfoService.findCount(pd).get("zs").toString());	//答对总数（考完直接出成绩的，只能是小题）
				pd.put("FR", "false");	
				int FRCount = Integer.parseInt(smallquestionfoService.findCount(pd).get("zs").toString());	//答错总数（考完直接出成绩的，只能是小题）
				Double SCORE;
				PageData spd = smallquestionfoService.findZFraction(pd);
				if(null == spd) {
					 SCORE = 0.0;
				}else {
					 SCORE = Double.parseDouble(spd.get("zf").toString());//得到总分（考完直接出成绩的，只计算小题即可）
				}
				Double PASSINGSCORE = Double.parseDouble(pd.get("PASSINGSCORE").toString());	//及格分数
				pd.put("PASSONOT", PASSINGSCORE>SCORE?"no":"yes");								//是否及格  yes：及格，no:不及格
				pd.put("SCORE", SCORE);			//得分
				pd.put("CORRECT", TRCount);		//答对
				pd.put("ERROR", FRCount);		//答错
				pd.put("NOREV", bigCount);		//未评 ，考完直接出成绩的，只能是大题总数，因为只有大题需要人工阅卷
				pd.put("STATE", "out");			//状态
			}
			achievementService.save(pd);
			/* 添加成绩单 end */
		}
		map.put("result", (dr-di)<0?"fail":errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("examinationrecord:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		pd.put("USERNAME", "admin".equals(Jurisdiction.getUsername())?"":Jurisdiction.getUsername()); //除admin用户外，只能查看自己的数据
		page.setPd(pd);
		List<PageData> varList = examinationrecordService.list(page);	//列出ExaminationRecord列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	
	/**查看作答记录
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
		String STATE = pd.getString("STATE");	//作答状态
		List<PageData> bList = new ArrayList<PageData>();
		List<PageData> sList = new ArrayList<PageData>();
		if("complete".equals(STATE)) {			//当试卷正常提交状态时，读取正式作答表里面的数据
			bList = bigquestionfoService.listAll(pd);
			sList = smallquestionfoService.listAll(pd);
		}else {									//否则读取临时作答表的数据
			bList = bigquestionteService.listAll(pd);
			sList = smallquestionteService.listAll(pd);
		}
		pd = testpaperService.findById(pd);	//根据ID读取试卷信息
		List<PageData> varList = testpapermxService.listAll(pd);//读取试卷题型数据
		for(int i = 0; i < varList.size(); i++) {	//循环题型
			String ArrayDATA_IDS[] = varList.get(i).getString("SELECTEDTOPICS").split(",");
			String TYPE = varList.get(i).getString("TYPE");
			List<PageData> tqList = new ArrayList<PageData>();
			String QUESTIONS_ID = "";
			switch(TYPE){	//匹配每个题型
			    case "A" :
			    	tqList = singleelectionService.getListByIDS(ArrayDATA_IDS);		//单选试题列表
			    	QUESTIONS_ID = "SINGLEELECTION_ID";
			    	break;
			    case "B" :
			    	tqList = multipleselectionService.getListByIDS(ArrayDATA_IDS);	//多选试题列表
			    	QUESTIONS_ID = "MULTIPLESELECTION_ID";
			       break;
			    case "C" :
			    	tqList = judgementquestionService.getListByIDS(ArrayDATA_IDS);	//判断题列表
			    	QUESTIONS_ID = "JUDGEMENTQUESTION_ID";
			       break;
			    case "D" :
			    	tqList = completionService.getListByIDS(ArrayDATA_IDS);			//填空题列表
			    	QUESTIONS_ID = "COMPLETION_ID";
			       break;
			    case "E" :
			    	tqList = largequestionService.getListByIDS(ArrayDATA_IDS);		//问答题列表
			    	QUESTIONS_ID = "LARGEQUESTION_ID";
			       break;
			    case "F" :
			    	tqList = compoundquestionService.getListByIDS(ArrayDATA_IDS);	//复合题列表
			    	QUESTIONS_ID = "COMPOUNDQUESTION_ID";
			       break;
			}
			for(int n = 0; n < tqList.size(); n++) {	//循环试题
				tqList.get(n).put("UA", "未作答");
				if("A".equals(TYPE) || "B".equals(TYPE) || "C".equals(TYPE)) {		//单选，多选，判断题
					for(int j = 0; j < sList.size(); j++) {
						if(sList.get(j).getString("QUESTIONS_ID").equals(tqList.get(n).getString(QUESTIONS_ID))) {	//匹配试题ID和作答里面的试题ID，找出作答时填写的答案
							tqList.get(n).put("UA", (null != sList.get(j).get("USERANSWER") && !"".equals(sList.get(j).getString("USERANSWER")))?sList.get(j).getString("USERANSWER"):"未作答");
							break;
						}
					}
				}else {	// 填空，问答，复合题
					for(int j = 0; j < bList.size(); j++) {
						if(bList.get(j).getString("QUESTIONS_ID").equals(tqList.get(n).getString(QUESTIONS_ID))) {
							tqList.get(n).put("UA", (null != bList.get(j).get("USERANSWER") && !"".equals(bList.get(j).getString("USERANSWER")))?bList.get(j).getString("USERANSWER"):"未作答");
							break;
						}
					}
				}
			}
			varList.get(i).put("tqList",tqList);
		}
		map.put("varList", varList);
		map.put("pd", pd);
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
	 /**获得参加考试的次数
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getCount")
	@ResponseBody
	public Object getCount() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("USERNAME", Jurisdiction.getUsername()); //当前用户名
		pd = examinationrecordService.getCount(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**去修改页面获取数据
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	@RequiresPermissions("examinationrecord:edit")
	@ResponseBody
	public Object goEdit() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = examinationrecordService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
		return map;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@RequiresPermissions("examinationrecord:del")
	@ResponseBody
	public Object deleteAll() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();		
		pd = this.getPageData();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(Tools.notEmpty(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			examinationrecordService.deleteAll(ArrayDATA_IDS);
			errInfo = "success";
		}else{
			errInfo = "error";
		}
		map.put("result", errInfo);				//返回结果
		return map;
	}
	
}
