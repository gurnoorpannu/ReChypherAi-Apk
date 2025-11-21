# Carbon Calculation - Quick Start Guide

## 🚀 Get Started in 3 Steps

### Step 1: Test the Implementation (30 seconds)

Add to MainActivity temporarily:
```kotlin
import com.example.rechypher_ai_app.utils.CarbonTestHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Test calculations
        CarbonTestHelper.verifyCalculations()
        
        // ... rest of your code
    }
}
```

Check Logcat for "CarbonTest" - should see ✅ PASS for all tests.

### Step 2: See It in Action (Already Done!)

The CameraScreen already shows carbon impact after scanning. Just:
1. Open the app
2. Go to Camera screen
3. Scan any waste item
4. See carbon impact displayed below the bin type

### Step 3: Use Anywhere (Copy & Paste)

```kotlin
import com.example.rechypher_ai_app.ui.functions.CarbonImpactCard

@Composable
fun YourScreen() {
    CarbonImpactCard(
        wasteLabel = "plastic",  // or any label
        compact = true           // false for detailed view
    )
}
```

## 📊 Quick Examples

### Example 1: Calculate Only
```kotlin
val (saved, emitted) = CarbonCalculator.calculateCarbon("plastic")
// saved = 0.075 kg CO2e
// emitted = 0.002 kg CO2e
```

### Example 2: Display Compact
```kotlin
CarbonImpactCard(wasteLabel = "cardboard", compact = true)
```

### Example 3: Display Detailed
```kotlin
CarbonImpactCard(wasteLabel = "metal", compact = false)
```

### Example 4: Full Screen
```kotlin
CarbonImpactScreen(wasteLabel = "battery")
```

### Example 5: Track Statistics
```kotlin
val viewModel: CarbonViewModel = viewModel()
viewModel.calculateAndAddToTotals("plastic")
```

## 🎯 All Waste Labels

Copy-paste ready:
```kotlin
"battery"       // Hazardous
"biological"    // Organic
"brown-glass"   // Recyclable
"green-glass"   // Recyclable
"white-glass"   // Recyclable
"cardboard"     // Recyclable
"paper"         // Recyclable
"clothes"       // Recyclable
"shoes"         // Recyclable
"metal"         // Recyclable
"plastic"       // Recyclable
"trash"         // General Waste
```

## 📈 Quick Results Reference

| Label | Saved | Emitted | Net |
|-------|-------|---------|-----|
| metal | 1.350 | 0.015 | +1.335 |
| cardboard | 0.660 | 0.300 | +0.360 |
| plastic | 0.075 | 0.002 | +0.073 |
| battery | 0.025 | 0.000 | +0.025 |
| trash | 0.000 | 0.350 | -0.350 |

## 🔧 Common Tasks

### Task: Add to existing screen
```kotlin
Column {
    // Your existing UI
    Text("Scan Result: Plastic")
    
    // Add carbon display
    CarbonImpactCard(wasteLabel = "plastic", compact = true)
}
```

### Task: Navigate to full analysis
```kotlin
Button(onClick = {
    navController.navigate("carbon_impact/plastic")
}) {
    Text("View Carbon Impact")
}
```

### Task: Show statistics
```kotlin
Button(onClick = {
    navController.navigate("carbon_stats")
}) {
    Text("View Statistics")
}
```

## 📚 Need More Info?

- **Quick Start**: This file
- **Usage Examples**: `CARBON_FEATURE_README.md`
- **Technical Details**: `CARBON_CALCULATION_GUIDE.md`
- **All Carbon Factors**: `CARBON_FACTORS_REFERENCE.md`
- **Integration Steps**: `INTEGRATION_CHECKLIST.md`
- **Summary**: `IMPLEMENTATION_SUMMARY.md`

## ✅ Verification Checklist

- [ ] Run `CarbonTestHelper.verifyCalculations()` - all pass?
- [ ] Open Camera screen - carbon display shows?
- [ ] Try `CarbonImpactCard` in a test screen - displays?
- [ ] Check Logcat for "CarbonTest" - no errors?

If all ✅, you're ready to go! 🎉

## 🆘 Quick Troubleshooting

**Problem**: Returns (0.0, 0.0)
**Fix**: Check label spelling - must be exact match

**Problem**: UI not showing
**Fix**: Check parent has `fillMaxWidth()` modifier

**Problem**: Compilation error
**Fix**: Run `./gradlew clean build`

**Problem**: Test not logging
**Fix**: Check Logcat filter set to "CarbonTest"

## 🎓 5-Minute Tutorial

1. **Minute 1**: Add test to MainActivity, run app, check Logcat
2. **Minute 2**: Open Camera screen, scan item, see carbon display
3. **Minute 3**: Copy CarbonImpactCard example, paste in test screen
4. **Minute 4**: Run app, verify card displays
5. **Minute 5**: Read CARBON_FEATURE_README.md for more options

Done! You now know how to use the carbon calculation system.

## 💡 Pro Tips

1. Use `compact = true` for inline displays
2. Use `compact = false` for dedicated sections
3. Use `CarbonImpactScreen` for full analysis
4. Use `CarbonViewModel` to track across sessions
5. Use `CarbonTestHelper` for debugging

## 🎯 Most Common Use Case

```kotlin
@Composable
fun ResultScreen(scannedLabel: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Show what was scanned
        Text(
            text = "Detected: ${scannedLabel.uppercase()}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Show bin type
        Text(
            text = WasteCategorizer.getBinTypeWithEmoji(scannedLabel),
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Show carbon impact
        CarbonImpactCard(
            wasteLabel = scannedLabel,
            compact = false
        )
    }
}
```

That's it! Copy, paste, customize. 🚀

---

**Ready to use!** ✅

For detailed documentation, see `CARBON_FEATURE_README.md`
