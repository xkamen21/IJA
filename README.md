# IJA
### Projekt:
    Aplikace pro zobrazení linek hromadné dopravy a sledování jejich pohybu

### Autoři:
    Daniel Kamenický (xkamen21)

### Popis:
    Aplikace po spuštění načte mapu. Mapa se skládá z ulic, zastávek a vozidel. Dále se v aplikaci vyskytují jednotlivé linky s konkrétním rozpisem výjezdů.
    Během průběhu plynutí času vozidla, reprezentujicí autobusy dopravní společnosti, vyjiždí ze svých počatečních bodů. Aplikace simuluje průběh dopravy autobusů v čase.
    Aplikace nadále umožňuje změnu času, nastavení zpoždění na celé délce konkrétní ulice a změnu rychlosti plynutí času.

### Postavení a spuštění aplikace:
    Pro správné spuštění aplikace postupně následujte níže zmíněné kroky"
        - nejdříve běžte do složky 'lib' a  zde spusťte skript get-libs.sh (při nefunkčnosti nastavte skriptu správná práva 'chmod 755 get-libs.sh')
        - v dalšímkroku běžte do hlavního adresáře a zde spusťte ant compiler (příkaz 'ant compile')
        - aplikaci spusťte pomocí příkazu 'ant run'
        - po spuštění vyberte zdrojový JSON soubor s daty, který se nachází v adresáři 'data'
        - pro vyčištění použijte příkaz 'ant clear'
