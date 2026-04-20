FROM node:22-alpine

WORKDIR /app

COPY package*.json ./

RUN npm install

ENV NG_CLI_ANALYTICS=false

COPY . .

EXPOSE 4200

CMD ["npm", "start", "--", "--host", "0.0.0.0"]
