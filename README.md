# Filepot-CICD

### Introduction
Project 1: Filepot</br>
University Name: http://www.sjsu.edu/</br>
Course: [Cloud Technologies](http://info.sjsu.edu/web-dbgen/catalog/courses/CMPE281.html)</br>
Professor: [Sanjay Garje](https://www.linkedin.com/in/sanjaygarje/)</br>
ISA: [Anushri Srinath Aithal ](https://www.linkedin.com/in/anushri-aithal/)</br>
Student: Yunting Xiang</br>

### Filepot Introduction
This project aims to create a 3 tier web application which is able to provide highly available, highly scalable and flexible cloud file storage management. The AWS cloud services are utilized to accelerate the development, build, test and deployment of this project. The following functions are implemented:

1.  User registration
2.  User login/logout
3.  Facebook login integration
4.  Upload/download/update/delete/list file

### Feature List
1.  Sign up a new user and create the corresponding account in AWS RDS MySQL database if it does not exist. 
2.  Verify if the username/password credential is valid when a user tries to log in.
3.  Allow the authorized user to upload file.  The fields like User’s first name, User’s last name, file upload time, file updated time and file description will be recorded. The maximum file size is 10MB.
4.  List all the files which have been uploaded by the authorized user.
5.  For each existing file, authorized user can update, delete and download.
6.  Provide a Admin role which can list all the users’ files and delete them if needed. 
7.  User logout.


### The adopted technologies:
1. AWS services:
R53, EC2, AutoScaling Group, ELB,  RDS, S3, S3 Transfer Acceleration, Glacier, CloudFront,  Lambda, CodePipeline, CodeCommit, CodeBuild, CodeDeploy, CloudWatch, SNS

2. Development tools/library:
Spring Boot, AWS toolkit for Eclipse, Maven, Hibernate

### Sample Demo Screenshots






