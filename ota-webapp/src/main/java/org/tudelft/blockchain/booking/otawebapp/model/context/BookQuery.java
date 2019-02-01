package org.tudelft.blockchain.booking.otawebapp.model.context;


public class BookQuery {
    String orgName;
    String propertyName;
    String fromDate;
    String toDate;

    public BookQuery(String orgName, String propertyName, String fromDate, String toDate) {
        this.orgName = orgName;
        this.propertyName = propertyName;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getFromDate() {
        return this.fromDate;
    }

    public String getToDate() {
        return this.toDate;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BookQuery)) return false;
        final BookQuery other = (BookQuery) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$orgName = this.getOrgName();
        final Object other$orgName = other.getOrgName();
        if (this$orgName == null ? other$orgName != null : !this$orgName.equals(other$orgName)) return false;
        final Object this$propertyName = this.getPropertyName();
        final Object other$propertyName = other.getPropertyName();
        if (this$propertyName == null ? other$propertyName != null : !this$propertyName.equals(other$propertyName))
            return false;
        final Object this$fromDate = this.getFromDate();
        final Object other$fromDate = other.getFromDate();
        if (this$fromDate == null ? other$fromDate != null : !this$fromDate.equals(other$fromDate)) return false;
        final Object this$toDate = this.getToDate();
        final Object other$toDate = other.getToDate();
        if (this$toDate == null ? other$toDate != null : !this$toDate.equals(other$toDate)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BookQuery;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $orgName = this.getOrgName();
        result = result * PRIME + ($orgName == null ? 43 : $orgName.hashCode());
        final Object $propertyName = this.getPropertyName();
        result = result * PRIME + ($propertyName == null ? 43 : $propertyName.hashCode());
        final Object $fromDate = this.getFromDate();
        result = result * PRIME + ($fromDate == null ? 43 : $fromDate.hashCode());
        final Object $toDate = this.getToDate();
        result = result * PRIME + ($toDate == null ? 43 : $toDate.hashCode());
        return result;
    }

    public String toString() {
        return "BookQuery(orgName=" + this.getOrgName() + ", propertyName=" + this.getPropertyName() + ", fromDate=" + this.getFromDate() + ", toDate=" + this.getToDate() + ")";
    }
}
