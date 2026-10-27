# Keanu Smart Pantry Manager — Java Android app

This is an **Android Studio project**, not a website or a Jotform app. The assignment specifies a native Java Android app with SQLite; Jotform is not needed for the required features.

## Setup

1. Install Android Studio and Android SDK Platform 35, with JDK 17.
2. Extract the ZIP and open the `Keanu_SmartPantry` folder using **File > Open**.
3. Allow Android Studio to install/sync Android Gradle Plugin 8.6.1. If prompted, install missing SDK components.
4. Select an emulator or Android device running Android 6.0 (API 23) or later.
5. Press **Run** to install and launch the app.

This source package does not include a compiled APK or Gradle wrapper. Android Studio can sync the Gradle build scripts with its installed Gradle distribution.

## Features

- Pantry screen: list all ingredients; add, edit and delete; confirmation before deleting.
- Edit screen: ingredient name, positive quantity, unit selector and optional valid expiry date.
- Recipes: 20 recipes automatically seeded in SQLite on first launch.
- Suggestions: only recipes with every required ingredient in sufficient quantity; compatible g/kg and ml/l values convert; basic singular/plural handling.
- Details: required quantities and preparation steps.
- Settings: locally persisted preference for expiry highlighting; information about the app.
- Data persists after closing and reopening because it is stored in SQLite.

## Quick acceptance test

1. Start with an empty pantry: no suggestions should appear.
2. Add `egg`, 2 `piece`: scrambled eggs must **not** appear (missing butter).
3. Add `butter`, 10 `g`: Scrambled Eggs **must** appear.
4. Edit butter to 5 g: Scrambled Eggs must disappear.
5. Edit butter to 0.01 kg: Scrambled Eggs must reappear.
6. Delete egg: Scrambled Eggs must disappear.
7. Add `tomatoes`, 2 `piece` and `salt`, 1 `g`: Tomato Salad should appear.
8. Close and reopen the app: pantry entries should remain.
9. Try negative quantities and an invalid expiry date: the form should reject them.

## Implementation notes

- Database: `DB.java` using `SQLiteOpenHelper`.
- Screen navigation: explicit `Intent` between activities.
- Dynamic list: `ListView` with `PantryAdapter`.
- No maps, GPS, shopping links or payment features.
- Expiry is informational; it does not disqualify recipes. Users should check food safety before cooking.

## Assessment evidence still to complete

You must build and run this on your device/emulator, capture **real** screenshots, record a **5–7-minute narrated** demonstration, and produce a genuine incremental GitHub history (10+ commits). No GitHub repository, screenshots or demonstration video have been fabricated.
