package com.twobytes.controller;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.twobytes.master.service.EmployeeService;
import com.twobytes.model.Employee;
import com.twobytes.security.form.ChangePasswordForm;
import com.twobytes.security.form.LoginForm;
import com.twobytes.security.service.SecurityService;
import com.twobytes.util.DevicePage;

@Controller
public class ChangePasswordController {
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private EmployeeService empService;
	
	@Autowired
	private DevicePage dp;
	
	@Autowired
	private MessageSource messages;
	
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	private String VIEWNAME_FORM = "changePassword.form";
	private String VIEWNAME_M_FORM = "changePassword_m.form";
	
	@RequestMapping(value = "/changePassword")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		ChangePasswordForm form = new ChangePasswordForm();
		model.addAttribute("form", form);
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	@RequestMapping(value = "/changePassword", params = "do=save")
	public String doSave(@ModelAttribute("form") ChangePasswordForm form, HttpServletRequest request, ModelMap model, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Date now = new Date();
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		String errMsg = "";
		String msg = "";
		Employee emp = new Employee();
		try{
			emp = securityService.login(user.getLogin(), form.getOldPassword());
		}catch(Exception e){
			errMsg = this.messages.getMessage("error.passwordMismatch", null, new Locale("th", "TH"));
			model.addAttribute("errMsg", errMsg);
			form = new ChangePasswordForm();
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		
		if(emp != null){
			emp.setPassword(form.getNewPassword());
			emp.setUpdatedBy(user.getEmployeeID());
			emp.setUpdatedDate(now);
			
			try{
				empService.save(emp);
			}catch(Exception e){
				errMsg = e.getMessage();
				model.addAttribute("errMsg", errMsg);
				form = new ChangePasswordForm();
				return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
			}
			
			msg = this.messages.getMessage("msg.updateComplete", null, new Locale("th", "TH"));
		}else{
			errMsg = this.messages.getMessage("error.passwordMismatch", null, new Locale("th", "TH"));
			model.addAttribute("errMsg", errMsg);
			form = new ChangePasswordForm();
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		form = new ChangePasswordForm();
		model.addAttribute("form", form);
		model.addAttribute("msg", msg);
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
}
