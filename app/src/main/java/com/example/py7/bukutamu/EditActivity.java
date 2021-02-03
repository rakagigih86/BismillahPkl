package com.example.py7.bukutamu;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    DBHelper helper;
    EditText TxNomor, TxNama, TxJumlahTamu, TxTanggal, TxKeterangan;
    Spinner SpNP;
    long id;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormatter;
    CircularImageView imageView;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        helper = new DBHelper(this);

        id = getIntent().getLongExtra(DBHelper.row_id, 0);

        TxNomor = (EditText)findViewById(R.id.txNomor_Edit);
        TxNama = (EditText)findViewById(R.id.txNama_Edit);
        TxJumlahTamu = (EditText)findViewById(R.id.txJumlahTamu_Edit);
        TxTanggal = (EditText)findViewById(R.id.txTanggal_Edit);
        TxKeterangan = (EditText)findViewById(R.id.txKeterangan_Edit);
        SpNP = (Spinner)findViewById(R.id.spNP_Edit);
        imageView = (CircularImageView)findViewById(R.id.image_profile);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        TxTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(EditActivity.this);
            }
        });

        getData();
    }

    private void showDateDialog(){
        Calendar calendar = Calendar.getInstance();

        datePickerDialog =  new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                TxTanggal.setText(dateFormatter.format(newDate.getTime()));
            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void getData(){
        Cursor cursor = helper.oneData(id);
        if(cursor.moveToFirst()){
            String nomor = cursor.getString(cursor.getColumnIndex(DBHelper.row_nomor));
            String nama = cursor.getString(cursor.getColumnIndex(DBHelper.row_nama));
            String jumlahtamu = cursor.getString(cursor.getColumnIndex(DBHelper.row_jumlahtamu));
            String np = cursor.getString(cursor.getColumnIndex(DBHelper.row_namapegawai));
            String tanggal = cursor.getString(cursor.getColumnIndex(DBHelper.row_tanggal));
            String keterangan = cursor.getString(cursor.getColumnIndex(DBHelper.row_keterangan));
            String foto = cursor.getString(cursor.getColumnIndex(DBHelper.row_foto));

            TxNomor.setText(nomor);
            TxNama.setText(nama);

            if (np.equals("Pak Hasan")){
                SpNP.setSelection(0);
            }else if(np.equals("Pak Depri")){
                SpNP.setSelection(1);
            }else if (np.equals("Pak Samid")){
                SpNP.setSelection(2);
            }else if (np.equals("Pak Haryo")){
                SpNP.setSelection(3);
            }else if (np.equals("Pak Kuncoro")){
                SpNP.setSelection(4);
            }else if (np.equals("Bu Ula")){
                SpNP.setSelection(5);
            }else if (np.equals("Bu Nunuk")){
                SpNP.setSelection(6);
            }else if (np.equals("Bu Retno")){
                SpNP.setSelection(7);
            }

            TxJumlahTamu.setText(jumlahtamu);
            TxTanggal.setText(tanggal);
            TxKeterangan.setText(keterangan);

            if (foto.equals("null")){
                imageView.setImageResource(R.drawable.ic_person_black_24dp);
            }else{
                imageView.setImageURI(Uri.parse(foto));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_edit:
                String nomor = TxNomor.getText().toString().trim();
                String nama = TxNama.getText().toString().trim();
                String jumlahtamu = TxJumlahTamu.getText().toString().trim();
                String tanggal = TxTanggal.getText().toString().trim();
                String keterangan = TxKeterangan.getText().toString().trim();
                String np = SpNP.getSelectedItem().toString().trim();

                ContentValues values = new ContentValues();
                values.put(DBHelper.row_nomor, nomor);
                values.put(DBHelper.row_nama, nama);
                values.put(DBHelper.row_jumlahtamu, jumlahtamu);
                values.put(DBHelper.row_tanggal, tanggal);
                values.put(DBHelper.row_keterangan, keterangan);
                values.put(DBHelper.row_namapegawai, np);
                values.put(DBHelper.row_foto, String.valueOf(uri));

                if (nomor.equals("") || nama.equals("") || jumlahtamu.equals("") ||tanggal.equals("") || keterangan.equals("")){
                    Toast.makeText(EditActivity.this, "Data Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
                }else {
                    helper.updateData(values, id);
                    Toast.makeText(EditActivity.this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
        switch (item.getItemId()){
            case R.id.delete_edit:
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setMessage("Data ini akan dihapus.");
                builder.setCancelable(true);
                builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.deleteData(id);
                        Toast.makeText(EditActivity.this, "Data Terhapus", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
                && resultCode == Activity.RESULT_OK){
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)){
                uri = imageuri;
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}
                        , 0);
            }else{
                startCrop(imageuri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                imageView.setImageURI(result.getUri());
                uri = result.getUri();
            }
        }
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
        uri = imageuri;
    }
}
