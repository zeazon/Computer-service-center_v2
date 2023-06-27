package com.twobytes.master.controller;

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
import com.twobytes.master.form.TransportCompanyForm;
import com.twobytes.master.form.TransportCompanyGridData;
import com.twobytes.master.form.TransportCompanySearchForm;
import com.twobytes.master.service.TransportCompanyService;
import com.twobytes.model.CustomGenericResponse;
import com.twobytes.model.Employee;
import com.twobytes.model.GridResponse;
import com.twobytes.model.TransportCompany;
import com.twobytes.security.form.LoginForm;
import com.twobytes.util.DevicePage;
import com.twobytes.util.StringUtility;

@Controller
public class TransportCompanyController {
	
	@Autowired
	private TransportCompanyService tcService;
	
	@Autowired
	private StringUtility stringUtility;
	
	@Autowired
	private DevicePage dp;
	
	@Autowired
	private MessageSource messages;
	
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	private String VIEWNAME_SEARCH = "transportCompany.search";
	private String VIEWNAME_FORM = "transportCompany.form";
	private String VIEWNAME_M_SEARCH = "transportCompany_m.search";
	private String VIEWNAME_M_FORM = "transportCompany_m.form";
	
	@RequestMapping(value = "/transportCompany")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		TransportCompanySearchForm searchForm = new TransportCompanySearchForm();
		model.addAttribute("searchForm", searchForm);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	// Change calling AJAX
	// - Change return GridResponse to String
	// - Use ObjectMapper to convert GridResponse object to text and return it.
	@RequestMapping(value="/searchTc")
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
		List<TransportCompany> tcList = new ArrayList<TransportCompany>(); 
		try {
			tcList = tcService.selectByCriteria(name, rows, page, sidx, sord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GridResponse response = new GridResponse();
		
		List<TransportCompanyGridData> rowsList = new ArrayList<TransportCompanyGridData>();
		
		Integer total_pages = 0;
		if(tcList.size() > 0){
			int endData = 0;
			if(tcList.size() < (rows*page)){
				endData = tcList.size();
			}else{
				endData = (rows*page);
			}
			// If data is thai change encode from UTF-8 to ISO-8859-1 for display thai in grid
			for(int i=(rows*page - rows); i<endData; i++){
				TransportCompany tc = tcList.get(i);
				TransportCompanyGridData gridData = new TransportCompanyGridData();
				gridData.setTcID(tc.getTransportCompanyID().toString());
//				gridData.setName(tc.getName());
				gridData.setName(stringUtility.convertUTF8ToISO_8859_1(tc.getName()));
				rowsList.add(gridData);
			}
			total_pages = new Double(Math.ceil(((double)tcList.size()/(double)rows))).intValue();
		}
		
		if (page > total_pages) page=total_pages;
		response.setPage(page.toString());
		response.setRecords(String.valueOf(tcList.size()));
		response.setTotal(total_pages.toString());
		response.setRows(rowsList);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
	@RequestMapping(value = "/transportCompany", params = "do=preAdd")
	public String preAdd(ModelMap model, HttpServletRequest request, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		TransportCompanyForm form = new TransportCompanyForm();
		model.addAttribute("form", form);
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	@RequestMapping(value = "/transportCompany", params = "do=save")
	public String doSave(@ModelAttribute("form") TransportCompanyForm form, HttpServletRequest request, ModelMap model, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Date now = new Date();
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		TransportCompany tc = new TransportCompany();
		String msg = "";
		if(null != form.getTcID()){
			// update
			try {
				tc = tcService.selectByID(form.getTcID());
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errMsg", e.getMessage());
				return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
			}
			msg = this.messages.getMessage("msg.updateComplete", null, new Locale("th", "TH"));
		}else{
			// add
			tc.setCreatedBy(user.getEmployeeID());
			tc.setCreatedDate(now);
			msg = this.messages.getMessage("msg.addComplete", null, new Locale("th", "TH"));
		}
		tc.setName(form.getName());
		
		tc.setUpdatedBy(user.getEmployeeID());
		tc.setUpdatedDate(now);
		boolean canSave;
		try{
			canSave = tcService.save(tc);
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("errMsg", e.getMessage());
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		if(!canSave){
			model.addAttribute("errMsg", this.messages.getMessage("error.cannotSave", null, new Locale("th", "TH")));
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		model.addAttribute("msg", msg);
		TransportCompanySearchForm searchForm = new TransportCompanySearchForm();
		model.addAttribute("searchForm", searchForm);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	@RequestMapping(value = "/transportCompany", params = "do=preEdit")
	public String preEdit(@RequestParam(value="tcID") Integer tcID, ModelMap model, HttpServletRequest request, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		TransportCompany tc = new TransportCompany();
		try {
			tc = tcService.selectByID(tcID);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errMsg", e.getMessage());
			TransportCompanySearchForm searchForm = new TransportCompanySearchForm();
			model.addAttribute("searchForm", searchForm);
			return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
		}
		TransportCompanyForm form = new TransportCompanyForm();
		form.setTcID(tc.getTransportCompanyID());
		form.setName(tc.getName());
		model.addAttribute("form", form);
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	// Change calling AJAX
	// - Change return CustomGenericResponse to String
	// - Use ObjectMapper to convert CustomGenericResponse object to text and return it.
	@RequestMapping(value = "/transportCompany", params = "do=delete")
	public @ResponseBody String delete(HttpServletRequest request) throws JsonProcessingException{
		CustomGenericResponse response = new CustomGenericResponse();
		response.setSuccess(true);
		try{
			tcService.delete(Integer.valueOf(request.getParameter("tcID")));
			response.setMessage(this.messages.getMessage("msg.deleteSuccess", null, new Locale("th", "TH")));
		}catch(Exception e){
			response.setSuccess(false);
			response.setMessage(this.messages.getMessage("error.cannotDelete", null, new Locale("th", "TH")));
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
}
