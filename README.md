# Unit Converter

Хэмжих нэгжүүдийг хооронд нь хөрвүүлдэг Android аппликейшн (Kotlin, XML).

## Боломжууд

- Нээлтийн дэлгэц (Splash): 2.5 секундын дараа автоматаар шилжинэ, буцах товчоор эргэж ирэхгүй
- Нэгжийн төрлүүдийн дэлгэц: 12 ангиллыг 2 баганатай grid-ээр харуулна, хайлт бодит цагт шүүнэ (том жижиг үсэг ялгахгүй)
- Хөрвүүлэгч дэлгэц: From / To нэгж сонгож утга оруулахад үр дүн шууд шинэчлэгдэнэ, ⇄ товчоор нэгжийг солино
- Оролтын шалгалт: хоосон, үсэг, олон аравтын таслал, сөрөг утга (зөвхөн Температурт зөвшөөрнө)

Ангиллууд: Length, Area, Volume, Mass, Time, Speed, Temperature, Density, Energy, Angle, Weight, Fuel

## Хөрвүүлэлтийн арга

- Ангилал бүр суурь нэгжтэй (Length - метр, Mass - кг гэх мэт). Нэгж бүр суурь нэгжтэй харьцуулсан коэффициенттэй тул `value * from.factor / to.factor` томьёогоор аль ч хосыг хөрвүүлнэ. Нэгжийн хос бүрийг тусад нь бичээгүй.
- Температур нь шилжилттэй томьёотой тул онцгой тохиолдол болгон Цельсээр дамжуулж хөрвүүлнэ (°C, °F, K).
- Үр дүн хамгийн ихдээ 6 орны нарийвчлалтай, төгсгөлийн тэгийг хасна. Хэт том эсвэл хэт жижиг утгыг шинжлэх ухааны хэлбэрээр харуулна.

## Шаардлага

- Android Studio (сүүлийн тогтвортой хувилбар)
- JDK 11 (Android Studio-той хамт ирдэг)
- Android SDK: minSdk 24, Android SDK Platform суулгасан байх
- Интернет холболт (Gradle эхний удаа сан татна)
- Төхөөрөмж: Android emulator эсвэл USB debugging-тэй Android утас (Android 7.0 буюу түүнээс дээш)

## Build хийх заавар

1. Репозиторийг clone хийнэ:
   ```
   git clone <repo-ийн хаяг>
   ```
2. Android Studio-д **File → Open** дээр clone хийсэн хавтсыг сонгоно.
3. Gradle sync дуустал хүлээнэ. SDK-ийн зам автоматаар `local.properties` файлд бичигдэнэ.
4. Дээд талаас төхөөрөмж сонгоод **Run 'app'** (▶) дарна.

Командын мөрөөс build хийх бол:

```
./gradlew assembleDebug
```

(Windows дээр `gradlew.bat assembleDebug`)

## Unit test ажиллуулах

Android Studio-д `ConverterTest` дээр баруун товч → **Run 'ConverterTest'**, эсвэл:

```
./gradlew test
```

Тест нь хөрвүүлэлт (Length, Time, Angle), температурын шилжилт, форматлалт болон оролтын шалгалтыг хамарна.

## Төслийн бүтэц

```
app/src/main/java/com/example/unitconverter/
├── SplashActivity.kt        Нээлтийн дэлгэц
├── MainActivity.kt          Нэгжийн төрлүүдийн дэлгэц (хайлттай grid)
├── CategoryAdapter.kt       Ангиллын картуудын RecyclerView adapter
├── ConverterActivity.kt     Хөрвүүлэгч дэлгэц
└── logic/
    └── Converter.kt         UI-аас хараат бус хөрвүүлэлтийн логик

app/src/test/java/com/example/unitconverter/
└── ConverterTest.kt         Unit test
```

## Зохиогч

Ч.Төгөлдөр

