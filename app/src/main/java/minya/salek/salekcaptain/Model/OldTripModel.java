package minya.salek.salekcaptain.Model;

public class OldTripModel {
    private String captainId, userId, rate, endTrip, tripDate, tripPrice;

    public OldTripModel() {
    }

    public OldTripModel(String captainId, String userId, String rate, String endTrip, String tripDate, String tripPrice) {
        this.captainId = captainId;
        this.userId = userId;
        this.rate = rate;
        this.endTrip = endTrip;
        this.tripDate = tripDate;
        this.tripPrice = tripPrice;
    }

    public String getCaptainId() {
        return captainId;
    }

    public void setCaptainId(String captainId) {
        this.captainId = captainId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getEndTrip() {
        return endTrip;
    }

    public void setEndTrip(String endTrip) {
        this.endTrip = endTrip;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getTripPrice() {
        return tripPrice;
    }

    public void setTripPrice(String tripPrice) {
        this.tripPrice = tripPrice;
    }
}