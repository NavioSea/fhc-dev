package org.fh.service.exam.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.mapper.dsno1.exam.SignUpMxMapper;
import org.fh.service.exam.SignUpMxService;

/** 
 * 说明： 报名管理(明细)接口实现类
 * 作者：FH Admin Q313596790
 * 官网：www.fhadmin.org
 * @version
 */
@Service
@Transactional //开启事物
public class SignUpMxServiceImpl implements SignUpMxService{

	@Autowired
	private SignUpMxMapper signupmxMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		signupmxMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		signupmxMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		signupmxMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return signupmxMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return signupmxMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return signupmxMapper.findById(pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		signupmxMapper.deleteAll(ArrayDATA_IDS);
	}
	
	/**查询明细总数
	 * @param pd
	 * @throws Exception
	 */
	public PageData findCount(PageData pd)throws Exception{
		return signupmxMapper.findCount(pd);
	}
	
	/**批量审核通过
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void updateYesAll(String[] ArrayDATA_IDS)throws Exception{
		signupmxMapper.updateYesAll(ArrayDATA_IDS);
	}
	
	/**批量审核不通过
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void updateNoAll(String[] ArrayDATA_IDS)throws Exception{
		signupmxMapper.updateNoAll(ArrayDATA_IDS);
	}
	
}

