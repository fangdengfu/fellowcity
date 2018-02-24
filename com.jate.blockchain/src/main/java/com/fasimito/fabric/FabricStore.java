package com.fasimito.fabric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;

/**
 * ���˴洢���ö���
 * 
 * @author aberic
 *
 * @date 2017��9��7�� - ����4:36:19
 * @email abericyang@gmail.com
 */
class FabricStore {

    private String file;
    /** �û���Ϣ���� */
    private final Map<String, FabricUser> members = new HashMap<>();

    public FabricStore(File file) {
        this.file = file.getAbsolutePath();
    }

    /**
     * ������������ص�ֵ
     *
     * @param name
     *            ����
     * @param value
     *            ���ֵ
     */
    public void setValue(String name, String value) {
        Properties properties = loadProperties();
        try (OutputStream output = new FileOutputStream(file)) {
            properties.setProperty(name, value);
            properties.store(output, "");
            output.close();
        } catch (IOException e) {
            System.out.println(String.format("Could not save the keyvalue store, reason:%s", e.getMessage()));
        }
    }

    /**
     * ��ȡ��������ص�ֵ
     *
     * @param ����
     * @return ���ֵ
     */
    public String getValue(String name) {
        Properties properties = loadProperties();
        return properties.getProperty(name);
    }

    /**
     * ���������ļ�
     * 
     * @return �����ļ�����
     */
    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(file)) {
            properties.load(input);
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println(String.format("Could not find the file \"%s\"", file));
        } catch (IOException e) {
            System.out.println(String.format("Could not load keyvalue store from file \"%s\", reason:%s", file, e.getMessage()));
        }
        return properties;
    }

    /**
     * �ø��������ƻ�ȡ�û�
     * 
     * @param ����
     * @param ��֯
     * 
     * @return �û�
     */
    public FabricUser getMember(String name, String org) {
        // ���Դӻ����л�ȡUser״???
        FabricUser fabricUser = members.get(FabricUser.toKeyValStoreName(name, org));
        if (null != fabricUser) {
            return fabricUser;
        }
        // ����User�������ԴӼ�ֵ�洢�лָ�����״???(����ҵ��Ļ�)??
        fabricUser = new FabricUser(name, org, this);
        return fabricUser;
    }

    /**
     * �ø��������ƻ�ȡ�û�
     * 
     * @param name
     *            ����
     * @param org
     *            ��֯
     * @param mspId
     *            ��Աid
     * @param privateKeyFile
     * @param certificateFile
     * 
     * @return user �û�
     * 
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    public FabricUser getMember(String name, String org, String mspId, File privateKeyFile, File certificateFile)
            throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        try {
            // ���Դӻ����л�ȡUser״???
            FabricUser fabricUser = members.get(FabricUser.toKeyValStoreName(name, org));
            if (null != fabricUser) {
                System.out.println("���Դӻ����л�ȡUser״??? User = " + fabricUser);
                return fabricUser;
            }
            // ����User�������ԴӼ�ֵ�洢�лָ�����״???(����ҵ��Ļ�)??
            fabricUser = new FabricUser(name, org, this);
            fabricUser.setMspId(mspId);
            String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");
            PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
            fabricUser.setEnrollment(new StoreEnrollement(privateKey, certificate));
            return fabricUser;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            throw e;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw e;
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * ͨ���ֽ�������Ϣ��ȡ˽Կ
     * 
     * @param data
     *            �ֽ�����
     * 
     * @return ˽Կ
     * 
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        final Reader pemReader = new StringReader(new String(data));
        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }
        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
        return privateKey;
    }

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * �Զ���ע��Ǽǲ�����
     * 
     * @author yangyi47
     *
     */
    static final class StoreEnrollement implements Enrollment, Serializable {

        private static final long serialVersionUID = 6965341351799577442L;

        /** ˽Կ */
        private final PrivateKey privateKey;
        /** ��Ȩ֤�� */
        private final String certificate;

        StoreEnrollement(PrivateKey privateKey, String certificate) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }

        @Override
        public PrivateKey getKey() {
            return privateKey;
        }

        @Override
        public String getCert() {
            return certificate;
        }
    }

}