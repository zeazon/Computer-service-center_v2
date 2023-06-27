package com.twobytes.master.dao;

import java.util.List;

//import org.hibernate.query.Query;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.twobytes.model.Brand;

@Repository
public class BrandDAOImpl implements BrandDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public boolean save(Brand brand) throws Exception{
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(brand);
		return true;
	}

	@Override
	public Brand selectByID(Integer brandID) throws Exception{
		Brand brand = new Brand();
		brand = (Brand)sessionFactory.getCurrentSession().get(Brand.class, brandID);
		return brand;
	}

	@Override
	public boolean edit(Brand brand) throws Exception{
		Session session = sessionFactory.getCurrentSession();
		session.update(brand);
		return true;
	}

	@Override
	public boolean delete(Brand brand) throws Exception {
		sessionFactory.getCurrentSession().delete(brand);
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Brand> selectByCriteria(String name, Integer rows, Integer page, String orderBy, String orderType) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("from Brand where 1=1 ");
		if(null != name && !name.equals("")){
			sql.append("and name like :name ");
		}
		
		if(!orderBy.equals("")){
			sql.append("order by "+orderBy+" "+orderType);
		}
		Query q = sessionFactory.getCurrentSession().createQuery(sql.toString());
		if(null != name && !name.equals("")) {
			q.setParameter("name", name);
		}

		List<Brand> result = q.list();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Brand> getAll() throws Exception {
		Query q = sessionFactory.getCurrentSession().createQuery("from Brand order by name ");
		List<Brand> retList = q.list();
		return retList;
	}

}