/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ijs.vsn.server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class ServerConfig {

    private static Properties configFile;
    private static int port;
    private static String dbAddress;
    private static String dbUsername;
    private static String dbpassword;
    private static String gsnBuildFlag;
    private static String gsnLocation;
    private static String miner;
    private static String minerAddress;
    private static String senseStream;
    private static String senseStreamAddress;
    private static String cTables;
    private static String metadataRequestFlag;
    private static String wsFlag;
    private static String wsAddress;
    private static final Logger logger = Logger.getLogger(ServerConfig.class);

    public void getConfigKeys() {
        try {
            configFile = new Properties();
            configFile.load(new FileInputStream("Server_Database.cfg"));
            configFile.store(new FileOutputStream("Server_Database.cfg"), "Set port on which server listens on\nEXAMPLE: PORT=33001\n"
                    + "Set database address\nEXAMPLE: DB_ADDRESS=jdbc:mysql://localhost/sensor_data/\n"
                    + "Set database username\nEXAMPLE: DB_USERNAME=root\n"
                    + "Set database password\nEXAMPLE: DB_PASSWORD=password\n"
                    + "Turn GSN builder ON or OFF\nEXAMPLE: GSN_BUILDER=on or GSN_BUILDER=off\n"
                    + "GSN location\nEXAMPLE: GSN_LOCATION=C:\\Program Files\n"
                    + "Turn Miner ON or OFF\nEXAMPLE: MINER=on or MINER=off\n"
                    + "Miner address\nEXAMPLE: MINER_ADDRESS=http://xpack.ijs.si:9989/add-measurements/\n"
                    + "Turn SenseStream ON or OFF\nEXAMPLE: SENSESTREAM=on or SENSESTREAM=off\n"
                    + "SenseStream address\nEXAMPLE: SENSESTREAM_ADDRESS=http://opcomm.ijs.si/api/add-som\n"
                    + "Download coordinator tables\nEXAMPLE: CTABLES=on or CTABLES=off\n"
                    + "Enable Metadata request\nEXAMPLE: METADATA_REQUEST=true or METADATA_REQUEST=false\n"
                    + "Enable Websocket\nEXAMPLE: WS_ENABLE=true or WS_ENABLE=false\n"
                    + "Websocket address\nEXAMPLE: WS_ADDRESS=ws://localhost:8080/ws/");
            configFile.load(new FileInputStream("Server_Database.cfg"));
            port = Integer.parseInt(configFile.getProperty("PORT"));
            dbAddress = configFile.getProperty("DB_ADDRESS");
            dbUsername = configFile.getProperty("DB_USERNAME");
            dbpassword = configFile.getProperty("DB_PASSWORD");
            gsnBuildFlag = configFile.getProperty("GSN_BUILDER");
            gsnLocation = configFile.getProperty("GSN_LOCATION");
            senseStream = configFile.getProperty("SENSESTREAM");
            senseStreamAddress = configFile.getProperty("SENSESTREAM_ADDRESS");
            miner = configFile.getProperty("MINER");
            minerAddress = configFile.getProperty("MINER_ADDRESS");
            cTables = configFile.getProperty("CTABLES");
            metadataRequestFlag = configFile.getProperty("METADATA_REQUEST");
            wsFlag = configFile.getProperty("WS_ENABLE");
            wsAddress = configFile.getProperty("WS_ADDRESS");
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public static String getGsnLocation() {
        return gsnLocation;
    }

    public static String getcTables() {
        return cTables;
    }

    public static int getPort() {
        return port;
    }

    public static String getDbAddress() {
        return dbAddress;
    }

    public static String getDbUsername() {
        return dbUsername;
    }

    public static String getDbPassword() {
        return dbpassword;
    }

    public static String getWsAddress() {
        return wsAddress;
    }

    public static boolean getGsnBuilderFlag() {
        boolean flag = false;
        if (gsnBuildFlag != null && gsnBuildFlag.contains("on")) {
            flag = true;
        }
        return flag;
    }

    public static boolean getMinerFlag() {
        boolean flag = false;
        if (miner != null && miner.contains("on")) {
            flag = true;
        }
        return flag;
    }

    public static String getMinerAddress() {
        return minerAddress;
    }

    public static boolean getSenseStreamFlag() {
        boolean flag = false;
        if (senseStream != null && senseStream.contains("on")) {
            flag = true;
        }
        return flag;
    }

    public static String getSenseStreamAddress() {
        return senseStreamAddress;
    }

    public static boolean getMetadataFlag() {
        boolean flag = false;
        if (metadataRequestFlag != null && metadataRequestFlag.contains("true")) {
            flag = true;
        }
        return flag;
    }

    public static boolean getWsFlag() {
        boolean flag = false;
        if (wsFlag != null && wsFlag.contains("true")) {
            flag = true;
        }
        return flag;
    }
}
