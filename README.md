# Harmonogram projektu #
Wyrażam zgodę na opublikowanie mojego kodu w celach edukacyjnych.
Celem projektu było stworzenie wielowątkowego chatu. Po uruchomieniu serwera chatu użytkownik podaje port nasłuchujący oraz listę pokoi oddzielonych średnikiem. Pola są wypełnione wartościami odczytanymi z pliku ustawień, a gdy ten nie istnieje, wartościami domyślnymi. Po wypełnieniu ustawień pojawia się okienko serwera, na którym znajdują się przyciski start, stop oraz tabela zalogowanych użytkowników z możliwością rozłączenia dowolnego z nich.
Po uruchomieniu serwera pojawia się okno z ustawieniami. W pierwszej zakładce użytkownik podaje adres i port serwera z którym się łączy. W kolejnej może się zarejestrować w bazie danych. W ostaniej zakładce użytkownik loguje się z użyciem podanego wcześniej hasła. Po pozytywnym przejściu trzech kroków nastąpi nawiązanie połączenia. Użytkownik zostanie przypisany do pokoju "Główny" i zostaniu mu przesłana lista ostatnich 200 wiadomości. 
## Wielowątkowy serwer chatu ##
### 1. Okienka ###
* *Okno chatu (15.03.2016)*
* *Okno rejestracji (17.05.2016)*
* *Okno logowania (15.03.2016)*
* *Okno historii (19.04.2016)*
### 2. Zapis i odczyt plików ###
* *Zapis historii rozmowy Odczyt historii rozmowy (19.04.2016)*
* *Zapis i odczyt konfiguracji klienta i serwera (12.04.2016)*
* *Zapis logów serwera (17.05.2016)*
### 3. Współbieżność ###
* *Wielowątkowość prosta (1 klient – 1 wątek), wątek nasłuchujący (8.03.2016)*
* *Mechanizm wrzucania wiadomości do skrzynek użytkowników (31.05.2016)*
* *Mechanizm przechowywania rozmowy (31.05.2016)*
### 4. Bazy danych ###
* *Baza danych użytkowników (JDBC, CRUD) (24.05.2016)*
* *Logowanie i rejestracja (logika leżąca po stronie serwera) sprawdzanie listy zalogowanych użytkowników (24.05.2016)*
* *Przetwarzanie nowych konwersacji (24.05.2016)*
### 5. Komunikacja sieciowa ###
* *Klasa serwer: nasłuchiwanie i akceptowanie połączenia (1.03.2016)*
* *Klasa klient: obsługa zdarzeń (8.03.2016)*
* *Obsługa plików (11.06.2016)*
* *Odczytanie wiadomości, wysłanie odpowiedzi (po stronie serwera i klienta), zorganizowanie protokołu komunikacji (1.03.2016)*
* *Wyrzucanie użytkowników wysyłających zbyt dużo wiadomości (11.06.2016)*
* *Administracja zalogowanymi użytkownikami (SPAM) (16.06.2016)*
### 6. Zaproponowane przez studenta ###
* *Pokoje rozmów (5.04.2016)*
* *GUI + logika na serwerze (12.04.2016)*
* *Wysyłanie prywatnych wiadomości (5.04.2016)*
* *Możliwość przesyłania plików przez sieć (10.05.2016)*
* *Historia rozmów (19.04.2016)*