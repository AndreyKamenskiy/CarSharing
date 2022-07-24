package carsharing;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu implements Callbackable {

    private boolean isSingleRun = false;

    static class MenuItem implements Callbackable{
        private final String title;
        private final Callbackable action;

        public MenuItem(String title, Callbackable action) {
            this.title = title;
            this.action = action;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public void callback() {
            action.callback();
        }
    }

    private String tittle;
    private final List<MenuItem> items;

    private String exitItem = "Exit";

    public Menu() {
        tittle = "";
        items = new ArrayList<>();
    }

    public Menu addItem(MenuItem item) {
        this.items.add(item);
        return this;
    }

    public Menu setExitItem(String exitItem) {
        this.exitItem = exitItem;
        return this;
    }

    public Menu setTitle(String tittle) {
        this.tittle = tittle;
        return this;
    }

    @Override
    public void callback() {
        String menuString = makeMenuString();
        int choice;
        while (true){
            choice = getChoice(menuString,items.size());
            if (choice == 0) {
                break;
            }
            items.get(choice - 1).callback();
            if (isSingleRun) {
                break;
            }
        }
    }

    private String makeMenuString() {
        StringBuilder menu = new StringBuilder();
        if (tittle != null && tittle.length() > 0) {
            menu.append(String.format("%s%n", tittle));
        }
        int i = 1;
        for (MenuItem item : items) {
            menu.append(String.format("%d. %s%n", i++, item.getTitle()));
        }
        menu.append(String.format("0. %s%n", exitItem));
        return menu.toString();
    }

    private int getChoice(String menu, int maxValue) {
        int choice;
        Scanner in = new Scanner(System.in);
        do {
            System.out.print(menu);
            try {
                choice = Integer.parseInt(in.nextLine());
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println();
                choice = -1;
            }
        } while (choice < 0 || choice > maxValue);
        return choice;
    }

    public Menu singleRun() {
        isSingleRun = true;
        return this;
    }


}
