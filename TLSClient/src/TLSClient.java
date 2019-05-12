import java.io.*;
import javax.net.ssl.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;

public class TLSClient
{
    private static String tlsServerAddress = "192.168.1.81";
    private static final int port = 4443;

    private static String request;
    private static DataOutputStream dataOutputStream;
    private static DataInputStream dataInputStream;

    public static void main(String[] args)
    {
        //setting system properties
        System.setProperty("javax.net.ssl.trustStore", "clientTrustore.ts");//defines the trustore file
        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "false");
        System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "true");
        System.setProperty("https.protocols", "SSLv2");
        if (true)
        {
            System.setProperty("javax.net.debug", "ssl");
        }

        try
        {
            SSLSocketFactory sslSocketFactory = getFactory(); //SSLSocketFactory creation
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(tlsServerAddress, port); //creates and initializes server socket

            new FileSharing(sslSocket).run(); //Creates a new FileSharing object with the ssl socket that was created and calls the run function
        }
        catch (IOException e1)
        {
            System.out.println("Exception: "+e1.getMessage());

            if(e1.getMessage().equals("Connection timed out: connect"))
            {
                String coordinationServer = "192.168.1.80";
                int portCoord = 5555;

                System.out.println("Connecting to coordination server..Please Wait");

                try
                {
                    Socket socketCoord = new Socket(coordinationServer, portCoord); //initializes the socket at the right server address and port
                    System.out.println("Connection OK");

                    dataOutputStream = new DataOutputStream(socketCoord.getOutputStream()); //creates data output and input streams
                    dataInputStream = new DataInputStream(socketCoord.getInputStream());

                    dataOutputStream.writeBytes("no tls server found\n");
                    dataOutputStream.flush();

                    request = dataInputStream.readLine(); //reads the new ip

                    tlsServerAddress = request;

                    System.out.println("New ip is: "+request);

                    dataOutputStream.writeBytes("received ip ok\n");
                    dataOutputStream.flush();

                    dataOutputStream.close();
                    dataInputStream.close();
                    socketCoord.close();

                    //edw xreiazetai mia nea prospatheia gia sindesi me ton tls server stin kainouria tou ip
                    SSLSocketFactory sslSocketFactory = getFactory(); //SSLSocketFactory creation
                    SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(tlsServerAddress, port); //creates and initializes server socket

                    new FileSharing(sslSocket).run(); //Creates a new FileSharing object with the ssl socket that was created and calls the run function

                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static SSLSocketFactory getFactory()
    {
        SSLContext sslContext = null;

        try {
            sslContext = SSLContext.getInstance("TLS");

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            char [] pKeyPassword= "evg2018".toCharArray();
            InputStream keyInput = new FileInputStream("D:/MSC/1ο Εξάμηνο/Ασφάλεια Δικτύων/Εργασία Β1/Φάση 3/TLSClient/evgKeystore.jks"); //keystore file
            keyStore.load(keyInput, pKeyPassword);
            keyInput.close();

            keyManagerFactory.init(keyStore, pKeyPassword);

            sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext.getSocketFactory();
    }
}