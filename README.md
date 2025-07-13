
# 📖 RecipeBook Planner App

An Android app built with Java and Gradle that helps you plan, save, and manage your favorite recipes — all in one place! Users can upload recipes using image files, add ingredients, and create a personalized list of favorite recipes.

## ✨ Features

- 📸 **Upload Recipes via Image**: Take or upload a photo of your recipe, and store it in your recipe book.
- 📝 **Add Ingredients**: Organize your recipes with structured ingredient lists.
- ⭐ **Favorites List**: Mark and quickly access your favorite recipes.
- 🔍 **Search & Browse**: Easily search through your saved recipes.
- 🗃️ **Recipe Storage**: All your recipes saved locally.

## 🛠 Tech Stack

- **Language**: Java 
- **Build Tool**: Gradle  
- **Minimum SDK**: 21  
- **Target SDK**: 34  
- **Image Handling**: Camera / Gallery integration  
- **Storage**: Local database (Room)  
- **UI**: Material Design Components  

## 📦 Project Structure

```
RecipeBookPlanner/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/myrecipebook/
│   │   │   │       ├── activities/
│   │   │   │       ├── adapters/
│   │   │   │       ├── models/
│   │   │   │       └── db/
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   └── values/
│   │   │   └── AndroidManifest.xml
│   └── build.gradle
└── build.gradle
```

## 🚀 Getting Started

1. **Clone the Repository**  
   ```bash
   git clone https://github.com/your-username/RecipeBookPlanner.git
   cd RecipeBookPlanner
   ```

2. **Open in Android Studio**  
   - Open Android Studio
   - Select "Open an Existing Project"
   - Choose the cloned folder

3. **Build the App**  
   - Let Gradle sync
   - Run the app on your emulator or device

## ✅ Requirements

- Android Studio Hedgehog or later
- Gradle 8+
- Android SDK 21+
- Internet permissions (if using cloud sync)
