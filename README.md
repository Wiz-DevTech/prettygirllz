# PrettyGirlz LLC - Candy Store Application

## Project Overview
PrettyGirlz LLC is an online store specializing in candy, teen fashion, and lifestyle products. This application delivers a comprehensive e-commerce platform with unique features including avatar-based fashion previews, integrated chat systems, and a QR-based pickup system for product delivery.

## Key Features
- **User Management** with role-based access (Admin, Seller, Customer)
- **Dual Product Catalog** for candy and fashion items
- **Avatar-Based Try-On** for fashion items
- **Flexible Delivery Options** with home delivery and drop zone pickup
- **QR Code Verification** for secure product pickups
- **Integrated Chat System** with product-specific and social feeds
- **TikTok-Style Social Feed** with comments and likes

## Technology Stack
- **Backend:** Spring Boot, Maven
- **Frontend:** JSP, Bootstrap
- **Database:** MongoDB
- **External Services:**
  - Google Maps API (location tagging)
  - ZXing Library (QR generation)
  - Cloudinary (image uploads)

## System Architecture

The application follows a microservice architecture with the following components:

```
prettygirlz-app/  
├── user-service/         # Auth & profiles  
├── product-service/      # Candy/fashion items  
├── delivery-service/     # Drop zones & QR  
├── chat-service/         # Real-time messaging  
└── web-ui/               # Frontend  
```

### Database Schema
MongoDB collections:
- `users` (auth data, roles)
- `products` (candy/fashion items)
- `drop_zones` (location data + QR info)
- `chats` (threads and messages)

## Development Setup

### Prerequisites
- JDK 11+
- Maven 3.6+
- MongoDB 4.4+
- Git

### Getting Started
1. Clone the repository
   ```bash
   git clone https://github.com/prettygirllz/candy-store-app.git
   cd candy-store-app
   ```

2. Configure environment variables
   - Create a `.env` file in the root directory
   - Add the following variables:
     ```
     MONGODB_URI=mongodb://localhost:27017/prettygirllz
     CLOUDINARY_CLOUD_NAME=your_cloud_name
     CLOUDINARY_API_KEY=your_api_key
     CLOUDINARY_API_SECRET=your_api_secret
     GOOGLE_MAPS_API_KEY=your_google_maps_key
     ```

3. Build all services
   ```bash
   mvn clean install
   ```

4. Run the application
   ```bash
   # Start all services
   ./run.sh start-all
   
   # Or start individual services
   ./run.sh start user-service
   ```

## Project Structure

Each microservice follows the standard Spring Boot structure:

```
service-name/
├── src/
│   ├── main/
│   │   ├── java/com/prettygirllz/servicename/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── ServiceNameApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   └── test/
│       └── java/com/prettygirllz/servicename/
└── pom.xml
```

## Development Guidelines

### Branching Strategy
- `main` - Production-ready code
- `develop` - Integration branch for feature development
- `feature/feature-name` - Feature development branches
- `bugfix/issue-name` - Bug fix branches

### Commit Message Format
```
[SERVICE-NAME] Short description of changes

Longer description with more context if needed.

Issue: #123
```

### Code Style
- Follow Google Java Style Guide
- Use meaningful variable and method names
- Include JavaDoc for public methods
- Write unit tests for all new features

### Pull Request Process
1. Create a feature branch from `develop`
2. Implement your changes with appropriate tests
3. Ensure all tests pass locally
4. Submit a PR to `develop` with a clear description
5. Require 2 reviewer approvals before merging

## Project Timeline
- **Core Architecture** (Weeks 1-2)
- **User Modules** (Weeks 3-5)
- **Product Modules** (Weeks 6-8)
- **Delivery System** (Weeks 9-10)
- **Chat Features** (Weeks 11-12)
- **Testing & Polish** (Weeks 13-14)

## Success Metrics
- **90%** successful QR pickups
- **<5%** cart abandonment
- **75%** user retention at 30 days

## API Documentation
API documentation is available via Swagger UI at:
- http://localhost:8080/swagger-ui.html (when running locally)

## Contributing
1. Pick a task from the ClickUp board
2. Follow the branching strategy and development guidelines
3. Create a PR with appropriate tests and documentation
4. Request a review from the tech leads

## License
[Proprietary] - © 2024 PrettyGirlz LLC, All Rights Reserved

## Contact
For technical issues, contact the development team at dev-team@prettygirllz.com
