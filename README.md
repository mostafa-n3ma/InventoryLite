# Inventory Lite

#### Video Demo: <https://youtu.be/j8Nf5t_UC0w>

#### Description:

Mostafa nema 

used principles :MVVM architecture pattern ,DI Dependencies Injection,Kotlin Coroutines ,DataBinding, ViewBinding,RoomDatabase,BindingAdapters,ExtensionFunctions
As a start, there are main packages to describe in an Android app, which is `(app)`. The other packages are either for managing dependencies and arranging them, such as the `Gradle` package, or self-generated files such as `Idea`, etc.

Starting to describe the `app` package:

The app code core is in the `src` package and it contains three packages:

1. `main`: Main code core and the UI layouts.
2. `android testing`: For applying Android testing operations.
3. `unit testing`: For writing unit testing.

As for now, I didn't have the time to do any testing classes, and I want to catch up with the course to submit before it ends this year. I will start in the `main` package, which is all the code I wrote:

### Main:

Contains two packages:

1. `java`: Logic code written with Kotlin.
2. `res`: XML layouts describing the screens layouts that are connected to the classes such as activities, fragments, and adapters.

#### Java Package:

In package `com.mostafan3ma.android.barcode11`, there are two packages:

1.1. `operations`.
1.2. `presentation`.
1.3. `InventoryLiteApp.kt` class: I am using Hilt Dagger for dependency injection, and it requires an Application Class to hold all the Dagger components and start with it as the base class.
1.4. `MainActivity.kt` class: The main screen which will carry different fragments will pop in and pop out on the `MainActivity` layout.


#### 1.1 Operations Description:

##### **1.1.1 data_Entities:** This section includes all the entities that the app deals with. Entities are separated into three types: Local, Remote, and Domain.

- Local: For saving entities into the database.
- Remote: For sending and retrieving entities from a remote server (not implemented yet).
- Domain: Entities that will be handled by screens and viewModels, so any logic doesn't deal with the database directly. A mapper class is provided to map to and from the entities.

##### **1.1.2 data_Management:** This section contains the logic for the local and remote data sources and a repository class to manage both data sources in one place.

Subsections:

- `data_Management.LocalDataSource.RoomDatabase`: Contains the `shopDao` and `shopDatabase` to generate and manage the database instance using RoomDatabase. RoomDatabase simplifies database management by only requiring the database name, context, and entity classes.

- `data_Management.LocalDataSource.DefaultLocalDataSource`: An interface for the default local database class. This interface is used to define the functions to be implemented in the real local data source. It facilitates testing by allowing the creation of a demo data source that implements the same interface.

- `data_Management.localDatabaseSource.LocalDatabaseSource`: The real data source that deals with the database and handles entity transformations between the database.

- `data_Management.repository.DefaultShopRepository`: An interface for the repository, designed for testing purposes. This allows the creation of a fake repository for testing and injecting it where needed.

- `operations.data_Management.repository.ShopRepository`: A single place that controls operations for both local and remote sources, including functions from different data sources. The local data source and mapper classes are injected into it.

##### **1.1.3 di:** Contains Dagger modules that will be injected into various places. There are two modules for the repository and the database. These modules help Dagger provide instances of complex classes for injection.

##### **1.1.4 utils:** Contains various classes and methods that aid in code development and reduce code duplication.

Subsections:

- `BindingAdapters`: Contains all binding adapters used for two-way data binding and to load images from internal storage and display them in an ImageView.

- `DataState`: A simple sealed class that helps detect data result states, such as error, success, and loading. It provides a quick way to handle different results when fetching data.

- `HideKeyBoard`: An extended method for use in fragments or activities to quickly hide the keyboard.

- `Mapper`: An interface to be extended in all mapper classes.

- `beep`: Contains the `BeepPlayer` class, which plays a beep sound when detecting barcode results on the camera surface.

- `permissionChecker`: An extended function to check permissions and get their results in a fragment using the new registering method and result Launcher, replacing the deprecated `onRequestPermissionsResult`.

- `simpleUtils`: Contains simple functions to get the current date and obtain a unique receipt ID.

- `SuperImageController`: A class to manage images with functions for displaying images from the gallery, saving images to internal storage, retrieving images by name, and deleting images by name.


#### 1.2 Presentation Description:
##### 1.2.1: Adapters

This section contains descriptions of various list adapters used to control and describe the items in the RecyclerViews. Below are short descriptions of each adapter and their unique features:

###### InventoriesAdapter

- `ViewHolder`, `DiffUtil`, and `InventoriesListener` are part of this adapter.
- The `InventoriesListener` class serves as a click listener and provides data information when a single item is clicked.
- The ViewHolder includes a variable passed with binding, like any other ViewHolder.
- An expandable animation is created for the main card, triggered when clicking an item by calling the `checkCardExpendableStatus()` function in the layout.
- The listener variable passes information about the clicked item view with the Adapter Constructor. This information can be used to take actions from the fragment or pass it to the ViewModel for handling.

