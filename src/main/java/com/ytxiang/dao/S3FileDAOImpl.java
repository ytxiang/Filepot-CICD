package com.ytxiang.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.util.CollectionUtils;
import com.ytxiang.model.S3File;


/**
 * DAO Implementation class
 */
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class S3FileDAOImpl implements S3FileDAO {

	@Autowired
	private SessionFactory sessionFactory;
/*
	@Override
	public void deleteFile(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		Object persistentInstance = session.load(CustomerFiles.class, id);
		if (persistentInstance != null) {
			session.delete(persistentInstance);
		}
	}
*/
	@Override
	public S3File getExistingFile(Integer userId, String fileName) {
		Query query = sessionFactory.getCurrentSession()
				.createQuery("from S3File where userId = :userId and fileName = :fileName");
		query.setParameter("userId", userId);
		query.setParameter("fileName", fileName);

		List<S3File> s3Files = query.list();
		if (CollectionUtils.isNullOrEmpty(s3Files)) {
			return null;
		}
		return s3Files.get(0);
	}

	@Override
	public void createOrUpdate(S3File file) {
		sessionFactory.getCurrentSession().saveOrUpdate(file);
	}
/*
	@Override
	public List<CustomerFiles> getAllCustomerFiles(Integer customerId) {

		Query query = sessionFactory.getCurrentSession()
				.createQuery("from CustomerFiles where customerId = :customerId");
		query.setParameter("customerId", customerId);
		List<CustomerFiles> customerFiles = query.list();
		return customerFiles;
	}
*/
}
