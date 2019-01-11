package org.tudelft.blockchain.booking;

import com.sun.istack.internal.NotNull;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Overbooking extends ChaincodeBase {

    public enum BookingStatus {
        AVAILABLE, BOOKED;

        public byte[] toBytes() {
            return this.name().getBytes();
        }

        public static BookingStatus from(byte[] bytes) {
            return BookingStatus.from(new String(bytes));
        }

        public static BookingStatus from(String name) {
            switch (name) {
                case "AVAILABLE":
                    return BookingStatus.AVAILABLE;
                case "BOOKED":
                    return BookingStatus.BOOKED;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public static final int DEFAULT_NB_DAYS_AVAILABLE = 500;

    private static Logger _logger = LogManager.getLogger(Overbooking.class);

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String dateToString(LocalDate date) throws DateTimeException {
        return dateFormatter.format(date);
    }

    public static LocalDate dateFromString (String date) throws DateTimeParseException {
        return LocalDate.from(dateFormatter.parse(date));
    }

    public Response init(ChaincodeStub stub) {
        _logger.debug("Initiating org.tudelft.blockchain.booking.Overbooking chaincode");
        try {
            String function = stub.getFunction();

            if (stub.getFunction() != "init")
                return newErrorResponse("function other than init is not supported");

            List<String> args = stub.getParameters();

            if (args.size() != 1)
                return newErrorResponse("Incorrect number of arguments. Expecting 1");

            // Initialize content of the booking store
            // Adding the next 500 days to the state as (day, availability) key value pairs
            // Each date is an object in the state
            for (int i = 0; i < DEFAULT_NB_DAYS_AVAILABLE; i++) {
                stub.putState(dateToString(LocalDate.now().plusDays(i)), BookingStatus.AVAILABLE.toBytes());
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

            switch(func) {
                case "book":    return book(stub, params);
                default:    return newErrorResponse("Invalid invoke function name. Expecting one of: [ book, ]");
            }
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    protected Response book(ChaincodeStub stub, @NotNull List<String> params) {
        try {
            switch (params.size()) {
                case 0: throw new IllegalArgumentException("Too few parameters for function : book (0 were given)");
                // case of a single date booking
                case 1: return bookDate(stub, params.get(0));
                case 2: return bookDateRange(stub, params.get(0), params.get(1));
                default:    throw new IllegalArgumentException("Too many arguments for function : book");
            }
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    protected Response bookDate(ChaincodeStub stub, String date) throws DateTimeParseException {
        try {
            if (isAvailable(stub, dateFromString(date))) {
                stub.putState(date, BookingStatus.BOOKED.toBytes());
                return newSuccessResponse();
            }
            return newErrorResponse("The date " + date + " is not available");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    protected Response bookDateRange(ChaincodeStub stub, String start, String end) throws DateTimeParseException {
        try {
            LocalDate dateStart = dateFromString(start),
                    dateEnd = dateFromString(end);

            if (isAvailable(stub, dateStart, dateEnd)) {
                for (int i = 0 ; i <= dateStart.until(dateEnd, ChronoUnit.DAYS); i++)
                    stub.putState(dateToString(dateStart.plusDays(i)), BookingStatus.BOOKED.toBytes());
                return newSuccessResponse();
            }
            return newErrorResponse("The requested dates are not available");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    public boolean isAvailable(ChaincodeStub stub, LocalDate date) {
        try {
            return BookingStatus.from(stub.getState(dateToString(date))).equals(BookingStatus.AVAILABLE);
        } catch (Throwable e) {
            return false;
        }
    }

    public boolean isAvailable(ChaincodeStub stub, LocalDate bookingStart, LocalDate bookingEnd) {
        if (bookingEnd.isBefore(bookingStart))
            throw new IllegalArgumentException("The date range can not end before it starts");
        if (bookingStart.isEqual(bookingEnd))
            return isAvailable(stub, bookingStart);

        QueryResultsIterator<KeyValue> dates = stub.getStateByRange(bookingStart.toString(), bookingEnd.toString());
        Iterator<KeyValue> it = dates.iterator();
        while (it.hasNext()) {
            if (BookingStatus.from(it.next().getValue()).equals(BookingStatus.BOOKED))
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        new Overbooking().start(args);
    }
}
