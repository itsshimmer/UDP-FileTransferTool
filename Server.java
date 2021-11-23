package com.company;
// Recebe um pacote de algum cliente
// Separa o dado, o endere�o IP e a porta deste cliente
// Imprime o dado na tela

import java.io.*;
import java.net.*;
import java.util.ArrayList;

class UDPServer {
    // cria socket do servidor com a porta 9876
    static DatagramSocket serverSocket;
    static InetAddress IPAddress;
    static String ultimoDado;
    static final String host = "localhost";
    static int port;

    static String pacote;
    static String diretorio = "G:\\1Universidade\\2_2021\\Tf_Cliente\\jao.txt";

    public static void main(String args[]) throws Exception {
        serverSocket = new DatagramSocket(9876);
        String recomecar = "s";
        do {
            port = 9876;
            IPAddress = InetAddress.getByName(host);

            recebePacotes();
            salvaArquivo();

            System.out.printf("\n A mensagem foi gravada com sucesso em " + diretorio);

            //Verifica se quer receber mais arquivos
            byte[] receiveDataRecomecar = new byte[1];
            DatagramPacket receivePacketRecomecar = new DatagramPacket(receiveDataRecomecar, 0, receiveDataRecomecar.length);
            System.out.println("\n--------------------------------------------\n");
            System.out.println("Esperando se o cliente quer mandar novos arquivos.");
            serverSocket.setSoTimeout(0);
            serverSocket.receive(receivePacketRecomecar);
            String jn = new String(receivePacketRecomecar.getData()).trim();
            recomecar = jn;
            mandandoConfirmação();
            System.out.println(recomecar);

        } while (recomecar.equals("s"));

        //fecha servidor
        System.out.println("Encerrando...");
        serverSocket.close();
    }

    public static void salvaArquivo() throws IOException {
        //Cria um arquivo
        FileWriter arq = new FileWriter(diretorio);
        PrintWriter gravarArq = new PrintWriter(arq);

        //Escreve no arquivo criado
        gravarArq.print(pacote);
        arq.close();
    }

    public static void recebePacotes() throws IOException {
        while (true) {
            //todo so ta pegando a primeira parte 100 bytes iniciais
            byte[] receiveData = new byte[100];
            // declara o pacote a ser recebido
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // recebe o pacote do cliente
            serverSocket.receive(receivePacket);//1
            port = receivePacket.getPort();
            mandandoConfirmação();//1

            pacote = new String(receivePacket.getData()).trim();
            System.out.println("Resposta recebida: " + pacote);
        }
    }

    public static void mandandoConfirmação() throws IOException {
        DatagramPacket sendPacket;
        byte[] sendData2 = new byte[1024];
        String recebeu = "ACK";
        sendData2 = recebeu.getBytes();
        sendPacket = new DatagramPacket(sendData2, sendData2.length, IPAddress, port);
        serverSocket.send(sendPacket);
    }

    public static void esperandoConfirmação() throws Exception {
        String resposta;
        int time = 5000;
        serverSocket.setSoTimeout(time);
        while (true) {
            try {
                byte[] receiveData2 = new byte[3];
                DatagramPacket receivePacket2 = new DatagramPacket(receiveData2, 0, receiveData2.length);
                //recebe a resposta
                serverSocket.receive(receivePacket2);
                resposta = new String(receivePacket2.getData()).trim();
                System.out.println("Confirmação recebida: " + resposta);
                if (resposta.equals("ACK")) {
                    return;
                }
                resposta = "";
                serverSocket.setSoTimeout(0);
                continue;
            } catch (SocketTimeoutException exception) { //nao recebe a resposta em no máximo 5 segundos
                System.out.println("Retransmissão:");
                DatagramPacket sendPacket;
                byte[] sendData = new byte[1024];
                //envia pergunta
                sendData = ultimoDado.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
                System.out.println("Dado retransmitido.");
                continue;
            }
        }
    }
}

