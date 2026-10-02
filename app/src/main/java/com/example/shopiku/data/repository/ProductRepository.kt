package com.example.shopiku.data.repository

import com.example.shopiku.data.model.Category
import com.example.shopiku.data.model.Product
import com.example.shopiku.data.model.Review
import com.example.shopiku.data.model.Variant
import com.example.shopiku.data.remote.RetrofitClient
import com.example.shopiku.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository {

    private val api = RetrofitClient.apiService

    suspend fun getCategories(): Resource<List<Category>> = withContext(Dispatchers.IO) {
        val categories = listOf(
            Category("cat_sneakers", "Sneakers", "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=300"),
            Category("cat_running", "Running", "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=300"),
            Category("cat_casual", "Casual", "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=300"),
            Category("cat_apparel", "Apparel", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=300"),
            Category("cat_local", "Khas Cirebon", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=300"),
            Category("cat_accessories", "Aksesoris", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300")
        )
        Resource.Success(categories)
    }

    suspend fun getProducts(
        searchQuery: String? = null,
        categoryId: String? = null
    ): Resource<List<Product>> = withContext(Dispatchers.IO) {
        try {
            // Attempt Supabase live cloud request
            val response = api.getProducts()
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                var filtered = response.body()!!
                if (!searchQuery.isNullOrBlank()) {
                    filtered = filtered.filter { it.name.contains(searchQuery, ignoreCase = true) }
                }
                if (!categoryId.isNullOrBlank() && categoryId != "all") {
                    filtered = filtered.filter { it.categoryId == categoryId }
                }
                return@withContext Resource.Success(filtered)
            }
        } catch (e: Exception) {
            // Live cloud offline or unconfigured -> Fallback gracefully to seed items
        }

        var list = seedProducts()
        if (!searchQuery.isNullOrBlank()) {
            list = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }
        }
        if (!categoryId.isNullOrBlank() && categoryId != "all") {
            list = list.filter { it.categoryId == categoryId }
        }
        Resource.Success(list)
    }

    suspend fun getFlashSaleProducts(): Resource<List<Product>> = withContext(Dispatchers.IO) {
        val flashSale = seedProducts().take(6)
        Resource.Success(flashSale)
    }

    suspend fun getProductDetail(productId: String): Resource<Product> = withContext(Dispatchers.IO) {
        val product = seedProducts().find { it.id == productId }
            ?: seedProducts().first()
        Resource.Success(product)
    }

    suspend fun getReviewsForProduct(productId: String): Resource<List<Review>> = withContext(Dispatchers.IO) {
        val reviews = listOf(
            Review("rev_1", productId, "Ahmad Rizky", "", 5.0f, "Kualitas sepatu sangat bagus, original dan empuk dipakai jalan jauh!", "Varian: Hitam / 42", "2 hari lalu"),
            Review("rev_2", productId, "Siti Nurhaliza", "", 4.5f, "Jahitan rapi, pengiriman ke Kesambi Cirebon cuma 1 hari pake Reguler.", "Varian: Putih / 39", "3 hari lalu"),
            Review("rev_3", productId, "Dedi Prasetyo", "", 5.0f, "Barang sesuai foto, packing bubble wrap tebal. Recommended seller!", "Varian: Navy / 41", "1 minggu lalu")
        )
        Resource.Success(reviews)
    }

    private fun seedProducts(): List<Product> {
        val defaultVariants = listOf(
            Variant("v1", "Hitam / 39", 15),
            Variant("v2", "Hitam / 40", 12),
            Variant("v3", "Hitam / 41", 20),
            Variant("v4", "Hitam / 42", 18),
            Variant("v5", "Putih / 40", 10),
            Variant("v6", "Putih / 42", 8)
        )

        return listOf(
            Product(
                id = "p1",
                name = "Air Jordan 1 Retro High OG Cirebon Edition",
                description = "Sneakers ikonik dengan sentuhan kontemporer dan kenyamanan maksimal. Dibuat dengan kulit premium bertekstur lembut dan cushion Air-Sole yang empuk untuk pemakaian sehari-hari.",
                price = 1850000.0,
                imageUrl = "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=600",
                categoryId = "cat_sneakers",
                categoryName = "Sneakers",
                rating = 4.9,
                soldCount = 340,
                stock = 30,
                variants = defaultVariants
            ),
            Product(
                id = "p2",
                name = "Nike Air Force 1 '07 Classic White",
                description = "Gaya klasik yang selalu tren. Sepatu serbaguna warna putih bersih dengan perforasi di bagian toebox untuk sirkulasi udara optimal.",
                price = 1549000.0,
                imageUrl = "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600",
                categoryId = "cat_sneakers",
                categoryName = "Sneakers",
                rating = 4.8,
                soldCount = 890,
                stock = 45,
                variants = defaultVariants
            ),
            Product(
                id = "p3",
                name = "Adidas Ultraboost Light Running Shoes",
                description = "Sepatu lari super ringan dengan teknologi Boost energy-return. Cocok untuk jogging di Alun-Alun Kejaksan Cirebon maupun penggunaan harian.",
                price = 2100000.0,
                imageUrl = "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=600",
                categoryId = "cat_running",
                categoryName = "Running",
                rating = 4.9,
                soldCount = 210,
                stock = 15,
                variants = defaultVariants
            ),
            Product(
                id = "p4",
                name = "Converse Chuck Taylor All Star Canvas High Top",
                description = "Canvas sneakers abadi sejak 1917. Ringan, kasual, dan cocok dipadukan dengan style pakaian apapun.",
                price = 799000.0,
                imageUrl = "https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=600",
                categoryId = "cat_casual",
                categoryName = "Casual",
                rating = 4.7,
                soldCount = 520,
                stock = 50,
                variants = defaultVariants
            ),
            Product(
                id = "p5",
                name = "Vans Old Skool Core Classics Black/White",
                description = "Sepatu skate ikonik dengan garis samping khas. Sol waffle karet memberikan cengkeraman maksimal.",
                price = 899000.0,
                imageUrl = "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=600",
                categoryId = "cat_casual",
                categoryName = "Casual",
                rating = 4.8,
                soldCount = 670,
                stock = 40,
                variants = defaultVariants
            ),
            Product(
                id = "p6",
                name = "Jaket Hoodie Streetwear Megamendung Batik",
                description = "Hoodie cotton fleece premium dengan motif Megamendung khas Cirebon pada bagian lengan. Hangat, stylish, dan bangga lokal.",
                price = 385000.0,
                imageUrl = "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=600",
                categoryId = "cat_local",
                categoryName = "Khas Cirebon",
                rating = 4.9,
                soldCount = 430,
                stock = 25,
                variants = listOf(
                    Variant("v_h1", "Hitam / M", 10),
                    Variant("v_h2", "Hitam / L", 15),
                    Variant("v_h3", "Hitam / XL", 8),
                    Variant("v_h4", "Navy / L", 12)
                )
            ),
            Product(
                id = "p7",
                name = "New Balance 530 Retro Athletic Silver",
                description = "Inspirasi sepatu lari era 90-an dengan midsole ABZORB yang meredam benturan dengan sempurna.",
                price = 1699000.0,
                imageUrl = "https://images.unsplash.com/photo-1539185441755-769473a23570?w=600",
                categoryId = "cat_sneakers",
                categoryName = "Sneakers",
                rating = 4.8,
                soldCount = 310,
                stock = 20,
                variants = defaultVariants
            ),
            Product(
                id = "p8",
                name = "Puma Suede Classic XXI Forest Green",
                description = "Bahan suede halus dengan Formstrip khas Puma. Tampilan vintage modern yang selalu menawan.",
                price = 1099000.0,
                imageUrl = "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600",
                categoryId = "cat_casual",
                categoryName = "Casual",
                rating = 4.6,
                soldCount = 180,
                stock = 18,
                variants = defaultVariants
            ),
            Product(
                id = "p9",
                name = "Kaos Oversized Streetwear ShopiKu Edition",
                description = "T-Shirt 100% Heavyweight Cotton 24s. Sablon DTF presisi tidak gampang pecah walau dicuci berulang kali.",
                price = 149000.0,
                imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600",
                categoryId = "cat_apparel",
                categoryName = "Apparel",
                rating = 4.7,
                soldCount = 590,
                stock = 60,
                variants = listOf(
                    Variant("v_t1", "Hitam / M", 20),
                    Variant("v_t2", "Hitam / L", 25),
                    Variant("v_t3", "Putih / L", 15)
                )
            ),
            Product(
                id = "p10",
                name = "Tas Slingbag Canvas Waterproof Cirebon",
                description = "Tas selempang kompak anti air cocok untuk membawa gadget, dompet, dan kunci saat bepergian.",
                price = 129000.0,
                imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600",
                categoryId = "cat_accessories",
                categoryName = "Aksesoris",
                rating = 4.8,
                soldCount = 410,
                stock = 35,
                variants = listOf(
                    Variant("v_b1", "Hitam", 20),
                    Variant("v_b2", "Olive", 15)
                )
            )
        )
    }
}