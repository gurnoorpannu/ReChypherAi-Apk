# Recypher AI – Android Application

Recypher AI is an intelligent Android application that helps users identify, understand, and responsibly dispose of waste using AI-powered image recognition and real-time guidance. The app serves as a sustainability companion that simplifies waste segregation and promotes eco-friendly habits through interactive features and a reward system.

---

## Overview

The Android app allows users to scan any waste item using their phone camera. An on-device TensorFlow Lite model classifies the item and provides clear disposal instructions. If the item requires responsible disposal, the app displays nearby waste disposal or recycling centers and guides the user through navigation. Disposal is verified via QR scanning and users are rewarded with Green Points to encourage sustainable behaviour.

---

## Key Features

- Real-time waste scanning using CameraX  
- On-device AI classification using TensorFlow Lite  
- Offline functionality for image recognition  
- Disposal guidance with Google Maps integration  
- Nearest waste center discovery (distance-based)  
- QR-based disposal verification  
- Green Points reward system  
- Scan history and user activity tracking  
- AI chatbot for waste-related queries  
- Educational Waste Guide for different categories  

---

## Technology Stack

- Kotlin  
- Jetpack Compose  
- TensorFlow Lite  
- CameraX  
- Google Maps SDK  
- MVVM Architecture  
- Node.js Backend Integration  
- REST APIs  

---

## App Flow

1. User captures an image of the waste item.  
2. The TFLite model classifies the item on-device.  
3. The app displays the waste category and disposal instructions.  
4. User views nearby disposal centers on the map.  
5. Navigation guides the user to the selected center.  
6. QR code is scanned to confirm disposal.  
7. Green Points are awarded and history is updated.

---

## Waste Categories Supported

- Plastic  
- Glass (White, Brown, Green)  
- Paper  
- Cardboard  
- Metal  
- Organic / Biological  
- E-Waste  
- Battery  
- Clothing & Shoes  
- Mixed Trash  

---

## Permissions Used

- Camera: For scanning waste items  
- Location: For showing nearby disposal centers  
- Internet: For backend communication and map services  
- Storage: For caching data and scan history  

---

## Architecture

The application follows MVVM architecture ensuring clean separation of concerns:
- UI Layer: Jetpack Compose screens and navigation  
- ViewModel Layer: State management and business logic  
- Data Layer: API communication and local storage handling  

---

## Impact

The application reduces waste misclassification, improves recycling efficiency, and promotes sustainable disposal habits by transforming complex disposal rules into easy-to-follow actions.

---

## Future Enhancements

- Multilingual support  
- Voice-enabled waste assistant  
- City-level analytics dashboard  
- Integration with municipal waste systems  
- Expanded waste database  
- Offline center caching  

---

## Contribution

This app was built as part of a hackathon project with the goal of creating a real-world, scalable solution for intelligent waste management using AI.

---

##Refer the link below for the demo video , ppt and the apk file:-

https://drive.google.com/drive/folders/1nJ2KmkCRadGgjG0n62pbMFEJcLxyJni5

---
