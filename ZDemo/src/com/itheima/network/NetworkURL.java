package com.itheima.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NetworkURL {
	private final static String BASE_URL = "http://service.incardata.com.cn/eightymiles/api/mobi/";
//	static String BASE_URL = "http://10.0.12.220:8011/eightymiles/api/mobi/";
	/**
	 * 创建账号
	 */
	public final static String REGISTER_URL = BASE_URL + "checkPhoneNameEmail";  //用户名、手机号、邮箱是否注册
	public final static String CODE_URL = BASE_URL + "sendVerifyCodeSms";  //发送手机验证码(动态地址,从下面方法获取)
	public final static String REGISTER_USER_URL = BASE_URL + "register";  //注册用户
	
	/**
	 * 创建车辆信息
	 */
	public final static String GET_BRAND_SERIES_URL = BASE_URL + "vehicle/brand";  //获取所有品牌
	public final static String VEHICLE_INFO_URL = BASE_URL + "vehicle/checkLicense";  //车牌号是否已注册(动态地址,从下面方法获取)
	public final static String BIND_VEHICLE = BASE_URL + "vehicle/bind";  //用户绑定车辆
	public final static String UN_BIND_VEHICLE = BASE_URL + "vehicle/unbind";  //用户解绑车辆
	
	/**
	 * 登陆
	 */
	public final static String LOGIN_URL = BASE_URL + "login"; 

	
	/**
	 * 找回密码
	 */
	//验证验证码  参考上面发送手机验证码
	public final static String PASSWORD_RESET = BASE_URL + "resetPwd";  //找回密码
	public final static String PASSWORD_MODIFY = BASE_URL + "user/changePwd";  //密码修改

	
	/**
	 * 用户信息
	 */
	public final static String USERNAME_MODIFY = BASE_URL + "user";  //更新用户信息
	//密码修改 参考上面
	
	
	/**
	 * 车辆信息
	 */
	public final static String VEHICLE_MODIFY = BASE_URL + "vehicle";  //(1)修改车辆信息,(2)获取用户的车辆信息
	
	
	/**
	 * 行程数据
	 */
	public final static String VEHICLE_TRIP = BASE_URL + "vehicle/trip";  //(1)批量添加行程数据,(2)获取行程分页数据
	
	
	/**
	 * 故障码
	 */
	public final static String FAULT_CODE = BASE_URL + "vehicle/faultcode";  //(1)批量添加故障码,(2)获取故障码分页数据(动态地址获取)
	
	
	/**
	 * 保养
	 */
	public final static String CARE_RECORD = BASE_URL + "vehicle/care";  //(1)获取保养记录分页数据,(2)添加保养记录,(3)修改保养记录
	public final static String ALL_CARE_RECORD = BASE_URL + "vehicle/careConfig";  //获取所有保养项目
	public final static String ALL_CARE_ADVICE = BASE_URL + "vehicle/careAdvice";  //获取保养建议
	
	/** 每日百公里油耗 */
	public final static String DAY_AVG_OIL_RATE = BASE_URL + "vehicle/dayAvgOilRate";
	/** 每日行驶里程 */
	public final static String DAY_MILEAGE = BASE_URL + "vehicle/dayMileage";
	/** 每日最高车速 */
	public final static String DAY_MAX_SPEED = BASE_URL + "vehicle/dayMaxSpeed";
	/** 每日急加速次数 */
	public final static String DAY_RAPID_ACCEL_TIMES = BASE_URL + "vehicle/daySpeedUpDownTimes";
	
	
	/**
	 * 升级
	 */
	public final static String UPGRADE_URL = "http://dev.incardata.com.cn/carcloud/api/file/version";
	
	/**
	 * 下载
	 */
	public final static String DOWN_URL ="http://dev.incardata.com.cn/carcloud/api/file/download";
	
	public final static String ADD_FEEDBACK_ADVICE = BASE_URL+"feedback";
	
	
	
	/* -----------------------动态地址拼接获取 ----------------------- */
	
	/**
	 * 获取手机验证码
	 * @param phone
	 * @return
	 */
	public static String getPhoneCode(String phone){
		StringBuilder sb = new StringBuilder();
		sb.append(CODE_URL).append("/").append(phone);
		return sb.toString();
	}
	
	/**
	 * 查询品牌下所有车系
	 * @param brandCode
	 * @return
	 */
	public static String getGetBrandSeriesUrl(int brandCode) {
		StringBuilder sb = new StringBuilder();
		sb.append(GET_BRAND_SERIES_URL).append("/").append(brandCode).append("/").append("series");
		return sb.toString();
	}
	
	
	/**
	 * 车牌号是否注册
	 * @return
	 * @throws IOException 
	 */
	public static String isVehicleRegister(String licenseCode){
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(VEHICLE_INFO_URL).append("/").append(URLEncoder.encode(licenseCode,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return VEHICLE_INFO_URL;
		}
		return sb.toString();
	}
	
	/**
	 * 修改一条保养记录
	 * @param careId
	 * @return
	 */
	public static String modifyCareRecord(String careId){
		StringBuilder sb = new StringBuilder();
		sb.append(CARE_RECORD).append("/").append(careId);
		return sb.toString();
	}
}
