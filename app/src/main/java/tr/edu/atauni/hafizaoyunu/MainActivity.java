package tr.edu.atauni.hafizaoyunu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Arayüz Elemanlarını Tanımlıyoruz

        EditText isimKutusu = findViewById(R.id.isimText);
        Button baslaButonu = findViewById(R.id.baslaBtn);

        // Zorluk seviyesi seçim elemanları
        RadioButton rbOrta = findViewById(R.id.rbOrta);
        RadioButton rbZor = findViewById(R.id.rbZor);

        // 2. Butona Tıklama Olayı
        baslaButonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isim = isimKutusu.getText().toString();

                if (isim.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Lütfen isminizi giriniz!", Toast.LENGTH_SHORT).show();
                } else {
                    //zor
                    int satir = 4;
                    int sutun = 4;

                    // orta
                    if (rbOrta.isChecked()) {
                        satir = 6;
                        sutun = 6;
                    } else if (rbZor.isChecked()) {//zor
                        satir = 8;
                        sutun = 8;
                    }

                    // 3. Oyunu Başlat ve Verileri Gönder
                    Intent i = new Intent(MainActivity.this, oyunEkrani.class);

                    i.putExtra("ism", isim);          // İsmi gönder
                    i.putExtra("satirSayisi", satir); // Satır sayısını gönder
                    i.putExtra("sutunSayisi", sutun); // Sütun sayısını gönder

                    startActivity(i);
                }
            }
        });

    }
}