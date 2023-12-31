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

import com.twobytes.report.form.CountCustomerRegionReportForm;
import com.twobytes.report.form.CountCustomerRegionReportSearchForm;
import com.twobytes.report.service.ReportService;
import com.twobytes.security.form.LoginForm;
import com.twobytes.util.DevicePage;

@Controller
public class CountCustomerRegionReportController {
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private DevicePage dp;
	
	private String VIEWNAME_SEARCH = "countCustomerRegionReport.search";
	private String VIEWNAME_M_SEARCH = "countCustomerRegionReport_m.search";
	
	@RequestMapping(value = "/countCustomerRegionReport")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if (null == request.getSession().getAttribute("UserLogin")) {
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		
		CountCustomerRegionReportSearchForm searchForm = new CountCustomerRegionReportSearchForm();
		searchForm.setNumRow(10);
		model.addAttribute("searchForm", searchForm);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	@RequestMapping(value = "/countCustomerRegionReport", params = "do=printReport")
	public String doPrintReport(@ModelAttribute CountCustomerRegionReportSearchForm form,
			HttpServletRequest request, ModelMap model) throws ParseException {
		String[] datePart;
		String searchStartDate = null;
		String searchEndDate = null;
		
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
		
		List<CountCustomerRegionReportForm> reportResultList = reportService.countCustomerRegion(searchStartDate, searchEndDate, form.getNumRow());
		
		model.addAttribute("reportResultList", reportResultList);
		model.addAttribute("startDate",form.getStartDate());
		model.addAttribute("endDate",form.getEndDate());
		model.addAttribute("numRow", form.getNumRow());
		return "countCustomerRegionReportDoc";
	}
}