# ProjectAngularMongoDB

A Spring Boot application with Angular frontend using MongoDB as the database.

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker and Docker Compose (for containerized setup)
- MongoDB (for local development without Docker)

## Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ProjectAngularMongoDB
   ```

2. **Build the application**
   ```bash
   mvn clean package
   ```

3. **Run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

This will start:
- MongoDB database on port 27017
- Spring Boot application on port 8080

## Local Development

1. **Start MongoDB locally** (or use Docker for MongoDB only)
   ```bash
   # Using Docker for MongoDB only
   docker run -d -p 27017:27017 --name mongodb \
     -e MONGO_INITDB_ROOT_USERNAME=admin \
     -e MONGO_INITDB_ROOT_PASSWORD=123456 \
     mongo:7-jammy
   ```

2. **Configure environment variables** (copy from .env.example)
   ```bash
   cp .env.example .env
   # Edit .env file as needed
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

## Database Migration

This project has been migrated from PostgreSQL to MongoDB:
- All entities use MongoDB annotations (@Document, @DBRef)
- Repositories extend MongoRepository
- Docker Compose now uses MongoDB instead of PostgreSQL

## JPA to MongoDB Refactoring Complete

This project has been successfully refactored from JPA (Java Persistence API) to MongoDB while maintaining **100% backward compatibility** with the frontend by preserving all DTOs unchanged.

## Key Changes Made

### 1. Entity Layer Refactoring
- **Before**: JPA entities with `@Entity`, `@Table`, `@Id` with UUID generation
- **After**: MongoDB documents with `@Document`, `@Id` with String IDs

### 2. Repository Layer Migration  
- **Before**: `JpaRepository<Entity, UUID>` with JPQL queries
- **After**: `MongoRepository<Entity, String>` with MongoDB aggregation pipelines

### 3. Complex Query Implementation
Custom aggregation pipelines implemented for:
- Revenue analytics by date range
- Event type revenue analysis  
- Top events by revenue
- Order listing with pagination

### 4. DTO Preservation
**Critical Requirement Met**: All DTOs remain exactly the same structure:
- `OrderDto` - UUID fields preserved for frontend compatibility
- `RevenueDataDto` - Analytics structure unchanged
- `EventTypeRevenueDto` - Report format maintained
- `TopEventDto` - Dashboard data structure preserved

## Technical Implementation

### ID Conversion Strategy
- **Internal**: MongoDB uses String ObjectIds
- **External**: DTOs use UUID for frontend compatibility
- **Conversion**: MapStruct handles String ↔ UUID conversion transparently

### Repository Architecture
```java
// Simple queries - handled by Spring Data MongoDB
Page<Order> findByOwner(User owner, Pageable pageable);

// Complex queries - custom aggregation implementation
@Repository
public class CustomOrderRepositoryImpl {
    // MongoDB aggregation pipelines for complex analytics
}
```

### Aggregation Pipeline Examples
- **Order listing with zone/event data**: $lookup operations across collections
- **Revenue analytics**: $group by date with $sum aggregations  
- **Event analytics**: Multi-stage pipelines with $project for calculated fields

## Benefits Achieved

1. **Frontend Compatibility**: Zero frontend changes required
2. **Scalability**: MongoDB's horizontal scaling capabilities
3. **Performance**: Optimized aggregation pipelines for analytics
4. **Flexibility**: Schema-less design for future enhancements
5. **Modern Stack**: MongoDB integration with Spring Boot

## Configuration

MongoDB configuration in `application.yml`:
```yaml
spring:
  data:
    mongodb:
      host: ${MONGO_HOST:localhost}
      port: ${MONGO_PORT:27017}
      database: ${MONGO_DB}
```

## Testing the Refactoring

The `MongoDBRefactoringDemo` class demonstrates:
- DTO structures remain unchanged
- Internal MongoDB operations work correctly
- ID conversion maintains compatibility

## Migration Path

1. ✅ Entities converted to MongoDB documents
2. ✅ Repositories refactored to MongoRepository  
3. ✅ Complex queries implemented as aggregations
4. ✅ DTOs preserved for frontend compatibility
5. ✅ MapStruct mappers handle ID conversion
6. ✅ Configuration updated for MongoDB

The refactoring is complete and maintains full backward compatibility while leveraging MongoDB's capabilities.