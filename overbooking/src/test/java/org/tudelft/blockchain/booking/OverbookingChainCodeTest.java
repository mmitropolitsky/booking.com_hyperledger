package org.tudelft.blockchain.booking;

import org.hamcrest.core.StringStartsWith;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hyperledger.fabric.shim.Chaincode.Response;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OverbookingChainCodeTest {

    ChaincodeStub chaincodeStub;
    OverbookingChainCode overbookingChainCode;

    @Before
    public void testInitialization() {
        this.chaincodeStub = mock(ChaincodeStub.class);
        this.overbookingChainCode = new OverbookingChainCode();
    }

    /* Tests for the init method of the chaincode
     *
     * */
    @Test
    public void testNotSupportedInitFunction() {

        when(chaincodeStub.getFunction()).thenReturn("notInit");

        Response response = overbookingChainCode.init(chaincodeStub);
        assertEquals(response.getMessage(), "function other than init is not supported");
    }


    @Test
    public void testIncorrectNumberOfArgumentsInitFunction1() {
        when(chaincodeStub.getFunction()).thenReturn("init");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList("onlyOneStringAsParameter"));

        Response response = overbookingChainCode.init(chaincodeStub);
        assertThat(response.getMessage(),
                StringStartsWith.startsWith("Incorrect number of arguments. Expecting 0 by default (or 2 for specifying a booking range)"));
    }

    @Test
    public void testIncorrectNumberOfArgumentsInitFunction2() {
        when(chaincodeStub.getFunction()).thenReturn("init");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList("more","than","two","strings",
                "as","parameter"));

        Response response = overbookingChainCode.init(chaincodeStub);
        assertThat(response.getMessage(),
                StringStartsWith.startsWith("Incorrect number of arguments. Expecting 0 by default (or 2 for specifying a booking range)"));
    }

    @Test
    public void testArgumentsWithIncorrectFormatInitFunction1() {
        when(chaincodeStub.getFunction()).thenReturn("init");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList("01/02/2019", "02/02/2019"));

        Response response = overbookingChainCode.init(chaincodeStub);
        assertEquals(response.getMessage(), "Please specify valid dates of the following format: YYYY-MM-DD");
    }

    @Test
    public void testArgumentsWithIncorrectFormatInitFunction2() {
        when(chaincodeStub.getFunction()).thenReturn("init");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList("2020-31-03","2020-31-03"));

        Response response = overbookingChainCode.init(chaincodeStub);
        assertEquals(response.getMessage(), "Please specify valid dates of the following format: YYYY-MM-DD");
    }

    @Test
    public void testArgumentsWithInvalidDateInitFunction() {
        when(chaincodeStub.getFunction()).thenReturn("init");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList("2020-02-30","2020-03-03"));

        Response response = overbookingChainCode.init(chaincodeStub);
        assertEquals(response.getMessage(), "Please specify valid dates of the following format: YYYY-MM-DD");
    }

    @Test
    public void testPutStateCalledInInitFunctionConsecutiveDays() {

        String startDate = "2019-07-03";
        String endDate = "2019-07-04";
        String available = DateStatus.AVAILABLE.toString();

        when(chaincodeStub.getFunction()).thenReturn("init");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList(startDate, endDate));

        Response response = overbookingChainCode.init(chaincodeStub);
        assertEquals(response.getStatus(), Response.Status.SUCCESS);

        InOrder inOrder = Mockito.inOrder(chaincodeStub);

        inOrder.verify(chaincodeStub).getFunction();
        inOrder.verify(chaincodeStub).getParameters();
        inOrder.verify(chaincodeStub).putStringState(startDate, available);
        inOrder.verify(chaincodeStub).putStringState(endDate, available);

        inOrder.verifyNoMoreInteractions();

    }

    @Test
    public void testPutStateCalledInInitFunctionForAMonth() {

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(31).toString();
        String availability = DateStatus.AVAILABLE.toString();

        when(chaincodeStub.getFunction()).thenReturn("init");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList(startDate, endDate));

        Response response = overbookingChainCode.init(chaincodeStub);
        assertEquals(response.getStatus(), Response.Status.SUCCESS);

        InOrder inOrder = Mockito.inOrder(chaincodeStub);

        inOrder.verify(chaincodeStub).getFunction();
        inOrder.verify(chaincodeStub).getParameters();
        inOrder.verify(chaincodeStub).putStringState(startDate,availability);
        inOrder.verify(chaincodeStub).putStringState(endDate,availability);

        inOrder.verifyNoMoreInteractions();

    }

    @Test
    public void testPutStateCalledInInitFunctionFor500Days() {

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(500).toString();
        String availability = DateStatus.AVAILABLE.toString();

        when(chaincodeStub.getFunction()).thenReturn("init");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList(startDate, endDate));

        Response response = overbookingChainCode.init(chaincodeStub);
        assertEquals(response.getStatus(), Response.Status.SUCCESS);

        InOrder inOrder = Mockito.inOrder(chaincodeStub);

        inOrder.verify(chaincodeStub).getFunction();
        inOrder.verify(chaincodeStub).getParameters();
        inOrder.verify(chaincodeStub).putStringState(startDate,availability);
        inOrder.verify(chaincodeStub).putStringState(endDate,availability);

        inOrder.verifyNoMoreInteractions();

    }


    /* Tests for the invoke method of the chaincode
     *
     *
     *
     *
     * */


    @Test
    public void testIncorrectNumberOfArgumentsInvokeFunctions1() {
        when(chaincodeStub.getFunction()).thenReturn("isBookable");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList("onlyOneStringAsParameter"));

        Response response = overbookingChainCode.invoke(chaincodeStub);
        assertEquals(response.getMessage(), "Incorrect number of arguments. Expecting 2");
    }

    @Test
    public void testIncorrectNumberOfArgumentsInvokeFunctions2() {
        when(chaincodeStub.getFunction()).thenReturn("book");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList("onlyOneStringAsParameter"));

        Response response = overbookingChainCode.invoke(chaincodeStub);
        assertEquals(response.getMessage(), "Incorrect number of arguments. Expecting 2");
    }

    @Test
    public void testInvokeBookStartBookingDateIsAfterEndBookingDateConsecutiveDays() {

        // Specifying valid dates as input parameters
        List<String> datesParam = Arrays.asList("2019-07-04", "2019-07-03");

        // Mocking these date parameters
        List<KeyValue> dates = generateMockKeyValueDateWithAvailabilityByInterval(LocalDate.parse(datesParam.get(0)),
                LocalDate.parse(datesParam.get(1)),"1");

        when(chaincodeStub.getFunction()).thenReturn("book");
        when(chaincodeStub.getParameters()).thenReturn(datesParam);

        QueryResultsIterator<KeyValue> current = mock(QueryResultsIterator.class);
        when(current.iterator()).thenReturn(dates.iterator());

        when(chaincodeStub.getStateByRange(datesParam.get(0), datesParam.get(1))).thenReturn(current);

        Response response = overbookingChainCode.invoke(chaincodeStub);
        assertEquals(response.getMessage(), "Please specify an end date after the start date!");
    }

    @Test
    public void testInvokeBookStartBookingDateIsAfterEndBookingDateNext500Days() {

        String startDate = LocalDate.now().toString();
        String endDate = LocalDate.now().plusDays(500).toString();

        when(chaincodeStub.getFunction()).thenReturn("book");
        when(chaincodeStub.getParameters()).thenReturn(Arrays.asList(endDate, startDate));
        //when(chaincodeStub.getStateByRange(endDate, startDate)).thenReturn(QueryResultsIterator<KeyValue>);

        KeyValue mockedDate = mock(KeyValue.class);
        when(mockedDate.getKey()).thenReturn(startDate.toString());
        when(mockedDate.getStringValue()).thenReturn(DateStatus.BOOKED.toString());

        Response response = overbookingChainCode.invoke(chaincodeStub);
        assertEquals(response.getMessage(), "Please specify an end date after the start date!");
    }
    @Test
    public void testIsNotBookable() {

        // Specifying valid dates as input parameters
        List<String> datesParam = Arrays.asList("2019-07-01", "2019-07-04");

        // Mocking these date parameters with a booked status
        List<KeyValue> dates = generateMockKeyValueDateWithAvailabilityByInterval(
                LocalDate.parse(datesParam.get(0)), LocalDate.parse(datesParam.get(1)),DateStatus.BOOKED.toString());

        when(chaincodeStub.getFunction()).thenReturn("isBookable");
        when(chaincodeStub.getParameters()).thenReturn(datesParam);

        QueryResultsIterator<KeyValue> current = mock(QueryResultsIterator.class);
        when(current.iterator()).thenReturn(dates.iterator());

        when(chaincodeStub.getStateByRange(datesParam.get(0), datesParam.get(1))).thenReturn(current);

        //when(overbookingChainCode.isBookable(chaincodeStub, someMockDate, someMockDate)).thenReturn(false);
        Response response = overbookingChainCode.invoke(chaincodeStub);
        assertEquals(response.getMessage(), "The dates specified are not available for booking!");
    }

    /* Method mocking a set of KeyValues
     *
     *
     * */
    private List<KeyValue> generateMockKeyValueDateWithAvailabilityByInterval(LocalDate startDate, LocalDate endDate, String availability) {

        List<KeyValue> localStore = new ArrayList<>();
        while (startDate.isBefore(endDate)) {

            // Create the mocked date
            KeyValue mockedDate = mock(KeyValue.class);
            when(mockedDate.getKey()).thenReturn(startDate.toString());
            when(mockedDate.getStringValue()).thenReturn(availability);
            localStore.add(mockedDate);

            // Get the next date
            startDate = startDate.plusDays(1);
        }
        return localStore;
    }
}