package BikeManagement;

public class RentalRequest {
    private String requestId;
    private String bikeId;
    private String renterUsername;
    private String email;
    private String rentalDays;
    private String totalPayment;
    private String additionalServices;
    private String fileName;
    private String additionalNotes;

    public RentalRequest(String requestId, String bikeId, String renterUsername, String email, String rentalDays, String totalPayment, String additionalServices, String fileName, String additionalNotes) {
        this.requestId = requestId;
        this.bikeId = bikeId;
        this.renterUsername = renterUsername;
        this.email = email;
        this.rentalDays = rentalDays;
        this.totalPayment = totalPayment;
        this.additionalServices = additionalServices;
        this.fileName = fileName;
        this.additionalNotes = additionalNotes;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getBikeId() {
        return bikeId;
    }

    public String getRenterUsername() {
        return renterUsername;
    }

    public String getEmail() {
        return email;
    }

    public String getRentalDays() {
        return rentalDays;
    }

    public String getTotalPayment() {
        return totalPayment;
    }

    public String getAdditionalServices() {
        return additionalServices;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }
}
