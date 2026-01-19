package tr.edu.atauni.hafizaoyunu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// --- FIREBASE KÜTÜPHANELERİ ---
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class oyunEkrani extends AppCompatActivity {

    // --- FIREBASE DEĞİŞKENLERİ ---
    FirebaseFirestore db;
    String oyuncuIsmi;


    TextView tvOyuncuIsmi, tvSure, tvSkor, tvHamle;
    GridLayout oyunIzgarasi;


    CountDownTimer zamanlayici;
    int satirSayisi, sutunSayisi;
    int toplamPuan = 0;
    int hamleSayisi = 0;


    MediaPlayer mpDogru, mpYanlis, mpBitis;


    ArrayList<Integer> kartResimIDleri = new ArrayList<>();
    boolean[] kartAcikMi;
    int ilkAcilanKartIndeksi = -1;
    boolean islemYapiliyor = false;

    // resimlerin tutulduğu yer
    int[] resimHavuzu = {
            R.drawable.bat, R.drawable.broccoli, R.drawable.camera, R.drawable.camping,
            R.drawable.corn, R.drawable.cruise_ship, R.drawable.devil, R.drawable.eggplant,
            R.drawable.elephant, R.drawable.frog, R.drawable.ginger, R.drawable.jellyfish,
            R.drawable.koala, R.drawable.luggage, R.drawable.map, R.drawable.mosnter,
            R.drawable.mountain, R.drawable.scythe, R.drawable.spider, R.drawable.squirrel,
            R.drawable.superhero, R.drawable.turtle, R.drawable.whale, R.drawable.witch_hat,
            R.drawable.kart1, R.drawable.kart2, R.drawable.kart3, R.drawable.kart4,
            R.drawable.kart5, R.drawable.kart6, R.drawable.kart7, R.drawable.kart8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_oyun_ekrani);

        // firebase tanımlama
        db = FirebaseFirestore.getInstance();

        // bildirim kanalına bağlanma
        FirebaseMessaging.getInstance().subscribeToTopic("duyurular");

        // Elemanları bağla
        tvOyuncuIsmi = findViewById(R.id.tvOyuncuIsmi);
        tvSure = findViewById(R.id.tvSure);
        tvSkor = findViewById(R.id.tvSkor);
        tvHamle = findViewById(R.id.tvHamle);
        oyunIzgarasi = findViewById(R.id.oyunIzgarasi);

        // Intent verilerini al
        Intent gelenIntent = getIntent();
        if (gelenIntent != null) {
            oyuncuIsmi = gelenIntent.getStringExtra("ism");
            satirSayisi = gelenIntent.getIntExtra("satirSayisi", 4);
            sutunSayisi = gelenIntent.getIntExtra("sutunSayisi", 4);
        }

        // İsim boş gelirse null olarak belirle hata alma diye
        if (oyuncuIsmi == null) oyuncuIsmi = "Misafir Oyuncu";

        tvOyuncuIsmi.setText("Oyuncu: " + oyuncuIsmi);
        tvHamle.setText("Hamle: 0");


        try {
            mpDogru = MediaPlayer.create(this, R.raw.ses_dogru);
            mpYanlis = MediaPlayer.create(this, R.raw.ses_yanlis);
            mpBitis = MediaPlayer.create(this, R.raw.ses_bitis);
        } catch (Exception e) { e.printStackTrace(); }

        oyunuHazirla();
    }

    private void oyunuHazirla() {
        int toplamKart = satirSayisi * sutunSayisi;
        kartAcikMi = new boolean[toplamKart];
        kartResimIDleri.clear();

        ArrayList<Integer> havuzListesi = new ArrayList<>();
        for (int resim : resimHavuzu) { havuzListesi.add(resim); }
        Collections.shuffle(havuzListesi);

        int gerekenCiftSayisi = toplamKart / 2;
        for (int i = 0; i < gerekenCiftSayisi; i++) {
            int secilenResim = havuzListesi.get(i % havuzListesi.size());
            kartResimIDleri.add(secilenResim);
            kartResimIDleri.add(secilenResim);
        }
        Collections.shuffle(kartResimIDleri);

        oyunIzgarasi.removeAllViews();
        oyunIzgarasi.setRowCount(satirSayisi);
        oyunIzgarasi.setColumnCount(sutunSayisi);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int ekranGenisligi = size.x;
        int boslukPayi = 100;
        int margin = 5;
        int kartBoyutu = ((ekranGenisligi - boslukPayi) / sutunSayisi) - (margin * 2);

        for (int i = 0; i < toplamKart; i++) {
            ImageButton kart = new ImageButton(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = kartBoyutu;
            params.height = kartBoyutu;
            params.setMargins(margin, margin, margin, margin);
            params.rowSpec = GridLayout.spec(i / sutunSayisi);
            params.columnSpec = GridLayout.spec(i % sutunSayisi);
            kart.setLayoutParams(params);

            kart.setImageResource(R.drawable.kart_arkaplan);
            kart.setScaleType(ImageView.ScaleType.FIT_XY);
            kart.setPadding(0, 0, 0, 0);
            kart.setBackgroundColor(Color.TRANSPARENT);
            kart.setId(i);

            final int tiklananKartIndex = i;
            kart.setOnClickListener(v -> kartTiklandi(tiklananKartIndex, (ImageButton) v));
            oyunIzgarasi.addView(kart);
        }

        zamanlayiciyiBaslat();
    }

    private void kartTiklandi(int index, ImageButton kartButton) {
        if (kartAcikMi[index] || islemYapiliyor) return;


        animasyonlaCevir(kartButton, kartResimIDleri.get(index)); // EKLE
        kartAcikMi[index] = true;

        if (ilkAcilanKartIndeksi == -1) {
            ilkAcilanKartIndeksi = index;
        } else {
            hamleSayisi++;
            tvHamle.setText("Hamle: " + hamleSayisi);
            islemYapiliyor = true;
            checkEslesme(ilkAcilanKartIndeksi, index);
        }
    }

    private void checkEslesme(int birinciIndex, int ikinciIndex) {
        ImageButton birinciButton = (ImageButton) oyunIzgarasi.getChildAt(birinciIndex);
        ImageButton ikinciButton = (ImageButton) oyunIzgarasi.getChildAt(ikinciIndex);

        if (kartResimIDleri.get(birinciIndex).equals(kartResimIDleri.get(ikinciIndex))) {

            if (mpDogru != null) mpDogru.start();
            toplamPuan += 10;
            tvSkor.setText("Skor: " + toplamPuan);

            birinciButton.setEnabled(false);
            ikinciButton.setEnabled(false);
            ilkAcilanKartIndeksi = -1;
            islemYapiliyor = false;
            oyunBittiMi();
            // checkEslesme fonksiyonunun içindeki ELSE kısmı:
        } else {

            if (mpYanlis != null) mpYanlis.start();

            new Handler().postDelayed(() -> {


                // 1. Kartı Kapat
                animasyonlaCevir(birinciButton, R.drawable.kart_arkaplan);
                kartAcikMi[birinciIndex] = false;

                // 2. Kartı Kapat
                animasyonlaCevir(ikinciButton, R.drawable.kart_arkaplan);
                kartAcikMi[ikinciIndex] = false;

                ilkAcilanKartIndeksi = -1;
                islemYapiliyor = false;
            }, 800); // 0.8 saniye yanlış kartı görsün, sonra dönsün
        }
    }

    private void oyunBittiMi() {
        boolean hepsiAcik = true;
        for (boolean b : kartAcikMi) {
            if (!b) { hepsiAcik = false; break; }
        }

        if (hepsiAcik) {
            zamanlayici.cancel();
            if (mpBitis != null) mpBitis.start();

            //
            skoruBulutaKaydet();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("TEBRİKLER! 🎉");
            builder.setMessage("Skor Buluta Kaydediliyor...\nPuan: " + toplamPuan);
            builder.setCancelable(false);
            builder.setPositiveButton("TEKRAR OYNA", (dialog, which) -> recreate());
            builder.setNegativeButton("ÇIKIŞ", (dialog, which) -> finish());
            builder.show();
        }
    }

    //veritabanı bağlantısı kısmı için fonk
    private void skoruBulutaKaydet() {
        // Kayıt başlıyor uyarısı
        Toast.makeText(this, "Buluta kayıt deneniyor...", Toast.LENGTH_SHORT).show();

        Map<String, Object> skorVerisi = new HashMap<>();
        skorVerisi.put("oyuncuIsmi", oyuncuIsmi);
        skorVerisi.put("puan", toplamPuan);
        skorVerisi.put("hamle", hamleSayisi);
        skorVerisi.put("tarih", Timestamp.now());

        db.collection("Skorlar")
                .add(skorVerisi)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(oyunEkrani.this, "BAŞARILI! Skor Kaydedildi ✅", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e ->
                        Toast.makeText(oyunEkrani.this, "HATA! Kaydedilemedi ❌: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void zamanlayiciyiBaslat() {
        if (zamanlayici != null) zamanlayici.cancel();
        zamanlayici = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long kalanSure) {
                tvSure.setText("Süre: " + kalanSure / 1000);
                if (kalanSure < 10000) tvSure.setTextColor(Color.RED);
            }
            @Override
            public void onFinish() {
                tvSure.setText("Süre: 0");
                islemYapiliyor = true;

                AlertDialog.Builder builder = new AlertDialog.Builder(oyunEkrani.this);
                builder.setTitle("SÜRE DOLDU!");
                builder.setMessage("Puan: " + toplamPuan);
                builder.setCancelable(false);
                builder.setPositiveButton("TEKRAR", (dialog, which) -> recreate());
                builder.setNegativeButton("ÇIK", (dialog, which) -> finish());
                builder.show();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (zamanlayici != null) zamanlayici.cancel();
        if (mpDogru != null) mpDogru.release();
        if (mpYanlis != null) mpYanlis.release();
        if (mpBitis != null) mpBitis.release();
    }
    // ekran döndürünce uyum için
    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Ekran boyutlarını yeniden al
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int ekranGenisligi = size.x;
        int ekranYuksekligi = size.y;

        // Kart boyutunu hesapla
        // Eğer YATAY ise (Landscape): Yüksekliğe göre hesapla ki taşmasın
        // Eğer DİKEY ise (Portrait): Genişliğe göre hesapla
        int kartBoyutu;
        int margin = 5;

        if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            // Yatay modda ekranın yüksekliğine göre ayarla
            int ustBoslukPayi = 200; // Skor ve süre yazıları için pay
            kartBoyutu = ((ekranYuksekligi - ustBoslukPayi) / satirSayisi) - (margin * 2);
        } else {
            // Dikey modda genişliğe göre ayarla
            int yanBoslukPayi = 100;
            kartBoyutu = ((ekranGenisligi - yanBoslukPayi) / sutunSayisi) - (margin * 2);
        }

        // Tüm kartların boyutunu güncelle
        for (int i = 0; i < oyunIzgarasi.getChildCount(); i++) {
            ImageButton kart = (ImageButton) oyunIzgarasi.getChildAt(i);
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) kart.getLayoutParams();
            params.width = kartBoyutu;
            params.height = kartBoyutu;
            params.setMargins(margin, margin, margin, margin);
            kart.setLayoutParams(params);
        }
    }

    // kart çevirme animasyonu
    private void animasyonlaCevir(final ImageButton kart, final int hedefResimID) {
        // 3D efekti için kamera mesafesi ayarı (Bu olmazsa kart kağıt gibi dümdüz döner)
        float scale = getResources().getDisplayMetrics().density;
        kart.setCameraDistance(8000 * scale);

        // 1. AŞAMA: Kartı 90 derece döndür (Görünmez olana kadar)
        kart.animate().withLayer().rotationY(90).setDuration(200).withEndAction(() -> {

            // Tam ortada resmi değiştir
            kart.setImageResource(hedefResimID);

            // 2. AŞAMA: Dönüşü tamamla (Ters taraftan geliyormuş gibi yap)
            kart.setRotationY(-90);
            kart.animate().withLayer().rotationY(0).setDuration(200).start();

        }).start();
    }

}