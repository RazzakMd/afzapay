package oracle.apps.xxcfi.iby;

import java.io.IOException;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Serializable;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import oracle.apps.fnd.common.VersionInfo;
import oracle.apps.iby.database.DBWrapper;
import oracle.apps.iby.exception.PSException;
import oracle.jbo.server.OAJboDBTransactionImpl;
import oracle.apps.iby.exception.Log;
import oracle.apps.fnd.framework.OAApplicationModuleFactory;
import oracle.apps.fnd.framework.OAApplicationModule;
import oracle.apps.fnd.sso.Utils;
import oracle.apps.fnd.common.WebAppsContext;
import oracle.apps.fnd.common.AppsContext;
import oracle.apps.fnd.common.WebRequestUtil;
import java.io.PrintWriter;
import oracle.apps.iby.ecservlet.ECServletResponse;
import oracle.apps.fnd.framework.server.OAViewObjectImpl;

import oracle.apps.fnd.framework.*;
import oracle.apps.fnd.framework.server.*;
import oracle.apps.iby.security.SecurityUtil;


public class InitIPaymentServlet extends HttpServlet
{

    /** iPayment Debug 
     * Module name for all debugging statements. */
    public static final String DEBUG_MODULE=Log.MODULE_PREFIX+"InitIPaymentServlet";

    public void init(ServletConfig servletconfig)
        throws ServletException
    {
        init();
    }

    public void init()
        throws ServletException
    {
   
        Log.debug("XXCFIPaymentServlet: init()");
        Log.debug("XXCFIPaymentServlet: exiting init()");

        /* iPayment Debug */
        String DBG_MOD=InitIPaymentServlet.DEBUG_MODULE+".init";
        Log.debug("enter",Log.PROCEDURE,DBG_MOD);
        Log.debug("exit",Log.PROCEDURE,DBG_MOD);
    }

