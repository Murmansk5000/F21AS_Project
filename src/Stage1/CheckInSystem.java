package Stage1;

import Stage1.modules.AllExceptions;
import Stage1.modules.FlightList;
import Stage1.modules.PassengerList;

/**
 * The CheckInSystem class serves as the entry point to the application and demonstrates the functionality of the first phase of the project.
 * <p>
 * At this stage, the application provides a graphical user interface (GUI) that allows passengers to perform check-in operations.
 * The application demonstrates how the GUI receives user input and handles the passenger check-in process, including passenger information validation and baggage fee calculation.
 * <p>
 * Example of use:
 * <pre>
 * public static void main(String[] args) {
 *     ProgrammeDemo demo = new ProgrammeDemo();
 *     demo.showDemo();
 * }
 * </pre>
 *
 * @version 1.0
 * @since 2024-03-14
 */
public class CheckInSystem {
    private static final String PASSENGER_DATA_FILE = "file/PassengerList.txt";
    private static final String FLIGHT_DATA_FILE = "file/FlightList.txt";
    private static PassengerList paxList;
    private static FlightList fltList;

    public CheckInSystem() throws AllExceptions.NoMatchingFlightException {
        // Initialize empty list of passengers and flights
        // Load passengers from TXT file directly into paxList
        paxList = new PassengerList();
        paxList.loadPassengersFromTXT(PASSENGER_DATA_FILE);
        // Load flights from TXT file directly into fltList
        fltList = new FlightList();
        fltList.loadFlightsFromTXT(FLIGHT_DATA_FILE);
        fltList.addPassengersToFlights(paxList);
    }

    public static void main(String[] args) throws AllExceptions.NoMatchingFlightException {
        CheckInSystem sld = new CheckInSystem();
        sld.showGUI(paxList, fltList);
    }

    /**
     * Show GUI
     */
    public void showGUI(PassengerList passengerList, FlightList flightList) {
        GUI gui = new GUI(passengerList, flightList);
        gui.FlightCheckInGUI();
        gui.setVisible(true);
    }
}
