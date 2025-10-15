# Auth Service - Microservice d'Authentification

[![Java](https://img.shields.io/badge/Java-11-blue.svg)](https://openjdk.java.net/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-orange.svg)](https://jakarta.ee/)
[![WildFly](https://img.shields.io/badge/WildFly-27+-green.svg)](https://www.wildfly.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791.svg)](https://www.postgresql.org/)

Microservice Jakarta EE pour la gestion de l'authentification, des utilisateurs, des rôles/permissions et des clients (KYC).

## 📋 Table des matières

- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Déploiement](#déploiement)
- [Documentation API](#documentation-api)
- [Tests](#tests)
- [Sécurité](#sécurité)
- [Contribuer](#contribuer)

## ✨ Fonctionnalités

### Authentification & Autorisation
- ✅ Authentification JWT (Access Token + Refresh Token)
- ✅ Gestion des rôles et permissions (RBAC)
- ✅ Protection des routes avec middleware
- ✅ Révocation de tokens (blacklist)
- ✅ Hashage sécurisé des mots de passe (SHA-256)

### Gestion des Utilisateurs
- ✅ CRUD complet des utilisateurs
- ✅ Association utilisateur ↔ personne (OneToOne)
- ✅ Attribution de rôles multiples
- ✅ Activation/désactivation de comptes
- ✅ Changement et réinitialisation de mot de passe
- ✅ Historique des modifications (audit)

### Gestion des Clients (KYC)
- ✅ Enregistrement clients (particuliers/entreprises)
- ✅ Processus KYC complet
- ✅ Upload et gestion des documents d'identité
- ✅ Vérification et validation des documents
- ✅ Calcul du score de risque
- ✅ Historique d'audit complet

### Configuration Centralisée
- ✅ Configuration des routes type Laravel
- ✅ Sécurité déclarative (rôles/permissions)
- ✅ Endpoint de visualisation des routes

## 🏗️ Architecture
```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/com/archer/cbs/authservice/
│   │   │   ├── config/          # Configurations (OpenAPI, Security, Routes)
│   │   │   ├── dao/             # Data Access Objects
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # Entités JPA
│   │   │   ├── enums/           # Énumérations
│   │   │   ├── filter/          # Filtres JAX-RS (CORS, etc.)
│   │   │   ├── mapper/          # Mappers Entity ↔ DTO
│   │   │   ├── resource/        # Endpoints REST
│   │   │   ├── security/        # Sécurité (JWT, Filters, Annotations)
│   │   │   └── service/         # Logique métier
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           ├── beans.xml
│   │           └── web.xml
│   └── test/                    # Tests unitaires et d'intégration
├── docs/                        # Documentation
├── pom.xml
└── README.md
```

### Stack Technique

- **Framework**: Jakarta EE 10
- **Serveur**: WildFly 27+
- **Base de données**: PostgreSQL 15+
- **ORM**: JPA (Hibernate)
- **API REST**: JAX-RS
- **Sécurité**: JWT (JJWT), Custom Filters
- **Documentation**: OpenAPI 3.1, JavaDoc
- **Build**: Maven 3.8+

## 📦 Prérequis

- Java 11+
- Maven 3.8+
- PostgreSQL 15+
- WildFly 27+

## 🚀 Installation

### 1. Cloner le projet
```bash
git clone https://github.com/Marcel-Mboyo/auth-service.git
cd auth-service
```

### 2. Créer la base de données
```sql
CREATE DATABASE auth_db;
\c auth_db

-- Exécuter les scripts SQL dans docs/sql/
\i docs/sql/01-schema.sql
\i docs/sql/02-data.sql
```

### 3. Configurer la datasource WildFly

Éditer `$WILDFLY_HOME/standalone/configuration/standalone.xml` :
```xml
<datasource jndi-name="java:/PostgresDS" pool-name="PostgresDS">
    <connection-url>jdbc:postgresql://localhost:5432/auth_db</connection-url>
    <driver>postgresql</driver>
    <security>
        <user-name>postgres</user-name>
        <password>votre_mot_de_passe</password>
    </security>
</datasource>
```

### 4. Compiler le projet
```bash
mvn clean package
```

### 5. Déployer sur WildFly
```bash
cp target/auth-service-1.0-SNAPSHOT.war $WILDFLY_HOME/standalone/deployments/
```

Ou avec Maven :
```bash
mvn wildfly:deploy
```

## ⚙️ Configuration

### Variables d'environnement

Créer un fichier `.env` (ou configurer dans WildFly) :
```properties
# Base de données
DB_HOST=localhost
DB_PORT=5432
DB_NAME=auth_db
DB_USER=postgres
DB_PASSWORD=admin

# JWT
JWT_SECRET=VotreCleSecreteTresLongue...
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Application
APP_ENV=development
APP_URL=http://localhost:8080/auth-service-1.0-SNAPSHOT
APP_DEBUG=true

# Upload de fichiers KYC
KYC_UPLOAD_DIR=/var/uploads/kyc
KYC_MAX_FILE_SIZE=10485760
KYC_ALLOWED_EXTENSIONS=pdf,jpg,jpeg,png

# Email (pour notifications)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=votre_email@gmail.com
SMTP_PASSWORD=votre_password
SMTP_FROM=noreply@archer-cbs.com

# Logging
LOG_LEVEL=INFO
```