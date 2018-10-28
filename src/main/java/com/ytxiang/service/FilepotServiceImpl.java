package com.ytxiang.service;

//import javax.xml.bind.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.ValidationException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.StringUtils;
import com.ytxiang.model.S3File;
import com.ytxiang.model.User;
import com.ytxiang.dao.S3FileDAO;
import com.ytxiang.dao.UserDAO;
import com.ytxiang.dto.S3FileDTO;
import com.ytxiang.dto.UserDTO;

@Service
public class FilepotServiceImpl implements FilepotService {
	
	@Autowired
	UserDAO userDao;

	@Autowired
	S3FileDAO s3FileDao;
	
	private AmazonS3Client s3Client;

	private Map<String, String> nvPair = new HashMap<String, String>();

	public Map<String, String> getNVPair() {
		return nvPair;
	}

	private boolean loadPropertyFile() {
		Properties props = new Properties();
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("application.properties");
		
		if (input != null) {
			try {
				props.load(input);
			} catch (Exception e) {
				System.err.println("Failed to load application.properties file!");
				return false;
			}

			for (String key : props.stringPropertyNames()) {
				String value = props.getProperty(key);
				nvPair.put(key, value);
			}
		}
		return true;
	}
	
	private void initS3Client() {
		// fetch access keys from property file
		String access = getNVPair().get("filepot.s3.access.id");
		String secret = getNVPair().get("filepot.s3.access.secret");
		AWSCredentials cred = new BasicAWSCredentials(access, secret);

		// instantiate S3 client
		s3Client = new AmazonS3Client(cred);

		// Set S3 region
		s3Client.setRegion(Region.getRegion(Regions.US_WEST_1));

		// Enable Transfer Acceleration Mode
		s3Client.setS3ClientOptions(S3ClientOptions.builder().setAccelerateModeEnabled(true).build());
	}
	
	@PostConstruct
	private void postInit() {
		if (loadPropertyFile())
			initS3Client();
	}

	@Override
	public UserDTO login(String userName, String password) /* throws ValidationException */ {
		User user = userDao.getUser(userName, password);
		
		if (null == user) {
			//throw new ValidationException("The username and password don't match");
			return null;
		}
		UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(),
				user.getFullName(), user.getNick());
		return userDTO;
	}
	
	@Override
	public S3FileDTO upload(MultipartFile mfile, String notes, String userName)
			throws IllegalStateException, IOException, ValidationException {
		
		String fileName = mfile.getOriginalFilename();
		final int maxFileSize = 10485760;
		
		if (mfile.getSize() > maxFileSize)
			throw new ValidationException("File size should not be over 10MB");

		/* Assemble to a single file */
		File file = File.createTempFile(fileName, "");
		mfile.transferTo(file);
		String bucket = getNVPair().get("filepot.s3.bucket");
		String key = getNVPair().get("filepot.s3.key") + userName + "/" + fileName;

		/* Upload to S3 */
		@SuppressWarnings("unused")
		PutObjectResult awsResult = s3Client.putObject(bucket, key, file);

		/* Create DB entry if new file or else update */
		S3FileDTO s3FileDTO = createOrUpdateS3File(fileName, notes, key, userName,
				mfile.getSize());
		return s3FileDTO;
		
	}

	private S3FileDTO createOrUpdateS3File(String fileName, String notes, String path,
			String userName, Long size) {

		User user = userDao.getUserByUserName(userName);

		S3File userFile = s3FileDao.getExistingFile(user.getId(), fileName);

		if (null == userFile) {
			userFile = new S3File();
			userFile.setFileName(fileName);
			userFile.setCreatedTime(new Timestamp(System.currentTimeMillis()));
			userFile.setModifiedTime(new Timestamp(System.currentTimeMillis()));
			userFile.setNotes(notes);
			userFile.setSize(FileUtils.byteCountToDisplaySize(size));
			userFile.setPath(path);
			userFile.setUser(user);
		} else {
			userFile.setModifiedTime(new Timestamp(System.currentTimeMillis()));
			userFile.setSize(FileUtils.byteCountToDisplaySize(size));
			if (!StringUtils.isNullOrEmpty(notes)) {
				userFile.setNotes(notes);
			}
		}

		s3FileDao.createOrUpdate(userFile);
		S3File returnUserFile = s3FileDao.getExistingFile(user.getId(), fileName);
		S3FileDTO s3FileDTO = new S3FileDTO(returnUserFile.getId(),
				returnUserFile.getFileName(), returnUserFile.getNotes(), returnUserFile.getSize(),
				returnUserFile.getPath(), returnUserFile.getCreatedTime(), returnUserFile.getModifiedTime());

		return s3FileDTO;
	}

}