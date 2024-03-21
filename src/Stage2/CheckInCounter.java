package Stage2;

import Stage1.*;

import java.util.ArrayList;
import java.util.List;


public class CheckInCounter extends Thread {
    private final int counterId;
    private final PassengerQueue queue; // Shared queue among all counters
    private final boolean isVIP;
    private volatile boolean running = true;
    private FlightList fltList;
    private List<Observer> observers = new ArrayList<>();

    /**
     * Constructs a CheckInCounter with specified ID, passenger queue, and VIP status.
     *
     * @param counterId Unique ID for the counter.
     * @param queue     Passengers queue for this counter.
     * @param isVIP     True if it's a VIP counter, false otherwise.
     */
    public CheckInCounter(int counterId, PassengerQueue queue, boolean isVIP, FlightList fltList) {
        this.counterId = counterId;
        this.queue = queue;
        this.isVIP = isVIP;
        this.fltList = fltList;
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public boolean isVIP() {
        return isVIP;
    }

    @Override
    public void run() {
        while (running) {

            try {
                Passenger passenger = null;
                // Synchronized block to ensure thread-safe access to the queue
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        passenger = queue.dequeue(); // dequeue the next passenger
                    }
                }
                if (passenger != null) {
//                    System.out.println((isVIP ? "VIP" : "Regular") + " Counter " + counterId + " is processing passenger " + passenger.getRefCode());
                    processPassenger(passenger);

                    // random time for process
                    int processTime = 4000;//5000 + random.nextInt(9000);
                    Thread.sleep(processTime);

//                    System.out.println("Passenger " + passenger.getRefCode() + " has been processed by " + (isVIP ? "VIP" : "Regular") + " Counter " + counterId);
                } else {
                    // wait for passengers
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Counter " + counterId + " interrupted.");
                Thread.currentThread().interrupt();
            } catch (AllExceptions.NumberErrorException e) {
                throw new RuntimeException(e);
            } catch (AllExceptions.NoMatchingFlightException e) {
                throw new RuntimeException(e);
            } catch (AllExceptions.NoMatchingRefException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Processes a passenger's check-in, including baggage handling and setting their check-in status.
     * Verifies the passenger first; if verification passes, handles their baggage and marks them as checked-in.
     * If verification fails, logs a message indicating failure.
     *
     * @param passenger The passenger to be processed.
     * @throws AllExceptions.NumberErrorException If an error occurs during baggage processing.
     */

    public boolean processPassenger(Passenger passenger) throws AllExceptions.NumberErrorException, AllExceptions.NoMatchingFlightException, AllExceptions.NoMatchingRefException {
        // System.out.println("Processing check-in for passenger: " + passenger.getRefCode());
        String flightCode = passenger.getFlightCode();
        // The flight has not departed.
        if (fltList.findByCode(flightCode) != null && !fltList.findByCode(flightCode).getIsTakenOff()) {
            if (verifyPassenger(passenger)) {
                handleBaggage(passenger.getHisBaggageList());
                passenger.checkIn();
                fltList.findByCode(flightCode).getPassengerInFlight().findByRefCode(passenger.getRefCode()).checkIn();
                fltList.findByCode(flightCode).getPassengerInFlight().findByRefCode(passenger.getRefCode()).setBaggageList(passenger.getHisBaggageList());
                System.out.println("Passenger " + passenger.getRefCode() + " with the baggage of " + passenger.getHisBaggageList().toString() +
                        "has successfully checked in at counter " + this.counterId + ".");
            } else {
                System.out.println("Passenger verification failed for: " + passenger.getRefCode());
                return false;
            }
        } else {
            System.out.println("Cannot check-in passenger " + passenger.getRefCode() + ": Flight has already taken off or flight info not found.");
            return false;
        }
        notifyObservers();
        return true;
    }
    private boolean verifyPassenger(Passenger passenger) {
        return passenger.getFlightCode() != null;
    }

    /**
     * Checks all baggage in the list.
     *
     * @param baggageList List of baggage to check.
     * @throws AllExceptions.NumberErrorException if any baggage fails the check.
     */

    private void handleBaggage(BaggageList baggageList) throws AllExceptions.NumberErrorException {
        for (Baggage baggage : baggageList.getBaggageList()) {
            baggage.checkBaggage();
        }
    }

    public void shutdown() {
        running = false;
    }

    public boolean getStatus() {
        return this.running;
    }

    public int getCounterId() {
        return this.counterId;
    }
}