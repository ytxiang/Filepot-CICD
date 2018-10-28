package com.ytxiang.service;

import java.io.IOException;
import javax.xml.bind.ValidationException;

import org.springframework.web.multipart.MultipartFile;

import com.ytxiang.dto.S3FileDTO;
import com.ytxiang.dto.UserDTO;

public interface FilepotService {
	UserDTO login(String userName, String password); /* throws ValidationException */
	S3FileDTO upload(MultipartFile mfile, String notes, String userName)
			throws IllegalStateException, IOException, ValidationException;
}