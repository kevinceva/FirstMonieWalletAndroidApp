package com.ceva.ubmobile.security;


public interface SecurityConstants {

    String YEAR_FRMT = "MM/dd/yyyy H:mm:ss a";

    String CELLULANT_YEAR_FRMT = "yyyy-MM-dd H:mm:ss";

    String YEAR = "yyyy";

    String GEN_MALE = "M";

    String VAL_00 = "00";

    String VAL_01 = "01";

    String VAL_02 = "02";

    String VAL_03 = "03";

    String VAL_DOT_00 = ".00";

    String VAL_DOT_01 = ".01";

    String VAL_DOT_02 = ".02";

    String VAL_DOT_03 = ".03";

    String VAL_001 = "001";

    String VAL_002 = "002";

    String VAL_003 = "003";

    String ACC_OPEN_PEN_FLAG = "FP";

    String NAT3_ACC_OPEN_PEN_FLAG = "NP";

    String ACC_OPEN_ID_FRMT = "yyddMMHmmssSSS";

    String ACC_OPEN_CHANNEL_ID_MOBILE = "MOBBK";

    String CONFIG_BUNDLE = "config";
    String NARR_PATTERN_COMP = "\\$(.*?)\\$";
    String NARR_DOLL_SYM = "\\$";
    String BAL_INQ_REG_SYM = "(?<=\\G.{12})";
    String MINI_STMT_REG_SYM = "(?<=\\G.{61})";
    String MINI_SUCCESS_ZERO = "000000000000";
    String MINI_AVAIL_BALANCE = "AVAIL BALANCE";
    String BALANCE_FORMAT = "###,###.###";

    String CONFIG_BUNDLE_ERROR = "resources/errors";
    String RESOURCE = "/resources/";
    String FUNDS_TRAN_DET = "transactiondetails.xml";
    String NHIF_TRAN_DET = "nhiftransactiondetails.xml";
    String FIMI_TRAN_CODES = "fimi_tran_codes";
    String ISO_RESPONSE_CODES = "iso_response_codes";

    // The below is for switch constant codes.

    char CHAR_VAL_0 = '0';
    String VAL_0 = "0";
    String VAL_139 = "139";
    int INT_VAL_0 = 0;
    int INT_VAL_1 = 1;
    String GMT = "GMT";
    String YES = "Y";
    String NHIF_IDEN_CRD = "IC";
    String NO = "N";

    String TRAN_LOG_ID = "tranlogid";
    String IMAL_TRAN_LOG_ID = "imaltranlogid";
    String BFUB_TRAN_LOG_ID = "bfubtranlogid";
    String TXN_ID = "txnid";
    String MOBILE_NO = "mobileno";
    String CURRENCY = "currency";
    String TO_MOBILE_NO = "tomobileno";
    String TXN_STAN = "txnstan";
    String TXN_RRN = "txnrrn";
    String REV_TXN_STAN = "revtxnstan";
    String REV_TXN_RRN = "revtxnrrn";
    String TXN_AMT = "txnamt";
    String NARRATION = "narration";
    String INT_TRN_CHK_CODE = "intertranscheckcode";
    String INSTITUTE = "institute";
    String ACCOUNT = "account";
    String INPUT = "input";
    String STATUS = "status";
    String CARD_NUMBER = "cardnumber";
    String NHIF_COMMON_NO = "commonno";
    String SESS_ID = "sessionid";
    String SUCESS_SMALL = "Success";
    String SUCESS_CAPS = "SUCCESS";
    String XML = "xml";
    String ISO_REQ_XML = "isoreqxml";
    String ISO_RES_XML = "isoresxml";
    String NHIF_RESP_STR = "nhifresp";
    String NHIF_RESP_CODE = "respcode";
    String NHIF_RESP_MESSG = "respmessg";
    String NHIF_MOB_EMAIL = "nhifmobemail";
    String NHIF_SPACE = " ";
    String TXN_CODE = "txncode";
    String MESSAGE = "message";
    String TXN_SUB_CODE = "txnsubcode";
    String FND_TXN_CODE = "fundtxncode";

    String TXN_PROC_CODE = "processingcode";
    String TXN_NARRATION = "narration";
    String TXN_FIN_STAT = "financialstatus";
    String TXN_NON_FIN_STAT = "nonfinancialstatus";
    String TXN_PREP_STAT = "preparestatus";
    String FIMI_STATUS = "fimistatus";
    String TXN_COMP_STAT = "completestatus";

