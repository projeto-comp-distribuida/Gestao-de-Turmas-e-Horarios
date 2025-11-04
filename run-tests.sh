#!/bin/bash

# Script para executar testes do Schedule Management Service
set -e

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=========================================="
echo "TESTES - SCHEDULE MANAGEMENT SERVICE"
echo "==========================================${NC}"
echo ""

# Verifica se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven não encontrado. Por favor, instale o Maven.${NC}"
    exit 1
fi

# Função para executar testes unitários
run_unit_tests() {
    echo -e "${BLUE}📦 Executando testes unitários...${NC}"
    echo "----------------------------------------"
    mvn test -Dtest=EventProducerTest,EventConsumerTest \
        -Dspring.profiles.active=test
    echo -e "${GREEN}✅ Testes unitários concluídos${NC}"
    echo ""
}

# Função para executar testes de integração
run_integration_tests() {
    echo -e "${BLUE}🔗 Executando testes de integração...${NC}"
    echo "----------------------------------------"
    mvn test -Dtest=HealthControllerTest,KafkaIntegrationTest,ApiIntegrationTest \
        -Dspring.profiles.active=test
    echo -e "${GREEN}✅ Testes de integração concluídos${NC}"
    echo ""
}

# Função para executar testes de WebSocket
run_websocket_tests() {
    echo -e "${BLUE}🔌 Executando testes de WebSocket...${NC}"
    echo "----------------------------------------"
    mvn test -Dtest=WebSocketIntegrationTest \
        -Dspring.profiles.active=test
    echo -e "${GREEN}✅ Testes de WebSocket concluídos${NC}"
    echo ""
}

# Função para executar todos os testes
run_all_tests() {
    echo -e "${BLUE}🚀 Executando todos os testes...${NC}"
    echo "----------------------------------------"
    mvn clean test -Dspring.profiles.active=test
    echo -e "${GREEN}✅ Todos os testes concluídos${NC}"
    echo ""
}

# Função para gerar relatório de testes
generate_test_report() {
    echo -e "${BLUE}📊 Gerando relatório de testes...${NC}"
    echo "----------------------------------------"
    mvn surefire-report:report
    echo -e "${GREEN}✅ Relatório gerado em: target/site/surefire-report.html${NC}"
    echo ""
}

# Função para executar testes com cobertura
run_with_coverage() {
    echo -e "${BLUE}📈 Executando testes com cobertura...${NC}"
    echo "----------------------------------------"
    # Requer jacoco plugin no pom.xml
    mvn clean test jacoco:report \
        -Dspring.profiles.active=test
    echo -e "${GREEN}✅ Relatório de cobertura gerado em: target/site/jacoco/index.html${NC}"
    echo ""
}

# Menu principal
show_menu() {
    echo "Escolha uma opção:"
    echo "1) Testes unitários"
    echo "2) Testes de integração"
    echo "3) Testes de WebSocket"
    echo "4) Testes de Kafka"
    echo "5) Todos os testes"
    echo "6) Gerar relatório"
    echo "7) Testes com cobertura"
    echo "0) Sair"
    echo ""
    read -p "Opção: " option
    
    case $option in
        1)
            run_unit_tests
            ;;
        2)
            run_integration_tests
            ;;
        3)
            run_websocket_tests
            ;;
        4)
            echo -e "${BLUE}📨 Executando testes de Kafka...${NC}"
            mvn test -Dtest=KafkaIntegrationTest -Dspring.profiles.active=test
            ;;
        5)
            run_all_tests
            ;;
        6)
            run_all_tests
            generate_test_report
            ;;
        7)
            run_with_coverage
            ;;
        0)
            echo -e "${YELLOW}👋 Saindo...${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}❌ Opção inválida${NC}"
            show_menu
            ;;
    esac
}

# Verifica argumentos da linha de comando
if [ "$1" == "--all" ] || [ "$1" == "-a" ]; then
    run_all_tests
elif [ "$1" == "--unit" ] || [ "$1" == "-u" ]; then
    run_unit_tests
elif [ "$1" == "--integration" ] || [ "$1" == "-i" ]; then
    run_integration_tests
elif [ "$1" == "--websocket" ] || [ "$1" == "-w" ]; then
    run_websocket_tests
elif [ "$1" == "--kafka" ] || [ "$1" == "-k" ]; then
    mvn test -Dtest=KafkaIntegrationTest,EventProducerTest,EventConsumerTest -Dspring.profiles.active=test
elif [ "$1" == "--report" ] || [ "$1" == "-r" ]; then
    run_all_tests
    generate_test_report
elif [ "$1" == "--coverage" ] || [ "$1" == "-c" ]; then
    run_with_coverage
elif [ -z "$1" ]; then
    show_menu
else
    echo -e "${RED}❌ Argumento desconhecido: $1${NC}"
    echo "Uso: ./run-tests.sh [--all|--unit|--integration|--websocket|--kafka|--report|--coverage]"
    exit 1
fi
