package com.clinkod.kabarak.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BioData {
    public static final String NAME_PROPERTY = "bio_name";
    public static final String FIRST_NAME = "bio_firstName";
    public static final String EMAIL = "bio_email";
    public static final String MIDDLE_NAME = "bio_middleName";
    public static final String LAST_NAME = "bio_lastName";
    public static final String DOB_PROPERTY = "bio_date_of_birth";
    public static final String PHONE_PROPERTY = "bio_phone";
    public static final String MARITAL_STATUS = "bio_marital_status";
    public static final String HEIGHT_PROPERTY = "bio_height";
    public static final String WEIGHT_PROPERTY = "bio_weight";
    public static final String KIN_NAME = "kin_name";
    public static final String KIN_CONTACT = "kin_contact";
    public static final String KIN_RELSHIP = "kin_relship";
    public static final String CAREGIVER_NAME = "bio_caregiver_name";
    public static final String CAREGIVER_PHONE = "bio_caregiver_phone";
    public static final String CAREGIVER_GENDER = "bio_caregiver_gender";
    public static final String HYPERTENSION_HISTORY_PROPERTY = "bio_hypertension_history";
    public static final String REMOTE_ID_PROPERTY = "bio_remote_id";

    private static volatile BioData instance;

    private String name;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dateOfBirth;
    private String phone;
    private String email;
    private String marital_status;
    private int height;
    private double weight;
    private String nextOfKin;
    private String nextOfKinContact;
    private String relWithNextOfKin;
    private String careGiverName;
    private String careGiverPhone;
    private boolean familyHypertensionHistory;
    private String remoteId;

    public BioData(String name, String firstName, String middleName, String lastName, String dateOfBirth, String phone, String email, String marital_status, int height,
                   double weight, String nextOfKin, String nextOfKinContact, String relWithNextOfKin, String careGiverName,
                   String careGiverPhone, boolean familyHypertensionHistory, String remoteId) {
        this.name = name;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
        this.marital_status = marital_status;
        this.height = height;
        this.weight = weight;
        this.nextOfKin = nextOfKin;
        this.nextOfKinContact = nextOfKinContact;
        this.relWithNextOfKin = relWithNextOfKin;
        this.careGiverName = careGiverName;
        this.careGiverPhone = careGiverPhone;
        this.familyHypertensionHistory = familyHypertensionHistory;
        this.remoteId = remoteId;
    }

    public BioData() {

    }


    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat sm = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateObject = sm.parse(this.dateOfBirth);
            if (dateObject != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                jsonObject.put("dateOfBirth", dateFormat.format(dateObject));
            }

            jsonObject.put("name", this.name);
            jsonObject.put("firstName", this.firstName);
            jsonObject.put("middleName", this.middleName);
            jsonObject.put("lastName", this.lastName);
            jsonObject.put("phone", this.phone);
            jsonObject.put("email", this.email);
            jsonObject.put("gender", this.marital_status);
            jsonObject.put("height", this.height);
            jsonObject.put("weight", this.weight);
            jsonObject.put(nextOfKin, this.nextOfKin);
            jsonObject.put(nextOfKinContact, this.nextOfKinContact);
            jsonObject.put(relWithNextOfKin, this.relWithNextOfKin);
            jsonObject.put("careGiverName", this.careGiverName);
            jsonObject.put("careGiverPhone", this.careGiverPhone);
            jsonObject.put("familyHypertensionHistory", this.familyHypertensionHistory);
            jsonObject.put("maritalStatus", this.marital_status);

            jsonObject.put("assignedId", String.valueOf(System.currentTimeMillis())); // TODO: Get the patient's national ID
            // TODO: Do not hard code this
            jsonObject.put("password", "deviceuserpassword");

            return jsonObject;
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static BioData loadData() {
        String name = PropertyUtils.getStringValue(NAME_PROPERTY);
        String firstName = PropertyUtils.getStringValue(FIRST_NAME);
        String middleName = PropertyUtils.getStringValue(MIDDLE_NAME);
        String lastName = PropertyUtils.getStringValue(LAST_NAME);
        String dateOfBirth = PropertyUtils.getStringValue(DOB_PROPERTY);
        String phone = PropertyUtils.getStringValue(PHONE_PROPERTY);
        String email = PropertyUtils.getStringValue(EMAIL);
        String marital_status = PropertyUtils.getStringValue(MARITAL_STATUS);
        int height = PropertyUtils.getIntValue(HEIGHT_PROPERTY);
        double weight = PropertyUtils.getDoubleValue(WEIGHT_PROPERTY);
        String nextOfKin = PropertyUtils.getStringValue(KIN_NAME);
        String nextOfKinContact = PropertyUtils.getStringValue(KIN_CONTACT);
        String relWithNextOfKin = PropertyUtils.getStringValue(KIN_RELSHIP);
        String careGiverName = PropertyUtils.getStringValue(CAREGIVER_NAME);
        String careGiverPhone = PropertyUtils.getStringValue(CAREGIVER_PHONE);
        boolean hypertensionHistory = PropertyUtils.getBooleanValue(HYPERTENSION_HISTORY_PROPERTY);

        String remoteId = PropertyUtils.getStringValue(REMOTE_ID_PROPERTY, null);

        return new BioData(name, firstName, middleName, lastName, dateOfBirth, phone, email, marital_status, height, weight, nextOfKin, nextOfKinContact, relWithNextOfKin, careGiverName, careGiverPhone, hypertensionHistory, remoteId);
    }



    public static void createBioData(String name, String firstName, String middleName, String lastName, String dateOfBirth, String phone, String email,
                                     String marital_status, int height, double weight, String nextOfKin, String nextOfKinContact, String relWithNextOfKin, String careGiverName,
                                     String careGiverPhone, boolean familyHypertensionHistory
    ) {
        BioData bioData = new BioData();
        bioData.setName(name);
        bioData.setFirstName(firstName);
        bioData.setMiddleName(middleName);
        bioData.setLastName(lastName);
        bioData.setDateOfBirth(dateOfBirth);
        bioData.setPhone(phone);
        bioData.setEmail(email);
        bioData.setMarital_status(marital_status);
        bioData.setHeight(height);
        bioData.setWeight(weight);
        bioData.setKinName(nextOfKin);
        bioData.setKinContact(nextOfKinContact);
        bioData.setKinRel(relWithNextOfKin);
        bioData.setCareGiverName(careGiverName);
        bioData.setCareGiverPhone(careGiverPhone);
        bioData.setFamilyHypertensionHistory(familyHypertensionHistory);

        instance = bioData;
    }


    public synchronized static BioData getBioData() {
        if (instance == null) {
            instance = loadData();
        }

        return instance;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        PropertyUtils.putProperty(FIRST_NAME, firstName);
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
        PropertyUtils.putProperty(MIDDLE_NAME, middleName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        PropertyUtils.putProperty(LAST_NAME, lastName);
    }

    public void setName(String name) {
        this.name = name;
        PropertyUtils.putProperty(NAME_PROPERTY, name);
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        PropertyUtils.putProperty(DOB_PROPERTY, dateOfBirth);
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        PropertyUtils.putProperty(EMAIL, email);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        PropertyUtils.putProperty(PHONE_PROPERTY, phone);
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
        PropertyUtils.putProperty(MARITAL_STATUS, marital_status);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        PropertyUtils.putProperty(HEIGHT_PROPERTY, height);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        PropertyUtils.putProperty(WEIGHT_PROPERTY, weight);
    }

    public String getNextOfKin() {
        return nextOfKin;
    }

    private void setKinName(String nextOfKin) {
        this.nextOfKin = nextOfKin;
        PropertyUtils.putProperty(KIN_NAME, nextOfKin);
    }

    public String getNextOfKinContact() {
        return nextOfKinContact;
    }


    private void setKinContact(String nextOfKinContact) {
        this.nextOfKinContact = nextOfKinContact;
        PropertyUtils.putProperty(KIN_CONTACT, nextOfKinContact);
    }

    public String getKinRelship() {
        return relWithNextOfKin;
    }

    private void setKinRel(String relWithNextOfKin) {
        this.relWithNextOfKin = relWithNextOfKin;
        PropertyUtils.putProperty(KIN_RELSHIP, relWithNextOfKin);
    }

    public boolean isFamilyHypertensionHistory() {
        return familyHypertensionHistory;
    }

    public void setFamilyHypertensionHistory(boolean familyHypertensionHistory) {
        this.familyHypertensionHistory = familyHypertensionHistory;
        PropertyUtils.putProperty(HYPERTENSION_HISTORY_PROPERTY, familyHypertensionHistory);
    }

    private void setCareGiverName(String careGiverName) {
        this.careGiverName = careGiverName;
        PropertyUtils.putProperty(CAREGIVER_NAME, careGiverName);
    }

    private void setCareGiverPhone(String careGiverPhone) {
        this.careGiverPhone = careGiverPhone;
        PropertyUtils.putProperty(CAREGIVER_PHONE, careGiverPhone);
    }

    public String getRemoteId() {
        return remoteId;
    }


    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
        PropertyUtils.putProperty(REMOTE_ID_PROPERTY, remoteId);
    }
}
