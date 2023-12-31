package com.twobytes.master.dao;

import java.util.List;

//��������͹������� hibernate ����������� 4
//import org.hibernate.query.Query;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.twobytes.model.Employee;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public boolean save(Employee emp) throws Exception{
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(emp);
		return true;
	}

	@Override
	public Employee selectByID(Integer employeeID) throws Exception{
		Employee emp = new Employee();
		emp = (Employee)sessionFactory.getCurrentSession().get(Employee.class, employeeID);
		return emp;
	}

	@Override
	public boolean edit(Employee emp) throws Exception{
		Session session = sessionFactory.getCurrentSession();
		session.update(emp);
		return true;
	}

	@Override
	public boolean delete(Employee emp) throws Exception{
		sessionFactory.getCurrentSession().delete(emp);
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Employee> selectByCriteria(String name, String surname, Integer rows, Integer page, String orderBy, String orderType) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("from Employee emp where 1=1 ");
		if(null != name && !name.equals("")){
			sql.append("and emp.name like :name ");
		}
		if(null != surname && !surname.equals("")){
			sql.append("and emp.surname like :surname ");
		}
		if(!orderBy.equals("")){
			sql.append("order by "+orderBy+" "+orderType);
		}

		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString());
		if(null != name && !name.equals("")) {
			q.setParameter("name", name);
		}
		if(null != surname && !surname.equals("")) {
			q.setParameter("surname", surname);
		}
		List<Employee> result = q.list();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean checkValidLogin(String login) throws Exception{
		Query q = sessionFactory.getCurrentSession().createQuery("from Employee emp where emp.login = :login ");
		q.setParameter("login", login);
		List<Employee> result = q.list();
		if(result.size() > 0){
			return false;
		}else{
			return true;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean checkValidLogin(String login, Integer employeeID) throws Exception{
		Query q = sessionFactory.getCurrentSession().createQuery("from Employee emp where emp.login = :login and emp.employeeID != :employeeID ");
		q.setParameter("login", login);
		q.setParameter("employeeID", employeeID);
		List<Employee> result = q.list();
		if(result.size() > 0){
			return false;
		}else{
			return true;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Employee> getAll() throws Exception {
		Query q = sessionFactory.getCurrentSession().createQuery("from Employee order by name ");
		List<Employee> retList = q.list();
		return retList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Employee> getByRole(Integer roleID) throws Exception {
		Query q = sessionFactory.getCurrentSession().createQuery("from Employee emp where emp.roleID.roleID = :roleID order by emp.name ");
		q.setParameter("roleID", roleID);
		List<Employee> retList = q.list();
		return retList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Employee> getByRole(List<Integer> roleList) throws Exception {
		StringBuffer query = new StringBuffer("from Employee emp where emp.roleID.roleID in (");
		for(int i=0; i<roleList.size(); i++){
			if(i==0){
				query.append(roleList.get(i));
			}else{
				query.append(", "+roleList.get(i));
			}
		}
		query.append(") order by emp.name");
		Query q = sessionFactory.getCurrentSession().createQuery(query.toString());
		List<Employee> retList = q.list();
		return retList;
	}

}