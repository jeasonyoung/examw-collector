package com.examw.collector.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.examw.collector.dao.ILoginLogDao;
import com.examw.collector.domain.LoginLog;
import com.examw.collector.model.LoginLogInfo;

/**
 * 登录日志数据接口实现。
 * @author yangyong.
 * @since 2014-05-17.
 */
public class LoginLogDaoImpl extends BaseDaoImpl<LoginLog> implements ILoginLogDao{
	/*
	 * 查询数据。
	 * @see com.examw.netplatform.dao.admin.ILoginLogDao#findLoginLogs(com.examw.netplatform.model.admin.LoginLogInfo)
	 */
	@Override
	public List<LoginLog> findLoginLogs(LoginLogInfo info) {
		String hql = "from LoginLog l where 1=1 ";
		Map<String, Object> parameters = new HashMap<>();
		hql = this.addWhere(info, hql, parameters);
		if(!StringUtils.isEmpty(info.getSort())){
			hql += " order by l." + info.getSort() + " " + info.getOrder();
		}
		return this.find(hql, parameters, info.getPage(), info.getRows());
	}
	/*
	 * 查询数据统计。
	 * @see com.examw.netplatform.dao.admin.ILoginLogDao#total(com.examw.netplatform.model.admin.LoginLogInfo)
	 */
	@Override
	public Long total(LoginLogInfo info) {
		String hql = "select count(*) from LoginLog l where 1 = 1 ";
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
	protected String addWhere(LoginLogInfo info, String hql, Map<String, Object> parameters){
		if(!StringUtils.isEmpty(info.getAccount())){
			hql += " and (l.account like :account)";
			parameters.put("account", "%" + info.getAccount() + "%");
		}
		return hql;
	}
}