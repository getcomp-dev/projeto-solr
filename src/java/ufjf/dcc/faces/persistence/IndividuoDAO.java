/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufjf.dcc.faces.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import ufjf.dcc.faces.model.Individuo;

/**
 *
 * @author hedersb
 */
public class IndividuoDAO {

    private static final String URL = "http://localhost:8983/solr/dcc-ufjf-faces";

    private SolrClient solr;

    public IndividuoDAO() {
        solr = new HttpSolrClient(URL);
    }

    public List<Individuo> lista() throws SolrServerException, IOException {
        return this.lista(null);
    }

    public List<Individuo> lista(String filtro) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        if (filtro == null || filtro.equals("")) {
            query.setQuery("*:*");
        } else {
            query.setQuery(filtro);
        }

        QueryResponse response = null;
        response = solr.query(query);

        SolrDocumentList list = response.getResults();
        List<Individuo> individuos = new ArrayList<>();
        for (Object object : list.toArray()) {
            Individuo i = new Individuo();
            SolrDocument doc = (SolrDocument) object;

            i.setId(UUID.fromString((String) doc.get("id")));
            i.setNome((String) doc.get("nome"));
            i.setDescricao((String) doc.get("descricao"));
            i.setFoto((String) ((List) doc.get("foto")).get(0)); //Foto foi definido nos dados como sendo multivalorado
            if (doc.get("caracteristicas") != null) {
                i.setCaracteristicas(new ArrayList<String>());
                for (Object os : (List) doc.get("caracteristicas")) {
                    i.getCaracteristicas().add((String) os);
                }
            }

            individuos.add(i);

            //System.out.println("ID: " + doc.get("id"));
            //System.out.println("Descricao: " + doc.get("descricao"));
            //System.out.println("Foto: " + doc.get("foto"));
            //System.out.println("Caracteristicas: " + doc.get("caracteristicas"));
        }

        return individuos;
    }

    public void inserir(Individuo individuo) throws IOException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("nome", individuo.getNome());
        doc.addField("descricao", individuo.getDescricao());
        doc.addField("foto", individuo.getFoto());
        doc.addField("caracteristicas", individuo.getCaracteristicas());
        try {
            solr.add(doc);
            solr.commit();
        } catch (SolrServerException ex) {
            throw new IOException(ex);
        }
        
    }

}
