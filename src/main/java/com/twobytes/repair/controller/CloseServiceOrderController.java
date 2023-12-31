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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twobytes.master.service.EmployeeService;
import com.twobytes.master.service.ModelService;
import com.twobytes.master.service.TypeService;
import com.twobytes.model.Brand;
import com.twobytes.model.Customer;
import com.twobytes.model.Employee;
import com.twobytes.model.GridResponse;
import com.twobytes.model.IssuePart;
import com.twobytes.model.Model;
import com.twobytes.model.OutsiteService;
import com.twobytes.model.OutsiteServiceDetail;
import com.twobytes.model.ServiceList;
import com.twobytes.model.ServiceOrder;
import com.twobytes.model.Type;
import com.twobytes.repair.form.OutsiteServiceDisplayForm;
import com.twobytes.repair.form.ServiceOrderDocForm;
import com.twobytes.repair.form.ServiceOrderForm;
import com.twobytes.repair.form.ServiceOrderGridData;
import com.twobytes.repair.form.ServiceOrderSearchForm;
import com.twobytes.repair.service.IssuePartService;
import com.twobytes.repair.service.OutsiteServiceDetailService;
import com.twobytes.repair.service.OutsiteServiceService;
import com.twobytes.repair.service.ServiceListService;
import com.twobytes.repair.service.ServiceOrderService;
import com.twobytes.security.form.LoginForm;
import com.twobytes.util.DevicePage;
import com.twobytes.util.StringUtility;

@Controller
public class CloseServiceOrderController {

	@Autowired
	private ServiceOrderService soService;
	
	@Autowired
	private OutsiteServiceService osService;
	
	@Autowired
	private OutsiteServiceDetailService osdService;
	
	@Autowired
	private TypeService typeService;
	
	@Autowired
	private ModelService modelService;
	
	@Autowired
	private ServiceListService slService;
	
	@Autowired
	private IssuePartService ipService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private StringUtility stringUtility;
	
	@Autowired
	private DevicePage dp;
	
	@Autowired
	private MessageSource messages;
	
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	private String VIEWNAME_SEARCH = "closeServiceOrder.search";
	private String VIEWNAME_FORM = "closeServiceOrder.form";
	private String VIEWNAME_M_SEARCH = "closeServiceOrder_m.search";
	private String VIEWNAME_M_FORM = "closeServiceOrder_m.form";
	
	private SimpleDateFormat sdfDateTime = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm", new Locale ( "US" ));
	
	@RequestMapping(value = "/closeServiceOrder")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		ServiceOrderSearchForm searchForm = new ServiceOrderSearchForm();
		/* If user is technician and admin set default employee search to user login */
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		if(user.getRoleID().getRoleID() == 4 || user.getRoleID().getRoleID() == 3){
			searchForm.setEmployee(user.getEmployeeID().toString());
		}
		model.addAttribute("searchForm", searchForm);
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("typeList", typeList);
		
