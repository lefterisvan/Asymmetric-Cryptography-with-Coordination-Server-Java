import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class IPSender extends Thread
{
    private Socket socket;
    private String clientAddress;
    private String tlsServerAddress = "192.168.1.81";
    private String request;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public IPSender(){}

    public IPSender(Socket socket, String clientAddress)
    {
        this.socket = socket;
        this.clientAddress = clientAddress;
    }

    public void run()
    {
        try
        {
            dataOutputStream = new DataOutputStream(socket.getOutputStream()); //creates data output and input streams
            dataInputStream = new DataInputStream(socket.getInputStream());

            request = dataInputStream.readLine(); //reads what the client sent

            if(request.equals("no tls server found")) //if the client sent file the server replies that everything is ok
            {
                System.out.println("sending new IP address now");
                dataOutputStream.writeBytes(tlsServerAddress+"\n");
                dataOutputStream.flush();

                System.out.println("Sent IP to client.");

                request = dataInputStream.readLine(); //reads what the client sent

                if(request.equals("received ip ok"))
                {
                    System.out.println("Client received the ip.Terminating connection");
                    dataInputStream.close();
                    dataOutputStream.close();
                    socket.close();
                }
                else
                {
                    System.out.println("Error!! Client didn't receive the ip address");
                    dataInputStream.close();
                    dataOutputStream.close();
                    socket.close();
                }
            }
            else
            {
                System.out.println("Unknown request");
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
