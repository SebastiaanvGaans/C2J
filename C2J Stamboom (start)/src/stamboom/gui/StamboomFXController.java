/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import stamboom.controller.StamboomController;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem miClose;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;

    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML Button btStamboom;

    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;

    //INVOER PERSOON
    @FXML ComboBox cbNewPersonOuderlijkGezin;
    @FXML TextField tfNewPersonVoornamen;
    @FXML TextField tfNewPersonTussenvoegsel;
    @FXML TextField tfNewPersonAchternaam;
    @FXML TextField tfNewPersonGebDatum;
    @FXML TextField tfNewPersonGebPlaats;
    @FXML TextField tfNewPersonGeslacht;

    //GEZIN
    @FXML ComboBox cbGezinnen;
    @FXML ComboBox cbGezinOuder1;
    @FXML ComboBox cbGezinOuder2;
    @FXML TextField tfGezinTrouw;
    @FXML TextField tfGezinScheiding;

    //opgave 4
    private boolean withDatabase;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboboxes();
        withDatabase = false;
    }

    private void initComboboxes() {
        //todo opgave 3 
        try {
            cbPersonen.setItems((ObservableList) super.getAdministratie().getPersonen());
            cbOuder1Invoer.setItems((ObservableList) super.getAdministratie().getPersonen());
            cbOuder2Invoer.setItems((ObservableList) super.getAdministratie().getPersonen());
            cbOuderlijkGezin.setItems((ObservableList) super.getAdministratie().getGezinnen());
            cbNewPersonOuderlijkGezin.setItems((ObservableList) super.getAdministratie().getGezinnen());
            cbGezinnen.setItems((ObservableList) super.getAdministratie().getGezinnen());
            cbGezinOuder1.setItems((ObservableList) super.getAdministratie().getPersonen());
            cbGezinOuder2.setItems((ObservableList) super.getAdministratie().getPersonen());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
            }
            //todo opgave 3
            lvAlsOuderBetrokkenBij.setItems(persoon.getAlsOuderBetrokkenIn());
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = getAdministratie().getPersoon(nr);
        if (getAdministratie().setOuders(p, ouderlijkGezin)) {
            showDialog("Success", ouderlijkGezin.toString()
                    + " is nu het ouderlijk gezin van " + p.getNaam());
        }
        initComboboxes();
    }

    public void selectGezin(Event evt) {
        // todo opgave 3
        Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
        showGezin(gezin);
    }

    private void showGezin(Gezin gezin) {
        // todo opgave 3
        if (gezin == null) {
            clearTabGezin();
            return;
        }
        if (gezin.getOuder1() != null) {
            this.cbGezinOuder1.getSelectionModel().select(gezin.getOuder1().getNaam());
        }
        if (gezin.getOuder2() != null) {
            this.cbGezinOuder2.getSelectionModel().select(gezin.getOuder2().getNaam());
        }
        else{
            this.cbGezinOuder2.getSelectionModel().clearSelection();
        }
        this.tfGezinScheiding.setText(StringUtilities.datumString(gezin.getScheidingsdatum()));
        this.tfGezinTrouw.setText(StringUtilities.datumString(gezin.getHuwelijksdatum()));

    }

    public void setHuwdatum(Event evt) {
        // todo opgave 3
        Gezin gezin = (Gezin) this.cbGezinnen.getSelectionModel().getSelectedItem();

        Calendar huwDatum;
        try {
            huwDatum = StringUtilities.datum(this.tfGezinTrouw.getText());
        } catch (Exception ex) {
            showDialog("Warning", "geboortedatum :" + ex.getMessage());
            return;
        }
        super.getAdministratie().setHuwelijk(gezin, huwDatum);
        initComboboxes();
    }

    public void setScheidingsdatum(Event evt) {
        // todo opgave 3
        Gezin gezin = (Gezin) this.cbGezinnen.getSelectionModel().getSelectedItem();

        Calendar scheidingDatum;
        try {
            scheidingDatum = StringUtilities.datum(this.tfGezinScheiding.getText());
        } catch (Exception ex) {
            showDialog("Warning", "geboortedatum :" + ex.getMessage());
            return;
        }
        super.getAdministratie().setScheiding(gezin, scheidingDatum);
        initComboboxes();
    }

    public void cancelPersoonInvoer(Event evt) {
        // todo opgave 3
        this.clearTabPersoonInvoer();
    }

    public void okPersoonInvoer(Event evt) {
        // todo opgave 3
        if (tfNewPersonVoornamen.getText().isEmpty()
                || tfNewPersonAchternaam.getText().isEmpty()
                || tfNewPersonGeslacht.getText().isEmpty()
                || tfNewPersonGebDatum.getText().isEmpty()
                || tfNewPersonGebPlaats.getText().isEmpty()) {
            showDialog("Warning", "vul alle velden in");
            return;
        }

        String[] voornamen = tfNewPersonVoornamen.getText().trim().split(" ");
        String tussenvoegsel = tfNewPersonTussenvoegsel.getText().trim();
        String achternaam = tfNewPersonAchternaam.getText().trim();
        String gebplaats = tfNewPersonGebDatum.getText().trim();

        Geslacht geslacht;
        if (tfNewPersonGeslacht.getText().trim().toLowerCase().startsWith("m")) {
            geslacht = Geslacht.MAN;
        } else if (tfNewPersonGeslacht.getText().trim().toLowerCase().startsWith("v")) {
            geslacht = Geslacht.VROUW;
        } else {
            showDialog("Warning", "geslacht is fout ingevoerd!");
            return;
        }

        Calendar gebDatum;
        try {
            gebDatum = StringUtilities.datum(tfNewPersonGebDatum.getText());
        } catch (Exception ex) {
            showDialog("Warning", "geboortedatum :" + ex.getMessage());
            return;
        }

        Gezin ouderlijkGezin = (Gezin) cbNewPersonOuderlijkGezin.getSelectionModel().getSelectedItem();

        try {
            super.getAdministratie().addPersoon(geslacht, voornamen, achternaam, tussenvoegsel, gebDatum, gebplaats, ouderlijkGezin);
        } catch (Exception ex) {
            showDialog("Warning", "Kan niet toevoegen:" + ex.getMessage());
            return;
        }

        this.clearTabPersoonInvoer();
        initComboboxes();
    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    if (scheidingsdatum != null) {
                        getAdministratie().setScheiding(g, scheidingsdatum);
                    }
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
            }
        } else {
            g = getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
            }
        }

        clearTabGezinInvoer();
        initComboboxes();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    public void showStamboom(Event evt) {
        // todo opgave 3
        Persoon persoon = (Persoon) this.cbPersonen.getSelectionModel().getSelectedItem();
        if (persoon == null) {
            return;
        }
        showDialog("Stamboom", persoon.stamboomAlsString());
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    public void openStamboom(Event evt) {
        // todo opgave 3
        if (!this.withDatabase) {
            File file = new File("data");
            try {

                super.deserialize(file);
            } catch (Exception ex) {
                showDialog("Warning", ex.getMessage());
            }
        } else {
            try {
                super.loadFromDatabase();

            } catch (Exception ex) {
                showDialog("Warning", ex.getMessage());
            }

        }
        initComboboxes();
    }

    public void saveStamboom(Event evt) {
        // todo opgave 3
        if (!this.withDatabase) {
            File file = new File("data");
            if (file.exists()) {
                file.delete();
            }
            try {
                super.serialize(file);
            } catch (Exception ex) {
                showDialog("Warning", ex.getMessage());
            }
        }
        else{
            try{
                super.saveToDatabase();
            } catch(Exception ex){
                showDialog("Warning", ex.getMessage());
            }
        }
    }

    public void closeApplication(Event evt) {
        saveStamboom(evt);
        getStage().close();
    }

    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
    }

    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

    private void clearTabPersoonInvoer() {
        //todo opgave 3
        this.cbNewPersonOuderlijkGezin.getSelectionModel().clearSelection();
        this.tfNewPersonAchternaam.clear();
        this.tfNewPersonGebDatum.clear();
        this.tfNewPersonGebPlaats.clear();
        this.tfNewPersonGeslacht.clear();
        this.tfNewPersonTussenvoegsel.clear();
        this.tfNewPersonVoornamen.clear();
    }

    private void clearTabGezinInvoer() {
        //todo opgave 3
        this.cbOuder1Invoer.getSelectionModel().clearSelection();
        this.cbOuder2Invoer.getSelectionModel().clearSelection();
        tfHuwelijkInvoer.clear();
        tfScheidingInvoer.clear();

    }

    private void clearTabPersoon() {
        cbPersonen.getSelectionModel().clearSelection();
        tfPersoonNr.clear();
        tfVoornamen.clear();
        tfTussenvoegsel.clear();
        tfAchternaam.clear();
        tfGeslacht.clear();
        tfGebDatum.clear();
        tfGebPlaats.clear();
        cbOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.setItems(FXCollections.emptyObservableList());
    }

    private void clearTabGezin() {
        // todo opgave 3
        this.cbGezinnen.getSelectionModel().clearSelection();
        this.cbGezinOuder1.getSelectionModel().clearSelection();
        this.cbGezinOuder2.getSelectionModel().clearSelection();
        this.tfGezinScheiding.clear();
        this.tfGezinTrouw.clear();
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

}
