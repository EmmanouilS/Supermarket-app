package com.example.supermarketapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.supermarketapp.data.Product
import com.example.supermarketapp.data.ProductCategory

enum class Language {
    ENGLISH, GREEK
}

data class LocalizedStrings(
    val language: Language,
    val appName: String,
    val catalog: String,
    val shoppingList: String,
    val wishList: String,
    val history: String,
    val search: String,
    val filter: String,
    val addToCart: String,
    val addToWishList: String,
    val remove: String,
    val quantity: String,
    val price: String,
    val total: String,
    val checkout: String,
    val clear: String,
    val save: String,
    val cancel: String,
    val edit: String,
    val delete: String,
    val confirm: String,
    val description: String,
    val ingredients: String,
    val nutritionalInfo: String,
    val calories: String,
    val protein: String,
    val carbohydrates: String,
    val fat: String,
    val fiber: String,
    val sugar: String,
    val sodium: String,
    val availability: String,
    val inStock: String,
    val outOfStock: String,
    val discount: String,
    val originalPrice: String,
    val validUntil: String,
    val categories: String,
    val allCategories: String,
    val offers: String,
    val noOffers: String,
    val emptyCart: String,
    val emptyWishList: String,
    val emptyHistory: String,
    val noResults: String,
    val loading: String,
    val error: String,
    val retry: String,
    val settings: String,
    val languageLabel: String,
    val darkMode: String,
    val about: String,
    val version: String,
    val orderDetails: String,
    val repeatOrder: String,
    val confirmOrder: String,
    val continueShopping: String,
    val confirmOrderMessage: String,
    val yes: String,
    val no: String,
    val appVersion: String,
    val appDescription: String,
    val features: String,
    val featureCatalog: String,
    val featureSearch: String,
    val featureShoppingList: String,
    val featureWishList: String,
    val featureHistory: String,
    val featureOffers: String,
    val featureMultiLanguage: String,
    val featureNutrition: String
)

class LanguageState {
    var currentLanguage by mutableStateOf(Language.ENGLISH)
        private set
    
    fun setLanguage(language: Language) {
        currentLanguage = language
    }
}

object LocalizationManager {
    private val languageState = LanguageState()
    var currentLanguage: Language
        get() = languageState.currentLanguage
        set(value) = languageState.setLanguage(value)
    
    // Cache for localized strings to avoid repeated lookups
    private val localizedStringsCache = mutableMapOf<Language, LocalizedStrings>()
    
    // Cache for product names and descriptions
    private val productNameCache = mutableMapOf<Pair<String, Language>, String>()
    private val productDescriptionCache = mutableMapOf<Pair<String, Language>, String>()
    private val productIngredientsCache = mutableMapOf<Pair<String, Language>, String>()
    private val categoryNameCache = mutableMapOf<Pair<ProductCategory, Language>, String>()

    fun setLanguage(language: Language) {
        languageState.setLanguage(language)
        // Clear caches when language changes
        localizedStringsCache.clear()
        productNameCache.clear()
        productDescriptionCache.clear()
        productIngredientsCache.clear()
        categoryNameCache.clear()
    }

    @Composable
    fun getLocalizedStrings(): LocalizedStrings {
        return localizedStringsCache.getOrPut(currentLanguage) {
            when (currentLanguage) {
                Language.ENGLISH -> englishStrings
                Language.GREEK -> greekStrings
            }
        }
    }

    fun getLocalizedProductName(product: Product): String {
        val cacheKey = product.id to currentLanguage
        return productNameCache.getOrPut(cacheKey) {
            when (currentLanguage) {
                Language.ENGLISH -> product.name
                Language.GREEK -> product.nameGreek
            }
        }
    }

    fun getLocalizedProductDescription(product: Product): String {
        val cacheKey = product.id to currentLanguage
        return productDescriptionCache.getOrPut(cacheKey) {
            when (currentLanguage) {
                Language.ENGLISH -> product.description
                Language.GREEK -> product.descriptionGreek
            }
        }
    }

    fun getLocalizedProductIngredients(product: Product): String {
        val cacheKey = product.id to currentLanguage
        return productIngredientsCache.getOrPut(cacheKey) {
            when (currentLanguage) {
                Language.ENGLISH -> product.ingredients
                Language.GREEK -> product.ingredientsGreek
            }
        }
    }

