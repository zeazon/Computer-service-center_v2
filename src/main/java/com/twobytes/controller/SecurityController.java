package com.twobytes.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.twobytes.model.Employee;
import com.twobytes.model.Menu;
import com.twobytes.model.Role;
import com.twobytes.security.form.LoginForm;
import com.twobytes.security.service.SecurityService;
import com.twobytes.util.DevicePage;

@Controller
public class SecurityController {

	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private DevicePage dp;
	
	@Autowired
	private MessageSource messages;
	
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String showLoginScreen(Device device, ModelMap model, HttpServletRequest request)
	{
		if(null != request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "firstScreen", "firstScreen_m");
		}
		
		LoginForm form = new LoginForm();
		model.addAttribute(form);
		return dp.page(device, "loginScreen", "loginScreen_m");
		
	}
	
	
	@RequestMapping(value="/login", method = RequestMethod.POST)
	public String onViewSubmit(@ModelAttribute("loginForm") LoginForm form, HttpServletRequest request, ModelMap model, Device device) {
		String errMsg = "";
		Employee emp = new Employee();
		try{
			// If cannot login securityService.login will return null
			emp = securityService.login(form.getLogin(), form.getPassword());
		}catch(Exception e){
			e.printStackTrace();
			errMsg = e.getMessage();
			model.addAttribute("errMsg", errMsg);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		
		if(null == emp){
			// Can't login
			errMsg = this.messages.getMessage("error.login", null, new Locale("th", "TH"));
			model.addAttribute("errMsg", errMsg);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}else{
			// Can login
			request.getSession().setAttribute("UserLogin", emp);
			request.getSession().setAttribute("UserName", emp.getLogin());
			StringBuffer menuStr = new StringBuffer();
			Role role = emp.getRoleID();
			try{
				List<Menu> mainMenu = securityService.selectMainMenu(role.getRoleID());
				
				// open ul 's main
				menuStr.append("<ul id=\"nav\">");
				for(Menu menu: mainMenu){
	//			for(int i=0; i<mainMenu.size(); i++){
	//				Menu menu = mainMenu.get(i);
					// open li 's main
					menuStr.append("<li class=\"top\">");
					/*
					 * get submenu lv 1
					 */
					List<Menu> subMenu1 = securityService.selectSubMenu(role.getRoleID(), menu.getMenuID());

					/*
					 * If menu don't have sub menu, this menu can click
					 */
					if(subMenu1.size()==0){
						menuStr.append("<a href=\""+request.getContextPath()+menu.getLink()+"\" class=\"top_link\"><span>"+menu.getName()+"</span></a>");
					}else{
						menuStr.append("<a href=\""+request.getContextPath()+menu.getLink()+"\" class=\"top_link\" onclick='return false'><span>"+menu.getName()+"</span></a>");
					}
					
					if(subMenu1.size()>0){
						// open ul 's sub menu 1
						menuStr.append("<ul class=\"sub\">");
						for(Menu subMenu1M:subMenu1){
	//					for(int j=0; j<subMenu1.size(); j++){
	//						Menu subMenu1M = subMenu1.get(j);
							// open li 's sub menu 1
							menuStr.append("<li>");
							menuStr.append("<a href=\""+request.getContextPath()+subMenu1M.getLink()+"\">"+subMenu1M.getName()+"</a>");
							// close li 's sub menu 1
							menuStr.append("</li>");
						}
						// close ul 's sub menu 1
						menuStr.append("</ul>");
					}
					// close li 's main
					menuStr.append("</li>");
				}
				// close ul 's main
				menuStr.append("</ul>");
			}catch(Exception e){
				e.printStackTrace();
			}
//			mav.addObject("menuStr", menuStr);
//			mav.setViewName("firstScreen");
//			model.addAttribute("menuStr", menuStr);
			request.getSession().setAttribute("menuStr", menuStr);
			return dp.page(device, "firstScreen", "firstScreen_m");
		}
//		return mav;
	}
	
	@RequestMapping(value = "/logout")
	public String logout(HttpServletRequest request, ModelMap model, Device device) {
		request.getSession().removeAttribute("UserLogin");
		LoginForm loginForm = new LoginForm();
		model.addAttribute(loginForm);
		return dp.page(device, "loginScreen", "loginScreen_m");
	}
}
