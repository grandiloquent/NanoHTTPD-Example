package euphoria.psycho;

import org.nanohttpd.util.ServerRunner;

import java.io.File;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {

        try {
            String hostName = ServerUtils.getLocalHostLANAddress().getHostName();
            System.out.println(hostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        WebServer webServer = new WebServer(ServerUtils.getLocalIp(), 8090);

        Log.e("TAG/Main", "main: " + webServer.getURL());

        webServer.setStaticDirectory(new File("C:\\Users\\psycho\\IdeaProjects\\WebServer\\www"));
        webServer.setUploadDirectory(new File("C:\\Users\\psycho\\IdeaProjects\\WebServer\\upload"));

        ServerRunner.executeInstance(webServer);


    }
}
