package tr.edu.atauni.hafizaoyunu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Sonuc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sonuc);


        TextView sonucYazisi = findViewById(R.id.sonucText);
        Button tekrarBtn = findViewById(R.id.yenidenBaslaBtn);

        Intent gelenVeri = getIntent();

        String durum = gelenVeri.getStringExtra("kazandiMi");

        if ("evet".equals(durum)) {
            sonucYazisi.setText("TEBRİKLER!\nKAZANDINIZ 🎉");
        } else {
            // Eğer durum null veya "hayır" ise
            sonucYazisi.setText("MAALESEF\nKAYBETTİNİZ 😔");
        }

        tekrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tekrar Giriş Ekranına Dön
                Intent i = new Intent(Sonuc.this, MainActivity.class);
                startActivity(i);
                finish(); // Bu sayfayı kapat
            }
        });
    }
}