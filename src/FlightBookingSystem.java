import java.sql.*;
import java.util.*;

public class FlightBookingSystem {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DBConnection.getConnection();

            System.out.println("Connected to database successfully.");

            while (true) {
                System.out.println("\n========= FLIGHT BOOKING SYSTEM =========");
                System.out.println("1. Book a Flight");
                System.out.println("2. Cancel Ticket");
                System.out.println("3. Display Booked Passengers");
                System.out.println("4. Check Seat Availability");
                System.out.println("5. Admin Login");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        bookFlight(conn, sc);
                        break;

                    case 2:
                        cancelTicket(conn, sc);
                        break;

                    case 3:
                        displayBookedPassengers(conn, sc);
                        break;

                    case 4:
                        checkSeatAvailability(conn, sc);
                        break;

                    case 5:
                        adminSection(conn, sc);
                        break;

                    case 6:
                        System.out.println("Exiting... Thank you!");
                        conn.close();
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void bookFlight(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter your name: ");
            String name = sc.next();

            System.out.print("Enter email: ");
            String email = sc.next();

            System.out.print("Enter passport number: ");
            String passport = sc.next();

            System.out.print("Enter flight number: ");
            int flightNumber = sc.nextInt();

            PreparedStatement getFlightId = conn.prepareStatement(
                "SELECT flight_id, seats_available FROM flights WHERE flight_number = ?");
            getFlightId.setInt(1, flightNumber);
            ResultSet rs = getFlightId.executeQuery();

            if (rs.next()) {
                int flightId = rs.getInt("flight_id");
                int seatsAvailable = rs.getInt("seats_available");

                if (seatsAvailable > 0) {
                    PreparedStatement bookStmt = conn.prepareStatement(
                        "INSERT INTO bookings (passenger_name, email, passport, flight_id, seat_no, status) VALUES (?, ?, ?, ?, ?, 'CONFIRMED')");
                    bookStmt.setString(1, name);
                    bookStmt.setString(2, email);
                    bookStmt.setString(3, passport);
                    bookStmt.setInt(4, flightId);
                    bookStmt.setInt(5, 51 - seatsAvailable + 1); // Assuming max 50 seats
                    bookStmt.executeUpdate();

                    PreparedStatement updateSeats = conn.prepareStatement(
                        "UPDATE flights SET seats_available = seats_available - 1 WHERE flight_id = ?");
                    updateSeats.setInt(1, flightId);
                    updateSeats.executeUpdate();

                    System.out.println("✅ Ticket booked successfully!");
                } else {
                    System.out.println("❌ No seats available.");
                }
            } else {
                System.out.println("❌ Invalid flight number.");
            }
        } catch (Exception e) {
            System.out.println("Error during booking: " + e.getMessage());
        }
    }

