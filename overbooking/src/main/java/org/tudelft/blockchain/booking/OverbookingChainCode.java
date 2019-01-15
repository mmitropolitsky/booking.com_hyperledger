package org.tudelft.blockchain.booking;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            if (args.size() == 0) {
                addDatesWithAvailability(stub, Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(500)), "0");
            } else if (args.size() == 2) {

                Response validationResponse = validateDateParams(args);
                if (validationResponse.getStatus() == Response.Status.SUCCESS) {
                    List<LocalDate> validatedParams = Arrays.asList(LocalDate.parse(args.get(0)),
                            LocalDate.parse(args.get(1)));
                    addDatesWithAvailability(stub, validatedParams, "0");
                } else {
                    return validationResponse;
                }

            } else {
                return newErrorResponse("Incorrect number of arguments." +
                        " Expecting 0 by default (or 2 for specifying a booking range). " +
                        "Stub parameters for function [" + stub.getFunction() + "] with size " +
                        "[" + stub.getParameters().size() + "]: [" + stub.getParameters() + "]");
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

        List<String> args = stub.getParameters();
        Response validationResponse = validateDateParams(args);

        if (validationResponse.getStatus() == Response.Status.INTERNAL_SERVER_ERROR) {
            return validationResponse;
        }

        List<LocalDate> validatedParams = Arrays.asList(LocalDate.parse(args.get(0)), LocalDate.parse(args.get(1)));

        //OTA may check availability
        if ("isBookable".equals(function)) {
            if (isBookable(stub, validatedParams)) {
                return newSuccessResponse();
            } else {
                return notBookableResponse();
            }
        }

        //OTA may book a date
        if ("book".equals(function)) {
            return book(stub, validatedParams);
        }
        return newErrorResponse("Function not found.");
    }

    private Response validateDateParams(List<String> params) {

        if (params.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting 2");
        }

        /*
        * DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("dd/MM/yyyy")
                .withResolverStyle(ResolverStyle.STRICT);
        *
        * */

        try {
            LocalDate.parse(params.get(0));
            LocalDate.parse(params.get(1));
        } catch (DateTimeParseException e) {
            return newErrorResponse("Please specify valid dates of the following format: YYYY-MM-DD");
        }

        LocalDate bookingStart = LocalDate.parse(params.get(0));
        LocalDate bookingEnd = LocalDate.parse(params.get(1));

        if (bookingStart.isAfter(bookingEnd)) {
            return newErrorResponse("Please specify an end date after the start date!");
        }
        return newSuccessResponse();
    }

    //Executes booking if the dates are available and returns success message
    //Checking if input string is a LocalDate of ISO_LOCAL_DATE format
    //If not returns error response
    private Response book(ChaincodeStub stub, List<LocalDate> params) {
        if (isBookable(stub, params)) {

            //Update the booking status for each date of the booking
            addDatesWithAvailability(stub, params, "1");
            Map<String, byte[]> transientMap = stub.getTransient();
            if (null != transientMap) {
                if (transientMap.containsKey("event") && transientMap.get("event") != null) {
                    stub.setEvent("event", transientMap.get("event"));
                }
                if (transientMap.containsKey("result") && transientMap.get("result") != null) {
                    return newSuccessResponse(transientMap.get("result"));
                }
            }
            return newSuccessResponse();
        } else {
            //TODO
            //Implement returning the unavailable dates
            return notBookableResponse();
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
    private void addDatesWithAvailability(ChaincodeStub stub, List<LocalDate> params, String isBooked) {

        LocalDate startDate = params.get(0);
        LocalDate endDate = params.get(1);

        while (!startDate.isAfter(endDate)) {

            stub.putStringState(startDate.toString(), isBooked);
            startDate = startDate.plusDays(1);
        }
    }

    private Response notBookableResponse() {
        return newErrorResponse("The dates specified are not available for booking!");
    }

    public static void main(String[] args) {
        new OverbookingChainCode().start(args);
    }
}