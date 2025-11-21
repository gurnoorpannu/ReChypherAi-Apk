# Carbon Calculation Integration Checklist

Quick checklist for integrating the carbon calculation system into your Recypher app.

## ✅ Files Created

### Data Layer
- [x] `app/src/main/java/com/example/rechypher_ai_app/data/CarbonData.kt`
  - Data class for carbon factors

### Utils Layer
- [x] `app/src/main/java/com/example/rechypher_ai_app/utils/CarbonCalculator.kt`
  - Core calculation logic
  - Carbon data table
  - Helper functions

### ViewModel Layer
- [x] `app/src/main/java/com/example/rechypher_ai_app/viewmodel/CarbonViewModel.kt`
  - State management
  - Accumulated statistics
  - StateFlow for reactive UI

### UI Components
- [x] `app/src/main/java/com/example/rechypher_ai_app/ui/functions/CarbonImpactCard.kt`
  - Reusable carbon display component
  - Compact and detailed modes

### UI Screens
- [x] `app/src/main/java/com/example/rechypher_ai_app/ui/screens/CarbonImpactScreen.kt`
  - Full-screen carbon analysis
  - Educational facts
  
- [x] `app/src/main/java/com/example/rechypher_ai_app/ui/screens/CarbonStatsScreen.kt`
  - Accumulated statistics
  - Real-world equivalences
  
- [x] `app/src/main/java/com/example/rechypher_ai_app/ui/screens/CameraScreen.kt`
  - Enhanced with carbon display

### Documentation
- [x] `CARBON_CALCULATION_GUIDE.md`
- [x] `CARBON_FACTORS_REFERENCE.md`
- [x] `INTEGRATION_CHECKLIST.md`

## 🔧 Integration Steps

### Step 1: Verify Dependencies
Ensure your `build.gradle.kts` has:
```kotlin
dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
}
```

### Step 2: Test Basic Calculation
```kotlin
// In any Activity or Composable
val (saved, emitted) = CarbonCalculator.calculateCarbon("plastic")
Log.d("Carbon", "Saved: $saved, Emitted: $emitted")
```

### Step 3: Add to Existing Screens
The CameraScreen is already integrated. To add to other screens:

```kotlin
// Import the component
import com.example.rechypher_ai_app.ui.functions.CarbonImpactCard

// Use in your Composable
@Composable
fun YourScreen() {
    Column {
        // Your existing UI
        
        // Add carbon display
        CarbonImpactCard(
            wasteLabel = "plastic",
            compact = true
        )
    }
}
```

### Step 4: Add Navigation Routes (Optional)
If you want dedicated carbon screens:

```kotlin
// In your NavHost
composable("carbon_impact/{label}") { backStackEntry ->
    val label = backStackEntry.arguments?.getString("label") ?: ""
    CarbonImpactScreen(
        wasteLabel = label,
        onBackClick = { navController.popBackStack() }
    )
}

composable("carbon_stats") {
    CarbonStatsScreen(
        onBackClick = { navController.popBackStack() }
    )
}
```

### Step 5: Integrate ViewModel (Optional)
For tracking across sessions:

```kotlin
// In your Activity
class MainActivity : ComponentActivity() {
    private val carbonViewModel: CarbonViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Pass viewModel to screens that need it
            YourApp(carbonViewModel = carbonViewModel)
        }
    }
}

// In your Composable
@Composable
fun YourScreen(viewModel: CarbonViewModel = viewModel()) {
    // Use viewModel
    val totalSaved by viewModel.totalCarbonSaved.collectAsState()
    
    // Calculate and track
    LaunchedEffect(wasteLabel) {
        viewModel.calculateAndAddToTotals(wasteLabel)
    }
}
```

## 🧪 Testing Checklist

### Manual Testing
- [ ] Test calculation for each waste type
- [ ] Verify compact card displays correctly
- [ ] Verify detailed card displays correctly
- [ ] Test CarbonImpactScreen with different labels
- [ ] Test CarbonStatsScreen accumulation
- [ ] Test reset functionality
- [ ] Verify camera screen integration

