# 声明 JAVA 包名
namespace java com.test.thrift.spriderservice
# 声明python包名
namespace py spriderservice.api

service  SpriderService{
       # 调用spider方法，返回布尔值
       bool spr_main();
}