package benciao.android.supportmonitor.http;

import java.io.InputStream;

import org.apache.http.impl.client.DefaultHttpClient;

import benciao.android.supportmonitor.domain.DomainDataWrapper;
import benciao.android.supportmonitor.domain.User.SupportStatus;

public class RequestWrapper
{
	private static String				URL_BASE	= "http://benciao-support-team-tracker.appspot.com";
	private static DefaultHttpClient	httpClient	= ConnectionManager
															.getClient();

	public static boolean isMobilePhoneRegistered(String mobilePhone)
	{
		String url = URL_BASE + "/isAccessible?mobilePhone=" + mobilePhone;

		String response = ResponseUtil.doGetWithResponse(url, httpClient);

		boolean isMobilePhoneRegistered = XmlParser.parseRegistration(response);
		return isMobilePhoneRegistered;
	}

	public static DomainDataWrapper getStatus()
	{
		String url = URL_BASE + "/status?action=all";

		InputStream response = ResponseUtil
				.doGetWithIsResponse(url, httpClient);
		DomainDataWrapper domainDataWrapper = XmlParser.parseStatus(response);

		return domainDataWrapper;
	}

	public static boolean changeStatus(String userId,
			String supportStatusDescription)
	{
		String url = URL_BASE
				+ "/status?action=updateUserStatus&user="
				+ userId
				+ "&newStatus="
				+ SupportStatus.getSupportStatusForDescription(
						supportStatusDescription).getId();

		String response = ResponseUtil
				.doGetWithResponse(url, httpClient);

		return XmlParser.parseBooleanResult(response);
	}

}
