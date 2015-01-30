Yelp Review Analysis and Recommendation
=======================================

Information Group: Yelp Review Analysis and Recommendation

Extract hidden informaion from raw Yelp review text,
find explicit industry and user attitude labels by classification algorithms
and retrieve hidden subtopic labels by LDA clustering.

Our code is organized in two directories:

- BigData:
  
  - Offline code to preprocess Yelp dataset and perform experiments on the parsed review text.
    
  - Windows command-line and Unix shell scripts are not included in this directory.
  
  - Also, some data set are not uploaded to Github due to their extremely large sizes.
    
- BigDataOnline:
  
  - Online code of a review analysis and business recommendation website.
  
  - The website address is: http://121.42.11.100:8078/BigDataOnline/ (Available before Jan. 20th 2015).
  
  - When deploying our website, please copy the 'data' and 'edu' subdirectory to the 'bin' directory of Tomcat 7.
  
