package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Penyewa;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListPenyewaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView rvPenyewa;
    private FloatingActionButton fabAddRent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_penyewa);

        dbHelper = new DBHelper(this);
        rvPenyewa = findViewById(R.id.rv_penyewa);
        fabAddRent = findViewById(R.id.fab_add_rent);

        rvPenyewa.setLayoutManager(new LinearLayoutManager(this));

        fabAddRent.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur Tambah Penyewa", Toast.LENGTH_SHORT).show();
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Penyewa> list = dbHelper.getAllPenyewa();
        rvPenyewa.setAdapter(new PenyewaAdapter(list));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_penyewa);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_penyewa) return true;
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_kamar) {
                startActivity(new Intent(this, ListKamarActivity.class));
            } else if (id == R.id.nav_keuangan) {
                startActivity(new Intent(this, KeuanganActivity.class));
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    // Simple Adapter
    class PenyewaAdapter extends RecyclerView.Adapter<PenyewaAdapter.ViewHolder> {
        private List<Penyewa> data;

        public PenyewaAdapter(List<Penyewa> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Penyewa p = data.get(position);
            holder.text1.setText(p.getNama());
            holder.text2.setText("WA: " + p.getWhatsapp());
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}