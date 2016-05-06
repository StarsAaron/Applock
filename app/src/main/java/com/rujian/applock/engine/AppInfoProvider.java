package com.rujian.applock.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.rujian.applock.bean.AppInfoBean;


public class AppInfoProvider {

	/**
	 * 获取手机里面所有的安装的应用程序信息
	 * @param context
	 * @return
	 */
	public static List<AppInfoBean> getAppInfos(Context context){
		//得到包管理器
		PackageManager pm = context.getPackageManager();
		List<AppInfoBean> AppInfoBeans = new ArrayList<AppInfoBean>();
		List<PackageInfo> packinfos = pm.getInstalledPackages(0);
		for(PackageInfo packinfo : packinfos){
			AppInfoBean appInfoBean = new AppInfoBean();
			appInfoBean.packname = packinfo.packageName;
			appInfoBean.icon = packinfo.applicationInfo.loadIcon(pm);
			appInfoBean.name = packinfo.applicationInfo.loadLabel(pm).toString()+packinfo.applicationInfo.uid;
			AppInfoBeans.add(appInfoBean);
		}
		return AppInfoBeans;
	}
}
