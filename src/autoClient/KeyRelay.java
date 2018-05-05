package autoClient;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class KeyRelay {
	
	/**
	 * 读取模板
	 */
    public void readTemplate(){
        String string;
        ByteBuffer buffer = ByteBuffer.allocate(500);
        //由于微软生成的文本文件是UTF-8+BOM格式，java不能很好的处理次格式，因此使用辅助类UnicodeReader来处理文本文件
        try(BufferedReader in = new BufferedReader(new UnicodeReader(new FileInputStream("C:\\Users\\Ttyy\\Desktop\\templateXOR_V1.2.txt"), "UTF-8"))){
            while(((string = in.readLine())!=null)&&(!string.isEmpty())){
                String[] ss = string.split(" # ");
                ss = ss[0].split(",");
                for (String s : ss) {
                    if(s.matches("\\{.*}")){
                        System.out.println(s);
                        buffer.put((byte) 0xff);
                    }else{
                        if(s.startsWith("0x")||s.startsWith("0X")){
                            s=s.substring(2);
                        }
                        buffer.put((byte) Integer.parseInt(s,16));
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void fillAll(){
    	
    }
    
    public void saveXML(){

    	DocumentBuilder builder;
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
        builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("template");
        Element child = doc.createElement("groundStation1");
        for (int i = 1; i < 4; i++) {
			Element indexChild = doc.createElement("keyIndex"+i);
			Element flashAddress = doc.createElement("flashAddress");
			flashAddress.setTextContent("FFAA1234");
			indexChild.appendChild(flashAddress);
			Element sdramAddress = doc.createElement("sdramAddress");
			sdramAddress.setTextContent("1F2A2223");
			indexChild.appendChild(sdramAddress);
			root.appendChild(indexChild);
		}
         doc.appendChild(root);
         TransformerFactory tff = TransformerFactory.newInstance();
         tff.setAttribute("indent-number", new Integer(6));
         Transformer tf = tff.newTransformer();
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.transform(new DOMSource(doc), new StreamResult("C:\\Users\\Ttyy\\Desktop\\templateXOR_V1.2.xml"));
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
    	
    }
    
    public void readKeyTable(File file){
    	
    }
    
    public void writeKeyTable(File file){
    	
    }
    
    public static void main(String[] args) {
    	KeyRelay kk = new KeyRelay();
    	kk.saveXML();
	}
    
}

class FlashAddrCell implements Recordable{
	public Calendar time;
	public long flashAddr;
	public long length;
	public boolean used;
	public static final SimpleDateFormat bartDateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ ");  
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		Date date = time.getTime();       
		s.append(bartDateFormat.format(date));
		s.append(Long.toHexString(flashAddr));
		s.append(length);
		s.append(used);
		return s.toString();
	}

	@Override
	public boolean readFromDataInput(DataInput in) {
		try {
			time = Calendar.getInstance();
			time.setTimeInMillis(in.readLong());
			flashAddr = in.readLong();
			length = in.readLong();
			used = in.readBoolean();
			
			return true;
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
	}

	@Override
	public void writeToDataOutput(DataOutput out) {
		try {
			out.writeLong(time.getTimeInMillis());
			out.writeLong(flashAddr);
			out.writeLong(length);
			out.writeBoolean(used);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public FlashAddrCell newInstance() {
		// TODO Auto-generated method stub
		return new FlashAddrCell();
	}
	
}

