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
 * REST Web Service
 *
 * @author gautham.r
 */
@Path("/sub")
public class CertificationReport {

    @Context
    private UriInfo context;
    DbPackageCall dbPackageCall = new DbPackageCall();

    /**
     * Creates a new instance of ReservationContract
     */
    public CertificationReport() {
    }

    /**
     * Retrieves representation of an instance of com.omniyat.rpt.ReservationContract
     * @param P_PROJ_NAME 
     * @param P_PROP_NAME 
     * @param P_File_Type 
     * @return an instance of java.lang.String
     */
    @Path("/cert")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response certificatePrint(
             @QueryParam(value = "P_CERT_ID") String P_CERT_ID,
             @QueryParam(value = "P_INTERIM_TYPE") String P_INTERIM_TYPE,
             @QueryParam(value = "P_STATUS") String P_STATUS,
             @QueryParam(value = "P_FILE_TYPE") String P_FILE_TYPE) throws ParseException{

        String fileName = "";
         String filePath="";
        if(P_FILE_TYPE.equals("pdf")){
            fileName = "PAY_CERT-"+P_CERT_ID +".pdf";
        }else{
            fileName = "PAY_CERT-"+P_CERT_ID +".xlsx";
        }
        
//        //-- Date
//        SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
//        java.util.Date date = parser.parse(P_DATE);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
//        String dateParam = formatter.format(date);
//        SimpleDateFormat pkgFormatter = new SimpleDateFormat("dd-MMM-yyyy");
//        String dateFormat = pkgFormatter.format(date);
//        System.out.println("Date--"+dateParam);
        
        
        if(P_INTERIM_TYPE.equalsIgnoreCase("External")){
            if(P_STATUS.equalsIgnoreCase("TRNS_DRAFT")){
                filePath="/u01/Payment_Certificate_Interim_EXT_D.rtf";
            }else{
                filePath="/u01/Payment_Certificate_Interim_EXT.rtf";
            }
        }else{
            if(P_STATUS.equalsIgnoreCase("TRNS_DRAFT")){
                filePath="/u01/Payment_Certificate_Interim_INT_D.rtf";
            }else{
                filePath="/u01/Payment_Certificate_Interim_INT.rtf";
            }
        }
       
        String xmlData = DbPackageCall.subContractCertification(P_CERT_ID);
        ResponseBuilder builder = Response.ok(rtfReport(xmlData, filePath,P_FILE_TYPE));
        builder.header("Content-Disposition", "attachment; filename=" + fileName);
        return builder.build();
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
           // outFileTypeByte = FOProcessor.FORMAT_XLSX;
              if(file_Type.equals("pdf")){
                 outFileTypeByte = FOProcessor.FORMAT_PDF;
             }
             else{
               outFileTypeByte = FOProcessor.FORMAT_XLSX;
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
            Logger.getLogger(CertificationReport.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("RTF==>ex==>"+ex.toString());
        }
        return dataBytes;
    }
}
