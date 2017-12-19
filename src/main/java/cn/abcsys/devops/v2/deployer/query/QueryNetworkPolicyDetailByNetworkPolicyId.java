package cn.abcsys.devops.v2.deployer.query;

import cn.abcsys.devops.v2.deployer.cores.interfaces.IQuery;
import cn.abcsys.devops.v2.deployer.cores.parameter.*;
import cn.abcsys.devops.v2.deployer.cores.results.GridBean;
import cn.abcsys.devops.v2.deployer.cores.results.ResultBean;
import cn.abcsys.devops.v2.deployer.db.dao.V2NetworkLabelsMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2NetworkPolicyMapper;
import cn.abcsys.devops.v2.deployer.db.dao.V2NetworkPortsMapper;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkLabels;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPolicy;
import cn.abcsys.devops.v2.deployer.db.model.V2NetworkPorts;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过 id 来进行资源的数据库查询操作
 *
 * @author xianghao
 * @create 2017-10-09 下午8:28
 **/

@Component("queryNetworkPolicyDetailByNetworkPolicyId")
public class QueryNetworkPolicyDetailByNetworkPolicyId implements IQuery{

    @Resource(name="v2NetworkPolicyMapper")
    private V2NetworkPolicyMapper networkPolicyMapper;

    @Resource(name="v2NetworkLabelsMapper")
    private V2NetworkLabelsMapper networkLabelsMapper;

    @Resource(name="v2NetworkPortsMapper")
    private V2NetworkPortsMapper networkPortsMapper;

    private static Logger logger = Logger.getLogger(QueryNetworkPolicyDetailByNetworkPolicyId.class);

    @Override
    public GridBean excuteQuery(QueryParameter parameter) throws NullPointerException, Exception{
        try {
            QueryEnvIdParameter queryEnvIdParameter = (QueryEnvIdParameter)parameter;
            Integer  networkPolicyId= queryEnvIdParameter.getNetworkPolicyId();
            try {
                V2NetworkPolicy v2NetworkPolicy = networkPolicyMapper.selectByPrimaryKey(networkPolicyId);
                if (v2NetworkPolicy == null){
                    throw new IndexOutOfBoundsException("查询不到该网络");
                }
                List<V2NetworkLabels> networkLabelsList = networkLabelsMapper.selectByNetworkPolicyId(networkPolicyId);
                List<V2NetworkPorts>  networkPortsList= networkPortsMapper.selectByNetworkPolicyId(networkPolicyId);
                NetworkParameter networkParameter = new NetworkParameter();
                //开始构造传入的网络参数

                //环境id列表就不传了
                networkParameter.setMasterIp(v2NetworkPolicy.getMasterIp());
                networkParameter.setMasterPort(v2NetworkPolicy.getMasterPort());
                networkParameter.setMasterType(v2NetworkPolicy.getMasterType());
                networkParameter.setName(v2NetworkPolicy.getObjectName());
                networkParameter.setPlatfromType("kubernetes-network");
                networkParameter.setConfigType("createNetworkConfig");
                networkParameter.setTriggerType("createNetworkTrigger");
                networkParameter.setId(networkPolicyId);
                List<Integer> envIdList = new ArrayList<>();
                envIdList.add(v2NetworkPolicy.getEnvId());
                networkParameter.setEnvIdList(envIdList);

                if (networkPortsList != null && networkPortsList.size()>0){
                    List<Ports> portsList = new ArrayList<>();
                    for (V2NetworkPorts networkPorts:networkPortsList) {
                        Ports ports = new Ports();
                        ports.setProtocol(networkPorts.getProtocol());
                        ports.setPort(networkPorts.getPortValue());
                        ports.setName(networkPorts.getPortName());
                        portsList.add(ports);
                    }
                    networkParameter.setPorts(portsList);
                }

                if (networkLabelsList != null && networkLabelsList.size()>0){
                    for (V2NetworkLabels networkLabels:networkLabelsList) {
                        if (networkLabels.getLabelType().equals("ingressPodSelector")){ // 只需要给一个label参数
                            Labels labels = new Labels();
                            labels.setValue(networkLabels.getLabelValue());
                            labels.setKey(networkLabels.getLabelKey());
                            labels.setType(networkLabels.getLabelType());
                            networkParameter.setLabel(labels);
                        }
                    }
                }
                List<Object> list = new ArrayList<>();
                list.add(networkParameter);
                GridBean gridBean = new GridBean(0, 0, 0, list, true);
                return gridBean;

            }catch (IndexOutOfBoundsException e){
                logger.info("在数据库查询网络发布细节时，查询不到该网络");
                throw e;
            }catch (Exception e){
                logger.info("在数据库查询网络发布细节时，发生未知错误");
                e.printStackTrace();
                throw e;
            }

        }catch (NullPointerException e){
            NullPointerException exception = new NullPointerException("传入参数为空");
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }catch (IndexOutOfBoundsException e){
            logger.info("在数据库查询网络发布细节时，查询不到该网络");
            throw e;
        }catch (Exception e){
            e.printStackTrace();
            Exception exception = new Exception("查询数据库操作错误: "+e.getClass());
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }
    }
}
