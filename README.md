```markdown
# TotemQuick (Fabric 1.20.1)

TotemQuick — V tuşuna basınca envanterdeki Totem of Undying'ı offhand (kalkan/sol el) slotuna koyan küçük Fabric modudur.

- Mod adı: totemquick
- Loader: Fabric
- Minecraft: 1.20.1
- Client-side only

Özellikler:
- V tuşu: envanterde totem varsa offhand ile swap eder veya yerleştirir.
- Totem yoksa action-bar (ekran üstü) ile uyarı verir.

Kurulum / Derleme:
1. Java 17 yüklü olduğundan emin olun.
2. Proje kökünde:
   - Unix/macOS: `./gradlew build`
   - Windows: `gradlew.bat build`
3. Oluşan `build/libs/totemquick-1.0.0.jar` dosyasını Minecraft `mods` klasörüne atın.
4. Fabric Loader + Fabric API (uyumlu sürüm) ile Minecraft 1.20.1 başlatın.

Notlar:
- Bu sürüm tamamen client-side implementasyondur. Bazı multiplayer sunucular inventory güncellemelerini geri çevirebilir. Sunucu-kompatibilite istiyorsan packet tabanlı senkronizasyon veya server mod desteği ekleyebilirim.
- Keybind kategorisi ve adı için basit İngilizce çeviri eklendi (assets/.../lang/en_us.json). İstersen Türkçe ekleyebilirim.
```# TotemQuick
Simple Fabric mod that equips a Totem of Undying to off-hand with one key. (Minecraft 1.20.1)
