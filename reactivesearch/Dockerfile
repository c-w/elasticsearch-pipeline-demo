# build environment
FROM node:12.2.0-alpine as build

# build time args
ARG REACT_APP_ELASTIC_INDEX
ARG REACT_APP_ELASTIC_URL
ENV REACT_APP_ELASTIC_INDEX $REACT_APP_ELASTIC_INDEX
ENV REACT_APP_ELASTIC_URL $REACT_APP_ELASTIC_URL

WORKDIR /app
COPY . .
RUN npm install --silent && npm run build

# production environment
FROM nginx:1.16.0-alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
