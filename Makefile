TOP_DIR := $(shell git rev-parse --show-toplevel)
SRC_DIR := $(TOP_DIR)/src
DOCKER_COMPOSE_FILE := $(SRC_DIR)/docker-compose.yml
COMPOSE_CMD := docker compose -f $(DOCKER_COMPOSE_FILE)

.PHONY: help up down logs ps restart ollama-pull ollama-list jenkins-shell ollama-shell clean

help: ## Show this help message
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}'

up: ## Docker Compose Up
	@$(COMPOSE_CMD) up -d

down: ## Docker Compose Down
	@$(COMPOSE_CMD) down

logs: ## Show Jenkins Logs
	@$(COMPOSE_CMD) logs jenkins

ps: ## List Running Containers
	@$(COMPOSE_CMD) ps

restart: ## Restart All Services
	@$(COMPOSE_CMD) restart

ollama-pull: ## Pull Ollama Model (usage: make ollama-pull MODEL=<name>)
	@$(COMPOSE_CMD) exec ollama ollama pull $(MODEL)

ollama-list: ## List Ollama Models
	@$(COMPOSE_CMD) exec ollama ollama list

jenkins-shell: ## Open Shell in Jenkins Container
	@$(COMPOSE_CMD) exec jenkins /bin/bash

ollama-shell: ## Open Shell in Ollama Container
	@$(COMPOSE_CMD) exec ollama /bin/sh

clean: ## Stop and Remove Containers and Volumes
	@$(COMPOSE_CMD) down -v