package org.guiandrade;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Path;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;


@Path("resources")
public class TesteResource implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private String name = null;
	private Date date = null;
	private String content = null;
	private final String mandatoryImport = "org.onepf.oms.OpenIabHelper";
	private final String billingImport = "com.android.vending.billing.IInAppBillingService";

	public boolean checkContent(String text){
		// Function that makes simple initial verifications.
		JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, text);
		String exampleText ="Paste your code here!";
		if (text.equals("") || text.equals(exampleText) || !javaClass.hasImport(billingImport)){
			return false;
		}
		else{
			return true;
		}
	}

	public String changeIAB(String text){
		// Function that will do the translation

		JavaClassSource javaClass = Roaster.parse(JavaClassSource.class, text);
		javaClass.addField("private OpenIabHelper mHelper;");
		String mandatoryCreation = " new OpenIabHelper.Options.Builder();";
		String setStoreSearchStrategy = "builder.setStoreSearchStrategy(OpenIabHelper.Options.SEARCH_STRATEGY_INSTALLER);\n\t";
		String setVerifyMode = "builder.setVerifyMode(OpenIabHelper.Options.VERIFY_ONLY_KNOWN);\n";
		String helperAssign = "mHelper = new OpenIabHelper(this, options.build());\n";
		String checkServiceConnected = checkServiceConnected(javaClass); 
		String setupIab= "  mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {\r\n      public void onIabSetupFinished(IabResult result) {\r\n          if (!result.isSuccess()) {\r\n              complain(\"Problem setting up in-app billing: \" + result); \r\n            return;\r\n          }\r\n              mHelper.queryInventoryAsync(mGotInventoryListener);\r\n                          }\r\n  });";
		String options = "\n OpenIabHelper.Options options ="
				+ mandatoryCreation
				+ setStoreSearchStrategy
				+ setVerifyMode
				+ helperAssign
				+setupIab;


		addIabImports(javaClass);

		return changeToIab(javaClass,options);

	}

	public String checkServiceConnected(JavaClassSource javaClass) {
		String method = "onServiceConnected";
		String param = "ComponentName";
		
		if (javaClass.getMethod(method,param) != null){
			return javaClass.getMethod(method,param).getBody();
		}
		else{
			return "cenas";
		}
	}
	
	public String checkServiceDisconnected(JavaClassSource javaClass) {
		// TODO Auto-generated method stub
		return null;
	}

	private String changeToIab(JavaClassSource javaClass,String newConstructor) {
		// Change constructor and onIabSetupFinished(labResult result) and verify Intent
		String method= "onIabSetupFinished";
		String intent = "Intent(\"com.android.vending.billing.InAppBillingService.BIND\")" ;
		String methodBody="if (result.isSuccess()) { \n\t onServiceConnected(); \n } \n else{ \n\t onServiceDisconnected(); \n }";
		List<MethodSource<JavaClassSource>> methods = javaClass.getMethods();
		int constructorSuccess=0;
		int intentSuccess=0;
		int methodSuccess=0;

		for (@SuppressWarnings("rawtypes") MethodSource m : methods){
			if (m.isConstructor()){
				String newBody = newConstructor.concat(m.getBody());
				m.setBody(newBody);
				constructorSuccess=1;
				if (intentSuccess==1 && methodSuccess==1){break;}	
			}
			if (m.getBody().contains(intent)){
				// Talvez necessario parsear o body para verificar que BindService usa este intent.
				intentSuccess=1;
				if (constructorSuccess==1 && methodSuccess==1){break;}	
			}
			if (m.getName().equals(method)){
				// Metodo esta dentro do startSetup...e necessario uma alternativa
				m.setBody(methodBody);
				methodSuccess=1;
				if (constructorSuccess==1 && intentSuccess==1){break;}
			}

		}
		if (methodSuccess==0){
			changeMethod(javaClass,methodBody);
		}
		if ( constructorSuccess==0 || intentSuccess==0 ){
			return "Error.";
		}
		else{
			return changeToString(javaClass);
		}
	}

	public void changeMethod(JavaClassSource javaClass,String methodBeginning) {
		String method= "startSetup";
		String param="OnIabSetupFinishedListener";
		//Metodos service connection and disconnected
		//String methodBody=methodBeginning+"        logDebug(\"Starting in-app billing setup.\");\r\n        mServiceConn = new ServiceConnection() {\r\n            @Override\r\n            public void onServiceDisconnected(ComponentName name) {\r\n                logDebug(\"Billing service disconnected.\");\r\n                mService = null;\r\n            }\r\n\r\n            @Override\r\n            public void onServiceConnected(ComponentName name, IBinder service) {\r\n                if (mDisposed) return;\r\n                logDebug(\"Billing service connected.\");\r\n                mService = IInAppBillingService.Stub.asInterface(service);\r\n                String packageName = mContext.getPackageName();\r\n                try {\r\n                    logDebug(\"Checking for in-app billing 3 support.\");\r\n\r\n                    int response = mService.isBillingSupported(3, packageName, ITEM_TYPE_INAPP);\r\n                    if (response != BILLING_RESPONSE_RESULT_OK) {\r\n                        if (listener != null) listener.onIabSetupFinished(new IabResult(response,\r\n                                \"Error checking for billing v3 support.\"));\r\n\r\n                        // if in-app purchases aren't supported, neither are subscriptions\r\n                        mSubscriptionsSupported = false;\r\n                        mSubscriptionUpdateSupported = false;\r\n                        return;\r\n                    } else {\r\n                        logDebug(\"In-app billing version 3 supported for \" + packageName);\r\n                    }\r\n\r\n\r\n                    response = mService.isBillingSupported(5, packageName, ITEM_TYPE_SUBS);\r\n                    if (response == BILLING_RESPONSE_RESULT_OK) {\r\n                        logDebug(\"Subscription re-signup AVAILABLE.\");\r\n                        mSubscriptionUpdateSupported = true;\r\n                    } else {\r\n                        logDebug(\"Subscription re-signup not available.\");\r\n                        mSubscriptionUpdateSupported = false;\r\n                    }\r\n\r\n                    if (mSubscriptionUpdateSupported) {\r\n                        mSubscriptionsSupported = true;\r\n                    } else {\r\n                        // check for v3 subscriptions support\r\n                        response = mService.isBillingSupported(3, packageName, ITEM_TYPE_SUBS);\r\n                        if (response == BILLING_RESPONSE_RESULT_OK) {\r\n                            logDebug(\"Subscriptions AVAILABLE.\");\r\n                            mSubscriptionsSupported = true;\r\n                        } else {\r\n                            logDebug(\"Subscriptions NOT AVAILABLE. Response: \" + response);\r\n                            mSubscriptionsSupported = false;\r\n                            mSubscriptionUpdateSupported = false;\r\n                        }\r\n                    }\r\n\r\n                    mSetupDone = true;\r\n                }\r\n                catch (RemoteException e) {\r\n                    if (listener != null) {\r\n                        listener.onIabSetupFinished(new IabResult(IABHELPER_REMOTE_EXCEPTION,\r\n                                \"RemoteException while setting up in-app billing.\"));\r\n                    }\r\n                    e.printStackTrace();\r\n                    return;\r\n                }\r\n\r\n                if (listener != null) {\r\n                    listener.onIabSetupFinished(new IabResult(BILLING_RESPONSE_RESULT_OK, \"Setup successful.\"));\r\n                }\r\n            }\r\n        };\r\n\r\n        Intent serviceIntent = new Intent(\"com.android.vending.billing.InAppBillingService.BIND\");\r\n        serviceIntent.setPackage(\"com.android.vending\");\r\n        List<ResolveInfo> intentServices = mContext.getPackageManager().queryIntentServices(serviceIntent, 0);\r\n        if (intentServices != null && !intentServices.isEmpty()) {\r\n            mContext.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);\r\n        }\r\n        else {\r\n            if (listener != null) {\r\n                listener.onIabSetupFinished(\r\n                        new IabResult(BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE,\r\n                                \"Billing service unavailable on device.\"));\r\n            }\r\n        }\r\n    }";

		@SuppressWarnings("rawtypes")
		MethodSource setup = javaClass.getMethod(method,param);
		javaClass.removeMethod(setup);

	}

	public String addIabImports(JavaClassSource javaClass){
		//Add mandatory imports to use OpenIAB
		String importHelper= "teste.example.android.trivialdrivesample.util.IabHelper";
		String importResult= "teste.example.android.trivialdrivesample.util.IabResult";
		String importInventory= "teste.example.android.trivialdrivesample.util.Inventory";
		String importPurchase= "teste.example.android.trivialdrivesample.util.Purchase";
		String importIabHelper= "org.onepf.oms.appstore.googleUtils.IabHelper";
		String importIabResult= "org.onepf.oms.appstore.googleUtils.IabResult";
		String importIabInventory= "org.onepf.oms.appstore.googleUtils.Inventory";
		String importIabPurchase= "org.onepf.oms.appstore.googleUtils.Purchase";
		String[] oldImports ={importHelper,importResult,importInventory, importPurchase};
		String[] newImports = {importIabHelper,importIabResult,importIabInventory,importIabPurchase};
		int i=0;

		javaClass.addImport(mandatoryImport);

		while (i < newImports.length){

			if(!javaClass.hasImport(newImports[i])){
				if(javaClass.hasImport(oldImports[i])){
					javaClass.removeImport(oldImports[i]);
				}
				javaClass.addImport(newImports[i]);
			}
			i++;
		}

		return changeToString(javaClass);
	}

	public String changeToString(JavaClassSource javaClass){
		String unformattedText = javaClass.toUnformattedString();
		String formattedText = Roaster.format(unformattedText);
		return formattedText;
	}

	public Date getDate() {
		return date;
	}



	public void setDate(Date date) {
		this.date = date;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



}