### Test Cases
```kotlin
// Test each category
testLabels = listOf(
    "battery",      // Hazardous
    "biological",   // Organic
    "plastic",      // Recyclable
    "trash"         // General Waste
)

testLabels.forEach { label ->
    val (saved, emitted) = CarbonCalculator.calculateCarbon(label)
    println("$label: saved=$saved, emitted=$emitted")
}
```

### Expected Results
```
battery: saved=0.025, emitted=0.0
biological: saved=0.06, emitted=0.15
plastic: saved=0.075, emitted=0.002
trash: saved=0.0, emitted=0.35
```

## 🎨 UI Customization

### Colors
Current colors in use:
- Green (Saved): `Color(0xFF4CAF50)`
- Red (Emitted): `Color(0xFFF44336)`
- Primary Green: `PrimaryGreen` from theme
- Background: `Color(0xFFF5F5F5)`

To customize, modify in respective screen files.

### Text Sizes
- Title: 24sp
- Metric Value: 28sp - 48sp
- Body: 14sp - 16sp
- Caption: 10sp - 12sp

### Card Styling
- Border Radius: 12dp - 16dp
- Elevation: 4dp - 8dp
- Padding: 16dp - 24dp

## 📊 Data Validation

### Verify Carbon Table
All 12 waste types should be in carbonTable:
- [x] battery
- [x] biological
- [x] brown-glass
- [x] green-glass
- [x] white-glass
- [x] cardboard
- [x] paper
- [x] clothes
- [x] shoes
- [x] metal
- [x] plastic
- [x] trash

### Verify Formulas
- [x] Hazardous: uses hazardFactor, emitted = 0
- [x] Organic: uses compostFactor and landfillFactor
- [x] Recyclable: uses recycleFactor and landfillFactor
- [x] Trash: saved = 0, uses landfillFactor

## 🚀 Deployment Checklist

### Before Release
- [ ] All calculations tested and verified
- [ ] UI displays correctly on different screen sizes
- [ ] No compilation errors
- [ ] No runtime crashes
- [ ] Performance is acceptable
- [ ] Documentation is complete

### Optional Enhancements
- [ ] Add persistence (Room database)
- [ ] Add analytics tracking
- [ ] Add user preferences
- [ ] Add export functionality
- [ ] Add social sharing
- [ ] Add achievements/gamification

## 📝 Usage Examples

### Quick Display in Any Screen
```kotlin
@Composable
fun QuickExample() {
    CarbonImpactCard(
        wasteLabel = "plastic",
        compact = true
    )
}
```

### Full Analysis Screen
```kotlin
@Composable
fun FullExample() {
    CarbonImpactScreen(
        wasteLabel = "cardboard"
    )
}
```

### With ViewModel Tracking
```kotlin
@Composable
fun TrackedExample(viewModel: CarbonViewModel = viewModel()) {
    val label = "metal"
    
    LaunchedEffect(label) {
        viewModel.calculateAndAddToTotals(label)
    }
    
    val totalSaved by viewModel.totalCarbonSaved.collectAsState()
    
    Column {
        CarbonImpactCard(wasteLabel = label)
        Text("Total saved so far: $totalSaved kg CO2e")
    }
}
```

## 🐛 Troubleshooting

### Issue: Calculation returns (0.0, 0.0)
**Solution**: Label not found in carbonTable. Check spelling and case.

### Issue: UI not updating
**Solution**: Ensure using StateFlow and collectAsState() properly.

### Issue: Compilation errors
**Solution**: Run `./gradlew clean build` and check imports.

### Issue: Card not displaying
**Solution**: Check parent layout has sufficient space (fillMaxWidth).

## ✨ Success Criteria

Your integration is complete when:
1. ✅ All files compile without errors
2. ✅ Carbon calculations return correct values
3. ✅ UI components display properly
4. ✅ Camera screen shows carbon impact
5. ✅ No runtime crashes
6. ✅ Formulas match specification

## 📞 Support

If you encounter issues:
1. Check `CARBON_CALCULATION_GUIDE.md` for detailed documentation
2. Check `CARBON_FACTORS_REFERENCE.md` for calculation examples
3. Review code comments in source files
4. Verify all files are in correct locations

---

**Status**: Ready for Integration ✅
**Last Updated**: November 22, 2025
