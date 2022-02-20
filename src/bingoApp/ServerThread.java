package bingoApp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that inherits from Thread and represents a connexion with one player
 */
public class ServerThread extends Thread {

    Socket service;

    /**
     * Constructor for the class ServerThread
     * @param service
     */
    public ServerThread(Socket service) {
        this.service = service;
    }

    /**
     * This is the method that sends to the player its 5 numbers and the
     * current number of the game.
     */
    @Override
    public void run() {
        ObjectInputStream socketIn = null;
        ObjectOutputStream socketOut = null;
        int bolaActual = 0;
        ArrayList<Integer> bolasExtraidas = new ArrayList<>();
        ArrayList<Integer> ticket = new ArrayList<>();
        boolean bingo;

        try {
            socketIn = new ObjectInputStream(service.getInputStream());
            socketOut = new ObjectOutputStream(service.getOutputStream());

            ticket = createTicket();
            socketOut.writeObject(ticket);
            socketOut.flush();

            do {
            esperar(2);
            // saco bola y la envio al cliente
            socketOut.writeObject(BingoServer.numerosSacados.get(bolaActual));
            socketOut.flush();
            bolasExtraidas.add(BingoServer.numerosSacados.get(bolaActual));
            bingo = (boolean) socketIn.readObject();

            if (bingo){
                BingoServer.finDeJuego = true;
                socketOut.writeObject(BingoServer.finDeJuego);
            }

            else
                socketOut.writeObject(BingoServer.finDeJuego);

            if (bolaActual < 19)
                bolaActual++;
            }
            while(!BingoServer.finDeJuego);

        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            System.out.println(e);
        }
        finally {
                try {
                    if (socketIn != null) {
                        socketIn.close();
                    }
                }
                catch (IOException e) {
                    System.out.println(e);
                }
                try {
                    if (socketOut != null) {
                        socketOut.close();
                    }
                }
                catch (IOException e) {
                    System.out.println(e);
                }
                try {
                    if (service != null) {
                        service.close();
                    }
                }
                catch (IOException e){
                    System.out.println(e);
                }
        }
    }

    /**
     * This method creates a list of five random numbers from 1 to 20. Those
     * are the numbers of the player
     * @return
     */
    private ArrayList<Integer> createTicket() {
        int number;
        int minimo = 1;
        int maximo = 20;
        ArrayList<Integer> lista = new ArrayList<>();

        do {
            number = (int) (Math.random() * (maximo - minimo)) + minimo;
            if(!lista.contains(number))
                lista.add(number);
        }
        while (lista.size() < 5);
        Collections.sort(lista);
        return lista;
    }

    /**
     * Method that makes the game to wait x seconds between the sent of
     * numbers
     * @param segundos number of seconds to wait
     * @throws InterruptedException
     */
    private void esperar(int segundos) throws InterruptedException {
        Thread.sleep(segundos * 1000);
    }

    /**
     * This method checks if all the numbers of the player have been sent by
     * the server
     * @param carton list with the 5 numbers of the player
     * @param bolas list with all the numbers
     * @return boolean if there is a bingo
     */
    public static boolean comprobarCarton(ArrayList<Integer> carton, ArrayList<Integer> bolas) {
        boolean bingo = false;
        int coincidencias = 0;
        if (bolas.size() >= 5) {

            for (int numero : bolas) {
                for (int numero2 : carton) {
                    if (numero == numero2)
                        coincidencias++;
                }
            }

            if (coincidencias == 5)
                bingo = true;
        }
        return bingo;
    }
}