###### ReceiptAdapter

- This adapter returns information about the clicked item, including the ID, position, and the operator (plus or minus button) that was clicked. This information informs the ViewModel.

###### AnalyticsAdapter

- The `AnalyticsAdapter` is divided into four types, distinguished by letters:
    - `_Q`: Displays quantities of inventories in the Analytics screen, taking items of the `Domain_Transaction` type. A condition in the `bind()` function turns the item background red if the quantity is less than 10.
    - `_S`: Shows inventory sales in the Analytics screen and has a condition to turn the item background gold if sales exceed $1000. It also takes items of the `Domain_Transaction` type.
    - `_E`: Displays expiry dates of inventories in the Analytics screen, taking items of the `Domain_Inventory` type. A condition checks if the expiry date is near to expire and turns the item background red. The `isNearToBeExpired()` function is used to determine this.
    - `_R`: Shows both sales and purchase receipts in the analytics screen and includes an expandable card animation similar to the `_Q` adapter. It displays receipt information such as the receipt ID, date, item list, and total amount.

##### 1.2.2: Fragments

This section provides an overview of classes that control various screens in the app:

###### DashboardFragment

- This is a simple fragment that displays the main dashboard screen with buttons that navigate to other screens.
- The ViewModel is directly injected into it.
- Layout inflation is done through a binding variable.
- The fragment is responsible for handling views and listening to ViewModel events, performing actions like click events and navigation.
- The `subscribeObservers()` function is where LiveData is observed from the ViewModel.

###### InventoriesFragment

- This fragment displays the inventories screen.
- It receives a `superImageController` variable in the constructor.
- The ViewModel is directly injected.
- The `InventoriesAdapter` is declared and initialized in the `onCreateView()` method.
- Layout inflation is done with binding, passing the ViewModel instance to it along with the adapter instance.
- An `ActivityResultLauncher<Array<String>>` named `permissionRequest` is declared for handling permissions requests.
- The `cameraSource` and `detector` are declared for camera operations and barcode detection.
- Various functions include:
    - `onCreateView()`: This is called when creating the main layout view.
    - `setUpBottomSheet()`: Initializes the bottom sheet and sets it to be non-draggable.
    - `onDestroy()`: Releases resources when the fragment is destroyed.
    - `getPermissionsRequest()`: Checks camera permissions.
    - `checkBarcodeScannerCard()`: Manages the barcode scanner card's animation.
    - `setUpBarcodeDetector()`: Initiates the surface callback and barcode detector setup.
    - `setUpCategoriesChips(categoriesList)`: Creates and inflates category chips based on the observed categories list.
    - `subscribeObservers()`: Observes various ViewModel LiveData and events.

###### PurchaseFragment

- This fragment is similar to `InventoriesFragment` but uses a `ReceiptAdapter`.
- Functions include:
    - `onCreateView()`, `setUpBottomSheet()`, `onDestroy()`, `getPermissionsRequest()`, `checkBarcodeScannerCard()`, `setUpBarcodeDetector()`, and `subscribeObservers()` are similar to `InventoriesFragment`.
    - `subscribeObservers()`: Observes LiveData specific to the purchase process.

###### SellFragment

- Similar to `PurchaseFragment` but with differences in ViewModel interaction through click events declared in the fragment layout.
- Functions include:
    - `subscribeObservers()`: Observes LiveData and events, including the `receiptList`, `quantitiesList`, `salesList`, `expiryList`, `sellReceiptsList`, and `purchaseReceiptsList`.

##### 1.2.3: ViewModels

###### DashBoardViewModel

In the `DashBoardViewModel`, I use events triggered in various places and change the LiveData values. This LiveData is observed in the fragment and used to take actions accordingly.

- The ViewModel uses `MutableLiveData` variables, and I change them according to various events, typically using the `setEvent()` function.
- For example, when the inventories button is clicked in the dashboard fragment, it triggers `viewModel.setEvent(inventoriesCArdClicked)`. In this function, I change the variable `_clickInventoriesCard.value = true`, which is observed in the fragment. It is used to navigate to the inventories fragment or perform other actions.
- LiveData isolation is used by having a `MutableLiveData` variable (e.g., `_clickInventoriesCard`) and a regular `LiveData` variable (e.g., `clickInventoriesCard`) that receives its value from the `MutableLiveData` version. This separation ensures that LiveData cannot be changed outside the ViewModel, and the copied version is for observing only.
- The repository is injected into the ViewModel class through the constructor.

The `DashBoardViewModel` is an essential part of an Android application and serves as the ViewModel for the dashboard view. It handles click events and provides LiveData for observing user interactions and UI updates.

###### InventoriesViewModel

