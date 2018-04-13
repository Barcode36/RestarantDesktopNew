package RestarantApp.Network;

import java.io.IOException;
import java.net.*;

public class NetworkConnection {
    public static NetworkChangeListener networkChangeListener;
    boolean isConnected = false;
    public NetworkConnection(NetworkChangeListener networkChangeListener)
    {
        this.networkChangeListener = networkChangeListener;
        isInternetReachable();

    }

        public boolean networkDetected()
        {
            Thread timerc = new Thread()
            {
                @Override
                public void run() {
                    for (;;)
                    {
                        Socket socket = new Socket();
                        InetSocketAddress inetSocketAddress = new InetSocketAddress("www.google.com",80);

                        try {
                            socket.connect(inetSocketAddress);
                            networkChangeListener.Networkchanged(true);
                            isConnected = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            networkChangeListener.Networkchanged(false);
                            isConnected = false;
                        }
                        try {
                            sleep(2000);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            };
            timerc.start();
            return isConnected;
        }

    public  boolean isInternetReachable()
    {
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                for (;;)
                {
                    URL url = null;
                    try {
                        url = new URL("http://www.google.com");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    //open a connection to that source
                    HttpURLConnection urlConnect = null;
                    try {
                        urlConnect = (HttpURLConnection)url.openConnection();
                        //trying to retrieve data from the source. If there
                        //is no connection, this line will fail
                        try {
                            Object objData = urlConnect.getContent();
                            networkChangeListener.Networkchanged(true);
                            isConnected = true;

                        }catch (NoRouteToHostException e)
                        {
                            networkChangeListener.Networkchanged(false);
                            isConnected = false;

                        }catch (UnknownHostException e)
                        {
                            networkChangeListener.Networkchanged(false);
                            isConnected = false;

                        }catch (ConnectException e)
                        {
                            networkChangeListener.Networkchanged(false);
                            isConnected = false;

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        networkChangeListener.Networkchanged(false);
                        isConnected = false;

                    }

                    try {
                        sleep(1000);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        return isConnected;

    }

}
