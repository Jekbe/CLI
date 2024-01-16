import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final Scanner s = new Scanner(System.in);
    private static String login = "";
    static Socket socket;
    public static void main(String[] args){
        boolean go = true;
        try{
            socket = new Socket("localhost", 8001);
        } catch (IOException e) {
            System.out.println("Nie udalo się połączyć z API");
            System.out.println("Błąd: " + e);
            return;
        }

        while (go){
            System.out.println("Wybierz opcję: ");
            switch (s.next()){
                case "help" -> help();
                case "login" -> login();
                case "register" -> register();
                case "posty" -> posty();
                case "chat" -> chat();
                case "upload" -> upload();
                case "download" -> download();
                case "exit" -> go = false;
                default -> System.out.println("Nieznana opcja");
            }
        }

    }

    public static void login(){
        if (!login.isEmpty()){
            System.out.println("Jesteś już zalogowany!");
            return;
        }

        System.out.println("Podaj login i hasło: ");
        String myLogin = s.next();
        String myPass = s.next();

        login = myLogin;
    }

    public static void register(){
        if (!login.isEmpty()){
            System.out.println("Jesteś już zalogowany!");
            return;
        }

        System.out.println("Utwórz login");
        String login = s.next();
    }

    public static void posty(){
        System.out.println("Ostatnie posty urzytkowników");
    }

    public static void chat(){
        if (login.isEmpty()){
            System.out.println("Zaloguj się aby korzystać z tej opcji!");
            return;
        }

        System.out.println("Chat");
    }

    public static void upload(){
        if (login.isEmpty()){
            System.out.println("Zaloguj się aby korzystać z tej opcji!");
            return;
        }

        System.out.println("Podaj ścieżkę do pliku: ");
    }

    public static void download(){
        if (login.isEmpty()){
            System.out.println("Zaloguj się aby korzystać z tej opcji!");
            return;
        }

        System.out.println("Pili zapisane w serwerze: ");
    }

    public static void help(){
        System.out.println("Pomoc:");
    }


}