package com.examw.collector.domain;

import java.io.Serializable;
/**
 * 课程科目。
 * @author yangyong.
 * @since 2014-06-26.
 */
public class Subject extends BaseDomain implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code,name;
	private Integer classTotal;
	private Catalog catalog;
	
	/**
	 * 获取所属课程。
	 * @return 所属课程。
	 */
	public Catalog getCatalog() {
		return catalog;
	}
	/**
	 * 设置所属课程。
	 * @param catalog
	 * 所属课程。
	 */
	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}
	/**
	 * 获取科目代码。
	 * @return 科目代码。
	 */
	public String getCode() {
		return code;
	}
	/**
	 * 设置科目代码。
	 * @param code
	 * 科目代码。
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 获取科目名称。
	 * @return 科目名称。
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置科目名称。
	 * @param name
	 * 科目名称。
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取科目下的班级数。
	 * @return 科目下的班级数。
	 */
	public Integer getClassTotal() {
		return classTotal;
	}
	/**
	 * 设置科目下的班级数。
	 * @param classTotal
	 * 科目下的班级数。
	 */
	public void setClassTotal(Integer classTotal) {
		this.classTotal = classTotal;
	}
	/* 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	/* 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subject other = (Subject) obj;
		boolean flag = true;	updateInfo = "";
		if (catalog == null) {
			if (other.catalog != null)
				flag = false;
		} else if (!catalog.getCode().equals(other.catalog.getCode()))
		{
			updateInfo += (" 上级分类变更:"+other.catalog.getName()+other.catalog.getCode()+"-->"+catalog.getName()+catalog.getCode());
			flag = false;
		}
		if (code == null) {
			if (other.code != null)
				flag = false;
		} else if (!code.equals(other.code))
		{
			updateInfo += (" 代码变更:"+other.code+"-->"+code);
			flag = false;
		}
		if (name == null) {
			if (other.name != null)
				flag = false;
		} else if (!name.equals(other.name))
		{
			updateInfo += (" 名称变更:"+other.name+"-->"+name);
			flag = false;
		}
		return flag;
	}
	
	@Override
	public String toString() {
		if(catalog==null) return "";
		return "所属类别:"+catalog.getName()+"("+catalog.getCode()+")";
	}
	
	private String add;	//是否自己添加的科目

	/**
	 * 获取 是否自己添加的科目
	 * @return add
	 * 
	 */
	public String getAdd() {
		return add;
	}
	/**
	 * 设置 是否自己添加的科目
	 * @param add
	 * 
	 */
	public void setAdd(String add) {
		this.add = add;
	}
	
}