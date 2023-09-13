/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.subcontract;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author gautham.r
 */
public class DbPackageCall {

    static Connection connection;
    static Statement statement;
    static ResultSet resultSet;
    static PreparedStatement ps;

        public static Connection getConnectionDS(String datasource) throws SQLException,
            NamingException {
        Connection con = null;
        DataSource data = null;
        Context initialContext = new InitialContext();
        if (initialContext == null) {

        }
        data = (DataSource) initialContext.lookup(datasource);
        if (data != null) {
            con = data.getConnection();
        } else {
            System.out.println("Failed to Find JDBC DataSource.");
        }
        return con;
    }
    
    public static Connection getDBConnectionLocal() throws SQLException, ClassNotFoundException {
        Connection con = null;
        try { 
            Class.forName("oracle.jdbc.driver.OracleDriver"); 
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.21.67.79:1533/omnipdb1.606532292.oraclecloud.internal", "XXSUBCONTST", "Omni_tst21");
         } catch (SQLException ex) {
            ex.printStackTrace();
        } 
        return con;
    }
    
    /*
    * DB Initializer
    */
    
    public static void dbInitialization() throws SQLException, NamingException, ClassNotFoundException {
        connection = getConnectionDS("subcont");
//        connection=getDBConnectionLocal();     
    }
    
       public static String subContractCertification(String P_CERT_ID) {
        String xmlString = null;
        try {
            dbInitialization();
            String sql = "select XXSC_REPORT_PKG.Payment_certificate('"+ P_CERT_ID +"') xml from dual";
            System.out.println("==>"+sql);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                xmlString = resultSet.getString("xml");
                System.out.println(xmlString);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbPackageCall.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbDestroy();
            } catch (SQLException ex) {
                Logger.getLogger(DbPackageCall.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("ex==>"+ex.toString());
            }
            return xmlString;
        }
    }
   public static String projectCostingReport(String P_BU_ID) {
        String xmlString = null;
        try {
            dbInitialization();
            String sql = "select XXREP_REPORT_PKG.PROJECT_COSTING_REPORT('"+ P_BU_ID  +"') xml from dual";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                xmlString = resultSet.getString("xml");
                System.out.println(xmlString);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbPackageCall.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbDestroy();
            } catch (SQLException ex) {
                Logger.getLogger(DbPackageCall.class.getName()).log(Level.SEVERE, null, ex);
            }
            return xmlString;
        }
    }   
         
    public static String approvedPCReport(String P_BU_ID,String P_DATE) {
        String xmlString = null;
        try {
            dbInitialization();
            String sql = "select XXREP_REPORT_PKG.APPROVED_PC_REPORT('"+ P_BU_ID  +"','"+P_DATE +"') xml from dual";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                xmlString = resultSet.getString("xml");
                System.out.println(xmlString);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbPackageCall.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbDestroy();
            } catch (SQLException ex) {
                Logger.getLogger(DbPackageCall.class.getName()).log(Level.SEVERE, null, ex);
            }
            return xmlString;
        }
    }   
      public static String errorStatus() {
        String xmlString = null;
        try {
            dbInitialization();
            String sql = "select XXPRISM_REPORT_PKG.XXPRISM_ERROR_STATUS() xml from dual";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                xmlString = resultSet.getString("xml");
                System.out.println(xmlString);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DbPackageCall.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbDestroy();
            } catch (SQLException ex) {
                Logger.getLogger(DbPackageCall.class.getName()).log(Level.SEVERE, null, ex);
            }
            return xmlString;
        }
    }
    
      public static String responseToRest(String result) {
        org.json.simple.JSONObject json = new org.json.simple.JSONObject();
        json.put("result", result);
        return json.toJSONString();
        }  
         
    public static void dbDestroy() throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (ps != null) {
            ps.close();
        }
        if (connection != null) {
            connection.close();
        }
    }



}


