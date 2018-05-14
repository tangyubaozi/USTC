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
	 * 存储参数类的序列化文件
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
	 * 读取序列化后存储的参数类
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
