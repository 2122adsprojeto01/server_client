FROM java:latest

COPY adsgrupo01.jar /home/adsgrupo01.jar
#CMD ["java","-jar" "/home/adsgrupo01.jar"]
#RUN java -jar /home/adsgrupo01.jar
CMD ["java","-jar","/home/adsgrupo01.jar"]