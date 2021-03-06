package com.examw.collector.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.examw.collector.dao.IAdVideoDao;
import com.examw.collector.dao.ICatalogDao;
import com.examw.collector.dao.ICatalogEntityDao;
import com.examw.collector.dao.IErrorRecordDao;
import com.examw.collector.dao.IGradeEntityDao;
import com.examw.collector.dao.IListenEntityDao;
import com.examw.collector.dao.IOperateLogDao;
import com.examw.collector.dao.IRelateDao;
import com.examw.collector.dao.ISubClassDao;
import com.examw.collector.dao.ISubjectDao;
import com.examw.collector.dao.ISubjectEntityDao;
import com.examw.collector.dao.ITeacherEntityDao;
import com.examw.collector.dao.IUpdateRecordDao;
import com.examw.collector.domain.AdVideo;
import com.examw.collector.domain.Catalog;
import com.examw.collector.domain.ErrorRecord;
import com.examw.collector.domain.OperateLog;
import com.examw.collector.domain.Relate;
import com.examw.collector.domain.SubClass;
import com.examw.collector.domain.Subject;
import com.examw.collector.domain.UpdateRecord;
import com.examw.collector.domain.local.CatalogEntity;
import com.examw.collector.domain.local.GradeEntity;
import com.examw.collector.domain.local.ListenEntity;
import com.examw.collector.domain.local.SubjectEntity;
import com.examw.collector.domain.local.TeacherEntity;
import com.examw.collector.model.SubClassInfo;
import com.examw.collector.service.IDataServer;
import com.examw.collector.service.IGradeUpdateService;
import com.examw.collector.support.JSONUtil;
import com.examw.model.DataGrid;

/**
 * 班级数据更新服务接口实现类
 * @author fengwei.
 * @since 2014年7月9日 下午5:01:58.
 */
public class GradeUpdateServiceImpl implements IGradeUpdateService{
	private ISubClassDao subClassDao;
	private ICatalogDao catalogDao;
	private ISubjectDao subjectDao;
	private IGradeEntityDao gradeEntityDao;
	private ISubjectEntityDao subjectEntityDao;
	private ICatalogEntityDao catalogEntityDao;
	private IListenEntityDao listenEntityDao;
	private IDataServer dataServer;
	private IRelateDao relateDao;
	private IOperateLogDao operateLogDao;
	private IAdVideoDao adVideoDao;
	private ITeacherEntityDao teacherEntityDao;
	private IErrorRecordDao errorRecordDao;
	private IUpdateRecordDao updateRecordDao;
	
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * 设置操作日志数据接口
	 * @param operateLogDao
	 * 
	 */
	public void setOperateLogDao(IOperateLogDao operateLogDao) {
		this.operateLogDao = operateLogDao;
	}
	/**
	 * 设置 远程班级数据接口
	 * @param subClassDao
	 * 
	 */
	public void setSubClassDao(ISubClassDao subClassDao) {
		this.subClassDao = subClassDao;
	}

	/**
	 * 设置 远程科目数据接口
	 * @param subjectDao
	 * 
	 */
	public void setSubjectDao(ISubjectDao subjectDao) {
		this.subjectDao = subjectDao;
	}

	/**
	 * 设置 本地班级数据接口
	 * @param gradeEntityDao
	 * 
	 */
	public void setGradeEntityDao(IGradeEntityDao gradeEntityDao) {
		this.gradeEntityDao = gradeEntityDao;
	}

	/**
	 * 设置 本地科目数据接口
	 * @param subjectEntityDao
	 * 
	 */
	public void setSubjectEntityDao(ISubjectEntityDao subjectEntityDao) {
		this.subjectEntityDao = subjectEntityDao;
	}
	
	/**
	 * 设置 本地课节数据接口
	 * @param listenEntityDao
	 * 
	 */
	public void setListenEntityDao(IListenEntityDao listenEntityDao) {
		this.listenEntityDao = listenEntityDao;
	}

