package minya.salek.salekcaptain;

public class TripModel {
    String captainId, endtrip, tripDate, IsAccept, userId, Uname, strip, tripPrice, Phone, Image;

    public TripModel() {
    }

    public TripModel(String captainId, String endtrip, String tripDate, String isAccept, String userId, String uname, String strip, String tripPrice, String phone, String image) {
        this.captainId = captainId;
        this.endtrip = endtrip;
        this.tripDate = tripDate;
        IsAccept = isAccept;
        this.userId = userId;
        Uname = uname;
        this.strip = strip;
        this.tripPrice = tripPrice;
        Phone = phone;
        Image = image;
    }

    public String getCaptainId() {
        return captainId;
    }

    public void setCaptainId(String captainId) {
        this.captainId = captainId;
    }

    public String getEndTrip() {
        return endtrip;
    }

    public void setEndTrip(String endTrip) {
        this.endtrip = endTrip;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getIsAccept() {
        return IsAccept;
    }

    public void setIsAccept(String isAccept) {
        IsAccept = isAccept;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUname() {
        return Uname;
    }

    public void setUname(String uname) {
        Uname = uname;
    }

    public String getStrip() {
        return strip;
    }

    public void setStrip(String strip) {
        this.strip = strip;
    }

    public String getTripPrice() {
        return tripPrice;
    }

    public void setTripPrice(String tripPrice) {
        this.tripPrice = tripPrice;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}