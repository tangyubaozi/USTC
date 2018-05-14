package autoClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class SerializeObject {
	private Path path;
	private Class objectClass;
	
	public SerializeObject(Path path) {
		this.path = path;

	}
	
	/**
	 * �洢����������л��ļ�
	 */
	public void saveObject(Object o){

		try(ObjectOutputStream outputStream = new ObjectOutputStream(
				new FileOutputStream(path.toFile())))
		{
			outputStream.writeObject(o);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
	}
	
	/**
	 * ��ȡ���л���洢�Ĳ�����
	 * @throws FileNotFoundException 
	 */
	public Object loadObject() throws FileNotFoundException{
		Object object = null;
		try(ObjectInputStream in = new ObjectInputStream(
				new FileInputStream(path.toFile()))){
			object = in.readObject();
		} catch (FileNotFoundException e1) {
			throw e1;
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {	
			
		}
		return object;
	}

}
