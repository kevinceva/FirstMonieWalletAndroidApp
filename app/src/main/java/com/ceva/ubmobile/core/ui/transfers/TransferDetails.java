package com.ceva.ubmobile.core.ui.transfers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.adapter.ConfirmAdapter;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.ConfirmModel;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brian on 08/08/2017.
 */

public class TransferDetails extends DialogFragment {
    @BindView(R.id.confirm_list)
    RecyclerView confirmView;
    ConfirmAdapter confirmAdapter;
    List<ConfirmModel> confirmModelList = new ArrayList<>();
    @BindView(R.id.btnClose)
    Button btnClose;
    @BindView(R.id.btnShare)
    Button btnShare;
    @BindView(R.id.btnDownload)
    Button btnDownload;
    @BindView(R.id.receipt)
    ScrollView receipt;
    Uri imageUri = null;

    /**
     * The system calls this to get the DialogFragment's layout, regardless
     * of whether it's being displayed as a dialog or an embedded fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.fragment_transfer_details, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();

        ArrayList<ConfirmModel> confirmModelList1 = bundle.getParcelableArrayList("confirmList");

        confirmModelList.addAll(confirmModelList1);

        /*ConfirmModel item = new ConfirmModel("Amount", "N5,000.00");
        confirmModelList.add(item);
        item = new ConfirmModel("Transaction Date", "18-May-2017 11:45:27");
        confirmModelList.add(item);
        item = new ConfirmModel("Debit Account", "0005534521");
        confirmModelList.add(item);
        item = new ConfirmModel("Beneficiary Account Name", "Brian Towett");
        confirmModelList.add(item);
        item = new ConfirmModel("Beneficiary Account", "8005534521");
        confirmModelList.add(item);
        item = new ConfirmModel("Beneficiary Bank", "CEVA Bank");
        confirmModelList.add(item);
        item = new ConfirmModel("Reference Number", "77558005534521");
        confirmModelList.add(item);*/

        confirmAdapter = new ConfirmAdapter(confirmModelList, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        confirmView.setLayoutManager(mLayoutManager);
        confirmView.setItemAnimator(new DefaultItemAnimator());
        confirmView.setAdapter(confirmAdapter);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File rec = saveBitMap(getContext(), receipt, true);
                if (rec == null) {
                    ((TransfersHistory) getActivity()).showToast("File could not be generated");
                }
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File rec = saveBitMap(getContext(), receipt, false);
                if (rec == null) {
                    ((TransfersHistory) getActivity()).showToast("File could not be generated");
                }
            }
        });

        setCancelable(false);
        return view;
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        try {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        } catch (Exception e) {
            Log.Error(e);
        }

        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private File saveBitMap(Context context, ScrollView drawView, boolean isShare) {
        ((TransfersHistory) getActivity()).showToast("Generating receipt, please wait");
        File pictureFileDir = new File(Constants.KEY_IMAGE_PATH, "UnionReceipts");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.debug("TAG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + "UnionReceipt" + System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.debug("TAG", "There was an issue saving the image.");
        }

        imageUri = Uri.parse(pictureFile.getAbsolutePath());
        scanGallery(context, pictureFile.getAbsolutePath(), isShare);
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(ScrollView view) {
        //Define a bitmap with the same size as the view
        int h = 0;
        //get the actual height of scrollview
        for (int i = 0; i < view.getChildCount(); i++) {
            h += view.getChildAt(i).getHeight();
            view.getChildAt(i).setBackgroundResource(R.color.white);
        }
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), h, Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }


    // used for scanning gallery
    private void scanGallery(Context cntx, String path, final boolean isShare) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    /*if (isShare) {
                        shareBitmap(path);
                    } else {
                        downloadPDF(path);
                    }*/
                    downloadPDF(path, isShare);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.debug("TAG", "There was an issue scanning gallery.");
        }
    }

    public void shareBitmap(String path) {

        Intent i = new Intent(Intent.ACTION_SEND);

        i.setType("image/*");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
    /*compress(Bitmap.CompressFormat.PNG, 100, stream);
    byte[] bytes = stream.toByteArray();*/
        i.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        try {
            startActivity(Intent.createChooser(i, "My Profile ..."));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }

    }

    private void downloadPDF(String path, boolean isShare) {
        //((TransfersHistory)getActivity()).showToast("Generating receipt, please wait");
        try {
            Document document = new Document();
            String dirpath = Constants.KEY_IMAGE_PATH + "UnionReceipts" + File.separator + "UnionReceipt" + System.currentTimeMillis() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(dirpath)); //  Change pdf's name.
            document.open();
            Image img = Image.getInstance(path);  // Change image's name and extension.

            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
            //img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            //img.setAlignment(Image.LEFT| Image.TEXTWRAP);
            document.setMargins(10, 10, 10, 10);
            float width = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float height = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            img.scaleToFit(width, height); //Or try this.

            document.add(img);
            document.close();

            File file = new File(dirpath);

            if (isShare) {
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(share, file.getName()));
            } else {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                //target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent intent = Intent.createChooser(target, "Open File");
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.Error(e);
        }
    }
}