package com.thinkgem.jeesite.modules.ws.utils;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.ws.common.WsConfig;
import com.thinkgem.jeesite.modules.ws.entity.Oauth2Token;
import com.thinkgem.jeesite.modules.wx.core.exception.WexinReqException;
import com.thinkgem.jeesite.modules.wx.wxbase.wxtoken.JwTokenAPI;
import com.thinkgem.jeesite.modules.wx.wxmenu.JwMenuAPI;

import net.sf.json.JSONObject;

public class WsUtils {
	
	private static Logger logger = LoggerFactory.getLogger(WsUtils.class);

	public final static String oauth_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

	public final static String code_token_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=URL&response_type=code&scope=snsapi_base&state=jeetc#wechat_redirect";

	public static String getOpenId(HttpServletRequest request,HttpServletResponse response) {
		String openId = "";
		try {
			openId = (String) UserUtils.getSession().getAttribute("openid");
			if(StringUtils.isNotEmpty(openId)){
				return openId;
			}
			/**
			 * 如果用户的openid不存在，则先获取code
			 */
			String code = request.getParameter("code");
			if (StringUtils.isEmpty(code)||code.equals("null")) {
				String url = request.getRequestURL().toString();
				String requestUrl = JwMenuAPI.urlEncode(url,WsConfig.accountappid);
				response.sendRedirect(requestUrl);
			}
			/**
			 * 根据code,重新请求获取openid
			 */
			if (StringUtils.isNotEmpty(code)) {
				Oauth2Token ot=getOauth2Token(WsConfig.accountappid, WsConfig.accountappsecret, code);
				if(ot!=null){
					openId = ot.getOpenId();
					UserUtils.getSession().setAttribute("openId", openId);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return openId;
	}

	public static Oauth2Token getOauth2Token(String appId, String appScecret, String code) throws Exception {
		Oauth2Token oauth2Token = null;
		JSONObject jsonObject = null;
		String requestUrl = oauth_token_url.replace("APPID", appId);
		requestUrl = requestUrl.replace("SECRET", appScecret);
		requestUrl = requestUrl.replace("CODE", code);
		try {
			jsonObject = HttpClientUtil.httpRequest(requestUrl, "POST", null);
			oauth2Token = new Oauth2Token();
			if(jsonObject.containsKey("openid")){
				oauth2Token.setOpenId(jsonObject.getString("openid"));
			}		
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
		}
		return oauth2Token;

	}
	
	public static String getAccessToken(){
		try {
			String token=(String) CacheUtils.get("access_token");
			Date nowDate=new Date();
			if(StringUtils.isEmpty(token)){
				token=JwTokenAPI.getAccessToken(WsConfig.accountappid, WsConfig.accountappsecret);
				CacheUtils.put("access_token", token);
				CacheUtils.put("access_time", nowDate.getTime());
			}else{
				long lastTime=(long) CacheUtils.get("access_time");
				if((nowDate.getTime()-lastTime)/1000>7000){
					token=JwTokenAPI.getAccessToken(WsConfig.accountappid, WsConfig.accountappsecret);
					CacheUtils.put("access_token", token);
					CacheUtils.put("access_time", nowDate.getTime());
				}
			}
		} catch (WexinReqException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		WsUtils.getAccessToken();
	}



}
