package com.twobytes.master.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twobytes.master.dao.CustomerDAO;
import com.twobytes.model.Customer;
import com.twobytes.util.DocRunningUtil;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerDAO customerDAO;
	
	@Autowired
	private DocRunningUtil docRunningUtil;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String save(Customer customer) throws Exception{
		if (customer.getCustomerID() == null) {
			String customerID = docRunningUtil.genDoc("customer");
			customer.setCustomerID(customerID);
		}
		if(customerDAO.save(customer)){
			return customer.getCustomerID();
		}else{
			return "false";
		}
	}

	@Override
	@Transactional
	public Customer selectByID(String customerID) throws Exception{
		return customerDAO.selectByID(customerID);
	}

	@Override
	@Transactional
	public Map<String, Object> selectByCriteria(String name,
			Integer rows, Integer page, String orderBy, String orderType) throws Exception{
		if(null != name && !name.equals("")) {
			name = "%"+name+"%";
		}
		return customerDAO.selectByCriteria(name, rows, page, orderBy, orderType);
	}
	
	@Override
	@Transactional
	public Map<String, Object> selectByCriteriaNameMobileTel(String name, String mobileTel, Integer rows, Integer page,
			String orderBy, String orderType) throws Exception {
		if(null != name && !name.equals("")) {
			name = "%"+name+"%";
		}
		if(null != mobileTel && !mobileTel.equals("")) {
			mobileTel = "%"+mobileTel+"%";
		}
		return customerDAO.selectByCriteriaNameMobileTel(name, mobileTel, rows, page, orderBy, orderType);
	}
	
	@Override
	@Transactional
	public Map<String, Object> selectByCriteriaNameTelMobileTel(String name, String tel, String mobileTel, Integer rows, Integer page,
			String orderBy, String orderType) throws Exception {
		if(null != name && !name.equals("")) {
			name = "%"+name+"%";
		}
		if(null != tel && !tel.equals("")) {
			tel = "%"+tel+"%";
		}
		if(null != mobileTel && !mobileTel.equals("")) {
			mobileTel = "%"+mobileTel+"%";
		}
		return customerDAO.selectByCriteriaNameTelMobileTel(name, tel, mobileTel, rows, page, orderBy, orderType);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean edit(Customer customer) throws Exception{
		return customerDAO.edit(customer);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean delete(String customerID) throws Exception{
		Customer customer = customerDAO.selectByID(customerID);
		if(null != customer){
			return customerDAO.delete(customer);
		}else{
			return false;
		}
	}

}
