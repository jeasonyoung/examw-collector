package com.examw.collector.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.examw.collector.dao.ICatalogDao;
import com.examw.collector.dao.ICatalogEntityDao;
import com.examw.collector.dao.IOperateLogDao;
import com.examw.collector.dao.IPackDao;
import com.examw.collector.dao.IPackageEntityDao;
import com.examw.collector.dao.ISubjectDao;
import com.examw.collector.dao.ISubjectEntityDao;
import com.examw.collector.domain.Catalog;
import com.examw.collector.domain.OperateLog;
import com.examw.collector.domain.Pack;
import com.examw.collector.domain.Subject;
import com.examw.collector.domain.local.CatalogEntity;
import com.examw.collector.domain.local.PackageEntity;
import com.examw.collector.domain.local.SubjectEntity;
import com.examw.collector.model.PackInfo;
import com.examw.collector.service.IDataServer;
import com.examw.collector.service.IPackageUpdateService;
import com.examw.collector.support.JSONUtil;
import com.examw.model.DataGrid;

/**
 * 套餐数据更新服务接口实现类
 * 
 * @author fengwei.
 * @since 2014年7月9日 下午5:12:19.
 */
public class PackageUpdateServiceImpl implements IPackageUpdateService {
	private IPackDao packDao;
	private ISubjectDao subjectDao;
	private ICatalogDao catalogDao;
	private IPackageEntityDao packageEntityDao;
	private ISubjectEntityDao subjectEntityDao;
	private ICatalogEntityDao catalogEntityDao;
	private IOperateLogDao operateLogDao;
	private IDataServer dataServer;
	
	/**
	 * 设置 远程数据接口
	 * @param dataServer
	 * 
	 */
	public void setDataServer(IDataServer dataServer) {
		this.dataServer = dataServer;
	}

	/**
	 * 设置操作日志数据接口
	 * 
	 * @param operateLogDao
	 * 
	 */
	public void setOperateLogDao(IOperateLogDao operateLogDao) {
		this.operateLogDao = operateLogDao;
	}

	/**
	 * 设置 远程套餐数据接口
	 * 
	 * @param packDao
	 * 
	 */
	public void setPackDao(IPackDao packDao) {
		this.packDao = packDao;
	}

	/**
	 * 设置 远程科目数据接口
	 * 
	 * @param subjectDao
	 * 
	 */
	public void setSubjectDao(ISubjectDao subjectDao) {
		this.subjectDao = subjectDao;
	}

	/**
	 * 设置 远程分类数据接口
	 * 
	 * @param catalogDao
	 * 
	 */
	public void setCatalogDao(ICatalogDao catalogDao) {
		this.catalogDao = catalogDao;
	}

	/**
	 * 设置 本地套餐数据接口
	 * 
	 * @param packageEntityDao
	 * 
	 */
	public void setPackageEntityDao(IPackageEntityDao packageEntityDao) {
		this.packageEntityDao = packageEntityDao;
	}

	/**
	 * 设置 本地科目数据接口
	 * 
	 * @param subjectEntityDao
	 * 
	 */
	public void setSubjectEntityDao(ISubjectEntityDao subjectEntityDao) {
		this.subjectEntityDao = subjectEntityDao;
	}

	/**
	 * 设置 本地分类数据接口
	 * 
	 * @param catalogEntityDao
	 * 
	 */
	public void setCatalogEntityDao(ICatalogEntityDao catalogEntityDao) {
		this.catalogEntityDao = catalogEntityDao;
	}

