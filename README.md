# res-sync
## 使用说明
1. 此为maven项目构建的工程；
2. 构建后需要配置数据源；config/jdbc.properties；
3. 需要配置tomcat服务器；
4. watcher的初始化操作，运行入口在cn.abcsys.devops.deployer.initialization的AllInit方法中；
5. 核心操作入口在：cn.abcsys.devops.v2.deployer.watches的PodWatcher中；
6. deployer数据库设计说明：http://nacha.mydoc.io/?t=234396

## 需要任务
1. 了解数据库构造；
2. 1.6实现了对pod的watch，现在需要watch deployment，并把数据填充到相应的deployer数据表中；

