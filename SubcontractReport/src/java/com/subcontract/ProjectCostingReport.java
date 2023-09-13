/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.subcontract;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import oracle.xdo.XDOException;
import oracle.xdo.template.FOProcessor;
import oracle.xdo.template.RTFProcessor;
/**
 *
 * @author Nandhini
 */
@Path("/project")
public class ProjectCostingReport {
    @Context
    private UriInfo context;
    DbPackageCall dbPackageCall = new DbPackageCall();
    
    public ProjectCostingReport() {
    }
    
    @Path("/costingreport")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response PCdetails(
            @QueryParam(value = "P_BU_ID") String P_BU_ID,
            @QueryParam(value = "P_FILE_FORMAT") String P_FILE_FORMAT
    ) throws ParseException{
        String fileName = "";
        if(P_FILE_FORMAT.equals("pdf")){
        fileName = "Project Costing Report.pdf";
        }
        else if(P_FILE_FORMAT.equals("xlsx")){
        fileName = "Project Costing Report.xlsx";
        }
        else{
        fileName = "Project Costing Report.docx";
        }
        
        String xmlData = DbPackageCall.projectCostingReport(P_BU_ID);
        String filePath = "/u01/data/reports/Project Costing Report.rtf";
//        String filePath="D:/Documents/RTFbkp/Project Costing Report.rtf";
        if(xmlData !=null){
            ResponseBuilder builder = Response.ok(rtfReport(xmlData, filePath,P_FILE_FORMAT));
            builder.header("Content-Disposition", "attachment; filename=" + fileName);
            return builder.build();  
        }else{
            Response.ResponseBuilder builder = Response.ok(DbPackageCall.responseToRest("No data found"));
            return builder.build();
        }

    }
    public byte[] rtfReport(String xmlData, String filePath,String file_Type) {
        InputStream fiS = null;
        ByteArrayInputStream xslInStream = null;
        ByteArrayInputStream dataStream = null;
        ByteArrayOutputStream pdfOutStream = null;

        byte[] dataBytes = null;
        byte outFileTypeByte = 0;
        try {

            fiS = new FileInputStream(new File(filePath));
            
             if(file_Type.equals("pdf")){
                 outFileTypeByte = FOProcessor.FORMAT_PDF;
             }
             else if(file_Type.equals("xlsx")){
               outFileTypeByte = FOProcessor.FORMAT_XLSX;
             }
             else{
               outFileTypeByte = FOProcessor.FORMAT_DOCX;
             }
            RTFProcessor rtfP = new RTFProcessor(fiS); 
            ByteArrayOutputStream xslOutStream = new ByteArrayOutputStream();
            rtfP.setOutput(xslOutStream);
            rtfP.process();
            xslInStream = new ByteArrayInputStream(xslOutStream.toByteArray());

            FOProcessor processor = new FOProcessor();
            processor.setConfig("/u01/data/font/xdo.cfg"); 
            dataStream = new ByteArrayInputStream(xmlData.getBytes());

            processor.setData(dataStream);
            processor.setTemplate(xslInStream);

            pdfOutStream = new ByteArrayOutputStream();
            processor.setOutput(pdfOutStream);

            processor.setOutputFormat(outFileTypeByte);
            processor.generate();
            dataBytes = pdfOutStream.toByteArray();
        } catch (XDOException | FileNotFoundException ex) {
            Logger.getLogger(ApprovedPCReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataBytes;
    } 
    
}
