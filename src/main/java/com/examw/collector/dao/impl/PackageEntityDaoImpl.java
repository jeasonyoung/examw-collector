package com.examw.collector.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.util.StringUtils;

import com.examw.collector.dao.IPackageEntityDao;
import com.examw.collector.domain.local.PackageEntity;
import com.examw.collector.model.PackInfo;

/**
 * 套餐数据接口实现类
 * @author fengwei.
 * @since 2014年7月1日 上午9:56:37.
 */
public class PackageEntityDaoImpl extends BaseDaoImpl<PackageEntity> implements IPackageEntityDao{

	@Override
	public List<PackageEntity> findPacks(PackInfo info) {
		String hql = "from PackageEntity p where 1=1 ";
		Map<String, Object> parameters = new HashMap<>();
		hql = this.addWhere(info, hql, parameters);
		if(info.getSort() != null && !info.getSort().trim().isEmpty()){
			hql += " order by p." + info.getSort() + " " + info.getOrder();
		}
		return this.find(hql, parameters, info.getPage(), info.getRows());
	}

	@Override
	public Long total(PackInfo info) {
		String hql = "select count(*) from PackageEntity p where 1=1 ";
		Map<String, Object> parameters = new HashMap<>();
		hql = this.addWhere(info, hql, parameters);
		return this.count(hql, parameters);
	}
	/**
	 * 添加查询条件到HQL。
	 * @param info
	 * 查询条件。
	 * @param hql
	 * HQL
	 * @param parameters
	 * 参数。
	 * @return
	 * HQL
	 */
	protected String addWhere(PackInfo info, String hql, Map<String, Object> parameters){
		if(info.getName() != null && !info.getName().trim().isEmpty()){
			hql += "  and (p.name like :name)";
			parameters.put("name", "%" + info.getName()+ "%");
		}
		if(info.getSubjectId() != null && !info.getSubjectId().trim().isEmpty()){
			hql += "  and (p.subjectEntity.id = :subjectId)";
			parameters.put("subjectId", info.getSubjectId());
		}
		if(!StringUtils.isEmpty(info.getCatalogId()))
		{
			hql += "  and (p.catalogEntity.code = :catalogId)";
			parameters.put("catalogId", info.getCatalogId());
		}
		return hql;
	}
	@Override
	public void delete(String subjectId) {
		String hql = "delete from PackageEntity p where p.subjectEntity.id = :subjectId";
		Query query = this.getCurrentSession().createQuery(hql);
		query.setParameter("subjectId", subjectId);
		query.executeUpdate();
	}
}
