package org.tudelft.blockchain.booking;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OverbookingChainCode extends ChaincodeBase {

    public enum BookingStatus {
        AVAILABLE, BOOKED
    }

    private static Logger _logger = LogManager.getLogger(Overbooking.class);

    //private static final Map<String, String> bookingStore = new HashMap<String, String>() {};

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("DD-MM-YYYY");

    //Using formatter to create String of the "2019-01-01" format  from LocalDateTime
    private String convertToString(LocalDateTime currentDate){
        return currentDate.format(this.dateFormatter);
    }

    //Using formatter to create LocalDateTime of a "2019-01-01" formatted String
    private LocalDateTime convertFromString(String stringOfCurrentDate){
        return LocalDateTime.parse(stringOfCurrentDate, this.dateFormatter);
    }

    public Response init(ChaincodeStub stub) {
        try {
            _logger.debug("Initiating org.tudelft.blockchain.booking.Overbooking chaincode");
            String function = stub.getFunction();

            if (!function.equals("init"))
                return newErrorResponse("function other than init is not supported");

            List<String> args = stub.getParameters();

            if (args.size() != 1)
                return newErrorResponse("Incorrect number of arguments. Expecting 1");

            //Initialize content of the booking store
            //Adding the next 500 days to the state as (day, availability) key value pairs
            //Each day is an object in the state
            for (int i = 0; i < 500; i++) {

                stub.putStringState(convertToString(LocalDateTime.now().plusDays(i)), BookingStatus.AVAILABLE);

            }

            return newSuccessResponse();
        } catch (Throwable e) {
            return newErrorResponse(e);
        }

    }

    public Response invoke(ChaincodeStub stub) {
        try {
            _logger.info("Invoke java simple chaincode");
            String func = stub.getFunction();
            List<String> params = stub.getParameters();

            if (func.equals("book")) {
                return book(stub, params);
            }

            return newErrorResponse("Invalid invoke function name. Expecting one of: [\"book\", ]");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    private Response book(ChaincodeStub stub, List<String> params) {
        try {
            LocalDateTime bookingStart = convertFromString(params.get(0)),
                          bookingEnd = convertFromString(params.get(1));

            if(isBookable(stub, bookingStart, bookingEnd)){
                doBooking(stub, bookingStart, bookingEnd);
                return newSuccessResponse();
            }
            else {
                //TODO
                //Implement returning the unavailable dates
                return newErrorResponse("Invalid booking dates!");
            }

        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

<<<<<<< HEAD:overbooking/src/main/java/Overbooking.java
    private Boolean isBookable(ChaincodeStub stub, LocalDateTime bookingStart, LocalDateTime bookingEnd) {

        while(bookingStart!=bookingEnd){

            //Retrieve information about overall availability
            if(stub.getState(this.convertToString(bookingStart))==BookingStatus.BOOKED){
                return FALSE;
            }
            bookingStart.plusDays(1);
        }

        return TRUE;
    }

    private void doBooking(ChaincodeStub stub, LocalDateTime bookingStart, LocalDateTime bookingEnd){

        while(bookingStart!=bookingEnd){

            //Update the booking status for each date of the booking
            stub.delState(this.convertToString(bookingStart));
            stub.putStringState(this.convertToString(bookingStart), BookingStatus.BOOKED);
            bookingStart.plusDays(1);
        }
    }

    public static void main(String[] args) {
        new OverbookingChainCode().start(args);
=======
    public static void main(String[] args) {
        new Overbooking().start(args);
>>>>>>> 7cd99e2e406c53f9a87440bc5ad1a6e8d1832f4a:overbooking/src/main/java/org/tudelft/blockchain/booking/Overbooking.java
    }
}
