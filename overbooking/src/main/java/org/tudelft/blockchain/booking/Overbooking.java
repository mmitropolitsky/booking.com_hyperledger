package org.tudelft.blockchain.booking;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Overbooking extends ChaincodeBase {

    private static Logger _logger = LogManager.getLogger(Overbooking.class);

    public Response init(ChaincodeStub stub) {
        try {
            _logger.debug("Initiating org.tudelft.blockchain.booking.Overbooking chaincode");
            String function = stub.getFunction();

            if (!function.equals("init"))
                return newErrorResponse("function other than init is not supported");

            List<String> args = stub.getParameters();

            if (args.size() != 1)
                return newErrorResponse("Incorrect number of arguments. Expecting 1");

            // Initialize the chaincode
            stub.putStringState("availableFrom", "01-01-2019");
            stub.putStringState("availableTill", "31-12-2019");

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
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-YYYY");
            LocalDateTime bookingStart = LocalDateTime.from(dateFormat.parse(params.get(0))),
                bookingEnd = LocalDateTime.from(dateFormat.parse(params.get(1))),
                availableFrom = LocalDateTime.from(dateFormat.parse(stub.getStringState("availableFrom"))),
                availableTill = LocalDateTime.from(dateFormat.parse(stub.getStringState("availableTill")));

            if (bookingStart.isAfter(availableFrom)
                    && bookingEnd.isBefore(availableTill)) {
                stub.putStringState("unavailableFrom", params.get(0));
                stub.putStringState("unavailableTill", params.get(1));
            } else
                return newErrorResponse("Invalid booking dates !");

            return newSuccessResponse();
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }
}
