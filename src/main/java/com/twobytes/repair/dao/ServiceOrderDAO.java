package com.twobytes.repair.dao;

import java.util.List;
import java.util.Map;

import com.twobytes.model.ServiceOrder;
import com.twobytes.report.form.NumRepairByEmpReportForm;
import com.twobytes.report.form.NumRepairReportForm;
import com.twobytes.report.form.SumAmountReportForm;

public interface ServiceOrderDAO {
	public boolean save(ServiceOrder serviceOrder) throws Exception;
	public ServiceOrder selectByID(String serviceOrderID) throws Exception;
	public List<ServiceOrder> selectByCriteria(String name, String startDate, String endDate, String type, String serialNo, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public Map<String, Object> selectByCriteria(String name, String startDate, String endDate, String type, String serialNo, String empID, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public Map<String, Object> selectByCriteria2(String serviceOrderID, String name, String mobileTel, String startDate, String endDate, String type, String serialNo, String empID, String issuePartCode, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public boolean edit(ServiceOrder serviceOrder) throws Exception;
	public boolean delete(ServiceOrder serviceOrder, Integer employeeID) throws Exception;
	
	public List<ServiceOrder> selectNewSOByCriteria(String name, String date, String type, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public List<ServiceOrder> selectSOForCloseByCriteria(String name, String startDate, String endDate, String type, String serialNo, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public List<ServiceOrder> selectSOForCloseByCriteria(Integer employeeID, String name, String startDate, String endDate, String type, String serialNo, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public List<ServiceOrder> selectSOForCloseByCriteria(String name, String startDate, String endDate, String type, String serialNo, String empFixID, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public List<ServiceOrder> selectSOForCloseByCriteriaIssuePart(String name, String startDate, String endDate, String type, String serialNo, String empFixID, String issuePartCode, Integer rows, Integer page, String orderBy,	String orderType) throws Exception;
	public List<ServiceOrder> selectFixedSOByCriteria(String name, String date, String type, String issuePartCode, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	
	public List<ServiceOrder> getRepairReport(String startDate, String endDate, String status) throws Exception;
	public NumRepairReportForm getNumRepairReport(String date) throws Exception;
	public List<NumRepairByEmpReportForm> getNumRepairByEmpReport(String startDate, String endDate, Integer employeeID) throws Exception;
	public List<SumAmountReportForm> getSumAmountReport(String startDate, String endDate) throws Exception;
}
