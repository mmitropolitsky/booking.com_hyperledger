package org.tudelft.blockchain.booking;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class OverbookingChainCode extends ChaincodeBase {

    private static Logger logger = LogManager.getLogger(OverbookingChainCode.class);

    @Override
    public Response init(ChaincodeStub stub) {
        try {
            logger.debug("Initiating " + this.getClass().getCanonicalName());

            String function = stub.getFunction();

            if (!"init".equals(function))
                return newErrorResponse("function other than init is not supported");

            List<String> args = stub.getParameters();


            //If no time range specified add the next 500 days to the store
            if (args.size() == 0){
                putDatesWithAvailability(stub, Arrays.asList(LocalDate.now(),LocalDate.now().plusDays(500)), "0");
            }
            else if(args.size() == 2){
                List<LocalDate> validatedParams = validateDateParams(stub.getParameters());
                putDatesWithAvailability(stub, validatedParams, "0");
            }
            else{
                return newErrorResponse("Incorrect number of arguments. Expecting 0");
            }

            return newSuccessResponse();
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    /**
     * Implements invoke method of the chaincode executing the booking
     * Invoked by the OTA
     */
    @Override
    public Response invoke(ChaincodeStub stub) {

        String function = stub.getFunction();

        //TODO
        //ADD: PO may book a date
        List<LocalDate> validatedParams = validateDateParams(stub.getParameters());

        //OTA may check availability
        if ("isBookable".equals(function)) {
            if (isBookable(stub, validatedParams)) {
                return newSuccessResponse();
            } else {
                return newErrorResponse("Invalid booking dates!");
            }
        }

        //OTA may book a date
        if ("book".equals(function)) {
            return book(stub, validatedParams);
        }
        return newErrorResponse("Function not found.");
    }

    private List<LocalDate> validateDateParams(List<String> params) {
        List<LocalDate> validatedParams = null;
        try {
            if (params.size() != 2) {
                throw new Exception("Incorrect number of arguments. Expecting 2");
            }

            LocalDate bookingStart = LocalDate.parse(params.get(0));
            LocalDate bookingEnd = LocalDate.parse(params.get(1));

            if (bookingStart.isAfter(bookingEnd)) {
                throw new Exception("Please specify an end date after the start date!");
            }
            validatedParams = Arrays.asList(bookingStart, bookingEnd);
        } catch (Throwable e) {
        }
        return validatedParams;
    }

    //Executes booking if the dates are available and returns success message
    //Checking if input string is a LocalDate of ISO_LOCAL_DATE format
    //If not returns error response
    private Response book(ChaincodeStub stub, List<LocalDate> params) {
        if (isBookable(stub, params)) {

            //Update the booking status for each date of the booking
            putDatesWithAvailability(stub, params,"1");
            return newSuccessResponse();
        } else {
            //TODO
            //Implement returning the unavailable dates
            return newErrorResponse("Invalid booking dates!");
        }
    }

    //Retrieve information about overall availability
    private boolean isBookable(ChaincodeStub stub, List<LocalDate> params) {

        QueryResultsIterator<KeyValue> currentInterval = stub.getStateByRange(params.get(0).toString(), params.get(1).toString());
        for (KeyValue currentDate : currentInterval) {
            if (currentDate.getStringValue().equals("1")) {
                return false;
            }
        }
        return true;
    }

    /*
    * Put the specified dates with the booking value into the store
    *
    * Used by init: to initialize dates as available in the store
    *
    * Used by invoke: to set the availability to booked for the specified dates
    *
    * */
    private void putDatesWithAvailability(ChaincodeStub stub, List<LocalDate> params, String isBooked) {

        LocalDate startDate = params.get(0);
        LocalDate endDate = params.get(1);

        while (startDate.isBefore(endDate)) {

            stub.putStringState(startDate.toString(), isBooked);
            startDate = startDate.plusDays(1);
        }
    }

    public static void main(String[] args) {

        new OverbookingChainCode().start(args);
    }
}