package com.consumers.fastwayadmin.Tables;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.consumers.fastwayadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AddTables extends AppCompatActivity {

    EditText tableNumber;
    Bitmap bmp,scaled;

    Uri filePath;
    EditText numberOfSeats;
    Button generateQrCode;
    SharedPreferences sharedPreferences;
    FirebaseAuth tableAuth;
    DatabaseReference tableRef;
    FirebaseStorage storage;
    StorageReference reference;
    ImageView imageView;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tables);
        initialise();
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }

        imageView.setOnClickListener(view -> {
        });

        generateQrCode.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            if(tableNumber.length() == 0){
                tableNumber.requestFocus();
                tableNumber.setError("Field can't be Empty");
                progressBar.setVisibility(View.INVISIBLE);
                return;
            }else if(numberOfSeats.length()==0){
                numberOfSeats.requestFocus();
                numberOfSeats.setError("Field can't be Empty");
                progressBar.setVisibility(View.INVISIBLE);
                return;
            }
            tableClass tableClass = new tableClass(numberOfSeats.getText().toString(),tableNumber.getText().toString());
            tableRef.child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(Objects.requireNonNull(tableAuth.getCurrentUser()).getUid())).child("Tables").child(tableNumber.getText().toString())
                    .setValue(tableClass)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(AddTables.this, "Table added Successfully", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(AddTables.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    });

            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(tableAuth.getUid() + "," + tableNumber.getText().toString() + "," + sharedPreferences.getString("state","") + "," + sharedPreferences.getString("locality","") + "," + "FastwayQR", BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
//                    Toast.makeText(AddTables.this, "Click on Image to download it..", Toast.LENGTH_SHORT).show();
                ((ImageView) findViewById(R.id.img_result_qr)).setImageBitmap(bmp);

                BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = draw.getBitmap();

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                File dir = new File(sdCard.getAbsolutePath());
                dir.mkdirs();
                String fileName = String.format("Table " + tableNumber.getText().toString() + ".jpg", "Table " + tableNumber.getText().toString());
                File outFile = new File(dir, fileName);
                File checkIfExist = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + fileName);
                if(outFile.exists())
                    outFile.delete();

                try {
                    outStream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                try {
                    assert outStream != null;
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(outFile));
                sendBroadcast(intent);
                Toast.makeText(AddTables.this, "Image and PDF saved in Root directory. Check your Internal Storage", Toast.LENGTH_SHORT).show();

                Document document = new Document();

                String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();

                try {
                    PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/Table " + tableNumber.getText().toString() +".pdf")); //  Change pdf name.
                } catch (DocumentException | FileNotFoundException e) {
                    Toast.makeText(AddTables.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                document.open();

                Image image = null;  // Change image's name and extension.
                try {
                    image = Image.getInstance(directoryPath + "/Table " + tableNumber.getText().toString() + ".jpg");
                } catch (BadElementException | IOException e) {
                    Toast.makeText(AddTables.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                image.scalePercent(scaler);
                image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

                try {
                    document.add(image);
                } catch (DocumentException e) {
                    Toast.makeText(AddTables.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                document.close();

                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
//                Bitmap map = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = reference.child(tableAuth.getUid()+"/"+tableNumber.getText().toString()).putBytes(data);
                uploadTask.addOnFailureListener(exception -> Toast.makeText(AddTables.this, "" + exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                });

                PdfDocument pdfDocument = new PdfDocument();
                Paint myPaint = new Paint();
                PdfDocument.PageInfo myPage = new  PdfDocument.PageInfo.Builder(1240,980,1).create();
                PdfDocument.Page page = pdfDocument.startPage(myPage);

                Paint text = new Paint();

                Canvas canvas = page.getCanvas();

                canvas.drawBitmap(bitmap,345,180,myPaint);
                text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                text.setTextSize(85);
                SharedPreferences resNamePref = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
                text.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Name: " + resNamePref.getString("hotelName",""),300,90,text);
                text.setTextAlign(Paint.Align.LEFT);
                text.setTextSize(70);
                canvas.drawText("Table Number: " + tableNumber.getText().toString(),345,215,text);
                canvas.drawText("Seats: " + numberOfSeats.getText().toString(),475,710,text);
                canvas.drawBitmap(scaled,950,700,myPaint);

                pdfDocument.finishPage(page);

                String fileNames = "/Table " + tableNumber.getText().toString() + ".pdf";
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileNames);

                try{
                    pdfDocument.writeTo(new FileOutputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StorageReference storageReference = reference.child(tableAuth.getUid() + "/" + "Tables" + fileNames);
                storageReference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                        Toast.makeText(AddTables.this, "PDF Uploaded", Toast.LENGTH_SHORT).show();
                });

                pdfDocument.close();
                if(file.exists()){
                    Uri selectedUri = Uri.fromFile(file);
                    Intent intents = new Intent(Intent.ACTION_VIEW);
                    intents.setDataAndType(FileProvider.getUriForFile(AddTables.this, AddTables.this.getApplicationContext().getPackageName() + ".provider",file), "application/pdf"); // here we set correct type for PDF
                    intents.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Log.i("info",selectedUri.toString());
                    PendingIntent pendingIntent;
                    pendingIntent = PendingIntent.getActivity(AddTables.this, 698, intents, PendingIntent.FLAG_IMMUTABLE
                    );


                    String channel_id = "notification_channel";
                    NotificationCompat.Builder builder
                            = new NotificationCompat
                            .Builder(getApplicationContext(),
                            channel_id)
                            .setPriority(NotificationManager.IMPORTANCE_MAX)
                            .setSmallIcon(R.drawable.foodinelogo)
                            .setAutoCancel(true)
                            .setVibrate(new long[]{1000, 1000, 1000,
                                    1000, 1000})
                            .setOnlyAlertOnce(true)
                            .setContentIntent(pendingIntent);
                    builder = builder.setContent(
                            getCustomDesign());
                    NotificationManager notificationManager
                            = (NotificationManager) getSystemService(
                            Context.NOTIFICATION_SERVICE);
                    // Check if the Android Version is greater than Oreo
                    if (Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel
                                = new NotificationChannel(
                                channel_id, "web_app",
                                NotificationManager.IMPORTANCE_HIGH);
                        notificationManager.createNotificationChannel(
                                notificationChannel);
                    }

                    notificationManager.notify(10101, builder.build());
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }

        });
    }
    private RemoteViews getCustomDesign() {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification);
        remoteViews.setTextViewText(R.id.title, "Invoice");
        remoteViews.setTextViewText(R.id.message, "Click here to check Invoice PDF (Print and paste on Table)");
        remoteViews.setImageViewResource(R.id.icon,
                R.drawable.foodinelogo);
        return remoteViews;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddTables.this);
                builder.setTitle("Important").setCancelable(false)
                        .setMessage("External storage is required for proper functioning of app. Wanna provide permission???")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (ContextCompat.checkSelfPermission(AddTables.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                            }
                        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                builder.show();
            }
        }

    }

    private void initialise() {
        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.foodinelogo);
        scaled = Bitmap.createScaledBitmap(bmp,250,250,false);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        tableNumber = findViewById(R.id.tableNumber);
        numberOfSeats = findViewById(R.id.numberOfSeats);
        generateQrCode = findViewById(R.id.genrateCode);
        tableAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.img_result_qr);
        tableRef = FirebaseDatabase.getInstance().getReference().getRoot();
        progressBar = findViewById(R.id.checkBar);
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
    }


}