package com.ytxiang.dao;

import java.util.List;
import com.ytxiang.model.S3File;
import com.ytxiang.dto.S3FileDTO;

public interface S3FileDAO {

	/**
	 * Delete File
	 * 
	 * @param id
	 */
	//void deleteFile(Integer id);

	/**
	 * Get File metadata from user id and fileName
	 * 
	 * @param userId
	 * @param fileName
	 * @return
	 */
	S3File getExistingFile(Integer userId, String fileName);

	/**
	 * Insert File metadata to folder
	 * 
	 * @param fileName
	 */
	void createOrUpdate(S3File customerFile);

	/**
	 * Fetch all files for a particular customer
	 * @param customerId
	 * @return
	 */
	//List<S3File> getAllS3File(Integer usrId);

}
