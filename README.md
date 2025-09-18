# ProjectAngularMongoDB

## JPA to MongoDB Refactoring Complete

This project has been successfully refactored from JPA (Java Persistence API) to MongoDB while maintaining **100% backward compatibility** with the frontend by preserving all DTOs unchanged.

## Quick Start Guide

### Option 1: Use Docker MongoDB (Recommended for new setups)

If you don't have MongoDB installed or want a clean development environment:

```bash
# Start both MongoDB and the application
docker-compose up --build

# The application will be available at http://localhost:8080
# MongoDB will be available at localhost:27017
```

### Option 2: Use Local MongoDB (For existing MongoDB users)

If you already have MongoDB installed locally (like with MongoDB Compass):

```bash
# Ensure your local MongoDB is running on localhost:27017
# Create the database 'angulartts-mongo' (or it will be created automatically)

# Option 2a: Run Spring Boot app locally
mvn spring-boot:run

# Option 2b: Run only the app in Docker, connecting to local MongoDB
docker-compose -f docker-compose.local.yml up --build
```

### Database Configuration

The application uses the following MongoDB configuration:
- **Host**: `localhost` (configurable via `MONGO_HOST`)
- **Port**: `27017` (configurable via `MONGO_PORT`)  
- **Database**: `angulartts-mongo` (configurable via `MONGO_DB`)

## Docker Configuration Options

### Full Docker Stack (docker-compose.yml)
- MongoDB container + Spring Boot application
- Perfect for new development environments
- Isolated and reproducible setup

### Local MongoDB + Docker App (docker-compose.local.yml)  
- Uses your existing MongoDB installation
- Only runs the Spring Boot application in Docker
- Great for users with MongoDB Compass or existing MongoDB setups

### No Docker (Local Development)
- Run MongoDB locally
- Run Spring Boot with `mvn spring-boot:run`
- Configure connection in `src/main/resources/application-dev.yml`

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
7. ✅ Docker configuration provided for both local and containerized MongoDB

The refactoring is complete and maintains full backward compatibility while leveraging MongoDB's capabilities.