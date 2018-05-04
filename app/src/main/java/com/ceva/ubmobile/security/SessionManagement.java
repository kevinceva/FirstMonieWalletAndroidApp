package com.ceva.ubmobile.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManagement {
    public static final String KEY_SESS = "sessid";
    public static final String SESSKEY = "sesskey";
    public static final String KEY_INSESS = "isinsess";
    public static final String SCHID = "schoolids";
    public static final String KEY_DOCID = "docid";
    public static final String STUDIDONE = "studone";
    public static final String STUDIDTWO = "studtwo";
    public static final String STUDIDTHREE = "studthree";
    public static final String KEY_THEME = "theme";
    public static final String SCHONE = "schone";
    public static final String SCHTWO = "schtwo";
    public static final String PKEY = "pkey";
    public static final String SCHTHREE = "schthree";
    public static final String CUSTOMID = "custidd";
    public static final String KEY_BIOLOG = "BioLog";
    public static final String BOOL_USERID = "booluserid";
    public static final String IS_TPIN = "IsTPin";
    public static final String NETWORK_URL = "neturl";
    public static final String SEL_ACCOUNT = "accont";
    public static final String LASTL = "lastl";
    public static final String DEF_ACCOUNT = "defacc";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_APPID = "appid";
    public static final String KEY_ACC = "keyacc";
    public static final String STARS = "stars";
    public static final String FETCHSTARS = "fetchstars";
    public static final String KEY_PRODID = "prodid";
    public static final String STAR_DATE = "stdate";
    public static final String STAR_MSG = "stmsg";
    // Email address (make variable public to access from outside)
    public static final String KEY_TPIN = "ktpin";
    public static final String KEY_TIMEST = "timest";
    public static final String KEY_TXPIN = "txpin";
    public static final String KEY_INST = "inst";
    public static final String KEY_CUSTNAME = "cust";
    public static final String KEY_FLGLOGIN = "fllog";
    public static final String KEY_USERID = "userid";
    public static final String KEY_NEWUSERID = "newuserid";
    public static final String KEY_MOBILE = "mobno";
    public static final String KEY_FNAMES = "fname";
    public static final String KEY_WPIN = "wpin";
    public static final String KEY_FULLN = "fulln";
    public static final String KEY_DISP = "disp";
    public static final String KEY_CARDL = "cardl";
    public static final String KEY_TOP1 = "top1";
    public static final String KEY_TOP2 = "top2";
    public static final String KEY_TOP3 = "top3";
    // Email address (make variable public to access from outside)
    public static final String KEY_QTY = "qty";
    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_BIOLOGIN = "IsBioLoggedIn";
    private static final String IS_TPINPREF = "IsTpinPref";
    private static final String IS_AST = "IsAst";
    private static final String IS_STARTED = "IsStarted";
    private static final String IS_REG = "IsReg";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public SessionManagement() {

        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String userid) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // editor.putString(KEY_USERID, userid);
        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void setAst() {
        // Storing login value as TRUE
        editor.putBoolean(IS_AST, true);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void UnSetAst() {
        // Storing login value as TRUE
        editor.putBoolean(IS_AST, false);

        // Storing name in pref

        // commit changes
        editor.commit();
    }


    public void setTpinPref() {
        // Storing login value as TRUE
        editor.putBoolean(IS_TPINPREF, true);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void UnSetTpinPref() {
        // Storing login value as TRUE
        editor.putBoolean(IS_TPINPREF, false);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void setBioLog() {
        // Storing login value as TRUE
        editor.putBoolean(IS_BIOLOGIN, true);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void UnSetBioLog() {
        // Storing login value as TRUE
        editor.putBoolean(IS_BIOLOGIN, false);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void setUser() {
        // Storing login value as TRUE
        editor.putBoolean(BOOL_USERID, true);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void UnSetUser() {
        // Storing login value as TRUE
        editor.putBoolean(BOOL_USERID, false);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetTpin(String tpin) {
        // Storing login value as TRUE
        editor.putBoolean(IS_TPIN, true);
        editor.putString(KEY_TPIN, tpin);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetStarted() {
        // Storing login value as TRUE
        editor.putBoolean(IS_STARTED, true);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetReg() {
        // Storing login value as TRUE
        editor.putBoolean(IS_REG, true);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetPersWidg(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, true);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetPersWidgFalse(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, false);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetPers(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, true);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetPersFalse(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, false);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetPersNews(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, true);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetPersNewsFalse(String serv) {
        // Storing login value as TRUE
        editor.putBoolean(serv, false);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public void SetSch1(String serv) {
        // Storing login value as TRUE
        editor.putString(SCHONE, serv);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getSch1() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SCHONE, pref.getString(SCHONE, null));

        return user;
    }

    public void SetSch2(String serv) {
        // Storing login value as TRUE
        editor.putString(SCHTWO, serv);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getSch2() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SCHTWO, pref.getString(SCHTWO, null));

        return user;
    }

    public void SetSch3(String serv) {
        // Storing login value as TRUE
        editor.putString(SCHTHREE, serv);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getSch3() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SCHTHREE, pref.getString(SCHTHREE, null));

        return user;
    }


    public void SetStud1(String serv) {
        // Storing login value as TRUE
        editor.putString(STUDIDONE, serv);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStud1() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STUDIDONE, pref.getString(STUDIDONE, null));

        return user;
    }

    public void SetStud2(String serv) {
        // Storing login value as TRUE
        editor.putString(STUDIDTWO, serv);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStud2() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STUDIDTWO, pref.getString(STUDIDTWO, null));

        return user;
    }

    public void SetStud3(String serv) {
        // Storing login value as TRUE
        editor.putString(STUDIDTHREE, serv);

        // Storing name in pref

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStud3() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STUDIDTHREE, pref.getString(STUDIDTHREE, null));

        return user;
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public boolean checkLogin() {
        // Check login status
        return this.isLoggedIn();

    }

    public boolean checkTpinPref() {
        // Check login status
        return this.isTpinPref();

    }

    public boolean checkBioLogin() {
        // Check login status
        return this.isBioLoggedIn();

    }

    public boolean checkAst() {
        // Check login status
        return this.isAst();

    }

    public boolean checkUser() {
        // Check login status
        return this.isUserId();

    }

    public boolean checkStarted() {
        // Check login status
        return this.isStarted();

    }

    public boolean checkReg() {
        // Check login status
        return this.isReg();

    }

    public boolean checkTPin() {
        // Check login status
        return this.isTPin();

    }

    public boolean checkPersWidg(String serv) {
        // Check login status
        return this.isPers(serv);

    }

    public boolean checkPers(String serv) {
        // Check login status
        return this.isPers(serv);

    }

    public boolean checkPersNews(String serv) {
        // Check login status
        return this.isPersNews(serv);

    }

    public void putURL(String neturl) {
        // Storing login value as TRUE

        editor.putString(NETWORK_URL, neturl);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getNetURL() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(NETWORK_URL, pref.getString(NETWORK_URL, null));

        return user;
    }

    public void putCardl(String neturl) {
        // Storing login value as TRUE

        editor.putString(KEY_CARDL, neturl);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getCardl() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_CARDL, pref.getString(KEY_CARDL, null));

        return user;
    }

    public void putCurrTime(Long time) {
        // Storing login value as TRUE

        editor.putLong(KEY_TIMEST, time);

        // commit changes
        editor.commit();
    }

    public HashMap<String, Long> getCurrTime() {
        HashMap<String, Long> user = new HashMap<>();
        // user name
        user.put(KEY_TIMEST, pref.getLong(KEY_TIMEST, 0));

        return user;
    }

    public void putCurrTheme(int pref) {
        // Storing login value as TRUE

        editor.putInt(KEY_THEME, pref);

        // commit changes
        editor.commit();
    }

    public HashMap<String, Integer> getCurrTheme() {
        HashMap<String, Integer> user = new HashMap<>();
        // user name
        user.put(KEY_THEME, pref.getInt(KEY_THEME, 0));

        return user;
    }

    public void putCustName(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_CUSTNAME, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getCustName() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_CUSTNAME, pref.getString(KEY_CUSTNAME, null));

        return user;
    }

    public void putDocid(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_DOCID, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getDocid() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_DOCID, pref.getString(KEY_DOCID, null));

        return user;
    }

    public void putFlLog(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_FLGLOGIN, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getFlLog() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_FLGLOGIN, pref.getString(KEY_FLGLOGIN, null));

        return user;
    }

    public void putStar(String mno) {
        // Storing login value as TRUE

        editor.putString(STARS, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStar() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STARS, pref.getString(STARS, null));

        return user;
    }


    public void putFetchedStar(String mno) {
        // Storing login value as TRUE

        editor.putString(FETCHSTARS, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getFetchedStar() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(FETCHSTARS, pref.getString(FETCHSTARS, null));

        return user;
    }

    public void putStarDate(String mno) {
        // Storing login value as TRUE

        editor.putString(STAR_DATE, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStarDate() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STAR_DATE, pref.getString(STAR_DATE, null));

        return user;
    }


    public void putStarMsg(String mno) {
        // Storing login value as TRUE

        editor.putString(STAR_MSG, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getStarMsg() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(STAR_MSG, pref.getString(STAR_MSG, null));

        return user;
    }

    public void putLastl(String mno) {
        // Storing login value as TRUE

        editor.putString(LASTL, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getLastl() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(LASTL, pref.getString(LASTL, null));

        return user;
    }

    public void putSessKey(String mno) {
        // Storing login value as TRUE

        editor.putString(SESSKEY, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getSessKey() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SESSKEY, pref.getString(SESSKEY, null));

        return user;
    }

    public void putSessId(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_SESS, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getSessId() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_SESS, pref.getString(KEY_SESS, null));

        return user;
    }

    public void putPKey(String mno) {
        // Storing login value as TRUE

        editor.putString(PKEY, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getPKey() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(PKEY, pref.getString(PKEY, null));

        return user;
    }

    public void putEmail(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_EMAIL, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getEmail() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        return user;
    }


    public void putCustid(String mno) {
        // Storing login value as TRUE

        editor.putString(CUSTOMID, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getCustid() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(CUSTOMID, pref.getString(CUSTOMID, null));

        return user;
    }

    public void putToken(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_TOKEN, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getToken() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        return user;
    }


    public void putAppID(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_APPID, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getAppID() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_APPID, pref.getString(KEY_APPID, null));

        return user;
    }

    public void putAccount(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_ACC, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getAccount() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_ACC, pref.getString(KEY_ACC, null));

        return user;
    }

    public void putTop1(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_TOP1, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getTop1() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOP1, pref.getString(KEY_TOP1, null));

        return user;
    }

    public void putTop2(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_TOP2, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getTop2() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOP2, pref.getString(KEY_TOP2, null));

        return user;
    }

    public void putTop3(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_TOP3, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getTop3() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TOP3, pref.getString(KEY_TOP3, null));

        return user;
    }

    public void putMobNo(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_MOBILE, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getMobNo() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));

        return user;
    }

    public void putUserid(String fulln) {
        // Storing login value as TRUE

        editor.putString(KEY_NEWUSERID, fulln);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserid() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NEWUSERID, pref.getString(KEY_NEWUSERID, null));

        return user;
    }

    public void putFulln(String fulln) {
        // Storing login value as TRUE

        editor.putString(KEY_FULLN, fulln);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getFulln() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_FULLN, pref.getString(KEY_FULLN, null));

        return user;
    }

    public void putBioLog(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_BIOLOG, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getBioLog() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_BIOLOG, pref.getString(KEY_BIOLOG, null));

        return user;
    }

    public void putDisp(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_DISP, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getDisp() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_DISP, pref.getString(KEY_DISP, null));

        return user;
    }

    public void putSelAcc(String acc) {
        // Storing login value as TRUE

        editor.putString(SEL_ACCOUNT, acc);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getSelAcc() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(SEL_ACCOUNT, pref.getString(SEL_ACCOUNT, null));

        return user;
    }

    public void putDefAcc(String acc) {
        // Storing login value as TRUE

        editor.putString(DEF_ACCOUNT, acc);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getDefAcc() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(DEF_ACCOUNT, pref.getString(DEF_ACCOUNT, null));

        return user;
    }

    public void putInst(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_INST, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getInst() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_INST, pref.getString(KEY_INST, null));

        return user;
    }


    public void putInSession(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_INSESS, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getInSession() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_INSESS, pref.getString(KEY_INSESS, null));

        return user;
    }

    public void putTxnFlag(String mno) {
        // Storing login value as TRUE

        editor.putString(KEY_TXPIN, mno);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getTxnFlag() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TXPIN, pref.getString(KEY_TXPIN, null));

        return user;
    }

    public HashMap<String, String> getTpin() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_TPIN, pref.getString(KEY_TPIN, null));

        return user;
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERID, pref.getString(KEY_USERID, null));
        user.put(KEY_FNAMES, pref.getString(KEY_FNAMES, null));
        // user email id

        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        user.put(KEY_WPIN, pref.getString(KEY_WPIN, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.remove(SEL_ACCOUNT);
        editor.remove(KEY_CARDL);
        editor.remove(KEY_DOCID);

        editor.remove(KEY_FULLN);
        editor.remove(KEY_TXPIN);
        editor.remove(KEY_MOBILE);

        editor.remove(KEY_TIMEST);
        editor.remove(DEF_ACCOUNT);

        editor.putBoolean(IS_LOGIN, false);
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isUserId() {

        return pref.getBoolean(BOOL_USERID, false);
    }

    public boolean isLoggedIn() {

        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isTpinPref() {

        return pref.getBoolean(IS_TPINPREF, false);
    }

    public boolean isBioLoggedIn() {

        return pref.getBoolean(IS_BIOLOGIN, false);
    }

    public boolean isAst() {

        return pref.getBoolean(IS_AST, false);
    }

    public boolean isStarted() {

        return pref.getBoolean(IS_STARTED, false);
    }

    public boolean isReg() {

        return pref.getBoolean(IS_REG, false);
    }

    public boolean isTPin() {

        return pref.getBoolean(IS_TPIN, false);
    }

    public boolean isPers(String serv) {

        return pref.getBoolean(serv, false);
    }

    public boolean isPersNews(String serv) {

        return pref.getBoolean(serv, false);
    }

}