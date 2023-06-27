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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.twobytes.repair.form.ServiceOrderGridData;
import com.twobytes.repair.form.ServiceOrderSearchForm;
import com.twobytes.repair.service.ServiceOrderService;
import com.twobytes.security.form.LoginForm;
import com.twobytes.util.DevicePage;
import com.twobytes.util.StringUtility;

@Controller
public class ReturnServiceOrderController {

	@Autowired
	private ServiceOrderService soService;
	
	@Autowired
	private TypeService typeService;
	
	@Autowired
	private MessageSource messages;
	
	@Autowired
	private StringUtility stringUtility;
	
	@Autowired
	private DevicePage dp;
	
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	private String VIEWNAME_SEARCH = "returnServiceOrder.search";
	private String VIEWNAME_M_SEARCH = "returnServiceOrder_m.search";
	
	private SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale ( "US" ));
	
	@RequestMapping(value = "/returnServiceOrder")
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
	@RequestMapping(value="/searchReturnServiceOrder")
	public @ResponseBody String getData(@RequestParam(value="name", required=false) String name, @RequestParam(value="date", required=false) String date, @RequestParam(value="type", required=false) String type, @RequestParam(value="issuePartCode", required=false) String issuePartCode, @RequestParam("rows") Integer rows, @RequestParam("page") Integer page, @RequestParam("sidx") String sidx, @RequestParam("sord") String sord) throws JsonProcessingException{
		// Because default Tomcat URI encoding is iso-8859-1 so it must encode back to tis620
		String[] datePart;
		String searchDate = null;
		// Because since tomcat 8 URI encoding is UTF-8 and set jsp charset to UTF-8 so it don't encode back to tis620
/*		try{
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
			if(null != issuePartCode) {
				issuePartCode = new String(issuePartCode.getBytes("iso-8859-1"), "tis620");
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
		
		List<ServiceOrder> soList = soService.selectFixedSOByCriteria(name, searchDate, type, issuePartCode, rows, page, sidx, sord);
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
			// If data is thai change encode from UTF-8 to ISO-8859-1 for display thai in grid
			for(int i=(rows*page - rows); i<endData; i++){
				ServiceOrder so = soList.get(i);
				ServiceOrderGridData gridData = new ServiceOrderGridData();
				gridData.setServiceOrderID(so.getServiceOrderID());
				gridData.setServiceOrderDate(sdfDateTime.format(so.getServiceOrderDate()));
				Customer customer = so.getCustomer();
//				gridData.setName(customer.getName());
				gridData.setName(stringUtility.convertUTF8ToISO_8859_1(customer.getName()));
				gridData.setTel(customer.getTel());
				gridData.setMobileTel(customer.getMobileTel());
				gridData.setStatus(so.getStatus());
				gridData.setCannotMakeContact(so.getCannotMakeContact());
//				gridData.setRemark(so.getRemark());
				gridData.setRemark(stringUtility.convertUTF8ToISO_8859_1(so.getRemark()));
				rowsList.add(gridData);
			}
			total_pages = new Double(Math.ceil(((double)soList.size()/(double)rows))).intValue();
		}
		
		if (page > total_pages) page=total_pages;
		response.setPage(page.toString());
		response.setRecords(String.valueOf(soList.size()));
		response.setTotal(total_pages.toString());
		response.setRows(rowsList);

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
	// Change calling AJAX
	// - Change return CustomGenericResponse to String
	// - Use ObjectMapper to convert CustomGenericResponse object to text and return it.
	@RequestMapping(value = "/returnServiceOrder", params = "do=returnServiceOrder")
	public @ResponseBody String returnServiceOrder(HttpServletRequest request) throws JsonProcessingException{
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		CustomGenericResponse response = new CustomGenericResponse();
		response.setSuccess(true);
		response.setMessage(stringUtility.convertUTF8ToISO_8859_1(this.messages.getMessage("msg.returnServiceOrder_success", null, new Locale("th", "TH"))));
		
		boolean success = true;
		try{
			Date now = new Date();
			ServiceOrder so = soService.selectByID(request.getParameter("serviceOrderID"));
			
			so.setReturnDate(now);
			so.setStatus(ServiceOrder.CLOSE);
			so.setUpdatedBy(user.getEmployeeID());
			so.setUpdatedDate(now);
			success = soService.edit(so);
		}catch(Exception e){
			response.setSuccess(false);
			response.setMessage(stringUtility.convertUTF8ToISO_8859_1(this.messages.getMessage("msg.returnServiceOrder_failure", null, new Locale("th", "TH"))));
			success = false;
		}
		if(!success){
			response.setSuccess(false);
			response.setMessage(stringUtility.convertUTF8ToISO_8859_1(this.messages.getMessage("msg.getServiceOrder_failure", null, new Locale("th", "TH"))));
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}

}
