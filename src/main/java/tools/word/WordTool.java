package tools.word;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

/**
 * Created by martin on 07.04.2017.
 */
public class WordTool {

    private FileSystem fileSystem;
    private Path pathToDocumentXML;
    private String xml;
    private String filename;

    public WordTool(String originalFilename, String filename) throws URISyntaxException, IOException {

        Files.copy(new File(originalFilename).toPath(), new File(filename).toPath(), StandardCopyOption.REPLACE_EXISTING);

        this.filename = filename;


        URI docxUri = URI.create("jar:file:" + Paths.get(filename).toUri().getPath());
        Map<String, String> zipProperties = new HashMap<>();
        zipProperties.put("encoding", "UTF-8");
        try (FileSystem zipFS = FileSystems.newFileSystem(docxUri, zipProperties)) {

            Path documentXmlPath = zipFS.getPath("/word/document.xml");

            byte[] content = Files.readAllBytes(documentXmlPath);
            xml = new String(content, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getXml() {
        return xml;
    }

    public void write(String xml) throws URISyntaxException {

        URI docxUri = URI.create("jar:file:" + Paths.get(filename).toUri().getPath());

        Map<String, String> zipProperties = new HashMap<>();
        zipProperties.put("encoding", "UTF-8");
        try (FileSystem zipFS = FileSystems.newFileSystem(docxUri, zipProperties)) {

            Path documentXmlPath = zipFS.getPath("/word/document.xml");

            byte[] content = xml.getBytes(StandardCharsets.UTF_8);
            Files.delete(documentXmlPath);
            Files.write(documentXmlPath, content);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String extractLineXML(String hint){

        int i = xml.indexOf(hint);
        int lineBegin = xml.lastIndexOf("<w:tr ",i);
        int lineEnd = xml.indexOf("</w:tr>", i) + "</w:tr>".length();

        return xml.substring(lineBegin, lineEnd);

    }

    public void replaceLineXML(String hint, List<String> xmlForNewLines){

        int i = xml.indexOf(hint);
        int lineBegin = xml.lastIndexOf("<w:tr ",i);
        int lineEnd = xml.indexOf("</w:tr>", i) + "</w:tr>".length();

        String xmlBefore = xml.substring(0, lineBegin);
        String xmlAfter = xml.substring(lineEnd);

        StringBuilder sb = new StringBuilder();

        sb.append(xmlBefore);

        for(String xml: xmlForNewLines){
            sb.append(xml);
        }

        sb.append(xmlAfter);

        xml = sb.toString();

    }


}
