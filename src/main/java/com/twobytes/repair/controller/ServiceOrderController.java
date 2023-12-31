package com.twobytes.repair.controller;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mobile.device.Device;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.twobytes.master.form.CustomerForm;
import com.twobytes.master.form.ModelForm;
import com.twobytes.master.form.ProductForm;
import com.twobytes.master.service.BrandService;
import com.twobytes.master.service.CustomerService;
import com.twobytes.master.service.CustomerTypeService;
import com.twobytes.master.service.DistrictService;
import com.twobytes.master.service.EmployeeService;
import com.twobytes.master.service.ModelService;
import com.twobytes.master.service.ProductService;
import com.twobytes.master.service.ProvinceService;
import com.twobytes.master.service.SubdistrictService;
import com.twobytes.master.service.TypeService;
import com.twobytes.model.Brand;
import com.twobytes.model.CustomGenericResponse;
import com.twobytes.model.Customer;
import com.twobytes.model.CustomerType;
import com.twobytes.model.District;
import com.twobytes.model.Employee;
import com.twobytes.model.GridResponse;
import com.twobytes.model.IssuePart;
import com.twobytes.model.Model;
import com.twobytes.model.OutsiteService;
import com.twobytes.model.OutsiteServiceDetail;
import com.twobytes.model.Product;
import com.twobytes.model.Province;
import com.twobytes.model.ServiceList;
import com.twobytes.model.ServiceOrder;
import com.twobytes.model.Subdistrict;
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
public class ServiceOrderController {

	@Autowired
	private ServiceOrderService soService;

	@Autowired
	private TypeService typeService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private ModelService modelService;
	
	@Autowired
	private SubdistrictService sdService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private ProvinceService provinceService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmployeeService empService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CustomerTypeService customerTypeService;

	@Autowired
	private IssuePartService issuePartService;
	
	@Autowired
	private ServiceListService serviceListService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private OutsiteServiceService osService;
	
	@Autowired
	private OutsiteServiceDetailService osdService;
	
	@Autowired
	private StringUtility stringUtility;
	
	@Autowired
	private DevicePage dp;
	
	@Autowired
	private MessageSource messages;

	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

	private String VIEWNAME_SEARCH = "serviceOrder.search";
	private String VIEWNAME_FORM = "serviceOrder.form";
	private String VIEWNAME_M_SEARCH = "serviceOrder_m.search";
	private String VIEWNAME_M_FORM = "serviceOrder_m.form";

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale ("US"));
	/*private SimpleDateFormat sdfDateTime = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm", new Locale("th", "TH"));*/
	private SimpleDateFormat sdfDateTime = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm", new Locale("US"));
	private SimpleDateFormat sdfDateTime_TH = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm", new Locale("th", "th"));
	private SimpleDateFormat sdfDateTimeNoLocale = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm");
	private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", new Locale("US"));

	@RequestMapping(value = "/serviceOrder")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if (null == request.getSession().getAttribute("UserLogin")) {
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
		
		List<Employee> empList = employeeService.getAll();
		model.addAttribute("employeeList", empList);
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}

	// Change calling AJAX
	// - Change return GridResponse to String
	// - Use ObjectMapper to convert GridResponse object to text and return it.
	//@RequestMapping(value = "/searchServiceOrder", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params="format=json")
	//@RequestMapping(value={"/getServiceCategoriesForVisitType"},method=RequestMethod.GET,headers="Accept=*/*",produces = "application/json")
//	@RequestMapping(value={"/searchServiceOrder"},method=RequestMethod.GET,headers="Accept=*/*",produces = "application/json")
//	@ResponseStatus(HttpStatus.OK)
	
	@RequestMapping(value="/searchServiceOrder")
	@SuppressWarnings("unchecked")
	public @ResponseBody
