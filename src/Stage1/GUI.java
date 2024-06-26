package Stage1;

import Stage1.modules.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class GUI extends JFrame {
    private static Passenger selectedPassenger;
    private final PassengerList passengerList;
    private final FlightList flightList;
    private JTextField lastNameTextField;
    private JTextField referenceTextField;

    /**
     * Constructs the GUI interface.
     * Initializes the GUI components and sets up actions to perform when the window is closing, such as saving data or generating reports.
     *
     * @param passengerList A list of passengers, used for storing and managing passenger information.
     * @param flightList    A list of flights, used for storing and managing flight information.
     */
    public GUI(PassengerList passengerList, FlightList flightList) {
        this.passengerList = passengerList;
        this.flightList = flightList;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    onExit();
                } catch (IOException | AllExceptions.NumberErrorException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public PassengerList getPassengerList() {
        return this.passengerList;
    }

    public FlightList getFlightList() {
        return this.flightList;
    }


    /**
     * Update baggage information for flights on exit and generate reports.
     *
     * @throws IOException                        If an I/O error occurs during the exit process.
     * @throws AllExceptions.NumberErrorException If a number error exception occurs during the exit process.
     */
    private void onExit() throws IOException, AllExceptions.NumberErrorException {
        this.flightList.renewAllFlight();
        new Report(this.flightList);
    }

    /**
     * Creates the flight check-in interface.
     * Builds a user login interface allowing users to log in using their last name and a matching reference code.
     */

    public void FlightCheckInGUI() {

        setTitle("Airport Check-in System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Using BoxLayout for panel layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Welcome to Airport Check-in System", SwingConstants.LEFT);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(headerLabel);
        mainPanel.add(createLoginPanel("Booking Reference:", new JTextField(20)));
        // mainPanel.add(createInputPanel("                First Name:", new JTextField(20)));
        mainPanel.add(createLoginPanel("               Last Name:", new JTextField(20)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> {
            try {
                onExit();
            } catch (IOException | AllExceptions.NumberErrorException ex) {
                throw new RuntimeException(ex);
            }
            System.exit(0);
        });

        JButton finishButton = new JButton("Log In");
        finishButton.addActionListener(e -> {

            // Open flight details window
            String lastName = lastNameTextField.getText();
            String reference = referenceTextField.getText();
            try {
                validateInputs();
                if (passengerList.matchPassenger(reference, lastName)) {
                    selectedPassenger = passengerList.findByRefCode(reference);
                    // If inputs are valid and correct, proceed to the next step
                    new FlightDetailsGUI(reference, passengerList, flightList).setVisible(true);
                    dispose(); // Close the current window
                }
            } catch (IllegalArgumentException |
                     AllExceptions.NoMatchingRefException |
                     AllExceptions.NameCodeMismatchException |
                     AllExceptions.NoMatchingFlightException ex) {
                ex.printStackTrace();
            }

        });

        buttonPanel.add(quitButton);
        buttonPanel.add(finishButton);

        mainPanel.add(buttonPanel);
        add(mainPanel);
    }

    /**
     * Validates the inputs for emptiness or invalidity.
     * Checks user input for last name and reference code to ensure they are not empty.
     *
     * @throws IllegalArgumentException If either last name or reference code is empty.
     */

    private void validateInputs() throws IllegalArgumentException {
        String lastName = lastNameTextField.getText().trim();
        String reference = referenceTextField.getText().trim();

        if (lastName.isEmpty() || reference.isEmpty()) {
            // Show dialog to remind passenger
            JOptionPane.showMessageDialog(null, "Last Name and Booking Reference cannot be empty.\n", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Last Name and Booking Reference cannot be empty.");
        }
    }

    /**
     * Creates a login panel for entering last name and booking reference.
     * Constructs a panel with a label and a text field for user input.
     *
     * @param label     The label for the input field.
     * @param textField The text field for user input.
     * @return A panel containing the label and text field.
     */
    public JPanel createLoginPanel(String label, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.add(new JLabel(label));

        if (label.trim().equals("Last Name:")) {
            //Set the object for the "Last Name" text box
            lastNameTextField = textField;
        } else if (label.trim().equals("Booking Reference:")) {
            // Set the object for the "Booking Reference" text box
            referenceTextField = textField;
        }

        panel.add(textField);
        return panel;
    }

    /**
     * Interface for displaying flight information.
     * Retrieves and displays flight-related information based on user's login information.
     */
    class FlightDetailsGUI extends JFrame {


        public FlightDetailsGUI(String reference, PassengerList passengerList, FlightList flightList) throws AllExceptions.NoMatchingRefException, AllExceptions.NoMatchingFlightException {

            setTitle("Flight Details");
            setSize(400, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel headerLabel = new JLabel("Your Flight Details", SwingConstants.LEFT);
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(headerLabel);

            String flightCode = selectedPassenger.getFlightCode();
            Flight selectedFlight = flightList.findByCode(flightCode);
            selectedPassenger = selectedFlight.getPassengerInFlight().findByRefCode(reference);
            //Add flight information
            mainPanel.add(createDetailPanel("Flight Number: ", selectedFlight.getFlightCode()));
            mainPanel.add(createDetailPanel("Carrier: ", selectedFlight.getCarrier()));
            mainPanel.add(createDetailPanel("Destination Airport: ", selectedFlight.getDestination()));
            mainPanel.add(createDetailPanel("Flight Max Passenger: ", String.valueOf(selectedFlight.getMaxPassengers())));
            mainPanel.add(createDetailPanel("Flight Max Baggage Weight: ", selectedFlight.getMaxBaggageWeight() + " kg"));
            mainPanel.add(createDetailPanel("Flight Max Baggage Volume: ", selectedFlight.getMaxBaggageVolume() / 1000000 + " cubic meters"));
            mainPanel.add(createDetailPanel("Your Purchased Baggage Weight: ", String.valueOf(selectedFlight.getBaggageInFlight().getFreeQuota())));


            JButton nextButton = new JButton("Next Step");
            JButton backButton = new JButton("Back");
            nextButton.addActionListener(e -> {
                // Close the current window
                this.dispose();
                // //Open the Baggage details window
                new BaggageDetailsGUI().setVisible(true);
            });
            backButton.addActionListener(e -> {
                this.dispose();
                GUI gui = new GUI(passengerList, flightList);
                gui.FlightCheckInGUI();
                gui.setVisible(true);
            });
            // To right-align the buttons below, use the FlowLayout manager
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(backButton);
            buttonPanel.add(nextButton);
            mainPanel.add(buttonPanel);
            add(mainPanel);
        }

        private JPanel createDetailPanel(String label, String value) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            panel.add(new JLabel(label));
            panel.add(new JLabel(value));
            return panel;
        }
    }

    /**
     * Interface for entering baggage information.
     * On this page, users are allowed to enter up to three pieces of their own baggage information.
     * When the user completes the input, the baggage information will be stored and whether to jump to the payment interface
     * or check-in success page based on the weight of the baggage carried by the user is determined.
     */
    class BaggageDetailsGUI extends JFrame {
        private double totalFee = 0;
        private JTextField
                weightField1, lengthField1, widthField1, heightField1,
                weightField2, lengthField2, widthField2, heightField2,
                weightField3, lengthField3, widthField3, heightField3;

        public BaggageDetailsGUI() {

            setTitle("Baggage Details");
            setSize(500, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel headerLabel = new JLabel("Please Enter Your Baggage Details", SwingConstants.LEFT);
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(headerLabel);

            JPanel buttonPanel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton clearBaggageButton = new JButton("Clear Baggage");
            clearBaggageButton.addActionListener(e -> removeBaggage());
            clearBaggageButton.addActionListener(e -> clearBaggageFields());
            buttonPanel1.add(clearBaggageButton);
            mainPanel.add(buttonPanel1);

            // Add the first baggage weight input area and its size input area
            mainPanel.add(createBaggageWeightPanel(1));
            mainPanel.add(createDimensionPanel(1));

            mainPanel.add(createBaggageWeightPanel( 2));
            mainPanel.add(createDimensionPanel(2));

            mainPanel.add(createBaggageWeightPanel( 3));
            mainPanel.add(createDimensionPanel(3));
            // Create a baggage weight input area (arranged vertically)
            JPanel weightPanel = new JPanel();
            weightPanel.setLayout(new BoxLayout(weightPanel, BoxLayout.Y_AXIS));

            // Create next button
            JButton nextButton = new JButton("Next Step");
            nextButton.addActionListener(e -> {

                // First try adding all luggage information to a temporary luggage list
                BaggageList tempBaggageList = selectedPassenger.getHisBaggageList();
                try {
                    if (isValidBaggage(weightField1, lengthField1, widthField1, heightField1)) {
                        Baggage tempBaggage = new Baggage(
                                Double.parseDouble(weightField1.getText()),
                                Double.parseDouble(lengthField1.getText()),
                                Double.parseDouble(widthField1.getText()),
                                Double.parseDouble(heightField1.getText())
                        );
                        tempBaggageList.addBaggage(tempBaggage);
                    }

                    if (isValidBaggage(weightField2, lengthField2, widthField2, heightField2)) {
                        Baggage tempBaggage = new Baggage(
                                Double.parseDouble(weightField2.getText()),
                                Double.parseDouble(lengthField2.getText()),
                                Double.parseDouble(widthField2.getText()),
                                Double.parseDouble(heightField2.getText())
                        );
                        tempBaggageList.addBaggage(tempBaggage);
                    }

                    if (isValidBaggage(weightField3, lengthField3, widthField3, heightField3)) {
                        Baggage tempBaggage = new Baggage(
                                Double.parseDouble(weightField3.getText()),
                                Double.parseDouble(lengthField3.getText()),
                                Double.parseDouble(widthField3.getText()),
                                Double.parseDouble(heightField3.getText())
                        );
                        tempBaggageList.addBaggage(tempBaggage);
                    }

                    selectedPassenger.setBaggageList(tempBaggageList);
                    double totalVolume = tempBaggageList.getTotalVolume();
                    double totalWeight = tempBaggageList.getTotalWeight();
                    totalFee = tempBaggageList.getTotalFee();

                    if (totalFee > 0) {
                        dispose();
                        new PaymentExtraFeeGUI(totalWeight, totalFee).setVisible(true);
                    } else {
                        new CongratsCheckInGUI(totalWeight).setVisible(true);
                        dispose();
                    }

                } catch (AllExceptions.InvalidBaggageInfoException ex) {
                    ex.printStackTrace();
                } catch (AllExceptions.IncompleteBaggageInfoException ex) {
                    // Handling exceptions with incomplete information for a single baggage
                    ex.printStackTrace();
                } catch (NumberFormatException ex) {
                    // Handle number format exceptions, such as when the user does not enter a valid number, etc.
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (AllExceptions.NumberErrorException ex) {
                    // Handling format error exceptions
                    ex.printStackTrace();
                }

            });
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> {
                this.dispose();
                String lastName = selectedPassenger.getLastName();
                String reference = selectedPassenger.getRefCode();
                try {
                    new FlightDetailsGUI(reference, passengerList, flightList).setVisible(true);
                } catch (AllExceptions.NoMatchingRefException | AllExceptions.NoMatchingFlightException ex) {
                    throw new RuntimeException(ex);
                }
            });

            JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel2.add(backButton);
            buttonPanel2.add(nextButton);
            mainPanel.add(buttonPanel2);

            add(mainPanel);
        }

        private static void removeBaggage() {
            // SelectedPassenger is the currently selected Passenger object
            if (selectedPassenger != null && selectedPassenger.getHisBaggageList() != null) {
                selectedPassenger.getHisBaggageList().clear();
                JOptionPane.showMessageDialog(null, "All baggage has been cleared.", "Baggage Cleared", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        /**
         * Clear the entered baggage information.
         */
        private void clearBaggageFields() {
            weightField1.setText("");
            lengthField1.setText("");
            widthField1.setText("");
            heightField1.setText("");

            weightField2.setText("");
            lengthField2.setText("");
            widthField2.setText("");
            heightField2.setText("");

            weightField3.setText("");
            lengthField3.setText("");
            widthField3.setText("");
            heightField3.setText("");
        }

        /**
         * Check whether the baggage information is valid.
         *
         * @param weightField The text field for weight.
         * @param lengthField The text field for length.
         * @param widthField  The text field for width.
         * @param heightField The text field for height.
         * @return True if the baggage information is valid, false otherwise.
         * @throws AllExceptions.IncompleteBaggageInfoException If any of the baggage information fields are incomplete.
         * @throws AllExceptions.InvalidBaggageInfoException    If any of the baggage information is invalid.
         */
        private boolean isValidBaggage(JTextField weightField, JTextField lengthField, JTextField widthField, JTextField heightField) throws AllExceptions.IncompleteBaggageInfoException, AllExceptions.InvalidBaggageInfoException {
            String weight = weightField.getText().trim();
            String length = lengthField.getText().trim();
            String width = widthField.getText().trim();
            String height = heightField.getText().trim();
            if (weight.isEmpty() &&
                    length.isEmpty() &&
                    width.isEmpty() &&
                    height.isEmpty()) {
                return false;
            }
            if (weight.isEmpty() ||
                    length.isEmpty() ||
                    width.isEmpty() ||
                    height.isEmpty()) {
                throw new AllExceptions.IncompleteBaggageInfoException();
            } else if (weight.equals("0") &&
                    length.equals("0") &&
                    width.equals("0") &&
                    height.equals("0")) {
                return false;
            } else if (weight.equals("0") ||
                    length.equals("0") ||
                    width.equals("0") ||
                    height.equals("0")) {
                throw new AllExceptions.InvalidBaggageInfoException();
            }
            return true;
        }

        private JPanel createBaggageWeightPanel(int baggageNumber) {
            JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            String labelText=String.format("Baggage %d:      Weight (kg):",baggageNumber);
            weightPanel.add(new JLabel(labelText));
            JTextField weightField = new JTextField(5);
            weightPanel.add(weightField);

            switch (baggageNumber) {
                case 1 -> weightField1 = weightField;
                case 2 -> weightField2 = weightField;
                case 3 -> weightField3 = weightField;
            }
            return weightPanel;
        }

        private JPanel createDimensionPanel(int baggageNumber) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel("                         Dimensions (cm):"));

            JTextField lengthField = new JTextField(3), widthField = new JTextField(3), heightField = new JTextField(3);
            panel.add(new JLabel("L:"));
            panel.add(lengthField);
            panel.add(new JLabel("W:"));
            panel.add(widthField);
            panel.add(new JLabel("H:"));
            panel.add(heightField);

            // According to the baggage number, point the corresponding length, width, and height text box variables to the created text box.
            switch (baggageNumber) {
                case 1 -> {
                    lengthField1 = lengthField;
                    widthField1 = widthField;
                    heightField1 = heightField;
                }
                case 2 -> {
                    lengthField2 = lengthField;
                    widthField2 = widthField;
                    heightField2 = heightField;
                }
                case 3 -> {
                    lengthField3 = lengthField;
                    widthField3 = widthField;
                    heightField3 = heightField;
                }
            }
            return panel;
        }
    }

    /**
     * Interface for paying extra luggage fees.
     * Displays the fees passengers need to pay for their luggage and the weight of the checked luggage.
     */
    class PaymentExtraFeeGUI extends JFrame {
        public PaymentExtraFeeGUI(double totalWeight, double totalFee) {
            setTitle("Pay Extra Fee");
            setSize(400, 250); // Window resized to better display layout
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            // Use BorderLayout as the main layout and prepare a new bottom panel for the bottom area
            setLayout(new BorderLayout(10, 10));

            // Top information area
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
            JLabel congratsLabel = new JLabel(" Congratulations! You have added " + totalWeight + " kg. Please pay extra fee.", SwingConstants.CENTER);
            JLabel feeLabel1 = new JLabel(" Extra Fee: GBP " + totalFee, SwingConstants.CENTER);
            JLabel feeLabel2 = new JLabel(" [Extra Fee = (Total Weight - Purchased Baggage Weight) * 50]", SwingConstants.CENTER);
            topPanel.add(congratsLabel);
            topPanel.add(feeLabel1);
            topPanel.add(feeLabel2);

            // Payment methods and icons on the left
            JPanel paymentMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            paymentMethodPanel.add(new JLabel("Payment Method:"));
            ImageIcon icon = new ImageIcon("src/img/payWays.jpg");
            Image image = icon.getImage();
            Image newImage = image.getScaledInstance(180, 120, java.awt.Image.SCALE_SMOOTH);
            ImageIcon newIcon = new ImageIcon(newImage);
            JLabel label = new JLabel(newIcon);
            paymentMethodPanel.add(label);

            // Bottom button panel, containing Back and Pay buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton payButton = new JButton("Pay");
            payButton.addActionListener(e -> {
                this.dispose();
                new CongratsCheckInGUI(totalWeight).setVisible(true);
            });
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> {
                this.dispose();
                BaggageDetailsGUI.removeBaggage();
                new BaggageDetailsGUI().setVisible(true);
            });
            buttonPanel.add(backButton);
            buttonPanel.add(payButton);

            // Bottom panel with integrated payment methods and buttons
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(paymentMethodPanel, BorderLayout.NORTH);
            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

            //Add a panel to a form
            add(topPanel, BorderLayout.NORTH);
            add(new JPanel(), BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

        }

    }

    /**
     * Interface for successful check-in confirmation.
     * Generates an interface for users to confirm successful check-in and provides options to check in the next passenger or exit the application.
     */

    class CongratsCheckInGUI extends JFrame {
        public CongratsCheckInGUI(double totalWeight) {
            setTitle("Congratulation!");
            setSize(500, 130);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel congratsLabel = new JLabel("Congratulations! You have successfully loaded your items.");
            JLabel weightLabel = new JLabel("The total weight is " + totalWeight + "kg.");
            mainPanel.add(congratsLabel);
            mainPanel.add(weightLabel);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton finishButton = new JButton("Finish");
            finishButton.addActionListener(e ->
            {
                selectedPassenger.checkIn();
                this.dispose();
                GUI gui = new GUI(passengerList, flightList);
                gui.FlightCheckInGUI();
                gui.setVisible(true);
            });
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> {
                this.dispose();
                BaggageDetailsGUI.removeBaggage();
                new BaggageDetailsGUI().setVisible(true);
            });
            buttonPanel.add(backButton);
            buttonPanel.add(finishButton);
            mainPanel.add(buttonPanel);
            add(mainPanel);
        }
    }
}
