package com.variance.msora.response;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

import com.anosym.vjax.annotations.Markup;
import com.anosym.vjax.annotations.v3.GenericMapType;
import com.anosym.vjax.converter.v3.Converter;
import com.variance.msora.util.Utils;

public class HttpResponseData {
	public static class V1ToV2EnumConverter implements
			Converter<HttpResponseStatus, String> {

		@Override
		public String convertFrom(HttpResponseStatus arg0) {
			return arg0.name();
		}

		@SuppressLint("DefaultLocale")
		@Override
		public HttpResponseStatus convertTo(String arg0) {
			return !Utils.isNullOrEmpty(arg0) ? HttpResponseStatus.valueOf(arg0
					.toUpperCase()) : null;
		}

	}

	@com.anosym.vjax.annotations.v3.Converter(V1ToV2EnumConverter.class)
	@Markup(name = "status")
	private HttpResponseStatus responseStatus;
	private String message;
	@GenericMapType(key = String.class, value = String.class, entryMarkup = "extra", keyMarkup = "id", valueMarkup = "value")
	private Map<String, String> extras;

	public HttpResponseData(HttpResponseStatus responseStatus, String message,
			Map<String, String> extras) {
		this.responseStatus = responseStatus;
		this.message = message;
		this.extras = extras;
	}

	public HttpResponseData(HttpResponseStatus responseStatus, String message) {
		this.responseStatus = responseStatus;
		this.message = message;
		this.extras = new HashMap<String, String>();
	}

	public HttpResponseData() {
	}

	public HttpResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(HttpResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getMessage() {
		if (!Utils.isNullStringOrEmpty(message)) {
			message = message.replaceAll("&lt;", "<");
			message = message.replaceAll("&gt;", ">");
		}
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void putExtra(String id, String value) {
		extras.put(id, value);
	}

	public String getExtra(String id) {
		return extras.get(id);
	}

	public boolean getBooleanExtra(String id) {
		String extra = getExtra(id);
		if (!Utils.isNullStringOrEmpty(extra)) {
			return extra.trim().equalsIgnoreCase("true");
		}
		return false;
	}

	public Map<String, String> getExtras() {
		return extras;
	}

	public void setExtras(Map<String, String> extras) {
		this.extras = extras;
	}

	public String toString() {
		return responseStatus.name() + ": " + message;
	}

	public int getIntExtra(String id) {
		String extra = getExtra(id);
		if (!Utils.isNullStringOrEmpty(extra)) {
			return Integer.parseInt(extra);
		}
		return 0;
	}
}