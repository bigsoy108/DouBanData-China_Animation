@echo off
echo produce interface document python
thrift --gen py -out ../ spriderservice.thrift
thrift --gen java -out ../../sprider-thrift-service-api/src/main/java spriderservice.thrift
