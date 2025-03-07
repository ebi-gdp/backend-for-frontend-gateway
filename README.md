# Backend for Frontend (BFF) Service

## Overview

The **Backend for Frontend (BFF)** service plays a crucial role in managing authentication, session handling, and secure communication between the frontend and backend microservices in the **IGS4EU Genetic Scoring Platform**. It acts as an intermediary, ensuring that access tokens are securely managed and passed to backend services while providing a seamless authentication experience for users.

This project is a Java Spring Boot application managed with Maven. Look for `application.properties` & `application-{env}.properties` files. There are total 3 environments dev, test & prod.
Changes to be made on this property file(s) according to environment.

## Key responsibilities

- **Session Management in Redis**
    - The BFF service stores user sessions in **Redis** for quick and efficient session validation.
    - Each session is linked to an **Access Token**, which is securely retrieved when needed.

- **Access Token Exchange & Storage**
    - Upon successful authentication, the BFF exchanges session details for an **Access Token**.
    - The **Access Token** is securely stored in a database for future use.
    - When a request is received, the BFF retrieves the **Access Token** and appends it to requests sent to backend services.

- **Authentication via Life Science AAI**
    - The BFF initiates the **Life Science AAI login process**, facilitating user authentication.
    - After successful authentication, the BFF creates a session and sets an **HTTP-only session cookie** on the client (e.g., browser).

- **Secure API Gateway**
    - All requests from the **React.js frontend** pass through the BFF.
    - The **HTTP-only session cookie** is validated before processing any request.
    - If the session is valid, the corresponding **Access Token** is retrieved from the database and used to authenticate requests to backend services.

## Request flow

1. **User Login**
    - The frontend redirects the user to **Life Science AAI login** via the BFF.
    - Upon successful authentication, the BFF creates a session and sets an **HTTP-only session cookie**.

2. **Frontend Requests**
    - When the user interacts with the frontend, requests are sent to the BFF.
    - The BFF verifies the session from the **HTTP-only cookie**.

3. **Token Retrieval & Request Forwarding**
    - If the session is valid, the **Access Token** is retrieved from the database.
    - The BFF appends the **Access Token** to the request and forwards it to the appropriate backend service.

4. **Response Handling**
    - The backend service processes the request and sends a response back to the BFF.
    - The BFF forwards the response to the frontend.

## Security considerations

- **Session Security**
    - Uses **HTTP-only cookies** to prevent access from JavaScript, mitigating **XSS attacks**.
- **Access Token Protection**
    - Tokens are **not exposed** to the frontend; only the BFF handles token management.
- **Centralized Authentication**
    - The BFF ensures all authentication flows are centralized, reducing security risks.

The **Backend for Frontend (BFF) service** is a key component in the **Genetic Scoring Platform**, ensuring secure authentication, session management, and controlled access to backend services. By acting as a gateway, it protects **Access Tokens**, enforces security policies, and simplifies the interaction between the frontend and backend microservices.

## Prerequisites

Before you can build and run this project, ensure that the following tools/softwares are installed on your system