    fun getLocalizedCategoryName(category: ProductCategory): String {
        val cacheKey = category to currentLanguage
        return categoryNameCache.getOrPut(cacheKey) {
            when (currentLanguage) {
                Language.ENGLISH -> when (category) {
                    ProductCategory.FRESH_FOOD -> "Fresh Food"
                    ProductCategory.DAIRY -> "Dairy"
                    ProductCategory.FROZEN -> "Frozen"
                    ProductCategory.CLEANING -> "Cleaning"
                    ProductCategory.BEVERAGES -> "Beverages"
                    ProductCategory.SNACKS -> "Snacks"
                    ProductCategory.BAKERY -> "Bakery"
                    ProductCategory.CANNED -> "Canned"
                    ProductCategory.SPICES -> "Spices"
                    ProductCategory.PERSONAL_CARE -> "Personal Care"
                }
                Language.GREEK -> when (category) {
                    ProductCategory.FRESH_FOOD -> "Φρέσκα Τρόφιμα"
                    ProductCategory.DAIRY -> "Γαλακτοκομικά"
                    ProductCategory.FROZEN -> "Κατεψυγμένα"
                    ProductCategory.CLEANING -> "Καθαριστικά"
                    ProductCategory.BEVERAGES -> "Αναψυκτικά"
                    ProductCategory.SNACKS -> "Σνακ"
                    ProductCategory.BAKERY -> "Αρτοποιία"
                    ProductCategory.CANNED -> "Κονσέρβες"
                    ProductCategory.SPICES -> "Μπαχαρικά"
                    ProductCategory.PERSONAL_CARE -> "Προσωπική Φροντίδα"
                }
            }
        }
    }

    val englishStrings = LocalizedStrings(
        language = Language.ENGLISH,
        appName = "Supermarket App",
        catalog = "Home",
        shoppingList = "Cart",
        wishList = "Wish List",
        history = "History",
        search = "Search",
        filter = "Filter",
        addToCart = "Add to Cart",
        addToWishList = "Add to Wish List",
        remove = "Remove",
        quantity = "Quantity",
        price = "Price",
        total = "Total",
        checkout = "Checkout",
        clear = "Clear",
        save = "Save",
        cancel = "Cancel",
        edit = "Edit",
        delete = "Delete",
        confirm = "Confirm",
        description = "Description",
        ingredients = "Ingredients",
        nutritionalInfo = "Nutritional Information",
        calories = "Calories",
        protein = "Protein",
        carbohydrates = "Carbohydrates",
        fat = "Fat",
        fiber = "Fiber",
        sugar = "Sugar",
        sodium = "Sodium",
        availability = "Availability",
        inStock = "In Stock",
        outOfStock = "Out of Stock",
        discount = "Discount",
        originalPrice = "Original Price",
        validUntil = "Valid Until",
        categories = "Categories",
        allCategories = "All Categories",
        offers = "Offers",
        noOffers = "No offers available",
        emptyCart = "Your shopping cart is empty",
        emptyWishList = "Your wish list is empty",
        emptyHistory = "No purchase history",
        noResults = "No results found",
        loading = "Loading...",
        error = "An error occurred",
        retry = "Retry",
        settings = "Settings",
        languageLabel = "Language",
        darkMode = "Dark Mode",
        about = "About",
        version = "Version",
        orderDetails = "Order Details",
        repeatOrder = "Repeat Order",
        confirmOrder = "Confirm Order",
        continueShopping = "Continue Shopping",
        confirmOrderMessage = "Are you sure you want to place this order?",
        yes = "Yes",
        no = "No",
        appVersion = "Supermarket App v1.0",
        appDescription = "A comprehensive shopping app for managing your supermarket purchases",
        features = "Features",
        featureCatalog = "Product Catalog with Categories",
        featureSearch = "Search and Filter Products",
        featureShoppingList = "Shopping List Management",
        featureWishList = "Wish List for Future Purchases",
        featureHistory = "Purchase History Tracking",
        featureOffers = "Discount and Offer Display",
        featureMultiLanguage = "Multi-language Support (EN/GR)",
        featureNutrition = "Nutritional Information"
    )

