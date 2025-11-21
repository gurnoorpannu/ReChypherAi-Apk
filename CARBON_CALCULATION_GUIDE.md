# Carbon Calculation Implementation Guide

## Overview
This guide documents the carbon calculation system implemented for the Recypher Android app. The system calculates carbon impact (saved and emitted) for waste items based on their type and disposal method.

## Architecture

### 1. Data Layer

#### CarbonData.kt
Location: `app/src/main/java/com/example/rechypher_ai_app/data/CarbonData.kt`

Data class representing carbon impact factors:
```kotlin
data class CarbonData(
    val avgWeight: Double,          // Average weight in kg
    val recycleFactor: Double,      // CO2e saved per kg when recycled
    val landfillFactor: Double,     // CO2e emitted per kg in landfill
    val compostFactor: Double,      // CO2e saved per kg when composted
    val hazardFactor: Double        // CO2e saved per kg when properly disposed
)
```

### 2. Business Logic Layer

#### CarbonCalculator.kt
Location: `app/src/main/java/com/example/rechypher_ai_app/utils/CarbonCalculator.kt`

Core calculation engine with:
- **carbonTable**: Map of waste labels to CarbonData
- **calculateCarbon(label)**: Returns Pair<carbonSaved, carbonEmitted>
- **getFormattedCarbonImpact(label)**: Returns formatted string
- **getNetCarbonImpact(label)**: Returns net impact (saved - emitted)

#### Calculation Logic

**For Hazardous Waste (battery):**
```kotlin
carbonSaved = avgWeight × hazardFactor
carbonEmitted = 0.0
```

**For Organic Waste (biological):**
```kotlin
carbonSaved = avgWeight × compostFactor
carbonEmitted = avgWeight × landfillFactor
```

**For Recyclable Items (glass, paper, plastic, metal, clothes, shoes):**
```kotlin
carbonSaved = avgWeight × recycleFactor
carbonEmitted = avgWeight × landfillFactor
```

**For General Waste (trash):**
```kotlin
carbonSaved = 0.0
carbonEmitted = avgWeight × landfillFactor
```

### 3. ViewModel Layer

#### CarbonViewModel.kt
Location: `app/src/main/java/com/example/rechypher_ai_app/viewmodel/CarbonViewModel.kt`

Manages carbon calculation state across the app:
- Current item carbon data
- Accumulated totals (saved, emitted, items scanned)
- State flows for reactive UI updates

**Key Methods:**
- `calculateCarbonImpact(label)`: Calculate for single item
- `addToTotals()`: Add current to running totals
- `calculateAndAddToTotals(label)`: Combined operation
- `resetAll()`: Reset all statistics

### 4. UI Layer

#### CarbonImpactCard.kt (Reusable Component)
Location: `app/src/main/java/com/example/rechypher_ai_app/ui/functions/CarbonImpactCard.kt`

Reusable Composable that displays carbon impact:
```kotlin
CarbonImpactCard(
    wasteLabel = "plastic",
    compact = true  // or false for detailed view
)
```

**Two Display Modes:**
- **Compact**: Single row with saved/emitted/net
- **Detailed**: Full card with metrics and dividers

#### CarbonImpactScreen.kt (Full Screen)
Location: `app/src/main/java/com/example/rechypher_ai_app/ui/screens/CarbonImpactScreen.kt`

Dedicated screen showing:
- Waste item identification
- Carbon saved/emitted metrics
- Net impact calculation
- Educational facts about the waste type

#### CarbonStatsScreen.kt (Statistics)
Location: `app/src/main/java/com/example/rechypher_ai_app/ui/screens/CarbonStatsScreen.kt`

Shows accumulated statistics:
- Total items scanned
- Total carbon saved/emitted
- Net environmental impact
- Real-world equivalences (km driven, tree days, etc.)

#### CameraScreen.kt (Integration)
Location: `app/src/main/java/com/example/rechypher_ai_app/ui/screens/CameraScreen.kt`

Enhanced with carbon calculation display after waste classification.

## Waste Categories and Carbon Data

### Hazardous Waste
- **battery**: 0.05 kg avg, 0.5 hazardFactor

### Organic Waste
- **biological**: 0.3 kg avg, 0.2 compostFactor, 0.5 landfillFactor

### Recyclable - Glass
- **brown-glass**: 0.4 kg avg, 0.31 recycleFactor, 0.02 landfillFactor
- **green-glass**: 0.4 kg avg, 0.31 recycleFactor, 0.02 landfillFactor
- **white-glass**: 0.4 kg avg, 0.31 recycleFactor, 0.02 landfillFactor

