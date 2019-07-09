FROM nginx:stable-alpine

#COPY --chown=nginx:nginx nginx.conf /etc/nginx/nginx.conf
COPY nginx.conf /etc/nginx/nginx.conf
RUN chown -R nginx:nginx /etc/nginx/nginx.conf

#COPY --chown=nginx:nginx textanalytics.conf /app/textanalytics.conf
COPY textanalytics.conf /app/textanalytics.conf
RUN chown -R nginx:nginx /app/textanalytics.conf

RUN chown -R nginx:nginx /etc/nginx \
 && chown -R nginx:nginx /var/cache/nginx

CMD envsubst < /app/textanalytics.conf > /etc/nginx/conf.d/default.conf && exec nginx -g 'daemon off;'

ENV PORT=5000

EXPOSE ${PORT}

USER nginx
