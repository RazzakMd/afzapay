package com.paymentech.iPayment;

import com.paymentech.orbital.sdk.configurator.Configurator;
import com.paymentech.orbital.sdk.configurator.ConfiguratorIF;
import com.paymentech.orbital.sdk.interfaces.RequestIF;
import com.paymentech.orbital.sdk.interfaces.ResponseIF;
import com.paymentech.orbital.sdk.interfaces.TransactionProcessorIF;
import com.paymentech.orbital.sdk.request.FieldNotFoundException;
import com.paymentech.orbital.sdk.request.Request;
import com.paymentech.orbital.sdk.transactionProcessor.TransactionException;
import com.paymentech.orbital.sdk.transactionProcessor.TransactionProcessor;
import com.paymentech.orbital.sdk.util.exceptions.InitializationException;

import java.io.*;
import java.util.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ipsServlet  extends HttpServlet { 

    static String getServerURL(String s, String s1, String s2)
    {
        lDebug("Orbital - ipsServlet.getServerURL(String, String,String) : Entered");
        StringBuffer stringbuffer = new StringBuffer("http://");
        stringbuffer.append(s);
        stringbuffer.append(":");
        stringbuffer.append(s1);
        stringbuffer.append("/");
        stringbuffer.append(s2);
        stringbuffer.append("/");
        lDebug("Orbital - ipsServlet.getServerURL(String, String,String) URL : " + stringbuffer.toString());
        lDebug("Orbital - ipsServlet.getServerURL(String, String,String) : Exiting");
        return stringbuffer.toString();
    }

    static void lHttpError(HttpServletResponse httpservletresponse, String msg, String expmsg)
    {
      try
        {
          lDebug("Orbital - ipsServlet.lHttpError(String): Entered. ");
          httpservletresponse.sendRedirect(expmsg + "Error.jsp?errMessage=" + msg);
          lDebug("Orbital - ipsServlet.lHttpError(String): Exiting.");
        }
        catch(Exception exception)
        {
          lDebug("Orbital - ipsServlet.lHttpError(String): In the catch of Exception = " + exception.getMessage(), exception);
        }

    }

    static void lDebug(String s)
    {
        oracle.apps.iby.exception.Log.debug(s);
    }

    static void lDebug(String s, Exception exception)
    {
        if(s != null && exception != null)
            lDebug(s + "\n" + exception.getMessage());
        else
            lDebug(s);
    }

    public void doGet(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
        throws ServletException, IOException
    {
        lDebug("Orbital - com.orbital.ipsProcess.doGet(): Entered.");
        doPost(httpservletrequest, httpservletresponse);
        lDebug("Orbital - com.orbital.ipsProcess.doGet(): Exiting.");
    }

    public void doPost(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
        throws ServletException, IOException
    {
        lDebug("orbital - com.orbital.ipsProcess.doPost(): Entered.");
        PrintWriter printwriter = httpservletresponse.getWriter();

        Hashtable hashtable = new Hashtable();
        Enumeration enumeration = httpservletrequest.getParameterNames();
        String s = "";
        String s2 = "";

        try   // getParameters
          {
            String s1;
            String s3;
            for(; enumeration.hasMoreElements(); hashtable.put(s1, s3))
            {
                s1 = (String)enumeration.nextElement();
                s3 = httpservletrequest.getParameter(s1);
            }

            lDebug("Retrieving all keys from the Hashtable");

            Enumeration e = hashtable.keys();
            String s9;
            String s10;

            while( e.hasMoreElements() ){        
               s9 = (String) e.nextElement();
               s10 = (String) hashtable.get(s9);
               lDebug("Key " + s9 + " = " + s10 );
             }

            lDebug("orbital - com.orbital.ipsProcess.doPost.try ...........") ;
          }
          catch(Exception exception) {
            lDebug("ipsServlet.doPost.getParameters(HttpServletRequest,HttpServletResponse ) - Exception Error: " + exception.getMessage(), exception);
            String s6 = getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            // iPaymentUtils.displayError(httpservletresponse, exception.getMessage(), s6);
            lHttpError(httpservletresponse, exception.getMessage(), s6);
         }

        ConfiguratorIF configurator = null;
        lDebug("orbital - ConfiguratorIF configurator initialization....");

        Hashtable hResponse = new Hashtable();

        System.setProperty( "http.proxyHost", "www-proxy.oracleoutsourcing.com");
        System.setProperty( "http.proxyPort", "80");

    	//Create a Transaction Processor
        //The Transaction Processor acquires and releases resources and executes transactions.
        //It configures a pool of protocol engines, then uses the pool to execute transactions.
        TransactionProcessorIF tp = null;
        try {
            tp = new TransactionProcessor();
          } catch (InitializationException iex) {
            lDebug("ipsServlet.doPost.TransactionProcessorIF.try(HttpServletRequest,HttpServletResponse ) - Exception Error: " + iex.getMessage(), iex);
            String s6 = getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            lHttpError(httpservletresponse, iex.getMessage(), s6);
          }

    	//Create a request object
        //The request object uses the XML templates along with data we provide
        //to generate a String representation of the xml request
        RequestIF request = null;
        lDebug(" after RequestIF initialization.");
 
        lDebug(" OapfAction : " + (String) hashtable.get("OapfAction" ) );
        String lOapfAction = (String) hashtable.get( "OapfAction" );

//        if ((String) hashtable.get( "OapfAction" ) == "oracapture") { 
//           lDebug( " .... AuthCapture Process....");
//           AuthCapture( hashtable, request );
//        } else if ((String) hashtable.get( "OapfAction" ) == "oraauth") { 
//                 lDebug( " .... Authonly Process....");
//                 AuthOnly( hashtable, request );
//        } else {
//            lDebug( " .... Error .... OapfAction : " + hashtable.get("OapfAction"));
//        }

          try {
            lDebug(" lOapfAction : " + lOapfAction );
            if (lOapfAction.compareTo("oracapture") == 0) { 
               lDebug( " .... AuthCapture Process....");
               request = AuthCapture(hashtable);
            } else if (lOapfAction.compareTo("oraauth") == 0) { 
                     lDebug( " .... Authonly Process....");
                     request = AuthOnly(hashtable);
            }
              else {
                 lDebug("OapfAction:[" + lOapfAction + "]" + ", not supported.");
                 throw new ServletException("OapfAction:[" + lOapfAction + "]" + ", not supported.");
            }
            }
              catch (Exception exception) {
                 lDebug("ipsServlet.doPost.TransactionProcessorIF.try(HttpServletRequest,HttpServletResponse ) - Exception Error: " + exception.getMessage(), exception);
                 String s6 = getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
                 lHttpError(httpservletresponse, exception.getMessage(), s6);
              }
            
        try {
            //Display the request
            lDebug("\nAuth Request:\n" + request.getXML());
          } catch (InitializationException ie) {
              lDebug("ipsServlet.doPost.try.request.getXML(HttpServletRequest,HttpServletResponse ) - Exception Error: Unable to initialize request object : " + ie.getMessage(), ie);
              String s6 = getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
              lHttpError(httpservletresponse, ie.getMessage(), s6);
          } catch (Exception e) {
             // lDebug("ipsProcess.orbital(HttpServletRequest,HttpServletResponse ) - Exception Error: " + e.getMessage()); //, exception);
              lDebug("ipsServlet.doPost.try.request.getXML(HttpServletRequest,HttpServletResponse ) - Exception Error: " + e.getMessage(), e);
              String s6 = getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
              lHttpError(httpservletresponse, e.getMessage(), s6);
          }

        //Process the transaction
        //Pass in the request object (created above), and receive a response object.
        //If the resources required by the Transaction Processor have been exhausted,
        //this code will block until the resources become available.
        //The "TransactionProcessor.poolSize" configuration property specifies how many resources
        //will be available.  The TransactionProcessor acts as a governor, only allowing
        //up to "poolSize" transactions outstanding at any point in time.
        //As transactions are completed, their resources are placed back in the pool.
        ResponseIF response = null;
        try {
            response = tp.process(request);
        } catch (TransactionException tex) {
            // lDebug("ipsProcess.orbital(HttpServletRequest,HttpServletResponse ) - Exception Error: Transaction failed, including retries and failover : " + tex.getMessage()); //, exception);
            // String s6 = iPaymentUtils.getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            // iPaymentUtils.displayError(httpservletresponse, tex.getMessage(), s6);
            lDebug("ipsServlet.doPost.process.try(HttpServletRequest,HttpServletResponse ) - Exception Error: Transaction failed, including retries and failover : " + tex.getMessage(), tex);
            String s6 = getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            lHttpError(httpservletresponse, tex.getMessage(), s6);
        }
        //Display the response
        //This line displays the entire xml response on the java system console.
        lDebug("\nResponse:\n" + response.toXmlString() + "\n");
        //The lines below report all the various attributes of the response object.
        //It is not necessary to use all of these attributes - use only the ones you need.
        //Also, some of the attributes are meaningful only for specific types of transactions,
        //but for consistency and simplicity in the sample code we dump them all for every transaction type.
        // System.out.println("Response Attributes:");
        // System.out.println("isGood=" + response.isGood());
        // System.out.println("isError=" + response.isError());
        // System.out.println("isQuickResponse=" + response.isQuickResponse());
        // System.out.println("isApproved=" + response.isApproved());
        // System.out.println("isDeclined=" + response.isDeclined());
        // System.out.println("AuthCode=" + response.getAuthCode());
        // System.out.println("TxRefNum=" + response.getTxRefNum());
        // System.out.println("ResponseCode=" + response.getResponseCode());
        // System.out.println("Status=" + response.getStatus());
        // System.out.println("Message=" + response.getMessage());
        // System.out.println("AVSCode=" + response.getAVSResponseCode());
        // System.out.println("CVV2ResponseCode=" + response.getCVV2RespCode());

        httpservletresponse.setHeader("oapfprefpsmsg", "");
        httpservletresponse.setHeader("oapfproccvv2", "");
        httpservletresponse.setHeader("OapfNumTrxns", "1");
        httpservletresponse.setHeader("OapfTrxnDate", ""); // 20110330140248 // ???? current date
        httpservletresponse.setHeader("oapffps_prexmldata", "");
        httpservletresponse.setHeader("OapfTrxnType", "2" ); // what is trxntype 2 ??? 
        httpservletresponse.setHeader("Connection", "close" );
        httpservletresponse.setHeader("OapfVendErrmsg", response.getMessage());
        httpservletresponse.setHeader("OapfAVScode", "");
        httpservletresponse.setHeader("oapfresptext", "");  // ??
        httpservletresponse.setHeader("oapfextendauthcode", response.getAuthCode() );
        httpservletresponse.setHeader("oapfproccardsecure", "");
        httpservletresponse.setHeader("oapfaddlmsgs", "" );
        httpservletresponse.setHeader("OapfOrderId", (String) hashtable.get("OapfOrderId"));
        httpservletresponse.setHeader("oapfavsaddr", "");
        // eResponse.put("[Mar 30, 2011 2:02:50 PM CDT]:1301511770286:Thread[Thread-59,10,main]:-1:-1:auohsaskj06:140.85.236.246:10720:20720:STATEMENT:[iby.payment.VendorAdapter.oraMipp]:Content-Type: text/html
        httpservletresponse.setHeader("oapfcardsecure", "");
        httpservletresponse.setHeader("OapfTrxnDate-0", "20110330140250"); // current date ???
        httpservletresponse.setHeader("OapfTrxnType-0", "2"); // ???
        httpservletresponse.setHeader("OapfVendErrCode", response.getResponseCode() );
        httpservletresponse.setHeader("OapfAuthcode", response.getAuthCode());
        httpservletresponse.setHeader("oapfpostfpsmsg", "");
        httpservletresponse.setHeader("oapffps_postxmldata", "");
        httpservletresponse.setHeader("oapfduplicate", "");
        httpservletresponse.setHeader("oapfprocavs", "");
        httpservletresponse.setHeader("OapfCVV2Result", "");
        httpservletresponse.setHeader("oapfavszip", "");
        httpservletresponse.setHeader("OapfRefcode", response.getTxRefNum());
        httpservletresponse.setHeader("oapfiavs", "N");  // ???
        httpservletresponse.setHeader("oapfhostcode", "");

        if ( response.isError() == true ) {
            // throw new eProcessError(response.getMessage());
            httpservletresponse.setHeader("oapfrespmsg", "Error");
            httpservletresponse.setHeader("oapfresult", "12"); // ?????
            httpservletresponse.setHeader("OapfErrLocation", "3"); /// ?????
            httpservletresponse.setHeader("OapfStatus", "16");   // Payment system failed
            httpservletresponse.setHeader("OapfStatus-0", "16");  // Payment system failed
            throw new ServletException( response.getMessage());
        } else if (response.isDeclined() == true) { 
            httpservletresponse.setHeader("oapfrespmsg", "Declined");
            httpservletresponse.setHeader("oapfresult", "12"); // ?????
            httpservletresponse.setHeader("OapfErrLocation", "3"); /// ?????
            if ("D7".compareTo(response.getResponseCode()) == 0) {
              httpservletresponse.setHeader("OapfStatus", "17");   // Unable to Pay(Insufficient funds)
              httpservletresponse.setHeader("OapfStatus-0", "17");  // Unable to Pay(Insufficient funds)
              } else if ("14".compareTo(response.getResponseCode()) == 0) {
                  httpservletresponse.setHeader("OapfStatus", "19");    // Invalid Credit Card Number
                  httpservletresponse.setHeader("OapfStatus-0", "19");  // Invalid Credit Card Number
              } else if ("68".compareTo(response.getResponseCode()) == 0) {
                  httpservletresponse.setHeader("OapfStatus", "19");    // Invalid Credit Card Number
                  httpservletresponse.setHeader("OapfStatus-0", "19");  // Invalid Credit Card Number
              } else {
                  httpservletresponse.setHeader("OapfStatus", "220");  // transaction failed
                  httpservletresponse.setHeader("OapfStatus-0", "220");  // transaction failed
              }
            // throw new ServletException( response.getMessage());
          } else if (response.isApproved() == true) {
            httpservletresponse.setHeader("oapfrespmsg", "Approved");
            httpservletresponse.setHeader("oapfresult", "0"); // ?????
            httpservletresponse.setHeader("OapfErrLocation", "0"); /// ?????
            httpservletresponse.setHeader("OapfStatus", "0000");
            httpservletresponse.setHeader("OapfStatus-0", "0000");  // ??
           } else { // this is not valid usually 
               httpservletresponse.setHeader("oapfrespmsg", "Error");
               httpservletresponse.setHeader("oapfrespmsg", "Error");
               httpservletresponse.setHeader("oapfresult", "12"); // ?????
               httpservletresponse.setHeader("OapfErrLocation", "3"); /// ?????
               httpservletresponse.setHeader("OapfStatus", "16");   // Payment system failed
               httpservletresponse.setHeader("OapfStatus-0", "16");  // Payment system failed
               throw new ServletException( "Unrecognized return status.");
           }

        printwriter.println("\n");
        printwriter.println("\n");
        httpservletresponse.setContentType("text/html");

//        return hResponse;
// code for ORBITAL END

    }


    RequestIF AuthOnly(Hashtable hashtable) {
        // // throws ie, fnfe, e {
    	//Create a request object
        //The request object uses the XML templates along with data we provide
        //to generate a String representation of the xml request
        RequestIF request = null;
        lDebug(" after RequestIF initialization.");
        try {
            //Tell the request object which template to use (see RequestIF.java)
            request = new Request(RequestIF.NEW_ORDER_TRANSACTION);
            
            lDebug("\n after reuest NEWORDERTRANSCTION.");
            //If there were no errors preparing the template, we can now specify the data
            //Basic Auth Fields
            request.setFieldValue("IndustryType", "RC"); // RC - Recurring Payment
            lDebug("\n after setFieldValue IndustryType." );

            request.setFieldValue("MessageType", "A"); // A - Authorization request 
                                                         // AC - Authorization and Mark for capture
                                                         // FC - Force-Capture request
                                                         // R - Refund request

            lDebug("\n after setFieldValue MessageType." );
            // request.setFieldValue("MerchantID", "041756");
            request.setFieldValue("MerchantID", (String) hashtable.get("OapfStoreId")); // "041756");
            lDebug("\n after setFieldValue MerchantID." );
            request.setFieldValue("BIN", "000001");   // Assigned by Chase Paymentech
                                                      // 000001  Salem
                                                      // 000002  PNS
            lDebug("\n after setFieldValue BIN." );
            // request.setFieldValue("OrderID", "122003SA");
            request.setFieldValue("OrderID", (String) hashtable.get("OapfOrderId"));

            request.setFieldValue("TxRefNum", (String) hashtable.get("OapfTrxnRef"));

            // request.setFieldValue("AccountNum", "4055011111111111");
            request.setFieldValue("AccountNum", (String) hashtable.get("OapfPmtInstrID"));

            // request.setFieldValue("Amount", "100");
            String lAmount = (String) hashtable.get("OapfPrice");
            request.setFieldValue("Amount", lAmount.replace(".", ""));
            lDebug("\n after setFieldValue Amount." );

            // request.setFieldValue("Exp", "1209");
            String ExpDt = (String) hashtable.get("OapfPmtInstrExp");
            lDebug(" ExpDt: " + ExpDt );
            lDebug(" ExpDt.substring(3,5): " + (String) ExpDt.substring(3,5) );
            lDebug(" ExpDt.substring(0,2): " + (String) ExpDt.substring(0,2) );
            lDebug(" before setFieldValue Exp" );
            request.setFieldValue("Exp", (String) ExpDt.substring(3,5)+(String) ExpDt.substring(0,2));
            lDebug("\n after setFieldValue Exp." );

            // AVS Information
            // request.setFieldValue("AVSname", "Jon Smith");
            // request.setFieldValue("AVSaddress1", "4200 W Cypress St");
            // request.setFieldValue("AVScity", "Tampa");
            // request.setFieldValue("AVSstate", "FL");
            // request.setFieldValue("AVSzip", "11111");
            // Additional Information
            request.setFieldValue("Comments", (String) hashtable.get("OapfTransactionId"));
            lDebug("\n after setFieldValue Comments." );
            // request.setFieldValue("ShippingRef", "FEDEX WB12345678 Pri 1");
            //Uncomment the line below and modify to add a card security value (CVV2, CVC2 or CID)
            // request.setFieldValue("CardSecVal", "111");
            // request.setFieldValue("CardSecValInd", "1");

            //Display the request
            lDebug("\nAuth Request:\n" + request.getXML());
        } catch (InitializationException ie) {
            //       throw new ie(ie.getMessage());
            // lDebug("ipsProcess.orbital(HttpServletRequest,HttpServletResponse ) - Exception Error: Unable to initialize request object : " + ie.getMessage()); //, exception);
            // String s6 = iPaymentUtils.getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            // iPaymentUtils.displayError(httpservletresponse, ie.getMessage(), s6);
            lDebug("ipsServlet.AuthOnly(HttpServletRequest,HttpServletResponse ) - Exception Error: " + ie.getMessage(), ie);
            // String s6 = getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            // lHttpError(httpservletresponse, ie.getMessage(), s6);
        } catch (FieldNotFoundException fnfe) {
             //      throw new fnfe(fnfe.getMessage());
            // lDebug("ipsProcess.orbital(HttpServletRequest,HttpServletResponse ) - Exception Error: Unable to find XML field in template : " + fnfe.getMessage()); //, exception);
            // String s6 = iPaymentUtils.getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            // iPaymentUtils.displayError(httpservletresponse, fnfe.getMessage(), s6);
            lDebug("ipsServlet.AuthOnly.try(HttpServletRequest,HttpServletResponse ) - Exception Error: " + fnfe.getMessage(), fnfe);
            // String s6 = getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            // lHttpError(httpservletresponse, fnfe.getMessage(), s6);
        } catch (Exception e) {
             // throw new e(e.getMessage());
            // lDebug("ipsProcess.orbital(HttpServletRequest,HttpServletResponse ) - Exception Error: " + e.getMessage()); //, exception);
            // String s6 = iPaymentUtils.getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            // iPaymentUtils.displayError(httpservletresponse, e.getMessage(), s6);
            lDebug("ipsServlet.AuthOnly(HttpServletRequest,HttpServletResponse ) - Exception Error: " + e.getMessage(), e);
            // String s6 = getServerURL(httpservletrequest.getServerName(), Integer.toString(httpservletrequest.getServerPort()), "OA_HTML");
            // lHttpError(httpservletresponse, e.getMessage(), s6);
	}

       return request;
   }


    RequestIF AuthCapture(Hashtable hashtable) {
    	//Create a request object
        //The request object uses the XML templates along with data we provide
        //to generate a String representation of the xml request
        RequestIF request = null;
        lDebug(" after RequestIF initialization.");

        try {
            //Tell the request object which template to use (see RequestIF.java)
            request = new Request(RequestIF.MARK_FOR_CAPTURE_TRANSACTION);
            
            lDebug(" after reuest MARK_FOR_CAPTURE_TRANSACTION.");
            //If there were no errors preparing the template, we can now specify the data
            //Basic Auth Fields
            request.setFieldValue("MerchantID", "041756");
            request.setFieldValue("BIN", "000001");
            //Note: for TxRefNum (below), you must substitute a value that has been returned by
            //a successful authorization transaction
            request.setFieldValue("TxRefNum", (String) hashtable.get("OapfTrxnRef"));
            request.setFieldValue("OrderID", (String) hashtable.get("OapfOrderId"));
            // request.setFieldValue("Amount", "100");
            String lAmount = (String) hashtable.get("OapfPrice");
            request.setFieldValue("Amount", lAmount.replace(".", ""));
            lDebug(" after setFieldValue Amount." );

            // PC 2 Data
            // request.setFieldValue("TaxInd", "1");
            // request.setFieldValue("Tax", "100");
            // request.setFieldValue("PCOrderNum", "PO8347465");
            // request.setFieldValue("PCDestZip", "33607");
            // request.setFieldValue("PCDestName", "Terry");
            // request.setFieldValue("PCDestAddress1", "88301 Teak Street");
            // request.setFieldValue("PCDestAddress2", "Apt 5");
            // request.setFieldValue("PCDestCity", "Hudson");
            // request.setFieldValue("PCDestState", "FL");

            //Display the request
            System.out.println("\nCapture Request:\n" + request.getXML());
        } catch (InitializationException ie) {
            lDebug("ipsServlet.AuthCapture(HttpServletRequest,HttpServletResponse ) - Unable to initialize request object - Exception Error: " + ie.getMessage(), ie);
            // lDebug("Unable to initialize request object");
            // lDebug(ie.getMessage());
            // ie.printStackTrace();
            // System.exit(ERROR_EXIT);
        } catch (FieldNotFoundException fnfe) {
            lDebug("ipsServlet.AuthCapture(HttpServletRequest,HttpServletResponse ) - Unable to find XML field in template - Exception Error: " + fnfe.getMessage(), fnfe);
            // lDebug("Unable to find XML field in template");
            // lDebug(fnfe.getMessage());
            // fnfe.printStackTrace();
            // System.exit(ERROR_EXIT);
        } catch (Exception ex) {
            lDebug("ipsServlet.AuthCapture(HttpServletRequest,HttpServletResponse ) - Exception Error: " + ex.getMessage(), ex);
                ex.printStackTrace();
                // System.exit(ERROR_EXIT);
        }

       return request;
    }


}
