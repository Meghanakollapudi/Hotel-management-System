import java.io.*;
import java.util.*;

public class hotel {

    enum RoomCategory {
        STANDARD, DELUXE, SUITE
    }

    static class Room implements Serializable {
        int roomId;
        RoomCategory category;
        boolean isAvailable;
        double price;

        Room(int roomId, RoomCategory category, double price) {
            this.roomId = roomId;
            this.category = category;
            this.price = price;
            this.isAvailable = true;
        }
    }

    static class Booking implements Serializable {
        int bookingId;
        String customerName;
        Room room;
        int days;
        double totalAmount;

        Booking(int bookingId, String customerName, Room room, int days) {
            this.bookingId = bookingId;
            this.customerName = customerName;
            this.room = room;
            this.days = days;
            this.totalAmount = room.price * days;
        }
    }

    static List<Room> rooms = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();
    static int bookingCounter = 1;

    static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("hotel.dat"))) {
            oos.writeObject(rooms);
            oos.writeObject(bookings);
        } catch (Exception e) {
            System.out.println("Error saving data");
        }
    }

    static void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("hotel.dat"))) {
            rooms = (List<Room>) ois.readObject();
            bookings = (List<Booking>) ois.readObject();
        } catch (Exception e) {
            // First run – file may not exist
        }
    }

    static void initRooms() {
        if (rooms.isEmpty()) {
            rooms.add(new Room(101, RoomCategory.STANDARD, 1500));
            rooms.add(new Room(102, RoomCategory.DELUXE, 2500));
            rooms.add(new Room(103, RoomCategory.SUITE, 4000));
        }
    }

    static void searchRooms(RoomCategory category) {
        System.out.println("Available Rooms:");
        for (Room r : rooms) {
            if (r.category == category && r.isAvailable) {
                System.out.println("Room " + r.roomId + " | Price: " + r.price);
            }
        }
    }

    static void bookRoom(String name, int roomId, int days) {
        for (Room r : rooms) {
            if (r.roomId == roomId && r.isAvailable) {
                r.isAvailable = false;
                Booking b = new Booking(bookingCounter++, name, r, days);
                bookings.add(b);

                System.out.println("Payment Successful!");
                System.out.println("Booking ID: " + b.bookingId);
                System.out.println("Total Amount: " + b.totalAmount);

                saveData();
                return;
            }
        }
        System.out.println("Room not available!");
    }

    static void cancelBooking(int bookingId) {
        Iterator<Booking> it = bookings.iterator();
        while (it.hasNext()) {
            Booking b = it.next();
            if (b.bookingId == bookingId) {
                b.room.isAvailable = true;
                it.remove();
                System.out.println("Booking Cancelled Successfully");
                saveData();
                return;
            }
        }
        System.out.println("Booking ID not found");
    }

    static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found");
            return;
        }

        for (Booking b : bookings) {
            System.out.println(
                    "ID: " + b.bookingId +
                            ", Name: " + b.customerName +
                            ", Room: " + b.room.roomId +
                            ", Days: " + b.days +
                            ", Amount: " + b.totalAmount);
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        loadData();
        initRooms();

        while (true) {
            System.out.println("\n--- HOTEL RESERVATION SYSTEM ---");
            System.out.println("1. Search Room");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View Bookings");
            System.out.println("5. Exit");
            System.out.print("Choose: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("1. STANDARD  2. DELUXE  3. SUITE");
                    int c = sc.nextInt();
                    searchRooms(RoomCategory.values()[c - 1]);
                    break;

                case 2:
                    System.out.print("Customer Name: ");
                    String name = sc.next();
                    System.out.print("Room ID: ");
                    int roomId = sc.nextInt();
                    System.out.print("No. of Days: ");
                    int days = sc.nextInt();
                    bookRoom(name, roomId, days);
                    break;

                case 3:
                    System.out.print("Booking ID: ");
                    int bid = sc.nextInt();
                    cancelBooking(bid);
                    break;

                case 4:
                    viewBookings();
                    break;

                case 5:
                    System.out.println("Thank you!");
                    System.exit(0);
            }
        }
    }
}