		// Get technician and admin
		List<Integer> roleList = new ArrayList<Integer>();
		roleList.add(3); // admin
		roleList.add(4); // technician
		List<Employee> empList = employeeService.getByRole(roleList);
		model.addAttribute("employeeList", empList);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	// Change calling AJAX
	// - Change return GridResponse to String
	// - Use ObjectMapper to convert GridResponse object to text and return it.
//	public @ResponseBody GridResponse getData(@RequestParam(value="name", required=false) String name,
	@RequestMapping(value="/searchCloseServiceOrder")
	public @ResponseBody String getData(@RequestParam(value="name", required=false) String name, 
												@RequestParam(value="startDate", required=false) String startDate, 
												@RequestParam(value="endDate", required=false) String endDate, 
												@RequestParam(value="type", required=false) String type, 
												@RequestParam(value="serialNo", required=false) String serialNo, 
												@RequestParam(value="employee", required = false) String employee, 
												@RequestParam(value="issuePartCode", required = false) String issuePartCode, 
												@RequestParam("rows") Integer rows, 
												@RequestParam("page") Integer page, 
												@RequestParam("sidx") String sidx, 
												@RequestParam("sord") String sord, 
												HttpServletRequest request) throws JsonProcessingException {
		// Because default Tomcat URI encoding is iso-8859-1 so it must encode back to tis620
		String[] datePart;
		String searchStartDate = null;
		String searchEndDate = null;
		// Because since tomcat 8 URI encoding is UTF-8 and set jsp charset to UTF-8 so it don't encode back to tis620
/*		try{
			if(null != name){
				name = new String(name.getBytes("iso-8859-1"), "tis620");	
			}
			if(null != startDate && !startDate.equals("")){
				startDate = new String(startDate.getBytes("iso-8859-1"), "tis620");
				datePart = startDate.split("/");
				// Change year to Christ year
//				Integer year = Integer.parseInt(datePart[2]);				
//				year = year - 543;
				searchStartDate = datePart[2]+"-"+datePart[1]+"-"+datePart[0];
			}
			if (null != endDate && !endDate.equals("")) {
				endDate = new String(endDate.getBytes("iso-8859-1"), "tis620");
				datePart = endDate.split("/");
				searchEndDate = datePart[2] + "-" + datePart[1] + "-"
						+ datePart[0];
			}
			if(null != type){
				type = new String(type.getBytes("iso-8859-1"), "tis620");	
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
*/
		if(null != startDate && !startDate.equals("")){
			datePart = startDate.split("/");
			searchStartDate = datePart[2]+"-"+datePart[1]+"-"+datePart[0];
		}
		if (null != endDate && !endDate.equals("")) {
			datePart = endDate.split("/");
			searchEndDate = datePart[2] + "-" + datePart[1] + "-"
					+ datePart[0];
		}

		/*Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		
		List<ServiceOrder> soList = soService.selectSOForCloseByCriteria(user.getEmployeeID(), name,
				searchStartDate, searchEndDate, type, serialNo, rows, page,
				sidx, sord);*/
		/* Query before add IssuePartCode*/
		/*List<ServiceOrder> soList = soService.selectSOForCloseByCriteria(name,
				searchStartDate, searchEndDate, type, serialNo, employee, rows, page,
				sidx, sord);*/
		List<ServiceOrder> soList = soService.selectSOForCloseByCriteriaIssuePart(name,
				searchStartDate, searchEndDate, type, serialNo, employee, issuePartCode, rows, page,
				sidx, sord);
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
//				gridData.setType(so.getProduct().getType().getName());
				gridData.setType(stringUtility.convertUTF8ToISO_8859_1(so.getProduct().getType().getName()));
//				gridData.setBrand(so.getProduct().getBrand().getName());
				gridData.setBrand(stringUtility.convertUTF8ToISO_8859_1(so.getProduct().getBrand().getName()));
//				gridData.setModel(so.getProduct().getModel().getName());
				gridData.setModel(stringUtility.convertUTF8ToISO_8859_1(so.getProduct().getModel().getName()));
				gridData.setSerialNo(so.getProduct().getSerialNo());
				if(so.getEmpFix()!=null){
//					gridData.setEmpFix(so.getEmpFix().getName()+" "+so.getEmpFix().getSurname());
					gridData.setEmpFix(stringUtility.convertUTF8ToISO_8859_1(so.getEmpFix().getName()+" "+so.getEmpFix().getSurname()));
				}else{
					gridData.setEmpFix("-");
				}
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
//		return response;
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setPage("0");
			response.setRecords("0");
			response.setTotal("0");
			response.setRows(new ArrayList<ServiceOrderGridData>());
			//response.setRows(rowsList);
			return mapper.writeValueAsString(response);
		}
	}
	
	@RequestMapping(value = "/closeServiceOrder", params = "do=preCloseServiceOrder")
	public String preCloseServiceOrder(
			@RequestParam(value = "serviceOrderID") String serviceOrderID,
			ModelMap model, HttpServletRequest request, Device device) {
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
		if(so.getServiceType() == 1){
			form.setGuaranteeNo(so.getGuaranteeNo());
		}else if(so.getServiceType() == 4){
			form.setRefJobID(so.getRefJobID());
		}
		if(so.getAppointmentDate() != null){
			form.setAppointmentDate(sdfDateTime.format(so.getAppointmentDate()));
		}
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
		
		form.setRealProblem(so.getRealProblem());
		form.setCause(so.getCause());
		form.setFixDesc(so.getFixDesc());
		form.setRemark(so.getRemark());
		
		List<OutsiteService> osList = osService.selectByServiceOrderID(so.getServiceOrderID());
		List<OutsiteServiceDisplayForm> osdfList = new ArrayList<OutsiteServiceDisplayForm>();
		for(OutsiteService os : osList){
			OutsiteServiceDisplayForm osdf = new OutsiteServiceDisplayForm();
			osdf.setOutsiteServiceID(os.getOutsiteServiceID());
			osdf.setOutsiteServiceDate(os.getOutsiteServiceDate());
			osdf.setServiceType(os.getServiceType());
			osdf.setSentDate(os.getSentDate());
			osdf.setReceivedDate(os.getReceivedDate());
			osdf.setRepairing(os.getRepairing());
			osdf.setCosting(os.getCosting());
			osdf.setNetAmount(os.getNetAmount());
			osdf.setDetailList(osdService.getByOutsiteService(os.getOutsiteServiceID()));
			osdfList.add(osdf);
		}
		model.addAttribute("osdfList", osdfList);
		
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		form.setEmpOpen(so.getEmpOpen());
		
		if(so.getStartFix() != null){
			form.setStartFix(sdfDateTime.format(so.getStartFix()));
		}else{
			form.setStartFix("-");
		}
		if(so.getEndFix() != null){
			form.setEndFix(sdfDateTime.format(so.getEndFix()));
		}else{
			form.setEndFix("-");
		}
		
		if(so.getStatus().equals(ServiceOrder.FIXING)){
			if(form.getServiceType() == 1){
				form.setCosting("warranty");
			}else if(form.getServiceType() == 2 || form.getServiceType() == 3 || form.getServiceType() == 4){
				form.setCosting("cost");
			}else if(form.getServiceType() == 5){
				form.setCosting("free");
			}
			
			form.setIssuePart("noIssuedPart");
			
			if(osList.size()>0){
				Double netTotal = 0.00;
				for(OutsiteService os : osList){
					if(os.getNetAmount() != null){
						netTotal = os.getNetAmount() + netTotal;
					}else{
						netTotal = 0 + netTotal;
					}
					List<OutsiteServiceDetail> osdList = osdService.getByOutsiteService(os.getOutsiteServiceID());
					for(OutsiteServiceDetail osd : osdList){
						if(osd.getType().equals(OutsiteServiceDetail.TYPE_SERVICE)){
							if(form.getServiceList_1() == null){
								form.setServiceList_1(osd.getDesc());
								form.setServicePrice_1(osd.getPrice());
							}else if(form.getServiceList_2() == null){
								form.setServiceList_2(osd.getDesc());
								form.setServicePrice_2(osd.getPrice());
							}else if(form.getServiceList_3() == null){
								form.setServiceList_3(osd.getDesc());
								form.setServicePrice_3(osd.getPrice());
							}else if(form.getServiceList_4() == null){
								form.setServiceList_4(osd.getDesc());
								form.setServicePrice_4(osd.getPrice());
							}else{
								form.setServiceList_4(form.getServiceList_4()+"+"+osd.getDesc());
								form.setServicePrice_4(form.getServicePrice_4()+osd.getPrice());
							}
						}else if(osd.getType().equals(OutsiteServiceDetail.TYPE_REPAIR)){
							if(form.getOutsiteRepairPrice() == null) form.setOutsiteRepairPrice(0.00);
							form.setOutsiteRepairPrice(form.getOutsiteRepairPrice()+osd.getPrice());
							/*if(form.getRepairList_1() == null){
								form.setRepairList_1(osd.getDesc());
								form.setRepairPrice_1(osd.getPrice());
							}else if(form.getRepairList_2() == null){
								form.setRepairList_2(osd.getDesc());
								form.setRepairPrice_2(osd.getPrice());
							}else if(form.getRepairList_3() == null){
								form.setRepairList_3(osd.getDesc());
								form.setRepairPrice_3(osd.getPrice());
							}else if(form.getRepairList_4() == null){
								form.setRepairList_4(osd.getDesc());
								form.setRepairPrice_4(osd.getPrice());
							}else{
								form.setRepairList_4(form.getRepairList_4()+"+"+osd.getDesc());
								form.setRepairPrice_4(form.getRepairPrice_4()+osd.getPrice());
							}*/
						}
					}
				}
				form.setNetAmount(netTotal);
			}else{
				form.setOutsiteRepairPrice(0.00);
				form.setNetAmount(0.00);
			}
		}else if(so.getStatus().equals(ServiceOrder.FIXED)){
			form.setCosting(so.getCosting());
			
			form.setNetAmount(so.getTotalPrice());
			
			List<ServiceList> serviceList = slService.getByServiceOrder(so.getServiceOrderID());
			List<IssuePart> issuePartList = ipService.getByServiceOrder(so.getServiceOrderID());
			
			if(issuePartList.size() > 0){
				form.setIssuePart("haveIssuedPart");
			}else{
				form.setIssuePart("noIssuedPart");
			}
			
			for(int i=0 ;i<serviceList.size(); i++){
				ServiceList sl = serviceList.get(i);
				if(i == 0){
					form.setServiceList_1(sl.getServiceName());
					form.setServicePrice_1(sl.getPrice());
				}else if(i == 1){
					form.setServiceList_2(sl.getServiceName());
					form.setServicePrice_2(sl.getPrice());
				}else if(i == 2){
					form.setServiceList_3(sl.getServiceName());
					form.setServicePrice_3(sl.getPrice());
				}else if(i == 3){
					form.setServiceList_4(sl.getServiceName());
					form.setServicePrice_4(sl.getPrice());
				}
			}
			
			if(osList.size()>0){
				for(OutsiteService os : osList){
					List<OutsiteServiceDetail> osdList = osdService.getByOutsiteService(os.getOutsiteServiceID());
					for(OutsiteServiceDetail osd : osdList){
						if(osd.getType().equals(OutsiteServiceDetail.TYPE_REPAIR)){
							if(form.getOutsiteRepairPrice() == null) form.setOutsiteRepairPrice(0.00);
							form.setOutsiteRepairPrice(form.getOutsiteRepairPrice()+osd.getPrice());
							/*if(form.getRepairList_1() == null){
								form.setRepairList_1(osd.getDesc());
								form.setRepairPrice_1(osd.getPrice());
							}else if(form.getRepairList_2() == null){
								form.setRepairList_2(osd.getDesc());
								form.setRepairPrice_2(osd.getPrice());
							}else if(form.getRepairList_3() == null){
								form.setRepairList_3(osd.getDesc());
								form.setRepairPrice_3(osd.getPrice());
							}else if(form.getRepairList_4() == null){
								form.setRepairList_4(osd.getDesc());
								form.setRepairPrice_4(osd.getPrice());
							}else{
								form.setRepairList_4(form.getRepairList_4()+"+"+osd.getDesc());
								form.setRepairPrice_4(form.getRepairPrice_4()+osd.getPrice());
							}*/
						}
					}
				}
			}
			
			for(int j=0; j<issuePartList.size(); j++){
				IssuePart sp = issuePartList.get(j);
				if(j == 0){
					form.setIssuePartCode_1(sp.getCode());
					form.setIssuePartName_1(sp.getName());
					form.setIssuePartQty_1(sp.getQuantity());
					form.setIssuePartPrice_1(sp.getPrice());
				}else if(j == 1){
					form.setIssuePartCode_2(sp.getCode());
					form.setIssuePartName_2(sp.getName());
					form.setIssuePartQty_2(sp.getQuantity());
					form.setIssuePartPrice_2(sp.getPrice());
				}else if(j == 2){
					form.setIssuePartCode_3(sp.getCode());
					form.setIssuePartName_3(sp.getName());
					form.setIssuePartQty_3(sp.getQuantity());
					form.setIssuePartPrice_3(sp.getPrice());
				}else if(j == 3){
					form.setIssuePartCode_4(sp.getCode());
					form.setIssuePartName_4(sp.getName());
					form.setIssuePartQty_4(sp.getQuantity());
					form.setIssuePartPrice_4(sp.getPrice());
				}else if(j == 4){
					form.setIssuePartCode_5(sp.getCode());
					form.setIssuePartName_5(sp.getName());
					form.setIssuePartQty_5(sp.getQuantity());
					form.setIssuePartPrice_5(sp.getPrice());
				}else if(j == 5){
					form.setIssuePartCode_6(sp.getCode());
					form.setIssuePartName_6(sp.getName());
					form.setIssuePartQty_6(sp.getQuantity());
					form.setIssuePartPrice_6(sp.getPrice());
				}else if(j == 6){
					form.setIssuePartCode_7(sp.getCode());
					form.setIssuePartName_7(sp.getName());
					form.setIssuePartQty_7(sp.getQuantity());
					form.setIssuePartPrice_7(sp.getPrice());
				}else if(j == 7){
					form.setIssuePartCode_8(sp.getCode());
					form.setIssuePartName_8(sp.getName());
					form.setIssuePartQty_8(sp.getQuantity());
					form.setIssuePartPrice_8(sp.getPrice());
				}else if(j == 8){
					form.setIssuePartCode_9(sp.getCode());
					form.setIssuePartName_9(sp.getName());
					form.setIssuePartQty_9(sp.getQuantity());
					form.setIssuePartPrice_9(sp.getPrice());
				}else if(j == 9){
					form.setIssuePartCode_10(sp.getCode());
					form.setIssuePartName_10(sp.getName());
					form.setIssuePartQty_10(sp.getQuantity());
					form.setIssuePartPrice_10(sp.getPrice());
				}else if(j == 10){
					form.setIssuePartCode_11(sp.getCode());
					form.setIssuePartName_11(sp.getName());
					form.setIssuePartQty_11(sp.getQuantity());
					form.setIssuePartPrice_11(sp.getPrice());
				}
			}
			
		}
		
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
		
		Type type = typeList.get(0);
		List<Brand> brandList = new ArrayList<Brand>();
		List<Model> modelList = new ArrayList<Model>();
		if (so.getProduct().getType().getBrands().size() > 0) {
			brandList = so.getProduct().getType().getBrands();
			form.setBrandID(form.getBrandID());
			
			Brand brand = brandList.get(0);
			modelList = modelService.getModelByTypeAndBrand(type.getTypeID(), brand.getBrandID());
		} else {
			Brand blankBrand = new Brand();
			blankBrand.setBrandID(null);
			blankBrand.setName("");
			brandList.add(blankBrand);
		}
		
		model.addAttribute("typeList", typeList);
		model.addAttribute("brandList", brandList);
		model.addAttribute("modelList", modelList);

		ServiceOrderDocForm docForm = new ServiceOrderDocForm();
		model.addAttribute("docForm", docForm);
		
		model.addAttribute("mode", "close");
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	@RequestMapping(value = "/closeServiceOrder", params = "do=save")
	public String doSave(@ModelAttribute("form") ServiceOrderForm form, HttpServletRequest request, ModelMap model, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Date now = new Date();
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		
		ServiceOrder so = soService.selectByID(form.getServiceOrderID());
		so.setEndFix(now);
		
		so.setCosting(form.getCosting());
		so.setRealProblem(form.getRealProblem());
		so.setCause(form.getCause());
		so.setFixDesc(form.getFixDesc());
		so.setTotalPrice(form.getNetAmount());
		so.setRemark(form.getRemark());
		
		so.setUpdatedBy(user.getEmployeeID());
		so.setUpdatedDate(now);
		
		/*
		 * If service type is outsite service set status to close
		 */
		if(so.getServiceType() == 4){
			so.setStatus(ServiceOrder.CLOSE);
		}else{
			so.setReturnDate(now);
			so.setStatus(ServiceOrder.FIXED);
		}
		
		/*
		 * Check if this service order checked cannot make contact with customer, uncheck it
		 */
		if(so.getCannotMakeContact() != null && so.getCannotMakeContact() == 1){
			so.setCannotMakeContact(0);
		}
		
//		Double netAmount = form.getNetAmount();
//		System.out.println("netAmount = "+netAmount);
		
		// Add list of service cost and issue part and sent with service order to method save
		
		List<ServiceList> serviceList = new ArrayList<ServiceList>();
		List<IssuePart> issuePartList = new ArrayList<IssuePart>();
		
		if(form.getIssuePartCode_1() != "" && form.getIssuePartName_1() != "" && (form.getIssuePartQty_1() != null && form.getIssuePartQty_1() > 0) && (form.getIssuePartPrice_1() != null && form.getIssuePartPrice_1() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_1().doubleValue() * form.getIssuePartPrice_1();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_1());
			ip.setName(form.getIssuePartName_1());
			ip.setQuantity(form.getIssuePartQty_1());
			ip.setPrice(form.getIssuePartPrice_1());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_2() != "" && form.getIssuePartName_2() != "" && (form.getIssuePartQty_2() != null && form.getIssuePartQty_2() > 0) && (form.getIssuePartPrice_2() != null && form.getIssuePartPrice_2() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_2().doubleValue() * form.getIssuePartPrice_2();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_2());
			ip.setName(form.getIssuePartName_2());
			ip.setQuantity(form.getIssuePartQty_2());
			ip.setPrice(form.getIssuePartPrice_2());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_3() != "" && form.getIssuePartName_3() != "" && (form.getIssuePartQty_3() != null && form.getIssuePartQty_3() > 0) && (form.getIssuePartPrice_3() != null && form.getIssuePartPrice_3() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_3().doubleValue() * form.getIssuePartPrice_3();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_3());
			ip.setName(form.getIssuePartName_3());
			ip.setQuantity(form.getIssuePartQty_3());
			ip.setPrice(form.getIssuePartPrice_3());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_4() != "" && form.getIssuePartName_4() != "" && (form.getIssuePartQty_4() != null && form.getIssuePartQty_4() > 0) && (form.getIssuePartPrice_4() != null && form.getIssuePartPrice_4() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_4().doubleValue() * form.getIssuePartPrice_4();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_4());
			ip.setName(form.getIssuePartName_4());
			ip.setQuantity(form.getIssuePartQty_4());
			ip.setPrice(form.getIssuePartPrice_4());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_5() != "" && form.getIssuePartName_5() != "" && (form.getIssuePartQty_5() != null && form.getIssuePartQty_5() > 0) && (form.getIssuePartPrice_5() != null && form.getIssuePartPrice_5() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_5().doubleValue() * form.getIssuePartPrice_5();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_5());
			ip.setName(form.getIssuePartName_5());
			ip.setQuantity(form.getIssuePartQty_5());
			ip.setPrice(form.getIssuePartPrice_5());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_6() != "" && form.getIssuePartName_6() != "" && (form.getIssuePartQty_6() != null && form.getIssuePartQty_6() > 0) && (form.getIssuePartPrice_6() != null && form.getIssuePartPrice_6() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_6().doubleValue() * form.getIssuePartPrice_6();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_6());
			ip.setName(form.getIssuePartName_6());
			ip.setQuantity(form.getIssuePartQty_6());
			ip.setPrice(form.getIssuePartPrice_6());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_7() != "" && form.getIssuePartName_7() != "" && (form.getIssuePartQty_7() != null && form.getIssuePartQty_7() > 0) && (form.getIssuePartPrice_7() != null && form.getIssuePartPrice_7() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_7().doubleValue() * form.getIssuePartPrice_7();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_7());
			ip.setName(form.getIssuePartName_7());
			ip.setQuantity(form.getIssuePartQty_7());
			ip.setPrice(form.getIssuePartPrice_7());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_8() != "" && form.getIssuePartName_8() != "" && (form.getIssuePartQty_8() != null && form.getIssuePartQty_8() > 0) && (form.getIssuePartPrice_8() != null && form.getIssuePartPrice_8() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_8().doubleValue() * form.getIssuePartPrice_8();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_8());
			ip.setName(form.getIssuePartName_8());
			ip.setQuantity(form.getIssuePartQty_8());
			ip.setPrice(form.getIssuePartPrice_8());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_9() != "" && form.getIssuePartName_9() != "" && (form.getIssuePartQty_9() != null && form.getIssuePartQty_9() > 0) && (form.getIssuePartPrice_9() != null && form.getIssuePartPrice_9() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_9().doubleValue() * form.getIssuePartPrice_9();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_9());
			ip.setName(form.getIssuePartName_9());
			ip.setQuantity(form.getIssuePartQty_9());
			ip.setPrice(form.getIssuePartPrice_9());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_10() != "" && form.getIssuePartName_10() != "" && (form.getIssuePartQty_10() != null && form.getIssuePartQty_10() > 0) && (form.getIssuePartPrice_10() != null && form.getIssuePartPrice_10() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_10().doubleValue() * form.getIssuePartPrice_10();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_10());
			ip.setName(form.getIssuePartName_10());
			ip.setQuantity(form.getIssuePartQty_10());
			ip.setPrice(form.getIssuePartPrice_10());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		if(form.getIssuePartCode_11() != "" && form.getIssuePartName_11() != "" && (form.getIssuePartQty_11() != null && form.getIssuePartQty_11() > 0) && (form.getIssuePartPrice_11() != null && form.getIssuePartPrice_11() > 0)){
			// Add to table issuePart
			Double netPrice = form.getIssuePartQty_11().doubleValue() * form.getIssuePartPrice_11();
			IssuePart ip = new IssuePart();
			ip.setServiceOrder(so);
			ip.setCode(form.getIssuePartCode_11());
			ip.setName(form.getIssuePartName_11());
			ip.setQuantity(form.getIssuePartQty_11());
			ip.setPrice(form.getIssuePartPrice_11());
			ip.setNetPrice(netPrice);
			ip.setCreatedBy(user.getEmployeeID());
			ip.setCreatedDate(now);
			ip.setUpdatedBy(user.getEmployeeID());
			ip.setUpdatedDate(now);
			issuePartList.add(ip);
		}
		
		if(form.getServiceList_1() != "" && (form.getServicePrice_1() != null && form.getServicePrice_1() > 0)){
			ServiceList sl = new ServiceList();
			sl.setServiceOrder(so);
			sl.setServiceName(form.getServiceList_1());
			sl.setPrice(form.getServicePrice_1());
			sl.setCreatedBy(user.getEmployeeID());
			sl.setCreatedDate(now);
			sl.setUpdatedBy(user.getEmployeeID());
			sl.setUpdatedDate(now);
			serviceList.add(sl);
		}
		if(form.getServiceList_2() != "" && (form.getServicePrice_2() != null && form.getServicePrice_2() > 0)){
			ServiceList sl = new ServiceList();
			sl.setServiceOrder(so);
			sl.setServiceName(form.getServiceList_2());
			sl.setPrice(form.getServicePrice_2());
			sl.setCreatedBy(user.getEmployeeID());
			sl.setCreatedDate(now);
			sl.setUpdatedBy(user.getEmployeeID());
			sl.setUpdatedDate(now);
			serviceList.add(sl);
		}
		if(form.getServiceList_3() != "" && (form.getServicePrice_3() != null && form.getServicePrice_3() > 0)){
			ServiceList sl = new ServiceList();
			sl.setServiceOrder(so);
			sl.setServiceName(form.getServiceList_3());
			sl.setPrice(form.getServicePrice_3());
			sl.setCreatedBy(user.getEmployeeID());
			sl.setCreatedDate(now);
			sl.setUpdatedBy(user.getEmployeeID());
			sl.setUpdatedDate(now);
			serviceList.add(sl);
		}
		if(form.getServiceList_4() != "" && (form.getServicePrice_4() != null && form.getServicePrice_4() > 0)){
			ServiceList sl = new ServiceList();
			sl.setServiceOrder(so);
			sl.setServiceName(form.getServiceList_4());
			sl.setPrice(form.getServicePrice_4());
			sl.setCreatedBy(user.getEmployeeID());
			sl.setCreatedDate(now);
			sl.setUpdatedBy(user.getEmployeeID());
			sl.setUpdatedDate(now);
			serviceList.add(sl);
		}
		
		try {
			soService.close(so, issuePartList, serviceList);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errMsg", e.getMessage());
		}
		
		
		// Set data for redirect back to form screen
		form.setServiceOrderDate(sdfDateTime.format(so.getServiceOrderDate()));
		form.setServiceType(so.getServiceType());
		if(so.getServiceType() == 1){
			form.setGuaranteeNo(so.getGuaranteeNo());
		}else if(so.getServiceType() == 4){
			form.setRefJobID(so.getRefJobID());
		}
		if(so.getAppointmentDate() != null){
			form.setAppointmentDate(sdfDateTime.format(so.getAppointmentDate()));
		}
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
		
		form.setEmpOpen(so.getEmpOpen());
		
		if(so.getStartFix() != null){
			form.setStartFix(sdfDateTime.format(so.getStartFix()));
		}else{
			form.setStartFix("-");
		}
		if(so.getEndFix() != null){
			form.setEndFix(sdfDateTime.format(so.getEndFix()));
		}else{
			form.setEndFix("-");
		}
		
		/*if(form.getServiceType() == 1){
			form.setCosting("warranty");
		}else if(form.getServiceType() == 2 || form.getServiceType() == 3 || form.getServiceType() == 4){
			form.setCosting("cost");
		}else if(form.getServiceType() == 5){
			form.setCosting("free");
		}*/
		
		List<OutsiteService> osList = osService.selectByServiceOrderID(so.getServiceOrderID());
		List<OutsiteServiceDisplayForm> osdfList = new ArrayList<OutsiteServiceDisplayForm>();
		for(OutsiteService os : osList){
			OutsiteServiceDisplayForm osdf = new OutsiteServiceDisplayForm();
			osdf.setOutsiteServiceID(os.getOutsiteServiceID());
			osdf.setOutsiteServiceDate(os.getOutsiteServiceDate());
			osdf.setServiceType(os.getServiceType());
			osdf.setSentDate(os.getSentDate());
			osdf.setReceivedDate(os.getReceivedDate());
			osdf.setRepairing(os.getRepairing());
			osdf.setCosting(os.getCosting());
			osdf.setNetAmount(os.getNetAmount());
			osdf.setDetailList(osdService.getByOutsiteService(os.getOutsiteServiceID()));
			osdfList.add(osdf);
		}
		model.addAttribute("osdfList", osdfList);
		
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

		
		
		// get data for print document
		ServiceOrderDocForm docForm = setDocPrintForm(so);
		model.addAttribute("docForm", docForm);
		model.addAttribute("action", "print");
		model.addAttribute("form", form);
		model.addAttribute("mode", "edit");
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	private ServiceOrderDocForm setDocPrintForm(ServiceOrder so){
		ServiceOrderDocForm docForm = new ServiceOrderDocForm();
		docForm.setServiceOrderID(so.getServiceOrderID());
		return docForm;
	}
}
