package org.fh.controller.exam;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.fh.controller.base.BaseController;
import org.fh.entity.PageData;
import org.fh.service.exam.SmallQuestionTeService;

/** 
 * 说明：答题表(小题-临时 临时表用于实时存储正在答题提交的答案，正式表用于存储已经提交的作答的答案，分开存储的目的是提高作答时的效率)
 * 作者：FH Admin QQ 313596790
 * 官网：www.fhadmin.org
 */
@Controller
@RequestMapping("/smallquestionte")
public class SmallQuestionTeController extends BaseController {
	
	@Autowired
	private SmallQuestionTeService smallquestionteService;
	
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
		/* 这里用A,B 当参数，已经前台的字符串加密解密 compileStr uncompileStr 是为了防止考试的时候看到正确答案 */
		String A = pd.getString("A");	//考生提交的答案
		String B = pd.getString("B");	//正确答案
		String RESULT = A.equals(B.replaceAll(";B;", ""))?"true":"false";
		pd.put("EXAMINATIONRECORD_ID", pd.getString("EXID"));	//作答ID
		pd.put("QUESTIONS_ID", pd.getString("QID"));			//试题ID
		pd.put("USERANSWER", A);								//考生提交的答案
		pd.put("RESULT", RESULT);
		smallquestionteService.edit(pd);
		map.put("result", errInfo);
		return map;
	}
	
}
