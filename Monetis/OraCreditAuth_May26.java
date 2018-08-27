/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.sql.*;
import java.io.*;
import java.sql.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import JavaAPI.*;
import java.lang.String;
import oracle.apps.iby.exception.Log;
import oracle.apps.iby.exception.*;
import oracle.apps.iby.ecapp.*;
import oracle.apps.iby.extend.TxnCustomizer;
import oracle.apps.iby.util.AddOnlyHashtable;
import oracle.apps.iby.util.ReadOnlyHashtable;
import oracle.apps.iby.exception.PSException;
import oracle.apps.jtf.aom.transaction.*;
import oracle.jdbc.driver.*;
import oracle.apps.iby.database.DBWrapper;



public class OraCreditAuth extends HttpServlet {

    void mylog(String e) {
    try {
      java.io.PrintWriter ps = new java.io.PrintWriter(new java.io.FileWriter("/usr/tmp/myibe.log", true));
      e = new java.util.Date() + " " + e;
      ps.println(e);
      ps.flush();
      ps.close();
    } catch (Exception e1) {
    }
  } 
/*
  public String  UpdateTxnNumber(String order_id, String txn_number) {
        String status = "";

    try {
      Connection conn=null;
      Class.forName("oracle.jdbc.driver.OracleDriver");
      conn=DriverManager.getConnection( "jdbc:oracle:thin:@pl1ua1191.corio.com:1523:TEKAAD1", "apps", "apps");
        mylog("UpdateTxnNumber = " + order_id );
        mylog("UpdateTxnNumber = " + txn_number );
      String str1="update iby_trxn_summaries_all a set trxnref = '" + txn_number + "' where a.tangibleid = '" + order_id + "' ";

      PreparedStatement ps1=null;

      ResultSet rs=null;

	ps1=conn.prepareStatement(str1);

        mylog("Update Txn Number String = " + str1);
        rs=ps1.executeQuery();
	if(rs.next())
        {
                 status= "true";
        mylog("Update Txn Number = " + status);
        }
      conn.commit();
      conn.close();
   } catch(Exception e){e.printStackTrace();}
   
   return status;
  }


  public boolean  UpdateTxnNumber2(String order_id) {

        boolean success = false;

    try {
        mylog("UpdateTxnNumber = " + order_id );
      Connection conn=null;
      Class.forName("oracle.jdbc.driver.OracleDriver");
      conn=DriverManager.getConnection( "jdbc:oracle:thin:@pl1ua1191.corio.com:1523:TEKAAD1", "apps", "apps");
        mylog("UpdateTxnNumber = " + order_id );
      String str1="update iby_trxn_summaries_all a set trxnref = '" + order_id + "' where a.tangibleid = '" + order_id + "' ";

      PreparedStatement ps1=null;

      ResultSet rs=null;

	ps1=conn.prepareStatement(str1);

        mylog("Update Txn Number 2 String = " + str1);
        int i =ps1.executeUpdate();
	if(i > 0)
        {
                 success= true;
        mylog("Update Txn Number 2 = " + success);
        }
      conn.close();
   } catch(Exception e){
      mylog("Exception  ");
   }
   
   return success;
  }
*/
  public String  getTxnNumber(String order_id, String txn_number, String trxnid) {
    try {
      Connection conn=null;
      Class.forName("oracle.jdbc.driver.OracleDriver");
      conn=DriverManager.getConnection( "jdbc:oracle:thin:@pl1ua119.corio.com:1523:TEKAAD1", "apps", "apps");

      String str1 = "";

         PreparedStatement ps1=null;

         ResultSet rs=null;




      if (!order_id.equals("") && !txn_number.equals(""))
      {
          str1="update iby_trxn_summaries_all a set trxnref = '" + txn_number + "' where a.tangibleid = '" + order_id + "'; commit; ";

           mylog("String 1 = " + str1);

	ps1=conn.prepareStatement(str1);

        int i =ps1.executeUpdate();
	if(i > 0)
        {
        mylog("Update Txn Number 2 = " + txn_number);
        }
       }

       if (!trxnid.equals(""))
       {
         str1="select trxnref from iby_trxn_summaries_all a where a.transactionid = " + trxnid + " and trxnref is not null";
          mylog("String 2 = " + str1);
	  ps1=conn.prepareStatement(str1);
          rs=ps1.executeQuery();
	  if(rs.next())
          {
                 txn_number= rs.getString("trxnref");
            mylog("In the if statement = " + txn_number);
          }
       }
      conn.close();
   } catch(Exception e){e.printStackTrace();}

   return txn_number;
 }
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String host = "esqa.moneris.com";

String ActionType=request.getParameter("OapfAction");
 String cust_id= request.getParameter("OapfCustName");
String crypt = "7";
        mylog("Action = " + ActionType);
        
