package com.powerhigh.gdfas;

import org.apache.log4j.Category;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.powerhigh.gdfas.module.ConfigManager;
import com.powerhigh.gdfas.util.CMContext;


/** 
 * Description: 解析前置机启动类<p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2014-12-05
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */
public class Run {	

	//加载日志
	@SuppressWarnings("unused")
	private static final String resource = "log4j.properties";

	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.Run.class);

	public static void main(String[] args) throws Exception{
		try{
			ApplicationContext ctx = 
				new FileSystemXmlApplicationContext("rmi-server.xml");
			Context.ctx = ctx;
			
			CMContext context = (CMContext)ctx.getBean("contextService");
			context.init();
			
			ConfigManager config = (ConfigManager)ctx.getBean("configService");
			config.excute();
			
//			2016-01-14定时招测
			TimingTask.sndQueryAllZd0CF2();
			
			cat.info("Server Started!");
			
			//test
//			Vector sp_param = new Vector();
//			//1、行政区线码
//			sp_param.addElement("1111");
//			//2、终端地址
//			sp_param.addElement("1111");
//			
//			//3、任务号
//			sp_param.addElement("1");
//			
//			String array = "zxwgzdnl,0.2099,0912110000,1,1;zxwgzdnl,0.21,0912110100,1,1;zxwgzdnl,0.1801,0912110200,1,1;zxwgzdnl,0.1701,0912110300,1,1;zxwgzdnl,0.17,0912110400,1,1;zxwgzdnl,0.18,0912110500,1,1;zxwgzdnl,0.18,0912110600,1,1;zxwgzdnl,0.2001,0912110700,1,1;zxwgzdnl,0.1999,0912110800,1,1;zxwgzdnl,0.1899,0912110900,1,1;zxwgzdnl,0.18,0912111000,1,1;zxwgzdnl,0.19,0912111100,1,1;zxwgzdnl,0.18,0912111200,1,1;zxwgzdnl,0.1999,0912111300,1,1;zxwgzdnl,0.22,0912111400,1,1;zxwgzdnl,0.21,0912111500,1,1;zxwgzdnl,0.2001,0912111600,1,1;zxwgzdnl,0.2101,0912111700,1,1;zxwgzdnl,0.2201,0912111800,1,1;zxwgzdnl,0.2301,0912111900,1,1;zxwgzdnl,0.2201,0912112000,1,1;zxwgzdnl,0.2201,0912112100,1,1;zxwgzdnl,0.21,0912112200,1,1;zxwgzdnl,0.1999,0912112300,1,1;fxygzdnl,0.0,0912110000,1,1;fxygzdnl,0.0,0912110100,1,1;fxygzdnl,0.0,0912110200,1,1;fxygzdnl,0.0,0912110300,1,1;fxygzdnl,0.0,0912110400,1,1;fxygzdnl,0.0,0912110500,1,1;fxygzdnl,0.0,0912110600,1,1;fxygzdnl,0.0,0912110700,1,1;fxygzdnl,0.0,0912110800,1,1;fxygzdnl,0.0,0912110900,1,1;fxygzdnl,0.0,0912111000,1,1;fxygzdnl,0.0,0912111100,1,1;fxygzdnl,0.0,0912111200,1,1;fxygzdnl,0.0,0912111300,1,1;fxygzdnl,0.0,0912111400,1,1;fxygzdnl,0.0,0912111500,1,1;fxygzdnl,0.0,0912111600,1,1;fxygzdnl,0.0,0912111700,1,1;fxygzdnl,0.0,0912111800,1,1;fxygzdnl,0.0,0912111900,1,1;fxygzdnl,0.0,0912112000,1,1;fxygzdnl,0.0,0912112100,1,1;fxygzdnl,0.0,0912112200,1,1;fxygzdnl,0.0,0912112300,1,1;zxygzdnsz,73.72,0912110000,1,1;zxygzdnsz,74.36,0912110100,1,1;zxygzdnsz,74.94,0912110200,1,1;zxygzdnsz,75.45,0912110300,1,1;zxygzdnsz,75.96,0912110400,1,1;zxygzdnsz,76.47,0912110500,1,1;zxygzdnsz,77.06,0912110600,1,1;zxygzdnsz,77.68,0912110700,1,1;zxygzdnsz,78.44,0912110800,1,1;zxygzdnsz,79.19,0912110900,1,1;zxygzdnsz,79.8,0912111000,1,1;zxygzdnsz,80.43,0912111100,1,1;zxygzdnsz,81.02,0912111200,1,1;zxygzdnsz,81.6,0912111300,1,1;zxygzdnsz,82.27,0912111400,1,1;zxygzdnsz,82.95,0912111500,1,1;zxygzdnsz,83.6,0912111600,1,1;zxygzdnsz,84.26,0912111700,1,1;zxygzdnsz,85.08,0912111800,1,1;zxygzdnsz,85.88,0912111900,1,1;zxygzdnsz,86.67,0912112000,1,1;zxygzdnsz,87.5,0912112100,1,1;zxygzdnsz,88.35,0912112200,1,1;zxygzdnsz,89.08,0912112300,1,1;zxwgzdnl,0.0,0912110000,1,1;zxwgzdnl,0.0,0912110100,1,1;zxwgzdnl,0.0,0912110200,1,1;zxwgzdnl,0.0,0912110300,1,1;zxwgzdnl,0.0,0912110400,1,1;zxwgzdnl,0.0,0912110500,1,1;zxwgzdnl,0.0,0912110600,1,1;zxwgzdnl,0.0,0912110700,1,1;zxwgzdnl,0.0,0912110800,1,1;zxwgzdnl,0.0,0912110900,1,1;zxwgzdnl,0.0,0912111000,1,1;zxwgzdnl,0.0,0912111100,1,1;zxwgzdnl,0.0,0912111200,1,1;zxwgzdnl,0.0,0912111300,1,1;zxwgzdnl,0.0,0912111400,1,1;zxwgzdnl,0.0,0912111500,1,1;zxwgzdnl,0.0,0912111600,1,1;zxwgzdnl,0.0,0912111700,1,1;zxwgzdnl,0.0,0912111800,1,1;zxwgzdnl,0.0,0912111900,1,1;zxwgzdnl,0.0,0912112000,1,1;zxwgzdnl,0.0,0912112100,1,1;zxwgzdnl,0.0,0912112200,1,1;zxwgzdnl,0.0,0912112300,1,1;zxygzdnl,0.1599,0912110000,1,1;zxygzdnl,0.17,0912110100,1,1;zxygzdnl,0.1299,0912110200,1,1;zxygzdnl,0.1301,0912110300,1,1;zxygzdnl,0.1201,0912110400,1,1;zxygzdnl,0.1399,0912110500,1,1;zxygzdnl,0.15,0912110600,1,1;zxygzdnl,0.18,0912110700,1,1;zxygzdnl,0.1999,0912110800,1,1;zxygzdnl,0.1499,0912110900,1,1;zxygzdnl,0.1499,0912111000,1,1;zxygzdnl,0.14,0912111100,1,1;zxygzdnl,0.15,0912111200,1,1;zxygzdnl,0.17,0912111300,1,1;zxygzdnl,0.1699,0912111400,1,1;zxygzdnl,0.16,0912111500,1,1;zxygzdnl,0.1701,0912111600,1,1;zxygzdnl,0.1901,0912111700,1,1;zxygzdnl,0.2,0912111800,1,1;zxygzdnl,0.21,0912111900,1,1;zxygzdnl,0.1901,0912112000,1,1;zxygzdnl,0.2201,0912112100,1,1;zxygzdnl,0.18,0912112200,1,1;zxygzdnl,0.1901,0912112300,1,1;fxygzdnsz,0.0,0912110000,1,1;fxygzdnsz,0.0,0912110100,1,1;fxygzdnsz,0.0,0912110200,1,1;fxygzdnsz,0.0,0912110300,1,1;fxygzdnsz,0.0,0912110400,1,1;fxygzdnsz,0.0,0912110500,1,1;fxygzdnsz,0.0,0912110600,1,1;fxygzdnsz,0.0,0912110700,1,1;fxygzdnsz,0.0,0912110800,1,1;fxygzdnsz,0.0,0912110900,1,1;fxygzdnsz,0.0,0912111000,1,1;fxygzdnsz,0.0,0912111100,1,1;fxygzdnsz,0.0,0912111200,1,1;fxygzdnsz,0.0,0912111300,1,1;fxygzdnsz,0.0,0912111400,1,1;fxygzdnsz,0.0,0912111500,1,1;fxygzdnsz,0.0,0912111600,1,1;fxygzdnsz,0.0,0912111700,1,1;fxygzdnsz,0.0,0912111800,1,1;fxygzdnsz,0.0,0912111900,1,1;fxygzdnsz,0.0,0912112000,1,1;fxygzdnsz,0.0,0912112100,1,1;fxygzdnsz,0.0,0912112200,1,1;fxygzdnsz,0.0,0912112300,1,1;cxdl,1.36,0912110000,1,1;cxdl,1.61,0912110100,1,1;cxdl,1.31,0912110200,1,1;cxdl,1.21,0912110300,1,1;cxdl,1.06,0912110400,1,1;cxdl,1.15,0912110500,1,1;cxdl,1.17,0912110600,1,1;cxdl,1.3,0912110700,1,1;cxdl,1.66,0912110800,1,1;cxdl,1.84,0912110900,1,1;cxdl,1.21,0912111000,1,1;cxdl,1.35,0912111100,1,1;cxdl,1.27,0912111200,1,1;cxdl,1.34,0912111300,1,1;cxdl,1.59,0912111400,1,1;cxdl,1.44,0912111500,1,1;cxdl,1.3,0912111600,1,1;cxdl,1.32,0912111700,1,1;cxdl,1.69,0912111800,1,1;cxdl,1.51,0912111900,1,1;cxdl,1.57,0912112000,1,1;cxdl,1.62,0912112100,1,1;cxdl,1.67,0912112200,1,1;cxdl,1.41,0912112300,1,1;zxwgzdnsz,85.55,0912110000,1,1;zxwgzdnsz,86.37,0912110100,1,1;zxwgzdnsz,87.11,0912110200,1,1;zxwgzdnsz,87.81,0912110300,1,1;zxwgzdnsz,88.5,0912110400,1,1;zxwgzdnsz,89.2,0912110500,1,1;zxwgzdnsz,89.94,0912110600,1,1;zxwgzdnsz,90.67,0912110700,1,1;zxwgzdnsz,91.48,0912110800,1,1;zxwgzdnsz,92.3,0912110900,1,1;zxwgzdnsz,93.05,0912111000,1,1;zxwgzdnsz,93.75,0912111100,1,1;zxwgzdnsz,94.49,0912111200,1,1;zxwgzdnsz,95.22,0912111300,1,1;zxwgzdnsz,96.06,0912111400,1,1;zxwgzdnsz,96.93,0912111500,1,1;zxwgzdnsz,97.77,0912111600,1,1;zxwgzdnsz,98.59,0912111700,1,1;zxwgzdnsz,99.46,0912111800,1,1;zxwgzdnsz,100.36,0912111900,1,1;zxwgzdnsz,101.25,0912112000,1,1;zxwgzdnsz,102.14,0912112100,1,1;zxwgzdnsz,103.04,0912112200,1,1;zxwgzdnsz,103.83,0912112300,1,1;lxdl,0.28,0912110000,1,1;lxdl,0.13,0912110100,1,1;lxdl,0.19,0912110200,1,1;lxdl,0.11,0912110300,1,1;lxdl,0.1,0912110400,1,1;lxdl,0.13,0912110500,1,1;lxdl,0.31,0912110600,1,1;lxdl,0.17,0912110700,1,1;lxdl,0.27,0912110800,1,1;lxdl,0.21,0912110900,1,1;lxdl,0.33,0912111000,1,1;lxdl,0.38,0912111100,1,1;lxdl,0.28,0912111200,1,1;lxdl,0.19,0912111300,1,1;lxdl,0.28,0912111400,1,1;lxdl,0.23,0912111500,1,1;lxdl,0.4,0912111600,1,1;lxdl,0.25,0912111700,1,1;lxdl,0.32,0912111800,1,1;lxdl,0.18,0912111900,1,1;lxdl,0.21,0912112000,1,1;lxdl,0.2,0912112100,1,1;lxdl,0.15,0912112200,1,1;lxdl,0.2,0912112300,1,1;zxwgzdnsz,0.0,0912110000,1,1;zxwgzdnsz,0.0,0912110100,1,1;zxwgzdnsz,0.0,0912110200,1,1;zxwgzdnsz,0.0,0912110300,1,1;zxwgzdnsz,0.0,0912110400,1,1;zxwgzdnsz,0.0,0912110500,1,1;zxwgzdnsz,0.0,0912110600,1,1;zxwgzdnsz,0.0,0912110700,1,1;zxwgzdnsz,0.0,0912110800,1,1;zxwgzdnsz,0.0,0912110900,1,1;zxwgzdnsz,0.0,0912111000,1,1;zxwgzdnsz,0.0,0912111100,1,1;zxwgzdnsz,0.0,0912111200,1,1;zxwgzdnsz,0.0,0912111300,1,1;zxwgzdnsz,0.0,0912111400,1,1;zxwgzdnsz,0.0,0912111500,1,1;zxwgzdnsz,0.0,0912111600,1,1;zxwgzdnsz,0.0,0912111700,1,1;zxwgzdnsz,0.0,0912111800,1,1;zxwgzdnsz,0.0,0912111900,1,1;zxwgzdnsz,0.0,0912112000,1,1;zxwgzdnsz,0.0,0912112100,1,1;zxwgzdnsz,0.0,0912112200,1,1;zxwgzdnsz,0.0,0912112300,1,1;";
//			array += array;
//			array += array;
//			System.out.println(array.length());
//			sp_param.addElement(array);
//			DataSource ds = (DataSource)ctx.getBean("dataSource");
//			Util.executeProcedure(ds,"sp_saveautotaskdata",sp_param,2);
			
//			parse pi = (parse)ctx.getBean("rmiService");
//			pi.test();
		}catch(Exception e){
			cat.error("解析前置机启动失败：",e);
		}
	}
				
	
}