	@Override
	public List<PackInfo> update(List<PackInfo> packs,String account) {
		if (packs == null || packs.size() == 0)
			return new ArrayList<PackInfo>();
		List<PackInfo> list = new ArrayList<PackInfo>();
		for (PackInfo info : packs) {
			if (StringUtils.isEmpty(info.getStatus())
					|| info.getStatus().equals("旧的")) {
				continue;
			}
			if (info.getStatus().equals("被删")) {
				Pack data1= this.packDao.load(Pack.class, info.getCode());
				PackageEntity data2 = this.packageEntityDao.load(
						PackageEntity.class, info.getCode());
				if (data2!=null && data1 != null) {
					this.packDao.delete(data1);
				}
				if (data2 != null) {
					this.packageEntityDao.delete(data2);
					list.add(info);
				}
				continue;
			}
			Pack pack = this.changeRemoteModel(info);
			PackageEntity p = changeLocalModel(info);
			if(pack!=null && p!=null)
				this.packDao.saveOrUpdate(pack);
			if(p != null)
			{
				this.packageEntityDao.saveOrUpdate(p);
				list.add(info);
			}
		}
		// 添加操作日志
		OperateLog log = new OperateLog();
		log.setId(UUID.randomUUID().toString());
		log.setType(OperateLog.TYPE_UPDATE_PACKAGE);
		log.setName("更新套餐数据");
		log.setAddTime(new Date());
		log.setAccount(account);
		log.setContent(JSONUtil.ObjectToJson(list));
		this.operateLogDao.save(log);
		return list;
	}
	/**
	 * 本地套餐数据模型转换
	 * @param info
	 * @return
	 */
	private Pack changeRemoteModel(PackInfo info) {
		if (info == null)
			return null;
		Pack data = new Pack();
		BeanUtils.copyProperties(info, data);
		if (StringUtils.isEmpty(info.getCatalogId())) {
			return null;
		} else {
			if (StringUtils.isEmpty(info.getSubjectId())) {
				Catalog catalog = this.catalogDao.load(Catalog.class,
						info.getCatalogId());
				if (catalog == null)
					return null;
				data.setCatalog(catalog);
			} else {
				Subject subject = this.subjectDao.load(Subject.class,
						info.getSubjectId());
				if (subject == null)
					return null;
				data.setSubject(subject);
				data.setCatalog(subject.getCatalog());
			}
		}
		return data;
	}

//	private void updateLocal(List<PackInfo> packs) {
//		if (packs == null || packs.size() == 0)
//			return;
//		List<PackInfo> list = new ArrayList<PackInfo>();
//		for (PackInfo info : packs) {
//			if (StringUtils.isEmpty(info.getStatus())
//					|| info.getStatus().equals("旧的")) {
//				continue;
//			}
//			if (info.getStatus().equals("被删")) {
//				PackageEntity data = this.packageEntityDao.load(
//						PackageEntity.class, info.getCode());
//				if (data != null) {
//					this.packageEntityDao.delete(data);
//					list.add(info);
//				}
//				continue;
//			}
//			PackageEntity p = changeLocalModel(info);
//			if(p != null)
//			{
//				//TODO	套餐价格不同的情况,要酌情更新
//				this.packageEntityDao.saveOrUpdate(p);
//				list.add(info);
//			}
//		}
//		// 添加操作日志
//		OperateLog log = new OperateLog();
//		log.setId(UUID.randomUUID().toString());
//		log.setType(OperateLog.TYPE_UPDATE_PACKAGE);
//		log.setName("更新套餐数据(实际数据)");
//		log.setAddTime(new Date());
//		log.setContent(JSONUtil.ObjectToJson(list));
//		this.operateLogDao.save(log);
//	}
	
	/**
	 * 实际套餐数据模型转换
	 * @param info
	 * @return
	 */
	private PackageEntity changeLocalModel(PackInfo info) {
		if (info == null)
			return null;
		PackageEntity data = this.packageEntityDao.load(PackageEntity.class, info.getCode());
		if(data==null)
			data = new PackageEntity();
		//	套餐价格不同的情况,要酌情更新,售价高于环球的售价,改
		else if(data.getDiscount() <= info.getDiscount())
		{
			info.setDiscount(data.getDiscount());	//售价比环球售价要低[这里没有进行价格修改]
		}
		BeanUtils.copyProperties(info, data);
		data.setId(info.getCode());
		if (StringUtils.isEmpty(info.getCatalogId())) {
			return null;
		} else {
			if (StringUtils.isEmpty(info.getSubjectId())) {
				CatalogEntity catalog = this.catalogEntityDao.find(info
						.getCatalogId());
				if (catalog == null)
					return null;
				data.setCatalogEntity(catalog);
			} else {
				SubjectEntity subject = this.subjectEntityDao.load(
						SubjectEntity.class, info.getSubjectId());
				if (subject == null)
					return null;
				data.setSubjectEntity(subject);
				data.setCatalogEntity(subject.getCatalogEntity());
			}
		}
		return data;
	}
	
