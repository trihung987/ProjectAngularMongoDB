#!/bin/bash

# Test script for verifying the getOrdersPaged fix
# This script demonstrates how to test the API endpoint

echo "=== Testing getOrdersPaged Fix ==="
echo

# Check if the application is running
echo "1. Make sure your MongoDB is running and the application is started"
echo "   Run: mvn spring-boot:run"
echo

# Test the API endpoint
echo "2. Test the orders endpoint with curl:"
echo "   curl -X GET 'http://localhost:8080/api/v1/orders?page=0&size=10' -H 'Authorization: Bearer YOUR_JWT_TOKEN'"
echo

echo "3. Expected response structure:"
cat << 'EOF'
{
  "content": [
    {
      "id": "order123",
      "zoneId": "zone456", 
      "ownerId": "user789",
      "quantity": 2,
      "totalAmount": 200.00,
      "createdAt": "2024-01-01T10:00:00",
      "priceZone": 100.00,
      "nameZone": "VIP Zone",
      "nameEvent": "Concert Event"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "page": 0,
  "size": 10
}
EOF

echo
echo "4. To enable debug logging, add this to application.yml:"
cat << 'EOF'
logging:
  level:
    me.trihung.service.impl.MongoOrderServiceImpl: DEBUG
    me.trihung.repository.CustomOrderRepositoryImpl: DEBUG
EOF

echo
echo "5. To run the debug utility, set ENABLE_DEBUG=true in OrdersDebugRunner.java"
echo
echo "=== Fix Summary ==="
echo "✅ Simplified MongoDB aggregation pipeline"
echo "✅ Added comprehensive error handling"  
echo "✅ Implemented fallback mechanisms"
echo "✅ Added detailed logging"
echo "✅ Created unit tests"
echo "✅ Added documentation"
echo
echo "The getOrdersPaged method should now work correctly!"