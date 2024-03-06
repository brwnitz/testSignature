package com.example.testsignature;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ViewPdf extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        imageView = findViewById(R.id.imagePdf);
        String pdfFileName = "/SignaturePdfs/signedFinal.pdf";
        loadPdfFromAssets(pdfFileName);
    }
    private void loadPdfFromAssets(String pdfFileName) {
        try {
            File outFile = new File(Environment.getExternalStorageDirectory() + pdfFileName);
            InputStream inputStream = new FileInputStream(outFile);
            File tempFile = File.createTempFile("temp",".pdf");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[256];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            PdfRenderer.Page page = pdfRenderer.openPage(0);

            // Render PDF page as bitmap
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Display bitmap in ImageView
            imageView.setImageBitmap(bitmap);

            // Close the PdfRenderer and ParcelFileDescriptor
            page.close();
            pdfRenderer.close();
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}