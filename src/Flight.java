public class Flight {
    private int flightId;
    private int flightNumber;
    private String source;
    private String destination;
    private String date;
    private String airline;
    private int departureTime;
    private int arrivalTime;
    private int seatsAvailable;

    public Flight(int flightId, int flightNumber, String source, String destination, String date,
                  String airline, int departureTime, int arrivalTime, int seatsAvailable) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.airline = airline;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.seatsAvailable = seatsAvailable;
    }

    // Getters
    public int getFlightId() { return flightId; }
    public int getFlightNumber() { return flightNumber; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getDate() { return date; }
    public String getAirline() { return airline; }
    public int getDepartureTime() { return departureTime; }
    public int getArrivalTime() { return arrivalTime; }
    public int getSeatsAvailable() { return seatsAvailable; }

    @Override
    public String toString() {
        return flightNumber + " | " + source + " -> " + destination + " | " + date +
               " | Airline: " + airline + " | Dep: " + departureTime + " | Arr: " + arrivalTime +
               " | Seats: " + seatsAvailable;
    }
}

