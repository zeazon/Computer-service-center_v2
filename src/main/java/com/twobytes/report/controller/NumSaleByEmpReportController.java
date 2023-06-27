package com.twobytes.report.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.twobytes.report.form.NumSaleByEmpReportForm;
import com.twobytes.report.form.NumSaleByEmpReportSearchForm;
import com.twobytes.report.service.ReportService;
import com.twobytes.security.form.LoginForm;
import com.twobytes.util.DevicePage;

@Controller
public class NumSaleByEmpReportController {

	@Autowired
	private ReportService reportService;
	
	@Autowired
	private DevicePage dp;
	
	private String VIEWNAME_SEARCH = "numSaleByEmpReport.search";
	private String VIEWNAME_M_SEARCH = "numSaleByEmpReport_m.search";
	
	@RequestMapping(value = "/numSaleByEmpReport")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if (null == request.getSession().getAttribute("UserLogin")) {
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		
		Calendar now = Calendar.getInstance(Locale.US);
		Integer year = now.get(Calendar.YEAR);
		
		NumSaleByEmpReportSearchForm searchForm = new NumSaleByEmpReportSearchForm();
		searchForm.setMonth(1);
		searchForm.setYear(year);
		model.addAttribute("searchForm", searchForm);
		
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	@RequestMapping(value = "/numSaleByEmpReport", params = "do=printReport")
	public String doPrintReport(@ModelAttribute NumSaleByEmpReportSearchForm form,
			HttpServletRequest request, ModelMap model) throws ParseException {
		List<NumSaleByEmpReportForm> reportResultList = reportService.numSale(form.getMonth(), form.getYear());
		
		String monthTxt = "";
		Calendar searchMonth = Calendar.getInstance(new Locale("TH"));
		searchMonth.set(Calendar.MONTH, form.getMonth() - 1);
		
		monthTxt = new SimpleDateFormat("MMMM", new Locale("TH")).format(searchMonth.getTime());
		
		model.addAttribute("reportResultList", reportResultList);
		model.addAttribute("month",monthTxt);
		model.addAttribute("year",form.getYear());
		return "numSaleByEmpReportDoc";
	}
}