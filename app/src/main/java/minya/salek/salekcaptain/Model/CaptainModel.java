package minya.salek.salekcaptain.Model;

public class CaptainModel {
    private String name, id, phone, email, password, imageUrl, carType, evaluation,key;

    public CaptainModel() {
    }

    public CaptainModel(String name, String id, String phone, String email, String password, String imageUrl, String carType, String evaluation, String key) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.carType = carType;
        this.evaluation = evaluation;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
