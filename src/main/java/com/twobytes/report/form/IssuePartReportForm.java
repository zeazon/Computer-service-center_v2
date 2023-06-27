package com.twobytes.report.form;

import java.io.Serializable;
import java.util.Date;

public class IssuePartReportForm implements Serializable {
	
	private static final long serialVersionUID = 3029806576350276178L;

	private String serviceOrderID;
	private String fixEmp_name;
	private Date serviceOrderDate;
	private Date returnDate;
	private Double totalPrice;
	private Integer quantity;
	
	public String getServiceOrderID() {
		return serviceOrderID;
	}
	public void setServiceOrderID(String serviceOrderID) {
		this.serviceOrderID = serviceOrderID;
	}
	public String getFixEmp_name() {
		return fixEmp_name;
	}
	public void setFixEmp_name(String fixEmp_name) {
		this.fixEmp_name = fixEmp_name;
	}
	public Date getServiceOrderDate() {
		return serviceOrderDate;
	}
	public void setServiceOrderDate(Date serviceOrderDate) {
		this.serviceOrderDate = serviceOrderDate;
	}
	public Date getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
}