package tr.edu.atauni.hafizaoyunu;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;

public class Kart extends AppCompatButton {

    public boolean acikMi = false;
    public boolean eslesti = false;
    public int resId; // Kartın ön yüzündeki resim
    public int kartId;

    public Kart(Context context, int rid, int id) {
        super(context);
        this.resId = rid;
        this.kartId = id;


        setBackgroundResource(R.drawable.kart_arkaplan);
    }

    public void dondur() {
        // eşlendiyse pasif yap
        if (eslesti) {
            return;
        }

        if (acikMi) {
            // açıldıysa tekrar kapat
            setBackgroundResource(R.drawable.kart_arkaplan);
            acikMi = false;
        } else {
            // kapalıysa aç
            setBackgroundResource(resId);
            acikMi = true;
        }
    }
}