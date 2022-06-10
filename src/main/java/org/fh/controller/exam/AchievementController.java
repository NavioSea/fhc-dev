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
import org.fh.util.DoubleUtil;
import org.fh.util.Jurisdiction;
import org.fh.util.ObjectExcelView;
import org.fh.util.Tools;
import org.fh.entity.PageData;
import org.fh.service.exam.AchievementService;
import org.fh.service.exam.BigQuestionFoService;
import org.fh.service.exam.CompletionService;
import org.fh.service.exam.CompoundQuestionService;
import org.fh.service.exam.ExaminationRecordService;
import org.fh.service.exam.JudgementQuestionService;
import org.fh.service.exam.LargeQuestionService;
import org.fh.service.exam.MultipleSelectionService;
import org.fh.service.exam.SingleElectionService;
import org.fh.service.exam.SmallQuestionFoService;
import org.fh.service.exam.TestPaperMxService;
import org.fh.service.exam.TestPaperService;

/** 
 * 说明：成绩管理
 * 作者：FH Admin QQ313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/achievement")
public class AchievementController extends BaseController {
	
	@Autowired
	private AchievementService achievementService;
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
	private BigQuestionFoService bigquestionfoService;
	@Autowired
	private SmallQuestionFoService smallquestionfoService;
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@RequiresPermissions("achievement:del")
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
	
	/**提交评阅
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
		pd.put("FR", "true");
		int TRCounts = Integer.parseInt(smallquestionfoService.findCount(pd).get("zs").toString());	//答对总数（小题）
		int TRCountb = Integer.parseInt(bigquestionfoService.findCount(pd).get("zs").toString());	//答对总数（大题）
		pd.put("FR", "false");	
		int FRCounts = Integer.parseInt(smallquestionfoService.findCount(pd).get("zs").toString());	//答错总数（小题）
		int FRCountb = Integer.parseInt(bigquestionfoService.findCount(pd).get("zs").toString());	//答错总数（大题）
		Double SSCORE;
		Double BSCORE;
		PageData spd = smallquestionfoService.findZFraction(pd);
		PageData bpd = bigquestionfoService.findZFraction(pd);
		if(null == spd) {
			SSCORE = 0.0;
		}else {
			SSCORE = Double.parseDouble(spd.get("zf").toString());//得到总分（小题）
		}
		if(null == bpd) {
			BSCORE = 0.0;
		}else {
			BSCORE = Double.parseDouble(bpd.get("zf").toString());//得到总分（小题）
		}
		Double SCORE = DoubleUtil.add(SSCORE, BSCORE);	//得到总分
		Double PASSINGSCORE = Double.parseDouble(pd.get("PASSINGSCORE").toString());	//及格分数
		pd.put("PASSONOT", PASSINGSCORE>SCORE?"no":"yes");								//是否及格  yes：及格，no:不及格
		pd.put("SCORE", SCORE);						//得分
		pd.put("CORRECT", TRCounts+TRCountb);		//答对
		pd.put("ERROR", FRCounts+FRCountb);			//答错
		pd.put("NOREV", 0);							//未评
		pd.put("STATE", "out");						//状态
		pd.put("CTIME", DateUtil.date2Str(new Date()));	//时间
		achievementService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	@RequiresPermissions("achievement:list")
	@ResponseBody
	public Object list(Page page) throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		String msg = pd.getString("msg");								// msg:admin 从评阅试卷菜单进入  msg:manage 从成绩管理里面进入 ， ""空值: 我的成绩菜单进入
		String KEYWORDS = pd.getString("KEYWORDS");						//关键词检索条件
		if(Tools.notEmpty(KEYWORDS))pd.put("KEYWORDS", KEYWORDS.trim());
		pd.put("USERNAME", "admin".equals(Jurisdiction.getUsername())||Tools.notEmpty(msg)?"":Jurisdiction.getUsername()); //除admin用户外或者msg非空时，只能查看自己的数据
		page.setPd(pd);
		List<PageData>	varList = achievementService.list(page);		//列出Achievement列表
		map.put("varList", varList);
		map.put("page", page);
		map.put("result", errInfo);
		return map;
	}
	
	/**成绩排名
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/ranking")
	@ResponseBody
	public Object ranking() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> rankingList = achievementService.getRanking(pd);		//列出成绩列表
		map.put("rankingList", rankingList);
		map.put("result", errInfo);
		return map;
	}
	
	/**统计
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/statistics")
	@ResponseBody
	public Object statistics() throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		String errInfo = "success";
		PageData pd = new PageData();
		pd = this.getPageData();
		int FNUM = Integer.parseInt(pd.getString("FNUM"));	//单选和多线 有多少选项
		String TYPE = pd.getString("TYPE");
		int right = 0,wrong = 0;
		if("A".equals(TYPE) || "B".equals(TYPE)) {			// A B 单选和多选
			List<String> tlist = new ArrayList<String>();
			List<Object> vlist = new ArrayList<Object>();
			String[] arLetter = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
			for (int i = 0; i < FNUM; i++) {
				tlist.add("选"+arLetter[i]);
				pd.put("FR", "false");
				pd.put("USERANSWER", arLetter[i]);
				vlist.add(smallquestionfoService.statistics(pd).get("zs"));
			}
			map.put("tlist", tlist);	//选项
			map.put("vlist", vlist);	//相应选项应答数量
			pd.put("FR", "true");
			pd.put("RESULT", "true");
			right = Integer.parseInt(smallquestionfoService.statistics(pd).get("zs").toString());
			pd.put("RESULT", "false");
			wrong = Integer.parseInt(smallquestionfoService.statistics(pd).get("zs").toString());
		}else if("C".equals(TYPE)) {	// C 判断题
			pd.put("FR", "true");
			pd.put("RESULT", "true");
			right = Integer.parseInt(smallquestionfoService.statistics(pd).get("zs").toString());
			pd.put("RESULT", "false");
			wrong = Integer.parseInt(smallquestionfoService.statistics(pd).get("zs").toString());
		}else{	//其它的 填空题 问答题 复合体
			pd.put("FRACTION", Integer.parseInt(pd.getString("FRACTION")));//满分时分值
			pd.put("FR", "false");//零分人数
			wrong = Integer.parseInt(bigquestionfoService.statistics(pd).get("zs").toString());
			pd.put("FR", "true"); //满分人数
			right = Integer.parseInt(bigquestionfoService.statistics(pd).get("zs").toString());
			pd.put("FR", "middle");//未满分
			map.put("middle", Integer.parseInt(bigquestionfoService.statistics(pd).get("zs").toString()));	//半对半错人数(就是既没有得满分，又没得零分)
		}
		map.put("right", right);	//答对人数
		map.put("wrong", wrong);	//答错人数
		map.put("result", errInfo);
		return map;
	}
	
	/**查看试卷
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
		List<PageData> bList = new ArrayList<PageData>();
		List<PageData> sList = new ArrayList<PageData>();
		bList = bigquestionfoService.listAll(pd);		//读取正式作答表里面的数据(大题)
		sList = smallquestionfoService.listAll(pd);		//读取正式作答表里面的数据(小题)
		pd = testpaperService.findById(pd);				//根据ID读取试卷信息
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
				tqList.get(n).put("UR", "false");		//答案是否正确
				if("A".equals(TYPE) || "B".equals(TYPE) || "C".equals(TYPE)) {		//单选，多选，判断题
					for(int j = 0; j < sList.size(); j++) {
						if(sList.get(j).getString("QUESTIONS_ID").equals(tqList.get(n).getString(QUESTIONS_ID))) {	//匹配试题ID和作答里面的试题ID，找出作答时填写的答案
							tqList.get(n).put("UA", (null != sList.get(j).get("USERANSWER") && !"".equals(sList.get(j).getString("USERANSWER")))?sList.get(j).getString("USERANSWER"):"未作答");
							tqList.get(n).put("UR", sList.get(j).getString("RESULT"));		//答案是否正确
							tqList.get(n).put("UF", sList.get(j).get("SCORE").toString());	//得分
							break;
						}
					}
				}else {	// 填空，问答，复合题
					for(int j = 0; j < bList.size(); j++) {
						if(bList.get(j).getString("QUESTIONS_ID").equals(tqList.get(n).getString(QUESTIONS_ID))) {
							tqList.get(n).put("UA", (null != bList.get(j).get("USERANSWER") && !"".equals(bList.get(j).getString("USERANSWER")))?bList.get(j).getString("USERANSWER"):"未作答");
							Double SCORE = Double.parseDouble(bList.get(j).get("SCORE").toString());			//得分
							Double FRACTION = Double.parseDouble(varList.get(i).get("FRACTION").toString());	//此题满分分值
							String RESULT = bList.get(j).getString("RESULT");
							if(null == bList.get(j).get("RESULT") || "".equals(RESULT)) {
								RESULT = "null";
							}else {
								RESULT = "false".equals(RESULT)?"false":(Double.doubleToLongBits(SCORE) == Double.doubleToLongBits(FRACTION)?"true":"nofalse");		//不满分就是半对半错
							}
							tqList.get(n).put("UF", SCORE);		//得分
							tqList.get(n).put("UR", RESULT);	//答案是否正确
							tqList.get(n).put("BIGID", bList.get(j).getString("BIGQUESTIONFO_ID"));	//exam_bigquestionfo 表的主键 BIGQUESTIONFO_ID 应用在评阅试卷时
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
		pd = achievementService.findById(pd);	//根据ID读取
		map.put("pd", pd);
		map.put("result", errInfo);
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
		titles.add("试卷ID");	//1
		titles.add("记录ID");	//2
		titles.add("用户名");	//3
		titles.add("得分");	//4
		titles.add("总分");	//5
		titles.add("及格");	//6
		titles.add("是否及格");	//7
		titles.add("答对");	//8
		titles.add("答错");	//9
		titles.add("未评");	//10
		titles.add("总题数");	//11
		titles.add("状态");	//12
		titles.add("时间");	//13
		dataMap.put("titles", titles);
		List<PageData> varOList = achievementService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("TESTPAPER_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("EXAMINATIONRECORD_ID"));	    //2
			vpd.put("var3", varOList.get(i).getString("USERNAME"));	    //3
			vpd.put("var4", varOList.get(i).get("SCORE").toString());	//4
			vpd.put("var5", varOList.get(i).get("TOTALSCORE").toString());	//5
			vpd.put("var6", varOList.get(i).get("PASSINGSCORE").toString());	//6
			vpd.put("var7", varOList.get(i).getString("PASSONOT"));	    //7
			vpd.put("var8", varOList.get(i).get("CORRECT").toString());	//8
			vpd.put("var9", varOList.get(i).get("ERROR").toString());	//9
			vpd.put("var10", varOList.get(i).get("NOREV").toString());	//10
			vpd.put("var11", varOList.get(i).get("QTOTAL").toString());	//11
			vpd.put("var12", varOList.get(i).getString("STATE"));	    //12
			vpd.put("var13", varOList.get(i).getString("CTIME"));	    //13
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
}
