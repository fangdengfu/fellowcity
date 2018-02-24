package com.fasimito.fabric.bean;

/**
 * Fabric������chaincode��Ϣ����������channel����Ϣ * 
 * @author fasimito *
 * @date 2018��2��23�� - ����2:07:42
 * @email fasimito@163.com
 */
public class Chaincode {
	
	/**
	 * ���ܺ�Լ�İ�װ��
	 * peer chaincode install -n mycc -v 1.0 -p github.com/chaincode/chaincode_example02/go/
	 * ���ܺ�Լ��ʵ������
	 * peer chaincode instantiate -o orderer.example.com:7050
	 *  --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem
	 *  -C $CHANNEL_NAME -n mycc -v 1.0 -c '{"Args":["init","a", "100", "b","200"]}' -P "OR ('Org1MSP.member','Org2MSP.member')" 
	 */

    /** ��ǰ��Ҫ���ʵ����ܺ�Լ����Ƶ������ */
    private String channelName; // ffetest
    /** ���ܺ�Լ���� */
    private String chaincodeName; // ffetestcc
    /** ���ܺ�Լ��װ·�� */
    private String chaincodePath; // github.com/hyperledger/fabric/xxx/chaincode/go/example/test
    /** ���ܺ�Լ�汾�� */
    private String chaincodeVersion; // 1.0
    /** ִ�����ܺ�Լ�����ȴ�ʱ�� */
    private int invokeWatiTime = 100000;
    /** ִ�����ܺ�Լʵ���ȴ�ʱ�� */
    private int deployWatiTime = 120000;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public String getChaincodePath() {
        return chaincodePath;
    }

    public void setChaincodePath(String chaincodePath) {
        this.chaincodePath = chaincodePath;
    }

    public String getChaincodeVersion() {
        return chaincodeVersion;
    }

    public void setChaincodeVersion(String chaincodeVersion) {
        this.chaincodeVersion = chaincodeVersion;
    }

    public int getInvokeWatiTime() {
        return invokeWatiTime;
    }

    public void setInvokeWatiTime(int invokeWatiTime) {
        this.invokeWatiTime = invokeWatiTime;
    }

    public int getDeployWatiTime() {
        return deployWatiTime;
    }

    public void setDeployWatiTime(int deployWatiTime) {
        this.deployWatiTime = deployWatiTime;
    }

}