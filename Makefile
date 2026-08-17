TOP_DIR := $(shell git rev-parse --show-toplevel)
SRC_DIR := $(TOP_DIR)/src
DOCKER_COMPOSE_FILE := $(SRC_DIR)/docker-compose.yml
COMPOSE_CMD := docker compose -f $(DOCKER_COMPOSE_FILE)

.PHONY: all reset clean down help jenkins-pass jenkins-shell logs ollama-list ollama-pull ollama-shell ps restart test up

all: clean up

help: ## Show this help message
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'

test: ## Validate Docker Compose Configuration
	@$(COMPOSE_CMD) config --quiet

up: test ## Docker Compose Up
	@$(COMPOSE_CMD) up -d

ps: ## List Running Containers
	@$(COMPOSE_CMD) ps

down: ## Docker Compose Down
	@$(COMPOSE_CMD) down

restart: up ## Restart All Services
	@$(COMPOSE_CMD) restart

logs: up ## Show Jenkins Logs
	@$(COMPOSE_CMD) logs jenkins

clean: ## Stop and Remove Containers and Volumes
	@$(COMPOSE_CMD) down --volumes --remove-orphans

reset:  ## Reset the Development Environment
	@$(COMPOSE_CMD) down --volumes --rmi all --remove-orphans

jenkins-pass: up ## Show Jenkins Initial Admin Password
	@$(COMPOSE_CMD) exec -T jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null

jenkins-shell: up ## Open Shell in Jenkins Container
	@$(COMPOSE_CMD) exec jenkins /bin/bash

ollama-list: up ## List Ollama Models
	@$(COMPOSE_CMD) exec ollama ollama list

ollama-pull: up ## Pull Ollama Model (usage: make ollama-pull MODEL=<name>)
	@$(COMPOSE_CMD) exec ollama ollama pull $(MODEL)

ollama-shell: up ## Open Shell in Ollama Container
	@$(COMPOSE_CMD) exec ollama /bin/sh
