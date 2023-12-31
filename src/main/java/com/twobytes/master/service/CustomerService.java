package com.twobytes.master.service;

import java.util.Map;

import com.twobytes.model.Customer;

public interface CustomerService {
	public String save(Customer customer) throws Exception;
	public Customer selectByID(String customerID) throws Exception;
	public Map<String, Object> selectByCriteria(String name, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public Map<String, Object> selectByCriteriaNameMobileTel(String name, String mobileTel, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public Map<String, Object> selectByCriteriaNameTelMobileTel(String name, String tel, String mobileTel, Integer rows, Integer page, String orderBy, String orderType) throws Exception;
	public boolean edit(Customer customer) throws Exception;
	public boolean delete(String customerID) throws Exception;
}
