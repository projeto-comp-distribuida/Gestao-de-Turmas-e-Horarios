# API Endpoints - DistriSchool Schedule Management Service

## Acesso Direto aos Serviços (Desenvolvimento)

Como o API Gateway ainda não tem todas as rotas configuradas, você pode acessar os serviços diretamente:

### Schedule Management Service (Porta 8084)

**Base URL:** `http://localhost:8084`

#### Classes (Turmas)
- `GET /api/v1/classes` - Listar todas as turmas
- `GET /api/v1/classes/{id}` - Buscar turma por ID
- `POST /api/v1/classes` - Criar nova turma
- `PUT /api/v1/classes/{id}` - Atualizar turma
- `DELETE /api/v1/classes/{id}` - Deletar turma
- `POST /api/v1/classes/{id}/students` - Adicionar estudantes à turma
- `DELETE /api/v1/classes/{id}/students/{studentId}` - Remover estudante da turma
- `POST /api/v1/classes/{id}/teachers` - Adicionar professores à turma
- `DELETE /api/v1/classes/{id}/teachers/{teacherId}` - Remover professor da turma
- `GET /api/v1/classes/{id}/room-conflicts` - Verificar conflitos de sala

#### Schedules (Horários)
- `GET /api/v1/schedules` - Listar todos os horários
- `GET /api/v1/schedules/{id}` - Buscar horário por ID
- `POST /api/v1/schedules` - Criar novo horário
- `PUT /api/v1/schedules/{id}` - Atualizar horário
- `DELETE /api/v1/schedules/{id}` - Deletar horário
- `POST /api/v1/schedules/{id}/check-conflicts` - Verificar conflitos de horário

#### Subjects (Disciplinas)
- `GET /api/v1/subjects` - Listar todas as disciplinas
- `GET /api/v1/subjects/{id}` - Buscar disciplina por ID
- `POST /api/v1/subjects` - Criar nova disciplina
- `PUT /api/v1/subjects/{id}` - Atualizar disciplina
- `DELETE /api/v1/subjects/{id}` - Deletar disciplina

#### Attendance (Presenças)
- `GET /api/v1/attendance` - Listar presenças
- `GET /api/v1/attendance/{id}` - Buscar presença por ID
- `POST /api/v1/attendance` - Registrar presença
- `PUT /api/v1/attendance/{id}` - Atualizar presença
- `DELETE /api/v1/attendance/{id}` - Deletar presença

#### Health Check
- `GET /actuator/health` - Status do serviço

### Outros Serviços

#### Auth Service (Porta 8085)
- `http://localhost:8085/api/v1/auth/**`
- `http://localhost:8085/api/v1/users/**`

#### Student Management Service (Porta 8086)
- `http://localhost:8086/api/v1/students/**`

#### Teacher Management Service (Porta 8087)
- `http://localhost:8087/api/v1/teachers/**`

## API Gateway (Porta 8080)

**Base URL:** `http://localhost:8080`

### Rotas Configuradas no Gateway

- ✅ `/api/v1/auth/**` → Auth Service
- ✅ `/api/v1/users/**` → Auth Service
- ✅ `/api/v1/students/**` → Student Management Service
- ✅ `/api/v1/teachers/**` → Teacher Management Service

### Rotas Faltando no Gateway

- ❌ `/api/v1/classes/**` → Schedule Management Service
- ❌ `/api/v1/schedules/**` → Schedule Management Service
- ❌ `/api/v1/subjects/**` → Schedule Management Service
- ❌ `/api/v1/attendance/**` → Schedule Management Service

**Nota:** Para usar essas rotas via Gateway, é necessário atualizar a configuração do API Gateway para incluir as rotas do Schedule Management Service.

## Exemplos de Uso

### Listar todas as turmas (direto)
```bash
curl http://localhost:8084/api/v1/classes
```

### Criar uma nova turma (direto)
```bash
curl -X POST http://localhost:8084/api/v1/classes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Turma A",
    "code": "TURMA-A-2024",
    "academicYear": "2024",
    "capacity": 30,
    "schoolId": 1
  }'
```

### Listar horários (direto)
```bash
curl http://localhost:8084/api/v1/schedules
```

## Próximos Passos

Para habilitar essas rotas no API Gateway, é necessário:

1. Atualizar a configuração do API Gateway (arquivo `application.yml` ou similar)
2. Adicionar as rotas:
   ```yaml
   spring:
     cloud:
       gateway:
         routes:
           - id: schedule-classes
             uri: http://schedule-management-service-dev:8080
             predicates:
               - Path=/api/v1/classes/**
           - id: schedule-schedules
             uri: http://schedule-management-service-dev:8080
             predicates:
               - Path=/api/v1/schedules/**
           - id: schedule-subjects
             uri: http://schedule-management-service-dev:8080
             predicates:
               - Path=/api/v1/subjects/**
           - id: schedule-attendance
             uri: http://schedule-management-service-dev:8080
             predicates:
               - Path=/api/v1/attendance/**
   ```
3. Rebuild e push da imagem do Gateway para o ACR
4. Reiniciar o container do Gateway


