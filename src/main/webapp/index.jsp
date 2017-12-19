<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>devops-deployer测试</title>
</head>

<body>
<h3>示例URL：</h3>

<h3>当前本地服务器的IP为：133.133.134.82</h3>
<h3>当前CI服务器的IP为：42.125.203.202</h3>
<p>
    # 示例:<br />
    http://localhost:8888/user/insertOneUser.do?userLoginName=xuyuanjia&userLoginPassword=123456
</p>

<p>
    # create method:<br />
    http://IP:9003/devops-deployer/createRc.do?instanceCore.instanceNamespace=default&instanceCore.instanceName=tomcat&instanceCore.ip=40.125.203.202&instanceCore.instanceType=kubernetes&instanceCore.instanceImage=registry.cn-hangzhou.aliyuncs.com/xuyuanjia/docker:tomcat-centos&instanceCore.instanceCpu=200m&instanceCore.instanceMemory=500Mi&instanceCore.instanceNetwork=bridge&instanceCore.instanceImagePullSecret=my-secret&instanceVolumesListString=local:/root/apache-tomcat-9.0.0.M21/logs;&instanceEnvsListString=XYJ_PATH:/root&instanceCore.appName=test-volume
    <br /><br />

    # start method:<br />
    http://IP:9003/devops-deployer/start.do?instanceCore.instanceCoreId=default-20170624172303547-tomcat&instancePortsString=8080&instanceCore.ip=40.125.203.202;<br /><br />

    #stop method:<br />
    http://IP:9003/devops-deployer/stop.do?instanceCore.instanceRename=default-20170624172303547-tomcat<br /><br />

    #delete method:<br />
    http://IP:9003/devops-deployer/delete.do?instanceCore.instanceRename=default-20170616092145004-tomcat<br /><br />
</p>

<p>
<div >
    <h4>getAllContainerInfo.do示例URL：</h4>
    http://IP:9003/devops-deployer/getAllContainerInfo.do?page=1&rows=10&instanceNamespace=default
</div>
</p>

<p>
<div >
    <h4>getAllContainerInfoNew.do示例URL：</h4>
    http://IP:9003/devops-deployer/getAllContainerInfoNew.do?page=1&rows=10&appName=zhangqintest3clone-kubernetes-20170717013034804&instanceImage=tomcat:latest
</div>
<div title="可选参数">
    <h4>可选参数：</h4>
    containerName，clusterName，imageName，hostIp，instanceNetwork
</div>
</p>

<p>
<div>
    <h4>getConatinerDetailInfo.do示例URL：</h4>
    http://IP:9003/devops-deployer/getConatinerDetailInfo.do?instanceRename=default-20170616092145004-tomcat
</div>
</p>

<p>
<div>
    <h4>getAllNetworksInfo.do示例URL：</h4>
    http://IP:9003/devops-deployer/getAllNetworksInfo.do?instanceNetwork=sss&page=1&rows=1
</div>
</p>

<p>
<div>
    <h4>getAllVolumesInfo.do示例URL：</h4>
    http://127.0.0.1:9003/devops-deployer/getAllVolumesInfo.do?instanceVolumesName=v&page=1&rows=10
</div>
</p>

<p>
<div>
    <h4>getAllImageInfo.do示例URL：</h4>
    http://IP:9003/devops-deployer/getAllImageInfo.do?imageName=tomcat&page=2&rows=1
</div>
</p>

<p>
<div>
    <h4>getContainersFromAppName.do示例URL：</h4>
    http://IP:9003/devops-deployer/getContainersFromAppName.do?instanceImage=registry.cn-hangzhou.aliyuncs.com/xuyuanjia/docker:tomcat-centos&page=1&rows=10&appName=test-kubernetes-20170630133116035
</div>
</p>


<p>
<div>
    <h4>getContainerNetworkVolumeCounts.do示例URL：</h4>
    http://IP:9003/devops-deployer/getContainerNetworkVolumeCounts.do
</div>
</p>

<p>
<div>
    <h4>setIP.do示例URL：</h4>
    http://127.0.0.1:9003/devops-deployer/setIP.do?instanceCore.instanceCoreId=305&clusterIp=192.168.1.100
</div>
</p>

<p>
<div>
    <h4>setAppIP.do示例URL：</h4>
    http://127.0.0.1:9003/devops-deployer/setAppIP.do?instanceCore.appName=a001-kubernetes-20170717024736812&clusterIp=192.168.1.100
</div>
</p>

<p>
<div>
    <h4>getAppIP.do示例URL：</h4>
    http://127.0.0.1:9003/devops-deployer/getAppIP.do?instanceCore.appName=a001-kubernetes-20170717024736812
</div>
</p>

<p>
<div>
    <h4>checkVolumesPath.do示例URL：</h4>
    http://127.0.0.1:9003/devops-deployer/checkVolumesPath.do?envId=1&instanceVolumesPath=/abcs/data/share/
</div>
</p>


</body>
</html>