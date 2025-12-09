import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Deklarasi Variabel View
    private TextView tvTotalKamar, tvTotalPenyewa, tvTotalLunas, tvTotalPerbaikan;
    private TextView tvPendapatanValue, tvTargetInfo;
    private ProgressBar pbTarget;
    private ImageButton btnProfile;
    private BottomNavigationView bottomNavigationView;

    // Helper Database
    private DatabaseHelper dbHelper;

    // Target Hardcoded (Bisa diubah nanti ke database settings jika perlu)
    private final double TARGET_PENDAPATAN = 10000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Database
        dbHelper = new DatabaseHelper(this);

        // Inisialisasi Views
        initViews();

        // Setup Listener Tombol Profil
        setupProfileButton();

        // Setup Navigasi Bawah
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Muat ulang data setiap kali Activity ini tampil (agar data selalu fresh)
        loadDashboardData();
    }

    private void initViews() {
        // Binding ID sesuai instruksi prasyarat XML di atas
        tvTotalKamar = findViewById(R.id.tv_total_kamar_value);
        tvTotalPenyewa = findViewById(R.id.tv_total_penyewa_value);
        tvTotalLunas = findViewById(R.id.tv_total_lunas_value);
        tvTotalPerbaikan = findViewById(R.id.tv_total_perbaikan_value);

        tvPendapatanValue = findViewById(R.id.tv_pendapatan_value);
        pbTarget = findViewById(R.id.pb_target);
        tvTargetInfo = findViewById(R.id.tv_target_info);

        btnProfile = findViewById(R.id.btn_profile);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void loadDashboardData() {
        // 1. Ambil data dari DatabaseHelper (Backend Local)
        int countKamar = dbHelper.getTotalKamar();
        int countPenyewa = dbHelper.getTotalPenyewa();
        int countLunas = dbHelper.getTotalLunasBulanIni();
        int countPerbaikan = dbHelper.getTotalPerbaikan();
        double totalPendapatan = dbHelper.getPendapatanBulanIni();

        // 2. Tampilkan ke TextView Statistik
        tvTotalKamar.setText(String.valueOf(countKamar));
        tvTotalPenyewa.setText(String.valueOf(countPenyewa));
        tvTotalLunas.setText(String.valueOf(countLunas));
        tvTotalPerbaikan.setText(String.valueOf(countPerbaikan));

        // 3. Format Mata Uang Rupiah
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tvPendapatanValue.setText(formatRupiah.format(totalPendapatan));

        // 4. Hitung dan Tampilkan Progress Target
        int percentage = 0;
        if (TARGET_PENDAPATAN > 0) {
            percentage = (int) ((totalPendapatan / TARGET_PENDAPATAN) * 100);
        }

        // Batasi max 100% agar bar tidak overflow visualnya (opsional)
        if (percentage > 100) percentage = 100;

        pbTarget.setProgress(percentage);
        tvTargetInfo.setText(percentage + "% dari target bulan ini");
    }

    private void setupProfileButton() {
        btnProfile.setOnClickListener(v -> {
            // Intent ke ProfilActivity (Pastikan Activity ini sudah dibuat nanti)
            Intent intent = new Intent(MainActivity.this, ProfilActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        // Set item yang aktif saat ini adalah Dashboard
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                return true; // Sedang di halaman ini
            } else if (itemId == R.id.nav_kamar) {
                startActivity(new Intent(this, KamarActivity.class));
                // Jangan finish() agar user bisa back ke dashboard dengan tombol back HP
                return true;
            } else if (itemId == R.id.nav_penyewa) {
                startActivity(new Intent(this, PenyewaActivity.class));
                return true;
            } else if (itemId == R.id.nav_pendapatan) {
                // Pastikan PendapatanActivity sudah dibuat
                startActivity(new Intent(this, PendapatanActivity.class));
                return true;
            }
            return false;
        });
    }
}