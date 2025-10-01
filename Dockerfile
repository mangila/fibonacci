FROM node:22-slim

WORKDIR /app

#ENV REDIS_URL=redis://host.docker.internal:6379
#ENV LOCAL_PUSH=true

COPY package*.json ./
RUN npm install

COPY . .

CMD ["node", "index.js"]