	/**
	 * 设置 远程数据获取接口
	 * @param dataServer
	 * 
	 */
	public void setDataServer(IDataServer dataServer) {
		this.dataServer = dataServer;
	}
	/**
	 * 设置 远程课节数据接口
	 * @param relateDao
	 * 
	 */
	public void setRelateDao(IRelateDao relateDao) {
		this.relateDao = relateDao;
	}
	
	/**
	 * 设置 实际分类数据接口
	 * @param catalogEntityDao
	 * 
	 */
	public void setCatalogEntityDao(ICatalogEntityDao catalogEntityDao) {
		this.catalogEntityDao = catalogEntityDao;
	}
	/**
	 * 设置 分类数据接口
	 * @param catalogDao
	 * 
	 */
	public void setCatalogDao(ICatalogDao catalogDao) {
		this.catalogDao = catalogDao;
	}
	
	/**
	 * 设置 宣传视频数据接口
	 * @param adVideoDao
	 * 
	 */
	public void setAdVideoDao(IAdVideoDao adVideoDao) {
		this.adVideoDao = adVideoDao;
	}
	/**
	 * 设置 老师数据接口 
	 * @param teacherEntityDao
	 * 
	 */
	public void setTeacherEntityDao(ITeacherEntityDao teacherEntityDao) {
		this.teacherEntityDao = teacherEntityDao;
	}
	
	
	/**
	 * 设置 错误记录数据接口
	 * @param errorRecordDao
	 * 
	 */
	public void setErrorRecordDao(IErrorRecordDao errorRecordDao) {
		this.errorRecordDao = errorRecordDao;
	}
	/**
	 * 设置 更新记录数据接口
	 * @param updateRecordDao
	 * 
	 */
	public void setUpdateRecordDao(IUpdateRecordDao updateRecordDao) {
		this.updateRecordDao = updateRecordDao;
	}
	@Override
	public List<SubClassInfo> update(List<SubClassInfo> subClasses,String account) {
		if(subClasses == null ||subClasses.size()==0) return new ArrayList<SubClassInfo>();
		List<SubClassInfo> list = new ArrayList<SubClassInfo>();
		for(SubClassInfo info:subClasses){
			if(StringUtils.isEmpty(info.getStatus())||info.getStatus().equals("旧的")){
				continue;
			}
			if(info.getStatus().equals("被删")){
				//本地副本
				SubClass data1 = this.subClassDao.load(SubClass.class, info.getCode());
				GradeEntity data2 = this.gradeEntityDao.load(GradeEntity.class, info.getCode());
				if(data1 != null && data2!=null){
					//要先删课节
					this.relateDao.delete(data1.getCode());
					this.subClassDao.delete(data1);
					//TODO 是否连删
				}
				//实际数据
				if(data2 != null){
					//先删课节
					this.listenEntityDao.delete(data2.getId());
					this.gradeEntityDao.delete(data2);
					list.add(info);
					//TODO 是否连删
				}
				continue;
			}
			SubClass sc = this.changeRemoteModel(info);
			GradeEntity g = changeLocalModel(info);
			if(sc!=null && g!=null)
			{
				this.subClassDao.saveOrUpdate(sc);
			}
			if(g!=null)
			{
				this.gradeEntityDao.saveOrUpdate(g);
				//删除原来班级带的课节地址,重新插入新的
				this.deleteOldAndInsertNewListen(info.getCode(),info.getDemo(),null,g);
				list.add(info);
			}
		}
		// 添加操作日志
		OperateLog log = new OperateLog();
		log.setId(UUID.randomUUID().toString());
		log.setType(OperateLog.TYPE_UPDATE_GRADE);
		log.setName("更新班级数据");
		log.setAddTime(new Date());
		log.setAccount(account);
		log.setContent(JSONUtil.ObjectToJson(list));
		this.operateLogDao.save(log);
		return list;
	}
	/**
	 * 本地数据模型转换
	 * @param info
	 * @return
	 */
	private SubClass changeRemoteModel(SubClassInfo info){
		if(info == null) return null;
		SubClass data = new SubClass();
		BeanUtils.copyProperties(info, data);
		if(StringUtils.isEmpty(info.getSubjectId())){
			return null;
		}else{
			Subject subject = this.subjectDao.load(Subject.class, info.getSubjectId());
			if(subject==null) return null;
			data.setSubject(subject);
			data.setCatalog(subject.getCatalog());
		}
		return data;
	}
	
