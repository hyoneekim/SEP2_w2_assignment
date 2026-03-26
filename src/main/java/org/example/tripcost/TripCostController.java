package org.example.tripcost;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.tripcost.service.LocalizationService;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TripCostController {

    @FXML private VBox rootVBox;
    @FXML private Label lblTitle;
    @FXML private Label lblDistant;
    @FXML private Label lblConsumption;
    @FXML private Label lblPrice;
    @FXML private TextField txtDistance;
    @FXML private TextField txtConsumption;
    @FXML private TextField txtPrice;
    @FXML private Button btnCalculate;
    @FXML private Label lblResult;
    @FXML private Label lblLocalTime;

    private Locale currentLocale = new Locale("en", "GB");
    private Map<String, String> localizedStrings;
    /**
     * Initialize the controller - called automatically after FXML loading
     */
    @FXML
    public void initialize() {
        // Set initial language
        setLanguage(currentLocale);

        // Add listeners to clear result when input changes
        txtDistance.textProperty().addListener((obs, oldVal, newVal) -> lblResult.setText(""));
        txtConsumption.textProperty().addListener((obs, oldVal, newVal) -> lblResult.setText(""));
        txtPrice.textProperty().addListener((obs, oldVal, newVal) -> lblResult.setText(""));
    }

    /**
     * Language button handlers
     */
    @FXML
    public void onENClick(ActionEvent e) { setLanguage(new Locale("en", "GB")); }

    @FXML
    public void onFRClick(ActionEvent e) { setLanguage(new Locale("fr", "FR")); }

    @FXML
    public void onJAClick(ActionEvent e) { setLanguage(new Locale("ja", "JP")); }

    @FXML
    public void onFAClick(ActionEvent e) { setLanguage(new Locale("fa", "IR")); }

    /**
     * Calculate consumption and cost based on user input
     */
    @FXML
    public void onCalculateClick(ActionEvent e) {
        try {
            double distant = Double.parseDouble(txtDistance.getText());
            double consumption = Double.parseDouble(txtConsumption.getText());
            double price = Double.parseDouble(txtPrice.getText());

            if (distant <= 0 || consumption <= 0 || price <= 0) {
                lblResult.setText(localizedStrings.getOrDefault("error_invalid_input", "Please enter valid numbers"));
                return;
            }
            double totalFuel = (distant / 100) * consumption;
            double totalCost = totalFuel * price;


            String result = localizedStrings.getOrDefault("result_label", "Total cost");
            String currency = localizedStrings.getOrDefault("currency", "EUR");
            lblResult.setText(String.format("%.2f l / %s: %.2f %s", totalFuel, result, totalCost, currency));

        } catch (NumberFormatException ex) {
            lblResult.setText(localizedStrings.getOrDefault("error_invalid_input", "Please enter valid numbers"));
        }
    }

    /**
     * Set the application language
     */
    private void setLanguage(Locale locale) {
        currentLocale = locale;
        lblResult.setText(""); // Clear previous result

        // Load localized strings
        localizedStrings = LocalizationService.getLocalizedStrings(locale);

        // Update all UI text

        lblTitle.setText(localizedStrings.getOrDefault("title", "Average Calculator"));
        lblDistant.setText(localizedStrings.getOrDefault("distance", "Distant (km):"));
        lblConsumption.setText(localizedStrings.getOrDefault("consumption", "Fuel Consumption (L/100 km):"));
        lblPrice.setText(localizedStrings.getOrDefault("price", "Fuel Price (per liter):"));
        btnCalculate.setText(localizedStrings.getOrDefault("calculate", "Calculate total cost"));

        txtDistance.setPromptText(localizedStrings.getOrDefault("distance_prompt", "Enter distance in km"));
        txtConsumption.setPromptText(localizedStrings.getOrDefault("consumption_prompt", "Enter fuel consumption in L/100 km"));
        txtPrice.setPromptText(localizedStrings.getOrDefault("price_prompt", "Enter fuel price per liter"));

        // Update time display with new locale
        displayLocalTime(locale);

        // Apply text direction based on language
        applyTextDirection(locale);
    }

    /**
     * Apply LTR or RTL layout direction
     */
    private void applyTextDirection(Locale locale) {
        // Step 1: Detect if the language is RTL
        String lang = locale.getLanguage();
        boolean isRTL = lang.equals("fa")   // Persian
                || lang.equals("ur")   // Urdu
                || lang.equals("ar")   // Arabic
                || lang.equals("he");  // Hebrew

        // Step 2: Wrap UI changes in Platform.runLater() for thread safety
        Platform.runLater(() -> {
            // Step 3: Set NodeOrientation on the root VBox
            if (rootVBox != null) {
                rootVBox.setNodeOrientation(
                        isRTL ? NodeOrientation.RIGHT_TO_LEFT
                                : NodeOrientation.LEFT_TO_RIGHT
                );
            }

            // Step 4: Align text inside TextFields
            String alignment = isRTL ? "-fx-text-alignment: right; -fx-alignment: center-right;"
                    : "-fx-text-alignment: left; -fx-alignment: center-left;";
            txtDistance.setStyle(alignment);
            txtConsumption.setStyle(alignment);
            txtPrice.setStyle(alignment);
        });
    }

    /**
     * Display local time formatted for the current locale
     */
    private void displayLocalTime(Locale locale) {
        LocalDateTime now = LocalDateTime.now();
        //TODO: change the local time of the location
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                localizedStrings.getOrDefault("time_format", "HH:mm:ss")
        ).withLocale(locale);

       String zoneId;

        switch (locale.getCountry()){
            case "FR" -> zoneId = "Europe/Paris";
            case "JP" -> zoneId = "Asia/Tokyo";
            case "IR" -> zoneId = "Asia/Tehran";
            case "GB" -> zoneId = "Europe/London";
            default -> zoneId = "UTC";
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(zoneId));

        String timeStr = String.format(
                localizedStrings.getOrDefault("current_time", "Current Time: %s"),
                zonedDateTime.format(formatter)
        );
        lblLocalTime.setText( zoneId + ": " +timeStr);
    }

}