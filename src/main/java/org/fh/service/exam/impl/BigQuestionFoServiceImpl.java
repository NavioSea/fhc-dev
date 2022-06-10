package org.fh.service.exam.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.exam.BigQuestionFoMapper;
import org.fh.service.exam.BigQuestionFoService;

/** 
 * 说明： 答题表(大题-正式)接口实现类
 * 作者：FH Admin Q313596790
 * 官网：www.fhadmin.org
 * @version
 */
@Service
@Transactional //开启事物
public class BigQuestionFoServiceImpl implements BigQuestionFoService{

	@Autowired
	private BigQuestionFoMapper bigquestionfoMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		bigquestionfoMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		bigquestionfoMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		bigquestionfoMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return bigquestionfoMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return bigquestionfoMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return bigquestionfoMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		bigquestionfoMapper.deleteAll(ArrayDATA_IDS);
	}
	
	/**统计
	 * @param pd
	 * @throws Exception
	 */
	public PageData statistics(PageData pd)throws Exception{
		return bigquestionfoMapper.statistics(pd);
	}

	/**查询总数
	 * @param pd
	 * @throws Exception
	 */
	public PageData findCount(PageData pd)throws Exception{
		return bigquestionfoMapper.findCount(pd);
	}
	
	/**查询明细总分
	 * @param pd
	 * @throws Exception
	 */
	public PageData findZFraction(PageData pd)throws Exception{
		return bigquestionfoMapper.findZFraction(pd);
	}
	
}

