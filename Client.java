package com.company;
// L� uma linha do teclado
// Envia o pacote (linha digitada) ao servidor

import java.awt.*;
import java.io.*; // classes para input e output streams e
import java.net.*;// DatagramaSocket,InetAddress,DatagramaPacket
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;


class UDPClient {
    // cria socket do servidor com a porta 9876
    static DatagramSocket clientSocket;
    static InetAddress IPAddress;
    static String ultimoDado;
    static final String host = "localhost";
    static int port;

    //Gerencia de Arquivos
    static File arquivoEscolhido;
    static String diretorio = "G:\\1Universidade\\2_2021\\Tf_Cliente\\pam.txt";
    static File file = new File(diretorio);
    static File afile[] = file.listFiles();
    static File arquivos;

    public static void main(String args[]) throws Exception
    {
        String recomeçar = "n";
        do {
            port = 9876;
            IPAddress = InetAddress.getByName(host);
            // cria o stream do teclado
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            // declara socket cliente
            clientSocket = new DatagramSocket();

            // obtem endere�o IP do servidor com o DNS
            IPAddress = InetAddress.getByName("localhost");

            byte[] sendData = new byte[100];
            byte[] receiveData = new byte[1024];

            menu();

            Path path = Paths.get(diretorio);
            byte[] data = Files.readAllBytes(path);
            slowStart(data);

            System.out.println(data.length);

            String sentence = "0";

//            ultimoDado = sentence;
//            sendData = data;
//            // cria pacote com o dado, o endere�o do server e porta do servidor
//            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
//            //envia o pacote com a dificuldade para o servidor
//            clientSocket.send(sendPacket);//1
//            esperandoConfirmação();//1

            System.out.println(data.length);
            System.out.println(recomeçar);
        }while (recomeçar.equals("s"));
        // fecha o cliente
        System.out.println("Encerrando...");
        clientSocket.close();
    }

    public static void mandaPacote(byte[] data) throws Exception {
        byte[] sendData = new byte[100];
        sendData = data;
        // cria pacote com o dado, o endere�o do server e porta do servidor
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        //envia o pacote com a dificuldade para o servidor
        clientSocket.send(sendPacket);
        esperandoConfirmação();
    }

    public static void mandandoConfirmação() throws IOException {
        DatagramPacket sendPacket;
        byte[] sendData = new byte[1024];
        String recebeu = "ACK";
        sendData = recebeu.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(sendPacket);
    }

    public static void esperandoConfirmação() throws Exception {
        String resposta;
        int time = 5000;
        clientSocket.setSoTimeout(time);
        while(true){
            try{
                byte[] receiveData2 = new byte[3];
                DatagramPacket receivePacket2 = new DatagramPacket(receiveData2,0, receiveData2.length);
                //recebe a resposta
                clientSocket.receive(receivePacket2);
                resposta = new String(receivePacket2.getData()).trim();
                //System.out.println("Confirmação recebida: " + resposta);
                if(resposta.equals("ACK")){
                    return;
                }
                resposta = "";
                clientSocket.setSoTimeout(0);
                continue;
            } catch (SocketTimeoutException exception){ //nao recebe a resposta em no máximo 5 segundos
                System.out.println("Retransmissão:");
                DatagramPacket sendPacket;
                byte[] sendData = new byte[1024];
                //envia pergunta
                sendData = ultimoDado.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                clientSocket.send(sendPacket);
                System.out.println("Dado retransmitido.");
                continue;
            }
        }
    }

    public static void slowStart(byte[] arquivo) throws Exception {
        int tam = 2;
        double pedaco = (arquivo.length) / (tam * 1.0);

        int pedacos = (int) Math.ceil(pedaco);

        System.out.println("tamarquivo: "+arquivo.length);
        System.out.println("pedas: "+pedaco);
        System.out.println("peda: "+pedacos);
        byte[][] matriz = new byte[pedacos][tam];
        String trash = "0";
        byte[] lixo = trash.getBytes();

        for (int i = 0; i<pedacos; i++ ) {
            for (int j = 0; j<tam; j++) {
                System.out.println("J: "+ j);
                System.out.println("i: "+ i);
                int arrayIndex = (j*i) + j;
                if (arrayIndex < arquivo.length){
                    matriz[i][j] = arquivo[arrayIndex];
                }else{
                    matriz[i][j] = lixo[0];
                }

                System.out.println(matriz[i][j]);
            }
        }

        //com a matriz montada, deve se percorrer as colunas e enviar os pacotes
        byte[] pacote = new byte[tam];
        for (int i = 0; i<pedacos; i++ ) {
            for (int j = 0; j<tam; j++) {
                pacote[j] = matriz[i][j];
                System.out.println("J: "+ j);
                System.out.println("i: "+ i);
                System.out.println("pacote["+j+"] = "+pacote[j]);
            }
            mandaPacote(pacote);
        }
    }

    public static void menu() {
        //menu
        System.out.println("Menu--------------------");
        System.out.println("--> Digite o caminho do arquivo escolhido:");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        //Scanner teclado = new Scanner(System.in);
        //int opcao = teclado.nextInt();
        //String opcao = inFromUser.readLine();
        //diretorio = opcao;

    }

}