	//=========================================================================
	@Override
	public DataGrid<PackInfo> dataGridUpdate(String account) {
		List<PackInfo> list = this.update(account);
		DataGrid<PackInfo> grid = new DataGrid<PackInfo>();
		grid.setRows(list);
		grid.setTotal((long) list.size());
		return grid;
	}
	private List<PackInfo> update(String account) {
		//找出需要查找并且有变化的科目集合
		List<Pack> packs = this.findChangedPacks();
		for (Pack info : packs) {
			if (StringUtils.isEmpty(info.getStatus())
					|| info.getStatus().equals("旧的")) {
				continue;
			}
			if (info.getStatus().equals("被删")) {
				Pack data1= this.packDao.load(Pack.class, info.getCode());
				PackageEntity data2 = this.packageEntityDao.load(
						PackageEntity.class, info.getCode());
				if (data2!=null && data1 != null) {
					this.packDao.delete(data1);
				}
				if (data2 != null) {
					this.packageEntityDao.delete(data2);
				}else{
					info.setUpdateInfo("<span style='color:purple'>删除失败</span>"+info.getUpdateInfo());
				}
				continue;
			}
			Pack pack = this.judgeDataSafe(info);
			PackageEntity p = changeModel(info);
			if(pack!=null && p!=null)
				this.packDao.saveOrUpdate(pack);
			if(p != null)
			{
				this.packageEntityDao.saveOrUpdate(p);
			}else{
				info.setUpdateInfo("<span style='color:purple'>插入或更新失败</span>"+info.getUpdateInfo());
			}
		}
		List<PackInfo> result = this.changeModel(packs);
		// 添加操作日志
		OperateLog log = new OperateLog();
		log.setId(UUID.randomUUID().toString());
		log.setType(OperateLog.TYPE_UPDATE_PACKAGE);
		log.setName("更新套餐数据");
		log.setAddTime(new Date());
		log.setAccount(account);
		log.setContent(JSONUtil.ObjectToJson(result));
		this.operateLogDao.save(log);
		return result;
	}
	/**
	 * 找出有变化的集合
	 * @return
	 */
	private List<Pack> findChangedPacks(){
		//需要进行更新比对的分类
		List<CatalogEntity> needList = this.catalogEntityDao.findAllWithCode();
		List<Pack> packList = new ArrayList<Pack>();
		PackInfo info = new PackInfo();
		for(CatalogEntity entity : needList){
			info.setCatalogId(entity.getCode());
			packList.addAll(this.findChangedPack(info));
		}
		return packList;
	}
	private List<Pack> findChangedPack(PackInfo info){
		Catalog catalog = this.catalogDao.load(Catalog.class, info.getCatalogId());
		List<Pack> data = this.dataServer.loadPacks(info.getCatalogId(), null);
		List<Pack> add = new ArrayList<Pack>();
		StringBuffer existIds = new StringBuffer();
		for(Pack p:data){
			p.setCatalog(catalog);
			if(!StringUtils.isEmpty(p.getCode())) existIds.append("'"+p.getCode()+"'").append(",");
			Pack local_p = this.packDao.load(Pack.class, p.getCode());
			if(local_p == null){
				p.setStatus("新增");
				p.setUpdateInfo("<span style='color:blue'>[新增]</span>"+p.toString());
				add.add(p);
			}else if(p.equals(local_p)){
				continue;
			}else{
				//TODO 套餐价格不同的情况 要进行提醒
				p.setStatus("新的");
				//价格变化,特别提醒
				if(!p.getDiscount().equals(local_p.getDiscount()))
				{
					//查询实际数据比较价格
					PackageEntity real_p = this.packageEntityDao.load(PackageEntity.class, p.getCode());
					if(real_p != null){
						p.setUpdateInfo("<span style='color:red'>！价格变化,我们的售价为:"+real_p.getDiscount()+";新售价:"+p.getDiscount()+"</span>"+p.getUpdateInfo());
					}
				}
				//local_p.setStatus("旧的");
				p.setUpdateInfo("<span style='color:red'>[更新]</span>"+p.getUpdateInfo());
				BeanUtils.copyProperties(p, local_p);	//已经存在的,必须用原有的数据进行更新,不然会出错
				add.add(local_p);
				//add.add(local_p);
			}
		}
		if(existIds.length()>0)
		{
			existIds.append("'0'");
			List<Pack> deleteList = this.packDao.findDeletePacks(existIds.toString(),info);
			if(deleteList!=null && deleteList.size()>0)
			{
				for(Pack s:deleteList){
					s.setStatus("被删");
					s.setUpdateInfo(s.toString());
				}
				add.addAll(deleteList);
			}
		}
		return add;
	}
	
