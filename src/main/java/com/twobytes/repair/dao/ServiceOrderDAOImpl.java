package com.twobytes.repair.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.twobytes.model.IssuePart;
import com.twobytes.model.ServiceOrder;
import com.twobytes.report.form.NumRepairByEmpReportForm;
import com.twobytes.report.form.NumRepairReportForm;
import com.twobytes.report.form.SumAmountReportForm;

@Repository
public class ServiceOrderDAOImpl implements ServiceOrderDAO {

	@Autowired
	private SessionFactory sessionFactory;

	private SimpleDateFormat sdfDateSQL = new SimpleDateFormat(
			"yyyy-MM-dd", new Locale("US"));
	private SimpleDateFormat sdfDate = new SimpleDateFormat(
			"dd/MM/yyyy", new Locale("US"));
	
	@Override
	public boolean save(ServiceOrder serviceOrder) throws Exception{
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(serviceOrder);
		return true;
	}

	@Override
	public ServiceOrder selectByID(String serviceOrderID) throws Exception{
		ServiceOrder so = new ServiceOrder();
		so = (ServiceOrder)sessionFactory.getCurrentSession().get(ServiceOrder.class, serviceOrderID);
		return so;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceOrder> selectByCriteria(String name,
			String startDate, String endDate, String type, String serialNo,
			Integer rows, Integer page, String orderBy, String orderType) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and serviceOrder.product.type.typeID = :type ");
		}
		if(null != serialNo && !serialNo.equals("")){
			sql.append("and serviceOrder.product.serialNo like :serialNo ");
		}
		
		sql.append("and serviceOrder.status != 'cancel' ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("surname")){
				orderBy = "serviceOrder.customer.surname";
			}else if(orderBy.equals("fullName")){
				orderBy = "serviceOrder.customer.name "+orderType+", serviceOrder.customer.surname"; 
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString()).setFetchSize(rows);
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
//		if(null != surname && !surname.equals("")) {
//			q.setString("surname", surname);
//		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		if(null != serialNo && !serialNo.equals("")) {
			q.setString("serialNo", serialNo);
		}
		List<ServiceOrder> result = q.list();
		return result;
	}
	
	/*@Override
	@SuppressWarnings("unchecked")
	public List<ServiceOrder> selectByCriteria(String name, String startDate,
			String endDate, String type, String serialNo, String empID,
			Integer rows, Integer page, String orderBy, String orderType)
			throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and serviceOrder.product.type.typeID = :type ");
		}
		if(null != serialNo && !serialNo.equals("")){
			sql.append("and serviceOrder.product.serialNo like :serialNo ");
		}
		if(null != empID && !empID.equals("")){
			sql.append("and empFix = :empID ");
		}
		
		sql.append("and serviceOrder.status != 'cancel' ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("surname")){
				orderBy = "serviceOrder.customer.surname";
			}else if(orderBy.equals("fullName")){
				orderBy = "serviceOrder.customer.name "+orderType+", serviceOrder.customer.surname"; 
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString()).setFetchSize(rows);
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		if(null != serialNo && !serialNo.equals("")) {
			q.setString("serialNo", serialNo);
		}
		if(null != empID && !empID.equals("")){
			q.setInteger("empID", Integer.parseInt(empID));
		}
		List<ServiceOrder> result = q.list();
		return result;
	}*/

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectByCriteria(String name, String startDate,
			String endDate, String type, String serialNo, String empID,
			Integer rows, Integer page, String orderBy, String orderType)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale ("US"));
		
		StringBuilder sql = new StringBuilder();
		sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and serviceOrder.product.type.typeID = :type ");
		}
		if(null != serialNo && !serialNo.equals("")){
			sql.append("and serviceOrder.product.serialNo like :serialNo ");
		}
		if(null != empID && !empID.equals("")){
			sql.append("and empFix = :empID ");
		}
		
		sql.append("and serviceOrder.status != 'cancel' ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("surname")){
				orderBy = "serviceOrder.customer.surname";
			}else if(orderBy.equals("fullName")){
				orderBy = "serviceOrder.customer.name "+orderType+", serviceOrder.customer.surname"; 
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString()).setFirstResult(rows*page - rows).setMaxResults(rows).setFetchSize(rows);
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		if(null != serialNo && !serialNo.equals("")) {
			q.setString("serialNo", serialNo);
		}
		if(null != empID && !empID.equals("")){
			q.setInteger("empID", Integer.parseInt(empID));
		}
		List<ServiceOrder> list = q.list();
		result.put("list", list);
		
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ServiceOrder.class, "serviceOrder");
		
		if(null != name && !name.equals("")) {
			criteria.createCriteria("serviceOrder.customer" , "customer");
			criteria.add(Restrictions.like("customer.name", name));
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			/*
			 * Because endDate field in database is timestamp and hibernate don't use Date function to compare date.
			 * So it'll make mistake to compare because timestamp will compare time too and time to compare is oo:oo.
			 * For correct compare endDate it must add end date by 1 day and use this date as end date.
			 */
			Calendar endDateCal = Calendar.getInstance();
			endDateCal.setTime(sdf.parse(endDate));
			endDateCal.add(Calendar.DATE, 1);
			criteria.add(Restrictions.between("serviceOrderDate", sdf.parse(startDate), endDateCal.getTime()));
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			criteria.add(Restrictions.ge("serviceOrderDate", sdf.parse(startDate)));
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			/*
			 * Because endDate field in database is timestamp and hibernate don't use Date function to compare date.
			 * So it'll make mistake to compare because timestamp will compare time too and time to compare is oo:oo.
			 * For correct compare endDate it must add end date by 1 day and use this date as end date.
			 */
			Calendar endDateCal = Calendar.getInstance();
			endDateCal.setTime(sdf.parse(endDate));
			endDateCal.add(Calendar.DATE, 1);
			criteria.add(Restrictions.lt("serviceOrderDate", endDateCal.getTime()));
		}
		if(null != type && !type.equals("")){
			criteria.createCriteria("serviceOrder.product", "product");
			criteria.add(Restrictions.eq("product.type.typeID", type));
		}
		if(null != serialNo && !serialNo.equals("")){
			if(null == type || type.equals("")){
				criteria.createCriteria("serviceOrder.product", "product");
			}
			criteria.add(Restrictions.like("product.serialNo", serialNo));
		}
		if(null != empID && !empID.equals("")){
			criteria.createCriteria("serviceOrder.empFix", "empFix");
			criteria.add(Restrictions.eq("empFix.employeeID", Integer.parseInt(empID)));
		}
		
		criteria.add(Restrictions.ne("status", "cancel"));
		criteria.setProjection(Projections.rowCount());
		
		result.put("maxRows", criteria.list().get(0));
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectByCriteria2(String serviceOrderID, String name, String mobileTel, String startDate,
			String endDate, String type, String serialNo, String empID, String issuePartCode, Integer rows, Integer page, String orderBy,
			String orderType) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale ("US"));
		
		StringBuilder sql = new StringBuilder();
