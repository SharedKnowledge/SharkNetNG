package net.sharkfw.security.key.storage.filesystem;

import net.sharkfw.security.key.storage.SharkKeyStorage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author ac
 */
public class FSSharkKeyStorage {

    private final String filePath;

    /**
     * Constructor.
     * @param filePath Filepath to store or load the object.
     */
    public FSSharkKeyStorage(final String filePath) {
        this.filePath = filePath;
    }

    /**
     * Serialize the given object and saved it to the given path.
     * @param sharkKeyStorage {@link SharkKeyStorage}
     * @return True or false
     */
    public boolean save(SharkKeyStorage sharkKeyStorage) {
        try {
            this.saveAndThrowExceptions(sharkKeyStorage);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /*
    author: Mario Neises
    */
    public void saveAndThrowExceptions(SharkKeyStorage sharkKeyStorage) throws IOException {
        FileOutputStream fileOutputStream =  new FileOutputStream(filePath);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(sharkKeyStorage);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    /**
     * Deserialize the object from the given filepath.
     * @return SharkKeyStorage
     */
    public SharkKeyStorage load() {

        SharkKeyStorage sharkKeyStorage;

        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            sharkKeyStorage = (SharkKeyStorage) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return sharkKeyStorage;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
