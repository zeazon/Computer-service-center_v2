package com.twobytes.report.controller;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.twobytes.master.service.EmployeeService;
import com.twobytes.model.Employee;
import com.twobytes.repair.service.ServiceOrderService;
import com.twobytes.report.form.NumRepairByEmpReportForm;
import com.twobytes.report.form.NumRepairByEmpReportSearchForm;
import com.twobytes.security.form.LoginForm;
import com.twobytes.util.DevicePage;

@Controller
public class NumRepairByEmpReportController {

	@Autowired
	private ServiceOrderService soService;
	
	@Autowired
	private EmployeeService empService;
	
	@Autowired
	private DevicePage dp;
	
	private String VIEWNAME_SEARCH = "numRepairByEmpReport.search";
	private String VIEWNAME_M_SEARCH = "numRepairByEmpReport_m.search";
	
	@RequestMapping(value = "/numRepairByEmpReport")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if (null == request.getSession().getAttribute("UserLogin")) {
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		
		NumRepairByEmpReportSearchForm searchForm = new NumRepairByEmpReportSearchForm();
		model.addAttribute("searchForm", searchForm);
		
		List<Employee> empList = empService.getAll();
		model.addAttribute("employeeList", empList);
		
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	@RequestMapping(value = "/numRepairByEmpReport", params = "do=printReport")
	public String doPrintReport(@ModelAttribute NumRepairByEmpReportSearchForm form,
			HttpServletRequest request, ModelMap model) throws ParseException {
		String[] datePart;
		String searchStartDate = null;
		String searchEndDate = null;
		Integer employeeID = 0;
		String employee = "";
		if (null != form.getStartDate() && !form.getStartDate().equals("")) {
			datePart = form.getStartDate().split("/");
			searchStartDate = datePart[2] + "-" + datePart[1] + "-"
					+ datePart[0];
		}
		if (null != form.getEndDate() && !form.getEndDate().equals("")) {
			datePart = form.getEndDate().split("/");
			searchEndDate = datePart[2] + "-" + datePart[1] + "-"
					+ datePart[0];
		}
		
		if(null != form.getEmployeeID() && !form.getEmployeeID().equals("")){
			employeeID = Integer.parseInt(form.getEmployeeID());
			Employee emp = new Employee();
			try {
				emp = empService.selectByID(employeeID);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			employee = emp.getName()+" "+emp.getSurname();
		}else{
			employeeID = null;
			employee = "All";
		}
		
		List<NumRepairByEmpReportForm> reportResultList = soService.getNumRepairByEmpReport(searchStartDate, searchEndDate, employeeID);
		
		model.addAttribute("reportResultList", reportResultList);
		model.addAttribute("startDate",form.getStartDate());
		model.addAttribute("endDate",form.getEndDate());
		model.addAttribute("employee", employee);
		return "numRepairByEmpReportDoc";
	}
}