    String KEN_CURR = "KES";
    String MOBILE = "mobile";
    String SERVICE_ID = "serviceid";
    String CELL_USER = "username";
    String CELL_PASS = "password";
    String CELL_FULL_NAME = "fullname";
    String CELL_ACCT_NUMBER = "accountnumber";
    String MEANS_OF_PAYMENT = "meansofpayment";
    String BRANCH_CODE = "branchcode";

    // public String BAL_TXNCODE = "310000";
    // public String MINI_STMT_TXNCODE = "940000";
    // public String FUNDS_OWN_TRNF_TXNCODE = "710000";
    // public String FUNDS_WITH_BNK_TXNCODE = "720000";
    // public String FUNDS_TO_PAY_TXNCODE = "740000";
    // public String AIR_TO_OWN_MOB_TXNCODE = "750000";
    // public String AIR_TO_OTH_MOB_TXNCODE = "760000";
    // public String MPESA_TO_BNK_TXNCODE = "780000";
    // public String BNK_TO_MPESA_TXNCODE = "800000";
    // public String FUNDS_TO_MYBILL_TXNCODE = "810000";
    // public String FUNDS_TO_OTBILL_TXNCODE = "820000";

    String VAL_404 = "404";
    String AMOUNT = "amount";
    String AMOUNT_TO_NHIF = "amounttonhif";
    String DB_ACCT = "debitaccount";
    String CR_ACCT = "creditaccount";
    String REV_DB_ACCT = "revdebitaccount";
    String REV_CR_ACCT = "revcreditaccount";
    String NHIF_PROCESSING_CODE = "nhifproccode";
    String NHIF_TYPE_OF_SEARCH = "typeofsearch";

    String MTI_HEADER = "0200";
    String NAC_TPDU = "ISO036000005";
    String ASCII_TPDU = "ISO006000040";
    String STATIC_CARD = "4459710000000000";
    String REV_FLD_12 = "revfld12";
    String REV_FLD_13 = "revfld13";
    String REV_FLD_37 = "revfld37";
    String REV_FLAG = "revflag";

    String REV_MTI_HEAD = "0420";
    String POS_1_4 = "0200";
    String POS_33_42 = "0000000000";

    String FLD_11 = "MI0001";
    String FLD_28 = "00000000";
    String FLD_32 = "445971";
    String FLD_35 = "379999999999999999=99999999999999999999";
    String FLD_41 = "MOBEE           ";
    String FLD_43 = "NBK                   NAIROBI         KE";
    String FLD_61 = "0130001NBOK0000";

    String ISO_CHANNEL = "iso-channel";
    String OUT_LOG = "outLog";
    String IMAL_SWITCH_IP = "imal.switch.ip";
    String IMAL_SWITCH_PORT = "imal.switch.port";
    String IMAL_SWITCH_TIME_OUT = "imal.switch.timeout";

    String BFUB_SWITCH_IP = "bfub.switch.ip";
    String BFUB_SWITCH_PORT = "bfub.switch.port";
    String BFUB_SWITCH_TIME_OUT = "bfub.switch.timeout";

    String BFUB = "BFUB";
    String IMAL = "IMAL";

    String REF_ID = "referenceid";
    String RESP_CODE = "responsecode";
    String RESP_MESSAGE = "responsemessage";
    String RESP_BALANCE = "balance";

    String RESP_ACCOUNTS = "accounts";
    String RESP_CUSTOMER = "customer";

    String RESP_BRANCHES = "branches";
    String RESP_BRANCH_CODE = "branchcode";
    String RESP_BRANCH_NAME = "branchname";
    String ERROR_CODE = "errorcode";
    String ERROR_MESSAGE = "errormessage";

    int NHIF_PAY_METHOD = 2;


    String MASTER_KEY = null;
    String KEYGEN_ALG = "AES";
    int SYMETRIC_KEY_SIZE = 256;
    String PKI_PROVIDER = "BC";
    String SYMETRICKEY_ALG = "AES/CBC/PKCS7Padding";
    String CH_KEY = "/9229874682736729";
    String KEY_IS_GENERAL = "generallogin";

}
