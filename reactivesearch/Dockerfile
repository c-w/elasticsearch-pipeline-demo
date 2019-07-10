FROM node:lts-stretch AS builder

RUN wget https://github.com/m3ng9i/ran/releases/download/v0.1.4/ran_linux_arm64.zip \
 && unzip ran_linux_arm64.zip \
 && rm ran_linux_arm64.zip \
 && mv ran_linux_arm64 /ran

WORKDIR /app
COPY package*.json ./
RUN npm install

COPY . .
RUN npm run lint \
 && npm run build

FROM debian:stretch-slim AS runtime

RUN useradd -s /bin/sh web
USER web

COPY --chown=web:web --from=builder /ran /usr/local/bin
COPY --chown=web:web --from=builder /app/build /app

ENV PORT="3000"
EXPOSE ${PORT}
CMD ran --port ${PORT} --root /app
