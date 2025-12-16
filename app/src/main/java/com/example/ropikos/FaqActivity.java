package com.example.ropikos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class FaqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        ImageView btnBack = findViewById(R.id.btn_back);
        Button btnGithub1 = findViewById(R.id.btn_github_1);
        Button btnGithub2 = findViewById(R.id.btn_github_2);

        // 1. Logika Tombol Back
        btnBack.setOnClickListener(v -> finish());

        // 2. Logika GitHub Link 1 (Thaaan184)
        btnGithub1.setOnClickListener(v -> {
            String url = "https://github.com/Thaaan184";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        // 3. Logika GitHub Link 2 (OrgBelajar)
        btnGithub2.setOnClickListener(v -> {
            String url = "https://github.com/orgbelajar";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }
}