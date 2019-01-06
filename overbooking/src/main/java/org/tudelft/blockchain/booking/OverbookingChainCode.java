package org.tudelft.blockchain.booking;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class OverbookingChainCode extends ChaincodeBase {

    private static Logger _logger = LogManager.getLogger(OverbookingChainCode.class);

    //Initialize content of the booking store
    //Adding the next 500 days to the state as (day, availability) key value pairs
    //Each day is represented as an object in the state
    public Response init(ChaincodeStub stub) {
        try {
            _logger.debug("Initiating org.tudelft.blockchain.booking.overbookingchaincode");

            String function = stub.getFunction();

            if (!function.equals("init"))
                return newErrorResponse("function other than init is not supported");

            List<String> args = stub.getParameters();

            if (args.size() != 1)
                return newErrorResponse("Incorrect number of arguments. Expecting 1");

            for (int i = 0; i < 500; i++) {
                stub.putStringState(LocalDate.now().plusDays(i).toString(), "0");
            }

            return newSuccessResponse();
        } catch (Throwable e) {
            return newErrorResponse(e);
        }

    }

    //Implements invoke method of the chaincode executing the booking
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

    //Executes booking if the dates are available and returns success message
    //Checking if input string is a LocalDate of ISO_LOCAL_DATE format
    //If not returns error response
    private Response book(ChaincodeStub stub, List<String> params) {
        try {
            LocalDate bookingStart = LocalDate.parse(params.get(0));
            LocalDate bookingEnd = LocalDate.parse(params.get(1));

            if (bookingStart.isAfter(bookingEnd)) {
                return newErrorResponse("Please specify an end date after the start date!");
            }

            if (isBookable(stub, bookingStart, bookingEnd)) {
                doBooking(stub, bookingStart, bookingEnd);
                return newSuccessResponse();
            } else {
                //TODO
                //Implement returning the unavailable dates
                return newErrorResponse("Invalid booking dates!");
            }

        } catch (Throwable excep) {
            return newErrorResponse("Please specify valid dates of the \"YYYY-MM-DD\" pattern!");
        }
    }

    //Retrieve information about overall availability
    private boolean isBookable(ChaincodeStub stub, LocalDate bookingStart, LocalDate bookingEnd) {

        QueryResultsIterator<KeyValue> currentInterval = stub.getStateByRange(bookingStart.toString(), bookingEnd.toString());
        for (KeyValue currentDate : currentInterval) {
            if (currentDate.getStringValue().equals("1")) {
                return false;
            }
        }
        return true;
    }

    //Update the booking status for each date of the booking
    private void doBooking(ChaincodeStub stub, LocalDate bookingStart, LocalDate bookingEnd) {

        while (bookingStart != bookingEnd) {

            stub.putStringState(bookingStart.toString(), "1");
            bookingStart.plusDays(1);
        }
    }

    public static void main(String[] args) {

        //Configuring log4j logger
        BasicConfigurator.configure();

        new OverbookingChainCode().start(args);
    }
}