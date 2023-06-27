package com.twobytes.repair.controller;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twobytes.model.Customer;
import com.twobytes.model.GridResponse;
import com.twobytes.model.ServiceOrder;
import com.twobytes.repair.form.ServiceOrderGridData;
import com.twobytes.repair.service.ServiceOrderService;

@RestController
public class AjaxController {

	@Autowired
	private ServiceOrderService soService;
	
	@Autowired
	private MessageSource messages;
	
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	private SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale ( "US" ));
	
	// Change calling AJAX
	// - Change return GridResponse to String
	// - Use ObjectMapper to convert GridResponse object to text and return it.
	//public 	GridResponse getData(

	//@RequestMapping(value="/searchAjaxGetServiceOrder", produces = "application/json")
	//@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value={"/searchAjaxGetServiceOrder"},method=RequestMethod.POST,headers="Accept=*/*",produces = "application/json")
	public 	@ResponseBody String getData(
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
		System.out.println("AjaxController: getData");
		// Because default Tomcat URI encoding is iso-8859-1 so it must encode
		// back to tis620
		try {
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
		if (soList.size() > 0) {
			for(ServiceOrder so:soList){
				ServiceOrderGridData gridData = new ServiceOrderGridData();
				gridData.setServiceOrderID(so.getServiceOrderID());
				gridData.setServiceOrderDate(sdfDateTime.format(so
						.getServiceOrderDate()));
				String serviceType = "";
				
				if (so.getServiceType() == 1) {
					serviceType = this.messages.getMessage(
							"serviceOrderType_guarantee", null, new Locale("th", "TH"));
					if(so.getGuaranteeNo() != null){
						serviceType = serviceType + " " + this.messages.getMessage("guarantee_No", null, new Locale("th", "TH")) + " " + so.getGuaranteeNo().toString();
					}
				} else if (so.getServiceType() == 2) {
					serviceType = this.messages.getMessage(
							"serviceOrderType_repair", null, new Locale("th", "TH"));
				} else if (so.getServiceType() == 3) {
					serviceType = this.messages.getMessage(
							"serviceOrderType_claim", null, new Locale("th", "TH"));
				} else if (so.getServiceType() == 4) {
					serviceType = this.messages.getMessage(
							"serviceOrderType_outsiteService", null, new Locale("th", "TH")) + " " +
							this.messages.getMessage("reference", null, new Locale("th", "TH")) + " " +
							so.getRefJobID();
				} else if (so.getServiceType() == 5) {
					serviceType = this.messages.getMessage(
							"serviceOrderType_refix", null, new Locale("th", "TH"))+ " " +
							this.messages.getMessage("reference", null, new Locale("th", "TH")) + " " +
							so.getRefServiceOrder();
				}
				gridData.setServiceType(serviceType);
				try{
					gridData.setAppointmentDate(sdfDateTime.format(so.getAppointmentDate()));
				}catch(NullPointerException npe){
					gridData.setAppointmentDate("-");
				}
				Customer customer = so.getCustomer();
				gridData.setName(customer.getName());
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
				gridData.setDeliveryCustomer(so.getDeliveryCustomer());
				gridData.setDeliveryEmail(so.getDeliveryEmail());
				gridData.setDeliveryTel(so.getDeliveryTel());
				gridData.setDeliveryMobileTel(so.getDeliveryMobileTel());
				gridData.setStatus(so.getStatus());
				gridData.setProductID(so.getProduct().getProductID());
				gridData.setTypeID(so.getProduct().getType().getTypeID());
				gridData.setType(so.getProduct().getType().getName());
				gridData.setBrandID(so.getProduct().getBrand().getBrandID());
				gridData.setBrand(so.getProduct().getBrand().getName());
				gridData.setModelID(so.getProduct().getModel().getModelID());
				gridData.setModel(so.getProduct().getModel().getName());
				gridData.setSerialNo(so.getProduct().getSerialNo());
				gridData.setAccessories(so.getAccessories());
				gridData.setProblem(so.getProblem());
				gridData.setDescription(so.getDescription());
				gridData.setEmpOpen(so.getEmpOpen().getName() + " "
						+ so.getEmpOpen().getSurname());

				if(so.getEmpFix() != null){
					gridData.setEmpFix(so.getEmpFix().getName() + " "
						+ so.getEmpFix().getSurname());
				}
				gridData.setCannotMakeContact(so.getCannotMakeContact());
				
				rowsList.add(gridData);
			}
			total_pages = new Double(Math.ceil(((double)(Long) ret.get("maxRows")/(double)rows))).intValue();
		}
		if (page > total_pages) page=total_pages;
		response.setPage(page.toString());
		response.setRecords(((Long) ret.get("maxRows")).toString());
		response.setTotal(total_pages.toString());
		response.setRows(rowsList);

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
		
		//return response;
	}
	
}
