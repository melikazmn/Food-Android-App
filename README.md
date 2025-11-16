# AI Recipe App (Android)
*A Kotlin MVVM Recipe Application Powered by AI & TheMealDB*

<p align="center">
  <img src="docs/home.png" width="65%" alt="Home screen"/>
</p>

---

## 📌 Overview

This Android application allows users to:

- Browse recipes from **TheMealDB**
- Search meals by name, category, and alphabet
- View detailed recipes (ingredients, measures, instructions)
- Save favorites offline using **Room**
- Generate **AI-powered recipes** based on user-provided ingredients

The AI recipes are fetched from a separate backend (FastAPI), but this README focuses **only on the Android client**.

---

## ✨ Features

### 🔎 Meal Discovery
- Search meals by name  
- Filter by category  
- Browse A–Z index  
- Random meal generator  

### 📖 Meal Details
- Step-by-step cooking instructions  
- Ingredients & measurement breakdown  
- YouTube tutorial link (if available)  
- Source link to original recipe  

### ⭐ Favorites
- Save or remove meals from favorites  
- Persisted locally using **Room Database**  
- Favorites searchable offline  

### 🤖 AI Recipe Generator
- Enter ingredients (comma-separated)
- App calls the backend’s `/v1/ai/recipe` endpoint
- AI returns a **MealDB-compatible** recipe object
- Displays in **the same UI** as regular meals

<p align="center">
  <img src="docs/ai-generate.png" width="65%" alt="AI Generation"/>
</p>

---

## 🧱 Architecture

This project follows a clean Android architecture:

```text
Presentation Layer
 ├── Fragments (Home, Detail, Favorites, AI Recipe)
 └── ViewModels (StateFlow based)

Domain Layer
 ├── Repository interfaces
 └── Mappers

Data Layer
 ├── Retrofit API Services
 ├── Room Database + DAO
 └── Network + Local models
```

### Patterns Used
- MVVM  
- Repository pattern  
- Kotlin Coroutines (Flow/StateFlow)  
- ViewBinding  
- Room (local data)  
- Retrofit + OkHttp  
- Glide for images  

---

## 📂 Project Structure

```text
android/
├── feature/
│   ├── home_screen/
│   ├── meal/
│   ├── favorites_screen/
│   └── ai_recipe/
│
├── db/
│   ├── Database.kt
│   └── MealDao.kt
│
├── shared_component/
│   ├── API.kt
│   ├── SearchAPIService.kt
│   ├── FilterAPIService.kt
│   ├── LookupAPIService.kt
│   └── RandomAPIService.kt
│
└── utils/
    └── (extensions, helpers)
```

---

## 📡 Networking

### 1) TheMealDB API
Used for:
- Search meals  
- Categories  
- Filters  
- Random meal  
- Detailed lookup  

Configured in `shared_component/API.kt` using Retrofit + OkHttp + Gson.

### 2) AI Backend (for AI recipes)
Used only in `feature/ai_recipe` module.

Base URL example (emulator local backend) in `AiNetwork.kt`:

```kotlin
object AiNetwork {
    const val BASE_URL = "http://10.0.2.2:8080/"
}
```

For production, it’s recommended to move this into `BuildConfig` via product flavors.

---

## 🧠 ViewModels

### HomeViewModel
- Handles search, categories, filtering, random meal
- Drives home screen UI state (loading/data/error)

### HowToViewModel (Detail)
- Loads meal details by `mealId`
- Manages favorite toggle state
- Talks to `MealRepository` (network + Room)

### FavoritesViewModel
- Observes `Flow<List<FavoriteMealEntity>>` from Room
- Exposes filtered favorites list based on search query

### AiRecipeViewModel
- Validates user input (ingredients)
- Calls AI backend through `AiRecipeRepository`
- Emits states: `Idle`, `Loading`, `Success`, `Error`

---

## 💾 Local Storage (Room)

Favorite meals are stored in a Room database (`MealDatabase`).  
The `FavoriteMealEntity` schema mirrors the important MealDB fields:

```text
idMeal (PK)
strMeal
strCategory
strArea
strInstructions
strMealThumb
strIngredient1..20
strMeasure1..20
```

Benefits:
- Favorites survive app restarts
- Accessible offline
- Reactive updates via Kotlin Flow

---

## 🖼️ UI Screens

### Home Screen
- Categories row
- Search bar
- Random meals carousel
- Alphabet filter

`feature/home_screen/presentation/ui/HomeFragment.kt`

### Meal Detail Screen
- Big image
- Area & category
- Full instructions
- Dynamic ingredient list (up to 20 rows)
- Favorite button

`feature/detail_screen/presentation/ui/DetailScreen.kt`

### Favorites Screen
- List of saved recipes
- Search within favorites

`feature/favorites_screen/presentation/ui/FavoritesFragment.kt`

### AI Recipe Screen
- Multi-line ingredient input
- Generate button
- Shows generated meal (image, title, ingredients, instructions)

`feature/ai_recipe/presentation/AiRecipeFragment.kt`

---

## 🚀 How to Run

### Prerequisites
- Android Studio Flamingo or newer  
- JDK 17  
- Internet connection for TheMealDB & AI backend  

### Steps
1. Clone the repository  
2. Open the **android** module in Android Studio  
3. Update backend URL for AI in `AiNetwork.kt` or via `BuildConfig`  
4. Sync Gradle  
5. Run on emulator or physical device  

---

## 🧪 Testing

Recommended / existing tests:
- Unit tests for data mappers (API → Entity → UI model)
- Repository tests (success + failure paths)
- ViewModel tests (state transitions, filters, favorites behavior)

---

## 🛣️ Roadmap

- Migrate manual DI to **Hilt**
- Add more UI polish & animations
- Improve error + empty states (skeletons, retry buttons)
- Pagination for searches
- Better offline experience (cached meal lists)

---

## 📄 License

MIT License (or your chosen license).
