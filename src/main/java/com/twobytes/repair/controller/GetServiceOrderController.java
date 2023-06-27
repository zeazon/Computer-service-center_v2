package com.twobytes.repair.controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mobile.device.Device;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twobytes.master.service.TypeService;
import com.twobytes.model.CustomGenericResponse;
import com.twobytes.model.Customer;
import com.twobytes.model.Employee;
import com.twobytes.model.GridResponse;
import com.twobytes.model.ServiceOrder;
import com.twobytes.model.Type;
import com.twobytes.repair.form.ServiceOrderForm;
import com.twobytes.repair.form.ServiceOrderGridData;
import com.twobytes.repair.form.ServiceOrderSearchForm;
import com.twobytes.repair.service.ServiceOrderService;
import com.twobytes.security.form.LoginForm;
import com.twobytes.util.DevicePage;
import com.twobytes.util.StringUtility;

@Controller
public class GetServiceOrderController {
	
	@Autowired
	private ServiceOrderService soService;
	
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
	
	private String VIEWNAME_SEARCH = "getServiceOrder.search";
	private String VIEWNAME_FORM = "getServiceOrder.form";
	private String VIEWNAME_M_SEARCH = "getServiceOrder_m.search";
	private String VIEWNAME_M_FORM = "getServiceOrder_m.form";
	
//	private SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("th", "TH"));
	private SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale ( "US" ));
	
	@RequestMapping(value = "/getServiceOrder")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		ServiceOrderSearchForm searchForm = new ServiceOrderSearchForm();
		model.addAttribute("searchForm", searchForm);
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("typeList", typeList);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	
	// Change calling AJAX
	// - Change return GridResponse to String
	// - Use ObjectMapper to convert GridResponse object to text and return it.
	//public @ResponseBody GridResponse getData(@RequestParam(value="name", required=false) String name, @RequestParam(value="date", required=false) String date, @RequestParam(value="type", required=false) String type, @RequestParam("rows") Integer rows, @RequestParam("page") Integer page, @RequestParam("sidx") String sidx, @RequestParam("sord") String sord){
	//@RequestMapping(value="/searchGetServiceOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params="format=json")
	@RequestMapping(value="/searchGetServiceOrder")
	public @ResponseBody String getData(@RequestParam(value="name", required=false) String name, @RequestParam(value="date", required=false) String date, @RequestParam(value="type", required=false) String type, @RequestParam("rows") Integer rows, @RequestParam("page") Integer page, @RequestParam("sidx") String sidx, @RequestParam("sord") String sord) throws JsonProcessingException{
		// Because default Tomcat URI encoding is iso-8859-1 so it must encode back to tis620
		String[] datePart;
		String searchDate = null;
		// Because since tomcat 8 URI encoding is UTF-8 and set jsp charset to UTF-8 so it don't encode back to tis620
/*
		try{
			if(null != name){
				name = new String(name.getBytes("iso-8859-1"), "tis620");	
			}
//			if(null != surname){
//				surname = new String(surname.getBytes("iso-8859-1"), "tis620");	
//			}
			if(null != date && !date.equals("")){
				date = new String(date.getBytes("iso-8859-1"), "tis620");
				datePart = date.split("/");
				// Change year to Christ year
//				Integer year = Integer.parseInt(datePart[2]);				
//				year = year - 543;
				searchDate = datePart[2]+"-"+datePart[1]+"-"+datePart[0];
			}
			if(null != type){
				type = new String(type.getBytes("iso-8859-1"), "tis620");	
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
*/
		//convert date format from d/m/y to y-m-d
		if(null != date && !date.equals("")){
			datePart = date.split("/");
			searchDate = datePart[2]+"-"+datePart[1]+"-"+datePart[0];
		}
		
		List<ServiceOrder> soList = soService.selectNewSOByCriteria(name, searchDate, type, rows, page, sidx, sord);
		GridResponse response = new GridResponse();
		
		List<ServiceOrderGridData> rowsList = new ArrayList<ServiceOrderGridData>();
		
		Integer total_pages = 0;
		if(soList.size() > 0){
			int endData = 0;
			if(soList.size() < (rows*page)){
				endData = soList.size();
			}else{
				endData = (rows*page);
			}
			for(int i=(rows*page - rows); i<endData; i++){
				ServiceOrder so = soList.get(i);
				ServiceOrderGridData gridData = new ServiceOrderGridData();
				gridData.setServiceOrderID(so.getServiceOrderID());
				gridData.setServiceOrderDate(sdfDateTime.format(so.getServiceOrderDate()));
				Customer customer = so.getCustomer();
//				gridData.setName(customer.getName());
				gridData.setName(stringUtility.convertUTF8ToISO_8859_1(customer.getName()));
//				gridData.setSurname(customer.getSurname());
				gridData.setTel(customer.getTel());
				gridData.setMobileTel(customer.getMobileTel());
				gridData.setStatus(so.getStatus());
				gridData.setProductID(so.getProduct().getProductID());
				gridData.setTypeID(so.getProduct().getType().getTypeID());
//				gridData.setType(so.getProduct().getType().getName());
				gridData.setType(stringUtility.convertUTF8ToISO_8859_1(so.getProduct().getType().getName()));
				gridData.setBrandID(so.getProduct().getBrand().getBrandID());
//				gridData.setBrand(so.getProduct().getBrand().getName());
				gridData.setBrand(stringUtility.convertUTF8ToISO_8859_1(so.getProduct().getBrand().getName()));
				gridData.setModelID(so.getProduct().getModel().getModelID());
//				gridData.setModel(so.getProduct().getModel().getName());
				gridData.setModel(stringUtility.convertUTF8ToISO_8859_1(so.getProduct().getModel().getName()));
//				gridData.setSerialNo(so.getProduct().getSerialNo());
				gridData.setSerialNo(stringUtility.convertUTF8ToISO_8859_1(so.getProduct().getSerialNo()));
				gridData.setAccessories(so.getAccessories());
				gridData.setProblem(so.getProblem());
				gridData.setDescription(so.getDescription());
				gridData.setEmpOpen(so.getEmpOpen().getName() + " "
						+ so.getEmpOpen().getSurname());

				if(so.getEmpFix() != null){
//					gridData.setEmpFix(so.getEmpFix().getName() + " "
//						+ so.getEmpFix().getSurname());
					gridData.setEmpFix(stringUtility.convertUTF8ToISO_8859_1(so.getEmpFix().getName()) + " " +
							stringUtility.convertUTF8ToISO_8859_1(so.getEmpFix().getSurname()));
				}
				gridData.setCannotMakeContact(so.getCannotMakeContact());

				rowsList.add(gridData);
			}
			total_pages = new Double(Math.ceil(((double)soList.size()/(double)rows))).intValue();
		}
		
		if (page > total_pages) page=total_pages;
		response.setPage(page.toString());
		response.setRecords(String.valueOf(soList.size()));
		response.setTotal(total_pages.toString());
		response.setRows(rowsList);
		//return response;
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
	@RequestMapping(value = "/getServiceOrder", params = "do=preview")
	public String preview(@RequestParam(value="serviceOrderID") String serviceOrderID, ModelMap model, HttpServletRequest request, Device device){
		if (null == request.getSession().getAttribute("UserLogin")) {
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		
		ServiceOrder so = soService.selectByID(serviceOrderID);
		ServiceOrderForm form = new ServiceOrderForm();
		form.setServiceOrderID(so.getServiceOrderID());
		form.setServiceOrderDate(sdfDateTime.format(so.getServiceOrderDate()));
		form.setServiceType(so.getServiceType());
		if(so.getAppointmentDate() != null){
			form.setAppointmentDate(sdfDateTime.format(so.getAppointmentDate()));
		}
		form.setRefJobID(so.getRefJobID());
		form.setRefServiceOrder(so.getRefServiceOrder());
		form.setCustomerType(so.getCustomerType());
		form.setCustomerID(so.getCustomer().getCustomerID().toString());
		form.setDeliveryCustomer(so.getDeliveryCustomer());
		form.setDeliveryEmail(so.getDeliveryEmail());
		form.setDeliveryMobileTel(so.getDeliveryMobileTel());
		form.setDeliveryTel(so.getDeliveryTel());
		form.setProductID(so.getProduct().getProductID());
		form.setTypeID(so.getProduct().getType().getTypeID());
		form.setBrandID(so.getProduct().getBrand().getBrandID());
		form.setModel(so.getProduct().getModel().getName());
		form.setSerialNo(so.getProduct().getSerialNo());
		form.setAccessories(so.getAccessories());
		form.setDesc(so.getDescription());
		form.setProblem(so.getProblem());
		form.setStatus(so.getStatus());

		model.addAttribute("form", form);

		model.addAttribute("customer", so.getCustomer());

		model.addAttribute(
				"fullAddr",
				so.getCustomer().getAddress()
						+ " "
						+ this.messages.getMessage("subdistrict_abbr", null,
								new Locale("th", "TH"))
						+ " "
						+ so.getCustomer().getSubdistrict().getName()
						+ " "
						+ this.messages.getMessage("district_abbr", null,
								new Locale("th", "TH"))
						+ " "
						+ so.getCustomer().getDistrict().getName()
						+ " "
						+ this.messages.getMessage("province_abbr", null,
								new Locale("th", "TH")) + " "
						+ so.getCustomer().getProvince().getName());

		model.addAttribute("product", so.getProduct());		
		
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	@RequestMapping(value = "/getServiceOrder", params = "do=getServiceOrder")
	public @ResponseBody CustomGenericResponse getServiceOrder(HttpServletRequest request){
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		CustomGenericResponse response = new CustomGenericResponse();
		response.setSuccess(true);
		response.setMessage(this.messages.getMessage("msg.getServiceOrder_success", null, new Locale("th", "TH")));
		
		boolean success = true;
		try{
			Date now = new Date();
			ServiceOrder so = soService.selectByID(request.getParameter("serviceOrderID"));
			
			so.setEmpFix(user);
			so.setStartFix(now);
			so.setStatus(ServiceOrder.FIXING);
			so.setUpdatedBy(user.getEmployeeID());
			so.setUpdatedDate(now);
			success = soService.edit(so);
		}catch(Exception e){
			response.setSuccess(false);
			response.setMessage(this.messages.getMessage("msg.getServiceOrder_failure", null, new Locale("th", "TH")));
			success = false;
		}
		if(!success){
			response.setSuccess(false);
			response.setMessage(this.messages.getMessage("msg.getServiceOrder_failure", null, new Locale("th", "TH")));
		}
		return response;
	}

	@RequestMapping(value = "/getServiceOrder", params = "do=accept")
	public String accept(@ModelAttribute("form") ServiceOrderForm form,
			HttpServletRequest request, ModelMap model, @RequestParam String mode, Device device) {
		if (null == request.getSession().getAttribute("UserLogin")) {
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		Date now = new Date();
		ServiceOrder so = soService.selectByID(request.getParameter("serviceOrderID"));
		
		so.setEmpFix(user);
		so.setStartFix(now);
		so.setStatus(ServiceOrder.FIXING);
		so.setUpdatedBy(user.getEmployeeID());
		so.setUpdatedDate(now);
		
		String msg = "";
		try {
			soService.edit(so);
			msg = this.messages.getMessage("msg.getServiceOrder_success", null,
					new Locale("th", "TH"));
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("form", form);
			
			msg = this.messages.getMessage("msg.getServiceOrder_failure", null,
					new Locale("th", "TH"));
			
			model.addAttribute("errMsg", msg);
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		model.addAttribute("msg", msg);
		
		ServiceOrderSearchForm searchForm = new ServiceOrderSearchForm();
		model.addAttribute("searchForm", searchForm);
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("typeList", typeList);
		
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
}
