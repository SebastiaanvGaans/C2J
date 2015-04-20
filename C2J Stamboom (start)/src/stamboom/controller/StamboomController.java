/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import stamboom.domain.Administratie;
import stamboom.storage.DatabaseMediator;
import stamboom.storage.IStorageMediator;

public class StamboomController {

    private Administratie admin;
    private IStorageMediator storageMediator;

    /**
     * creatie van stamboomcontroller met lege administratie en onbekend
     * opslagmedium
     */
    public StamboomController() {
        admin = new Administratie();
        storageMediator = null;
    }

    public Administratie getAdministratie() {
        return admin;
    }

    /**
     * administratie wordt leeggemaakt (geen personen en geen gezinnen)
     */
    public void clearAdministratie() {
        admin = new Administratie();
    }

    /**
     * administratie wordt in geserialiseerd bestand opgeslagen
     *
     * @param bestand
     * @throws IOException
     */
    public void serialize(File bestand) throws IOException {
        //todo opgave 2
        FileOutputStream fileOut = new FileOutputStream(bestand);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);

        try {
            out.writeObject(this.admin);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            fileOut.close();
            out.close();
        }

    }

    /**
     * administratie wordt vanuit geserialiseerd bestand gevuld
     *
     * @param bestand
     * @throws IOException
     */
    public void deserialize(File bestand) throws IOException, ClassNotFoundException {
        //todo opgave 2
        FileInputStream fileIn = new FileInputStream(bestand);
        ObjectInputStream in = new ObjectInputStream(fileIn);

        try {
            this.admin = (Administratie) in.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            fileIn.close();
            in.close();
        }
    }

    // opgave 4
    private void initDatabaseMedium() throws IOException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("database.properties")) {
            props.load(in);
        }
        storageMediator = new DatabaseMediator();
        storageMediator.configure(props);

    }

    /**
     * administratie wordt vanuit standaarddatabase opgehaald
     *
     * @throws IOException
     */
    public void loadFromDatabase() throws IOException {
        //todo opgave 4
        initDatabaseMedium();
        this.admin = storageMediator.load();
    }

    /**
     * administratie wordt in standaarddatabase bewaard
     *
     * @throws IOException
     */
    public void saveToDatabase() throws IOException {
        //todo opgave 4
        initDatabaseMedium();
        storageMediator.save(admin);
    }

}