	/*private void updateLocal(List<SubClassInfo> subClasses) {
		if(subClasses == null ||subClasses.size()==0) return;
		List<SubClassInfo> list = new ArrayList<SubClassInfo>();
		for(SubClassInfo info:subClasses){
			if(StringUtils.isEmpty(info.getStatus())||info.getStatus().equals("旧的")){
				continue;
			}
			if(info.getStatus().equals("被删")){
				GradeEntity data = this.gradeEntityDao.load(GradeEntity.class, info.getCode());
				if(data != null){
					//先删课节
					this.listenEntityDao.delete(data.getId());
					this.gradeEntityDao.delete(data);
					list.add(info);
					//TODO 是否连删
				}
				continue;
			}
			GradeEntity g = changeLocalModel(info);
			if(g!=null)
			{
				this.gradeEntityDao.saveOrUpdate(g);
				//删除原来班级带的课节地址,重新插入新的
				this.deleteOldAndInsertNewListen(info.getCode());
				list.add(info);
			}
		}
		// 添加操作日志
		OperateLog log = new OperateLog();
		log.setId(UUID.randomUUID().toString());
		log.setType(OperateLog.TYPE_UPDATE_GRADE);
		log.setName("更新班级数据(实际数据)");
		log.setAddTime(new Date());
		log.setContent(JSONUtil.ObjectToJson(list));
		this.operateLogDao.save(log);
	}*/
	
