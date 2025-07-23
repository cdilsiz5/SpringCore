
# SpringCore API Test Script (Postman Style)

Tüm endpointleri kolayca test etmek için aşağıdaki sıralamayı kullanabilirsin.  
Ana URL: `http://localhost:8080/springcore/api/epam/v1`

---

## 1. Create Trainee (Public)
```http
POST http://localhost:8080/springcore/api/epam/v1/trainees
Content-Type: application/json

{
  "firstName": "Ali",
  "lastName": "Veli",
  "dateOfBirth": "2000-01-01",
  "address": "Istanbul"
}
```

---

## 2. Create Trainer (Public)
```http
POST http://localhost:8080/springcore/api/epam/v1/trainers
Content-Type: application/json

{
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "specialization": "CARDIO"
}
```

---

## 3. Get Trainee by Username
```http
GET http://localhost:8080/springcore/api/epam/v1/trainees/Ali.Veli
authUsername: Ali.Veli       # Header
authPassword: ali123         # Header
```

---

## 4. Get All Trainees
```http
GET http://localhost:8080/springcore/api/epam/v1/trainees
authUsername: Ali.Veli       # Header
authPassword: ali123         # Header
```

---

## 5. Update Trainee
```http
PUT http://localhost:8080/springcore/api/epam/v1/trainees/Ali.Veli
Content-Type: application/json
authUsername: Ali.Veli       # Header
authPassword: ali123         # Header

{
  "address": "Ankara",
  "dateOfBirth": "2000-02-02"
}
```

---

## 6. Delete Trainee
```http
DELETE http://localhost:8080/springcore/api/epam/v1/trainees/Ali.Veli
authUsername: Ali.Veli       # Header
authPassword: ali123         # Header
```

---

## 7. Toggle Trainee Activation
```http
PATCH http://localhost:8080/springcore/api/epam/v1/trainees/Ali.Veli/toggle-activation
authUsername: Ali.Veli       # Header
authPassword: ali123         # Header
```

---

## 8. Get Trainee Training History
```http
GET http://localhost:8080/springcore/api/epam/v1/trainees/Ali.Veli/training-history?from=2025-07-01&to=2025-12-31&trainerName=Ahmet
authUsername: Ali.Veli       # Header
authPassword: ali123         # Header
```

---

## 9. Get All Trainers
```http
GET http://localhost:8080/springcore/api/epam/v1/trainers
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header
```

---

## 10. Get Trainer by Username
```http
GET http://localhost:8080/springcore/api/epam/v1/trainers/Ahmet.Yılmaz
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header
```

---

## 11. Update Trainer
```http
PUT http://localhost:8080/springcore/api/epam/v1/trainers/Ahmet.Yılmaz
Content-Type: application/json
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header

{
  "specialization": "YOGA"
}
```

---

## 12. Delete Trainer
```http
DELETE http://localhost:8080/springcore/api/epam/v1/trainers/Ahmet.Yılmaz
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header
```

---

## 13. Toggle Trainer Activation
```http
PATCH http://localhost:8080/springcore/api/epam/v1/trainers/Ahmet.Yılmaz/toggle-activation
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header
```

---

## 14. Get Trainer Training History
```http
GET http://localhost:8080/springcore/api/epam/v1/trainers/Ahmet.Yılmaz/trainings?from=2025-07-01&to=2025-12-31&traineeName=Ali
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header
```

---

## 15. Create Training
```http
POST http://localhost:8080/springcore/api/epam/v1/trainings
Content-Type: application/json
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header

{
  "traineeId": 1,
  "trainerId": 1,
  "date": "2025-07-25",
  "durationMinutes": 60,
  "trainingType": "CARDIO"
}
```

---

## 16. Get Training by ID
```http
GET http://localhost:8080/springcore/api/epam/v1/trainings/1
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header
```

---

## 17. Get All Trainings
```http
GET http://localhost:8080/springcore/api/epam/v1/trainings
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header
```

---

## 18. Update Training
```http
PUT http://localhost:8080/springcore/api/epam/v1/trainings/1
Content-Type: application/json
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header

{
  "date": "2025-08-01",
  "durationMinutes": 90
}
```

---

## 19. Delete Training
```http
DELETE http://localhost:8080/springcore/api/epam/v1/trainings/1
authUsername: Ahmet.Yılmaz   # Header
authPassword: ahmet123       # Header
```

---
