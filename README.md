
📱 Quote of the Day App

An Android app that displays daily inspirational quotes fetched from the FavQs API.
Built using Java, following MVVM architecture, and using modern Material 3 UI for a clean and elegant design.

✨ Features

✅ Fetch a new inspirational quote  
✅ Refresh quotes on demand  
✅ Share quotes via social apps  
✅ Save your favorite quotes locally using **Room DB**  
✅ View a list of saved favorite quotes  
✅ Splash screen with fade-in/Lottie animation  
✅ Fully responsive **Material 3** themed UI  
✅ Light/Dark mode support  
✅ Snackbar for elegant feedback instead of toasts


🛠️ Tech Stack

| Layer             | Tech Used                           |
|------------------|-------------------------------------|
| Language          | Java                                |
| Architecture      | MVVM (Model-View-ViewModel)         |
| Networking        | Retrofit (API: [FavQs](https://favqs.com/api/)) |
| Local Storage     | Room Database                       |
| UI                | Material 3, ConstraintLayout        |
| Binding           | Data Binding + LiveData             |
| Animation         | Splash Screen with Fade/Lottie      |


📸 Screenshots

> Add your app screenshots here:
- Main screen with quote card
- Share & Favorite buttons
- Favorites list screen
- Splash screen animation


📦 Folder Structure

com.surajvanshsv.quoteapps/
│
├── data/
│   ├── api/            # Retrofit API Service
│   ├── db/             # Room Entities & DAO
│   └── repository/     # Repository layer
│
├── model/              # Quote model classes
│
├── ui/
│   ├── view/           # Activities
│   ├── adapter/        # RecyclerView adapters
│   └── viewmodel/      # ViewModels
│
└── utils/              # Helper classes, constants


🔌 API Used

FavQs API
📘 Docs: [https://favqs.com/api/](https://favqs.com/api/)

To use this API:

* Register for an API key from FavQs
* Add your key in the Retrofit header using:


@Headers("Authorization: Token token=\"YOUR_API_KEY\"")

🚀 Getting Started

1. Clone this repo

   git clone https://github.com/yourusername/QuoteApp.git

2. Open in Android Studio

3. Add your API key
   Edit the `QuoteApiService.java` with your FavQs API key.

4. Run on emulator or device

💡 Learnings

✔ Retrofit and JSON parsing
✔ MVVM structure with ViewModel and LiveData
✔ Room database integration
✔ Material Design 3 styling
✔ DataBinding & UI updates
✔ Snackbar vs Toast feedback
✔ ConstraintLayout mastery
✔ Lottie & splash screen integration

🙌 Acknowledgements

This project is part of my **Android Internship at [CodSoft].
A huge thanks to them for the opportunity to build, learn, and grow! 🌱


📬 Connect with Me

Suraj Kumar
📧 [Email](mailto:sk658139@gmail.com)
🔗 [LinkedIn](https://www.linkedin.com/in/surajpsk12/)
🐙 [GitHub](https://github.com/surajpsk12)


🏷️ Tags

`#Java` `#AndroidDev` `#MVVM` `#Room` `#Retrofit` `#Material3` `#QuotesApp` `#CodSoft` `#InternshipProject` `#MadeWithLove`


Let me know if you'd like:
- `LICENSE` file for open-source
- `CONTRIBUTING.md`
- GitHub Actions workflow setup

