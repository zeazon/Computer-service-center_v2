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
import com.twobytes.master.service.SubdistrictService;
import com.twobytes.model.Subdistrict;
import com.twobytes.util.StringUtility;

@Controller
public class SubdistrictController {
	
	@Autowired
	private SubdistrictService sdService;
	
	@Autowired
	private StringUtility stringUtility;
	
	private static final Logger logger = LoggerFactory
	.getLogger(SubdistrictController.class);
	
	// Change calling AJAX
	// - Change return List<Subdistrict> to String
	// - Use ObjectMapper to convert List<Subdistrict> object to text and return it.
	@RequestMapping(value = "/findSubdistrict", method = RequestMethod.GET)
	public @ResponseBody
	String subdistrictForDistrict(
			@RequestParam(value = "districtID", required = true) Integer districtID) throws JsonProcessingException {
		logger.debug("finding subdistrict for districtID " + districtID);
//		return this.sdService.getByDistrict(districtID);
		ObjectMapper mapper = new ObjectMapper();
//		return mapper.writeValueAsString(this.sdService.getByDistrict(districtID));
		
		// If data is thai change encode from UTF-8 to ISO-8859-1 for display thai
		List<Subdistrict> retList = new ArrayList<Subdistrict>();
		List<Subdistrict> subdistrictList = this.sdService.getByDistrict(districtID);
		for(int i=0; i<subdistrictList.size(); i++) {
			Subdistrict subdistrict = subdistrictList.get(i);
			Subdistrict dataSubdistrict = new Subdistrict();
			dataSubdistrict.setSubdistrictID(subdistrict.getSubdistrictID());
			dataSubdistrict.setName(stringUtility.convertUTF8ToISO_8859_1(subdistrict.getName()));
			dataSubdistrict.setDistrict(subdistrict.getDistrict());
			dataSubdistrict.setZipcode(subdistrict.getZipcode());
			
			retList.add(dataSubdistrict);
		}
		return mapper.writeValueAsString(retList);
	}
	
	// Change calling AJAX
	// - Change return Integer to String
	@RequestMapping(value = "/findZipcode", method = RequestMethod.GET)
	public @ResponseBody String findZipcode(@RequestParam Integer subdistrictID){
		if(subdistrictID != null){
			Subdistrict sd = sdService.selectByID(subdistrictID);
			return sd.getZipcode().toString();
		}else return "0";
	}
}
