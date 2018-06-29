package com.rest.api.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class Field {

	private BigInteger id;
	private String fString;
	private short fShort;
	private int fInt;
	private long fLong;
	private float fFloat;
	private double fDouble;
	private BigDecimal fBigDecimal;
	private String fClob;
	private byte[] fBlob;
	private LocalDate fDate;
	private LocalTime fTime;
	private ZonedDateTime fTimestamp;
	
	public Field() {
		super();
	}

	public Field(String fString, short fShort, int fInt, long fLong, float fFloat, double fDouble,
			BigDecimal fBigDecimal, String fClob, byte[] fBlob, LocalDate fDate, LocalTime fTime, ZonedDateTime fTimestamp) {
		super();
		this.fString = fString;
		this.fShort = fShort;
		this.fInt = fInt;
		this.fLong = fLong;
		this.fFloat = fFloat;
		this.fDouble = fDouble;
		this.fBigDecimal = fBigDecimal;
		this.fClob = fClob;
		this.fBlob = fBlob;
		this.fDate = fDate;
		this.fTime = fTime;
		this.fTimestamp = fTimestamp;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getfString() {
		return fString;
	}

	public void setfString(String fString) {
		this.fString = fString;
	}

	public short getfShort() {
		return fShort;
	}

	public void setfShort(short fShort) {
		this.fShort = fShort;
	}

	public int getfInt() {
		return fInt;
	}

	public void setfInt(int fInt) {
		this.fInt = fInt;
	}

	public long getfLong() {
		return fLong;
	}

	public void setfLong(long fLong) {
		this.fLong = fLong;
	}

	public float getfFloat() {
		return fFloat;
	}

	public void setfFloat(float fFloat) {
		this.fFloat = fFloat;
	}

	public double getfDouble() {
		return fDouble;
	}

	public void setfDouble(double fDouble) {
		this.fDouble = fDouble;
	}

	public BigDecimal getfBigDecimal() {
		return fBigDecimal;
	}

	public void setfBigDecimal(BigDecimal fBigDecimal) {
		this.fBigDecimal = fBigDecimal;
	}

	public String getfClob() {
		return fClob;
	}

	public void setfClob(String fClob) {
		this.fClob = fClob;
	}

	public byte[] getfBlob() {
		return fBlob;
	}

	public void setfBlob(byte[] fBlob) {
		this.fBlob = fBlob;
	}

	public LocalDate getfDate() {
		return fDate;
	}

	public void setfDate(LocalDate fDate) {
		this.fDate = fDate;
	}

	public LocalTime getfTime() {
		return fTime;
	}

	public void setfTime(LocalTime fTime) {
		this.fTime = fTime;
	}

	public ZonedDateTime getfTimestamp() {
		return fTimestamp;
	}

	public void setfTimestamp(ZonedDateTime fTimestamp) {
		this.fTimestamp = fTimestamp;
	}
	
}
