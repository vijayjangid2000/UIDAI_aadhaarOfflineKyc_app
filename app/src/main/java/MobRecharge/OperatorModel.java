package MobRecharge;

public class OperatorModel implements Comparable<OperatorModel> {

    /* PARAMETERS HERE */
    private String id, providerName, providerImage, serviceId,
            dealer, extraParam, providerState, providerStateId,
            customerName, dueDate, mobileNumber, billCost;

    private boolean isStateAvailable, isPostpaid; // false by default

    /* CONSTRUCTOR HERE */

    public OperatorModel() {

        String notNull = "";

        this.id = notNull;
        this.providerName = notNull;
        this.providerImage = notNull;
        this.serviceId = notNull;
        this.dealer = notNull;
        this.extraParam = notNull;
        providerState = notNull;
        providerStateId = notNull;

        // for customer details
        customerName = notNull;
        dueDate = notNull;
        mobileNumber = notNull;
        billCost = notNull;

        this.isStateAvailable = false;
        this.isPostpaid = false;

    }

    /* Comparable for sorting using the providerName*/

    @Override
    public int compareTo(OperatorModel operatorModel) {
        return this.getProviderName().compareTo(operatorModel.getProviderName());
    }

    /* GETTERS AND SETTERS HERE */

    public boolean isPostpaid() {
        return isPostpaid;
    }

    public void setPostpaid(boolean postpaid) {
        isPostpaid = postpaid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderImage() {
        return providerImage;
    }

    public void setProviderImage(String providerImage) {
        this.providerImage = providerImage;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getExtraParam() {
        return extraParam;
    }

    public void setExtraParam(String extraParam) {
        this.extraParam = extraParam;
    }

    public boolean isStateAvailable() {
        return isStateAvailable;
    }

    public void setStateAvailable(boolean stateAvailable) {
        isStateAvailable = stateAvailable;
    }

    public String getProviderState() {
        return providerState;
    }

    public void setProviderState(String providerState) {
        this.providerState = providerState;
    }

    public String getProviderStateId() {
        return providerStateId;
    }

    public void setProviderStateId(String providerStateId) {
        this.providerStateId = providerStateId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getBillCost() {
        return billCost;
    }

    public void setBillCost(String billCost) {
        this.billCost = billCost;
    }


}
