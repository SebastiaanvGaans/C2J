/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import stamboom.domain.Administratie;
import stamboom.domain.*;
import stamboom.util.StringUtilities;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;

    @Override
    public Administratie load() throws IOException {
        //todo opgave 4
        Administratie admin = new Administratie();
        if (this.isCorrectlyConfigured()) {
            try {
                this.initConnection();
                Statement stat = this.conn.createStatement();
                ResultSet rs = stat.executeQuery("SELECT * FROM Persoon ORDER BY PersoonNr");
                
                ArrayList<familyrelation> relaties = new ArrayList<>();
                while (rs.next()) {
                    //Get geslacht
                    Geslacht geslacht;
                    if (rs.getString("Geslacht").equals("M")) {
                        geslacht = Geslacht.MAN;
                    } else if (rs.getString("Geslacht").equals("V")) {
                        geslacht = Geslacht.VROUW;
                    } else {
                        throw new Exception("Invalid geslacht");
                    }
                    //Get info
                    String[] voornamen = rs.getString("Voornamen").split(" ");
                    String achternaam = rs.getString("Achternaam");
                    String tussenvoegsel = rs.getString("Tussenvoegsel");
                    String gebdat = rs.getString("Geboortedatum");
                    String gebplaats = rs.getString("Geboorteplaats");
                    int ouders = rs.getInt("Ouders");
                    if (ouders != 0) {
                        relaties.add(new familyrelation(rs.getInt("PersoonNr"), ouders));
                    }
                    admin.addPersoon(geslacht, voornamen, achternaam, tussenvoegsel, StringUtilities.datum(gebdat), gebplaats, null);
                }
                rs = stat.executeQuery("SELECT * FROM Gezin ORDER BY GezinNr");
                while (rs.next()) {
                    Persoon ouder1 = admin.getPersoon(rs.getInt("Ouder1"));
                    Persoon ouder2 = admin.getPersoon(rs.getInt("Ouder2"));
                    String huwelijksdatum = rs.getString("Huwelijk");
                    String scheidingsdatum = rs.getString("Scheiding");

                    if (huwelijksdatum.isEmpty()) {
                        admin.addOngehuwdGezin(ouder1, ouder2);
                    } else {
                        Gezin gezin = admin.addHuwelijk(ouder1, ouder2, StringUtilities.datum(huwelijksdatum));
                        if (!scheidingsdatum.isEmpty()) {
                            admin.setScheiding(gezin, StringUtilities.datum(scheidingsdatum));
                        }
                    }
                }
                for (familyrelation relation : relaties) {
                    admin.setOuders(
                            admin.getPersoon(relation.child),
                            admin.getGezin(relation.parents));
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            } finally {
                this.closeConnection();
            }
        }
        return admin;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        //todo opgave 4  
        if (this.isCorrectlyConfigured()) {
            try {
                this.initConnection();
                //Initialize the preparedstatements
                PreparedStatement psPersoon = this.conn.prepareStatement("INSERT INTO Persoon VALUES(?,?,?,?,?,?,?,?)");
                PreparedStatement psGezin = this.conn.prepareStatement("INSERT INTO Gezin VALUES(?,?,?,?,?)");

                for (Persoon persoon : admin.getPersonen()) {
                    psPersoon.setInt(1, persoon.getNr());
                    psPersoon.setString(2, persoon.getVoornamen());
                    psPersoon.setString(3, persoon.getTussenvoegsel());
                    psPersoon.setString(4, persoon.getAchternaam());
                    if (persoon.getGeslacht() == Geslacht.MAN) {
                        psPersoon.setString(5, "M");
                    } else {
                        psPersoon.setString(5, "V");
                    }
                    psPersoon.setString(6, StringUtilities.datumString(persoon.getGebDat()));
                    psPersoon.setString(7, persoon.getGebPlaats());
                    if (persoon.getOuderlijkGezin() != null) {
                        psPersoon.setInt(8, persoon.getOuderlijkGezin().getNr());
                    } else {
                        psPersoon.setNull(8, java.sql.Types.INTEGER);
                    }
                    psPersoon.executeUpdate();
                }
                for (Gezin gezin : admin.getGezinnen()) {
                    psGezin.setInt(1, gezin.getNr());
                    psGezin.setInt(2, gezin.getOuder1().getNr());
                    if (gezin.getOuder2() != null) {
                        psGezin.setInt(3, gezin.getOuder2().getNr());
                    } else {
                        psGezin.setNull(3, java.sql.Types.INTEGER);
                    }
                    if (gezin.getHuwelijksdatum() != null) {
                        psGezin.setString(4, StringUtilities.datumString(gezin.getHuwelijksdatum()));
                    } else {
                        psGezin.setString(4, "");
                    }
                    if (gezin.getScheidingsdatum() != null) {
                        psGezin.setString(5, StringUtilities.datumString(gezin.getScheidingsdatum()));
                    } else {
                        psGezin.setString(5, "");
                    }
                    psGezin.executeUpdate();
                }

            } catch (Exception ex) {
                System.out.println("Failed to save administration: " + ex.getMessage());
            } finally {
                this.closeConnection();
            }
        } else {
            System.out.println("Not correctly configured");
        }
    }

    /**
     * Laadt de instellingen, in de vorm van een Properties bestand, en
     * controleert of deze in de correcte vorm is, en er verbinding gemaakt kan
     * worden met de database.
     *
     * @param props
     * @return
     */
    @Override
    public final boolean configure(Properties props) {
        this.props = props;
        if (!isCorrectlyConfigured()) {
            System.err.println("props mist een of meer keys");
            return false;
        }

        try {
            initConnection();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            closeConnection();
        }
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException {
        //opgave 4
        try {
            System.setProperty("jdbc.drivers", this.props.getProperty("driver"));
            String url = this.props.getProperty("url");
            String username = this.props.getProperty("username");
            String password = this.props.getProperty("password");

            this.conn = DriverManager.getConnection(url, username, password);
        } catch (Exception ex) {
            System.out.println("Can't establish connection: " + ex.getMessage());
        }
    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private class familyrelation {

        public int child;
        public int parents;

        familyrelation(int child, int parents) {
            this.child = child;
            this.parents = parents;
        }
    }
}
