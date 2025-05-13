
# LAT 2025 – Charity Collection - Spring Boot Application

  

##  Pre-requisites

  

-  **Java Development Kit (JDK 21)**

-  **Maven 3.8+**

-  **Git** (to clone the repository)

  

##  Running the Application

  

1.  **Clone the repository**

```bash

git clone https://github.com/Lukasz-Kow/Sii_Recruitment_Task.git

```

  

2.  **Navigate to the project directory**

```bash

cd path/to/project/with/pom.xml

```

  

3.  **Build the project and run tests**

```bash

mvn clean install

```

  

4.  **Start the application**

```bash

mvn spring-boot:run

```

  

##  JaCoCo Test Coverage Report

  

After running tests with `mvn clean install`, open the file:

```

target/site/jacoco/index.html

```

  

---

  

##  Accessing the H2 Database Console

  

-  **URL**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

-  **JDBC URL**: `jdbc:h2:mem:collectionboxdb`

-  **Username**: `username`

-  **Password**: *(leave blank)*

  

---

  

##  REST API Services

  

###  Fundraising Events

  

**Create a new fundraising event**

`POST /api/fundraising-events`

Sample body:

```json

{

"eventName": "Charity One",

"currency": "EUR"

}

```

  

**Get all events**

`GET /api/fundraising-events`

  

**Search for an event by name**

`GET /api/fundraising-events/search`

Sample body:

```json

{

"eventName": "Charity One"

}

```



---

  

###  Collection Boxes

  

**Register a new collection box**

`POST /api/collection-boxes`

Sample body:

```json

{

"identifier": "Box-001"

}

```

  

**List all collection boxes**

`GET /api/collection-boxes`

  

**Unregister a collection box**

`DELETE /api/collection-boxes/Box-001`

  

**Assign a box to a fundraising event**

`PUT /api/collection-boxes/1/assign/1`

  

**Transfer collected money to event**

`POST /api/collection-boxes/1/transfer`

  

---

  

###  Donations

  

**Add a donation to a box**

`POST /api/donations`

Sample body:

```json

{

"collectionBoxId": 1,

"amount": 50.00,

"currency": "USD"

}

```

  

---

  

##  Financial Report


**Get financial report**

`GET /api/fundraising-events/report`

  
```json

[

{

"eventName": "Charity One",

"amount": 2048.00,

"currency": "EUR"

},

{

"eventName": "All for Hope",

"amount": 512.64,

"currency": "GBP"

}

]

```
---

  

