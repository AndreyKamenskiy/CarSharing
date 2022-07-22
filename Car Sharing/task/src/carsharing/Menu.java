package carsharing;

import java.util.List;
import java.util.Scanner;

public class Menu implements Callbackable {

    class MenuItem implements Callbackable{
        private String title;
        private Callbackable action;

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

    private final String tittle;
    private List<MenuItem> items;

    private String exitItem = "Exit";

    public Menu(String tittle, List<MenuItem> items) {
        this.tittle = tittle;
        this.items = items;
    }

    public Menu addItem(MenuItem item) {
        this.items.add(item);
        return this;
    }

    public Menu setExitItem(String exitItem) {
        this.exitItem = exitItem;
        return this;
    }

    @Override
    public void callback() {
        String menuString = makeMenuString();
        int choice = 0;
        while (true){
            choice = getChoice(menuString,items.size());
            if (choice == 0) {
                break;
            }
            items.get(choice - 1).callback();
        }
    }

    private String makeMenuString() {
        StringBuilder menu = new StringBuilder();
        menu.append(String.format("%s:%n", tittle));
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
            System.out.println(menu);
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

}
