package com.example.demo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    public static File start(InputStream stream) throws IOException {

        if (stream == null) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        File outputFile = new File("output.txt");
        FileOutputStream output = new FileOutputStream(outputFile);

        Hero hero = new Hero();

        Random r = new Random();

        Combat c = new Combat();

        introduction(hero, r, output, reader);

        shopping(hero, output, reader);

        firstFight(hero, r, c, output, reader);

        try {
            while (hero.getGameIsRunning()) {runGame(hero, r, c, output, reader);}
        } catch (EOFException e) {
            return outputFile;
        }

        return outputFile;
    }

    //Copy and Paste all of this in the beginning to skip to arena

    // Jeanius123 Mage No 1 2 0 1 1 1 1 1 1 1 1 1 1 (to skip intro and go to temple to heal)
    // 0 0 HP 2 2 Yes (to return to town, skip fountain and guard talk)
    // 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 2 1 (to defeat guard)
    // 0 1 1 2 (to go fight priest)
    // 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 (to defeat priest)
    // No No Yes No Yes No Yes Yes Yes Yes (to skip grave)
    // 0 2 2 0 4 Hello (to go to manor)
    // 2 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1
    // 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 2 2 1 (to defeat tycoon)
    // 0 2 2 0 5 3 4 (buy sword and enter arena)

    /* 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9
       9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 (to lose 350HP and die)*/

    /* 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
         1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 (to get money in tavern)*/

    //guard 200HP attack-defend
    //priest 300HP attack-attack-defend
    //tycoon 500HP defend-defend-attack


    public static void runGame(Hero hero, Random r, Combat c, FileOutputStream output, BufferedReader reader) throws IOException {

        if (!reader.ready()) {
            throw new EOFException();
        }

        switch (hero.getLocation()) {

            case "Town":
                Town.atTown(hero, output, reader);
                break;
            case "Temple":
                Temple.atTemple(hero, r, c, output, reader);
                break;
            case "Grave":
                Grave.atGrave(hero, output, reader);
                hero.setLocation("Temple");
                break;
            case "Castle":
                Castle.atCastle(hero, r, c, output, reader);
                break;
            case "Tavern":
                Tavern.atTavern(hero, output, reader);
                break;
            case "Manor":
                Manor.atManor(hero, r, c, output, reader);
                hero.setLocation("Town");
                break;
            case "Arena":
                Arena.atArena(hero, r, c, output, reader);
                hero.setLocation("Town");
                break;
            case "Shop":
                Shop.atShop(hero, output, reader);
                hero.setLocation("Town");
                break;
        }

    }



    public static void introduction(Hero hero, Random r, FileOutputStream output, BufferedReader reader) throws IOException {

        if (!reader.ready()) {
            return;
        }

        try {
            hero.setHeroStat(output, reader);
        } catch (EOFException e) {
            return;
        }

        boolean heroIsCreated;

        do {

            hero.assignStat(r);

            hero.showStat(output);

            try {
                heroIsCreated = !getBoolean("Do you want to re-roll?", output, reader);
            } catch (EOFException e) {
                return;
            }

        } while (!heroIsCreated);

        output.write(("\n").getBytes());
    }

    public static void shopping(Hero hero, FileOutputStream output, BufferedReader reader) throws IOException {

        if (!reader.ready()) {
            return;
        }

        hero.setGold(Hero.INITIAL_GOLD);

        boolean[] shopStatus = new boolean[] {true, false, false, false}; //shopLoop, swordSold, shieldSold, masterSold

        while (shopStatus[0]) {

            Shop.printShop(shopStatus[1], shopStatus[2], shopStatus[3], hero, output);

            int itemToBuy;

            try {
                itemToBuy = getInt(output, reader);
            } catch (EOFException e) {
                return;
            }

            boolean[] soldStatus = Shop.buyItem(shopStatus[1], shopStatus[2], shopStatus[3], itemToBuy, hero, output);

            System.arraycopy(soldStatus, 0, shopStatus, 0, 4);

            output.write(("\n").getBytes());

        }

    }

    public static void firstFight(Hero hero, Random r, Combat c, FileOutputStream output, BufferedReader reader) throws IOException {

        if (!reader.ready()) {
            return;
        }

        output.write(("After a step out of the shop..." + "\n").getBytes());

        Monster Madman = new Monster(50, "Madman", new boolean[] {true});

        boolean madManDefeated = false;

        while (!madManDefeated) {

            c.fight(hero, Madman, r, output, reader);

            madManDefeated = (Madman.getHp() <= 0);

            if(madManDefeated) {
                output.write(("Nice first fight! Let's move on to the town square." + "\n\n").getBytes());
            } else {
                output.write(("You lost to your opponent! Let's try this one more time." + "\n\n").getBytes());
                hero.setHp(hero.getMaxHp());
            }

        }

        hero.setLocation("Town");

    }



    public static int getInt(FileOutputStream output, BufferedReader reader) throws IOException {

        boolean inputIsInt = false;
        int option = 0;

        while (!inputIsInt) {

            if (reader.ready()) {
                try {
                    option = Integer.parseInt(reader.readLine());
                    output.write((option + "\n").getBytes());
                    inputIsInt = true;
                } catch (NumberFormatException e) {
                    output.write(("Enter an integer: ").getBytes());
                }
            } else {
                throw new EOFException();
            }

        }

        return option;
    }

    public static boolean getBoolean(String prompt, FileOutputStream output, BufferedReader reader) throws IOException {

        String accept = "";

        while (!(accept.equals("Yes") || accept.equals("No"))) {

            output.write((prompt + " (Yes or No): ").getBytes());

            if (reader.ready()) {
                accept = reader.readLine();
                output.write((accept + "\n").getBytes());
            } else {
                throw new EOFException();
            }
        }

        return accept.equals("Yes");

    }

}
