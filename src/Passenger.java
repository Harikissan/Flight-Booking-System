import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Passenger {

public static void bookTicket(Connection conn, Scanner sc) {

    try {

        System.out.print("Enter passenger name: ");
        String name = sc.next();

        System.out.print("Enter email: ");
        String email = sc.next();

        System.out.print("Enter passport number: ");
        String passport = sc.next();

        System.out.print("Enter flight number: ");
        int flightNumber = sc.nextInt();

        PreparedStatement checkFlight = conn.prepareStatement(
                "SELECT flight_id, seats_available FROM flights WHERE flight_number = ?");

        checkFlight.setInt(1, flightNumber);

        ResultSet rs = checkFlight.executeQuery();

        if (rs.next()) {

            int flightId = rs.getInt("flight_id");
            int seatsAvailable = rs.getInt("seats_available");

            if (seatsAvailable > 0) {

                int seatNo = 51 - seatsAvailable + 1;

                PreparedStatement insertBooking = conn.prepareStatement(
                        "INSERT INTO bookings (passenger_name, email, passport, flight_id, seat_no, status) VALUES (?, ?, ?, ?, ?, 'CONFIRMED')");

                insertBooking.setString(1, name);
                insertBooking.setString(2, email);
                insertBooking.setString(3, passport);
                insertBooking.setInt(4, flightId);
                insertBooking.setInt(5, seatNo);

                int inserted = insertBooking.executeUpdate();

                if (inserted > 0) {

                    PreparedStatement updateSeats = conn.prepareStatement(
                            "UPDATE flights SET seats_available = seats_available - 1 WHERE flight_id = ?");

                    updateSeats.setInt(1, flightId);
                    updateSeats.executeUpdate();

                    System.out.println("\n=================================");
                    System.out.println("Ticket Booked Successfully");
                    System.out.println("Passenger Name : " + name);
                    System.out.println("Flight Number  : " + flightNumber);
                    System.out.println("Seat Number    : " + seatNo);
                    System.out.println("=================================\n");

                } else {
                    System.out.println("Failed to book ticket.");
                }

            } else {
                System.out.println("No seats available.");
            }

        } else {
            System.out.println("Invalid flight number.");
        }

    } catch (Exception e) {
        System.out.println("Error while booking: " + e.getMessage());
    }
}

}
