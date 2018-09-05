package org.sex.hanker.ProxyURL;

import android.util.Log;


import org.sex.hanker.Utils.LogTools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/6/28.
 */
public class ProxyServer {

    public static ExecutorService socketProcessor;//处理视频请求
    String RequestUrl;
    ServerSocket serverSocket;
    private static final String PROXY_HOST = "127.0.0.1";
    private int port;
    private Thread SocketServerThread;

    private static ProxyServer proxyServer;

    public static ProxyServer getInsance() {
        if (proxyServer == null)
            proxyServer = new ProxyServer();
        return proxyServer;
    }

    private ProxyServer() {

    }

    public String getProxyUrl(String url) {
        shutdown();
        socketProcessor = Executors.newFixedThreadPool(4);

        try {
            InetAddress inetAddress = InetAddress.getByName(PROXY_HOST);
            this.serverSocket = new ServerSocket(0, 8, inetAddress);
            this.port = serverSocket.getLocalPort();
            CountDownLatch startSignal = new CountDownLatch(1);
            SocketServerThread = new Thread(new WaitRequestsRunnable(startSignal));
            SocketServerThread.start();
            startSignal.await(); // freeze thread, wait for server starts
        } catch (IOException | InterruptedException e) {
            socketProcessor.shutdown();
            throw new IllegalStateException("Error starting local proxy server", e);
        }

            return String.format(Locale.US, "http://%s:%d/%s", PROXY_HOST, port, url);
    }

    public void shutdown() {
        if (socketProcessor != null)
            socketProcessor.shutdownNow();

        if (SocketServerThread != null)
            SocketServerThread.interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final class WaitRequestsRunnable implements Runnable {

        private final CountDownLatch startSignal;

        public WaitRequestsRunnable(CountDownLatch startSignal) {
            this.startSignal = startSignal;
        }

        @Override
        public void run() {
            startSignal.countDown();
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    socketProcessor.submit(new SocketProcessorRunnable(socket));
                }
            } catch (IOException e) {
                LogTools.e("IOException", e.toString());
            }
        }
    }

    private final class SocketProcessorRunnable implements Runnable {

        private final Socket socket;

        public SocketProcessorRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                GetRequest request=GetRequest.read(socket.getInputStream());
                String url = request.uri;

                if(url.toLowerCase().equalsIgnoreCase("ping"))
                {
                    IOUtil.responseToPing(socket);
                }
                else
                {
                    DownLoadAndTransfer dlt=new DownLoadAndTransfer(socket, request);
                    dlt.OpenConnection();
                }

                if(socket.isConnected())
                {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
