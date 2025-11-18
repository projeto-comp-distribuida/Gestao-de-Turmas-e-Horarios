# Correção do Erro do API Gateway

## Problema

O API Gateway está falhando ao iniciar com o seguinte erro:

```
Failed to bind properties under 'exceptions' to java.lang.Class<java.lang.Throwable>[]:
Property: exceptions
Value: "IOException,TimeoutException"
Reason: failed to convert java.lang.String to java.lang.Class<java.lang.Throwable> 
(caused by java.lang.ClassNotFoundException: IOException)
```

## Causa

A configuração do filtro Retry do Spring Cloud Gateway na imagem `distrischooldevacr.azurecr.io/api-gateway:latest` está usando nomes de classes de exceção sem os pacotes completos:

```yaml
exceptions: IOException,TimeoutException  # ❌ INCORRETO
```

O Spring Boot não consegue converter esses nomes para classes porque não encontra as classes sem o pacote completo.

## Solução

A configuração precisa usar os nomes completos das classes:

```yaml
exceptions: java.io.IOException,java.util.concurrent.TimeoutException  # ✅ CORRETO
```

### Passos para Corrigir

1. **Acessar o repositório do API Gateway:**
   ```bash
   cd ~/unifor/projeto-integrador/distrischool-api-gateway
   ```

2. **Localizar o arquivo de configuração:**
   - Provavelmente em `src/main/resources/application.yml` ou `application-dev.yml`
   - Procurar por `exceptions: IOException,TimeoutException`

3. **Corrigir a configuração:**
   ```yaml
   spring:
     cloud:
       gateway:
         routes:
           - id: auth-service
             filters:
               - name: Retry
                 args:
                   exceptions: java.io.IOException,java.util.concurrent.TimeoutException
           # Repetir para todas as rotas que usam o filtro Retry
   ```

4. **Rebuild e push da imagem:**
   ```bash
   docker build -t distrischooldevacr.azurecr.io/api-gateway:latest .
   docker push distrischooldevacr.azurecr.io/api-gateway:latest
   ```

5. **Reiniciar o container:**
   ```bash
   docker-compose restart api-gateway-dev
   ```

## Workaround Temporário

Se não for possível corrigir a imagem imediatamente, você pode tentar desabilitar o retry completamente (não recomendado para produção):

No `docker-compose.yml`, adicione:
```yaml
environment:
  SPRING_CLOUD_GATEWAY_DEFAULT_FILTERS_RETRY_ENABLED: "false"
```

**Nota:** Esta propriedade pode não existir no Spring Cloud Gateway. A solução definitiva é corrigir a imagem.

## Referências

- [Spring Cloud Gateway Retry Filter](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#retry-filter)
- [Spring Boot Configuration Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties)

