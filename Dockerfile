# Dockerfile para o frontend KMP (Compose Multiplatform)
FROM node:18-alpine as node-setup

# Instalar dependências do sistema para Gradle
FROM gradle:8.14.3-jdk21-alpine as build

WORKDIR /app

# Copiar arquivos do projeto
COPY . .

# Build da aplicação web (Wasm)
RUN ./gradlew :composeApp:wasmJsBrowserProductionWebpack --no-daemon

# Imagem final com nginx para servir a aplicação
FROM nginx:alpine

# Copiar arquivos buildados para o nginx
COPY --from=build /app/composeApp/build/dist/wasmJs/productionExecutable/ /usr/share/nginx/html/

# Configuração do nginx para SPA
RUN echo 'server {\
    listen 80;\
    server_name localhost;\
    root /usr/share/nginx/html;\
    index index.html;\
    \
    # Handle client-side routing\
    location / {\
        try_files $uri $uri/ /index.html;\
    }\
    \
    # Cache static assets\
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {\
        expires 1y;\
        add_header Cache-Control "public, immutable";\
    }\
    \
    # Gzip compression\
    gzip on;\
    gzip_vary on;\
    gzip_min_length 1024;\
    gzip_types\
        text/plain\
        text/css\
        text/xml\
        text/javascript\
        application/javascript\
        application/xml+rss\
        application/json;\
}' > /etc/nginx/conf.d/default.conf

# Porta padrão
EXPOSE 80

# Comando para iniciar nginx
CMD ["nginx", "-g", "daemon off;"]