### Recyclable - Paper
- **cardboard**: 0.2 kg avg, 3.3 recycleFactor, 1.5 landfillFactor
- **paper**: 0.1 kg avg, 2.5 recycleFactor, 1.3 landfillFactor

### Recyclable - Textiles
- **clothes**: 0.5 kg avg, 3.6 recycleFactor, 0.6 landfillFactor
- **shoes**: 0.6 kg avg, 2.5 recycleFactor, 0.5 landfillFactor

### Recyclable - Metals
- **metal**: 0.15 kg avg, 9.0 recycleFactor, 0.1 landfillFactor

### Recyclable - Plastics
- **plastic**: 0.05 kg avg, 1.5 recycleFactor, 0.04 landfillFactor

### General Waste
- **trash**: 0.5 kg avg, 0.7 landfillFactor

## Usage Examples

### Basic Calculation
```kotlin
val (saved, emitted) = CarbonCalculator.calculateCarbon("plastic")
// saved = 0.075 kg CO2e
// emitted = 0.002 kg CO2e
```

### Using in Composable
```kotlin
@Composable
fun MyScreen() {
    val wasteLabel = "cardboard"
    
    // Compact display
    CarbonImpactCard(
        wasteLabel = wasteLabel,
        compact = true
    )
}
```

### Using ViewModel
```kotlin
val viewModel: CarbonViewModel = viewModel()

// Calculate for single item
viewModel.calculateCarbonImpact("battery")

// Add to totals
viewModel.addToTotals()

// Or do both at once
viewModel.calculateAndAddToTotals("plastic")

// Observe state
val totalSaved by viewModel.totalCarbonSaved.collectAsState()
val itemsScanned by viewModel.itemsScanned.collectAsState()
```

## Integration Points

### 1. Camera Screen Integration
The CameraScreen now displays carbon impact immediately after classification:
- Shows compact carbon card below bin type
- Displays saved/emitted/net impact
- Updates in real-time

### 2. Navigation Integration
Add to your navigation graph:
```kotlin
// Full carbon impact screen
composable("carbon_impact/{label}") { backStackEntry ->
    val label = backStackEntry.arguments?.getString("label") ?: ""
    CarbonImpactScreen(wasteLabel = label)
}

// Statistics screen
composable("carbon_stats") {
    CarbonStatsScreen(
        viewModel = carbonViewModel // shared instance
    )
}
```

### 3. ViewModel Sharing
Share CarbonViewModel across screens:
```kotlin
val carbonViewModel: CarbonViewModel = viewModel(
    viewModelStoreOwner = LocalContext.current as ComponentActivity
)
```

## Testing

### Unit Test Example
```kotlin
@Test
fun testCarbonCalculation_plastic() {
    val (saved, emitted) = CarbonCalculator.calculateCarbon("plastic")
    assertEquals(0.075, saved, 0.001)
    assertEquals(0.002, emitted, 0.001)
}

@Test
fun testCarbonCalculation_battery() {
    val (saved, emitted) = CarbonCalculator.calculateCarbon("battery")
    assertEquals(0.025, saved, 0.001)
    assertEquals(0.0, emitted, 0.001)
}
```

## Customization

### Updating Carbon Factors
To update carbon factors, modify the `carbonTable` in `CarbonCalculator.kt`:
```kotlin
"plastic" to CarbonData(
    avgWeight = 0.05,      // Update weight
    recycleFactor = 1.5,   // Update recycle factor
    landfillFactor = 0.04  // Update landfill factor
)
```

### Adding New Waste Types
1. Add to `carbonTable` in CarbonCalculator.kt
2. Add to `categoryMap` in WasteCategorizer.kt
3. Add to labels.txt in assets
4. Retrain model if needed

## Performance Considerations

- All calculations are lightweight (simple arithmetic)
- StateFlow provides efficient reactive updates
- Composables recompose only when relevant state changes
- No network calls or heavy operations

## Future Enhancements

1. **Persistence**: Save statistics to local database
2. **Analytics**: Track trends over time
3. **Gamification**: Achievements and goals
4. **Social**: Share impact with friends
5. **Localization**: Region-specific carbon factors
6. **API Integration**: Real-time carbon factor updates

## Support

For questions or issues:
1. Check this documentation
2. Review code comments in source files
3. Test with provided examples
4. Verify carbon factors match requirements

---

**Last Updated**: November 22, 2025
**Version**: 1.0
**Status**: Production Ready ✅