	private Pack judgeDataSafe(Pack info)
	{
		if (info == null)
			return null;
		if (StringUtils.isEmpty(info.getCode())) {
			return null;
		} else {
			if (info.getSubject()==null) {
				Catalog catalog = this.catalogDao.load(Catalog.class,
						info.getCatalog().getCode());
				if (catalog == null)
					return null;
				info.setCatalog(catalog);
			} else {
				Subject subject = this.subjectDao.load(Subject.class,
						info.getSubject().getCode());
				if (subject == null)
					return null;
				info.setSubject(subject);
				info.setCatalog(subject.getCatalog());
			}
		}
		return info;
	}
	private PackageEntity changeModel(Pack info)
	{
		if (info == null)
			return null;
		PackageEntity data = this.packageEntityDao.load(PackageEntity.class, info.getCode());
		if(data==null)
			data = new PackageEntity();
		//	套餐价格不同的情况,要酌情更新,售价高于环球的售价,改
		else if(data.getDiscount() <= info.getDiscount())
		{
			info.setDiscount(data.getDiscount());	//售价比环球售价要低[这里没有进行价格修改]
		}
		BeanUtils.copyProperties(info, data);
		data.setId(info.getCode());
		if (StringUtils.isEmpty(info.getCode())) {
			return null;
		} else {
			if (info.getSubject()==null) {
				CatalogEntity catalog = this.catalogEntityDao.find(info
						.getCatalog().getCode());
				if (catalog == null)
					return null;
				data.setCatalogEntity(catalog);
			} else {
				SubjectEntity subject = this.subjectEntityDao.load(
						SubjectEntity.class, info.getSubject().getCode());
				if (subject == null)
					return null;
				data.setSubjectEntity(subject);
				data.setCatalogEntity(subject.getCatalogEntity());
			}
		}
		return data;
	}
	private List<PackInfo> changeModel(List<Pack> packs)
	{
		List<PackInfo> list = new ArrayList<>();
		if(list != null && list.size() > 0){
			for(Pack data : packs){
				PackInfo info = this.change2InfoModel(data);
				if(info != null){
					list.add(info);
				}
			}
		}
		return list;
	}
	private PackInfo change2InfoModel(Pack data)
	{
		if (data == null)
			return null;
		PackInfo info = new PackInfo();
		BeanUtils.copyProperties(data, info);
		// 设置科目
		if (data.getSubject() != null) {
			info.setSubjectId(data.getSubject().getCode());
			info.setSubjectName(data.getSubject().getName());
		}
		if (data.getCatalog() != null) {
			info.setCatalogId(data.getCatalog().getCode());
			info.setCatalogName(data.getCatalog().getName());
		}
		return info;
	}
}
