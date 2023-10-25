# Inventory Lite

#### Video Demo: <URL HERE>

#### Description:

As a beginner, there are main packages to describe in an Android app, which is `(app)`. The other packages are either for managing dependencies and arranging them, such as the `Gradle` package, or self-generated files such as `Idea`, etc.

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


1.1 operations description: 
contains the following :-
                        1.1.1 - data_Entities : which includes all the the entities that the app deals with I separate the entities types for Local& Remote % Domain 
                                Local for saving them into database 
                                Remote : fro sending and retrieving them from a remote server ( I didn't implement that yet but planning to do so after submitting the project )
                                Domain : the entity that will be handled by screens and viewModels basely the any logic don't deal with database directly
                                the reason I did that is for in case something wrong happened in one of the dataSources it will not effect the UI data 
                                and I also provided a mapper Class to map from and into the entities
                        1.1.2 - data_Management : which contains the logic for the local and remote data sources and also a repository class to manage both data sources in one place 
                                sub files are :
                               - data_Management.LocalDataSource.RoomDatabase : which contains the shopDao and shopDatabase to generate and database instance and provide controlling function to manage that database
                                 RoomDataBase is really helpful library 
                                 I only need to provide the database name and the required context and the cacheEntities I want to create tables of and it just do the rest
                                 and in the Dao it's an interface annotated with @DAO annotation add  the function with no body's that  I want to operate  on my database some queries and SQLITE commands  
                               - data_Management.LocalDataSource.DefaultLocalDataSource : an interface for the default localDataBase class I made this just to clear the function first that I want to use on the data Source 
                                 then I can extend this interface to the real class of the LocalDataSource 
                                 the reason I did that is for testing reasons so I can create demo dataSource and injected it to the repository later and test the repository ANd since the database cant be 
                                 tested without android classes and libraries so I plan to do fake DataSource that will implement the same DefaultLocalDataSource interface
                               - data_Management.localDatabaseSource.LocalDatabaseSource  the real DataSource that will deal with the database and handle the entities transformation form and into the database
                               - data_Management.repository.DefaultShopRepository : Interface of the repository again for testing reasons so I can make fake repository and inject it where I need
                               - operations.data_Management.repository.ShopRepository: one place that controls the operations of both the local and remote sources complaining functions from all different dataSources
                                  also I am injecting the localDataSource and mapper Classes to it
                        1.1.3 - di : contains the dagger modules that will be injected in many places 
                                     here I have two modules for the repository and for the database 
                                     this helps the dagger to know how to provide complicated classes instances for injection
                        1.1.4 - utils : all kind of classes and methods that would help me get some quick results and shorten the code for me in any class I need it 
                                        - BindingAdapters : contains all the binding adapters that  used to bind views in tow way dataBinding and also to get the image from internal storage and display it in a ImageView 
                                        - DataState : simple Sealed Class helps me to detect result data states such as error , success , Loading a quick way to handle different results when fetching data 
                                        - HideKeyBoard : extended method to use in fragments or activities useful for hiding the keyBoard quickly
                                        - Mapper : mapper interface to be extended in all mappers classes 
                                        - beep : contains the class BeepPlayer to play the beep sound when detecting barcode results on the camera surface
                                        - permissionChecker : just an extended function to check permissions and get their result in a fragment using the new
                                          registering way and result Launcher instead of the old onRequestPermissionsResult fun that is deprecated
                                        - simpleUtils : contains simple function to get the currentDate and getting unique receipt_id
                                        - SuperImageController : A simple class I wrote previously to deal with images
                                          Its functions are:
                                                        - Display the photo selection window from the gallery
                                                        - Give the resulting image that was returned with the selection window closed
                                                        - Save images to the device’s internal storage and within the program files to save them locally and save them under a specific name
                                                        - Retrieve images with a specific name from the internal storage
                                                        - Delete an image with a specific name from the internal storage

1.2 presentation description : 
contains as follows:- 
                    1.2.1:Adapters: which contains the listAdapters that wil control and descripe the items in the recyclerViews their are many adapters classes but I will give some short description for each one of them and the special things of it 
                          - InventoriesAdapter: like any adapter it has ViewHolder ,DiffUtil and I create a class called InventoriesListener which I used like
                                                a click Listener will send some data information whine the single item clicked 
                                                the viewHolder have a variable passed to it with binding like every other viewHolder
                                                but this viewHolder I create for it an expend animation for the main card triggered when 
                                                clicking the item and calling the fun  checkCardExpendableStatus() view in the layout
                                                and back to the listener variable it passing back with the Adapter Constructor information of
                                                the clicked item view so I can make some action from the fragment or pass it to the viewModel to handle it.
                          - ReceiptAdapter: the special thing about this adapter that it's listener return back information about the clicked
                                            item like id ,position and the operator which tells the viewModel that the plus or the minus button have been clicked
                          - AnalyticsAdapter : this Adapter have four types resampled by the letters:
                                               _Q : to show the quantities of the inventories in the Analytics screen take items from the type Domain_Transaction
                                                   I just wrote a condition in the bind() function that will turn the item background to red if the quantity is then 10
                                               _S : to show the inventories sales in the Analytics screen it has condition that turns the item background to gold when the
                                                   sales is more then 1000$ as a sign of good sales (take items in the type of Domain_Transaction)
                                               _E : to show the expiry dates of inventories in the Analytics screen it takes items in the type Domain_Inventory and has a condition
                                                   if the expiry date is near to expire turns the item background to red and I am using the fun  isNearToBeExpired() that returning true if the expiry date is near to expire 
                                               _R : to show both sales & purchases receipts in the analytics screen and also have expendable card animation like _Q adapter 
                                                   showing the receipt information like the receipt id ,date,items list and the total amount 
                    1.2.2:Fragments : the classes that controls the screens of the app 
                                      - DashboardFragment : simple fragment that display the main dashboard screen with the buttons that navigate to the other screens 
                                                            the viewModel is injected to it directly and the layout is inflated by binding variable
                                                            viewModel is passed to the layout by the viewBinding principle with the viewLifeCycle to match the view context with the fragment context 
                                                            the fragment controls only the views and do not make any logic it's only listens to the viewModels events and make actions on the views such as click events and navigation directions 
                                                            the fun subscribeObservers() is where the liveData observed from the viewModel 
                                      - InventoriesFragment : this fragment displays the inventories screen 
                                                              a variable of superImageController is injected to it in the constructor 
                                                              the viewModel also injected directly 
                                                              inventoriesAdapter is declared latent and will be initiated in the onCreateView()
                                                              binding is inflated and passing the viewModel instance to it along with the adapter instance 
                                                              permissionRequest is declared as ActivityResultLauncher<Array<String>> to be used latter in the getPermissions fun 
                                                              cameraSource & detector is declared to be used in opening camera and detecting barcodes used in  setUpBarcodeDetector()
                                                              functions : 
                                                              - onCreateView(): this is one of the super class Fragment functions represents one of the fragment lifecycle status it will be overwritten and it's called when creating the main layout view
                                                                                  like I sade profusely inflating the binding layout by layoutInflater and passing the viewModel instance setting the lifecycleOwner on from the fragment 
                                                                                  initiating the inventoriesAdapter and detecting the listeners returning data it has two listener ( clickListener & longClickListener) both returning the clicked item id and position 
                                                                                  the clickListener have no use right now but in the longListener I am triggering a viewModel event to opened the bottomSheet with the clickedItem data that will be observed in fragment
                                                                                  but getting the actual data from the viewModel 
                                                                                  then passing the adapter instance to the binding layout 
                                                                                  initiating the beepPlayer instance to used later to play beep when detecting barcodes
                                                                                  launching the permissions request by initiating the permissionRequest instance 
                                                                                  calling subscribeObservers() that will listen to the viewModel event and live data and preform actions according to their values 
                                                                                  and last thing onCreateView must return the layout view that wil be created in this fragment will be the inventories layout and since I am using the binding instance I will return binding.root 
                                                              - setUpBottomSheet() : just fun to initiate the bottomSheetBehavior inflating it from the bottomSheet layout and setting it's draggable to false so it's not gona close when dragging it down only 
                                                                                     when the viewModel event CloseBottomSheet is triggered
                                                              - onDestroy() : this also is one of the super class Fragment functions represents one of the fragment lifecycle status it will be overwritten
                                                                              this function called when destroying the fragment layout and popping the fragment out from the stack 
                                                                              I used it to release the beepPlayer instance to release the mediaPlayer resources and avoid memory leaks
                                                              - getPermissionsRequest() : to check the permissions if is granted or not 
                                                                                          in this fragment we need the Camera permission to open and use the device camera for detecting barcodes 
                                                                                          in this fun I called the function registerForActivityResult which is from the super class Fragment it's new method instead of onRequestPermissionsResult() which is deprecated  
                                                                                          the idea is to check the permission every time the user clicks the barcode button and request to use the camera for detecting the barcode so if the permission is granted setUp 
                                                                                          the barcode detector and expend the barcode card/ if not granted announce a toast message to the user to grant the permission if he want to use the camera
                                                              - checkBarcodeScannerCard() : this fun called when viewModel event is triggered when the button is clicked it simply check the cardView if not expended then play the animation 
                                                                                            and expend the card by converting the card layout visibility to vbe visible the opposite when its expended collapse the card View by turning the visibility back to gone and play the animation 
                                                              - setUpBarcodeDetector() : I collected the in which initiating the surfaceCallBack and the detector possessor in one place 
                                                                                         setting the surfaceCallBack will intuit the camera surface and open the camera in the cardView
                                                                                         initiating the possessor will connect a barCode detecting possessor to possess the images coming from the camera and determine if it valid barcode or not  
                                                                                         this will be explained in the InventoriesViewModel class as it part of the events flow 
                                                              - setUpCategoriesChips(categoriesList) : when fetching the Inventories List from the database and filtering the categories list as liveData in the InventoriesViewModel 
                                                                                                       the categories list will be observed in the fragment in the function subscribeObservers and then it will call setUpCategoriesChips() this method is for creating the 
                                                                                                       categories chips after passing the categoriesList to it the chips are created and inflated inside a ChipGroup view   
                                                                                                       in the same fun I am mapping through the categories list and inflating each chip by the categories names 
                                                                                                       setting the chip name and tag , setting a clickListener on each chip to trigger an event when it checked to filter the inventories list according to the checked categoryChip
                                                                                                       and also changing the checked chip background color , the event is from the Inventories events (FilterInventories) it will be explained in the viewModel paragraph bellow
                                                              - subscribeObservers() : the place where all the viewModel liveData and events while be observed : 
                                                                                       - categoriesList : it's liveData fetched inside the viewModel and posted their and observed in the fragment passing it to the setUpCategoriesChips() 
                                                                                       - filteredList : the inventories list filtered with checked categories from the viewModel 
                                                                                                        when the event(FilterInventories(checkedCategory name)) the viewModel while filter the list and edit the liveData posting new value
                                                                                       - namesSuggestions : MutableList<String> contains the all the inventories names observed to be passed to an arrayAdapter that while set to the autoComplete editText 
                                                                                                            as suggestions displayed when trying to write an inventory name in the search auto edt text
                                                                                       - barcodeBtnClicked : clickEvent triggered when clicking the barcode button and navigate checking the camera permission and runs the cardView animation then 
                                                                                                             displaying the barcode detector.
                                                                                       - backBtnClicked : clickEvent triggered when clicking the ck button  and the fragment navigate back to the dashboard fragment 
                                                                                       - editBottomSheetOpened : event triggered when requesting to open the bottom Sheet 
                                                                                       - clickable : when the bottom sheet opened the clicks in the background views still works so I made this event to stop the clicks when opening the bottom sheet 
                                                                                       - hideKeyBoardRequired : event triggered when required to hide the keyBoard 
                                                                                       - openImgChooser : event to request launching the image picker from the superImageController class 
                                                                                       - returnedBottomImgUri : when the image is chosen and the image chooser taking the user back to the app the result image uri while be in the onResume() and their I am sending it back to the viewModel 
                                                                                                                then will be requested to save the image to the internal storage when editing an inventory item 
                                                                                       - saveNewImg: event triggered when requesting to save new image the uri is saved in the viewModel so call supperImageController to handle that 
                                                                                       - beep : event to call beepPlayer to play the beep sound 
                                                              - onDestroy() : override fun from the supper class Fragment where I am releasing the beepPlayer to prevent memory leak 
                                      - PurchaseFragment : some of the initiations is the same as inventoriesFragment except the productsAdapter is from the type ReceiptAdapter the rest (binding,viewModel,bottomSheet,
                                                           beepPlayer,permissions,cameraSource,detector) are the same
                                                           functions : 
                                                           - onCreateView() & onDestroy() & setUpBottomSheet() & getPermissionsRequest() & checkBarcodeScannerCard() & setUpBarcodeDetector() just like the InventoriesFragment
                                                           - subscribeObservers() : almost the same but here are the different : 
                                                                                    - toast_msg : a String LiveData posted from the viewModel to be a message in a toast in the fragment 
                                                                                    - localProductsList : liveData<List<Domain_Inventories>> posted and updated from the viewModel when an item is added to be purchased 
                                                                                      this will be passed to the productsAdapter
                                                                                    - clickDoneBtn : event triggered when the done button is clicked to till the viewModel save purchasing transaction and update the products quantities in the database 
                                                                                    - notifyPosition : event triggered when updating a single item by the plus or minus buttons and to be used to notify the adapter itemSetChanged with the position coming from the liveData
                                                                                    - bottom_P_total_items_quantity : updated liveData of the total receipt amount that will be displayed in the screen
                                      - SellFragment : some of the initiations is the same as inventoriesFragment except the productsAdapter is from the type ReceiptAdapter the rest (binding,viewModel,bottomSheet,
                                                       beepPlayer,permissions,cameraSource,detector) are the same
                                                       in this fragment i did something different with viewBinding I declared the viewModel click events in th fragment layout and assigned them in the fragment from the onCreateView() 
                                                       where I call declareClickEvents() , in the layout I used dataBinding on the button to call the viewModel function setEvent(passing the related event) like android:onClick = "@{viewModel.setEvent(ClickBackButton)}"
                                                       functions : I will mention only the difference the rest is just the same as purchaseFragment
                                                       - subscribeObservers :  - receiptList is the items add to the sell receipt will be submitted to the adapter 
                                                                               - quantitiesList : list of inventories sorted by their quantities to displayed in the quantities cardView
                                                                               - salesList : list of transactions of type sell sorted from the highest to be displayed in the sales cardView
                                                                               - expiryList : list of inventories sorted by their expiry date to be displayed in the expiry cardView
                                                                               - sellReceiptsList  : list of transactions of the type SELL to be displayed in the sell receipts cardView
                                                                               - purchaseReceiptsList : list of transactions of the type PURCHASE to be displayed in the purchase receipts cardView
                    1.2.3 : viewModels :
                                        - DashBoardViewModel : in the viewModel I usually use events triggered in varies places and change the liveData values so it's will be observed in the fragment and do action according to that 
                                                               * so in this viewModel I used MutableLiveData variables and chang them according to their events and I am using setEvent() for that   
                                                                 for example If the inventories button clicked in the dashBoard fragment that will trigger viewModel.setEvent(inventoriesCArdClicked) 
                                                                 in the viewModel for example in that fun I will change the variable _clickInventoriesCard.value = true this will be observed in the fragment and it will use navigator to navigate to that fragment
                                                               * also I am using liveData isolation Like the variable _clickInventoriesCard which is MutableLiveData<Boolean>() and a regular liveData<Boolean>  in the variable 
                                                                 clickInventoriesCard how will receive it value from the MutableLiveData version this will give me kind of safety so that the live data cannot be changed outside
                                                                 the viewModel and the copy version will be for observing only
                                                               - the repository is injected to the viewModel class through the constructor
                                                               The DashboardViewModel is part of an Android application and serves as the ViewModel
                                                               for the dashboard view. It is designed to handle user interactions and provide data for the dashboard's UI components.
                                                               Click Events:- 
                                                                             This ViewModel handles several click events, including:
                                                                             clickInventoryCard(): Clicking on the inventory card.
                                                                             clickAddProductsCard(): Clicking on the add products card.
                                                                             clickSellReceiptCard(): Clicking on the sell receipt card.
                                                                             clickAnalyticsCard(): Clicking on the analytics card.
                                                                             clickTodaySalesCard(): Clicking on the today's sales card.
                                                                             clickRunOffProductsCard(): Clicking on the run-off products card.
                                                               LiveData:-
                                                                         The ViewModel provides LiveData objects for each click event. These LiveData objects are observed by the UI components to react to user interactions.
                                                                         Initialization:-
                                                                         Upon initialization, the ViewModel initializes the LiveData objects for click events and sets some initial values.
                                                                         It also updates the information about sold-out products and today's sales.
                                                               Functions:-
                                                                          - updateSoldOutProduct(): This function retrieves a list of inventories and updates the soldOutProduct LiveData with the 
                                                                                                    names of products that are sold out. it uses Kotlin Coroutines feature specifically viewModelScope.launch and 
                                                                                                    onEach, to asynchronously fetch data from suspended functions in the repository,viewModelScope.launch: This is 
                                                                                                    used to launch a new coroutine within the viewModelScope. The viewModelScope is a special scope provided by the 
                                                                                                    Android Architecture Components and is typically associated with the ViewModel's lifecycle. It ensures that the
                                                                                                    coroutine is automatically canceled when the ViewModel is cleared, which helps prevent memory leaks.
                                                                                                    repository.get_Inventories() is a suspend fun from the repository ,Suspending functions are used for asynchronous
                                                                                                    operations , such as querying databases. The use of suspend allows the functions to be called from a coroutine without
                                                                                                    blocking the main thread.
                                                                                                    onEach: This is an operator used with Kotlin Flow, which is a type of asynchronous stream of data. onEach allows me to perform
                                                                                                    some action for each emitted item in the Flow. In this case, it's used to observe the data emitted by the repository's suspended functions.
                                                                                                    Handling Different DataStates: Inside the onEach block, you handle different DataState objects that are emitted by the repository function:
                                                                                                    DataState.Error: If there's an error during data retrieval, you log the error message, which can be useful for debugging and error reporting.
                                                                                                    DataState.Loading: You log a message indicating that data is currently loading. This can be helpful to provide feedback to the user or for debugging purposes.
                                                                                                    DataState.Success: If data retrieval is successful, you extract the data from the DataState object and perform the necessary processing. For example,
                                                                                                                       in updateSoldOutProduct(), you check if any of the products are sold out, and in updateTodaySales(), you calculate today's sales.
                                                                                                    By using viewModelScope.launch and onEach, I ensure that these data retrieval operations are performed asynchronously in the background, which prevents them from 
                                                                                                    blocking the main thread and keeps the app responsive. Additionally, the use of Kotlin Flow and DataState allows for handling various scenarios, including errors 
                                                                                                    and loading states, in a structured and clean way.
                                                                          - updateTodaySales(): This function calculates today's sales by retrieving a list of transactions and summing up the transaction amounts for sales made today. 
                                                                                                it uses the same principal as updateSoldOutProduct()
                                                                          - setDashBoardEvent(event: DashboardViewModelEvents): This function handles click events and updates the corresponding LiveData objects.
                                                               DashboardViewModelEvents:- 
                                                                                         a sealed class, to represent the various click events. These events are objects within the sealed class 
                                                                                         and are used to distinguish between different user interactions.
                                                                                         This ViewModel is designed to work with Dagger Hilt for dependency injection and is part of an Android architecture following best practices for ViewModel and
                                                                                         LiveData usage. It interacts with a repository (ShopRepository) to fetch data and provides a clean interface for the dashboard view to respond to user interactions
                                                                                         and display relevant information.
                                        - InventoriesViewModel :-
                                                                 it is a crucial component for managing inventory-related data and user interactions in the application and follows Android best practices for ViewModel and LiveData usage. It also utilizes
                                                                 Kotlin Coroutines for asynchronous operations and provides a structured approach to handling different user events and states.
                                                                 - Dependency Injection: The ViewModel is annotated with @HiltViewModel and is injected with a ShopRepository instance via the @Inject annotation. This is a common practice for using Dagger
                                                                   Hilt for dependency injection in Android apps.
                                                                 - Companion Object: The ViewModel includes a companion object with a constant TAG for logging purposes.
                                                                   Dependency Injection: The ViewModel is annotated with @HiltViewModel and is injected with a ShopRepository instance via the @Inject annotation.
                                                                   This is a common practice for using Dagger Hilt for dependency injection in Android apps.
                                                                 - Companion Object: The ViewModel includes a companion object with a constant TAG for logging purposes.
                                                                 - LiveData Properties : The ViewModel defines various LiveData properties, each representing a different aspect of the inventory view:
                                                                                         - beep: A boolean LiveData property used to play a beep sound.
                                                                                         -saveNewImg: A LiveData property holding a string, typically an image URL or path, representing a new image.
                                                                                         -returnedBottomImgUri: LiveData property holding a Uri representing a returned image URI.
                                                                                         -openImgChooser: A boolean LiveData property used to trigger the opening of the image chooser from superImageController class.
                                                                                         -clickable: A boolean LiveData property indicating whether UI elements are clickable.
                                                                                         -editBottomSheetOpened: A LiveData property used to track the visibility of an edit bottom sheet.
                                                                                         -detectorOpened: A boolean LiveData property to control the state of the barcode detector.
                                                                                         -barcodeBtnClicked: A boolean LiveData property used to handle barcode button clicks.
                                                                                         -hideKeyBoardRequired: A LiveData property to hide the keyboard.
                                                                                         -backBtnClicked: A boolean LiveData property to handle back button clicks.
                                                                                         Various LiveData properties to hold values for inventory item details, such as product name, image, purchase and selling prices,
                                                                                         barcode, category, description, quantity, dates, etc.
                                                                 - Lists and Suggestion LiveData: 
                                                                                                 - inventories: A list of Domain_Inventory objects representing inventory items to be saved locally.
                                                                                                 - filteredList: A LiveData property used to hold a filtered list of inventory items.
                                                                                                 - categoriesList: A LiveData property to store a list of unique categories in the inventory.
                                                                                                 - namesSuggestions: A LiveData property to provide suggestions for product names to be submitted latter to the autoComplete editText
                                                                 - Initialization :
                                                                                    In the init block, initial values are set for various LiveData properties. The updateInventories() function is called to populate the inventory data.
                                                                 - Functions: 
                                                                             - setEvent(events: InventoriesEvents): This function handles different events by updating the LiveData properties.
                                                                                                                    Events include button clicks, filter changes, and more.
                                                                             - updateInventories(): This function uses Kotlin Coroutines to fetch inventory data from a repository. It observes a 
                                                                                                    DataState Flow and handles loading, success, and error states. It also updates LiveData properties with inventory and category data.
                                                                             - setFilter(input: String, filterType: FilterType): This function filters the inventory based on user input and filter type.
                                                                             - getProductObject(id: Int): Retrieves a specific Domain_Inventory object from the list of inventories.
                                                                             - clickEditBtn(): Triggers an edit button click event.
                                                                             - editProduct(product: Domain_Inventory): Edits an inventory item and updates the repository.
                                                                             - loadToBottomFields(product: Domain_Inventory): Populates the LiveData properties with details of an inventory item for editing.
                                                                             - emptyBottomFields(): Clears the LiveData properties for editing.
                                                                             - calculateProductDataFromBottomFields(): Constructs a Domain_Inventory object from the data provided in the LiveData properties. in which they are from the bottomSheet fields
                                                                             - isBarcodeAvailable(code: String): Checks if a barcode is available in the inventory.
                                                                 - InventoriesEvents:
                                                                                     a sealed class, InventoriesEvents, to represent different events and actions that can occur in the inventory view. These events include button clicks, filtering, enabling/disabling UI elements, and more.
                                                                 - Enum: FilterType
                                                                 - The FilterType enum represents the type of filters used in inventory filtering, including CATEGORY, NAME, BARCODE, and NOTHING.
                                        - PurchaseViewModel :a summary of what this ViewModel does:
                                                             - It contains various MutableLiveData objects to store and observe data.
                                                             - It communicates with a ShopRepository to fetch and manage data related to inventory and transactions.
                                                             - It handles user interactions and events triggered in the UI.
                                                             - It manages a list of products (localProductsList) and calculates the total receipt amount.
                                                             - It has functions for handling barcode scanning, opening and closing a bottom sheet for product details, and adding products to the list.
                                                             - It updates and maintains data about product details in the bottom sheet.
                                                             - It handles various UI-related events, such as showing toasts and playing beeps.
                                        - SellViewModel : It is responsible for managing data, UI interactions, and business logic related to selling products. Here's an overview of its key components and functionality:
                                                          - Constructor and Dependency Injection:
                                                                                                 The ViewModel is injected with a dependency, the ShopRepository, which is used for data management. This follows the dependency injection pattern.  
                                                          - LiveData for Data Observation:
                                                                                          It uses LiveData objects to store and observe data, including the total receipt amount (receipt_total), beep events (beep), detector status 
                                                                                          (detectorOpened), barcode button clicks (barcodeBtnClicked), and more.
                                                          - Event Handling:
                                                                            It handles various UI events using the setEvent function. Events include clicking the back button, barcode button, and done button, updating the receipt list,
                                                                            hiding the keyboard, announcing toasts, and more.
                                                          - Data Initialization:
                                                                                The ViewModel initializes various properties, LiveData objects, and lists.
                                                                                It also fetches data from the ShopRepository during initialization, populating inventories and namesSuggestions LiveData objects.
                                                          - Business Logic and Data Management:
                                                                                                It manages a list of product inventories (inventories) and a list of items in the current receipt (_receiptList).
                                                                                                It provides functions to update the receipt list, calculate the total receipt amount, and manage individual product quantities in the receipt.
                                                                                                The done function finalizes a purchase transaction by updating inventories and recording a transaction.
                                                          - Coroutine Usage:
                                                                            The ViewModel uses Kotlin Coroutines for handling asynchronous operations, including data fetching and processing.
                                                          - Sealed Classes for Event Types:
                                                                                           It uses sealed classes (SellViewModelEvent) to categorize different types of UI events and handle them accordingly. Events trigger specific behaviors in the ViewModel.                           
                                                          - Enumeration Types:
                                                                              The ViewModel defines an enumeration type (InputType) for distinguishing between different types of user inputs (text and barcode).
                                                          the SellViewModel class follows the MVVM (Model-View-ViewModel) architecture commonly used in Android app development. It effectively manages the state and business
                                                          logic for selling products, handling various UI interactions and events,and uses LiveData and Coroutines for reactive programming and asynchronous operations.
                                        - AnalyticsViewModel : responsible for displaying analytics and insights related to product inventory and transactions. Here's an overview of its key components and functionality:
                                                              - Constructor and Dependency Injection:
                                                                The ViewModel is injected with a dependency, the ShopRepository, which is used for data management. This follows the dependency injection pattern.
                                                              - LiveData for Data Observation:
                                                                                              It uses LiveData objects to store and observe various data, including total expenses (totalExpenses), total profit (totalProfit),
                                                                                              and various flags to track UI events (e.g., backBtnClicked, quantitiesCardClicked, etc.).
                                                              - Event Handling:
                                                                               It handles various UI events using the setEvent function, such as clicking different cards and buttons to trigger specific UI actions.
                                                              - Data Initialization:
                                                                                    The ViewModel initializes LiveData objects, flags, and some properties.It initiates the fetching of data through the fetchData function.
                                                              - Data Fetching and Processing:
                                                                                             It uses Kotlin Coroutines to fetch data asynchronously from the ShopRepository.
                                                                                             The ViewModel fetches and processes two types of data: product inventories and transaction records.
                                                              - Update Total Expenses and Profit:
                                                                                                 It calculates the total expenses and profit based on the transaction records. Expenses are calculated
                                                                                                 from "PURCHASE" transactions, while profit is calculated from "SELL" transactions.
                                                              - Generate Receipt Lists:
                                                                                       It generates lists of receipts by grouping transactions based on transaction type ("SELL" or "PURCHASE"). The transactions within a receipt
                                                                                       are grouped together into ReceiptItem objects.
                                                              - Sort and Filter Data:
                                                                                     It sorts and filters the data to generate various insights, such as the list of products by quantities (quantitiesList), sales records (salesList),
                                                                                     and expiring products (expiryList).
                                                              - Sealed Classes for Event Types:
                                                                                               It uses sealed classes (AnalyticsEvent) to categorize different types of UI events and handle them accordingly. These events trigger specific
                                                                                               behaviors in the ViewModel.
                                                              - Enumeration Types:
                                                                                  The ViewModel defines an enumeration type (TRANSACTION_TYPE) for distinguishing between different types of transactions (SELL and PURCHASE).
                                                              the AnalyticsViewModel class follows the MVVM (Model-View-ViewModel) architecture commonly used in Android app development. It effectively manages data related to product inventories
                                                              and transactions, handles various UI interactions, and uses LiveData and Coroutines for reactive programming and asynchronous data fetching. It also calculates and presents insights 
                                                              related to expenses, profit, product quantities, sales records, and expiring products.
                    1.2.4:FragmentFactory:
                                          used to create and instantiate fragments dynamically. Here's a summary of the key components and functionality of this FragmentFactory:
                                          - Constructor and Dependency Injection:
                                                                                 The FragmentFactory is injected with dependencies, including a ShopRepository and a SuperImageController. These dependencies are provided for use 
                                                                                 within the fragments.
                                          - Fragment Instantiation:
                                                                   The FragmentFactory overrides the instantiate method. This method is called by the Android framework to create instances of fragments based on their class names
                                                                   (class names as strings).
                                          - Fragment Mapping:
                                                             Inside the instantiate method, there is a when expression that maps class names to the corresponding fragment types and creates instances of those fragments.
                                          - Fragment Types:
                                                           The following fragment types are created:
                                                                                                    - DashboardFragment: An instance of the DashboardFragment is created when its class name is provided.
                                                                                                    - PurchaseFragment: An instance of the PurchaseFragment is created when its class name is provided.
                                                                                                    - SellFragment: An instance of the SellFragment is created when its class name is provided.
                                                                                                    - InventoriesFragment: An instance of the InventoriesFragment is created, and it is injected with the SuperImageController.
                                                                                                    - AnalyticsFragment: An instance of the AnalyticsFragment is created, and it is injected with the SuperImageController.
                                          - Fallback Behavior:
                                                              If the provided class name doesn't match any of the predefined fragment types, the instantiate method calls the super.instantiate method to use the default 
                                                              fragment instantiation behavior provided by the Android framework.
                                          this custom FragmentFactory is responsible for creating specific fragment instances based on their class names. It provides flexibility and customization for fragment instantiation
                                          and allows the injection of dependencies into fragments that require them. This is a useful approach for managing the creation of fragments in a modular and organized way within an 
                                          Android application.
