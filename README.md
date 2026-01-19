# 🧠 Android Memory Game (Firebase Integrated)

Bu proje, Java ve Android SDK kullanılarak geliştirilmiş, bulut tabanlı özelliklerle desteklenen gelişmiş bir hafıza oyunudur. Veri saklama ve kullanıcı etkileşimi için Firebase servisleri aktif olarak kullanılmıştır.


## 🛠 Kullanılan Teknolojiler (Tech Stack)

* **Language:** Java
* **Platform:** Android SDK (Min API 24)
* **Database:** Firebase Firestore (NoSQL - Realtime Data)
* **Notifications:** Firebase Cloud Messaging (FCM)
* **UI/UX:** XML Layouts, Custom Animations, Responsive Grid
* **Version Control:** Git & GitHub

## 🚀 Öne Çıkan Özellikler (Key Features)

### 1. Bulut Tabanlı Skor Tablosu (Cloud Persistence)
Oyun yerel depolama yerine **Firebase Firestore** ile entegre çalışır.
* Kullanıcı oyunu bitirdiği anda skor, isim ve tarih bilgisi asenkron olarak buluta gönderilir.
* Veriler anlık olarak güncellenir ve kaybolmaz.

### 2. Uzaktan Bildirim Sistemi (Push Notifications)
Kullanıcı etkileşimini artırmak için **Firebase Cloud Messaging (FCM)** entegrasyonu yapılmıştır.
* Uygulama kapalıyken veya arka plandayken kullanıcılara anlık duyuru/güncelleme mesajları gönderilebilir.
* Topic Messaging (`duyurular`) yapısı kullanılmıştır.

### 3. Ekran Oryantasyonu Yönetimi (Orientation Handling)
Android Activity Yaşam Döngüsü (Lifecycle) profesyonelce yönetilmiştir.
* **Sorun:** Cihaz yan çevrildiğinde (Landscape) Activity'nin yeniden başlatılması ve oyunun sıfırlanması.
* **Çözüm:** `onConfigurationChanged` metodu override edilerek, oyun durumu korunmuş ve grid yapısı yeni ekran boyutuna göre dinamik olarak yeniden hesaplanmıştır.

### 4. Gelişmiş Animasyonlar
* Kullanıcı deneyimini (UX) iyileştirmek için kart açma/kapama işlemlerinde **3D Flip (Dönme)** animasyonları kullanılmıştır.
* Doğru/Yanlış eşleşmelerde sesli geri bildirimler (MediaPlayer) eklenmiştir.

## 👨‍💻 Geliştirici (Developer)

**Muhammet Aslan**
* Software Engineering Student
* LinkedIn Profil Linkim : (https://www.linkedin.com/in/muhammet-aslan-1890a4350/)
* Email Adresim : muhammet.aslan.dev@gmail.com

---
