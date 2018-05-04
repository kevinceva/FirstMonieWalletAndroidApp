package com.ceva.ubmobile.core.constants;

import android.os.Environment;
import android.text.Html;

import java.io.File;

/**
 * Created by brian on 26/09/2016.
 */
public class Constants {
    //https://196.6.205.67/ubmobileapi/index.html
    public static final String NET_URL = "https://unionmobile.unionbankng.com";//prod url
    //public static final String NET_URL = "http://196.6.204.54:8089";//uat
    //public static final String NET_URL = "http://196.6.204.47";//preprod
    //public static final String NET_URL = "https://196.6.205.67";
    public static final String UAT_HOST = "196.6.204.54:8089";
    public static final String HOSTNAME = "unionmobile.unionbankng.com";
    //public static final String HOSTNAME = "digital.unionbankng.com";
    //public static final String NET_PATH = "/ubmobileapisec/rest/ubws/";//uat without sec
    //public static final String NET_PATH = "/ubcustomermobileapi/rest/ubws/";//uat without sec original - ls working
    //public static final String NET_PATH = "/ubmobileapisec/rest/ubcustomerws/";
    public static final String NET_PATH = "/ubcustomermobileapi/rest/ubcustomerws/";//prod with sec
    public static final String GEO_CONTEXT = "/locatormobapi/resources/ubws/"; //prod locator
    //public static final String GEO_CONTEXT = "/locatormobapi/rest/ubws/";//uat locator
    public static final String IMAGE_DOWNLOAD_PATH = "/ubcustomermobileapi/images/";
    public static final String KEY_CODE = "respcode";
    public static final String KEY_MSG = "respdesc";
    public static final String KEY_BANKRESP_CODE = "responseCode";
    public static final String KEY_BANK_MSG = "responseMessage";
    public static final String KEY_FRAG_POSITION = "position";
    public static final String RANDOM_STR = "AeH6GrLRGK2SBtNiziAdl+Z9HK+98qChhGuCaLZ7O5M";
    public static final String PRIVATE_KEY = "mYCvSane74ZV2rS5BXiWi0beWxFQ2037I00wLipnFhU=";
    public static final String NBK_URL = "https://196.32.226.78:8010";
    public static final String NBK_ENDPOINT = "/natmobileapi/rest/appws/";
    public static final String KIFUNGUO_CHA_BARABARA = "/9229874682736729";
    public static final String APP_ID = "155155155";
    public static final String APP_OUTSIDEID = "255255255";
    public static final String KEY_USER_ID = "userid";
    //Bank Account Constants
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_ACCOUNTNAME = "accountname";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PHONENUMBER = "mobilenumber";
    public static final String KEY_EMAIL = "customeremail";
    public static final String KEY_ACCOUNTCURRENCY = "accountcurrency";
    public static final String KEY_ACCOUNTNUMBER = "accountnumber";
    public static final String KEY_TRANSFERLIMIT = "transferlimit";
    public static final String KEY_ACCOUNTSTATUS = "accountstatus";
    public static final String KEY_ACCOUNTPRODUCT = "accountproduct";
    public static final String KEY_CUSTINFO = "custAcctInfo";
    public static final String KEY_FULLINFO = "balInfo";
    public static final String KEY_CLEAREDBALANCE = "availabletowithdraw";
    public static final String PREFS_NAME = "UBN_PREFS";
    public static final String AZURE_MSG = "message";
    public static final String AZURE_TITLE = "title";
    public static final String AZURE_LINK = "link";
    public static final String KEY_TX_XYBALANCE = "xybalance";
    public static final String KEY_TX_NARRATION = "desc";
    public static final String KEY_TX_AMOUNT = "withdraw";
    public static final String KEY_TX_DATE = "tdate";
    public static final String KEY_TX_DRCR = "drcr";
    public static final String KEY_NAIRA = Html.fromHtml("&#8358;").toString();
    //Beneficiaries section public static final String KEY_BEN_STATUS = "";
    public static final String KEY_BEN_STATUS = "beneficiaryStatus";
    public static final String KEY_BEN_TXTYPE = "transactionType";
    public static final String KEY_BEN_ACCOUNTNAME = "accountName";
    public static final String KEY_BEN_ACCOUNT = "beneficiaryAccount";
    public static final String KEY_BEN_BANKNAME = "destinationBankName";
    public static final String KEY_BEN_NICKNAME = "nickName";
    public static final String KEY_BEN_USERNAME = "userName";
    public static final String KEY_BEN_DATEADDED = "dateAdded";
    public static final String KEY_BEN_BREF = "bref";
    public static final String KEY_BEN_BANKCODE = "destinationBank";
    public static final String KEY_BEN_ID = "beneficiaryId";

    //other constants
    public static final String KEY_PRODUCT_BUNDLE = "productsbundle";
    public static final String KEY_PRODUCT_NAMES = "productNames";
    public static final String KEY_PRODUCT_CODES = "productCodes";
    public static final String KEY_ACTIVITY_NAME = "activityName";
    public static final String KEY_EXTACCOUNTNUMBER = "accountNumber";
    public static final String KEY_REFERENCE_ID = "refid";
    public static final String KEY_CUSTOMER_ID = "custid";
    public static final String KEY_DoB = "dateOfBirth";

    //Transaction Controller KEYS
    public static final int KEY_TX_BEN = 0;
    public static final int KEY_TX_ADDBEN = 1;
    public static final String KEY_CONTROL_POS = "beneficiary";
    //Transaction Controller Titles
    public static final String KEY_CONTROL_TITLE = "title";
    //Endpoints section
    public static final String KEY_LOGIN_ENDPOINT = "userauthentication";
    public static final String KEY_MINISTATEMENT_ENDPOINT = "ministatement";
    public static final String KEY_FUNDTRANSFER_ENDPOINT = "fundtransfer";
    public static final String KEY_FETCHBENEFICIARIES_ENDPOINT = "fetchallbeneficiariesbyuser";
    public static final String KEY_SAVEBENEFICIARY_ENDPOINT = "savebeneficiariesdata";
    public static final String KEY_FETCHBANKSORBRANCHES_ENDPOINT = "loadbanksOrbranches";
    public static final String KEY_FETCHACCOUNTINFO_ENDPOINT = "fetchAccountInfo";
    public static final String KEY_BALINQUIRY_ENDPOINT = "balanceEnquiry";
    public static final String KEY_NIPENQUIRY_ENDPOINT = "nipenquiry";
    public static final String KEY_CHEAQUEBOOKREQUEST_ENDPOINT = "chequebookrequest";
    public static final String KEY_BANKDRAFTREQUEST_ENDPOINT = "draftrequest";
    public static final String KEY_ACCOUNT_OPEN_ENDPOINT = "extaccountopen";
    public static final String HARDCODED_PHONE = "254710584935";

    public static final String KIFUNGUO_CHA_WENYEPESA = "5427114";
    public static final String KEY_FUNDTRANS_WITHIN = "CUSTFUNDTRANSF";
    public static final String KEY_FUNDTRANS_OTHER = "FUND_TRNS_OTCR";
    public static final String KEY_FUNDTRANS_PAYBILL = "CUSTPAYBILL";
    public static final String KEY_AUTH_SMS = "SMS";
    public static final String KEY_AUTH_TOKEN = "TOKEN";
    public static final String KEY_AUTH_NO_AUTH = "NO-AUTH";
    public static final String KEY_HARDCODE_BRANCH = "682";
    public static final String KEY_WALLET = "wallet";
    public static final String KEY_IMAGE_PATH = Environment.getExternalStorageDirectory() + File.separator + "UBN" + File.separator;
}