  String order_id= request.getParameter("OapfOrderId");
        mylog("Order Id = " + order_id);
  String amount= request.getParameter("OapfPrice");
        mylog("Amount = " + amount);
  String trxnid= request.getParameter("OapfTransactionId");
        mylog("trxnid = " + trxnid);
/*

  String expiry_date= request.getParameter("OapfPmtInstrExp");
        mylog("expiry = " + expiry_date);
  String strExpDate[] = expiry_date.split("/");
  expiry_date = strExpDate[0] + strExpDate[1];
        mylog("expiry = " + expiry_date);
  Log.debug("Expiry date: "+ expiry_date);

     String authtype= request.getParameter("OapfAuthType");
     String cntry= request.getParameter("OapfCntry");
     String curr= request.getParameter("OapfCurr");
     String retry= request.getParameter("OapfRetry");
     String cvv= request.getParameter("OapfCVV2");
     String addr1= request.getParameter("OapfAddr1");
     String addr2= request.getParameter("OapfAddr2");
     String addr3= request.getParameter("OapfAddr3");
     String postal= request.getParameter("OapfPostalCode");
     String state= request.getParameter("OapfState");
     String city= request.getParameter("OapfCity");
*/
     String storeid= request.getParameter("OapfStoreId");
  String strArr[] = storeid.split(":");
  String Store_id=strArr[0];
  String apiToken=strArr[1];
        mylog("store id = " + Store_id);
        mylog("api Token = " + apiToken);

/*-----------------Code to authorise-----------------------------*/
if(ActionType.equals("oraauth"))
    {

  String pan= request.getParameter("OapfPmtInstrID");    
        mylog("Pan = " + pan);
  String expiry_date= request.getParameter("OapfPmtInstrExp");
        mylog("expiry = " + expiry_date);
  String strExpDate[] = expiry_date.split("/");
  expiry_date = strExpDate[0] + strExpDate[1];
        mylog("expiry = " + expiry_date);
  Log.debug("Expiry date: "+ expiry_date);

     String authtype= request.getParameter("OapfAuthType");
     String cntry= request.getParameter("OapfCntry");
     String curr= request.getParameter("OapfCurr");
     String retry= request.getParameter("OapfRetry");
     String cvv= request.getParameter("OapfCVV2");
     String addr1= request.getParameter("OapfAddr1");
     String addr2= request.getParameter("OapfAddr2");
     String addr3= request.getParameter("OapfAddr3");
     String postal= request.getParameter("OapfPostalCode");
     String state= request.getParameter("OapfState");
     String city= request.getParameter("OapfCity");

Log.debug("Monrise1 order_id = " + order_id);
Log.debug("Monrise2 amount = " + amount);
Log.debug("Monrise3 instrumentid= " + pan);
Log.debug("Monrise4 expiry_date= " + expiry_date);
Log.debug("Monrise5 storeid = " + storeid);
Log.debug("Monrise6 authtype= " + authtype);


Log.debug("Monrise HOST= " + host);

HttpsPostRequest mpgReq = new HttpsPostRequest(host,Store_id,apiToken, new PreAuth(order_id, amount, pan , expiry_date, crypt));

Log.debug("Monrise mpgReq = " + mpgReq );


      try {
             Receipt receipt = mpgReq.getReceipt();

               mylog("Txn Number 1 = " + receipt.getTxnNumber() ) ;

             String txn_number = receipt.getTxnNumber();

             String null_value = "";

                 txn_number= getTxnNumber(order_id, txn_number, trxnid);

            //    String txn_number= UpdateTxnNumber2(order_id);

               mylog("Txn Number 2 = " + txn_number ) ;

String OapfStatus = "0000"; // Hard coding to success
String OapfOrderId = order_id;
String OapfTrxnType = "2"; //AUTH ONLY
String OapfPmtInstrType = receipt.getCardType();
String OapfTrxnDate = getOracleDate();
String OapfAuthcode = receipt.getAuthCode();
String OapfRefcode = receipt.getReferenceNum();

response.setHeader("OapfStatus", OapfStatus);
response.setHeader("OapfAuthcode", OapfAuthcode);
response.setHeader("OapfTrxnDate", OapfTrxnDate);
response.setHeader("OapfTrxnType", OapfTrxnType);
response.setHeader("OapfRefcode", OapfRefcode);
response.setHeader("OapfOrderId", OapfOrderId);
response.setHeader("OapfPmtInstrType", OapfPmtInstrType);

      } 
      finally { 
            //out.close();
      }
   }

/*------------------- Authorization End ---------------*/

if(ActionType.equals("oracapture"))
    {
        mylog("UR In = " );
      Connection conn = null;

      PreparedStatement ps1=null;
      PreparedStatement ps2=null; 
      ResultSet rs1=null;
      ResultSet rs2=null;
      String str1="";
      String str2="";


      try
      {

        mylog("UR In");
        mylog("Txn Id = " + trxnid);

        String null_value = "";

        String price="";
        String quant="";
        String segment="";
        String desc="";
        String uomcode="";            


                String txn_number= getTxnNumber(null_value, null_value, trxnid);

        mylog("txn_number = " + txn_number);

        	HttpsPostRequest mpgReq1 = new HttpsPostRequest(host,Store_id,apiToken,new Completion(order_id, amount, txn_number, crypt));
		 try {
			 Receipt receipt1 = mpgReq1.getReceipt();

                         String OapfStatus = "0000"; // Hard coding to success
			String OapfOrderId = order_id;
			String OapfTrxnType = "8"; //CAPTURE ONLY
			String OapfPmtInstrType = receipt1.getCardType();
			String OapfTrxnDate = getOracleDate();
			String OapfAuthcode = receipt1.getAuthCode();
			String OapfRefcode = receipt1.getReferenceNum();
			
			String Message=receipt1.getMessage();  //added nithin
			String Txnno= receipt1.getTxnNumber();  //added by nithin
			String captured= receipt1.getComplete();  //added by nithin

        mylog("Order Id = " + order_id);
        mylog("Amount  = " + amount);
        mylog("Crypt = " + crypt);
        mylog("Pmt Instr Type  = " + OapfPmtInstrType);
        mylog("Auth Code = " + OapfAuthcode);
        mylog("Ref Number = " + OapfRefcode);
        mylog("Response Code = " + receipt1.getResponseCode());
        mylog("Vendor Msg = " + receipt1.getMessage());

response.setHeader("OapfStatus", OapfStatus);
response.setHeader("OapfAuthcode", OapfAuthcode);
response.setHeader("OapfTrxnDate", OapfTrxnDate);
response.setHeader("OapfTrxnType", OapfTrxnType);
response.setHeader("OapfRefcode", OapfRefcode);
response.setHeader("OapfOrderId", OapfOrderId);
               
           
                //if capture is success receipt1.getComplete() method returns true
           if(captured.compareTo("true")==0) 
           {

     		 str1= "select b.unit_standard_price amount, quantity_ordered, mtl.segment1 part_number,b.uom_code uom, mtl.description desc from oe_order_headers_all h , oe_order_lines_all l,ra_customer_trx_all a, ra_customer_trx_lines_all b, mtl_system_items_b mtl where  h.header_id = l.header_id and a.customer_trx_id = b.customer_trx_id and b.inventory_item_id = mtl.inventory_item_id and to_char(h.order_number) = a.interface_header_attribute1 and line_type='LINE' and h.order_number = 2000218 and organization_id = 107";
        
  		 str2="select count(*) cnt from oe_order_headers_all h , oe_order_lines_all l,ra_customer_trx_all a, ra_customer_trx_lines_all b, mtl_system_items_b mtl where  h.header_id = l.header_id and a.customer_trx_id = b.customer_trx_id and b.inventory_item_id = mtl.inventory_item_id and to_char(h.order_number) = a.interface_header_attribute1 and line_type='LINE' and h.order_number = 2000218 and organization_id = 107";  
 
        mylog("Str2 = " + str2);

            ps1=conn.prepareStatement(str1);
            ps2=conn.prepareStatement(str2);
            rs1=ps1.executeQuery();
            rs2=ps2.executeQuery();//returns count 


            int cnt=rs2.getInt("cnt");

        mylog("Cnt = " + cnt);
            int i=0;

     	      VSPurchl[] data3=new VSPurchl[cnt];

     	      while(rs1.next())
     	      {
     	     
        	price=rs1.getString("amount");	
     	  	quant=rs1.getString("quantity_ordered");
     	  	segment=rs1.getString("part_number");
     	  	uomcode=rs1.getString("uom");
     	  	desc=rs1.getString("desc");
     
        mylog("Price = " + price);
        mylog("quant = " + quant);

		/* data3[0]=new VSPurchl("CC09","DDD","CC01 descr","1","EA","1.01","0","0","0"); */

    		 data3[i]=new VSPurchl(segment,segment,desc,quant, uomcode,price,"0","0","0"); 
        
          	i++;     
         	} //while
           
 	   	VSPurcha data2= new VSPurcha("2.00","M8X 2W8","M1K 2Y7","ssd","frdessd");

         	VSPurchal data=new VSPurchal(order_id,txn_number,data2,data3);

                   
		L23HttpsPostRequest L23request=new L23HttpsPostRequest(host, storeid, apiToken, data);

			try
			{
				Receipt receipt = L23request.getReceipt();

				mylog("CardType = " + receipt.getCardType());
				mylog("AuthCode = " + receipt.getAuthCode());
				mylog("Complete = " + receipt.getComplete());
				mylog("Message = " + receipt.getMessage());
				mylog("MessageId = " + receipt.getMessageId());

			}//end try
			catch (Exception e)
			{
				e.printStackTrace();
			}//end catch
                      
           } // end of if


           }
	     catch (Exception ex1)
	     {}

      
     
	}
      catch(Exception exception)
      {
        mylog(" Exception " );
      }
      finally
      {
      }
    } // end of if Capture
}
   




    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

	  // Method getOracleDate returns today's date formatted as YYYYMMDDHHMISS
  private String getOracleDate() {
    try { 
      Log.debug("Entered:Moneris:getDate");
      java.util.Date d = new java.util.Date();
      Calendar cal = Calendar.getInstance();
      cal.setTime(d);
      String year = String.valueOf(cal.get(Calendar.YEAR));
      String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
      String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
      String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
      String minute = String.valueOf(cal.get(Calendar.MINUTE));
      String second = String.valueOf(cal.get(Calendar.SECOND));
      String formatDate = year + leftPad(month, "0", 2) + leftPad(day, "0", 2) + leftPad(hour, "0", 2) + leftPad(minute, "0", 2) + leftPad(second, "0", 2);
      Log.debug("Return String: " + formatDate);
      return formatDate;
    } // end try
    finally  {
      Log.debug("Exited:Moneris:getDate");
    } //end finally
  }// end method getOracleDate

  private String leftPad(String s, String pad, int n) {
    try {
     	Log.debug("Entered:Moneris:leftPad");
      if (s.length() >= n)
        return s;
      else
        return leftPad(pad + s, pad, n); 
    } // end try
    finally {   
      Log.debug("Exited:Moneris:leftPad"); 
    } //end finally
  } // end method leftPad

    /** 
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}


