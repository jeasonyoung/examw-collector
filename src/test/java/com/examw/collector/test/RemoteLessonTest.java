package com.examw.collector.test;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.examw.collector.domain.Catalog;
import com.examw.collector.domain.Pack;
import com.examw.collector.domain.Relate;
import com.examw.collector.domain.SubClass;
import com.examw.collector.service.IDataServer;
import com.thoughtworks.xstream.XStream;

/**
 * 远程课程数据测试。
 * @author yangyong.
 * @since 2014-06-26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-examw-collector.xml"})
public class RemoteLessonTest {
	@Resource
	private IDataServer dataServer;
	/**
	 * 加载课程数据。
	 * @throws IOException 
	 */
	//@Test
	public void loadCataData() throws IOException{
		System.out.print("开始获取课程类别：");
		List<Catalog> list = this.dataServer.loadCatalogs();
		
		XStream xStream = new XStream();
		String xml = xStream.toXML(list);
		System.out.print(xml);
	}
	@Test
	public void loadClasses(){
		List<SubClass>  list = this.dataServer.loadClasses("1905", null);
		for(SubClass s:list){
			System.out.println(s.getSubject().getCode());
		}
		XStream xStream = new XStream();
		String xml = xStream.toXML(list);
		System.out.print(xml);
	}
	//@Test
	public void loadRelates(){
		List<Relate> list = this.dataServer.loadRelates("491");
		XStream xStream = new XStream();
		String xml = xStream.toXML(list);
		System.out.print(xml);
	}
	//@Test
	public void loadPacks(){
		List<Pack> list = this.dataServer.loadPacks("700", "700");
		XStream xStream = new XStream();
		String xml = xStream.toXML(list);
		System.out.print(xml);
	}
}