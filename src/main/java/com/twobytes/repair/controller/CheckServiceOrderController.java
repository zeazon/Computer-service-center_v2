package com.twobytes.repair.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.twobytes.repair.form.CheckServiceOrderSearchForm;
import com.twobytes.util.DevicePage;

@Controller
public class CheckServiceOrderController {

	@Autowired
	private DevicePage dp;
	
	private String VIEWNAME_SEARCH = "checkServiceOrder.search";
	private String VIEWNAME_M_SEARCH = "checkServiceOrder_m.search";
	
	@RequestMapping(value = "/checkServiceOrder")
	public String view(ModelMap model, HttpServletRequest request, Device device) {

		CheckServiceOrderSearchForm searchForm = new CheckServiceOrderSearchForm();
		model.addAttribute("searchForm", searchForm);
		
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
}