	/**
	 * 实际班级数据模型转换
	 * @param info
	 * @return
	 */
	private GradeEntity changeLocalModel(SubClassInfo info){
		if(info == null) return null;
		GradeEntity data = new GradeEntity();
		BeanUtils.copyProperties(info, data);
		data.setId(info.getCode());
		if(StringUtils.isEmpty(info.getSubjectId())){
			return null;
		}else{
			SubjectEntity subject = this.subjectEntityDao.load(SubjectEntity.class,info.getSubjectId());
			if(subject==null) return null;
			data.setSubjectEntity(subject);
		}
		return data;
	}
	/**
	 * 删除实际班级数据,然后插入新进数据
	 * @param gradeId
	 */
	private void deleteOldAndInsertNewListen(String gradeId,String demoNum,AdVideo adVideo,GradeEntity g){
		//删除
		this.listenEntityDao.delete(gradeId);
		//插入
		List<Relate> list = this.dataServer.loadRelates(gradeId);
		List<ListenEntity> data = new ArrayList<ListenEntity>();
		if(list!=null && list.size()>0){
			dealRelateList(list,demoNum);
			for(Relate r:list){
				ListenEntity l = this.changeListenModel(r);
				if(l!=null)
					data.add(l);
			}
		}
		if(data.size()>0){
			this.listenEntityDao.batchSave(data);
		}
		if(adVideo!=null){
			ListenEntity listen  = new ListenEntity();
			listen.setAddress(adVideo.getAddress());
			listen.setGrade(g);
			listen.setName(adVideo.getName()+"-宣讲试听");
			listen.setId("k"+adVideo.getCode()+"_"+g.getId());
			listen.setUpdateDate(formatter.format(new Date()));
			this.listenEntityDao.save(listen);
		}
		if(!StringUtils.isEmpty(g.getTeacherId()) && !"0".equals(g.getTeacherId())){
			TeacherEntity teacher = this.teacherEntityDao.load(TeacherEntity.class, g.getTeacherId());
			if(teacher == null){
				this.teacherEntityDao.save(this.dataServer.loadTeacher(g.getTeacherId()));
			}
		}
	}
	/**
	 * 处理课节
	 * @param relateList
	 * @param demoNum
	 */
	private void dealRelateList(List<Relate> relateList,String demoNum){
		if(StringUtils.isEmpty(demoNum)) return ;
		if(demoNum.equals("0")) return;
		try{
			String[] arr = demoNum.split(",");
			int m = 0;
			Collections.sort(relateList);	//排序
			for(Relate r:relateList){	//赋值
				if(r == null) return;
				if(!StringUtils.isEmpty(r.getAddress()) && m < arr.length){
					r.setOrderNum(Integer.valueOf(arr[m]));
					m++;
				}
			}
		}catch(Exception e){ return;}
		return;
	}
	private ListenEntity changeListenModel(Relate relate){
		if(relate == null) return null;
		ListenEntity data = new ListenEntity();
		BeanUtils.copyProperties(relate, data);
		data.setId(DataServerImpl.ID_PREFIX+relate.getNum().toString());	//进行加e处理
		if(relate.getSubclass()==null) return null;
		GradeEntity grade = new GradeEntity();
		grade.setId(relate.getSubclass().getCode());
		data.setGrade(grade);
		return data;
	}
	/**
	 * 返回更新以后的结果
	 */
	@Override
	public DataGrid<SubClassInfo> dataGridUpdate(String account) {
		List<SubClassInfo> list = this.update(account);
		DataGrid<SubClassInfo> grid = new DataGrid<SubClassInfo>();
		grid.setRows(list);
		grid.setTotal((long) list.size());
		return grid;
	}
	/**
	 * 直接更新
	 * @param account
	 * @return
	 */
	public List<SubClassInfo> update(String account) {
		//找出需要查找并且有变化的科目集合
		List<SubClass> grades = this.findChangedSubClass();
		List<SubClass> listForShow = new ArrayList<SubClass>();
		if(grades == null ||grades.size()==0){
			OperateLog log = new OperateLog();
			log.setId(UUID.randomUUID().toString());
			log.setType(OperateLog.TYPE_UPDATE_GRADE);
			log.setName("更新班级数据");
			log.setAddTime(new Date());
			log.setAccount(account);
			log.setContent("[ ]");
			this.operateLogDao.save(log);
			return null;
		}
		for(SubClass info:grades){
			if(StringUtils.isEmpty(info.getStatus())||info.getStatus().equals("旧的")){
				continue;
			}
			if(info.getStatus().equals("被删")){
				//本地副本
				SubClass data1 = this.subClassDao.load(SubClass.class, info.getCode());
				GradeEntity data2 = this.gradeEntityDao.load(GradeEntity.class, info.getCode());
				if(data1 != null){
					this.relateDao.delete(data1.getCode());
					this.subClassDao.delete(data1);
					//TODO 是否连删
				}
				if(data2 != null)
				{
					this.listenEntityDao.delete(data2.getId());
					this.gradeEntityDao.delete(data2);
					listForShow.add(info);
				}else{
					info.setUpdateInfo("<span style='color:purple'>删除失败,数据不存在或已被删除</span>"+info.getUpdateInfo());
					
				}
				continue;
			}
			//如果页面中没有这个ID,不要进行操作
			if(info.getStatus().equals("页面无此ID")){
				info.setUpdateInfo("<span style='color:purple'>插入或更新失败</span>"+info.getUpdateInfo());
				ErrorRecord error = this.errorRecordDao.find(info.getCode(),ErrorRecord.TYPE_ERROR_GRADE);
				if(error == null){
					listForShow.add(info);
					error = new ErrorRecord(UUID.randomUUID().toString(),info.getCode(),
							"页面无此ID",info.getUpdateInfo(),ErrorRecord.TYPE_ERROR_GRADE,"插入失败",new Date(),info.getCatalog().getMyId());
					this.errorRecordDao.save(error);
				}
				continue;
			}
			if(info.getStatus().equals("页面有数据库没有")){
				info.setUpdateInfo("<span style='color:purple'>插入或更新失败</span>"+info.getUpdateInfo());
				listForShow.add(info);
				continue;
			}
			//本地副本
			SubClass s = judgeDataSafe(info);
			GradeEntity se = changeModel(info);
			if(s != null)
			{
				this.subClassDao.saveOrUpdate(s);
			}
			//实际数据
			if(se != null)
			{
				this.gradeEntityDao.saveOrUpdate(se);
				//删除原来班级带的课节地址,重新插入新的
				//this.deleteOldAndInsertNewListen(info.getCode(),info.getDemo(),info.getAdVideo(),se);
				info.setStatus("更新成功");
				listForShow.add(info);
			}else{
				info.setUpdateInfo("<span style='color:purple'>插入或更新失败</span>"+info.getUpdateInfo());
				ErrorRecord error = this.errorRecordDao.find(info.getCode(),ErrorRecord.TYPE_ERROR_GRADE);
				if(error == null){
					listForShow.add(info);
					error = new ErrorRecord(UUID.randomUUID().toString(),info.getCode(),
							"找不到科目",info.getUpdateInfo(),ErrorRecord.TYPE_ERROR_GRADE,"插入失败",new Date(),info.getCatalog().getMyId());
					this.errorRecordDao.save(error);
				}
			}
		}
		List<SubClassInfo> result = this.changeModel(listForShow);
		addToUpdateRecord(listForShow);
		//添加操作日志
		OperateLog log = new OperateLog();
		log.setId(UUID.randomUUID().toString());
		log.setType(OperateLog.TYPE_UPDATE_GRADE);
		log.setName("更新班级数据");
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
	private List<SubClass> findChangedSubClass(){
		//需要进行更新比对的分类
		List<CatalogEntity> needList = this.catalogEntityDao.findAllWithCode();
		List<SubClass> gradeList = new ArrayList<SubClass>();
		for(CatalogEntity entity : needList){
			String[] arr = entity.getCode().split(",");
			String[] pages = null;
			if(!StringUtils.isEmpty(entity.getPageUrl()))
				 pages = entity.getPageUrl().split(",");
			for(int i=0;i<arr.length;i++)
			{
				if(StringUtils.isEmpty(arr[i])) continue;
				String page = pages==null?"":pages.length==1?pages[0]:pages[i];
				gradeList.addAll(this.findChangedSubClass(entity.getId(),arr[i],page));
			}
		}
		return gradeList;
	}
	private List<SubClass> findChangedSubClass(String classId,String catalogId,String page)
	{
		Catalog catalog = this.catalogDao.load(Catalog.class, catalogId);
		catalog.setMyId(classId);
		//Subject subject = this.subjectDao.load(Subject.class, info.getSubjectId());
		List<SubClass> data = this.dataServer.loadClasses(catalogId, null);
		List<SubClass> add = new ArrayList<SubClass>();
		StringBuffer existIds = new StringBuffer();
		//*******取消页面的数据的采集比对***************************************
		//String ids = this.dataServer.loadPageGradeIds(page);
		String ids = "";	
		//*******取消页面的数据的采集比对***************************************
		if(!StringUtils.isEmpty(ids)){
			existIds.append("'0'").append(ids.replaceAll("("+DataServerImpl.ID_PREFIX+"\\d+)", "'$1'"));	//如果不为空,页面上的ID就是应该存在的ID
		}
		if(data == null) return add;
		findMissId(catalog,data,ids,add);
		for(SubClass s:data){
			if(s == null) continue;
			s.setCatalog(catalog);
			if(!StringUtils.isEmpty(s.getCode()) && StringUtils.isEmpty(ids)) existIds.append("'"+s.getCode()+"'").append(",");
			SubClass local_s = this.subClassDao.load(SubClass.class, s.getCode());
			if(local_s == null){
				s.setStatus("新增");
				s.setUpdateInfo("<span style='color:blue'>[新增]</span>"+s.toString());
				addSubject(s,ids);
				add.add(s);
			}else if(s.equals(local_s)){
				continue;
			}else{
				s.setStatus("新的");
				//local_s.setStatus("旧的");
				if(!s.getSalePrice().equals(local_s.getSalePrice()))
				{
					//查询实际数据比较价格
					GradeEntity real_s = this.gradeEntityDao.load(GradeEntity.class, s.getCode());
					if(real_s != null){
						s.setUpdateInfo("<span style='color:red'>！价格变化,我们的售价为:"+real_s.getSalePrice()+";新售价:"+s.getSalePrice()+"</span>"+s.getUpdateInfo());
					}
				}
				s.setUpdateInfo("<span style='color:red'>[更新]</span>"+s.getUpdateInfo());
				BeanUtils.copyProperties(s, local_s, new String[]{"catalog"});	//已经存在的,必须用原有的数据进行更新,不然会出错
				local_s.setCatalog(catalog);
//				if(s.getAdVideo()!=null){
//					AdVideo adVideo = this.adVideoDao.load(AdVideo.class, s.getAdVideo().getCode());
//					if(adVideo==null){
//						adVideo = s.getAdVideo();
//						this.adVideoDao.save(adVideo);
//					}
//					s.setAdVideo(adVideo);
//				}
				//local_s.setAdVideo(s.getAdVideo());
				local_s.setAdVideo(null);
				add.add(local_s);
				//add.add(local_s);
			}
		}
		if(existIds.length()>0)
		{
			existIds.append("'0'");
			SubClassInfo info = new SubClassInfo();
			info.setCatalogId(catalogId);
			List<SubClass> deleteList = this.subClassDao.findDeleteSubClasss(existIds.toString(),info);
			if(deleteList!=null && deleteList.size()>0)
			{
				for(SubClass s:deleteList){
					s.setStatus("被删");
					s.setUpdateInfo(s.toString());
				}
				add.addAll(deleteList);
			}
		}
		return add;
	}
	
	private SubClass judgeDataSafe(SubClass info)
	{
		if(info == null) return null;
		if(info.getSubject()==null || StringUtils.isEmpty(info.getSubject().getCode())){
			return null;
		}else{
			Subject subject = this.subjectDao.load(Subject.class, info.getSubject().getCode());
			if(subject==null) return null;
			info.setSubject(subject);
		}
		if(info.getAdVideo()!=null){
			AdVideo adVideo = this.adVideoDao.load(AdVideo.class, info.getAdVideo().getCode());
			if(adVideo == null){
				adVideo = info.getAdVideo();
				this.adVideoDao.save(adVideo);
				info.setAdVideo(adVideo);
			}else
				info.setAdVideo(adVideo);
		}
		return info;
	}
	private GradeEntity changeModel(SubClass info)
	{
		if(info == null) return null;
		GradeEntity data = this.gradeEntityDao.load(GradeEntity.class, info.getCode());
		if(data!=null){	//有变化的
			BeanUtils.copyProperties(info, data);
			SubjectEntity subject = data.getSubjectEntity();
			info.setUpdateInfo(info.getUpdateInfo()+"   所属类别:"+info.getCatalog().getName()+"("+info.getCatalog().getCode()+");"
					+("所属科目:"+subject.getName()+"("+subject.getId()+");"));
			return data;
		}
		data = new GradeEntity();
		BeanUtils.copyProperties(info, data);
		data.setId(info.getCode());
		if(info.getSubject()==null || StringUtils.isEmpty(info.getSubject().getCode())){
			return null;
		}else{
			SubjectEntity subject = this.subjectEntityDao.load(SubjectEntity.class,info.getSubject().getCode());
			if(subject==null){
				info.setUpdateInfo(info.getUpdateInfo()+" !!没有找到相应的科目!!");
				return null;
			}
			data.setSubjectEntity(subject);
		}
		return data;
	}
	private List<SubClassInfo> changeModel(List<SubClass> list)
	{
		List<SubClassInfo> results = new ArrayList<>();
		if(list != null && list.size() > 0){
			for(SubClass data : list){
				SubClassInfo info = this.change2InfoModel(data);
				if(info != null){
					results.add(info);
				}
			}
		}
		return results;
	}
	private SubClassInfo change2InfoModel(SubClass data){
		if(data == null) return null;
		SubClassInfo info = new SubClassInfo();
		BeanUtils.copyProperties(data, info);
		if(data.getSubject() != null){
			info.setSubjectId(data.getSubject().getCode());
			info.setSubjectName(data.getSubject().getName());
		}
		Catalog c = data.getCatalog();
		if( c!= null){
			info.setCatalogId(c.getCode());
			info.setCatalogName(c.getName());	//这句代码为什么会出现no session的情况
		}
		return info;
	}
	/**
	 * 页面上有的,但是实际没有的科目进行添加
	 * @param info
	 * @param ids
	 */
	private void addSubject(SubClass info,String ids)
	{
		if(info==null||StringUtils.isEmpty(ids)) return;
		if(ids.contains(","+info.getCode()+",")){	//如果页面上包含这个班级
			Subject s = this.subjectDao.load(Subject.class, info.getSubject().getCode());
			if(s == null)	//实际数据库中找不到这个
			{
				//把这个科目加上
				s = info.getSubject();
				s.setAdd("1"); 	//表示是自己加上的
				s.setCatalog(info.getCatalog());
				subjectDao.save(s);
				//实际数据库也加上
				SubjectEntity entity = new SubjectEntity();
				entity.setId(s.getCode());
				entity.setName(s.getName());
				entity.setCatalogEntity(this.catalogEntityDao.find(info.getCatalog().getCode()));
				subjectEntityDao.save(entity);
				//添加记录
				info.setUpdateInfo(info.getUpdateInfo()+" !!![同时新增科目]!!!");
			}
		}else{
			info.setStatus("页面无此ID");
			info.setUpdateInfo("<span style='color:#777777'>[页面不存在此ID]</span><span style='color:blue'>[新增]</span>"+info.toString());
		}
	}
	
	private void addToUpdateRecord(List<SubClass> list){
		if(list.size() == 0) return;
		for(SubClass info:list){
			UpdateRecord data = new UpdateRecord();
			data = new UpdateRecord(UUID.randomUUID().toString(),info.getCode(),
					info.getName(),info.getUpdateInfo(),UpdateRecord.TYPE_UPDATE_GRADE,"更新成功".equals(info.getStatus())?"更新成功":"更新失败",new Date(),info.getCatalog().getMyId());
			this.updateRecordDao.save(data);
		}
	}
	
	private void findMissId(Catalog catalog,List<SubClass> data,String ids,List<SubClass> add){
		if(StringUtils.isEmpty(ids)) return;
		try{
			String[] arr = ids.split(",");
			for(int i=1;i<arr.length;i++){
				boolean exist = false;
				for(SubClass s:data){
					if(arr[i].equals(s.getCode())){
						exist = true;
						break;
					}
				}
				if(!exist){
					SubClass sub = new SubClass();
					sub.setCode(arr[i]);
					sub.setStatus("页面有数据库没有");
					sub.setCatalog(catalog);
					sub.setUpdateInfo("[页面存在但没有数据]!!!需要手动操作!!! 所属考试:"+catalog.getName()+"("+catalog.getCode()+"),班级的ID号:"+arr[i]);
					add.add(sub);
				}
			}
		}catch(Exception e){
			System.out.println(ids);
			e.printStackTrace();
		}
	}
}
