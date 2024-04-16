import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner s = new Scanner(System.in);
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static String login = "";

    public static void main(String[] args) {
        boolean go = true;
        try {
            socket = new Socket("localhost", 8001);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (go) {
                System.out.println("\nWybierz opcję: ");
                switch (s.next()) {
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
        } catch (IOException e) {
            System.out.println("Nie udalo się połączyć z API");
            System.out.println("Błąd: " + e);
        } finally {
            try {
                socket.close();
                out.close();
                in.close();
            } catch (IOException e) {
                System.out.println("Błąd: " + e);
            }
        }
    }

    private static void login() {
        if (!login.isEmpty()) {
            System.out.println("\nJesteś już zalogowany!");
            return;
        }

        System.out.println("\nPodaj login i hasło: ");
        try {
            String myLogin = s.next();
            String myPass = s.next();
            String request = "typ:login;id:20;login:" + myLogin + ";haslo:" + myPass;

            String[] ramka = wyslij_odbierz(request);
            switch (ramka[2]) {
                case "status:200" -> {
                    System.out.println("\nZalogowno pomyślnie");
                    login = myLogin;
                }
                case "status:401" -> System.out.println("\nBłędny login lub hasło. Spróbuj ponownie");
                case "status:500" -> System.out.println("\nSerwer nie nawiązał połączenia z bazą danych. Spróbuj ponownie");
                default -> System.out.println("\nNieznany błąd. Spróbuj ponownie");
            }

        } catch (IOException | InputMismatchException e) {
            System.out.println("\nBłąd: " + e + ". Spróbuj ponownie");
        }
    }

    private static void register() {
        if (!login.isEmpty()) {
            System.out.println("\nJesteś już zalogowany!");
            return;
        }

        System.out.println("\nUtwórz login");
        try {
            String myLogin = s.next();
            System.out.println("Utwórz haslo");
            String myPass = s.next();
            String request = "typ:register;id:10;login:" + myLogin + ";haslo:" + myPass;

            String[] ramka = wyslij_odbierz(request);
            switch (ramka[2]) {
                case "status:200" -> System.out.println("\nZarejestrowano pomyślnie. Teraz zaloguj się");
                case "status:406" -> System.out.println("\nUrzytkownik o takim loginie już istnieje. Spróbuj ponownie");
                case "status:500" -> System.out.println("\nSerwer nie nawiązał połączenia z bazą danych. Spróbuj ponownie");
                default -> System.out.println("\nNieznany błąd. Spróbuj ponownie");
            }
        } catch (IOException | InputMismatchException e) {
            System.out.println("\nBłąd: " + e + ". Spróbuj ponownie");
        }
    }

    private static void posty() {
        System.out.println("\nPodaj ile ostatnich postów chcesz wyświetlić: ");
        try {
            int a = s.nextInt();
            String request = "typ:pobiez_posty;id:30;ilosc:" + a;

            String[] ramka = wyslij_odbierz(request);
            switch (ramka[2]){
                case "status:200" -> {
                    System.out.println("\nOstatnie" + a + "posty urzytkowników:");
                    for (int f1 = 3; f1 < ramka.length; f1++){
                        String[] wiadomosc = ramka[f1].split(":");
                        System.out.println(wiadomosc[0] + " " + wiadomosc[1] + "\n" + wiadomosc[2] + "\n") ;
                    }
                }
                case "status:206" -> {
                    System.out.println("\nNie uzyskano oczekiwanej liczby postów\nUzystane posty:");
                    for (int f1 = 3; f1 < ramka.length; f1++){
                        String[] wiadomosc = ramka[f1].split(":");
                        System.out.println(wiadomosc[0] + " " + wiadomosc[1] + "\n" + wiadomosc[2] + "\n") ;
                    }
                }
                case "status:500" -> System.out.println("\nSerwer nie nawiązał połączenia z bazą danych. Spróbuj ponownie");
                default -> System.out.println("\nNastąpił nieoczekiwany błąd");
            }
        } catch (IOException | InputMismatchException e){
            System.out.println("Błąd: " + e);
        }
    }

    private static void chat() {
        if (login.isEmpty()) {
            System.out.println("\nZaloguj się aby korzystać z tej opcji!");
            return;
        }

        System.out.println("Napisz wiadomość:");
        try{
            String wiadomosc = s.next();
            if (wiadomosc.contains(":")) throw new IllegalArgumentException();
            String request = "typ:nowa_wiadomosc;id:40;wiadomosc:";

            String[] ramka = wyslij_odbierz(request);
            switch (ramka[2]){
                case "status:200" -> System.out.println("Twoja wiadomość została wysłana");
                case "status:500" -> System.out.println("\nSerwer nie nawiązał połączenia z bazą danych. Spróbuj ponownie");
                default -> System.out.println("\nNastąpił nieoczekiwany błąd");
            }
        } catch (IOException | InputMismatchException e){
            System.out.println("Błąd: " + e);
        } catch (IllegalArgumentException e){
            System.out.println("Twoja wiadomość nie może zawierać znaku :");
        }
    }

    private static void upload() {
        if (login.isEmpty()) {
            System.out.println("\nZaloguj się aby korzystać z tej opcji!");
            return;
        }

        System.out.println("\nPodaj ścieżkę do pliku: ");
        try {
            String sciezka = s.next();
            System.out.println("Nazwij plik:");
            String nazwa = s.next();

            File plik = new File(sciezka);
            if (plik.exists()){
                System.out.println("Wysyłanie pliku:");
                long rozmiar = plik.length();

                if (rozmiar > 0){
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(plik));

                    int k;
                    long ostatni = 0;
                    byte[] data = new byte[512];
                    long bajt = 0;

                    while ((k = bis.read(data)) != -1){
                        String request = "typ:wysylanie;id:50;login:" + login + "nazwa:" + nazwa + ";rozmiar:" + rozmiar + ";pierwszy_byte:" + ostatni + ";offset:" + k + ";data:" + Base64.getEncoder().encodeToString(data);
                        out.println(request);
                        out.flush();
                        for (int f1 = 0; f1 < 512; f1++) data[f1] = 0;
                        bajt += k;
                        ostatni = bajt;
                    }
                    bis.close();

                    System.out.println("Wysyłanie pliku zakończone");
                } else {
                    String request = "typ:wysylanie;id:50;login:" + login + "nazwa:" + nazwa + ";rozmiar:" + rozmiar + ";pierwszy_byte:" + 0 + ";offset:" + 0 + ";data:";
                    out.println();
                    out.flush();
                    System.out.println("Wysyłanie pustego pliku zakończone");
                }
                String request = "typ:wysylanie;id:60;status:end";

                String[] ramka = wyslij_odbierz(request);
                switch (ramka[2]){
                    case "status:200" -> System.out.println("Plik wysłany pomyślnie");
                    case "status:500" -> System.out.println("\nSerwer nie nawiązał połączenia z bazą danych. Spróbuj ponownie");
                    default -> System.out.println("\nNastąpił nieoczekiwany błąd");
                }
            } else System.out.println("Plik nie istanieje");
        } catch (IOException | InputMismatchException e){
            System.out.println("Błąd: " + e);
        }
    }

    private static void download() {
        if (login.isEmpty()) {
            System.out.println("\nZaloguj się aby korzystać z tej opcji!");
            return;
        }

        String listaRequest = "typ:lista_plikow;id:70;login:" + login;
        try {
            String[] listaRamka = wyslij_odbierz(listaRequest);
            switch (listaRamka[2]){
                case "status:200" -> {
                    System.out.println("\nPili zapisane w serwerze: ");
                    for (int f1 = 3; f1 < listaRamka.length; f1++) System.out.println(listaRamka[f1]);
                }
                case "status:500" -> System.out.println("\nSerwer nie nawiązał połączenia z bazą danych. Spróbuj ponownie");
                default -> System.out.println("\nNastąpił nieoczekiwany błąd");
            }

            System.out.println("Wybierz plik do pobrania:");
            String nazwa = s.next();
            String plikRequest = "typ:pobierz_plik;id:80;nazwa:" + nazwa + ";login:" + login;

            String[] plikRamka = wyslij_odbierz(plikRequest);
            switch (plikRamka[2]){
                case "status:200" -> {
                    long rozmiar = Long.parseLong(plikRamka[4].substring(8));
                    long bajt = 0;
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(nazwa));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while (bajt < rozmiar){
                        String dataRamka = in.readLine();
                        byte[] data = Base64.getDecoder().decode(dataRamka);
                        bos.write(data);
                        bajt += data.length;
                    }
                    bos.close();
                    System.out.println("\nPobieranie pliku zakończone");
                }
                case "status:404" -> System.out.println("\nNie znaleziono pliku");
                case "status:500" -> System.out.println("\nSerwer nie nawiązał połączenia z bazą danych. Spróbuj ponownie");
                default -> System.out.println("\nNastąpił nieoczekiwany błąd");
            }
        } catch (IOException e){
            System.out.println("Błąd: " + e);
        }
    }

    public static void help() {
        System.out.println("\nPomoc:");
    }

    private static String[] wyslij_odbierz(String request) throws IOException {
        System.out.println("Wygenerowano ramkę: " + request);
        out.println(request);
        out.flush();

        String response = in.readLine();
        System.out.println("Otrzymano odpowiedź: " + response);
        return response.split(";");
    }
}