The `InventoriesViewModel` is a crucial component for managing inventory-related data and user interactions in the application. It follows Android best practices for ViewModel and LiveData usage and utilizes Kotlin Coroutines for asynchronous operations.

- Dependency Injection: The ViewModel is annotated with `@HiltViewModel` and is injected with a `ShopRepository` instance via the `@Inject` annotation, following the best practice for using Dagger Hilt for dependency injection in Android apps.
- LiveData Properties: The ViewModel defines various LiveData properties to represent different aspects of the inventory view, such as `beep`, `saveNewImg`, `clickable`, and many others.
- Lists and Suggestion LiveData: The ViewModel manages lists like `inventories`, `filteredList`, and provides suggestions for product names.
- Initialization: Upon initialization, the ViewModel sets initial values for various LiveData properties and fetches inventory data.
- Functions: The ViewModel provides functions for handling events, updating inventories, applying filters, and more.
- InventoriesEvents: A sealed class (`InventoriesEvents`) categorizes different events and actions in the inventory view.

The `InventoriesViewModel` is designed to work with Dagger Hilt for dependency injection, following best practices for ViewModel and LiveData usage. It interacts with a repository (`ShopRepository`) to fetch data and offers a clean interface for the inventory view to respond to user interactions and display relevant information.

###### PurchaseViewModel

The `PurchaseViewModel` manages various MutableLiveData objects for storing and observing data, communicates with a `ShopRepository`, and handles user interactions and events in the UI.

- It manages a list of products (`localProductsList`) and calculates the total receipt amount.
- Functions are provided for handling barcode scanning, opening/closing a bottom sheet for product details, and adding products to the list.
- It updates and maintains data about product details in the bottom sheet.
- Various UI-related events are handled, such as showing toasts and playing beeps.

###### SellViewModel

The `SellViewModel` is responsible for managing data, UI interactions, and business logic related to selling products.

- It is injected with a `ShopRepository` for data management.
- LiveData properties are used for data observation, including the total receipt amount, beep events, and more.
- It handles various UI events via the `setEvent` function.
- The ViewModel manages a list of product inventories, updates the receipt list, and calculates the total receipt amount.
- Coroutine usage is employed for asynchronous operations.
- Sealed classes (`SellViewModelEvent`) categorize different UI events.
- An enumeration type (`InputType`) distinguishes between different types of user inputs.

The `SellViewModel` class follows the MVVM architecture, effectively managing the state and business logic for selling products, handling UI interactions, and using LiveData and Coroutines for reactive programming and asynchronous operations.

###### AnalyticsViewModel

The `AnalyticsViewModel` displays analytics and insights related to product inventory and transactions.

- It is injected with a `ShopRepository` for data management.
- LiveData properties are used for data observation, including total expenses, total profit, and flags for tracking UI events.
- UI events are handled via the `setEvent` function.
- The ViewModel fetches and processes data from the repository, including product inventories and transaction records.
- It calculates total expenses and profit, generates lists of receipts, sorts and filters data, and uses sealed classes (`AnalyticsEvent`) to categorize different UI events.

The `AnalyticsViewModel` class follows the MVVM architecture, effectively managing data related to product inventories and transactions, handling UI interactions, and using LiveData and Coroutines for reactive programming and asynchronous data fetching.

##### 1.2.4: FragmentFactory

The `FragmentFactory` is used to create and instantiate fragments dynamically. Here's a summary of the key components and functionality of this `FragmentFactory`:

###### Constructor and Dependency Injection

- The `FragmentFactory` is injected with dependencies, including a `ShopRepository` and a `SuperImageController`. These dependencies are provided for use within the fragments.

###### Fragment Instantiation

- The `FragmentFactory` overrides the `instantiate` method. This method is called by the Android framework to create instances of fragments based on their class names (class names as strings).

###### Fragment Mapping

- Inside the `instantiate` method, there is a `when` expression that maps class names to the corresponding fragment types and creates instances of those fragments.

###### Fragment Types

- The following fragment types are created:
    - `DashboardFragment`: An instance of the `DashboardFragment` is created when its class name is provided.
    - `PurchaseFragment`: An instance of the `PurchaseFragment` is created when its class name is provided.
    - `SellFragment`: An instance of the `SellFragment` is created when its class name is provided.
    - `InventoriesFragment`: An instance of the `InventoriesFragment` is created, and it is injected with the `SuperImageController`.
    - `AnalyticsFragment`: An instance of the `AnalyticsFragment` is created, and it is injected with the `SuperImageController`.

###### Fallback Behavior

- If the provided class name doesn't match any of the predefined fragment types, the `instantiate` method calls the `super.instantiate` method to use the default fragment instantiation behavior provided by the Android framework.

This custom `FragmentFactory` is responsible for creating specific fragment instances based on their class names. It provides flexibility and customization for fragment instantiation and allows the injection of dependencies into fragments that require them. This is a useful approach for managing the creation of fragments in a modular and organized way within an Android application.

