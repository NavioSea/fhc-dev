package org.fh.service.exam.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.exam.CompoundQuestionMapper;
import org.fh.service.exam.CompoundQuestionService;

/** 
 * 说明： 复合题接口实现类
 * 作者：FH Admin Q313596790
 * 官网：www.fhadmin.org
 * @version
 */
@Service
@Transactional //开启事物
public class CompoundQuestionServiceImpl implements CompoundQuestionService{

	@Autowired
	private CompoundQuestionMapper compoundquestionMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		compoundquestionMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		compoundquestionMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		compoundquestionMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return compoundquestionMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return compoundquestionMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return compoundquestionMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		compoundquestionMapper.deleteAll(ArrayDATA_IDS);
	}
	
	/**通过ID组获取列表
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> getListByIDS(String[] ArrayDATA_IDS)throws Exception{
		return compoundquestionMapper.getListByIDS(ArrayDATA_IDS);
	}
	
}

