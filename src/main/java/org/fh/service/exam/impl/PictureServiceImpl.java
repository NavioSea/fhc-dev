package org.fh.service.exam.impl;

import java.util.ConcurrentModificationException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.fh.entity.Page;
import org.fh.entity.PageData;
import org.fh.entity.exam.Picture;
import org.fh.mapper.dsno1.exam.PictureMapper;
import org.fh.service.exam.PictureService;

/** 
 * 说明： 图片管理接口实现类
 * 作者：FH Admin Q313596790
 * 官网：www.fhadmin.org
 * @version
 */
@Service
@Transactional //开启事物
public class PictureServiceImpl implements PictureService{

	@Autowired
	private PictureMapper pictureMapper;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		pictureMapper.save(pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		pictureMapper.delete(pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		pictureMapper.edit(pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception{
		return pictureMapper.datalistPage(page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception{
		return pictureMapper.listAll(pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return pictureMapper.findById(pd);
	}

	/**
	 * 通过ID获取其子级列表
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List<Picture> listByParentId(String parentId) throws Exception {
		return pictureMapper.listByParentId(parentId);
	}
	
	/**
	 * 获取所有数据并填充每条数据的子级列表(递归处理)
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List<Picture> listTree(String parentId) throws Exception {
		List<Picture> valueList = this.listByParentId(parentId);
		try {
			for(Picture picture : valueList){
				picture.setTreeurl("0".equals(picture.getPARENT_ID())?("picture_list.html?PICTURE_ID="+picture.getPICTURE_ID()):"");
				picture.setSubPicture(this.listTree(picture.getPICTURE_ID()));
				picture.setTarget("treeFrame");
			}
			
		}catch (ConcurrentModificationException e) {
		}
		return valueList;
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		pictureMapper.deleteAll(ArrayDATA_IDS);
	}
	
	/**批量获取
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> getAllById(String[] ArrayDATA_IDS) throws Exception {
		return pictureMapper.getAllById(ArrayDATA_IDS);
	}
		
}

