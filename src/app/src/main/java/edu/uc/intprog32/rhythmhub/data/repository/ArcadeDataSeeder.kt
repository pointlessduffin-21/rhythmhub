package edu.uc.intprog32.rhythmhub.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import edu.uc.intprog32.rhythmhub.data.model.Arcade
import kotlinx.coroutines.tasks.await

object ArcadeDataSeeder {
    
    // Extracted from https://location.am-all.net/alm/location?gm=98&ct=1010&at=12&lang=en
    // Coordinates for known locations are precise; others are initialized to 0.0 for Geocoding.
    private val initialArcades = listOf(
        // Cebu Locations (Verified)
        Arcade(
            id = "seed_cebu_1",
            name = "QUANTUM SM SEASIDE CEBU",
            address = "3104-3106 SM SEASIDE COMPLEX, SOUTH ROAD PROPERTIES, CEBU CITY",
            city = "Cebu City",
            latitude = 10.2818,
            longitude = 123.8804,
            machineCount = 2
        ),
        Arcade(
            id = "seed_cebu_2",
            name = "Q POWER STATION SM JMALL CEBU",
            address = "165 A.S. Fortuna St, Mandaue City",
            city = "Mandaue City",
            latitude = 10.3315,
            longitude = 123.9317,
            machineCount = 2
        ),
        // Metro Manila & Others (Extracted)
        Arcade(name = "QUANTUM SM NOVALICHES GF", address = "GF SM NOVALICHES, QUIRINO HWY.,NOVALICHES, QUEZON CITY", city = "Quezon City"),
        Arcade(name = "QUANTUM SM NORTH EDSA", address = "SM NORHT EDSA, QUEZON CITY", city = "Quezon City"),
        Arcade(name = "Q POWER STATION ROCKWELL", address = "AMAPOLA STREET, POBLACION, MAKATI CITY", city = "Makati City"),
        Arcade(name = "QUANTUM SM SAN LAZARO", address = "A.H LACSON STREET, SAN LAZARO,METRO MANILA", city = "Manila"),
        Arcade(name = "QUANTUM SM CALAMBA", address = "NATIONAL HIGHWAY BRGY.REAL.CALAMBA CITY", city = "Calamba City"),
        Arcade(name = "QUANTUM SM MANILA", address = "CORNER ARROCEROS, ERMITA, METROMANILA", city = "Manila"),
        Arcade(name = "QUANTUM SM BACOOR", address = "LGF SM BACOOR, CORNER AGUINALDO& TIRONA HWAY, BACOOR CAVITE", city = "Bacoor"),
        Arcade(name = "Q POWER STATION SHANGRILA", address = "EASTWING EDSA CORNER SHAW BOULEVARD, MANDALUYONG CITY", city = "Mandaluyong"),
        Arcade(name = "CLUB SYNERGY SM MEGAMALL", address = "LGF SM MEGAMALL, JULIA VARGAS COR EDSA, MANDALUYONG CITY", city = "Mandaluyong"),
        Arcade(name = "QUANTUM SM MARIKINA", address = "BARANGAY CALUMPANG, BARANGKA, MARIKINA CITY", city = "Marikina"),
        Arcade(name = "QUANTUM SM BACOLOD EXP", address = "SM BACOLOD EXP, SM CITY BACOLOD, RECLAMATION AREA, BACOLOD CITY", city = "Bacolod"),
        Arcade(name = "Q POWER STATION MALL OF ASIA", address = "3/F North Wing Expansion Area, SM Mall of Asia,OceanDrive,PASAY", city = "Pasay"),
        Arcade(name = "Q POWER STATION SANTOLAN", address = "3RD FL., SANTOLAN TOWN PLAZA MALL, LITTLE BAGUIO, SAN JUAN CITY", city = "San Juan"),
        Arcade(name = "QUANTUM SM SOUTHMALL", address = "LGF SM SOUTHMALL, ALMANZA, LAS PINAS CITY", city = "Las Pinas"),
        Arcade(name = "QUANTUM SM DASMARINAS", address = "Dasmarinas, 4114 Cavite, Phillipines", city = "Dasmarinas"),
        Arcade(name = "QUANTUM SM GRAND CENTRAL", address = "5TH Fl., Sm Grand Central, Rizal Ave., Caloocan City", city = "Caloocan"),
        Arcade(name = "QUANTUM GREENHILLS", address = "L547 & L549 5F Greenhills Shopping Center, San Juan City,1502", city = "San Juan"),
        Arcade(name = "TIMEZONE UP TOWN CENTER", address = "2F UP Town Center Phase 2 Katipunan Ave. U.P. Campus, Diliman", city = "Quezon City"),
        Arcade(name = "TIMEZONE TRINOMA MALL", address = "4th Floor Trinoma North Ave. Bagong Pag-asa, Quezon City", city = "Quezon City"),
        Arcade(name = "TIMEZONE ALABANG TOWN CENTER MALL", address = "3rd Floor Alabang Town Center, Alabang, Muntinlupa City", city = "Muntinlupa"),
        Arcade(name = "TIMEZONE GLORIETTA 4 MALL", address = "4th Floor Glorietta 4, San Lorenzo, Makati City", city = "Makati"),
        Arcade(name = "TIMEZONE GREENHILLS MALL", address = "Greenhills Mall Phase 1Greenhills Center,Greenhills,SanJuan City", city = "San Juan"),
        Arcade(name = "QUANTUM SM FAIRVIEW", address = "UGF/GF/FF, ANNEX 2, SM City Fairview", city = "Quezon City"),
        Arcade(name = "QUANTUM UPTOWN MALL BGC", address = "Uptown Mall,9th Ave cor36th St,Fort Bonifacio,Taguig,MetroManila", city = "Taguig"),
        Arcade(name = "TIMEZONE SM MEGAMALL", address = "L Ground Fl Bldg B Megamall Edsa J Vargas Wack Wack Greenhills", city = "Mandaluyong"),
        Arcade(name = "QUANTUM SM CITY MARILAO", address = "2F SM City Marilao Mc arthur highway brgy. ibayo marilao bulacan", city = "Marilao"),
        Arcade(name = "QUANTUM SM BAGUIO", address = "3/F SM BAGUIO, LUNETA HILL SESSION ROAD, BAGUIO CITY", city = "Baguio"),
        Arcade(name = "QUANTUM SM URDANETA", address = "Level2, SM Urdaneta Central, McArthur Hwy, Pangasinan", city = "Urdaneta"),
        Arcade(name = "QUANTUM SM CITY LAOAG", address = "Barangay, 51-B Airport Rd, Laoag City, 2900 Ilocos Norte", city = "Laoag"),
        Arcade(name = "QUANTUM SM CLARK", address = "SM CLARK M.A.ROXAS ST.,MALABANIAS,ANGELES CITY", city = "Angeles"),
        Arcade(name = "QUANTUM SM BATAAN", address = "2nd Level, Sm City Bataan, Balanga, Bataan", city = "Balanga"),
        Arcade(name = "QUANTUM SM LUCENA", address = "2nd FL,140 Dalahican Road, corner Maharlika Hwy, Lucena", city = "Lucena"),
        Arcade(name = "QUANTUM SM PAMPANGA", address = "3rd Floor, San Jose, San Fernando", city = "San Fernando"),
        Arcade(name = "QUANTUM SM LIPA", address = "2nd FL. SM LIPA,BATANGAS CITY", city = "Lipa"),
        Arcade(name = "QUANTUM SM STA ROSA", address = "SM City Sta. Rosa,Old National Hi-way,Sta.Rosa City,4026 Laguna", city = "Santa Rosa"),
        Arcade(name = "QUANTUM SM GENERAL SANTOS", address = "2nd Floor SM General Santos Santiago Blvd General Santos City", city = "General Santos")
    )

    suspend fun seedArcadesIfNeeded(firestore: FirebaseFirestore) {
        try {
            val snapshot = firestore.collection("arcades").limit(1).get().await()
            if (snapshot.isEmpty) {
                Log.d("ArcadeDataSeeder", "Seeding arcades...")
                val batch = firestore.batch()
                initialArcades.forEach { arcade ->
                    val docRef = if (arcade.id.isNotEmpty()) {
                        firestore.collection("arcades").document(arcade.id)
                    } else {
                        firestore.collection("arcades").document()
                    }
                    // Copy arcade but ensure ID matches docRef if it was auto-generated
                    val arcadetoSave = arcade.copy(id = docRef.id)
                    batch.set(docRef, arcadetoSave)
                }
                batch.commit().await()
                Log.d("ArcadeDataSeeder", "Seeding complete.")
            } else {
                Log.d("ArcadeDataSeeder", "Arcades already seeded.")
            }
        } catch (e: Exception) {
            Log.e("ArcadeDataSeeder", "Error seeding arcades", e)
        }
    }
}