//	GridResponse getData(
	String getData(
			@RequestParam(value = "serviceOrderID", required = false) String serviceOrderID,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "mobileTel", required = false) String mobileTel,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "serialNo", required = false) String serialNo,
			@RequestParam(value = "employee", required = false) String employee,
			@RequestParam(value = "issuePartCode", required=false) String issuePartCode,
			@RequestParam("rows") Integer rows,
			@RequestParam("page") Integer page,
			@RequestParam("sidx") String sidx, @RequestParam("sord") String sord) throws JsonProcessingException {
		String[] datePart;
		String searchStartDate = null;
		String searchEndDate = null;
		// Because default Tomcat URI encoding is iso-8859-1 so it must encode back to tis620

		// Because since tomcat 8 URI encoding is UTF-8 and set jsp charset to UTF-8 so it don't encode back to tis620
/*		try {
			if (null != serviceOrderID) {
				serviceOrderID = new String(serviceOrderID.getBytes("iso-8859-1"), "tis620");
			}
			if (null != name) {
				name = new String(name.getBytes("iso-8859-1"), "tis620");
			}
			if (null != mobileTel) {
				mobileTel = new String(mobileTel.getBytes("iso-8859-1"), "tis620");
			}
			if (null != startDate && !startDate.equals("")) {
				startDate = new String(startDate.getBytes("iso-8859-1"),
						"tis620");
				datePart = startDate.split("/");
				searchStartDate = datePart[2] + "-" + datePart[1] + "-"
						+ datePart[0];
			}
			if (null != endDate && !endDate.equals("")) {
				endDate = new String(endDate.getBytes("iso-8859-1"), "tis620");
				datePart = endDate.split("/");
				searchEndDate = datePart[2] + "-" + datePart[1] + "-"
						+ datePart[0];
			}
			if (null != type) {
				type = new String(type.getBytes("iso-8859-1"), "tis620");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
*/
		//convert date format from d/m/y to y-m-d
		if (null != startDate && !startDate.equals("")) {
			datePart = startDate.split("/");
			searchStartDate = datePart[2] + "-" + datePart[1] + "-"
					+ datePart[0];
		}
		if (null != endDate && !endDate.equals("")) {
			datePart = endDate.split("/");
			searchEndDate = datePart[2] + "-" + datePart[1] + "-"
					+ datePart[0];
		}
		
		Map<String, Object> ret = new HashMap<String, Object>();
//		ret = soService.selectByCriteria(name,
//				searchStartDate, searchEndDate, type, serialNo, employee, rows, page,
//				sidx, sord);
		ret = soService.selectByCriteria2(serviceOrderID, name, mobileTel,
				searchStartDate, searchEndDate, type, serialNo, employee, issuePartCode, rows, page,
				sidx, sord);
		
		List<ServiceOrder> soList = (List<ServiceOrder>) ret.get("list");
		GridResponse response = new GridResponse();
		List<ServiceOrderGridData> rowsList = new ArrayList<ServiceOrderGridData>();
		
		Integer total_pages = 0;
//		System.out.println("ServiceOrderController:getData:soList.size() = "+soList.size());
		if (soList.size() > 0) {
			// If data is thai change encode from UTF-8 to ISO-8859-1 for display thai in grid
			for(ServiceOrder so:soList){
				ServiceOrderGridData gridData = new ServiceOrderGridData();
				gridData.setServiceOrderID(so.getServiceOrderID());
//				System.out.println("ServiceOrderController:getData:so.getServiceOrderID() = "+so.getServiceOrderID());
				gridData.setServiceOrderDate(sdfDateTime.format(so
						.getServiceOrderDate()));
				String serviceType = "";
				
				if (so.getServiceType() == 1) {
					serviceType = stringUtility.convertUTF8ToISO_8859_1(this.messages.getMessage(
							"serviceOrderType_guarantee", null, new Locale("th", "TH")));
					if(so.getGuaranteeNo() != null){
						serviceType = serviceType + " " + stringUtility.convertUTF8ToISO_8859_1(this.messages.getMessage("guarantee_No", null, new Locale("th", "TH"))) + " " + stringUtility.convertUTF8ToISO_8859_1(so.getGuaranteeNo().toString());
					}
				} else if (so.getServiceType() == 2) {
					serviceType = stringUtility.convertUTF8ToISO_8859_1(this.messages.getMessage(
							"serviceOrderType_repair", null, new Locale("th", "TH")));
				} else if (so.getServiceType() == 3) {
					serviceType = stringUtility.convertUTF8ToISO_8859_1(this.messages.getMessage(
							"serviceOrderType_claim", null, new Locale("th", "TH")));
				} else if (so.getServiceType() == 4) {
					serviceType = stringUtility.convertUTF8ToISO_8859_1(this.messages.getMessage(
							"serviceOrderType_outsiteService", null, new Locale("th", "TH")) + " " +
							this.messages.getMessage("reference", null, new Locale("th", "TH")) + " " +
							so.getRefJobID());
				} else if (so.getServiceType() == 5) {
					serviceType = stringUtility.convertUTF8ToISO_8859_1(this.messages.getMessage(
							"serviceOrderType_refix", null, new Locale("th", "TH"))+ " " +
							this.messages.getMessage("reference", null, new Locale("th", "TH")) + " " +
							so.getRefServiceOrder());
				}
				gridData.setServiceType(serviceType);
				try{
					gridData.setAppointmentDate(sdfDateTime.format(so.getAppointmentDate()));
				}catch(NullPointerException npe){
					gridData.setAppointmentDate("-");
				}
				Customer customer = so.getCustomer();
//				gridData.setName(customer.getName());
				gridData.setName(stringUtility.convertUTF8ToISO_8859_1(customer.getName()));
				gridData.setEmail(customer.getEmail());
				gridData.setTel(customer.getTel());
				gridData.setMobileTel(customer.getMobileTel());
					
				gridData.setCustomer(customer);
				gridData.setCustomerFullAddress(customer.getAddress()
						+ " "
						+ this.messages.getMessage("subdistrict_abbr", null,
								new Locale("th", "TH"))
						+ " "
						+ customer.getSubdistrict().getName()
						+ " "
						+ this.messages.getMessage("district_abbr", null,
								new Locale("th", "TH"))
						+ " "
						+ customer.getDistrict().getName()
						+ " "
						+ this.messages.getMessage("province_abbr", null,
								new Locale("th", "TH")) + " "
						+ customer.getProvince().getName());
//				gridData.setDeliveryCustomer(so.getDeliveryCustomer());
				gridData.setDeliveryCustomer(stringUtility.convertUTF8ToISO_8859_1(so.getDeliveryCustomer()));
				gridData.setDeliveryEmail(so.getDeliveryEmail());
				gridData.setDeliveryTel(so.getDeliveryTel());
				gridData.setDeliveryMobileTel(so.getDeliveryMobileTel());
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
//				gridData.setProblem(so.getProblem());
				gridData.setProblem(stringUtility.convertUTF8ToISO_8859_1(so.getProblem()));
//				gridData.setDescription(so.getDescription());
				gridData.setDescription(stringUtility.convertUTF8ToISO_8859_1(so.getDescription()));
//				gridData.setEmpOpen(so.getEmpOpen().getName() + " "
//						+ so.getEmpOpen().getSurname());
				gridData.setEmpOpen(stringUtility.convertUTF8ToISO_8859_1(so.getEmpOpen().getName() + " "
						+ so.getEmpOpen().getSurname()));
				
				if(so.getEmpFix() != null){
//					gridData.setEmpFix(so.getEmpFix().getName() + " "
//						+ so.getEmpFix().getSurname());
					gridData.setEmpFix(stringUtility.convertUTF8ToISO_8859_1(so.getEmpFix().getName()) + " " +
							stringUtility.convertUTF8ToISO_8859_1(so.getEmpFix().getSurname()));
				}
				gridData.setCannotMakeContact(so.getCannotMakeContact());
				
				rowsList.add(gridData);
//				System.out.println("ServiceOrderController:getData:gridData = "+gridData);
			}
			total_pages = new Double(Math.ceil(((double)(Long) ret.get("maxRows")/(double)rows))).intValue();
		}
		if (page > total_pages) page=total_pages;
		response.setPage(page.toString());
		response.setRecords(((Long) ret.get("maxRows")).toString());
		response.setTotal(total_pages.toString());
		response.setRows(rowsList);
//		System.out.println("ServiceOrderController:getData:response.getTotal() = "+response.getTotal());
//		System.out.println("ServiceOrderController:getData:response = "+response);
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
		 //mapper.writeValueAsString(response);
		 
		 //System.out.println("mapper.writeValueAsString(response) = "+mapper.writeValueAsString(response));
		
		//return response;
	}

	@RequestMapping(value = "/serviceOrder", params = "do=preAdd")
	public String preAdd(ModelMap model, HttpServletRequest request, Device device)
			throws ParseException {
		if (null == request.getSession().getAttribute("UserLogin")) {
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		ServiceOrderForm form = new ServiceOrderForm();
		ProductForm productForm = new ProductForm();
		Date now = new Date();
		form.setServiceOrderDate(sdfDateTime.format(now));
		form.setServiceType(2);
		form.setCustomerType(ServiceOrder.CUSTOMERTYPE_SHOP);
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Type type = typeList.get(0);
		form.setTypeID(type.getTypeID());
		List<Brand> brandList = new ArrayList<Brand>();
		if (type.getBrands().size() > 0) {
			brandList = type.getBrands();
			Brand brand = brandList.get(0);
			form.setBrandID(brand.getBrandID());
		} else {
			Brand blankBrand = new Brand();
			blankBrand.setBrandID(null);
			blankBrand.setName("");
			brandList.add(blankBrand);
		}

		List<Model> modelList = new ArrayList<Model>();
		if (type.getBrands().size() > 0) {
			brandList = type.getBrands();
			form.setBrandID(form.getBrandID());
			
			Brand brand = brandList.get(0);
			modelList = modelService.getModelByTypeAndBrand(type.getTypeID(), brand.getBrandID());
		} else {
			Brand blankBrand = new Brand();
			blankBrand.setBrandID(null);
			blankBrand.setName("");
			brandList.add(blankBrand);
		}
				
		model.addAttribute("form", form);
		model.addAttribute("typeList", typeList);
		model.addAttribute("brandList", brandList);
		model.addAttribute("modelList", modelList);

		// get data for customer form
		List<Province> provinceList = provinceService.getAll();
		List<District> districtList = districtService.getByProvince(7);
		// set subdistrict from Muang district
		List<Subdistrict> subdistrictList = sdService.getByDistrict(160);

		//Subdistrict sd = subdistrictList.get(0);
		
		List<CustomerType> customerTypeList = customerTypeService.getAll();
		
		model.addAttribute("provinceList", provinceList);
		model.addAttribute("districtList", districtList);
		model.addAttribute("subdistrictList", subdistrictList);
		model.addAttribute("customerTypeList", customerTypeList);

		CustomerForm custForm = new CustomerForm();
		custForm.setProvinceID(7);
		// set default district to Muang
		custForm.setDistrictID(160);
		// set subdistrict from Muang district
		custForm.setSubdistrictID(((Subdistrict)subdistrictList.get(0)).getSubdistrictID());
		custForm.setZipcode(((Subdistrict)subdistrictList.get(0)).getZipcode().toString());
		model.addAttribute("customerForm", custForm);

		ModelForm modelForm = new ModelForm();
		model.addAttribute("modelForm", modelForm);
		
		// get form for print document
		ServiceOrderDocForm docForm = new ServiceOrderDocForm();
		model.addAttribute("docForm", docForm);
		
		model.addAttribute("productForm", productForm);
		
		model.addAttribute("mode", "add");
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}

	@RequestMapping(value = "/serviceOrder", params = "do=save")
	public String doSave(@ModelAttribute("form") ServiceOrderForm form,
			HttpServletRequest request, ModelMap model, @RequestParam String mode, Device device) {
		if (null == request.getSession().getAttribute("UserLogin")) {
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Date now = new Date();
		Employee user = (Employee) request.getSession().getAttribute(
				"UserLogin");
		ServiceOrder so = new ServiceOrder();
		String msg = "";
		if (!form.getServiceOrderID().equals("")) {
			// update
			so = soService.selectByID(form.getServiceOrderID());
			msg = this.messages.getMessage("msg.updateComplete", null,
					new Locale("th", "TH"));
			
			if(form.getEmpFixID() != null){
				Employee empFix = new Employee();
				try {
					empFix = employeeService.selectByID(form.getEmpFixID());
					so.setEmpFix(empFix);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			// add
			so.setServiceType(form.getServiceType());
			if(form.getServiceType() == 1){
				so.setGuaranteeNo(form.getGuaranteeNo());
			}
			
			if(form.getServiceType() == 4){
				so.setRefJobID(form.getRefJobID());
			}
			
			if(form.getServiceType() == 5){
				so.setRefServiceOrder(form.getRefServiceOrder());
			}
			try {
				so.setServiceOrderDate(sdfDateTime.parse(form
						.getServiceOrderDate()));
			} catch (ParseException e) {
				e.printStackTrace();
				so.setServiceOrderDate(new Date());
			}
			
			so.setEmpOpen(user);
			so.setCreatedBy(user.getEmployeeID());
			so.setCreatedDate(now);
			so.setStatus(ServiceOrder.NEW);
			msg = this.messages.getMessage("msg.addComplete", null, new Locale(
					"th", "TH"));
		}
		
		if(!form.getAppointmentDate().equals(null) && !form.getAppointmentDate().equals("")){
			try {
				so.setAppointmentDate(sdfDateTime.parse(form.getAppointmentDate()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		Customer customer = new Customer();
		try {
			customer = customerService.selectByID(form.getCustomerID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		so.setCustomer(customer);
		model.addAttribute("customer", customer);
		
		so.setDeliveryCustomer(form.getDeliveryCustomer());
		so.setDeliveryEmail(form.getDeliveryEmail());
		so.setDeliveryMobileTel(form.getDeliveryMobileTel());
		so.setDeliveryTel(form.getDeliveryTel());
		if(form.getCannotMakeContact() == null){
			so.setCannotMakeContact(0);	
		}else{
			so.setCannotMakeContact(form.getCannotMakeContact());
		}
		so.setRemark(form.getRemark());
		
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

		Product product = new Product();
		product = productService.selectByID(form.getProductID());
		so.setProduct(product);
		so.setAccessories(form.getAccessories());
		so.setDescription(form.getDesc());
		so.setProblem(form.getProblem());

		so.setUpdatedBy(user.getEmployeeID());
		so.setUpdatedDate(now);
		String result;
		try {
			result = soService.save(so);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errMsg", e.getMessage());
			
			Customer customer1 = new Customer();
			try {
				customer1 = customerService.selectByID(form.getCustomerID());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			so.setCustomer(customer1);
			model.addAttribute("customer", customer1);
			
			List<Type> typeList = new ArrayList<Type>();
			try {
				typeList = typeService.getAll();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Type type = typeList.get(0);
			
			List<Model> modelList = new ArrayList<Model>();
			List<Brand> brandList = new ArrayList<Brand>();
			if (type.getBrands().size() > 0) {
				brandList = type.getBrands();
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

			// get data for customer form
			List<Province> provinceList = provinceService.getAll();
			List<District> districtList = districtService.getByProvince(7);
			// set subdistrict from Muang district
			List<Subdistrict> subdistrictList = sdService.getByDistrict(160);

			Subdistrict sd = subdistrictList.get(0);
			
			List<CustomerType> customerTypeList = customerTypeService.getAll();
			
			model.addAttribute("provinceList", provinceList);
			model.addAttribute("districtList", districtList);
			model.addAttribute("subdistrictList", subdistrictList);
			model.addAttribute("zipcode", sd.getZipcode());
			model.addAttribute("customerTypeList", customerTypeList);
			
			CustomerForm custForm = new CustomerForm();
			custForm.setProvinceID(7);
			// set default district to Muang
			custForm.setDistrictID(160);
			model.addAttribute("customerForm", custForm);

			ProductForm productForm = new ProductForm();
			model.addAttribute("productForm", productForm);
			
			model.addAttribute("product", so.getProduct());
			
			List<Employee> empList = employeeService.getAll();
			
			model.addAttribute("employeeList", empList);
			
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
			
			ModelForm modelForm = new ModelForm();
			model.addAttribute("modelForm", modelForm);
			
			// get form for print document
			ServiceOrderDocForm docForm = setDocPrintForm(so);
			model.addAttribute("docForm", docForm);
			model.addAttribute("mode", mode);
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		if (result.equals("false")) {
			model.addAttribute("errMsg", this.messages.getMessage(
					"error.cannotSave", null, new Locale("th", "TH")));
			
			Customer customer1 = new Customer();
			try {
				customer1 = customerService.selectByID(form.getCustomerID());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			so.setCustomer(customer1);
			model.addAttribute("customer", customer1);
			
			List<Type> typeList = new ArrayList<Type>();
			try {
				typeList = typeService.getAll();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Type type = typeList.get(0);
			
			List<Model> modelList = new ArrayList<Model>();
			List<Brand> brandList = new ArrayList<Brand>();
			if (type.getBrands().size() > 0) {
				brandList = type.getBrands();
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

			// get data for customer form
			List<Province> provinceList = provinceService.getAll();
			List<District> districtList = districtService.getByProvince(7);
			// set subdistrict from Muang district
			List<Subdistrict> subdistrictList = sdService.getByDistrict(160);
			
			Subdistrict sd = subdistrictList.get(0);
			
			List<CustomerType> customerTypeList = customerTypeService.getAll();
			
			model.addAttribute("provinceList", provinceList);
			model.addAttribute("districtList", districtList);
			model.addAttribute("subdistrictList", subdistrictList);
			model.addAttribute("zipcode", sd.getZipcode());
			model.addAttribute("customerTypeList", customerTypeList);

			CustomerForm custForm = new CustomerForm();
			custForm.setProvinceID(7);
			// set default district to Muang
			custForm.setDistrictID(160);
			model.addAttribute("customerForm", custForm);

			ProductForm productForm = new ProductForm();
			model.addAttribute("productForm", productForm);
			
			model.addAttribute("product", so.getProduct());
			
			List<Employee> empList = employeeService.getAll();
			
			model.addAttribute("employeeList", empList);

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
			
			ModelForm modelForm = new ModelForm();
			model.addAttribute("modelForm", modelForm);
			
			// get form for print document
			ServiceOrderDocForm docForm = setDocPrintForm(so);
			model.addAttribute("docForm", docForm);
			model.addAttribute("mode", mode);
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		so.setServiceOrderID(result);
		form.setServiceOrderID(result);
		model.addAttribute("msg", msg);

		// for closed service order
		if(!so.getStatus().equals(ServiceOrder.NEW)){
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
			form.setRealProblem(so.getRealProblem());
			form.setCause(so.getCause());
			form.setFixDesc(so.getFixDesc());
			form.setCosting(so.getCosting());
			
			List<ServiceList> serviceList = serviceListService.getByServiceOrder(so.getServiceOrderID());
			model.addAttribute("serviceList", serviceList);
			
			List<IssuePart> issuePartList = issuePartService.getByServiceOrder(so.getServiceOrderID());
			if(issuePartList.size() > 0){
				form.setIssuePart("haveIssuedPart");
			}else{
				form.setIssuePart("noIssuedPart");
			}
			model.addAttribute("issuePartList", issuePartList);
			form.setNetAmount(so.getTotalPrice());
			form.setRemark(so.getRemark());
		}
		
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Type type = typeList.get(0);
		
		List<Brand> brandList = new ArrayList<Brand>();
		List<Model> modelList = new ArrayList<Model>();
		if (type.getBrands().size() > 0) {
			brandList = type.getBrands();
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

		// get data for customer form
		List<Province> provinceList = provinceService.getAll();
		List<District> districtList = districtService.getByProvince(7);
		// set subdistrict from Muang district
		List<Subdistrict> subdistrictList = sdService.getByDistrict(160);
		
		Subdistrict sd = subdistrictList.get(0);
		
		List<CustomerType> customerTypeList = customerTypeService.getAll();
		
		model.addAttribute("provinceList", provinceList);
		model.addAttribute("districtList", districtList);
		model.addAttribute("subdistrictList", subdistrictList);
		model.addAttribute("zipcode", sd.getZipcode());
		model.addAttribute("customerTypeList", customerTypeList);

		CustomerForm custForm = new CustomerForm();
		custForm.setProvinceID(7);
		// set default district to Muang
		custForm.setDistrictID(160);
		model.addAttribute("customerForm", custForm);

		ProductForm productForm = new ProductForm();
		model.addAttribute("productForm", productForm);
		
		model.addAttribute("product", so.getProduct());
		
		List<Employee> empList = employeeService.getAll();
		
		model.addAttribute("employeeList", empList);
		
		List<OutsiteService> osList = osService.selectByServiceOrderID(so.getServiceOrderID());
		List<OutsiteServiceDisplayForm> osdfList = new ArrayList<OutsiteServiceDisplayForm>();
		for(OutsiteService os : osList){
			OutsiteServiceDisplayForm osdf = new OutsiteServiceDisplayForm();
			osdf.setOutsiteServiceID(os.getOutsiteServiceID());
			osdf.setOutsiteServiceDate(os.getOutsiteServiceDate());
			osdf.setOutsiteServiceDate(null);
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
		
		ModelForm modelForm = new ModelForm();
		model.addAttribute("modelForm", modelForm);
		
		// get data for print document
		ServiceOrderDocForm docForm = setDocPrintForm(so);
		
		if(so.getStatus().equals(ServiceOrder.FIXED)|| so.getStatus().equals(ServiceOrder.CLOSE)){
			if(so.getStartFix() != null){
				docForm.setStartFix(sdf.format(so.getStartFix()));
				docForm.setStartFixTime(sdfTime.format(so.getStartFix()));
			}else{
				docForm.setStartFix("-");
				docForm.setStartFixTime("");
			}
			if(so.getEndFix() != null){
				docForm.setEndFix(sdfDateTime.format(so.getEndFix()));	
			}else{
				docForm.setEndFix("-");
			}		
			docForm.setEmpFix(so.getEmpFix().getName());
			
			docForm.setCosting(so.getCosting());
			docForm.setRealProblem(so.getRealProblem());
			docForm.setCause(so.getCause());
			docForm.setFixDesc(so.getFixDesc());
			docForm.setTotalPrice(so.getTotalPrice());
		}

		model.addAttribute("docForm", docForm);
		if(mode.equals("add")){
			model.addAttribute("action", "print");
		}
		model.addAttribute("mode", "edit");
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}

	@RequestMapping(value = "/serviceOrder", params = "do=preEdit")
	public String preEdit(
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
		form.setCannotMakeContact(so.getCannotMakeContact());
		form.setStatus(so.getStatus());
		form.setRemark(so.getRemark());

		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// for fixing service order
		if(so.getEmpFix() != null){
			form.setEmpFixID(so.getEmpFix().getEmployeeID());
		}
		
		List<Employee> empList = employeeService.getAll();
		
		model.addAttribute("employeeList", empList);
		
		// for closed service order
		if(!so.getStatus().equals(ServiceOrder.NEW)){
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
			form.setRealProblem(so.getRealProblem());
			form.setCause(so.getCause());
			form.setFixDesc(so.getFixDesc());
			form.setCosting(so.getCosting());
			
			List<ServiceList> serviceList = serviceListService.getByServiceOrder(so.getServiceOrderID());
			model.addAttribute("serviceList", serviceList);
			
			List<IssuePart> issuePartList = issuePartService.getByServiceOrder(so.getServiceOrderID());
			if(issuePartList.size() > 0){
				form.setIssuePart("haveIssuedPart");
			}else{
				form.setIssuePart("noIssuedPart");
			}
			model.addAttribute("issuePartList", issuePartList);
			form.setNetAmount(so.getTotalPrice());
		}
		
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

		List<CustomerType> customerTypeList = customerTypeService.getAll();
		model.addAttribute("customerTypeList", customerTypeList);
		
		CustomerForm custForm = new CustomerForm();
		custForm.setProvinceID(7);
		// set default district to Muang
		custForm.setDistrictID(160);
		
		List<Subdistrict> subdistrictList = sdService.getByDistrict(160);
		// set subdistrict from Muang district
		custForm.setSubdistrictID(((Subdistrict)subdistrictList.get(0)).getSubdistrictID());
		custForm.setZipcode(((Subdistrict)subdistrictList.get(0)).getZipcode().toString());
		custForm.setZipcode(((Subdistrict)subdistrictList.get(0)).getZipcode().toString());
		model.addAttribute("customerForm", custForm);

		ServiceOrderDocForm docForm = setDocPrintForm(so);
		
		model.addAttribute("docForm", docForm);

		ProductForm productForm = new ProductForm();
		model.addAttribute("productForm", productForm);
		
		ModelForm modelForm = new ModelForm();
		model.addAttribute("modelForm", modelForm);
		
		model.addAttribute("mode", "edit");
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}

	// Change calling AJAX
	// - Change return CustomGenericResponse to String
	// - Use ObjectMapper to convert CustomGenericResponse object to text and return it.
	@RequestMapping(value = "/serviceOrder", params = "do=delete")
	public @ResponseBody
	String delete(HttpServletRequest request) throws JsonProcessingException {
		Employee user = (Employee) request.getSession().getAttribute(
				"UserLogin");
		CustomGenericResponse response = new CustomGenericResponse();
		response.setSuccess(true);
		try {
			soService.delete(request.getParameter("serviceOrderID"),
					user.getEmployeeID());
			response.setMessage(this.messages.getMessage("msg.deleteSuccess", null, new Locale("th", "TH")));
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage(this.messages.getMessage("error.cannotDelete", null, new Locale("th", "TH")));
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}

	@RequestMapping(value = "/serviceOrder", params = "do=print")
	public String doPrint(@ModelAttribute ServiceOrderDocForm docForm,
			HttpServletRequest request, ModelMap model) {

		Type type = new Type();
		try {
			type = typeService.selectByID(docForm.getTypeID());
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Brand brand = new Brand();
		try {
			brand = brandService.selectByID(Integer.parseInt(docForm
					.getBrandID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Employee empOpen = new Employee();
		try {
			empOpen = empService.selectByID(Integer.parseInt(docForm
					.getEmpOpenID()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		docForm.setType(type.getName());
		docForm.setBrand(brand.getName());
		docForm.setEmpOpen(empOpen.getName() + " " + empOpen.getSurname());

		List<ServiceOrderDocForm> resultList = new ArrayList<ServiceOrderDocForm>();
		resultList.add(docForm);

		model.addAttribute("serviceOrderResultList", resultList);

//		return "serviceOrderDoc";
		
		// test excel view
		
		Map map = new HashMap();
        List wordList = new ArrayList();
        wordList.add("hello");
        wordList.add("world");
        map.put("wordList", wordList);
        
        model.addAttribute("wordList", wordList);
        
        return "serviceOrderDocExcel";
	}
	
	@RequestMapping(value = "/serviceOrder", params = "do=printExcel")
	public String doPrintExcel(@ModelAttribute ServiceOrderDocForm docForm,
			HttpServletRequest request, ModelMap model) {
		ServiceOrder so = soService.selectByID(docForm.getServiceOrderID());
		
		docForm = setDocPrintForm(so);
		
		if(so.getStatus().equals(ServiceOrder.FIXED)|| so.getStatus().equals(ServiceOrder.CLOSE)){
			if(so.getStartFix() != null){
				docForm.setStartFix(sdf.format(so.getStartFix()));
				docForm.setStartFixTime(sdfTime.format(so.getStartFix()));	
			}else{
				docForm.setStartFix("-");
				docForm.setStartFixTime("");
			}
			if(so.getEndFix() != null){
				docForm.setEndFix(sdfDateTime.format(so.getEndFix()));	
			}else{
				docForm.setEndFix("-");
			}
			docForm.setEmpFix(so.getEmpFix().getName());
			
			docForm.setCosting(so.getCosting());
			docForm.setRealProblem(so.getRealProblem());
			docForm.setCause(so.getCause());
			docForm.setFixDesc(so.getFixDesc());
			docForm.setTotalPrice(so.getTotalPrice());
			
			List<IssuePart> issuePartList = new ArrayList<IssuePart>();
			issuePartList = issuePartService.getByServiceOrder(so.getServiceOrderID());
			
			List<ServiceList> serviceList = new ArrayList<ServiceList>();
			serviceList = serviceListService.getByServiceOrder(so.getServiceOrderID());
			
			docForm.setIssuePartList(issuePartList);
			docForm.setServiceList(serviceList);
		}
		
        model.addAttribute("form", docForm);
		return "serviceOrderDocExcel";
	}
	
	@RequestMapping(value = "/serviceOrder", params = "do=printCloseExcel")
	public String doPrintCloseExcel(@ModelAttribute ServiceOrderDocForm docForm,
			HttpServletRequest request, ModelMap model) {
		ServiceOrder so = soService.selectByID(docForm.getServiceOrderID());
		
		docForm = setDocPrintForm(so);
		
		if(so.getStartFix() != null){
			docForm.setStartFix(sdf.format(so.getStartFix()));
			docForm.setStartFixTime(sdfTime.format(so.getStartFix()));	
		}else{
			docForm.setStartFix("-");
			docForm.setStartFixTime("");
		}
		if(so.getEndFix() != null){
			docForm.setEndFix(sdfDateTime.format(so.getEndFix()));	
		}else{
			docForm.setEndFix("-");
		}
		if(so.getEmpFix() != null){
			docForm.setEmpFix(so.getEmpFix().getName());
		}else{
			docForm.setEmpFix("-");
		}
		
		docForm.setCosting(so.getCosting());
		docForm.setRealProblem(so.getRealProblem());
		docForm.setCause(so.getCause());
		docForm.setFixDesc(so.getFixDesc());
		docForm.setTotalPrice(so.getTotalPrice());
		
		List<IssuePart> issuePartList = new ArrayList<IssuePart>();
		issuePartList = issuePartService.getByServiceOrder(so.getServiceOrderID());
		
		List<ServiceList> serviceList = new ArrayList<ServiceList>();
		serviceList = serviceListService.getByServiceOrder(so.getServiceOrderID());
		
		List<OutsiteService> osList = osService.selectByServiceOrderID(so.getServiceOrderID());
		for(OutsiteService os : osList){
			List<OutsiteServiceDetail> osdl = osdService.getByOutsiteService(os.getOutsiteServiceID());
			for(OutsiteServiceDetail osd : osdl){
				if(osd.getType().equals(OutsiteServiceDetail.TYPE_REPAIR)){
					ServiceList sl = new ServiceList();
					sl.setServiceOrder(os.getServiceOrder());
					sl.setServiceName(osd.getDesc());
					sl.setPrice(osd.getPrice());
					serviceList.add(sl);
				}
			}
		}
		
		docForm.setIssuePartList(issuePartList);
		docForm.setServiceList(serviceList);
		
        model.addAttribute("form", docForm);
//		return "closeServiceOrderDocExcel";
        return "closeServiceOrderDocExcelFull";
	}
	
	@RequestMapping(value = "/serviceOrder", params = "do=printReturnExcel")
	public String doPrintReturnExcel(@ModelAttribute ServiceOrderDocForm docForm,
			HttpServletRequest request, ModelMap model) {
		ServiceOrder so = soService.selectByID(docForm.getServiceOrderID());
		
		docForm = setDocPrintForm(so);
		
		if(so.getStartFix() != null){
			docForm.setStartFix(sdf.format(so.getStartFix()));
			docForm.setStartFixTime(sdfTime.format(so.getStartFix()));	
		}else{
			docForm.setStartFix("-");
			docForm.setStartFixTime("");
		}
		if(so.getEndFix() != null){
			docForm.setEndFix(sdfDateTime.format(so.getEndFix()));	
		}else{
			docForm.setEndFix("-");
		}
		docForm.setEmpFix(so.getEmpFix().getName());
		
		docForm.setCosting(so.getCosting());
		docForm.setRealProblem(so.getRealProblem());
		docForm.setCause(so.getCause());
		docForm.setFixDesc(so.getFixDesc());
		docForm.setTotalPrice(so.getTotalPrice());
		docForm.setReturnDate(sdfDateTime.format(so.getReturnDate()));
		
		List<IssuePart> issuePartList = new ArrayList<IssuePart>();
		issuePartList = issuePartService.getByServiceOrder(so.getServiceOrderID());
		
		List<ServiceList> serviceList = new ArrayList<ServiceList>();
		serviceList = serviceListService.getByServiceOrder(so.getServiceOrderID());
		
		docForm.setIssuePartList(issuePartList);
		docForm.setServiceList(serviceList);
		
        model.addAttribute("form", docForm);
		return "returnServiceOrderDocExcel";
	}
	
	private ServiceOrderDocForm setDocPrintForm(ServiceOrder so){
		ServiceOrderDocForm docForm = new ServiceOrderDocForm();
		docForm.setServiceOrderID(so.getServiceOrderID());
		docForm.setServiceOrderDate(sdf.format(so.getServiceOrderDate()));
		docForm.setServiceOrderTime(sdfTime.format(so.getServiceOrderDate()));
		docForm.setAppointmentDate(sdfDateTime.format(so.getServiceOrderDate()));
		docForm.setServiceType(so.getServiceType());
		docForm.setServiceType(so.getServiceType());
		if(so.getServiceType() == 1){
			docForm.setGuaranteeNo(so.getGuaranteeNo());
		}
		if(so.getServiceType() == 4){
			docForm.setRefJobID(so.getRefJobID());
		}
		if(so.getServiceType() == 5){
			docForm.setRefServiceOrder(so.getRefServiceOrder());
		}
		
		docForm.setCustomerID(so.getCustomer().getCustomerID());
		docForm.setName(so.getCustomer().getName());
		docForm.setEmail(so.getCustomer().getEmail());
		docForm.setTel(so.getCustomer().getTel());
		docForm.setMobileTel(so.getCustomer().getMobileTel());
		docForm.setAddress(so.getCustomer().getAddress());
		docForm.setSubdistrict(so.getCustomer().getSubdistrict().getName());
		docForm.setDistrict(so.getCustomer().getDistrict().getName());
		docForm.setProvince(so.getCustomer().getProvince().getName());
		try{
			docForm.setZipcode(so.getCustomer().getZipcode().toString());
		}catch(Exception e){
			docForm.setZipcode("");
		}
		docForm.setDeliveryCustomer(so.getDeliveryCustomer());
		docForm.setDeliveryEmail(so.getDeliveryEmail());
		docForm.setDeliveryMobileTel(so.getDeliveryMobileTel());
		docForm.setDeliveryTel(so.getDeliveryTel());
		docForm.setTypeID(so.getProduct().getType().getTypeID().toString());
		docForm.setType(so.getProduct().getType().getName());
		docForm.setBrandID(so.getProduct().getBrand().getBrandID().toString());
		docForm.setBrand(so.getProduct().getBrand().getName());
		docForm.setModel(so.getProduct().getModel().getName());
		docForm.setSerialNo(so.getProduct().getSerialNo());
		if(so.getProduct().getWarrantyDate() != null){
			docForm.setWarrantyDate(sdf.format(so.getProduct().getWarrantyDate()));
		}
		if(so.getProduct().getWarrantyExpire() != null){
			docForm.setWarrantyExpire(sdf.format(so.getProduct().getWarrantyExpire()));
		}
		docForm.setAccessories(so.getAccessories());
		docForm.setDesc(so.getDescription());
		docForm.setProblem(so.getProblem());
		docForm.setEmpOpenID(so.getEmpOpen().getEmployeeID().toString());
		docForm.setEmpOpen(so.getEmpOpen().getName() + " "
				+ so.getEmpOpen().getSurname());
		docForm.setStatus(so.getStatus());
		
		return docForm; 
	}
}
