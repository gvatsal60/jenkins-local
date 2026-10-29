# 🚀 Jenkins Local

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://img.shields.io/github/license/gvatsal60/jenkins-local)
[![pre-commit.ci status](https://results.pre-commit.ci/badge/github/gvatsal60/jenkins-local/master.svg)](https://results.pre-commit.ci/latest/github/gvatsal60/jenkins-local/HEAD)
[![Docker Compose](https://img.shields.io/badge/Docker_Compose-required-blue)](https://docs.docker.com/compose/)

A local development environment with **Jenkins** running via Docker Compose.

## 🛠️ Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## 🚀 Quick Start

```sh
git clone https://github.com/gvatsal60/jenkins-local.git
cd jenkins-local
make up
```

## 📋 Services

| Service   | Port    | Description                  |
|-----------|---------|------------------------------|
| Jenkins   | `8080`  | CI/CD automation server      |

### Jenkins

Access Jenkins at `http://localhost:8080`

> Default credentials: `admin` / `admin` (override via `JENKINS_ADMIN_ID` and `JENKINS_ADMIN_PASSWORD`).

Initial admin password:

```sh
make jenkins-pass
```

## 🧰 Make Commands

```sh
make all                        # Start all services (default target)
make up                         # Start all services
make down                       # Stop all services
make logs                       # Show Jenkins logs
make ps                         # List running containers
make restart                    # Restart all services
make jenkins-pass               # Show Jenkins initial admin password
make jenkins-shell              # Open shell in Jenkins container
make clean                      # Stop and remove containers and volumes
make test                       # Validate Docker Compose configuration
make help                       # Show all available commands
```

## 🛡️ License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
