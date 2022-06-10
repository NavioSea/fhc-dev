package org.fh.service.exam.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.exam.TestPaperMxMapper;
import org.fh.service.exam.TestPaperMxService;

/** 
 * 说明： 试卷管理(明细)接口实现类
 * 作者：FH Admin Q313596790
 * 官网：www.fhadmin.org
 * @version
 */
@Service
@Transactional //开启事物
public class TestPaperMxServiceImpl implements TestPaperMxService{

	@Autowired
	private TestPaperMxMapper testpapermxMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		testpapermxMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		testpapermxMapper.delete(pd);
	}
	
	/**通过试卷ID删除
	 * @param pd
	 * @throws Exception
	 */
	public void deleteByParId(PageData pd)throws Exception{
		testpapermxMapper.deleteByParId(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		testpapermxMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return testpapermxMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return testpapermxMapper.listAll(pd);
	}
	
	/**随机获取试题
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAutomatic(PageData pd)throws Exception{
		return testpapermxMapper.listAutomatic(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return testpapermxMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		testpapermxMapper.deleteAll(ArrayDATA_IDS);
	}
	
	/**查询明细总数
	 * @param pd
	 * @throws Exception
	 */
	public PageData findCount(PageData pd)throws Exception{
		return testpapermxMapper.findCount(pd);
	}
	
	/**查询明细总分
	 * @param pd
	 * @throws Exception
	 */
	public PageData findZFraction(PageData pd)throws Exception{
		return testpapermxMapper.findZFraction(pd);
	}
	
}

