# Crave

App de recetas para Android hecha para el 2do parcial de Desarrollo de Aplicaciones Móviles II (UCES, 2C 2026). Tema elegido: **2) Explorador de Recetas y Platos**, con la API de [TheMealDB](https://www.themealdb.com/api.php).

## Video

<!-- Arrastrar el video acá al editar el README en GitHub (o pegar el link de YouTube/Drive) -->

[VIDEO]

## Qué hace

- **Explorar:** lista de platos por categoría (se elige en un Spinner). Cada plato muestra su foto y el nombre.
- **Buscar:** por nombre del plato o por ingrediente. Se usan `search.php?s=` y `filter.php?i=` y se juntan los resultados sin repetir.
- **Detalle:** categoría, área de origen, ingredientes con sus medidas y los pasos de la preparación. Esta pantalla está hecha en Jetpack Compose.
- **Mi libreta:** desde el detalle se puede guardar una receta en SQLite. Las recetas guardadas se ven en la segunda pestaña y se pueden abrir sin internet.

Si no hay conexión o la búsqueda no trae nada, la lista muestra un mensaje en vez de quedar vacía.

## Cómo cumple los requisitos

| Requisito | Dónde está |
|---|---|
| MVVM | `ui/viewmodel` (MainViewModel, DetailViewModel) + `data/repository/MealRepository` |
| Retrofit + Gson | `data/remote/ApiService`, `data/remote/RetrofitClient`, modelos en `data/model` |
| RecyclerView + Adapter + ViewHolder en XML | `ui/adapter/MealAdapter` + `res/layout/item_meal.xml` |
| SQLite | `data/local/MealDbHelper` (SQLiteOpenHelper, tabla `cookbook`) |
| Fragments | `MealListFragment` y `CookbookFragment`, alternados con un BottomNavigationView |
| Jetpack Compose | `DetailActivity` + `ui/screen/MealDetailScreen` + `ui/components` |
| Picasso | En `MealAdapter` y en `MealImage` (dentro de Compose con `AndroidView`) |

## Estructura

```
com.example.crave
├── data
│   ├── local        -> MealDbHelper (SQLite)
│   ├── model        -> data classes de la API y de la app
│   ├── remote       -> ApiService y RetrofitClient
│   └── repository   -> MealRepository (decide si va a la API o a SQLite)
├── ui
│   ├── adapter      -> MealAdapter
│   ├── components   -> partes de la pantalla de detalle (Compose)
│   ├── fragment     -> MealListFragment, CookbookFragment
│   ├── screen       -> MealDetailScreen (Compose)
│   ├── theme        -> colores y tema de Compose
│   └── viewmodel    -> MainViewModel, DetailViewModel
├── MainActivity     -> contiene los fragments y la barra de abajo
└── DetailActivity   -> abre la pantalla de detalle
```

Las llamadas a la API y a la base corren en corrutinas con `Dispatchers.IO`. Los ViewModels exponen el estado con LiveData (pantallas XML) y StateFlow (pantalla Compose).

## Cómo correrlo

1. Clonar el repo y abrirlo con Android Studio.
2. Esperar a que sincronice Gradle.
3. Ejecutar en un emulador o en un celular con Android 7.0 (API 24) o superior. La primera vez hace falta internet para cargar las recetas.

## Librerías

Retrofit 2 + Gson, Picasso, Kotlin Coroutines, AndroidX Lifecycle (ViewModel y LiveData), Fragment KTX, Material Components y Jetpack Compose con Material 3.

---

Alumna: Antonella Montalvan
