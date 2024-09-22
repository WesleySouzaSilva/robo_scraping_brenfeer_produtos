package br.com.brenfeer.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class LeitorXml {

	// f�brica de construtores (de documentos)
	private DocumentBuilderFactory fabrica;
	// construtor de documentos
	private DocumentBuilder docBuilder;

	public LeitorXml() throws ParserConfigurationException {
		fabrica = DocumentBuilderFactory.newInstance();
		docBuilder = fabrica.newDocumentBuilder();
	}

	public List<List<String>> processar(InputStream arquivoXml, List<String> listaTags) throws SAXException, IOException {

	    List<List<String>> lista = new ArrayList<List<String>>();
	    List<String> sublista = new ArrayList<String>();

	    // documento
	    Document doc = docBuilder.parse(arquivoXml);

	    // Obtem, pelo nome, todas as tags do documento
	    // tag = <tagNome> valor </tagNome>
	    NodeList nodes = doc.getElementsByTagName("*");

	    int quantTags = 0;

	    // percorre todas as tags do arquivo
	    for (int i = 0; i < nodes.getLength(); i++) {
	        // elemento = <tagNome> valor </tagNome>
	        Element elemento = (Element) nodes.item(i);
	        // id, nome, rua, cidade, estado
	        String tagNome = elemento.getNodeName();

	        // processa apenas as tags contidas em listaTags
	        if (listaTags.contains(tagNome)) {
	            // valor da tag
	            String valor = elemento.getFirstChild().getNodeValue();
	            sublista.add(valor);

	            // quantidade de tags processadas
	            quantTags++;
	            // um grupo de tags completas foram processadas
	            if (quantTags % listaTags.size() == 0) {
	                lista.add(sublista);
	                sublista = new ArrayList<String>();
	            }
	        }
	    }

	    return lista;
	}

}
