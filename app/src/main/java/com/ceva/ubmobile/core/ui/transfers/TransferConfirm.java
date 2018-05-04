package com.ceva.ubmobile.core.ui.transfers;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.ConfirmModel;
import com.ceva.ubmobile.models.ConfirmPageModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransferConfirm extends BaseActivity {
    @BindView(R.id.confirm_list)
    RecyclerView confirmationRecyler;
    @BindView(R.id.loadingLayout)
    LinearLayout loadingLayout;
    ConfirmPageModel confirmPageModel;
    @BindView(R.id.transaction_success_fail)
    ImageView txPassFailImage;
    @BindView(R.id.transactionMessage)
    TextView transactionMessage;
    @BindView(R.id.btncancel)
    Button btnCancel;
    @BindView(R.id.btnContinue)
    Button btnContinue;
    @BindView(R.id.buttonsField)
    LinearLayout buttonsField;
    @BindView(R.id.pin_lock_view)
    PinLockView mPinLockView;
    @BindView(R.id.indicator_dots)
    IndicatorDots mIndicatorDots;
    @BindView(R.id.passcodelayout)
    RelativeLayout passcodelayout;
    @BindView(R.id.pinFields)
    LinearLayout pinFields;
    @BindView(R.id.confirmLayout)
    LinearLayout confirmLayout;
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            //Log.debug("Pin complete: " + pin);
            showLoadingLayout();
        }

        @Override
        public void onEmpty() {
            Log.debug("Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.debug("Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirm);
        setToolbarTitle("Confirm");
        ButterKnife.bind(this);
        confirmPageModel = getIntent().getExtras().getParcelable("confirm");
        buildPage(confirmPageModel);
        hideLoadingLayout();
        showConfirmLayout();
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setPinLength(4);
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);
        mPinLockView.setDeleteButtonPressedColor(ContextCompat.getColor(this, R.color.accent_color));
        mPinLockView.setShowDeleteButton(true);

    }

    @OnClick(R.id.btnContinue)
    protected void onContinueClick() {
        showTransactionPinLayout();
    }

    private void showConfirmLayout() {
        confirmLayout.setVisibility(View.VISIBLE);
        pinFields.setVisibility(View.GONE);
    }

    private void showTransactionPinLayout() {
        confirmLayout.setVisibility(View.GONE);
        pinFields.setVisibility(View.VISIBLE);
    }

    private void showSuccess(String message) {
        txPassFailImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_blue));
        transactionMessage.setText(message);
    }

    private void showFail(String message) {
        txPassFailImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_fail_red));
        transactionMessage.setText(message);
    }

    private void showDebitCardValidation() {

    }

    private void showLoadingLayout() {
        loadingLayout.setVisibility(View.VISIBLE);
        buttonsField.setVisibility(View.GONE);
        showConfirmLayout();
    }

    private void hideLoadingLayout() {
        loadingLayout.setVisibility(View.GONE);

    }

    private void buildPage(ConfirmPageModel confirmPageModel) {
        List<ConfirmModel> confirmItems = confirmPageModel.getConfirmModelList();
        ConfirmAdapter confirmAdapter = new ConfirmAdapter(confirmItems, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        confirmationRecyler.setLayoutManager(mLayoutManager);
        confirmationRecyler.setItemAnimator(new DefaultItemAnimator());
        confirmationRecyler.setAdapter(confirmAdapter);

    }

}