    public static void cancelTicket(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter your passport number: ");
            String passport = sc.next();
            System.out.print("Enter flight number: ");
            int flightNumber = sc.nextInt();

            PreparedStatement getFlightId = conn.prepareStatement(
                "SELECT flight_id FROM flights WHERE flight_number = ?");
            getFlightId.setInt(1, flightNumber);
            ResultSet rs = getFlightId.executeQuery();

            if (rs.next()) {
                int flightId = rs.getInt("flight_id");
                PreparedStatement cancelStmt = conn.prepareStatement(
                    "UPDATE bookings SET status = 'CANCELLED' WHERE passport = ? AND flight_id = ? AND status = 'CONFIRMED'");
                cancelStmt.setString(1, passport);
                cancelStmt.setInt(2, flightId);
                int rowsAffected = cancelStmt.executeUpdate();

                if (rowsAffected > 0) {
                    PreparedStatement updateSeats = conn.prepareStatement(
                        "UPDATE flights SET seats_available = seats_available + 1 WHERE flight_id = ?");
                    updateSeats.setInt(1, flightId);
                    updateSeats.executeUpdate();

                    System.out.println("✅ Ticket cancelled successfully.");
                } else {
                    System.out.println("❌ No confirmed booking found.");
                }
            } else {
                System.out.println("❌ Invalid flight number.");
            }
        } catch (Exception e) {
            System.out.println("Error during cancellation: " + e.getMessage());
        }
    }

    public static void displayBookedPassengers(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter flight number: ");
            int flightNumber = sc.nextInt();

            PreparedStatement getFlightId = conn.prepareStatement(
                "SELECT flight_id FROM flights WHERE flight_number = ?");
            getFlightId.setInt(1, flightNumber);
            ResultSet rs = getFlightId.executeQuery();

            if (rs.next()) {
                int flightId = rs.getInt("flight_id");
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT passenger_name, email, passport, seat_no FROM bookings WHERE flight_id = ? AND status = 'CONFIRMED'");
                ps.setInt(1, flightId);
                ResultSet bookings = ps.executeQuery();

                System.out.println("\nBooked Passengers:");
                while (bookings.next()) {
                    System.out.println(
                        "Name: " + bookings.getString("passenger_name") +
                        ", Email: " + bookings.getString("email") +
                        ", Passport: " + bookings.getString("passport") +
                        ", Seat: " + bookings.getInt("seat_no")
                    );
                }
            } else {
                System.out.println("❌ Invalid flight number.");
            }
        } catch (Exception e) {
            System.out.println("Error displaying passengers: " + e.getMessage());
        }
    }

    public static void checkSeatAvailability(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter flight number: ");
            int flightNumber = sc.nextInt();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT seats_available FROM flights WHERE flight_number = ?");
            ps.setInt(1, flightNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int available = rs.getInt("seats_available");
                System.out.println("Available seats: " + available);
            } else {
                System.out.println("❌ Flight not found.");
            }
        } catch (Exception e) {
            System.out.println("Error checking availability: " + e.getMessage());
        }
    }

    public static void adminSection(Connection conn, Scanner sc) {
        try {
            System.out.print("Enter admin password: ");
            String password = sc.next();

            if (!password.equals("Kissan1221")) {
                System.out.println("❌ Incorrect password.");
                return;
            }

            System.out.println("\n===== Admin Section =====");
            System.out.println("1. View All Flights");
            System.out.println("2. Add New Flight");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM flights");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println(
                        "Flight No: " + rs.getInt("flight_number") +
                        ", From: " + rs.getString("source") +
                        ", To: " + rs.getString("destination") +
                        ", Date: " + rs.getDate("date") +
                        ", Airline: " + rs.getString("airline") +
                        ", Seats Available: " + rs.getInt("seats_available")
                    );
                }
            } else if (choice == 2) {
                System.out.print("Enter flight number: ");
                int fno = sc.nextInt();
                System.out.print("Enter source: ");
                String source = sc.next();
                System.out.print("Enter destination: ");
                String dest = sc.next();
                System.out.print("Enter date (YYYY-MM-DD): ");
                String date = sc.next();
                System.out.print("Enter airline: ");
                String airline = sc.next();
                System.out.print("Enter departure time (HHMM): ");
                int dep = sc.nextInt();
                System.out.print("Enter arrival time (HHMM): ");
                int arr = sc.nextInt();
                System.out.print("Enter total seats: ");
                int seats = sc.nextInt();

                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO flights (flight_number, source, destination, date, airline, departure_time, arrival_time, seats_available) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setInt(1, fno);
                ps.setString(2, source);
                ps.setString(3, dest);
                ps.setDate(4, java.sql.Date.valueOf(date));
                ps.setString(5, airline);
                ps.setInt(6, dep);
                ps.setInt(7, arr);
                ps.setInt(8, seats);
                ps.executeUpdate();

                System.out.println("✅ Flight added successfully.");
            }

        } catch (Exception e) {
            System.out.println("Admin error: " + e.getMessage());
        }
    }
}
