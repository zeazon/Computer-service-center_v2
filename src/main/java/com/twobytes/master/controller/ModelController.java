package com.twobytes.master.controller;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.twobytes.master.form.ModelForm;
import com.twobytes.master.form.ModelGridData;
import com.twobytes.master.form.ModelSearchForm;
import com.twobytes.master.service.BrandService;
import com.twobytes.master.service.ModelService;
import com.twobytes.master.service.TypeService;
import com.twobytes.model.Brand;
import com.twobytes.model.CustomGenericResponse;
import com.twobytes.model.Employee;
import com.twobytes.model.GridResponse;
import com.twobytes.model.Model;
import com.twobytes.model.Type;
import com.twobytes.security.form.LoginForm;
import com.twobytes.util.DevicePage;
import com.twobytes.util.StringUtility;

@Controller
public class ModelController {

	@Autowired
	private ModelService modelService;
	
	@Autowired
	private TypeService typeService;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private StringUtility stringUtility;
	
	@Autowired
	private DevicePage dp;
	
	@Autowired
	private MessageSource messages;
	
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	private static final Logger logger = LoggerFactory
	.getLogger(ModelController.class);
	
	private String VIEWNAME_SEARCH = "model.search";
	private String VIEWNAME_FORM = "model.form";
	private String VIEWNAME_M_SEARCH = "model_m.search";
	private String VIEWNAME_M_FORM = "model_m.form";
	
