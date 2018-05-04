package com.ceva.ubmobile.core.signon;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.dialogs.ResponseDialogs;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.core.ui.Sign_In;
import com.ceva.ubmobile.models.SecurityQuestionsModel;
import com.ceva.ubmobile.network.ApiClientString;
import com.ceva.ubmobile.network.ApiInterface;
import com.ceva.ubmobile.network.NetworkUtils;
import com.ceva.ubmobile.security.UBNSession;
import com.ceva.ubmobile.security.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecurityQuestion extends BaseActivity {
    RadioGroup questionsGroup;
    LinearLayout custom;
    String selectedQuestion = null;
    EditText custUser;

    String accountnumber = null, customermobile = null;
    ArrayList<SecurityQuestionsModel> securityQuestions = new ArrayList<>();
    Button button;
    UBNSession session;
    String selectedQuestionId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limitsetting);
        setToolbarTitle("Security Question");
        session = new UBNSession(this);

        String query = session.getString(SecurityQuestionsModel.KEY_QUESTIONS);

        if (query != null) {
            try {
                JSONArray array = new JSONArray(query);
                int k = array.length();
                for (int i = 0; i < k; i++) {
                    JSONObject obj = array.getJSONObject(i);
                    securityQuestions.add(new SecurityQuestionsModel(obj.optString(SecurityQuestionsModel.KEY_QST), obj.optString(SecurityQuestionsModel.KEY_QST_ID)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        accountnumber = session.getString(SecurityQuestionsModel.KEY_ACCOUNT);
        customermobile = session.getString(SecurityQuestionsModel.KEY_CUSTMOBILE);

        custom = findViewById(R.id.custom);
        custUser = findViewById(R.id.custUser);

        questionsGroup = findViewById(R.id.usernameGroup);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utility.isNotNull(selectedQuestion)) {
                    if (Utility.isNotNull(custUser.getText().toString())) {
                        if (NetworkUtils.isConnected(SecurityQuestion.this)) {
                            registerUser(session.getString(SecurityQuestionsModel.KEY_ACCOUNT),
                                    session.getString(SecurityQuestionsModel.KEY_SELID),
                                    session.getString(SecurityQuestionsModel.KEY_PASSWORD),
                                    session.getString(SecurityQuestionsModel.KEY_PIN),
                                    selectedQuestionId,
                                    selectedQuestion,
                                    custUser.getText().toString(),
                                    session.getString(SecurityQuestionsModel.KEY_STATUS));
                        } else {
                            ResponseDialogs.warningDialogLovely(SecurityQuestion.this, "Error", getString(R.string.error_no_internet_connection));

                        }
                    } else {
                        ResponseDialogs.warningDialogLovely(SecurityQuestion.this, "Error", "Please supply an answer for your selected security question.");

                    }
                } else {
                    ResponseDialogs.warningDialogLovely(SecurityQuestion.this, "Error", "Please select a security question");
                }

            }
        });
        setQuestions();

    }

    private void setQuestions() {
        //for (int row = 0; row < 1; row++) {
        //RadioGroup ll = new RadioGroup(this);
        int row = 4;
        int number = securityQuestions.size();
        if (number > 0) {
            for (int i = 0; i < number; i++) {
                SecurityQuestionsModel model = securityQuestions.get(i);
                RadioButton rdbtn = new RadioButton(this);
                rdbtn.setId(i);
                rdbtn.setText(model.getQuestion());
                questionsGroup.addView(rdbtn);
            }
        }

        questionsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                RadioButton selectedRadio = findViewById(i);
                selectedQuestion = selectedRadio.getText().toString();

                for (int j = 0; j < securityQuestions.size(); j++) {
                    SecurityQuestionsModel que = securityQuestions.get(j);
                    if (selectedQuestion.equals(que.getQuestion())) {
                        selectedQuestionId = que.getQuestionID();
                    }
                }

            }
        });

        //((ViewGroup) findViewById(R.id.radiogroup)).addView(ll);
        //}
    }

    /**
     * @param accountnumber
     * @param userName
     * @param pin
     * @param tranpin
     * @param quest_id
     * @param question
     * @param answer
     * @param status
     */
    private void registerUser(String accountnumber, final String userName, String pin, String tranpin, String quest_id, String question, String answer, String status) {
        //@Path("/register/{acctnumber}/{userid}/{pin}/{txnpin}/{sqid}/{sqs}/{sqans}/{status}")

        showLoadingProgress();

        String urlparam = "register/" + accountnumber + "/" + userName + "/" + pin + "/" + tranpin + "/" + quest_id + "/" + question + "/" + answer + "/" + status;
        String data = null;

        ApiInterface apiService = ApiClientString.getClient().create(ApiInterface.class);
        Call<String> call = apiService.setGenericRequestRaw(urlparam);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.debug("register", response.body());
                dismissProgress();

                try {
                    JSONObject obj = new JSONObject(response.body());

                    if (obj.optString(Constants.KEY_CODE).equals("00")) {
                        ResponseDialogs.sucessDialogLovelyToActivity(SecurityQuestion.this, "Registered!", obj.optString(Constants.KEY_MSG), Sign_In.class);
                    } else {
                        ResponseDialogs.warningDialogLovely(SecurityQuestion.this, "Error", obj.optString(Constants.KEY_MSG));
                        //onContinueClick();
                    }

                    session.clearData(SecurityQuestionsModel.KEY_PASSWORD);
                    session.clearData(SecurityQuestionsModel.KEY_ACCOUNT);
                    session.clearData(SecurityQuestionsModel.KEY_PIN);
                    session.clearData(SecurityQuestionsModel.KEY_QUESTIONS);
                    session.clearData(SecurityQuestionsModel.KEY_CUSTMOBILE);
                    session.clearData(SecurityQuestionsModel.KEY_SELID);
                    session.clearData(SecurityQuestionsModel.KEY_STATUS);
                    session.clearData(SecurityQuestionsModel.KEY_USERNAMES);
                    session.clearData(SecurityQuestionsModel.KEY_QST);
                    session.clearData(SecurityQuestionsModel.KEY_QST_ID);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                dismissProgress();
                com.ceva.ubmobile.core.ui.Log.debug("register", t.toString());
                //showToast(getString(R.string.error_500));
                // prog.dismiss();
                // startDashBoard();
            }
        });

    }

    public void onContinueClick() {

    }
}
