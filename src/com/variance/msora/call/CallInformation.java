package com.variance.msora.call;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import android.telephony.CellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.anosym.vjax.annotations.Markup;
import com.anosym.vjax.converter.v3.Converter;
import com.anosym.vjax.converter.v3.impl.CalendarConverter;

public class CallInformation {

	public static class LatLng implements Serializable {

		private static final long serialVersionUID = 134893483L;
		private BigDecimal latitude;
		private BigDecimal longitude;

		public LatLng() {
		}

		public LatLng(double latitude, double longitude) {
			this.latitude = BigDecimal.valueOf(latitude);
			this.longitude = BigDecimal.valueOf(longitude);
		}

		public LatLng(BigDecimal latitude, BigDecimal longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public BigDecimal getLatitude() {
			return latitude;
		}

		public void setLatitude(BigDecimal latitude) {
			this.latitude = latitude;
		}

		public BigDecimal getLongitude() {
			return longitude;
		}

		public void setLongitude(BigDecimal longitude) {
			this.longitude = longitude;
		}

	}

	public static class CellLocationInfo {
		@Markup(name = "gci")
		private int gsmCellId;
		@Markup(name = "glac")
		private int gsmLocationAreaCode;
		@Markup(name = "upscsc")
		private int umtsPrimaryScramblingCodeOfServingCell;

		private CellLocationInfo(int gsmCellId, int gsmLocationAreaCode,
				int umtsPrimaryScramblingCodeOfServingCell) {
			super();
			this.gsmCellId = gsmCellId;
			this.gsmLocationAreaCode = gsmLocationAreaCode;
			this.umtsPrimaryScramblingCodeOfServingCell = umtsPrimaryScramblingCodeOfServingCell;
		}

		private CellLocationInfo() {
			this(-1, -1, -1);
		}

		public int getGsmCellId() {
			return gsmCellId;
		}

		public void setGsmCellId(int gsmCellId) {
			this.gsmCellId = gsmCellId;
		}

		public int getGsmLocationAreaCode() {
			return gsmLocationAreaCode;
		}

		public void setGsmLocationAreaCode(int gsmLocationAreaCode) {
			this.gsmLocationAreaCode = gsmLocationAreaCode;
		}

		public int getUmtsPrimaryScramblingCodeOfServingCell() {
			return umtsPrimaryScramblingCodeOfServingCell;
		}

		public void setUmtsPrimaryScramblingCodeOfServingCell(
				int umtsPrimaryScramblingCodeOfServingCell) {
			this.umtsPrimaryScramblingCodeOfServingCell = umtsPrimaryScramblingCodeOfServingCell;
		}

	}

	public static class CellLocationConverter implements
			Converter<CellLocation, CellLocationInfo> {

		@Override
		public CellLocationInfo convertFrom(CellLocation arg0) {
			if (arg0 instanceof GsmCellLocation) {
				GsmCellLocation cl = (GsmCellLocation) arg0;
				return new CellLocationInfo(cl.getCid(), cl.getLac(), -1);
			}
			return new CellLocationInfo();
		}

		@Override
		public CellLocation convertTo(CellLocationInfo arg0) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private String clientId;
	private String phoneDialed;
	@com.anosym.vjax.annotations.v3.Converter(CalendarConverter.class)
	private Calendar dialedDate;
	@com.anosym.vjax.annotations.v3.Converter(CalendarConverter.class)
	private Calendar endDate;
	@com.anosym.vjax.annotations.v3.Converter(CellLocationConverter.class)
	private CellLocation cellLocation;
	private String caller_msisdn;
	private LatLng coordinate;
	private String imei; // gsm imei cdma meid

	public CallInformation() {
		super();
	}

	public CallInformation(String clientId, String phoneDialed,
			Calendar dialedDate, Calendar endDate, CellLocation cellLocation) {
		this();
		this.clientId = clientId;
		this.phoneDialed = phoneDialed;
		this.dialedDate = dialedDate;
		this.endDate = endDate;
		this.cellLocation = cellLocation;
	}

	public CallInformation(String clientId, String phoneDialed,
			Calendar dialedDate, Calendar endDate, CellLocation cellLocation,
			String caller_msisdn) {
		super();
		this.clientId = clientId;
		this.phoneDialed = phoneDialed;
		this.dialedDate = dialedDate;
		this.endDate = endDate;
		this.cellLocation = cellLocation;
		this.caller_msisdn = caller_msisdn;
	}

	public CallInformation(String clientId, String phoneDialed,
			Calendar dialedDate, Calendar endDate, CellLocation cellLocation,
			String caller_msisdn, String imei) {
		super();
		this.clientId = clientId;
		this.phoneDialed = phoneDialed;
		this.dialedDate = dialedDate;
		this.endDate = endDate;
		this.cellLocation = cellLocation;
		this.caller_msisdn = caller_msisdn;
		this.imei = imei;
	}

	public CallInformation(String clientId, String phoneDialed,
			Calendar dialedDate, Calendar endDate, CellLocation cellLocation,
			String caller_msisdn, BigDecimal latitude, BigDecimal longitude,
			String imei) {
		super();
		this.clientId = clientId;
		this.phoneDialed = phoneDialed;
		this.dialedDate = dialedDate;
		this.endDate = endDate;
		this.cellLocation = cellLocation;
		this.caller_msisdn = caller_msisdn;
		this.coordinate = new LatLng(latitude, longitude);
		this.imei = imei;
	}

	public LatLng getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(LatLng coordinate) {
		this.coordinate = coordinate;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getCaller_msisdn() {
		return caller_msisdn;
	}

	public void setCaller_msisdn(String caller_msisdn) {
		this.caller_msisdn = caller_msisdn;
	}

	public CellLocation getCellLocation() {
		return cellLocation;
	}

	public void setCellLocation(CellLocation cellLocation) {
		this.cellLocation = cellLocation;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPhoneDialed() {
		return phoneDialed;
	}

	public void setPhoneDialed(String phoneDialed) {
		this.phoneDialed = phoneDialed;
	}

	public Calendar getDialedDate() {
		return dialedDate;
	}

	public void setDialedDate(Calendar dialedDate) {
		this.dialedDate = dialedDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

}
