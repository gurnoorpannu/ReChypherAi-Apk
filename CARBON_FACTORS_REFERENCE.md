# Carbon Factors Reference

Quick reference table for all waste types and their carbon impact factors.

## Complete Carbon Data Table

| Waste Label | Category | Avg Weight (kg) | Recycle Factor | Landfill Factor | Compost Factor | Hazard Factor |
|-------------|----------|-----------------|----------------|-----------------|----------------|---------------|
| battery | Hazardous | 0.05 | - | - | - | 0.5 |
| biological | Organic | 0.3 | - | 0.5 | 0.2 | - |
| brown-glass | Recyclable | 0.4 | 0.31 | 0.02 | - | - |
| green-glass | Recyclable | 0.4 | 0.31 | 0.02 | - | - |
| white-glass | Recyclable | 0.4 | 0.31 | 0.02 | - | - |
| cardboard | Recyclable | 0.2 | 3.3 | 1.5 | - | - |
| paper | Recyclable | 0.1 | 2.5 | 1.3 | - | - |
| clothes | Recyclable | 0.5 | 3.6 | 0.6 | - | - |
| shoes | Recyclable | 0.6 | 2.5 | 0.5 | - | - |
| metal | Recyclable | 0.15 | 9.0 | 0.1 | - | - |
| plastic | Recyclable | 0.05 | 1.5 | 0.04 | - | - |
| trash | General Waste | 0.5 | - | 0.7 | - | - |

## Calculation Examples

### Example 1: Plastic Bottle
```
Label: plastic
Avg Weight: 0.05 kg
Recycle Factor: 1.5
Landfill Factor: 0.04

Carbon Saved = 0.05 × 1.5 = 0.075 kg CO2e
Carbon Emitted = 0.05 × 0.04 = 0.002 kg CO2e
Net Impact = 0.075 - 0.002 = 0.073 kg CO2e (POSITIVE)
```

### Example 2: Battery
```
Label: battery
Avg Weight: 0.05 kg
Hazard Factor: 0.5

Carbon Saved = 0.05 × 0.5 = 0.025 kg CO2e
Carbon Emitted = 0.0 kg CO2e
Net Impact = 0.025 - 0.0 = 0.025 kg CO2e (POSITIVE)
```

### Example 3: Cardboard Box
```
Label: cardboard
Avg Weight: 0.2 kg
Recycle Factor: 3.3
Landfill Factor: 1.5

Carbon Saved = 0.2 × 3.3 = 0.66 kg CO2e
Carbon Emitted = 0.2 × 1.5 = 0.3 kg CO2e
Net Impact = 0.66 - 0.3 = 0.36 kg CO2e (POSITIVE)
```

### Example 4: Organic Waste
```
Label: biological
Avg Weight: 0.3 kg
Compost Factor: 0.2
Landfill Factor: 0.5

Carbon Saved = 0.3 × 0.2 = 0.06 kg CO2e
Carbon Emitted = 0.3 × 0.5 = 0.15 kg CO2e
Net Impact = 0.06 - 0.15 = -0.09 kg CO2e (NEGATIVE if not composted)
```

### Example 5: Metal Can
```
Label: metal
Avg Weight: 0.15 kg
Recycle Factor: 9.0
Landfill Factor: 0.1

Carbon Saved = 0.15 × 9.0 = 1.35 kg CO2e
Carbon Emitted = 0.15 × 0.1 = 0.015 kg CO2e
Net Impact = 1.35 - 0.015 = 1.335 kg CO2e (HIGHEST POSITIVE)
```

### Example 6: General Trash
```
Label: trash
Avg Weight: 0.5 kg
Landfill Factor: 0.7

Carbon Saved = 0.0 kg CO2e
Carbon Emitted = 0.5 × 0.7 = 0.35 kg CO2e
Net Impact = 0.0 - 0.35 = -0.35 kg CO2e (NEGATIVE)
```

## Ranking by Recycling Impact

### Highest Carbon Savings (per kg)
1. **Metal**: 9.0 kg CO2e/kg
2. **Clothes**: 3.6 kg CO2e/kg
3. **Cardboard**: 3.3 kg CO2e/kg
4. **Paper**: 2.5 kg CO2e/kg
5. **Shoes**: 2.5 kg CO2e/kg
6. **Plastic**: 1.5 kg CO2e/kg
7. **Glass**: 0.31 kg CO2e/kg

### Highest Landfill Emissions (per kg)
1. **Cardboard**: 1.5 kg CO2e/kg
2. **Paper**: 1.3 kg CO2e/kg
3. **Trash**: 0.7 kg CO2e/kg
4. **Clothes**: 0.6 kg CO2e/kg
5. **Biological**: 0.5 kg CO2e/kg
6. **Shoes**: 0.5 kg CO2e/kg

## Real-World Equivalences

### 1 kg CO2e is equivalent to:
- 8.3 km of driving (average car)
- 17 days of a tree's CO2 absorption
- 125 smartphone charges
- 100 hours of LED bulb usage
- 0.5 kg of beef production

### Common Item Impacts:
- **1 Aluminum Can (metal)**: Saves ~1.35 kg CO2e
- **1 Cardboard Box**: Saves ~0.36 kg CO2e
- **1 Plastic Bottle**: Saves ~0.073 kg CO2e
- **1 Glass Bottle**: Saves ~0.124 kg CO2e
- **1 T-shirt (clothes)**: Saves ~1.8 kg CO2e

## Notes

- All factors are in kg CO2e (carbon dioxide equivalent)
- Factors based on lifecycle analysis and industry standards
- Actual values may vary by region and recycling infrastructure
- Net positive impact means recycling is beneficial
- Net negative impact means landfill disposal has environmental cost

## Formula Summary

```
For Recyclable Items:
  carbonSaved = avgWeight × recycleFactor
  carbonEmitted = avgWeight × landfillFactor

For Hazardous (Battery):
  carbonSaved = avgWeight × hazardFactor
  carbonEmitted = 0

For Organic (Biological):
  carbonSaved = avgWeight × compostFactor
  carbonEmitted = avgWeight × landfillFactor

For General Waste (Trash):
  carbonSaved = 0
  carbonEmitted = avgWeight × landfillFactor

Net Impact = carbonSaved - carbonEmitted
```

---

**Last Updated**: November 22, 2025
