# CORS Configuration Fix

## Problem
Frontend Angular application at `http://localhost:4200` was unable to call backend API at `http://localhost:8080` due to CORS (Cross-Origin Resource Sharing) errors:

```
Cross-Origin Request Blocked: The Same Origin Policy disallows reading the remote resource at http://localhost:8080/api/v1/auth/login. (Reason: CORS header 'Access-Control-Allow-Origin' missing). Status code: 403.
```

## Root Cause
The CORS configuration was present but had several issues:
1. Used `setAllowedOrigins` instead of the more flexible `setAllowedOriginPatterns`
2. Missing explicit OPTIONS method handling in security filter chains
3. No WebMVC-level CORS configuration as backup

## Solution
### 1. Enhanced CorsConfig.java
- Changed `setAllowedOrigins` to `setAllowedOriginPatterns` for better Spring Security compatibility
- Added `setExposedHeaders(List.of("*"))` to expose response headers
- Added `setMaxAge(3600L)` to cache preflight requests

### 2. Updated SecurityConfig.java
- Added explicit OPTIONS method handling in both security filter chains:
  ```java
  .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
  ```

### 3. Enhanced WebConfig.java
- Added WebMVC-level CORS configuration as a backup:
  ```java
  @Override
  public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/api/**")
              .allowedOriginPatterns("http://localhost:4200")
              .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
              .allowedHeaders("*")
              .allowCredentials(true)
              .maxAge(3600);
  }
  ```

## Testing Results
✅ **All tests passed:**
- OPTIONS preflight requests return 200 OK with proper CORS headers
- GET/POST requests include required CORS headers
- Frontend can now successfully communicate with backend

## CORS Headers Confirmed Working:
```
Access-Control-Allow-Origin: http://localhost:4200
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
Access-Control-Allow-Headers: Content-Type
Access-Control-Expose-Headers: *
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

## Files Modified
- `src/main/java/me/trihung/auth/CorsConfig.java`
- `src/main/java/me/trihung/auth/SecurityConfig.java`
- `src/main/java/me/trihung/auth/WebConfig.java`
- `src/main/java/me/trihung/controller/CorsTestController.java` (added for testing)