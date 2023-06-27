package com.twobytes.master.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twobytes.master.form.RoleForm;
import com.twobytes.master.form.RoleGridData;
import com.twobytes.master.form.RoleSearchForm;
import com.twobytes.master.service.MenuService;
import com.twobytes.model.CustomGenericResponse;
import com.twobytes.model.Employee;
import com.twobytes.model.GridResponse;
import com.twobytes.model.Menu;
import com.twobytes.model.Role;
import com.twobytes.security.form.LoginForm;
import com.twobytes.security.service.RoleService;
import com.twobytes.security.service.SecurityService;
import com.twobytes.util.DevicePage;
import com.twobytes.util.StringUtility;

@Controller
public class RoleController {

	private static final Logger logger = LoggerFactory
	.getLogger(BrandController.class);
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private StringUtility stringUtility;
	
	@Autowired
	private DevicePage dp;
	
	@Autowired
	private MessageSource messages;
	
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	private String VIEWNAME_SEARCH = "role.search";
	private String VIEWNAME_FORM = "role.form";
	private String VIEWNAME_M_SEARCH = "role_m.search";
	private String VIEWNAME_M_FORM = "role_m.form";
	
	@RequestMapping(value = "/role")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		RoleSearchForm searchForm = new RoleSearchForm();
		model.addAttribute("searchForm", searchForm);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	// Change calling AJAX
	// - Change return GridResponse to String
	// - Use ObjectMapper to convert GridResponse object to text and return it.
	@RequestMapping(value="/searchRole")
	public @ResponseBody String getData(@RequestParam(value="name", required=false) String name, @RequestParam("rows") Integer rows, @RequestParam("page") Integer page, @RequestParam("sidx") String sidx, @RequestParam("sord") String sord) throws JsonProcessingException{
		// Because default Tomcat URI encoding is iso-8859-1 so it must encode back to tis620

		// Because since tomcat 8 URI encoding is UTF-8 and set jsp charset to UTF-8 so it don't encode back to tis620
/*		try{
			if(null != name){
				name = new String(name.getBytes("iso-8859-1"), "tis620");	
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
*/
		List<Role> roleList = new ArrayList<Role>();
		try{
			roleList = roleService.selectByCriteria(name, rows, page, sidx, sord);
		}catch(Exception e){
			e.printStackTrace();
		}
		GridResponse response = new GridResponse();
		
		List<RoleGridData> rowsList = new ArrayList<RoleGridData>();
		
		Integer total_pages = 0;
		if(roleList.size() > 0){
			int endData = 0;
			if(roleList.size() < (rows*page)){
				endData = roleList.size();
			}else{
				endData = (rows*page);
			}
			// If data is thai change encode from UTF-8 to ISO-8859-1 for display thai in grid
			for(int i=(rows*page - rows); i<endData; i++){
				Role role = roleList.get(i);
				RoleGridData gridData = new RoleGridData();
				gridData.setRoleID(role.getRoleID().toString());
//				gridData.setName(role.getName());
				gridData.setName(stringUtility.convertUTF8ToISO_8859_1(role.getName()));
				rowsList.add(gridData);
			}
			total_pages = new Double(Math.ceil(((double)roleList.size()/(double)rows))).intValue();
		}
		
		if (page > total_pages) page=total_pages;
		response.setPage(page.toString());
		response.setRecords(String.valueOf(roleList.size()));
		response.setTotal(total_pages.toString());
		response.setRows(rowsList);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
	@RequestMapping(value = "/role", params = "do=preAdd")
	public String preAdd(ModelMap model, HttpServletRequest request, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		RoleForm form = new RoleForm();
		String treeMenu = getTreeMenu();
		model.addAttribute("form", form);
		model.addAttribute("treeMenu", treeMenu);
		model.addAttribute("mode", "add");
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	@RequestMapping(value = "/role", params = "do=save")
	public String doSave(@ModelAttribute("form") RoleForm form, HttpServletRequest request, ModelMap model, @RequestParam String mode, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Date now = new Date();
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		Role role = new Role();
		String msg = "";
		if(null != form.getRoleID()){
			// update
			role = roleService.getRole(Integer.valueOf(form.getRoleID()));
			msg = this.messages.getMessage("msg.updateComplete", null, new Locale("th", "TH"));
		}else{
			// add
//			role.setRoleID(Integer.valueOf(form.getRoleID()));
		}
		role.setName(form.getName());
		Set<Menu> menuList = new HashSet<Menu>();
		if(form.getMenu().length > 0){
			for(int i=0; i<form.getMenu().length; i++){
				Menu menu = menuService.selectByID(Integer.valueOf(form.getMenu()[i]));
				menuList.add(menu);
			}
		}
		role.setMenus(menuList);
		
		// save
		try{
			roleService.save(role);
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("errMsg", e.getMessage());
			String treeMenu = "";
			if(mode.equals("add")){
				treeMenu = getTreeMenu();
			}else if(model.equals("edit")){
				List<Menu> menuList2 = new ArrayList<Menu>();
				menuList2.addAll(role.getMenus());
				treeMenu = getTreeMenu(menuList2);
			}
			model.addAttribute("treeMenu", treeMenu);
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		
		model.addAttribute("msg", msg);
		RoleSearchForm searchForm = new RoleSearchForm();
		model.addAttribute("searchForm", searchForm);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	@RequestMapping(value = "/role", params = "do=preEdit")
	public String preEdit(@RequestParam(value="roleID") Integer roleID, ModelMap model, HttpServletRequest request, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Role role = new Role();
		try{
			role = roleService.getRole(roleID);
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("errMsg", e.getMessage());
			RoleSearchForm searchForm = new RoleSearchForm();
			model.addAttribute("searchForm", searchForm);
			return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
		}
		RoleForm form = new RoleForm();
		form.setRoleID(role.getRoleID());
		form.setName(role.getName());
		
		List<Menu> menuList = new ArrayList<Menu>();
		menuList.addAll(role.getMenus());
		
		String treeMenu = getTreeMenu(menuList);
		
		model.addAttribute("form", form);
		model.addAttribute("treeMenu", treeMenu);
		model.addAttribute("mode", "edit");
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	// Change calling AJAX
	// - Change return CustomGenericResponse to String
	// - Use ObjectMapper to convert CustomGenericResponse object to text and return it.
	@RequestMapping(value = "/role", params = "do=delete")
	public @ResponseBody String delete(HttpServletRequest request) throws JsonProcessingException{
		CustomGenericResponse response = new CustomGenericResponse();
		response.setSuccess(true);
		try{
			roleService.delete(Integer.valueOf(request.getParameter("roleID")));
			response.setMessage(this.messages.getMessage("msg.deleteSuccess", null, new Locale("th", "TH")));
		}catch(Exception e){
			e.printStackTrace();
			response.setSuccess(false);
			response.setMessage(this.messages.getMessage("error.cannotDelete", null, new Locale("th", "TH")));
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
	private String getTreeMenu(){
		StringBuffer menuStr = new StringBuffer();
		
		List<Menu> mainMenu = securityService.selectMainMenu();
		// open ul 's main
		menuStr.append("<ul id=\"menuTree\" style=\"padding-left:20px\">");
		for(int i=0; i<mainMenu.size(); i++){
			Menu menu = (Menu)mainMenu.get(i);
			// open li 's main
			menuStr.append("<li>");
			menuStr.append("<input type=\"checkbox\" name=\"menu\" value=\""+menu.getMenuID()+"\">&nbsp;"+menu.getName());
			/*
			 * get submenu lv 1
			 */
			List<Menu> subMenu1 = securityService.selectSubMenu(menu.getMenuID());
			if(subMenu1.size()>0){
				// open ul 's sub menu 1
				menuStr.append("<ul style=\"padding-left:20px\">");
				for(int j=0; j<subMenu1.size(); j++){
					Menu subMenu1M = subMenu1.get(j);
					// open li 's sub menu 1
					menuStr.append("<li>");
					menuStr.append("<input type=\"checkbox\" name=\"menu\" value=\""+subMenu1M.getMenuID()+"\">&nbsp;"+subMenu1M.getName());
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
		
		return menuStr.toString();
	}
	
	private String getTreeMenu(List<Menu> roleMenu){
		StringBuffer menuStr = new StringBuffer();
		
		List<Menu> mainMenu = securityService.selectMainMenu();
		boolean checked = false;
		// open ul 's main
		menuStr.append("<ul id=\"menuTree\" style=\"padding-left:20px\">");
		for(int i=0; i<mainMenu.size(); i++){
			Menu menu = (Menu)mainMenu.get(i);
			// open li 's main
			menuStr.append("<li>");
			for(int x=0; x<roleMenu.size(); x++){
				Menu rm = roleMenu.get(x);
				if(menu.getMenuID().equals(rm.getMenuID())) checked = true;
			}
			if(checked){
				menuStr.append("<input type=\"checkbox\" name=\"menu\" value=\""+menu.getMenuID()+"\" checked>&nbsp;"+menu.getName());
			}else{
				menuStr.append("<input type=\"checkbox\" name=\"menu\" value=\""+menu.getMenuID()+"\">&nbsp;"+menu.getName());					
			}
			checked = false;
			/*
			 * get submenu lv 1
			 */
			List<Menu> subMenu1 = securityService.selectSubMenu(menu.getMenuID());
			if(subMenu1.size()>0){
				// open ul 's sub menu 1
				menuStr.append("<ul style=\"padding-left:20px\">");
				for(int j=0; j<subMenu1.size(); j++){
					Menu subMenu1M = subMenu1.get(j);
					// open li 's sub menu 1
					menuStr.append("<li>");
					for(int x=0; x<roleMenu.size(); x++){
						Menu rm = roleMenu.get(x);
						if(subMenu1M.getMenuID().equals(rm.getMenuID())) checked = true;
					}
					if(checked){
						menuStr.append("<input type=\"checkbox\" name=\"menu\" value=\""+subMenu1M.getMenuID()+"\" checked>&nbsp;"+subMenu1M.getName());		
					}else{
						menuStr.append("<input type=\"checkbox\" name=\"menu\" value=\""+subMenu1M.getMenuID()+"\">&nbsp;"+subMenu1M.getName());			
					}
					checked = false;
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
		
		return menuStr.toString();
	}
}
