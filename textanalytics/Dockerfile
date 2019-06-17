FROM nginx:stable-alpine

COPY textanalytics.conf /app/textanalytics.conf

CMD envsubst < /app/textanalytics.conf > /etc/nginx/conf.d/default.conf && exec nginx -g 'daemon off;'

ENV PORT=5000

EXPOSE ${PORT}
