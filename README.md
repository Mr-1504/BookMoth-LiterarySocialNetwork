# BookMoth - Literary Social Network

## Overview

BookMoth is an Android application that combines social networking with a digital marketplace for literary works. It allows users to connect with other book lovers, share posts and comments, browse and purchase literary content, and create their own works using integrated author tools. The app is built with a clean architecture, leveraging modern Android technologies and multiple backend services for a seamless user experience.

## Purpose and Scope

BookMoth aims to create a vibrant community for literary enthusiasts by providing:
- **Social Profiles**: Create and manage profiles with following/follower relationships.
- **Social Interactions**: Share posts, comments, and engage in discussions.
- **Marketplace**: Browse, purchase, and read literary works.
- **Author Tools**: Create and publish literary content.
- **Digital Wallet**: Manage transactions for purchasing content.

## Features

- **Authentication**: Supports email and Google Sign-In with secure token management.
- **Profile Management**: Edit profiles, choose avatars, and manage user settings.
- **Social Features**: Create posts, comment, and interact with other users via Supabase-powered social features.
- **Shop System**: Browse literary works, view categories, and access detailed work information.
- **Wallet System**: Deposit funds and process payments using ZaloPay and VietinBank integrations.
- **Library Tools**: Add works, chapters, and read content within the app.

### System Architecture

BookMoth follows a clean architecture pattern with multiple backend services supporting different functional domains. The application integrates social networking capabilities with e-commerce features for literary content.


<img src="overview/system architecture.png">

### Core Application Flow
The application follows a structured user journey from authentication through core feature usage. The main entry point is `SplashScreenActivity`, with `HomeActivity` serving as the central navigation hub.


<img src="overview/core application flow.png">

## Technology Stack
BookMoth utilizes a modern Android technology stack with clean architecture principles and multiple backend integrations.


<img src="overview/technology stack.png">

### Frontend Technologies
- **Android SDK**: minSdk 24, targetSdk 34
- **UI Framework**: ViewBinding, Material Design, ConstraintLayout
- **Architecture**: MVVM with LiveData and ViewModels
- **Image Processing**: Glide, Picasso, ImageCache
- **Adapters**: RecyclerView with custom adapters (e.g., Work_Adapter, PostAdapter)

### Network & Data
- **HTTP Client**: Retrofit with Gson Converter and OkHttp Logging
- **Local Storage**: Room Database, SQLite (PostSQLiteHelper), SecureStorage
- **Backend Services**:
  - ASP.NET API: Authentication and profile management
  - Shop Server: Literary works and categories
  - Supabase: Posts, comments, and social features
  - Flask API: Content processing and library tools
- **External SDKs**:
  - Google Play Services: OAuth authentication
  - ZaloPay SDK: Payment processing
  - Firebase: Push notifications and cloud messaging

- **Data Flow Patterns**:

  
<img src="overview/data flow pattens.png">

### Architectural Patterns
- **Repository Pattern**: Abstracts data sources (e.g., ShopRepository, ProfileRepositoryImpl)
- **Use Case Pattern**: Encapsulates business logic (e.g., ShopUseCase, ProfileUseCase)
- **Multi-Backend Architecture**: Integrates ASP.NET, Supabase, Shop Server, and Flask APIs
- **Clean Architecture**: Maintains dependency inversion with domain models and repository interfaces



## Usage

- **Sign Up/Login**: Use email or Google Sign-In to authenticate.
- **Profile Setup**: Edit your profile and choose an avatar in `EditProfileActivity`.
- **Explore Content**: Browse literary works in `ShopActivity` or engage in discussions via `CreatePostActivity` and `CommentActivity`.
- **Manage Wallet**: Deposit funds and make purchases in `WalletActivity`.
- **Create Content**: Use `AddWorkActivity` and `AddChapterActivity` to publish your own literary works.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Author
- Mr-1504
- ducvinh101101
- watashiwalds
- Wangamrt

## Contact

For questions or feedback, reach out to the project maintainer:
- GitHub: [Mr-1504](https://github.com/Mr-1504)
- Email: truongvan.minh1504@gmail.com
