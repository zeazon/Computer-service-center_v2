package com.twobytes.master.controller;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twobytes.master.form.TypeForm;
import com.twobytes.master.form.TypeGridData;
import com.twobytes.master.form.TypeSearchForm;
import com.twobytes.master.service.TypeService;
import com.twobytes.model.CustomGenericResponse;
import com.twobytes.model.Employee;
import com.twobytes.model.GridResponse;
import com.twobytes.model.Type;
import com.twobytes.security.form.LoginForm;
import com.twobytes.util.DevicePage;
import com.twobytes.util.StringUtility;

@Controller
public class TypeController {
	
	@Autowired
	private TypeService typeService;
	
	@Autowired
	private StringUtility stringUtility;
	
	@Autowired
	private DevicePage dp;
	
	@Autowired
	private MessageSource messages;
	
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	private String VIEWNAME_SEARCH = "type.search";
	private String VIEWNAME_FORM = "type.form";
	private String VIEWNAME_M_SEARCH = "type_m.search";
	private String VIEWNAME_M_FORM = "type_m.form";
	
	@RequestMapping(value = "/type")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		TypeSearchForm searchForm = new TypeSearchForm();
		model.addAttribute("searchForm", searchForm);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	// Change calling AJAX
	// - Change return GridResponse to String
	// - Use ObjectMapper to convert GridResponse object to text and return it.
	@RequestMapping(value="/searchType")
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
		List<Type> typeList = new ArrayList<Type>(); 
			try {
				typeList = typeService.selectByCriteria(name, rows, page, sidx, sord);
			} catch (Exception e) {
				e.printStackTrace();
			}
		GridResponse response = new GridResponse();
		
		List<TypeGridData> rowsList = new ArrayList<TypeGridData>();
		
		Integer total_pages = 0;
		if(typeList.size() > 0){
			int endData = 0;
			if(typeList.size() < (rows*page)){
				endData = typeList.size();
			}else{
				endData = (rows*page);
			}
			// If data is thai change encode from UTF-8 to ISO-8859-1 for display thai in grid
			for(int i=(rows*page - rows); i<endData; i++){
				Type type = typeList.get(i);
				TypeGridData gridData = new TypeGridData();
//				gridData.setTypeID(type.getTypeID().toString());
				gridData.setTypeID(stringUtility.convertUTF8ToISO_8859_1(type.getTypeID()));
//				gridData.setName(type.getName());
				gridData.setName(stringUtility.convertUTF8ToISO_8859_1(type.getName()));
				rowsList.add(gridData);
			}
			total_pages = new Double(Math.ceil(((double)typeList.size()/(double)rows))).intValue();
		}
		
		if (page > total_pages) page=total_pages;
		response.setPage(page.toString());
		response.setRecords(String.valueOf(typeList.size()));
		response.setTotal(total_pages.toString());
		response.setRows(rowsList);
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
	@RequestMapping(value = "/type", params = "do=preAdd")
	public String preAdd(ModelMap model, HttpServletRequest request, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		TypeForm form = new TypeForm();
		model.addAttribute("form", form);
		model.addAttribute("mode", "add");
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	@RequestMapping(value = "/type", params = "do=save")
	public String doSave(@ModelAttribute("form") TypeForm form, HttpServletRequest request, ModelMap model,@RequestParam String mode, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Date now = new Date();
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		Type type = new Type();
		String msg = "";
//		if(null != form.getTypeID()){
//		if (!form.getTypeID().equals("")) {
		if(mode.equals("edit")){
			// update
			try {
				type = typeService.selectByID(form.getTypeID());
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errMsg", e.getMessage());
				return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
			}
			msg = this.messages.getMessage("msg.updateComplete", null, new Locale("th", "TH"));
		}else{
			// add
			type.setCreatedBy(user.getEmployeeID());
			type.setCreatedDate(now);
			msg = this.messages.getMessage("msg.addComplete", null, new Locale("th", "TH"));
		}
		type.setTypeID(form.getTypeID());
		type.setName(form.getName());
		
		type.setUpdatedBy(user.getEmployeeID());
		type.setUpdatedDate(now);
		boolean canSave;
		try{
			canSave = typeService.save(type);
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("errMsg", e.getMessage());
			model.addAttribute("mode", mode);
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		if(!canSave){
			model.addAttribute("errMsg", this.messages.getMessage("error.cannotSave", null, new Locale("th", "TH")));
			model.addAttribute("mode", mode);
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		model.addAttribute("msg", msg);
		TypeSearchForm searchForm = new TypeSearchForm();
		model.addAttribute("searchForm", searchForm);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	@RequestMapping(value = "/type", params = "do=preEdit")
	public String preEdit(@RequestParam(value="typeID") String typeID, ModelMap model, HttpServletRequest request, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Type type = new Type();
			try {
				type = typeService.selectByID(typeID);
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errMsg", e.getMessage());
				TypeSearchForm searchForm = new TypeSearchForm();
				model.addAttribute("searchForm", searchForm);
				return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
			}
		TypeForm form = new TypeForm();
		form.setTypeID(type.getTypeID());
		form.setName(type.getName());
		model.addAttribute("form", form);
		model.addAttribute("mode", "edit");
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	// Change calling AJAX
	// - Change return CustomGenericResponse to String
	// - Use ObjectMapper to convert CustomGenericResponse object to text and return it.
	@RequestMapping(value = "/type", params = "do=delete")
	public @ResponseBody String delete(HttpServletRequest request) throws JsonProcessingException{
		String reqID = String.valueOf(request.getParameter("typeID"));
		// Because default Tomcat URI encoding is iso-8859-1 so it must encode back to tis620
		
		// Because since tomcat 8 URI encoding is UTF-8 and set jsp charset to UTF-8 so it don't encode back to tis620
/*		try{
			if(null != reqID){
				reqID = new String(reqID.getBytes("iso-8859-1"), "tis620");	
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
*/
		
		CustomGenericResponse response = new CustomGenericResponse();
		response.setSuccess(true);
		boolean success = false;
		try{
			success = typeService.delete(reqID);
//			response.setMessage("Action successful!");
		}catch(Exception e){
			e.printStackTrace();
			response.setSuccess(false);
//			response.setMessage("Action failure!");
		}
		
		if(!success){
			response.setSuccess(false);
			response.setMessage(this.messages.getMessage("error.cannotDelete", null, new Locale("th", "TH")));
		}else{
			response.setMessage(this.messages.getMessage("msg.deleteSuccess", null, new Locale("th", "TH")));
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
}
