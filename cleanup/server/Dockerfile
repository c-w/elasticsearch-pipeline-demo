FROM golang:1.12
EXPOSE 8080
COPY . /server
WORKDIR /server
RUN go build
CMD "./server"
