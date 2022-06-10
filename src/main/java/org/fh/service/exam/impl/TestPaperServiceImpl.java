package org.fh.service.exam.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.exam.TestPaperMapper;
import org.fh.service.exam.TestPaperService;

/** 
 * 说明： 试卷管理接口实现类
 * 作者：FH Admin Q313596790
 * 官网：www.fhadmin.org
 * @version
 */
@Service
@Transactional //开启事物
public class TestPaperServiceImpl implements TestPaperService{

	@Autowired
	private TestPaperMapper testpaperMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		testpaperMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		testpaperMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		testpaperMapper.edit(pd);
	}
	
	/**修改试卷状态
	 * @param pd
	 * @throws Exception
	 */
	public void editState(PageData pd)throws Exception{
		testpaperMapper.editState(pd);
	}
	
	/**修改总分
	 * @param pd
	 * @throws Exception
	 */
	public void editTot(PageData pd)throws Exception{
		testpaperMapper.editTot(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return testpaperMapper.datalistPage(page);
	}
	
	/**列表(我可参加的考试)
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> listMy(Page page)throws Exception{
		return testpaperMapper.myDdatalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return testpaperMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return testpaperMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		testpaperMapper.deleteAll(ArrayDATA_IDS);
	}
	
}