//		sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		
		if(null != issuePartCode && !issuePartCode.equals("")) {
			sql.append("select serviceOrder from ServiceOrder as serviceOrder, IssuePart as issuePart where serviceOrder.serviceOrderID=issuePart.serviceOrder.serviceOrderID ");
		}else {
			sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		}
		
		if(null != serviceOrderID && !serviceOrderID.equals("")){
			sql.append("and serviceOrder.serviceOrderID = :serviceOrderID ");
		}
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}
		if(null != mobileTel && !mobileTel.equals("")){
			sql.append("and serviceOrder.customer.mobileTel = :mobileTel ");
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and serviceOrder.product.type.typeID = :type ");
		}
		if(null != serialNo && !serialNo.equals("")){
			sql.append("and serviceOrder.product.serialNo like :serialNo ");
		}
		if(null != empID && !empID.equals("")){
			sql.append("and empFix = :empID ");
		}
		if(null != issuePartCode && !issuePartCode.equals("")) {
			sql.append("and issuePart.code like :issuePartCode ");
		}
		
		sql.append("and serviceOrder.status != 'cancel' ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("surname")){
				orderBy = "serviceOrder.customer.surname";
			}else if(orderBy.equals("fullName")){
				orderBy = "serviceOrder.customer.name "+orderType+", serviceOrder.customer.surname"; 
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString()).setFirstResult(rows*page - rows).setMaxResults(rows).setFetchSize(rows);
		if(null != serviceOrderID && !serviceOrderID.equals("")) {
			q.setString("serviceOrderID", serviceOrderID);
		}
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
		if(null != mobileTel && !mobileTel.equals("")) {
			q.setString("mobileTel", mobileTel);
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		if(null != serialNo && !serialNo.equals("")) {
			q.setString("serialNo", serialNo);
		}
		if(null != empID && !empID.equals("")){
			q.setInteger("empID", Integer.parseInt(empID));
		}
		if(null != issuePartCode && !issuePartCode.equals("")) {
			q.setString("issuePartCode", issuePartCode);
		}
		List<ServiceOrder> list = q.list();

		result.put("list", list);
		
		
		if(null != issuePartCode && !issuePartCode.equals("")) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IssuePart.class, "issuePart");
			criteria.createCriteria("issuePart.serviceOrder", "serviceOrder");
			
			if(null != serviceOrderID && !serviceOrderID.equals("")) {
				criteria.add(Restrictions.eq("serviceOrder.serviceOrderID", serviceOrderID));
			}
			
			if((null != name && !name.equals("")) || (null != mobileTel && !mobileTel.equals(""))) {
				criteria.createCriteria("serviceOrder.customer" , "customer");
				if(null != name && !name.equals("")) {
					criteria.add(Restrictions.like("customer.name", name));
				}
				if(null != mobileTel && !mobileTel.equals("")) {
					criteria.add(Restrictions.like("customer.mobileTel", mobileTel));
				}
			}
			
			if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
				/*
				 * Because endDate field in database is timestamp and hibernate don't use Date function to compare date.
				 * So it'll make mistake to compare because timestamp will compare time too and time to compare is oo:oo.
				 * For correct compare endDate it must add end date by 1 day and use this date as end date.
				 */
				Calendar endDateCal = Calendar.getInstance();
				endDateCal.setTime(sdf.parse(endDate));
				endDateCal.add(Calendar.DATE, 1);
				criteria.add(Restrictions.between("serviceOrder.serviceOrderDate", sdf.parse(startDate), endDateCal.getTime()));
			}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
				criteria.add(Restrictions.ge("serviceOrder.serviceOrderDate", sdf.parse(startDate)));
			}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
				/*
				 * Because endDate field in database is timestamp and hibernate don't use Date function to compare date.
				 * So it'll make mistake to compare because timestamp will compare time too and time to compare is oo:oo.
				 * For correct compare endDate it must add end date by 1 day and use this date as end date.
				 */
				Calendar endDateCal = Calendar.getInstance();
				endDateCal.setTime(sdf.parse(endDate));
				endDateCal.add(Calendar.DATE, 1);
				criteria.add(Restrictions.lt("serviceOrder.serviceOrderDate", endDateCal.getTime()));
			}
			
			if(null != type && !type.equals("")){
				criteria.createCriteria("serviceOrder.product", "product");
				criteria.add(Restrictions.eq("product.type.typeID", type));
			}
			if(null != serialNo && !serialNo.equals("")){
				if(null == type || type.equals("")){
					criteria.createCriteria("serviceOrder.product", "product");
				}
				criteria.add(Restrictions.like("product.serialNo", serialNo));
			}
			if(null != empID && !empID.equals("")){
				criteria.createCriteria("serviceOrder.empFix", "empFix");
				criteria.add(Restrictions.eq("empFix.employeeID", Integer.parseInt(empID)));
			}
			
			criteria.add(Restrictions.ilike("issuePart.code", issuePartCode));
			
			
			criteria.add(Restrictions.ne("serviceOrder.status", "cancel"));
			criteria.setProjection(Projections.rowCount());
			
			result.put("maxRows", criteria.list().get(0));
			
		}else {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ServiceOrder.class, "serviceOrder");
			
	//		if(null != issuePartCode && !issuePartCode.equals("")) {
	//			criteria.createAlias("IssuePart", "issuePart").add( Restrictions.eqProperty("issuePart.serviceOrder.serviceOrderID", "serviceOrder.serviceOrderID") );
	//		}
			
			if(null != serviceOrderID && !serviceOrderID.equals("")) {
				criteria.add(Restrictions.eq("serviceOrderID", serviceOrderID));
			}
			if((null != name && !name.equals("")) || (null != mobileTel && !mobileTel.equals(""))) {
				criteria.createCriteria("serviceOrder.customer" , "customer");
				if(null != name && !name.equals("")) {
					criteria.add(Restrictions.like("customer.name", name));
				}
				if(null != mobileTel && !mobileTel.equals("")) {
					criteria.add(Restrictions.like("customer.mobileTel", mobileTel));
				}
			}
			if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
				/*
				 * Because endDate field in database is timestamp and hibernate don't use Date function to compare date.
				 * So it'll make mistake to compare because timestamp will compare time too and time to compare is oo:oo.
				 * For correct compare endDate it must add end date by 1 day and use this date as end date.
				 */
				Calendar endDateCal = Calendar.getInstance();
				endDateCal.setTime(sdf.parse(endDate));
				endDateCal.add(Calendar.DATE, 1);
				criteria.add(Restrictions.between("serviceOrderDate", sdf.parse(startDate), endDateCal.getTime()));
			}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
				criteria.add(Restrictions.ge("serviceOrderDate", sdf.parse(startDate)));
			}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
				/*
				 * Because endDate field in database is timestamp and hibernate don't use Date function to compare date.
				 * So it'll make mistake to compare because timestamp will compare time too and time to compare is oo:oo.
				 * For correct compare endDate it must add end date by 1 day and use this date as end date.
				 */
				Calendar endDateCal = Calendar.getInstance();
				endDateCal.setTime(sdf.parse(endDate));
				endDateCal.add(Calendar.DATE, 1);
				criteria.add(Restrictions.lt("serviceOrderDate", endDateCal.getTime()));
			}
			if(null != type && !type.equals("")){
				criteria.createCriteria("serviceOrder.product", "product");
				criteria.add(Restrictions.eq("product.type.typeID", type));
			}
			if(null != serialNo && !serialNo.equals("")){
				if(null == type || type.equals("")){
					criteria.createCriteria("serviceOrder.product", "product");
				}
				criteria.add(Restrictions.like("product.serialNo", serialNo));
			}
			if(null != empID && !empID.equals("")){
				criteria.createCriteria("serviceOrder.empFix", "empFix");
				criteria.add(Restrictions.eq("empFix.employeeID", Integer.parseInt(empID)));
			}
	//		if(null != issuePartCode && !issuePartCode.equals("")) {
	//			//q.setString("issuePartCode", issuePartCode);
	//			criteria.createCriteria("issuePart.serviceOrder" , "customer");
	//			if(null != issuePartCode && !issuePartCode.equals("")) {
	//				criteria.add(Restrictions.like("customer.name", name));
	//			}
				//criteria.createAlias("IssuePart", "issuePart").add( Restrictions.eqProperty("issuePart.serviceOrder.serviceOrderID", "serviceOrderID") );
	//			criteria.add(Restrictions.ilike("issuePart.code", issuePartCode));
	//		}
			
			criteria.add(Restrictions.ne("status", "cancel"));
			criteria.setProjection(Projections.rowCount());
			
			result.put("maxRows", criteria.list().get(0));
		}
		
		//System.out.println("criteria.list().get(0)="+criteria.list().get(0));
		
		
		
		//result.put("maxRows", criteria.list().get(0));
		return result;
	}

	@Override
	public boolean edit(ServiceOrder serviceOrder) throws Exception{
		Session session = sessionFactory.getCurrentSession();
		session.update(serviceOrder);
		return true;
	}

	@Override
	public boolean delete(ServiceOrder serviceOrder, Integer employeeID) throws Exception{
		serviceOrder.setStatus(ServiceOrder.CANCEL);
		serviceOrder.setUpdatedDate(new Date());
		serviceOrder.setUpdatedBy(employeeID);
		sessionFactory.getCurrentSession().update(serviceOrder);
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceOrder> selectNewSOByCriteria(String name,
			String date, String type, Integer rows,
			Integer page, String orderBy, String orderType) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}
		if(null != date && !date.equals("")){
			sql.append("and DATE(serviceOrderDate) = :serviceOrderDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and type = :type ");
		}
		
		sql.append("and serviceOrder.status = 'new' ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("tel")){
				orderBy = "serviceOrder.customer.tel";
			}else if(orderBy.equals("mobileTel")){
				orderBy = "serviceOrder.customer.mobileTel";
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString());
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
		if(null != date && !date.equals("")) {
			q.setString("serviceOrderDate", date);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		List<ServiceOrder> result = q.list();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceOrder> selectSOForCloseByCriteria(String name,
			String startDate, String endDate, String type, String serialNo,
			Integer rows, Integer page, String orderBy, String orderType)
			throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}else
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and serviceOrder.product.type.typeID = :type ");
		}
		if(null != serialNo && !serialNo.equals("")){
			sql.append("and serviceOrder.product.serialNo like :serialNo ");
		}
		
		sql.append("and serviceOrder.status in ('fixing','received') ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("surname")){
				orderBy = "serviceOrder.customer.surname";
			}else if(orderBy.equals("fullName")){
				orderBy = "serviceOrder.customer.name "+orderType+", serviceOrder.customer.surname"; 
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString());
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		if(null != serialNo && !serialNo.equals("")) {
			q.setString("serialNo", serialNo);
		}
		List<ServiceOrder> result = q.list();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceOrder> selectSOForCloseByCriteria(Integer employeeID, String name,
			String startDate, String endDate, String type, String serialNo,
			Integer rows, Integer page, String orderBy, String orderType)
			throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("from ServiceOrder as serviceOrder where serviceOrder.empFix = :employeeID ");
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}else
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and serviceOrder.product.type.typeID = :type ");
		}
		if(null != serialNo && !serialNo.equals("")){
			sql.append("and serviceOrder.product.serialNo like :serialNo ");
		}
		
		sql.append("and serviceOrder.status in ('fixing','received','fixed') ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("surname")){
				orderBy = "serviceOrder.customer.surname";
			}else if(orderBy.equals("fullName")){
				orderBy = "serviceOrder.customer.name "+orderType+", serviceOrder.customer.surname"; 
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString());
		q.setInteger("employeeID", employeeID);
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		if(null != serialNo && !serialNo.equals("")) {
			q.setString("serialNo", serialNo);
		}
		List<ServiceOrder> result = q.list();
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceOrder> selectSOForCloseByCriteria(String name,
			String startDate, String endDate, String type, String serialNo,
			String empFixID, Integer rows, Integer page, String orderBy,
			String orderType) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}else
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and serviceOrder.product.type.typeID = :type ");
		}
		if(null != serialNo && !serialNo.equals("")){
			sql.append("and serviceOrder.product.serialNo like :serialNo ");
		}
		if(null != empFixID && !empFixID.equals("")){
			sql.append("and serviceOrder.empFix = :empFixID ");
		}
		
		sql.append("and serviceOrder.status in ('fixing','received','fixed') ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("surname")){
				orderBy = "serviceOrder.customer.surname";
			}else if(orderBy.equals("fullName")){
				orderBy = "serviceOrder.customer.name "+orderType+", serviceOrder.customer.surname"; 
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString());
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		if(null != serialNo && !serialNo.equals("")) {
			q.setString("serialNo", serialNo);
		}
		if(null != empFixID && !empFixID.equals("")){
			q.setInteger("empFixID", Integer.parseInt(empFixID));
		}
		List<ServiceOrder> result = q.list();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceOrder> selectSOForCloseByCriteriaIssuePart(String name,
			String startDate, String endDate, String type, String serialNo,
			String empFixID, String issuePartCode, Integer rows, Integer page, String orderBy,
			String orderType) throws Exception {
		StringBuilder sql = new StringBuilder();
		if(null != issuePartCode && !issuePartCode.equals("")) {
			sql.append("select serviceOrder from ServiceOrder as serviceOrder, IssuePart as issuePart where serviceOrder.serviceOrderID=issuePart.serviceOrder.serviceOrderID ");
		}else {
			sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		}
		
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}else
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and serviceOrder.product.type.typeID = :type ");
		}
		if(null != serialNo && !serialNo.equals("")){
			sql.append("and serviceOrder.product.serialNo like :serialNo ");
		}
		if(null != empFixID && !empFixID.equals("")){
			sql.append("and serviceOrder.empFix = :empFixID ");
		}
		if(null != issuePartCode && !issuePartCode.equals("")) {
			sql.append("and issuePart.code = :issuePartCode ");
		}
		
		
		sql.append("and serviceOrder.status in ('fixing','received','fixed') ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("surname")){
				orderBy = "serviceOrder.customer.surname";
			}else if(orderBy.equals("fullName")){
				orderBy = "serviceOrder.customer.name "+orderType+", serviceOrder.customer.surname"; 
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString());
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		if(null != serialNo && !serialNo.equals("")) {
			q.setString("serialNo", serialNo);
		}
		if(null != empFixID && !empFixID.equals("")){
			q.setInteger("empFixID", Integer.parseInt(empFixID));
		}
		if(null != issuePartCode && !issuePartCode.equals("")) {
			q.setString("issuePartCode", issuePartCode);
		}
		List<ServiceOrder> result = q.list();
		return result;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceOrder> selectFixedSOByCriteria(String name, String date,
			String type, String issuePartCode, Integer rows, Integer page, String orderBy,
			String orderType) throws Exception {
		StringBuilder sql = new StringBuilder();
		//sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		if(null != issuePartCode && !issuePartCode.equals("")) {
			sql.append("select serviceOrder from ServiceOrder as serviceOrder, IssuePart as issuePart where serviceOrder.serviceOrderID=issuePart.serviceOrder.serviceOrderID ");
		}else {
			sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		}
		
		if(null != name && !name.equals("")){
			sql.append("and serviceOrder.customer.name like :name ");
		}
		if(null != date && !date.equals("")){
			sql.append("and DATE(serviceOrderDate) = :serviceOrderDate ");
		}
		if(null != type && !type.equals("")){
			sql.append("and serviceOrder.product.type.typeID = :type ");
		}
		if(null != issuePartCode && !issuePartCode.equals("")) {
			sql.append("and issuePart.code like :issuePartCode ");
		}
		
		sql.append("and serviceOrder.status = '"+ServiceOrder.FIXED+"' ");
		
		if(!orderBy.equals("")){
			if(orderBy.equals("name")){
				orderBy = "serviceOrder.customer.name";
			}else if(orderBy.equals("tel")){
				orderBy = "serviceOrder.customer.tel";
			}else if(orderBy.equals("mobileTel")){
				orderBy = "serviceOrder.customer.mobileTel";
			}
			sql.append("order by "+orderBy+" "+orderType);
		}else{
			sql.append("order by serviceOrder.serviceOrderDate desc");
		}
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString());
		if(null != name && !name.equals("")) {
			q.setString("name", name);
		}
		if(null != date && !date.equals("")) {
			q.setString("serviceOrderDate", date);
		}
		if(null != type && !type.equals("")) {
			q.setString("type", type);
		}
		if(null != issuePartCode && !issuePartCode.equals("")) {
			q.setString("issuePartCode", issuePartCode);
		}
		List<ServiceOrder> result = q.list();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceOrder> getRepairReport(String startDate, String endDate, String status) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("from ServiceOrder as serviceOrder where 1=1 ");
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		
		if(status != null && status != ""){
			sql.append("and serviceOrder.status = :status ");
		}
		
		sql.append("and serviceOrder.status != 'cancel' ");
		
		sql.append("order by serviceOrder.serviceOrderDate ");
		
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString());
		
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		
		if(status != null && status != ""){
			q.setString("status", status);
		}
		
		List<ServiceOrder> result = q.list();
		return result;
	}

	@Override
	public NumRepairReportForm getNumRepairReport(String date)
			throws Exception {
		
		/*select openProd.numOpen, fixingProd.numFixing, fixedProd.numFixed,returnProd.numReturn from
	    (select count(*) numOpen from serviceOrder where status = 'new' and DATE(serviceOrderDate) = '2011-09-26') openProd, 
	    (select count(*) numReturn from serviceOrder where status = 'close' and DATE(serviceOrderDate) = '2011-09-26') returnProd, 
	    (select count(*) numFixed from serviceOrder where status = 'fixed' and DATE(serviceOrderDate) = '2011-09-26') fixedProd,
	    (select count(*) numFixing from serviceOrder where status in ('fixing', 'outsite') and DATE(serviceOrderDate) = '2011-09-26') fixingProd;*/
		
		StringBuilder sql = new StringBuilder();
		sql.append("select openProd.numOpen numOpen, fixingProd.numFixing numFixing, fixedProd.numFixed numFixed, returnProd.numReturn numReturn from ");
		sql.append("(select count(*) numOpen from serviceOrder where status = 'new' and DATE(serviceOrderDate) = :date) openProd, ");
		sql.append("(select count(*) numReturn from serviceOrder where status = 'close' and DATE(serviceOrderDate) = :date) returnProd,");
		sql.append("(select count(*) numFixed from serviceOrder where status = 'fixed' and DATE(serviceOrderDate) = :date) fixedProd,");
		sql.append("(select count(*) numFixing from serviceOrder where status in ('fixing', 'outsite') and DATE(serviceOrderDate) = :date) fixingProd;");
		Query q = sessionFactory.getCurrentSession().createSQLQuery(sql.toString()).addScalar("numOpen", new IntegerType()).addScalar("numFixing", new IntegerType()).addScalar("numFixed", new IntegerType()).addScalar("numReturn", new IntegerType()).setResultTransformer(Transformers.aliasToBean(NumRepairReportForm.class)).setString("date", date);
		
		NumRepairReportForm reportForm = (NumRepairReportForm)q.uniqueResult();
		Date dateSQL = sdfDateSQL.parse(date);
		reportForm.setDate(sdfDate.format(dateSQL));
		return reportForm;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<NumRepairByEmpReportForm> getNumRepairByEmpReport(
			String startDate, String endDate, Integer employeeID)
			throws Exception {
		//		select c.num, emp.name, emp.surname, CONCAT(emp.name,' ',emp.surname) fullname, serviceOrderID, serviceOrderDate, startFix, endFix, appointmentDate, returnDate, CALTIMEDIFF(startFix, serviceOrderDate) diffStartFix_sec, CALTIMEDIFF(endFix, startFix) diffFix_sec, CALTIMEDIFF(appointmentDate, endFix) diffFinish_sec, CALTIMEDIFF(returnDate, endFix) diffReturn_sec from serviceOrder so, (select count(*) num from serviceOrder where empFix = 1) c, employee emp where so.empFix = 1 and so.empFix = emp.employeeID and so.status = 'close';
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c.num numOfDoc, emp.name, emp.surname, CONCAT(emp.name,' ',emp.surname) fullName, serviceOrderID, " +
				"serviceOrderDate, startFix, endFix, appointmentDate, returnDate, " +
				"CALTIMEDIFF(startFix, serviceOrderDate) diffStartFix, CALTIMEDIFF(endFix, startFix) diffFix, CALTIMEDIFF(appointmentDate, endFix) diffFinish, CALTIMEDIFF(returnDate, endFix) diffReturn " +
				"FROM serviceOrder so, (select count(*) num from serviceOrder where status = 'close' ");
		
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(employeeID != null){
			sql.append("and empFix = :empID ");
		}
		sql.append(") c, employee emp ");
		sql.append("WHERE so.empFix = emp.employeeID ");
		
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) between :startDate and :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("and DATE(serviceOrderDate) <= :endDate ");
		}
		if(employeeID != null){
			sql.append("and so.empFix = :empID ");
		}
		
		sql.append("and so.status = 'close' ");
		sql.append("order by fullName, so.serviceOrderDate ");
		
		Query q = sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
			.addScalar("numOfDoc", new IntegerType())
			.addScalar("name", new StringType())
			.addScalar("surname", new StringType())
			.addScalar("fullName", new StringType())
			.addScalar("serviceOrderID", new StringType())
			.addScalar("serviceOrderDate", new TimestampType())
			.addScalar("startFix", new TimestampType())
			.addScalar("endFix", new TimestampType())
			.addScalar("appointmentDate", new TimestampType())
			.addScalar("returnDate", new TimestampType())
			.addScalar("diffStartFix", new StringType())
			.addScalar("diffFix", new StringType())
			.addScalar("diffFinish", new StringType())
			.addScalar("diffReturn", new StringType())
			.setResultTransformer(Transformers.aliasToBean(NumRepairByEmpReportForm.class));
		
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		if(employeeID != null){
			q.setInteger("empID", employeeID);
		}
		
		List<NumRepairByEmpReportForm> retList = q.list();
		return retList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SumAmountReportForm> getSumAmountReport(String startDate,
			String endDate) throws Exception {
		
//		SELECT count( a.serviceOrderID ) numServiceOrder, concat( emp.name, ' ', emp.surname ) fullName, sum( a.totalPrice ) , sum( a.service_price ) , sum( a.part_price )
//		FROM (
//			SELECT so.serviceOrderID serviceOrderID, so.returnDate, so.empFix, so.totalPrice totalPrice, IFNULL( sum( sl.price ) , 0 ) service_price, IFNULL( sum( sp.price ) , 0 ) part_price
//			FROM `serviceOrder` so
//			LEFT JOIN serviceList sl ON so.serviceOrderID = sl.serviceOrderID
//			AND so.totalPrice >0
//			AND so.status = 'close'
//			LEFT JOIN issuePart sp ON so.serviceOrderID = sp.serviceOrderID
//			AND so.totalPrice >0
//			AND so.status = 'close'
//			WHERE STATUS = 'close'
//			GROUP BY so.serviceOrderID
//		)a, employee emp
//		WHERE a.empFix = emp.employeeID
//		GROUP BY fullName
//		ORDER BY fullName

		
//		SELECT count( a.serviceOrderID ) numServiceOrder, concat( emp.name, ' ', emp.surname ) fullName, sum( a.totalPrice ) , sum( a.service_price ) ,  IFNULL(sum(b.price), 0) , sum( a.part_price )
//		FROM (
//			SELECT so.serviceOrderID serviceOrderID, so.returnDate, so.empFix, so.totalPrice totalPrice, IFNULL( sum( sl.price ) , 0 ) service_price, IFNULL( sum( sp.price ) , 0 ) part_price
//			FROM `serviceOrder` so
//			LEFT JOIN serviceList sl ON so.serviceOrderID = sl.serviceOrderID
//			AND so.totalPrice >0
//			AND so.status = 'close'
//			LEFT JOIN issuePart sp ON so.serviceOrderID = sp.serviceOrderID
//			AND so.totalPrice >0
//			AND so.status = 'close'
//			WHERE STATUS = 'close'
//			GROUP BY so.serviceOrderID
//		)a
//		LEFT JOIN 
//		( select os.serviceOrderID,  osd.price from outsiteService os, outsiteServiceDetail osd where os.outsiteServiceID = osd.outsiteServiceID and osd.costType = 'repair') b
//		ON a.serviceOrderID = b.serviceOrderID , employee emp
//		WHERE a.empFix = emp.employeeID
//		GROUP BY fullName
//		ORDER BY fullName;
		

		/* New Query */
/*		SELECT COUNT( a.serviceOrderID ) numServiceOrder , CONCAT( emp.name,  ' ', emp.surname ) fullName, SUM( a.totalPrice ) amount , IFNULL( SUM(b.sumService), 0 ) sumService , IFNULL(sum(d.price), 0) sumOutsiteRepair , IFNULL( SUM(c.sumNetPrice), 0 ) sumPart
		FROM (

		SELECT serviceOrderID, totalPrice, empFix, returnDate
		FROM  `serviceorder` 
		WHERE status = 'close'
		)a
		LEFT JOIN (

		SELECT sl.serviceOrderID, SUM( price ) sumService
		FROM serviceList sl
		GROUP BY sl.serviceOrderID
		)b ON a.serviceOrderID = b.serviceOrderID
		LEFT JOIN (

		SELECT ip.serviceOrderID, SUM( netprice ) sumNetPrice
		FROM issuePart ip
		GROUP BY ip.serviceOrderID
		)c ON a.serviceOrderID = c.serviceOrderID
		LEFT JOIN
		(
		SELECT os.serviceOrderID, osd.price 
		FROM outsiteService os, outsiteServiceDetail osd 
		WHERE os.outsiteServiceID = osd.outsiteServiceID AND osd.costType = 'repair'
		) d ON a.serviceOrderID = d.serviceOrderID
		, employee emp
		WHERE a.empFix = emp.employeeID
		AND DATE( a.returnDate ) between  '2012-07-01' and '2012-07-7'
		GROUP BY fullName
		ORDER BY fullName*/
		
		
		
		StringBuilder sql = new StringBuilder();
		
/*		sql.append("SELECT count( a.serviceOrderID ) numServiceOrder, concat( emp.name, ' ', emp.surname ) fullName, sum( a.totalPrice ) amount, sum( a.service_price ) sumService, sum( a.part_price ) sumPart " +
				"FROM ( " +
				"	SELECT so.serviceOrderID serviceOrderID, so.returnDate, so.empFix, so.totalPrice totalPrice, IFNULL( sum( sl.price ) , 0 ) service_price, IFNULL( sum( sp.price ) , 0 ) part_price " +
				"	FROM `serviceOrder` so " +
				"	LEFT JOIN serviceList sl ON so.serviceOrderID = sl.serviceOrderID " +
				"	AND so.totalPrice >0 " +
				"	AND so.status = 'close' " +
				"	LEFT JOIN issuePart sp ON so.serviceOrderID = sp.serviceOrderID " +
				"	AND so.totalPrice >0 " +
				"	AND so.status = 'close' " +
				"	WHERE STATUS = 'close' " +
				"	GROUP BY so.serviceOrderID " +
				")a, employee emp " +
				"WHERE a.empFix = emp.employeeID ");*/
	
		/*sql.append("SELECT count( a.serviceOrderID ) numServiceOrder, concat( emp.name, ' ', emp.surname ) fullName, sum( a.totalPrice ) amount , sum( a.service_price ) sumService ,  IFNULL(sum(b.price), 0) sumOutsiteRepair , sum( a.part_price ) sumPart " +
				"FROM ( " +
				"	SELECT so.serviceOrderID serviceOrderID, so.returnDate, so.empFix, so.totalPrice totalPrice, IFNULL( sum( sl.price ) , 0 ) service_price, IFNULL( sum( sp.price ) , 0 ) part_price " +
				"	FROM `serviceOrder` so " +
				"	LEFT JOIN serviceList sl ON so.serviceOrderID = sl.serviceOrderID " +
				"	AND so.totalPrice >0 " +
				"	AND so.status = 'close' " +
				"	LEFT JOIN issuePart sp ON so.serviceOrderID = sp.serviceOrderID " +
				"	AND so.totalPrice >0 " +
				"	AND so.status = 'close' " +
				"	WHERE STATUS = 'close' " +
				"	GROUP BY so.serviceOrderID " +
				")a " +
				"LEFT JOIN " + 
				"( select os.serviceOrderID,  osd.price from outsiteService os, outsiteServiceDetail osd where os.outsiteServiceID = osd.outsiteServiceID and osd.costType = 'repair') b " +
				"ON a.serviceOrderID = b.serviceOrderID , employee emp " +
				"WHERE a.empFix = emp.employeeID ");*/
		
		sql.append("SELECT COUNT( a.serviceOrderID ) numServiceOrder , CONCAT( emp.name,  ' ', emp.surname ) fullName, SUM( a.totalPrice ) amount , IFNULL( SUM(b.sumService), 0 ) sumService , IFNULL(sum(d.price), 0) sumOutsiteRepair , IFNULL( SUM(c.sumNetPrice), 0 ) sumPart " +
				"FROM ( " +
				"	SELECT serviceOrderID, totalPrice, empFix, returnDate " +
				"	FROM  `serviceorder` " + 
				"	WHERE status = 'close' " +
				")a " +
				"LEFT JOIN ( " +
				"	SELECT sl.serviceOrderID, SUM( price ) sumService " +
				"	FROM serviceList sl " +
				"	GROUP BY sl.serviceOrderID " +
				")b ON a.serviceOrderID = b.serviceOrderID " +
				"LEFT JOIN ( " +
				"	SELECT ip.serviceOrderID, SUM( netprice ) sumNetPrice " +
				"	FROM issuePart ip " +
				"	GROUP BY ip.serviceOrderID " +
				")c ON a.serviceOrderID = c.serviceOrderID " +
				"LEFT JOIN " +
				"( " +
				"	SELECT os.serviceOrderID, osd.price " +
				"	FROM outsiteService os, outsiteServiceDetail osd " +
				"	WHERE os.outsiteServiceID = osd.outsiteServiceID AND osd.costType = 'repair' " +
				") d ON a.serviceOrderID = d.serviceOrderID " +
				", employee emp " +
				"WHERE a.empFix = emp.employeeID ");
		
		
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("AND DATE(a.returnDate) BETWEEN :startDate AND :endDate ");
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			sql.append("AND DATE(a.returnDate) >= :startDate ");
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			sql.append("AND DATE(a.returnDate) <= :endDate ");
		}
		sql.append("GROUP BY fullName ORDER BY fullName ");
		
		Query q = sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
		.addScalar("fullName", new StringType())
		.addScalar("numServiceOrder", new IntegerType())
		.addScalar("amount", new DoubleType())
		.addScalar("sumService", new DoubleType())
		.addScalar("sumOutsiteRepair", new DoubleType())
		.addScalar("sumPart", new DoubleType())
		.setResultTransformer(Transformers.aliasToBean(SumAmountReportForm.class));
		
		if((null != startDate && !startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("startDate", startDate);
			q.setString("endDate", endDate);
		}else if((null != startDate && !startDate.equals("")) && (null == endDate || endDate.equals(""))){
			q.setString("startDate", startDate);
		}else if((null == startDate || startDate.equals("")) && (null != endDate && !endDate.equals(""))){
			q.setString("endDate", endDate);
		}
		
		List<SumAmountReportForm> retList = q.list();
		return retList;
	}

}