package GUI;

import models.*;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private boolean shouldExit = false;
    private JTextField lastNameTextField;
    private JTextField referenceTextField;
    private PassengerList passengerList;
    private FlightList flightList;
//    private Baggage oneBaggage;
//    private BaggageList baggageListOfOne;
    private Passenger passengerRef;

    public PassengerList getPassengerList() {
        return this.passengerList;
    }

    public FlightList getFlightList() {
        return this.flightList;
    }

    public boolean getShouldExit() {
        return shouldExit;
    }


    public GUI(PassengerList passengerList, FlightList flightList) {
        this.passengerList = passengerList;
        this.flightList = flightList;

//        for (int i = 0; i < this.flightList.size(); i++) {
//            System.out.println(this.flightList.get(i).getFlightCode());
//        }

    }

    public void FlightCheckInGUI() {


        setTitle("Airport Check-in System");
        setSize(400, 300); // 设置窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        // 使用BoxLayout进行面板布局
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Welcome to Airport Check-in System", SwingConstants.LEFT);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(headerLabel);

        // 输入面板的统一方法，优化代码重用
        // mainPanel.add(createInputPanel("                First Name:", new JTextField(20)));
        mainPanel.add(createLoginPanel("               Last Name:", new JTextField(20)));
        mainPanel.add(createLoginPanel("Booking Reference:", new JTextField(20)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));

        JButton finishButton = new JButton("Log In");
        finishButton.addActionListener(e -> {

            // 打开航班详情窗口（最终实现这里会有修改）

            String lastName = lastNameTextField.getText();
            String reference = referenceTextField.getText();
            try {
                validateInputs();
                passengerRef = passengerList.findByRefCode(reference);
                if(passengerRef.getLastName().equals(lastName)){
                    // If inputs are valid and correct, proceed to the next step
                    new FlightDetailsGUI(lastName, reference, passengerList, flightList).setVisible(true);
                    dispose(); // Close the current window
                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (AllExceptions.NoMatchingRefException ex){
                ex.printStackTrace();
            }

        });

        buttonPanel.add(quitButton);
        buttonPanel.add(finishButton);

        // 添加按钮面板到主面板
        mainPanel.add(buttonPanel);
        // 添加主面板到Frame
        add(mainPanel);

    }
    private void validateInputs() throws IllegalArgumentException {
        String lastName = lastNameTextField.getText().trim();
        String reference = referenceTextField.getText().trim();

        if (lastName.isEmpty() || reference.isEmpty()) {
            // Show dialog to remind passenger
            JOptionPane.showMessageDialog(null, "Last Name and Booking Reference cannot be empty.\n", "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException("Last Name and Booking Reference cannot be empty.");
        }
    }


    public JPanel createLoginPanel(String label, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.add(new JLabel(label));

        if (label.trim().equals("Last Name:")) {
            lastNameTextField = textField; // 为 "Last Name" 文本框设置对象
        } else if (label.trim().equals("Booking Reference:")) {
            referenceTextField = textField; // 为 "Booking Reference" 文本框设置对象
        }

        panel.add(textField);
        return panel;
    }

    class FlightDetailsGUI extends JFrame{

        private String lastName;
        private String reference;
        public FlightDetailsGUI(String lastName, String reference, PassengerList passengerList, FlightList flightList) throws AllExceptions.NoMatchingRefException {
            this.lastName = lastName;
            this.reference = reference;


            setTitle("Flight Details");
            setSize(400, 500); // 统一界面大小
            setLocationRelativeTo(null); // 居中显示
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel headerLabel = new JLabel("Your Flight Details", SwingConstants.LEFT);
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(headerLabel);

            Passenger passenger = passengerList.findByRefCode(reference);
            if (passenger != null) {
                String flightCode = passenger.getFlightCode();
                Flight flightDetails = flightList.findByCode(flightCode);
                if (flightDetails != null) {
                    //添加航班信息
                    mainPanel.add(createDetailPanel("Flight Number: ", flightDetails.getFlightCode()));
                    mainPanel.add(createDetailPanel("Carrier: ", flightDetails.getCarrier()));
                    mainPanel.add(createDetailPanel("Max Passenger: ", String.valueOf(flightDetails.getMaxPassengers())));
                    mainPanel.add(createDetailPanel("Max Baggage Weight: ", String.valueOf(flightDetails.getMaxBaggageWeight())));
                    mainPanel.add(createDetailPanel("Max Baggage Volume: ", String.valueOf(flightDetails.getMaxBaggageVolume())));

                } else {
                    // TODO 处理找不到航班的情况
                }
            } else {
                // TODO 处理找不到乘客的情况
            }



//            mainPanel.add(createDetailPanel("Airport of Destination: ", "Example Airport"));
//            mainPanel.add(createDetailPanel("Estimated time of Departure: ", "00:00"));
//            mainPanel.add(createDetailPanel("Estimated time of Arrival: ", "22:00"));
            JButton nextButton = new JButton("Next Step");
            nextButton.addActionListener(e -> {
                this.dispose(); // 关闭当前窗口
                // 打开航班详情窗口（最终实现这里会有修改）
                new BaggageDetailsGUI().setVisible(true);
            });

            // 为了在下方右对齐按钮，用FlowLayout管理器
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
    private JTextField weightField1;
    private JTextField lengthField1;
    private JTextField widthField1;
    private JTextField heightField1;
    class BaggageDetailsGUI extends JFrame{
        private double totalFee = 0;
        private JTextField weightField1, weightField2, weightField3;
        private JTextField lengthField1, widthField1, heightField1;
        private JTextField lengthField2, widthField2, heightField2;
        private JTextField lengthField3, widthField3, heightField3;

        private Baggage oneBaggage;
        private BaggageList allBaggageOfOne;
        public BaggageDetailsGUI(){
            this.oneBaggage = new Baggage();
            this.allBaggageOfOne = new BaggageList();

            setTitle("Baggage Details");
            setSize(500, 500); // 统一界面大小
            setLocationRelativeTo(null); // 居中显示
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel headerLabel = new JLabel("Please Enter Your Baggage Details", SwingConstants.LEFT);
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(headerLabel);

            // 添加第一个行李重量输入区域和它的尺寸输入区域
            mainPanel.add(createBaggageWeightPanel("Baggage 1:      Weight (kg):", 1));
            mainPanel.add(createDimensionPanel("                         Dimensions (cm):", 1));

            // 添加第二个行李重量输入区域和它的尺寸输入区域
            mainPanel.add(createBaggageWeightPanel("Baggage 2:      Weight (kg):", 2));
            mainPanel.add(createDimensionPanel("                         Dimensions (cm):", 2));

            // 添加第三个行李重量输入区域和它的尺寸输入区域
            mainPanel.add(createBaggageWeightPanel("Baggage 3:      Weight (kg):", 3));
            mainPanel.add(createDimensionPanel("                         Dimensions (cm):", 3));
            // 创建行李重量输入区域（纵向排列）
            JPanel weightPanel = new JPanel();
            weightPanel.setLayout(new BoxLayout(weightPanel, BoxLayout.Y_AXIS));

            // 创建下一步按钮
            JButton nextButton = new JButton("Next Step");
            nextButton.addActionListener(e -> {


                try {
                    oneBaggage.setWeight(Double.parseDouble(weightField1.getText()));
                    oneBaggage.setLength(Double.parseDouble(lengthField1.getText()));
                    oneBaggage.setWidth(Double.parseDouble(lengthField1.getText()));
                    oneBaggage.setHeight(Double.parseDouble(lengthField1.getText()));
                    allBaggageOfOne.addBaggage(oneBaggage);
                    oneBaggage.checkBaggage();

                    oneBaggage.setWeight(Double.parseDouble(weightField1.getText()));
                    oneBaggage.setLength(Double.parseDouble(lengthField1.getText()));
                    oneBaggage.setWidth(Double.parseDouble(lengthField1.getText()));
                    oneBaggage.setHeight(Double.parseDouble(lengthField1.getText()));
                    allBaggageOfOne.addBaggage(oneBaggage);
                    oneBaggage.checkBaggage();

                    oneBaggage.setWeight(Double.parseDouble(weightField1.getText()));
                    oneBaggage.setLength(Double.parseDouble(lengthField1.getText()));
                    oneBaggage.setWidth(Double.parseDouble(lengthField1.getText()));
                    oneBaggage.setHeight(Double.parseDouble(lengthField1.getText()));
                    allBaggageOfOne.addBaggage(oneBaggage);
                    oneBaggage.checkBaggage();

                    //TODO
                    double totalVolume = allBaggageOfOne.getTotalVolume();
                    double totalWeight = allBaggageOfOne.getTotalWeight();
                    totalFee = allBaggageOfOne.calculateTotalFee();
//                    System.out.println(totalWeight);
//                    System.out.println(totalFee);

                    if(totalFee > 0){
                        dispose();
                        new PaymentExtraFeeGUI(totalWeight, totalFee).setVisible(true);
                    }else {
                        new CongratsPaymentGUI().setVisible(true);
                        dispose();
                    }

                } catch (NumberFormatException ex) {
                    // 处理数字格式异常，例如用户未输入有效的数字等情况
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.\n" +
                            "Please enter 0 in each text box if you not have this baggage, thank you.", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (AllExceptions.FormatErrorException ex) {
                    // 处理格式错误异常
                    ex.printStackTrace();

                }

            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(nextButton);
            mainPanel.add(buttonPanel);

            add(mainPanel);
        }
        private JPanel createBaggageWeightPanel(String labelText, int baggageNumber) {
            JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            weightPanel.add(new JLabel(labelText));
            JTextField weightField = new JTextField(5);
            //weightPanel.add(new JTextField(5));

            weightPanel.add(weightField);

            switch (baggageNumber) {
                case 1:
                    weightField1 = weightField;
                    break;
                case 2:
                    weightField2 = weightField;
                    break;
                case 3:
                    weightField3 = weightField;
                    break;
            }
            return weightPanel;
        }

        private JPanel createDimensionPanel(String labelText, int baggageNumber) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.add(new JLabel(labelText));

            JTextField lengthField = new JTextField(3), widthField = new JTextField(3), heightField = new JTextField(3);
            panel.add(new JLabel("L:"));
            panel.add(lengthField);
            panel.add(new JLabel("W:"));
            panel.add(widthField);
            panel.add(new JLabel("H:"));
            panel.add(heightField);

            // 根据行李编号，将对应的长、宽、高文本框变量指向创建的文本框
            switch (baggageNumber) {
                case 1:
                    lengthField1 = lengthField;
                    widthField1 = widthField;
                    heightField1 = heightField;
                    break;
                case 2:
                    lengthField2 = lengthField;
                    widthField2 = widthField;
                    heightField2 = heightField;
                    break;
                case 3:
                    lengthField3 = lengthField;
                    widthField3 = widthField;
                    heightField3 = heightField;
                    break;
            }
            return panel;
        }
    }
    class PaymentExtraFeeGUI extends JFrame{
        public PaymentExtraFeeGUI(double totalWeight, double totalFee) {
            setTitle("Pay Extra Fee");
            setSize(600, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel congratsLabel = new JLabel("Congratulations! You have added " + totalWeight + " kg. Please pay extra fee.");
            mainPanel.add(congratsLabel);

            JLabel feeLabel = new JLabel("Please pay extra fee: $" + totalFee);
            mainPanel.add(feeLabel);

            // 添加支付方式图标和支付按钮
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            buttonPanel.add(new JLabel("Payment Method:"));
            ImageIcon icon = new ImageIcon("D:\\Learn\\fourth_year\\advanced\\visa.png");
            Image image = icon.getImage(); // 转换图标为 Image
            Image newImage = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH); // 调整图标大小
            ImageIcon newIcon = new ImageIcon(newImage); // 生成新的 ImageIcon

            JLabel label = new JLabel(newIcon);
            buttonPanel.add(label);


//            buttonPanel.add(new JLabel(new ImageIcon("D:\\Learn\\fourth_year\\advanced\\visa.png"))); // 替换为实际的图标文件路径
//            buttonPanel.add(new JLabel(new ImageIcon("D:\\Learn\\fourth_year\\advanced\\Paypal.png")));
//            buttonPanel.add(new JLabel(new ImageIcon("D:\\Learn\\fourth_year\\advanced\\wechat-1.png")));
//            buttonPanel.add(new JLabel(new ImageIcon("D:\\Learn\\fourth_year\\advanced\\Alipay.png")));
            JButton payButton = new JButton("Pay");
            payButton.addActionListener(e ->
            {
                this.dispose();
                new CongratsPaymentGUI().setVisible(true);
            });

            JButton backButton = new JButton("Back");
            backButton.addActionListener(e ->
            {
                this.dispose();
                new BaggageDetailsGUI().setVisible(true);
            });

            buttonPanel.add(payButton);
            buttonPanel.add(backButton);

            mainPanel.add(buttonPanel);

            add(mainPanel);
        }
    }

    class CongratsPaymentGUI extends JFrame{
        public CongratsPaymentGUI() {
            setTitle("Congratulation！");
            setSize(400, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel congratsLabel = new JLabel("Congratulations! You have successfully loaded your items.");
            mainPanel.add(congratsLabel);

            JButton finishButton = new JButton("Finish");
                finishButton.addActionListener(e ->
                {
                    this.dispose();
                    GUI gui = new GUI(passengerList,flightList);
                    gui.FlightCheckInGUI();
                    gui.setVisible(true);
                });
            mainPanel.add(finishButton);

            add(mainPanel);
        }
    }
//    public static void main(String[] args) {
//        EventQueue.invokeLater(() -> {
//            ShowGUI showGUI = new ShowGUI();
//            showGUI.FlightCheckInGUI();
//            showGUI.setVisible(true);
//        });
//
//    }
}