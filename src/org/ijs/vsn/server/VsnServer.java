package org.ijs.vsn.server;

import org.ijs.vsn.listener.ServerListener;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;

public class VsnServer extends Server implements NewDataEvent {

    private static final Logger logger = Logger.getLogger(VsnServer.class);
    private ArrayList<ServerListener> registerServerListener = new ArrayList<ServerListener>();
    private ThreadPoolExecutor threadPool = null;
    private LinkedBlockingQueue<Runnable> queue;
    //int poolSize = 100;
    //int maxPoolSize = 1000;
    //long keepAliveTime = 0;
    QueuedThreadPool threads = null;

    public VsnServer() throws Exception {

        //queue = new LinkedBlockingQueue<Runnable>();
        //threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
        //keepAliveTime, TimeUnit.SECONDS, queue);

        /*Server configureServer = new Server();
         XmlConfiguration configuration = new XmlConfiguration(new FileInputStream("jetty.xml")); //or use new XmlConfiguration(new FileInputStream("myJetty.xml"));
         configuration.configure(configureServer);
         configureServer.start();*/

        //threads = new QueuedThreadPool();
        //threads.setMaxThreads(10);
        //threads.setMinThreads(5);
        //threads.setLowThreads(6);
        //setThreadPool(threads);

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(ServerConfig.getPort());
        connector.setMaxIdleTime(30000);
        connector.setAcceptors(2);
        connector.setStatsOn(false);
        connector.setLowResourcesConnections(5000);
        connector.setLowResourceMaxIdleTime(5000);
        addConnector(connector);

        /*String server_config =
         "<Configure id=\"Server\" class=\"org.mortbay.jetty.Server\">\n"
         + "  <Call name=\"addConnector\">\n"
         + "    <Arg>\n"
         + "      <New class=\"org.mortbay.jetty.nio.SelectChannelConnector\">\n"
         + "        <Set name=\"host\"><SystemProperty name=\"jetty.host\" /></Set>\n"
         + "        <Set name=\"port\"><SystemProperty name=\"jetty.port\" default=\"" + ServerConfig.getPort() + "\"/></Set>\n"
         + "        <Set name=\"maxIdleTime\">10000</Set>\n"
         + "        <Set name=\"statsOn\">false</Set>\n"
         + "        <Set name=\"lowResourcesConnections\">5000</Set>\n"
         + "        <Set name=\"lowResourcesMaxIdleTime\">5000</Set>\n"
         + "      </New>\n"
         + "    </Arg>\n"
         + "  </Call>\n"
         + "</Configure>\n";*/

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        setHandler(contexts);

        Context time = new Context(contexts, "/vsn/time", Context.SESSIONS);
        time.addServlet(new ServletHolder(new TimeHandler()), "/*");
        Context miren = new Context(contexts, "/vsn/miren", Context.SESSIONS);
        miren.addServlet(new ServletHolder(new MirenHandler(this)), "/*");
        Context mirenTable = new Context(contexts, "/vsn/mtable", Context.SESSIONS);
        mirenTable.addServlet(new ServletHolder(new MtableHandler()), "/*");
        Context kostanjevica = new Context(contexts, "/staro/vn2.php", Context.SESSIONS);
        kostanjevica.addServlet(new ServletHolder(new KostanjevicaHandler(this)), "/*");
        Context picture = new Context(contexts, "/vsn/picture", Context.SESSIONS);
        picture.addServlet(new ServletHolder(new PictureHandler()), "/*");
        Context data = new Context(contexts, "/vsn/data", Context.SESSIONS);
        data.addServlet(new ServletHolder(new DataHandler(this)), "/*");
        Context farm = new Context(contexts, "/vsn/farms", Context.SESSIONS);
        farm.addServlet(new ServletHolder(new FarmHandler(this)), "/*");
        Context table = new Context(contexts, "/vsn/tables", Context.SESSIONS);
        table.addServlet(new ServletHolder(new TableHandler()), "/*");
        Context putFile = new Context(contexts, "/staro/putfile.php", Context.SESSIONS);
        putFile.addServlet(new ServletHolder(new TableHandler()), "/*");
        Context time2 = new Context(contexts, "/staro/v7.php", Context.SESSIONS);
        time2.addServlet(new ServletHolder(new TimeHandler()), "/*");
        Context metadata = new Context(contexts, "/vsn/metadata", Context.SESSIONS);
        metadata.addServlet(new ServletHolder(new MetadataHandler()), "/*");
        Context gps = new Context(contexts, "/vsn/gps", Context.SESSIONS);
        gps.addServlet(new ServletHolder(new GpsHandler()), "/*");
        Context mirror = new Context(contexts, "/vsn/mirror", Context.SESSIONS);
        mirror.addServlet(new ServletHolder(new MirrorHandler()), "/*");
        Context TimestampedData = new Context(contexts, "/vsn/timestampeddata", Context.SESSIONS);
        TimestampedData.addServlet(new ServletHolder(new TimestampedDataHandler(this)), "/*");
    }

    public void addServerListener(ServerListener serverListener) {
        registerServerListener.add(serverListener);
    }

    public void removeServerListener(ServerListener serverListener) {
        registerServerListener.remove(serverListener);
    }

    public synchronized void notifyServerListeners(ArrayList<Object> newData) {
        for (int i = 0; i < registerServerListener.size(); i++) {
            //threadPool.execute(new ThreadNotify(i, newData, registerServerListener));
            //(new Thread(new ThreadNotify(i, newData, registerServerListener))).start();            
            ((ServerListener) registerServerListener.get(i)).newData(newData);
        }
    }

    @Override
    public void newDataNotification(ArrayList<Object> newData) {
        notifyServerListeners(newData);
    }

    private class ThreadNotify implements Runnable {

        int i;
        ArrayList<Object> newData;
        ArrayList<ServerListener> listener;

        public ThreadNotify(int i, ArrayList<Object> newData, ArrayList<ServerListener> listener) {
            this.i = i;
            this.newData = newData;
            this.listener = listener;
        }

        @Override
        public void run() {
            ((ServerListener) listener.get(i)).newData(newData);
        }
    }
}
