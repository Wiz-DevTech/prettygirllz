# Social Commerce Service Architecture

## System Overview
The Social Commerce Service is built using Spring Boot and follows a modular architecture.

## Modules

### Chat Module
Handles real-time product discussions using WebSocket and STOMP protocol.

### Feed Module
Manages community content feed with personalized recommendations.

### Moderation Module
Provides content moderation capabilities with automated and manual review processes.

### Common Module
Contains shared utilities, security, and configuration.

## Technology Stack
- Java 17
- Spring Boot 3.x
- PostgreSQL
- WebSocket/STOMP
- JWT Authentication