	@RequestMapping(value = "/model")
	public String view(ModelMap model, HttpServletRequest request, Device device) {
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		ModelSearchForm searchForm = new ModelSearchForm();
		model.addAttribute("searchForm", searchForm);
		
		List<Brand> brandList = new ArrayList<Brand>();
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("typeList", typeList);
		model.addAttribute("brandList", brandList);
		
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	// Change calling AJAX
	// - Change return GridResponse to String
	// - Use ObjectMapper to convert GridResponse object to text and return it.
	@RequestMapping(value="/searchModel")
	public @ResponseBody String getData(@RequestParam(value="name", required=false) String name, @RequestParam(value="typeID", required=false) String typeID, @RequestParam(value="brandID", required=false) String brandID, @RequestParam("rows") Integer rows, @RequestParam("page") Integer page, @RequestParam("sidx") String sidx, @RequestParam("sord") String sord) throws JsonProcessingException{
		// Because default Tomcat URI encoding is iso-8859-1 so it must encode back to tis620
		
		// Because since tomcat 8 URI encoding is UTF-8 and set jsp charset to UTF-8 so it don't encode back to tis620
/*		try{
			if(null != name){
				name = new String(name.getBytes("iso-8859-1"), "tis620");	
			}
			if(null != typeID){
				typeID = new String(typeID.getBytes("iso-8859-1"), "tis620");	
			}
			if(null != brandID){
				brandID = new String(brandID.getBytes("iso-8859-1"), "tis620");	
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
*/
		
		List<Model> modelList = new ArrayList<Model>();
		Integer brandCri = null;
		if(null != brandID && !brandID.equals("")){
			brandCri = Integer.valueOf(brandID);
		}
		modelList = modelService.selectByCriteria(name, typeID, brandCri, rows, page, sidx, sord);
		
		GridResponse response = new GridResponse();
		
		List<ModelGridData> rowsList = new ArrayList<ModelGridData>();
		
		Integer total_pages = 0;
		if(modelList.size() > 0){
			int endData = 0;
			if(modelList.size() < (rows*page)){
				endData = modelList.size();
			}else{
				endData = (rows*page);
			}
			// If data is thai change encode from UTF-8 to ISO-8859-1 for display thai in grid
			for(int i=(rows*page - rows); i<endData; i++){
				Model model = modelList.get(i);
				ModelGridData gridData = new ModelGridData();
				gridData.setModelID(model.getModelID().toString());
//				gridData.setName(model.getName());
				gridData.setName(stringUtility.convertUTF8ToISO_8859_1(model.getName()));
//				gridData.setTypeName(model.getType().getName());
				gridData.setTypeName(stringUtility.convertUTF8ToISO_8859_1(model.getType().getName()));
//				gridData.setBrandName(model.getBrand().getName());
				gridData.setBrandName(stringUtility.convertUTF8ToISO_8859_1(model.getBrand().getName()));
				rowsList.add(gridData);
			}
			total_pages = new Double(Math.ceil(((double)modelList.size()/(double)rows))).intValue();
		}
		
		if (page > total_pages) page=total_pages;
		response.setPage(page.toString());
		response.setRecords(String.valueOf(modelList.size()));
		response.setTotal(total_pages.toString());
		response.setRows(rowsList);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
	@RequestMapping(value = "/model", params = "do=preAdd")
	public String preAdd(ModelMap model, HttpServletRequest request, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		ModelForm form = new ModelForm();

		List<Brand> brandList = new ArrayList<Brand>();
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(typeList.size() > 0){
			Type type = typeList.get(0);
			brandList = type.getBrands();
			form.setTypeID(type.getTypeID());
		}
		if(brandList.size() == 0){
			Brand brand = new Brand();
			brand.setBrandID(null);
			brand.setName(" ");
			brandList.add(brand);
		}else{
			Brand brand = brandList.get(0);
			form.setBrandID(brand.getBrandID());
		}
		
		model.addAttribute("form", form);
		model.addAttribute("typeList", typeList);
		model.addAttribute("brandList", brandList);
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	@RequestMapping(value = "/model", params = "do=save")
	public String doSave(@ModelAttribute("form") ModelForm form, HttpServletRequest request, ModelMap model, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Date now = new Date();
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		Model modelObj = new Model();
		String msg = "";
		if(null != form.getModelID()){
			// update
			try{
				modelObj = modelService.selectByID(form.getModelID());
			}catch(Exception e){
				e.printStackTrace();
				model.addAttribute("errMsg", e.getMessage());
				
				List<Brand> brandList = new ArrayList<Brand>();
				List<Type> typeList = new ArrayList<Type>();
				try {
					typeList = typeService.getAll();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				if(typeList.size() > 0){
					try {
						Type type = typeService.selectByID(form.getTypeID());
						brandList = type.getBrands();
					}catch(Exception e2){
						e2.printStackTrace();
					}
				}
				if(brandList.size() == 0){
					Brand brand = new Brand();
					brand.setBrandID(null);
					brand.setName(" ");
					brandList.add(brand);
				}
				model.addAttribute("typeList", typeList);
				model.addAttribute("brandList", brandList);
				return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
			}
			msg = this.messages.getMessage("msg.updateComplete", null, new Locale("th", "TH"));
		}else{
			// add
			modelObj.setCreatedBy(user.getEmployeeID());
			modelObj.setCreatedDate(now);
			msg = this.messages.getMessage("msg.addComplete", null, new Locale("th", "TH"));
		}
		modelObj.setName(form.getName());
		
		Type type = new Type();
		try {
			type = typeService.selectByID(form.getTypeID());
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		modelObj.setType(type);
		
		Brand brand = new Brand();
		try {
			brand = brandService.selectByID(form.getBrandID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		modelObj.setBrand(brand);
		modelObj.setUpdatedBy(user.getEmployeeID());
		modelObj.setUpdatedDate(now);
		
		try{
			modelService.save(modelObj);
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("errMsg", e.getMessage());
			
			List<Brand> brandList = new ArrayList<Brand>();
			List<Type> typeList = new ArrayList<Type>();
			try {
				typeList = typeService.getAll();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			if(typeList.size() > 0){
				try {
					Type type2 = typeService.selectByID(form.getTypeID());
					brandList = type2.getBrands();
				}catch(Exception e2){
					e2.printStackTrace();
				}
			}
			if(brandList.size() == 0){
				Brand brand2 = new Brand();
				brand2.setBrandID(null);
				brand2.setName(" ");
				brandList.add(brand2);
			}
			model.addAttribute("typeList", typeList);
			model.addAttribute("brandList", brandList);
			return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
		}
		
		model.addAttribute("msg", msg);
		ModelSearchForm searchForm = new ModelSearchForm();
		model.addAttribute("searchForm", searchForm);
		
		List<Brand> brandList = new ArrayList<Brand>();
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addAttribute("typeList", typeList);
		model.addAttribute("brandList", brandList);
		
		return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
	}
	
	@RequestMapping(value = "/model", params = "do=preEdit")
	public String preEdit(@RequestParam(value="modelID") Integer modelID, ModelMap model, HttpServletRequest request, Device device){
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			return dp.page(device, "loginScreen", "loginScreen_m");
		}
		Model modelObj = new Model();
		modelObj = modelService.selectByID(modelID);
		if(modelObj.equals(new Model())){
			model.addAttribute("errMsg", "Cannot find data");
			ModelSearchForm searchForm = new ModelSearchForm();
			model.addAttribute("searchForm", searchForm);
			return dp.page(device, VIEWNAME_SEARCH, VIEWNAME_M_SEARCH);
		}
		ModelForm form = new ModelForm();
		form.setModelID(modelObj.getModelID());
		form.setName(modelObj.getName());

		form.setTypeID(modelObj.getType().getTypeID());
		form.setBrandID(modelObj.getBrand().getBrandID());
		
		List<Brand> brandList = new ArrayList<Brand>();
		List<Type> typeList = new ArrayList<Type>();
		try {
			typeList = typeService.getAll();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if(typeList.size() > 0){
			try {
				Type type = typeService.selectByID(form.getTypeID());
				brandList = type.getBrands();
			}catch(Exception e2){
				e2.printStackTrace();
			}
		}
		if(brandList.size() == 0){
			Brand brand = new Brand();
			brand.setBrandID(null);
			brand.setName(" ");
			brandList.add(brand);
		}
		model.addAttribute("typeList", typeList);
		model.addAttribute("brandList", brandList);
		model.addAttribute("form", form);
		return dp.page(device, VIEWNAME_FORM, VIEWNAME_M_FORM);
	}
	
	// Change calling AJAX
	// - Change return CustomGenericResponse to String
	// - Use ObjectMapper to convert CustomGenericResponse object to text and return it.
	@RequestMapping(value = "/model", params = "do=delete")
	public @ResponseBody String delete(HttpServletRequest request) throws JsonProcessingException{
		CustomGenericResponse response = new CustomGenericResponse();
		response.setSuccess(true);
		try{
			modelService.delete(Integer.valueOf(request.getParameter("modelID")));
			response.setMessage(this.messages.getMessage("msg.deleteSuccess", null, new Locale("th", "TH")));
		}catch(Exception e){
			e.printStackTrace();
			response.setSuccess(false);
			response.setMessage(this.messages.getMessage("error.cannotDelete", null, new Locale("th", "TH")));
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(response);
	}
	
	// Change calling AJAX
	// - Change return List<Model> to String
	// - Use ObjectMapper to convert List<Model> object to text and return it.
	@RequestMapping(value="/model", params = "do=getModel")
	public @ResponseBody String getModelByTypeAndBrand(@RequestParam(value="typeID") String typeID, @RequestParam(value="brandID") String brandID) throws JsonProcessingException{
		List<Model> retList = new ArrayList<Model>();
		ObjectMapper mapper = new ObjectMapper();
		
		Integer brandCri = null;
		if(null != brandID && !brandID.equals("")){
			brandCri = Integer.valueOf(brandID);
			retList = modelService.getModelByTypeAndBrand(typeID, brandCri);
		}
		
		return mapper.writeValueAsString(retList);
	}
	
	// Change calling AJAX
	// - Change return CustomGenericResponse to String
	// - Use ObjectMapper to convert CustomGenericResponse object to text and return it.
	@RequestMapping(value = "/model", params = "do=savePopup")
	public @ResponseBody String doSavePopup(@ModelAttribute ModelForm form, HttpServletRequest request, ModelMap model) throws JsonProcessingException{
		CustomGenericResponse response = new CustomGenericResponse();
		ObjectMapper mapper = new ObjectMapper();
		
		if(null == request.getSession().getAttribute("UserLogin")){
			LoginForm loginForm = new LoginForm();
			model.addAttribute(loginForm);
			response.setSuccess(false);
			response.setMessage(this.messages.getMessage("error.cannotSave", null, new Locale("th", "TH")));
			return mapper.writeValueAsString(response);
		}
		
		// Because default Tomcat URI encoding is iso-8859-1 so it must encode back to tis620
		
		// Because since tomcat 8 URI encoding is UTF-8 and set jsp charset to UTF-8 so it don't encode back to tis620
/*		try{
			if(null != form.getName()){
//				form.setDescription(new String(form.getDescription().getBytes("iso-8859-1"), "tis620"));
				form.setName(new String(form.getName().getBytes("iso-8859-1"), "UTF-8"));
			}
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
*/
		
		Date now = new Date();
		Employee user = (Employee)request.getSession().getAttribute("UserLogin");
		Model modelObj = new Model();
		
		modelObj.setCreatedBy(user.getEmployeeID());
		modelObj.setCreatedDate(now);
		
		modelObj.setName(form.getName());
		
		Type type = new Type();
		try {
			type = typeService.selectByID(form.getTypeID());
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		modelObj.setType(type);
		
		Brand brand = new Brand();
		try {
			brand = brandService.selectByID(form.getBrandID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		modelObj.setBrand(brand);
		modelObj.setUpdatedBy(user.getEmployeeID());
		modelObj.setUpdatedDate(now);
		
		try{
			modelService.save(modelObj);
			response.setSuccess(true);
			response.setMessage(this.messages.getMessage("msg.addComplete", null, new Locale("th", "TH")));
			response.setData(form.getName());
		}catch(Exception e){
			e.printStackTrace();
			response.setSuccess(false);
			response.setMessage(this.messages.getMessage("error.cannotSave", null, new Locale("th", "TH")));
			return mapper.writeValueAsString(response);
		}
		return mapper.writeValueAsString(response);
	}
	
}
