# Estágio 1: Build do Angular
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# Gera os arquivos estáticos de produção na pasta dist/
RUN npm run build -- --configuration production

# Estágio 2: Servidor Web (NGINX)
FROM nginx:alpine
# Remove a config padrão do Nginx
RUN rm /etc/nginx/conf.d/default.conf
# Copia nossa config personalizada
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copia os arquivos compilados do Angular para a pasta pública do Nginx
# ATENÇÃO: Verifique se no seu angular.json o outputPath é "dist/frontend" ou apenas "dist"
COPY --from=build /app/dist/frontend /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]