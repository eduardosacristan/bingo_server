package bingoApp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents the server of the game. It has the main method
 */
public class BingoServer {

    public static final int PORT = 2010;
    public static final int MAX_CONNECTIONS = 3;
    public static List<Integer> numerosSacados = sacarTodos();
    public static boolean finDeJuego = false;

    /**
     * The main method of the server application. It creates the connexion and waits
     * until all the players are connected to start the game
     * @param args
     */
    public static void main(String[]args) {
        System.out.println("Waiting for players...");
        //numerosSacados.forEach(System.out::println);
        List<ServerThread> conexiones = new ArrayList<>();

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (conexiones.size() < MAX_CONNECTIONS) {
                Socket service = server.accept();
                ServerThread thread = new ServerThread(service);
                conexiones.add(thread);
                System.out.println("Number of players: " + conexiones.size());
            }

            for(ServerThread c : conexiones)
                c.start();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * This method creates a random numbers list
     * @return
     */
    private static List<Integer> sacarTodos() {
        List<Integer> numbers = IntStream.rangeClosed(1, 20)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(numbers);
        return numbers;
    }
}
