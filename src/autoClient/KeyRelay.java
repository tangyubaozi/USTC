package autoClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
	
	private List<FlashAddrCell> stationThis;
	private List<FlashAddrCell> stationOther;
	private int numThis;
	private int numOther;
	private byte[] data;
	private int cmd_cnt;
	
	public static final Path PATH_ADDRESS_TABLE_THIS = Paths.get(".").resolve("this_station_address_table.txt");
	public static final Path PATH_ADDRESS_TABLE_OTHER = Paths.get(".").resolve("other_station_address_table.txt");
	
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
        ByteBuffer b = ByteBuffer.allocate(96);
        for (int i = 0; i < 96; i++) {
            b.put((byte) i);
        }
        b.position(0);
        Path path = Paths.get(".").resolve("datas");
    	CallDLL.INSTANCE.BlocksCnt(3, b, path.toString());
    	KeyRelay kk = new KeyRelay();
    	kk.readTemplate();
    	System.out.println("ok");
	}
    
}

