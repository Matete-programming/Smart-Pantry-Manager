# Smart-Pantry-Manager



Smart Pantry Manager is an Android application developed in Java using Android Studio. It helps users organise pantry ingredients, track quantities and expiry dates, and reduce household food waste by suggesting recipes based strictly on the ingredients available.



The application allows users to add, view, edit, and delete pantry items, recording ingredient names, quantities, measurement units, and optional expiry dates. It also provides recipe suggestions, detailed preparation instructions, and a settings screen for managing the saved expiry-alert preference.



A key feature is the strict recipe-matching system, which ensures that a recipe is suggested only when all its required ingredients are present in sufficient quantities. The system supports selected ingredient-name variations and compatible measurement conversions. The application includes 20 predefined recipes.



The application uses SQLite for local data storage, managed through SQLiteOpenHelper. Its database, smart\_pantry.db, contains four tables: pantry\_items, recipes, recipe\_ingredients, and settings. Data and preferences persist when the application is closed and reopened. The database will automatically install when you run the code



The main technologies used are Java, Android Studio, XML layouts, SQLite, RecyclerView, custom adapters, Android Activities and Intents, and Git/GitHub for version control. The application consists of five main screens: Pantry, Add/Edit Ingredient, Suggested Recipes, Recipe Details, and Settings.



Functional testing covered pantry management, input validation, data persistence, recipe matching, ingredient quantity checks, and settings persistence. During testing, the database contained 20 recipes and 54 recipe-ingredient records.



The application's main limitations are that data is stored locally, recipes are predefined, ingredient matching supports only selected variations and conversions, and actual expiry notifications have not been implemented.









