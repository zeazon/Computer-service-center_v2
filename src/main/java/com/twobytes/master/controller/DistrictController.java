package com.twobytes.master.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twobytes.master.service.DistrictService;
import com.twobytes.model.District;
import com.twobytes.util.StringUtility;

@Controller
public class DistrictController {

	@Autowired
	private DistrictService districtService;

	@Autowired
	private StringUtility stringUtility;
	
	private static final Logger logger = LoggerFactory
	.getLogger(DistrictController.class);
	
	// Change calling AJAX
	// - Change return List<District> to String
	// - Use ObjectMapper to convert List<District> object to text and return it.
	@RequestMapping(value = "/findDistrit", method = RequestMethod.GET)
	public @ResponseBody
	String districtForProvince(
			@RequestParam(value = "provinceID", required = true) Integer provinceID) throws JsonProcessingException {
		logger.debug("finding district for provinceID " + provinceID);
//		return this.districtService.getByProvince(provinceID);
		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsString(this.districtService.getByProvince(provinceID));
		
		// If data is thai change encode from UTF-8 to ISO-8859-1 for display thai
		List<District> retList = new ArrayList<District>();
		List<District> districtList = this.districtService.getByProvince(provinceID);
		for(int i=0; i<districtList.size(); i++) {
			District district = districtList.get(i);
			District dataDistrict = new District();
			dataDistrict.setDistrictID(district.getDistrictID());
			dataDistrict.setName(stringUtility.convertUTF8ToISO_8859_1(district.getName()));
			dataDistrict.setProvince(district.getProvince());
			
			retList.add(dataDistrict);
		}
		return mapper.writeValueAsString(retList);
	}
}
