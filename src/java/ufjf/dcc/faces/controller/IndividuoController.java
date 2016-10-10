/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufjf.dcc.faces.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import ufjf.dcc.faces.model.Individuo;
import ufjf.dcc.faces.persistence.IndividuoDAO;

/**
 *
 * @author Heder Soares Bernardino
 */
@ManagedBean
@ViewScoped
public class IndividuoController implements Serializable {

    private static final String DIRETORIO_FOTOS = "/home/hedersb/workspace/dcc-ufjf-faces/web/resources/images/";

    private List<Individuo> individuos;
    private Individuo individuo;
    private IndividuoDAO individuoDAO;
    private String filtro;
    private String caracteristica;
    private UploadedFile file;

    public IndividuoController() {
        this.individuo = new Individuo();
    }

    /*
     @ManagedProperty("#{carService}")
     private CarService service;*/
    @PostConstruct
    public void init() {
        //cars = service.createCars(48);
        this.individuoDAO = new IndividuoDAO();

        try {
            this.individuos = this.individuoDAO.lista();
        } catch (SolrServerException ex) {
            Logger.getLogger(IndividuoController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Exception", ex.getMessage()));
        } catch (IOException ex) {
            Logger.getLogger(IndividuoController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Exception", ex.getMessage()));
        }
    }

    public List<Individuo> getIndividuos() {
        return individuos;
    }

    public void upload(FileUploadEvent e) {
        try {
            // Get uploaded file from the FileUploadEvent
            this.file = e.getFile();
            // Print out the information of the file
            System.out.println("Uploaded File Name Is :: " + file.getFileName() + " :: Uploaded File Size :: " + file.getSize());
            //atualiza nome do arquivo
            //Path folder = Paths.get(this.DIRETORIO_FOTOS);
            String filename = UUID.randomUUID().toString();
            String extension = FilenameUtils.getExtension(file.getFileName());
            
            this.individuo.setFoto(filename + "." + extension);
            
            //Path filePath = Files.createTempFile(folder, filename, "." + extension);
            Path filePath = Paths.get(this.DIRETORIO_FOTOS + filename + "." + extension);

            //salva arquivo
            try (InputStream input = this.file.getInputstream()) {
                Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("Uploaded file successfully saved in " + file);
            
            System.out.println("Uploaded file successfully saved in " + this.individuo.getFoto());
            
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Foto carregada com sucesso!"));
        } catch (IOException ex) {
            Logger.getLogger(IndividuoController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Exception", ex.getMessage()));
        }
    }

    public void insere() {

        try {

            System.out.println("Foto do individuo: " + this.individuo.getFoto());
            
            this.individuoDAO.inserir(individuo);

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Individuo salvo com sucesso!"));
            individuo = new Individuo();
            file = null;

            /*
             SolrInputDocument doc = new SolrInputDocument();
             doc.addField("id", "1");
             doc.addField("first_name", "Ann");
             doc.addField("last_name", "Smit");
             doc.addField("email", "test@test.com");
             try {
             solr.add(doc);
             solr.commit();
             } catch (SolrServerException e) {
            
             } catch (IOException ex) {
             Logger.getLogger(IndividuoController.class.getName()).log(Level.SEVERE, null, ex);
             }
             */
        } catch (IOException ex) {
            Logger.getLogger(IndividuoController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Exception", ex.getMessage()));
        }
    }

    public void incluiCaracteristica() {

        if (this.individuo.getCaracteristicas() == null) {
            this.individuo.setCaracteristicas(new ArrayList<String>());
        }
        this.individuo.getCaracteristicas().add(this.caracteristica);
        this.caracteristica = "";

    }

    public void filtra() {

        try {
            this.individuos = this.individuoDAO.lista(filtro);
        } catch (SolrServerException ex) {
            Logger.getLogger(IndividuoController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Exception", ex.getMessage()));
        } catch (IOException ex) {
            Logger.getLogger(IndividuoController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Exception", ex.getMessage()));
        }

    }

    /**
     * @return the filtro
     */
    public String getFiltro() {
        return filtro;
    }

    /**
     * @param filtro the filtro to set
     */
    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    /**
     * @return the individuo
     */
    public Individuo getIndividuo() {
        return individuo;
    }

    /**
     * @param individuo the individuo to set
     */
    public void setIndividuo(Individuo individuo) {
        this.individuo = individuo;
    }

    /**
     * @return the caracteristica
     */
    public String getCaracteristica() {
        return caracteristica;
    }

    /**
     * @param caracteristica the caracteristica to set
     */
    public void setCaracteristica(String caracteristica) {
        this.caracteristica = caracteristica;
    }

    /**
     * @return the file
     */
    public UploadedFile getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
