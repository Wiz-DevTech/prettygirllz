# Social Commerce Service

A microservice for social commerce functionality including product chat, community feed, and content moderation.

## Features

- **Product Chat**: Real-time chat functionality for product discussions
- **Community Feed**: Personalized content feed with recommendations
- **Content Moderation**: Automated and manual content review system

## Architecture

The service is built using Spring Boot and follows a modular architecture:

```
social-commerce-service/
├── chat/           # Product chat module
├── feed/           # Community feed module
├── moderation/     # Content moderation module
└── common/         # Shared utilities and configurations
```

## Prerequisites

- Java 17
- Maven 3.8+
- PostgreSQL 15+
- Docker (optional)

## Getting Started

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd social-commerce-service
   ```

2. Set up the database:
   ```bash
   psql -U postgres -f config/db-init.sql
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

Alternatively, use Docker:
```bash
docker-compose up
```

## Configuration

The application can be configured using properties files:
- `config/application-dev.properties`: Development configuration
- `config/application-prod.properties`: Production configuration

## API Documentation

See [API.md](docs/API.md) for detailed API documentation.

## Architecture

See [ARCHITECTURE.md](docs/ARCHITECTURE.md) for system architecture details.

## Database Schema

See [DB_SCHEMA.md](docs/DB_SCHEMA.md) for database schema documentation.

## Testing

Run tests with:
```bash
mvn test
```

## Deployment

Use the provided deployment script:
```bash
./scripts/deploy.sh
```

## Contributing

1. Create a feature branch
2. Make your changes
3. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
