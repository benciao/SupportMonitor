package benciao.android.supportmonitor.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.net.ParseException;
import android.util.Log;

public class ResponseUtil
{
	private static final String	LOG_TAG	= "ResponseUtil";

	public static String doGetWithResponse(String mUrl,
			DefaultHttpClient httpClient)
	{
		String ret = null;
		HttpResponse response = null;

		HttpGet getMethod = new HttpGet(mUrl);
		try
		{
			response = httpClient.execute(getMethod);
			Log.i(LOG_TAG,
					"STATUS CODE: "
							+ String.valueOf(response.getStatusLine()
									.getStatusCode()));
			if (null != response)
			{
				// helper method to convert HTTP response to String
				ret = getResponseBody(response);
			}
		}
		catch (Exception e)
		{
			Log.e("Exception", e.getMessage());
		}
		finally
		{
		}
		return ret;
	}

	public static InputStream doGetWithIsResponse(String mUrl,
			DefaultHttpClient httpClient)
	{
		InputStream ret = null;
		HttpResponse response = null;

		HttpGet getMethod = new HttpGet(mUrl);
		try
		{
			response = httpClient.execute(getMethod);
			Log.i(LOG_TAG,
					"STATUS CODE: "
							+ String.valueOf(response.getStatusLine()
									.getStatusCode()));
			if (null != response)
			{
				// helper method to convert HTTP response to String
				ret = getIsResponseBody(response);
			}
		}
		catch (Exception e)
		{
			Log.e("Exception", e.getMessage());
		}
		finally
		{
		}
		return ret;
	}

	public static String getResponseBody(HttpResponse response)
	{
		String response_text = null;
		HttpEntity entity = null;

		try
		{
			entity = response.getEntity();
			response_text = _getResponseBody(entity);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			if (entity != null)
			{
				try
				{
					entity.consumeContent();
				}
				catch (IOException ex)
				{

				}
			}
		}
		return response_text;
	}

	public static InputStream getIsResponseBody(HttpResponse response)
	{
		InputStream isResponse = null;
		HttpEntity entity = null;

		try
		{
			entity = response.getEntity();
			isResponse = _getIsResponseBody(entity);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			if (entity != null)
			{
				try
				{
					entity.consumeContent();
				}
				catch (IOException ex)
				{

				}
			}
		}
		return isResponse;
	}

	public static String _getResponseBody(final HttpEntity entity)
			throws IOException, ParseException
	{

		if (entity == null)
		{
			throw new IllegalArgumentException("HTTP entity may not be null");
		}

		InputStream instream = entity.getContent();

		if (instream == null)
		{
			return "";
		}

		if (entity.getContentLength() > Integer.MAX_VALUE)
		{
			throw new IllegalArgumentException(

			"HTTP entity too large to be buffered in memory");
		}

		String charset = getContentCharSet(entity);

		if (charset == null)
		{

			charset = HTTP.DEFAULT_CONTENT_CHARSET;

		}

		Reader reader = new InputStreamReader(instream, charset);

		StringBuilder buffer = new StringBuilder();

		try
		{

			char[] tmp = new char[1024];

			int l;

			while ((l = reader.read(tmp)) != -1)
			{

				buffer.append(tmp, 0, l);

			}

		}
		finally
		{

			reader.close();

		}

		return buffer.toString();

	}

	public static InputStream _getIsResponseBody(final HttpEntity entity)
			throws IOException, ParseException
	{

		if (entity == null)
		{
			throw new IllegalArgumentException("HTTP entity may not be null");
		}

		InputStream instream = entity.getContent();

		return instream;
	}

	public static String getContentCharSet(final HttpEntity entity)
			throws ParseException
	{

		if (entity == null)
		{
			throw new IllegalArgumentException("HTTP entity may not be null");
		}

		String charset = null;

		if (entity.getContentType() != null)
		{

			HeaderElement values[] = entity.getContentType().getElements();

			if (values.length > 0)
			{

				NameValuePair param = values[0].getParameterByName("charset");

				if (param != null)
				{

					charset = param.getValue();

				}

			}

		}

		return charset;

	}

}
