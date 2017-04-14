package org.ijs.vsn.server;

import org.ijs.vsn.gsn.Gsn;
import org.ijs.vsn.database.Database;
import org.ijs.vsn.websocket.WebsocketMessage;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.ijs.vsn.miner.SendMiner;
import org.ijs.vsn.node.*;
import org.ijs.vsn.sensestream.SendSenseStream;
import org.ijs.vsn.validator.DataValidator;
import org.mortbay.xml.XmlConfiguration;

public class VsnServerMain {

    private static final Logger logger = Logger.getLogger(VsnServerMain.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // TODO Auto-generated method stub
            DOMConfigurator.configure("log4jConfig.xml");
            ServerConfig confKeys = new ServerConfig();
            confKeys.getConfigKeys();
            NodeList nodes = new NodeList();
            nodes.downloadNodes();

            VsnServer server = new VsnServer();
            XmlConfiguration logConfiguration = new XmlConfiguration(loggingConfig());
            logConfiguration.configure(server);
            server.start();

            DataValidator validator = new DataValidator();
            server.addServerListener(validator);
            Database db = new Database();
            validator.addValidatorListener(db);
            if (ServerConfig.getGsnBuilderFlag()) {
                Gsn gsnFiles = new Gsn();
                validator.addValidatorListener(gsnFiles);
                logger.info("GSN creator is turned ON!");
            } else {
                logger.info("GSN creator is turned OFF!");
            }
            if (ServerConfig.getMinerFlag()) {
                SendMiner miner = new SendMiner();
                validator.addValidatorListener(miner);
                logger.info("Miner is turned ON!");
            } else {
                logger.info("Miner is turned OFF!");
            }
            if (ServerConfig.getSenseStreamFlag()) {
                SendSenseStream senseStream = new SendSenseStream();
                validator.addValidatorListener(senseStream);
                logger.info("SenseStream is turned ON!");
            } else {
                logger.info("SenseStream is turned OFF!");
            }
            if (ServerConfig.getcTables().equals("on")) {
                logger.info("Coordinator tables are being downloaded in folder "
                        + System.getProperty("user.dir")
                        + System.getProperty("file.separator")
                        + "CoordinatorTables!");
            } else {
                logger.info("Coordinator tables are NOT being downloaded!");
            }
            if (ServerConfig.getMetadataFlag()) {
                logger.info("Metadata request is being send!");
            } else {
                logger.info("Metadata request is NOT being send!");
            }
            if (ServerConfig.getWsFlag()) {   // Websocket enable check
                try {
                    WebsocketMessage wsMessage = new WebsocketMessage();
                    validator.addValidatorListener(wsMessage);
                    logger.info("Websocket is turned ON!");
                } catch (Exception e) {
                    logger.error("Websocket failed at setup! Error report: " + e);
                }

            } else {
                logger.info("Websocket is turned OFF!");
            }
            logger.info("VsnServer is logging to directory " + System.getProperty("user.dir")
                    + System.getProperty("file.separator") + "logs" + System.getProperty("file.separator")
                    + "vsn_server" + System.getProperty("file.separator"));
            logger.info("VsnServer started successfully and it is listening on port " + ServerConfig.getPort() + "...");
        } catch (Exception ex) {
            logger.fatal("Creating main objects failed!");
            logger.fatal(ex);
        }
    }

    private static String loggingConfig() {
        String logging_config =
                "<Configure id=\"Server\" class=\"org.mortbay.jetty.Server\">\n"
                + "     <New id=\"ServerLog\" class=\"java.io.PrintStream\">\n"
                + "         <Arg>\n"
                + "             <New class=\"org.mortbay.util.RolloverFileOutputStream\">\n"
                + "                 <Arg><SystemProperty name=\"jetty.home\" default=\".\"/>/logs/jetty/yyyy_mm_dd.stderrout.log</Arg>\n"
                + "                 <Arg type=\"boolean\">false</Arg>\n"
                + "                 <Arg type=\"int\">90</Arg>\n"
                + "                 <Arg><Call class=\"java.util.TimeZone\" name=\"getTimeZone\"><Arg>GMT</Arg></Call></Arg>\n"
                + "                 <Get id=\"ServerLogName\" name=\"datedFilename\"/>\n"
                + "             </New>\n"
                + "         </Arg>\n"
                + "     </New>\n"
                + "     <Call class=\"org.mortbay.log.Log\" name=\"info\"><Arg>Redirecting stderr/stdout to <Ref id=\"ServerLogName\"/></Arg></Call>\n"
                + "     <Call class=\"java.lang.System\" name=\"setErr\"><Arg><Ref id=\"ServerLog\"/></Arg></Call>\n"
                + "     <Call class=\"java.lang.System\" name=\"setOut\"><Arg><Ref id=\"ServerLog\"/></Arg></Call>\n"
                + "</Configure>\n";
        return logging_config;
    }
}
