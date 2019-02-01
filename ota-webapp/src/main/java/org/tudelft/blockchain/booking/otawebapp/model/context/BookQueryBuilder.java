package org.tudelft.blockchain.booking.otawebapp.model.context;

public class BookQueryBuilder {
    private String orgName;
    private String propertyName;
    private String fromDate;
    private String toDate;

    public BookQueryBuilder setOrgName(String orgName) {
        this.orgName = orgName;
        return this;
    }

    public BookQueryBuilder setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    public BookQueryBuilder setFromDate(String fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public BookQueryBuilder setToDate(String toDate) {
        this.toDate = toDate;
        return this;
    }

    public BookQuery createBookQuery() {
        return new BookQuery(orgName, propertyName, fromDate, toDate);
    }
}