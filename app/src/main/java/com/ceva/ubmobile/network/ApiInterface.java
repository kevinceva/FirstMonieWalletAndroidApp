package com.ceva.ubmobile.network;

/**
 * Created by brian on 29/09/2016.
 */

import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.models.BankAccountResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ApiInterface {
    //Login endpoint
    @POST(Constants.KEY_LOGIN_ENDPOINT + "/{username}/{password}")
    Call<BankAccountResponse> getLoginResponse(@Path(value = "username", encoded = true) String username, @Path(value = "password", encoded = true) String password);

    //ministatement
    @POST(Constants.KEY_MINISTATEMENT_ENDPOINT + "/{params}")
    Call<BankAccountResponse> getMiniStatement(@Path(value = "params", encoded = true) String accountNumber);

    //ministatement
    @POST(Constants.KEY_FUNDTRANSFER_ENDPOINT + "/{params}")
    Call<BankAccountResponse> invokeFundTransfer(@Path(value = "params", encoded = true) String params);

    //fetchsavedbenef
    @POST(Constants.KEY_FETCHBENEFICIARIES_ENDPOINT + "/{accountNumber}")
    Call<BankAccountResponse> getSavedBeneficiaries(@Path("accountNumber") String accountNumber);

    //@Path("/savebeneficiariesdata/{accountname}/{beneficiaryaccount}/{destinationbank}/{destinationbankname}/{username}")
    @POST(Constants.KEY_SAVEBENEFICIARY_ENDPOINT + "/{params}")
    Call<BankAccountResponse> setBeneficiary(@Path(value = "params", encoded = true) String params);

    @POST(Constants.KEY_FETCHBANKSORBRANCHES_ENDPOINT + "/{params}")
    Call<BankAccountResponse> getBanks(@Path(value = "params", encoded = true) String params);

    @POST(Constants.KEY_FETCHBANKSORBRANCHES_ENDPOINT + "/{params}")
    Call<BankAccountResponse> getBranches(@Path(value = "params", encoded = true) String params);

    @POST(Constants.KEY_FETCHACCOUNTINFO_ENDPOINT + "/{params}")
    Call<BankAccountResponse> getBeneficiaryAccountName(@Path(value = "params", encoded = true) String params);

    @POST(Constants.KEY_BALINQUIRY_ENDPOINT + "/{params}")
    Call<BankAccountResponse> getBalinquiry(@Path(value = "params", encoded = true) String params);

    @POST(Constants.KEY_NIPENQUIRY_ENDPOINT + "/{params}")
    Call<BankAccountResponse> getNIPinquiry(@Path(value = "params", encoded = true) String params);

    @POST(Constants.KEY_BANKDRAFTREQUEST_ENDPOINT + "/{params}")
    Call<BankAccountResponse> setBankDraftRequest(@Path(value = "params", encoded = true) String params);

    @POST(Constants.KEY_CHEAQUEBOOKREQUEST_ENDPOINT + "/{params}")
    Call<BankAccountResponse> setChequeBookRequest(@Path(value = "params", encoded = true) String params);

    @POST(Constants.KEY_ACCOUNT_OPEN_ENDPOINT + "/{params}")
    Call<BankAccountResponse> setAccountOpenRequest(@Path(value = "params", encoded = true) String params);

    @POST("{params}")
    Call<BankAccountResponse> setGenericRequest(@Path(value = "params", encoded = true) String params);

    @POST("{params}")
    Call<String> setGenericRequestRaw(@Path(value = "params", encoded = true) String params);

}