     public void doGet(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
	        throws ServletException, IOException
	 {
		doPost(httpservletrequest,httpservletresponse);
	 }


    public void doPost(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
        throws ServletException, IOException
    {        
             /* iPayment Debug */
            String DBG_MOD=InitIPaymentServlet.DEBUG_MODULE+".doPost";


      	Connection connection=null;
  		  boolean sessionFlag=false;
  		  boolean addPayeeFlag=false;
  		  Log.debug("============ XXCFIPaymentServlet ================");

               /* iPayment Debug */
               Log.debug("============ XXCFIPaymentServlet ================",
                Log.PROCEDURE,DBG_MOD);


		  String newKey=(String)httpservletrequest.getParameter("newKey");
		  String oldKey=(String)httpservletrequest.getParameter("oldKey");
		  String sessionKey=(String)httpservletrequest.getParameter("sessionKey");
		  String addPayee=(String)httpservletrequest.getParameter("addPayee");

		  if (addPayee != null && addPayee.equals("Y")){
			  addPayeeFlag=true;
		  }
		  if (sessionKey != null && sessionKey.equals("Y")){
			   sessionFlag=true;
		  }
		  Log.debug("============ After reterive the key ================");

              /* iPayment Debug */
               Log.debug("============ After reterive the key ================",
                Log.PROCEDURE,DBG_MOD);

		  WebAppsContext webappscontext = null;
		  AppsContext appsContext = null;
		  OAJboDBTransactionImpl oaJboDBTransactionImpl = null;
		  OAApplicationModule   oaapplicationmodule = null;
		  OAApplicationModule   oaapplicationmodule1 = null;
		  boolean flag = true;
		 try{
			webappscontext =WebRequestUtil.validateContext(httpservletrequest,httpservletresponse);
			boolean sessionF=webappscontext.createAnonymousSession();
			if (sessionF)
			{
			  System.out.println("Session Id created for guest user is "+webappscontext.getCurrentSessionId());
			  System.out.println("Guest session is created successfully");

                   /* iPayment Debug */
                   Log.debug("Session Id created for guest user is"+webappscontext.getCurrentSessionId(),Log.PROCEDURE,DBG_MOD);
			
			 Log.debug("Guest session is created successfully",Log.PROCEDURE,DBG_MOD);

			


			}
		   //webappscontext.createAnonymousSession();
			int l1 = webappscontext.getCurrentSessionId();
			connection = webappscontext.getJDBCConnection();
			appsContext = (AppsContext)Utils.getAppsContext();
			String s="oracle.apps.iby.admin.security.server.SecuritySetupAM";
			String s1="oracle.apps.xxcfi.iby.XxcfiSessionSecurityAM";
			Properties properties = new Properties();
			properties.put("APPS_CONTEXT", appsContext);

        	oaapplicationmodule1 = (OAApplicationModule)OAApplicationModuleFactory.createRootOAApplicationModule(properties, s);

        	oaapplicationmodule = (OAApplicationModule)OAApplicationModuleFactory.createRootOAApplicationModule(properties, s1);

			System.out.println("Avg for  iterations");
                  
                  /* iPayment Debug */
			Log.debug("Avg for  iterations",Log.PROCEDURE,DBG_MOD);



		if (!sessionFlag && !addPayeeFlag){
			if(oaapplicationmodule == null || oaapplicationmodule1 == null)
			{
				Log.debug("============ SecuritySetupAM application module cannot be created We cannot proceed further ================");
                       
                        /* iPayment Debug */
                         Log.debug("============ SecuritySetupAM application module cannot be created We cannot proceed further ================",
                         Log.PROCEDURE,DBG_MOD);


			}
			if (oldKey == null || oldKey.equals("")){
					Serializable createKey[] ={
								null,newKey
					};
					oaapplicationmodule1.invokeMethod("createKey",createKey);
				}
				else{
					Serializable changeKey[] ={
						null,oldKey,newKey
					};
					oaapplicationmodule1.invokeMethod("changeKey",changeKey);
				}

				System.out.println("============ After Submission of Create key  or change key  ================");
				 
				/* iPayment Debug */
                         Log.debug("============ After Submission of Create key  or change key  ================", Log.PROCEDURE,DBG_MOD);


				System.out.println("============ Before calling payee ids ================");
                        
				 /* iPayment Debug */
                         Log.debug("============ Before calling payee ids ================",Log.PROCEDURE,DBG_MOD);

				setPayeeIdMasterKey(oaapplicationmodule1,connection,newKey,oldKey,addPayee);
				System.out.println("============ After calling payee ids ================");

                        /* iPayment Debug */
                         Log.debug("============ After calling payee ids ================",
                         Log.PROCEDURE,DBG_MOD);

				connection.commit();
			}
		if (addPayeeFlag)
		{
			System.out.println("before invoke of all payee methods for  iterations");


			/* iPayment Debug */
                         Log.debug("before invoke of all payee methods for  iterations",
                         Log.PROCEDURE,DBG_MOD);

			setPayeeIdMasterKey(oaapplicationmodule1,connection,newKey,oldKey,addPayee);
			System.out.println("after invoke of all payee methods for  iterations");
	
			/* iPayment Debug */
                         Log.debug("after invoke of all payee methods for  iterations",
                         Log.PROCEDURE,DBG_MOD);

			connection.commit();
			System.out.println("After connection commit for new payee flag");
		}
		if (sessionFlag)
		{
			System.out.println("before invoke of all methods for  iterations");
			

			/* iPayment Debug */
                         Log.debug("before invoke of all methods for  iterations",
                         Log.PROCEDURE,DBG_MOD);

			Serializable storeKey[] ={
				newKey
			};
			oaapplicationmodule.invokeMethod("setRegInstrEnterKey",storeKey);
			System.out.println("after invoke of setRegInstrEnterKey method");

                  /* iPayment Debug */
                         Log.debug("after invoke of setRegInstrEnterKey method",
                         Log.PROCEDURE,DBG_MOD);

			setPayeeIdKeys(oaapplicationmodule,connection,newKey);
			System.out.println("after invoke of setPayeeIdKeys method");

			/* iPayment Debug */
                         Log.debug("after invoke of setPayeeIdKeys method",
                         Log.PROCEDURE,DBG_MOD);

		}


		}catch(SQLException sqlexception)
		 {
		  sqlexception.printStackTrace();
		  int s4 = sqlexception.getErrorCode();
		  String s8 = sqlexception.getMessage();
		  s8 = s8.replace('\n', ' ');
		  String s2 = new Integer(s4).toString();
		  String s41 = new String("3");
		  Log.debug("sqlexception: " + s8);
		  Log.debug("Cause: " + s8);
		  ECServletResponse ecservletresponse1 = new ECServletResponse(httpservletresponse);
		  Hashtable hashtableSQL = ecservletresponse1.setError(s41, s2, s8, flag);
		  ecservletresponse1.setHeaders(hashtableSQL);
		  ecservletresponse1.setBody(hashtableSQL);

	     }
         catch(PSException psexception)
	     {
		  psexception.printStackTrace();
		  String s51 = psexception.getCode();
		  String s81 = psexception.getCause();
		  s81 = s81.replace('\n', ' ');
		  String s21 = new String("3");
		  Log.debug("PSException: " + s51);
		  Log.debug("Cause: " + s81);
		  ECServletResponse ecservletresponse1 = new ECServletResponse(httpservletresponse);
		  Hashtable hashtablePS = ecservletresponse1.setError(s21, s51, s81, flag);
		  ecservletresponse1.setHeaders(hashtablePS);
		  ecservletresponse1.setBody(hashtablePS);

	     }
        catch(Exception exception)
        {
		  exception.printStackTrace();
		  String s3 = new String("3");
		  String s6 = new String("");
		  String s9 = (new PSException("IBY_204604")).getMessage();
		  Log.debug(exception);
		  ECServletResponse ecservletresponse2 = new ECServletResponse(httpservletresponse);
		  Hashtable hashtableEx = ecservletresponse2.setError(s3, s6, s9, flag);
		  ecservletresponse2.setHeaders(hashtableEx);
		  ecservletresponse2.setBody(hashtableEx);

        }

	   finally
	   {
		 try{
		 	if (connection != null){
		   		webappscontext.releaseJDBCConnection();
			}
		 }catch (Exception exp){};
	   }

    }


	private void setPayeeIdMasterKey(OAApplicationModule oaapplicationmodule,Connection connection,String newKey, String oldKey,String addPayee)
	throws PSException,SQLException,Exception

	{

	    /* iPayment Debug */
	    String DBG_MOD=InitIPaymentServlet.DEBUG_MODULE+".setPayeeIdMasterKey";

	    java.sql.CallableStatement callablestatement =null;
	    if (addPayee != null && addPayee.equals("Y"))
	    {
			callablestatement = connection.prepareCall("select MPAYEEID from iby_payee where activestatus = 'Y' and MASTER_KEY is NULL");
		}
		else{
			callablestatement = connection.prepareCall("select MPAYEEID from iby_payee where activestatus = 'Y'");
		}
 		ResultSet resultset = callablestatement.executeQuery();
 		System.out.println("After executing setPayeeIdMasterKey Method query");

            /* iPayment Debug */
                         Log.debug("After executing setPayeeIdMasterKey Method query",
                         Log.PROCEDURE,DBG_MOD);
		while (resultset.next())
            {
                String s = resultset.getString(1);
                System.out.println("After fetch value of mpayeeid from iby_payee "+s);

                 /* iPayment Debug */
                         Log.debug("After fetch value of mpayeeid from iby_payee"+s,
                         Log.PROCEDURE,DBG_MOD);

                if (oldKey == null || oldKey.equals("")){
					Serializable createKey[] ={
						s,newKey
					};
		   			oaapplicationmodule.invokeMethod("createKey",createKey);
                }
				else{
					Serializable changeKey[] ={
							s,oldKey,newKey
					};
					oaapplicationmodule.invokeMethod("changeKey",changeKey);
				}

            }
             resultset.close();
             callablestatement.close();
	}

	private void setPayeeIdKeys(OAApplicationModule oaapplicationmodule,Connection connection,String newKey)
		throws PSException,SQLException,Exception

	{
 		   /* iPayment Debug */
       	    String DBG_MOD=InitIPaymentServlet.DEBUG_MODULE+".setPayeeIdMasterKey";


		    java.sql.CallableStatement callablestatement =null;
			System.out.println("First line invoke of setPayeeIdKeys method");
                
                /* iPayment Debug */
                         Log.debug("First line invoke of setPayeeIdKeys method",
                         Log.PROCEDURE,DBG_MOD);


			callablestatement = connection.prepareCall("select payeeid from iby_payee where activestatus = 'Y'");
	 		ResultSet resultset = callablestatement.executeQuery();
			while (resultset.next())
			{
				String s = resultset.getString(1);
				Serializable storeKey[] ={
						s,newKey
				};
				oaapplicationmodule.invokeMethod("setRegPayeeEnterKey",storeKey);
			 }
	         resultset.close();
	         callablestatement.close();
	         System.out.println("Last line invoke of setPayeeIdKeys method");

                /* iPayment Debug */
                         Log.debug("Last line invoke of setPayeeIdKeys method",
                         Log.PROCEDURE,DBG_MOD);


		   
	}


    public InitIPaymentServlet()
    {
    }

    public static final String RCS_ID = "$Header: InitIPaymentServlet.java 115.58 2003/06/20 19:04:29 nmukerje ship $";
    public static final boolean RCS_ID_RECORDED = VersionInfo.recordClassVersion("$Header: InitIPaymentServlet.java 1.0  $", "oracle.apps.xxcfi.iby");

}