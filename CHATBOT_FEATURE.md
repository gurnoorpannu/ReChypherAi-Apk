# Waste Assistant Chatbot

## Overview
A floating action button (FAB) chatbot integrated with Google Gemini AI to answer waste-related queries.

## Features
- **Smart Query Filtering**: Only responds to waste-related questions (recycling, disposal, composting, etc.)
- **Gemini AI Integration**: Powered by Google's Gemini Pro model
- **Clean UI**: Material Design 3 with chat bubbles and typing indicators
- **Real-time Responses**: Async API calls with loading states

## Usage
1. Tap the chat FAB icon (bottom-right corner)
2. Ask any waste management question
3. Get instant AI-powered responses
4. Close with the X button in the top-right

## Technical Details
- **API**: Google Gemini Pro API
- **Architecture**: MVVM with ViewModel
- **Networking**: Retrofit + OkHttp
- **UI**: Jetpack Compose

## Waste Keywords Detected
The chatbot validates queries against keywords like: waste, trash, recycle, compost, disposal, plastic, paper, glass, metal, organic, biodegradable, hazardous, e-waste, segregation, pollution, environment, sustainable, etc.

## API Key
Configured in `ChatViewModel.kt` - already set up and ready to use.
