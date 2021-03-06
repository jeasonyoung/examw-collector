package com.examw.collector.service;

import com.examw.collector.model.RelateInfo;

/**
 * 课节服务接口
 * @author fengwei.
 * @since 2014年7月1日 上午9:23:40.
 */
public interface IListenEntityService extends IBaseDataService<RelateInfo>{
	/**
	 * 初始化导入数据
	 * @param info
	 */
	void init(RelateInfo info);
	/**
	 * 根据班级ID删除课节
	 * @param subClassId
	 */
	void delete(RelateInfo info);
}
