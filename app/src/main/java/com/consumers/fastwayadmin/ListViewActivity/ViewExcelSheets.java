package com.consumers.fastwayadmin.ListViewActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;

import com.consumers.fastwayadmin.R;

import java.io.File;

public class ViewExcelSheets extends AppCompatActivity {
    Button viewSheets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_excel_sheets);
        viewSheets = findViewById(R.id.viewSheetsButton);

        viewSheets.setOnClickListener(click -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/RestaurantEarningTracker.xlsx");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(FileProvider.getUriForFile(ViewExcelSheets.this, getApplicationContext().getPackageName() + ".provider",file), "application/vnd.ms-excel");
//            intent.setDataAndType(Uri.parse(file.getPath().toString()), "application/vnd.ms-excel");
            startActivity(intent);
        });
    }
}