### 1. **Java Development Kit (JDK)**
- **Version**: Java 19 or later (Tested on version 19).
- **Installation**:
    - [Download JDK from Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://openjdk.java.net/).
    - Set `JAVA_HOME` environment variable to java installation path. You can check whether it has already been set.
      ```
      # Unix system
      echo $JAVA_HOME
      
      # Windows system
      echo %JAVA_HOME%
      ```
      if it is empty, then follow below steps according to your system. This is shell/commandline local variable.
      ```
      # Unix system
      export JAVA_HOME=/path/to/jdk/installation
      
      # Windows system
      set JAVA_HOME=/path/to/jdk/installation
      ```
      In case you want more details, you can follow [Set JAVA_HOME Variable](https://www.baeldung.com/java-home-on-windows-mac-os-x-linux).
- **Verify installation**:
  ```bash
  java -version
  ```

### 2. **Apache Maven** [optional]
(This is an optional step, you can use maven wrapper included in this project, refer to [Build the project](#how-to-build) section)
- **Version**: Maven 3.9.1 or later (Tested on version 3.9.1, you can try lower version if it works).
- **Installation**:
    - [Download Apache Maven](https://maven.apache.org/download.cgi).
    - Follow the installation steps and set the `M2_HOME` and `JAVA_HOME` environment variables if not set.
- **Verify installation**:
  ```bash
  mvn -v
  ```

### 3. **Git**
- **Version**: git 2.39 or later (Tested on version 2.39.5, you can try lower version if it works).
- **Installation**:
    - [Download Git](https://git-scm.com/downloads).
    - Follow the installation steps.
- **Verify installation**:
  ```bash
  git -v
  ```
---

# Application properties

## Application configuration `application.properties`

| Property                   | Value                          | Description                                      |
|----------------------------|--------------------------------|--------------------------------------------------|
| `spring.application.name`  | `backend-for-frontend-gateway` | Defines the name of the Spring Boot application. |
| `spring.webflux.base-path` | `/bff`                         | Sets the base path for WebFlux endpoints.        |
| `spring.profiles.active`   | `${ENV:dev}`                   | Specifies the active Spring profile.             |

## Metrics configuration

| Property                                         | Value                            | Description                                     |
|--------------------------------------------------|----------------------------------|-------------------------------------------------|
| `management.endpoints.web.exposure.include`      | `health,metrics,info,prometheus` | Defines which management endpoints are exposed. |

## Application Configuration `application-{env}.properties`

> **Note:** The following configuration is specific to the **development environment**. For `test` and `production`, values should be adjusted accordingly.

## General Configuration

| Parameter     | Value  | Description                        |
|---------------|--------|------------------------------------|
| `server.port` | `8080` | Port on which the application runs |

## OAuth2 Client Registration (Life Science AAI Login)

| Parameter                                                                        | Value                                                                  | Description                                     |
|----------------------------------------------------------------------------------|------------------------------------------------------------------------|-------------------------------------------------|
| `spring.security.oauth2.client.registration.elixir.client-name`                  | `Login with the Elixir Identity Server`                                | OAuth2 client name.                             |
| `spring.security.oauth2.client.registration.elixir.client-id`                    | `@elixir.client.id@`                                                   | Client ID for OAuth2 authentication.            |
| `spring.security.oauth2.client.registration.elixir.client-authentication-method` | `none`                                                                 | Authentication method used.                     |
| `spring.security.oauth2.client.registration.elixir.authorization-grant-type`     | `authorization_code`                                                   | Grant type for authorization.                   |
| `spring.security.oauth2.client.registration.elixir.redirect-uri`                 | `https://gcp.geneticscores.org/bff/login/oauth2/code/{registrationId}` | Redirect URI after successful authentication.   |
| `spring.security.oauth2.client.registration.elixir.scope[0]`                     | `openid`                                                               | Scope for authentication.                       |
| `spring.security.oauth2.client.registration.elixir.scope[1]`                     | `profile`                                                              | User profile scope.                             |
| `spring.security.oauth2.client.registration.elixir.scope[2]`                     | `email`                                                                | Email scope.                                    |
| `spring.security.oauth2.client.registration.elixir.scope[3]`                     | `offline_access`                                                       | Scope for offline access.                       |
| `spring.security.oauth2.client.provider.elixir.issuer-uri`                       | `https://login.elixir-czech.org/oidc/`                                 | OIDC issuer URI.                                |
| `spring.security.oauth2.client.provider.elixir.success-url`                      | `https://gcp.geneticscores.org/pgs-calculator`                         | URL to redirect upon successful authentication. |
| `spring.security.oauth2.client.logout-uri`                                       | `/logout`                                                              | Logout endpoint.                                |

## Database Configuration (R2DBC)

| Parameter               | Value                                                                                     | Description                                            |
|-------------------------|-------------------------------------------------------------------------------------------|--------------------------------------------------------|
| `spring.r2dbc.url`      | `r2dbc:postgresql://127.0.0.1:5432/intervene?currentSchema=intervene-dev&sslMode=disable` | Database connection string.                            |
| `spring.r2dbc.username` | `${DB_USERNAME}`                                                                          | Database username (fetched from environment variable). |
| `spring.r2dbc.password` | `${DB_PASSWORD}`                                                                          | Database password (fetched from environment variable). |

### R2DBC connection string details

| Parameter               | Value            | Description                                            |
|-------------------------|------------------|--------------------------------------------------------|
| `spring.r2dbc.protocol` | `r2dbc`          | Specifies the R2DBC protocol.                          |
| `spring.r2dbc.driver`   | `postgresql`     | Specifies the database driver.                         |
| `spring.r2dbc.host`     | `127.0.0.1`      | Database host (IP or domain).                          |
| `spring.r2dbc.port`     | `5432`           | Database port (default for PostgreSQL).                |
| `spring.r2dbc.database` | `intervene`      | Database name.                                         |
| `spring.r2dbc.schema`   | `intervene-dev`  | Schema within the database.                            |
| `spring.r2dbc.sslMode`  | `disable`        | SSL mode for database connection.                      |

## API Gateway Configuration

| Route ID           | URI                                                            | Description                                |
|--------------------|----------------------------------------------------------------|--------------------------------------------|
| `file-handler`     | `http://file-handler.intervene-dev.svc.cluster.local:8080`     | Base URL for the File Handler Service.     |
| `pipeline-manager` | `http://pipeline-manager.intervene-dev.svc.cluster.local:8080` | Base URL for the Pipeline Manager Service. |
| `user-manager`     | `http://user-manager.intervene-dev.svc.cluster.local:8080`     | Base URL for the User Manager Service.     |
| `key-handler`      | `http://key-handler.intervene-dev.svc.cluster.local:8080`      | Base URL for the Key Handler Service.      |

## Redis Configuration

| Parameter                  | Value                                   | Description            |
|----------------------------|-----------------------------------------|------------------------|
| `spring.redis.host`        | `redis.intervene-dev.svc.cluster.local` | Redis server hostname. |
| `spring.redis.port`        | `6379`                                  | Redis server port.     |
| `spring.redis.client-type` | `lettuce`                               | Redis client type.     |

## Session Management

| Parameter                        | Value   | Description                       |
|----------------------------------|---------|-----------------------------------|
| `spring.session.store-type`      | `redis` | Session store type.               |
| `spring.session.timeout`         | `5h`    | Session timeout duration.         |
| `spring.webflux.session.timeout` | `5h`    | WebFlux session timeout duration. |

## Logging Configuration

| Parameter                                          | Value  | Description                     |
|----------------------------------------------------|--------|---------------------------------|
| `logging.level.root`                               | `INFO` | Log level for root application. |
| `logging.level.uk.ac.ebi.gdp.intervene.igs4eu.bff` | `INFO` | Log level for BFF service.      |
| `logging.level.org.springframework.security`       | `INFO` | Log level for Spring Security.  |
| `logging.level.org.springframework.cloud`          | `INFO` | Log level for Spring Cloud.     |

## Web Session Cookie Configuration

| Parameter                      | Value                   | Description        |
|--------------------------------|-------------------------|--------------------|
| `web-session.cookie.path`      | `/`                     | Cookie path.       |
| `web-session.cookie.domain`    | `gcp.geneticscores.org` | Domain for cookie. |
| `web-session.cookie.http-only` | `true`                  | HTTP-only flag.    |
| `web-session.cookie.same-site` | `Lax`                   | Same-site policy.  |
| `web-session.cookie.secure`    | `true`                  | Secure flag.       |

## Web Client Configuration

| Parameter             | Value                           | Description              |
|-----------------------|---------------------------------|--------------------------|
| `web-client.base-url` | `https://gcp.geneticscores.org` | Base URL for web client. |

## Whitelist Paths

| Value                               | Description                       |
|-------------------------------------|-----------------------------------|
| `/pipeline-manager/integration/**`  | Pipeline manager integration API. |
| `/key-handler/key/**`               | Key handler key API path.         |
| `/user-manager/actuator/health`     | User manager health endpoint.     |
| `/pipeline-manager/actuator/health` | Pipeline manager health endpoint. |
| `/file-handler/actuator/health`     | File handler health endpoint.     |
| `/key-handler/actuator/health`      | Key handler health endpoint.      |

---

## How to build?

Follow these steps to build the project:

1. **Clone the repository**:
   If you haven't already, clone the project repository to your local machine.
   ```bash
   # Clone git repo.
   git clone https://github.com/ebi-gdp/backend-for-frontend-gateway.git
   
   # Get into cloned directory.
   cd backend-for-frontend-gateway
   ```

2. **Build the project**: [2 options, either can be used]
    1. Use Maven bundled in this project to compile and package the project (preferred).
       ```bash
       # Unix system
       ./mvnw clean install
       
       # OR
       ./mvnw clean package
       
       -------------------------
       
       # Windows system
       mvnw.cmd clean install
       
       # OR
       mvnw.cmd clean package
       ``` 
       OR
    2. Use Maven installed on your system to compile and package the project.
       ```bash
       mvn clean install
       ```
       OR        
       ```bash
       mvn clean package
       ``` 
       Both of these commands will download necessary dependencies, compile the source code, and generate a `.jar` file in the `target/` directory.

## Development workflow

### Making changes
1. Check out a new branch from GitHub
   ```bash
   git checkout -b <feature-branch>
   ```
2. Make necessary changes in the project.
3. Build the project to ensure changes are complied!
   ```bash
   mvn clean package
   ```
4. Commit the changes if you are okay with it! You can follow `Git` best practices to commit changes.
5. Push the branch e.g.
   ```bash
   git push origin <feature-branch>
   ```
6. In case of code changes, `gitlab` pipeline is expected to trigger. Once gitlab pipeline executes successfully for a branch, raise a pull request. If pull request doesn't have any conflicts, you are good to merge branch with `main` branch.
7. After merging changes to `main` branch you can release the project. Deployment steps have been mentioned under `Deployments` section below.

### Run application

1. Build the application as mentioned in [How to build](#How-to-build) section.
2. Make sure you have built the project with necessary `application.properties` & `application-{env}.properties`.
3. Run the application
   ```
   # Once you build the service, jar file generates inside target dir.
   java -jar target/{jar file}
   ```
   Example
   ```
   java -jar target/backend-for-frontend-gateway-1.0.8.jar
   ```
   Alternatively you can run the project via IDE e.g. IntelliJ IDEA.
4. By default services should be up & running at
   ```
   http://localhost:8090/bff
   ```

### Releasing Code
Once the project builds successfully, prepare for release, you can use maven wrapper as shown below or maven installed on your system.
```bash
./mvnw -Darguments=-DskipTests release:clean release:prepare
```
Provide appropriate version number based on changes e.g. Major, minor & patch etc.

This command cleans up previous release data and prepares a new release while skipping tests.

***IMPORTANT***:
After releasing the project, check tag on [backend-for-frontend-gateway](https://github.com/ebi-gdp/backend-for-frontend-gateway/tags).
You should see the tag you have just released. This repository is synced with Gitlab, navigate to Gitlab instance [backend-for-frontend-gateway-gitlab](https://gitlab.ebi.ac.uk/gdp/backend-for-frontend-gateway).

### Deployments - 2 options
#### 1. Gitlab CI/CD
Currently, deployment is being handled via `Gitlab` CI/CD. Details steps are defined at [Deploy using Gitlab](https://www.ebi.ac.uk/seqdb/confluence/x/cZNEE).

#### 2. Helm charts without CI/CD
Microservice can be deployed in 2 ways via CI/CD using `gitlab` or individually using `helm` charts; `gitlab` also uses `helm` charts. Use `gitlab` for deployments, it's preferred way.
You can use direct `helm` command, `helm` charts scripts are defined under `deployments` directory.

Helm chart structure
```
deployments
  |- files
     |- application-gcp-{env}-env.properties
  |- templates
     |- configmap.yaml
     |- gcp-deployment.yaml
     |- health-check-deployment.yaml
  |- Chart.yaml
  |- values-gcp-{env}-env.yaml
```
Example
```
deployments
  |- files
     |- application-gcp-dev-env.properties
     |- application-gcp-test-env.properties
     |- application-gcp-prod-env.properties
  |- templates
     |- configmap.yaml
     |- gcp-deployment.yaml
     |- health-check-deployment.yaml
  |- Chart.yaml
  |- values-gcp-dev-env.yaml
  |- values-gcp-test-env.yaml
  |- values-gcp-prod-env.yaml 
```
This is standard helm format, in order to deploy service, it is important to look for `files` & `values` files. Whenever there are any property file changes
make sure you update property files. In case change differs according env. then make those changes inside values files. Deployment file refers values files according to env.
Also when you release underlying application's new version, you can update `appVersion` inside `Chart.yaml`.

You can deploy service directly using `helm` charts. Sometimes after individual deployments, dependant services requires restart, if you face connection issues with other services then restart calling & dependant pods.
Run these commands from project's home directory. Before you run these command make sure you have updated `helm` files e.g. `application.properties`. Code should have been released with latest version & `docker` image has been created for the services.
`image.tag` sets `docker` image tag, you can use temporary tag in case want to test deployment OR use actual released tag for deployment.

1. Build docker image without CI/CD
   Build the project as mentioned [How to build](#How-to-build) & generate jar file for service & then run the following
   ```
   docker build -f docker/Dockerfile -t dockerhub.ebi.ac.uk/gdp/backend-for-frontend-gateway:1.0.0-dev --build-arg TARGET_PLATFORM=linux/amd64 .
   ```
2. Push docker image to gitlab container registry, make sure you have access to gitlab project.
   ```
   docker push dockerhub.ebi.ac.uk/gdp/backend-for-frontend-gateway:1.0.0-dev
   ```
3. Run helm command to deploy service.
   ```
   helm upgrade --install backend-for-frontend "./deployments" -f "./deployments/values-gcp-{env}-env.yaml" \
      --namespace {namespace} \
      --set image.tag={image-released-tag}
   ```
   Example:
   ```
   helm upgrade --install backend-for-frontend "./deployments" -f "./deployments/values-gcp-dev-env.yaml" \
      --namespace intervene-dev \
      --set image.tag=1.0.0
   ```
Check deployment status on kubernetes cluster. e.g.
   ```
   # Change namespace according to env.
   kubectl get pods -n intervene-dev
   ```

You can delete release by running following command
```
# command
helm delete {release-name}

# example
helm delete backend-for-frontend
```