    private val greekStrings = LocalizedStrings(
        language = Language.GREEK,
        appName = "Εφαρμογή Σούπερ Μαρκετ",
        catalog = "Αρχική",
        shoppingList = "Καλάθι",
        wishList = "Αγαπημένα",
        history = "Ιστορικό",
        search = "Αναζήτηση",
        filter = "Φίλτρο",
        addToCart = "Προσθήκη στο Καλάθι",
        addToWishList = "Προσθήκη στη Λίστα Επιθυμιών",
        remove = "Αφαίρεση",
        quantity = "Ποσότητα",
        price = "Τιμή",
        total = "Σύνολο",
        checkout = "Ολοκλήρωση",
        clear = "Καθαρισμός",
        save = "Αποθήκευση",
        cancel = "Ακύρωση",
        edit = "Επεξεργασία",
        delete = "Διαγραφή",
        confirm = "Επιβεβαίωση",
        description = "Περιγραφή",
        ingredients = "Συστατικά",
        nutritionalInfo = "Θρεπτικές Πληροφορίες",
        calories = "Θερμίδες",
        protein = "Πρωτεΐνη",
        carbohydrates = "Υδατάνθρακες",
        fat = "Λίπος",
        fiber = "Φυτικές ίνες",
        sugar = "Ζάχαρη",
        sodium = "Νάτριο",
        availability = "Διαθεσιμότητα",
        inStock = "Διαθέσιμο",
        outOfStock = "Εξαντλημένο",
        discount = "Έκπτωση",
        originalPrice = "Αρχική Τιμή",
        validUntil = "Ισχύει Μέχρι",
        categories = "Κατηγορίες",
        allCategories = "Όλες οι Κατηγορίες",
        offers = "Προσφορές",
        noOffers = "Δεν υπάρχουν προσφορές",
        emptyCart = "Το καλάθι σας είναι άδειο",
        emptyWishList = "Η λίστα επιθυμιών σας είναι άδεια",
        emptyHistory = "Δεν υπάρχει ιστορικό αγορών",
        noResults = "Δεν βρέθηκαν αποτελέσματα",
        loading = "Φόρτωση...",
        error = "Προέκυψε σφάλμα",
        retry = "Επανάληψη",
        settings = "Ρυθμίσεις",
        languageLabel = "Γλώσσα",
        darkMode = "Σκοτεινό Θέμα",
        about = "Σχετικά",
        version = "Έκδοση",
        orderDetails = "Λεπτομέρειες Παραγγελίας",
        repeatOrder = "Επανάληψη Παραγγελίας",
        confirmOrder = "Επιβεβαίωση Παραγγελίας",
        continueShopping = "Συνέχεια Αγορών",
        confirmOrderMessage = "Είστε σίγουροι ότι θέλετε να κάνετε αυτή την παραγγελία;",
        yes = "Ναι",
        no = "Όχι",
        appVersion = "Εφαρμογή Σούπερ Μαρκετ v1.0",
        appDescription = "Μια ολοκληρωμένη εφαρμογή αγορών για τη διαχείριση των αγορών σας από το σούπερ μάρκετ",
        features = "Χαρακτηριστικά",
        featureCatalog = "Κατάλογος Προϊόντων με Κατηγορίες",
        featureSearch = "Αναζήτηση και Φιλτράρισμα Προϊόντων",
        featureShoppingList = "Διαχείριση Λίστας Αγορών",
        featureWishList = "Λίστα Επιθυμιών για Μελλοντικές Αγορές",
        featureHistory = "Παρακολούθηση Ιστορικού Αγορών",
        featureOffers = "Εμφάνιση Εκπτώσεων και Προσφορών",
        featureMultiLanguage = "Υποστήριξη Πολλαπλών Γλωσσών (EN/GR)",
        featureNutrition = "Θρεπτικές Πληροφορίες"
    )
}


@Composable
fun rememberLocalizedStrings(): LocalizedStrings {
    return LocalizationManager.getLocalizedStrings()
}

@Composable
fun LocalizedText(
    text: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    androidx.compose.material3.Text(
        text = text,
        modifier = modifier
    )
} 