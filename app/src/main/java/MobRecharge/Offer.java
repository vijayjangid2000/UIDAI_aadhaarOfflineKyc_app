package MobRecharge;

public class Offer {
    private String price;
    private String offer;
    private String commAmount;
    private String commType;

    public Offer() {
    }

    public Offer(String price, String offer, String commAmount, String commType) {
        this.price = price;
        this.offer = offer;
        this.commAmount = commAmount;
        this.commType = commType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getCommAmount() {
        return commAmount;
    }

    public void setCommAmount(String commAmount) {
        this.commAmount = commAmount;
    }

    public String getCommType() {
        return commType;
    }

    public void setCommType(String commType) {
        this.commType = commType;
    }
}
