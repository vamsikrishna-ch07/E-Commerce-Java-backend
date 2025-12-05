
## Core Microservices (Business Logic)

- **User Service** – manages users (registration, profile, roles)
- **Product Service** – manages products (catalog, inventory, categories)
- **Order Service** – manages customer orders
- **Payment Service** – handles payments
- **Cart Service** – manages shopping carts

---

### 🔹 Infrastructure & Support Services

- **Auth (JWT Security) Service** – responsible for authentication & issuing JWT tokens
- **API Gateway** – single entry point for all clients (routes requests, validates JWT)
- **Service Registry (Eureka/Consul)** – service discovery
- **Config Server** – centralized configuration management




=======
left to create:# Microservices Overview
🆕 Product Service	Handles products (CRUD: add, update, delete, view)
🆕 Inventory Service	Manages stock for each product
🆕 Cart Service	Add/remove items for a user's cart
🆕 Order Service  Places an order from cart


✅ Next Step (Step-by-Step)
🔹 Step 1: Create Product Service
Responsibilities:

Add product

Get product list

Get product by ID

Update / delete product

Tables: product(id, name, description, price)

Register with Eureka and get configs from Config Server.

🔹 Step 2: Create Inventory Service
Responsibilities:

Track stock for products

Update stock after order

Tables: inventory(id, product_id, available_quantity)

Communicates with Product Service (via RestTemplate or Feign)

🔹 Step 3: Create Cart Service
Responsibilities:

Add product to user's cart

Remove product

View cart

Tables: cart(id, user_id), cart_items(id, cart_id, product_id, quantity)

🔹 Step 4: Create Order Service
Responsibilities:

Place order

Reduce inventory

Store order data

Tables: order(id, user_id, order_date, total_amount)
order_items(order_id, product_id, quantity, price)


============git commands====================
📘 Git Workflow – Real-Time Project Guide

This document explains all Git commands used in real company workflow, starting from git init to branch creation, pulling, merging, resolving conflicts, PR process, and deployment flow.

🏁 1. Initialize Git (Only Once Per Project)
git init
git add .
git commit -m "Initial commit"

🌐 2. Connect Local Project to GitHub
git remote add origin <REMOTE_GITHUB_URL>
git branch -M main
git push -u origin main

👥 3. Clone Existing Project (If Repository Already Exists)

(Most developers start from here)

git clone <REMOTE_GITHUB_URL>
cd project-folder

🌿 4. Create Your Feature Branch (Real-Time Workflow)
git checkout -b feature/<your-feature-name>


Example:

git checkout -b feature/user-module

🛠 5. Make Changes and Commit
git add .
git commit -m "Added user service implementation"

🔄 6. Always Keep Your Branch Updated (Daily Workflow)

Before you push, you must update your branch with the latest main.

➤ Step 1: Go to main
git checkout main

➤ Step 2: Pull latest code from main
git pull origin main

➤ Step 3: Go back to your branch
git checkout feature/user-module

➤ Step 4: Merge main into your branch
git merge main


→ If conflicts occur, resolve them
→ After resolving:

git add .
git commit

🚀 7. Push Your Updated Branch
git push origin feature/user-module

🔁 8. Create Pull Request (PR) – Real-Time Process

Go to GitHub

Open your branch

Click “Compare & Pull Request”

Add proper title & description

Team lead reviews

PR gets approved or requested for changes

After approval → Merge PR to main

🧹 9. Delete Branch After Merge (Recommended)
git branch -d feature/user-module       # local delete
git push origin --delete feature/user-module  # remote delete

🧩 10. Fix Merge Conflicts (If Occurs)

When you merge main into your feature branch:

git merge main


If conflict occurs:

Open the file

You will see:

<<<<<<< HEAD
your code
=======
their code
>>>>>>> main


Choose final code

Remove conflict markers

Stage file:

git add <file>


Commit:

git commit

🔄 11. Rebase Instead of Merge (Optional)

(Advanced developers use this)

git checkout feature/user-module
git pull --rebase origin main

📦 12. Stashing Changes (When switching branches)
git stash
git checkout main
git pull
git checkout feature/user-module
git stash pop

🧭 13. Check Status, Log, and Branches
git status
git log
git branch
git branch -a

🧨 14. Undo Mistakes (Important Real-Time Commands)
Undo last commit (keep code):
git reset --soft HEAD~1

Undo last commit (delete code):
git reset --hard HEAD~1

Discard local changes:
git restore .

🏷 15. Tagging Releases (Deployment Pipeline)
git tag -a v1.0 -m "First release"
git push origin v1.0

🔚 END-TO-END WORKFLOW SUMMARY (MOST IMPORTANT)
git clone repo
git checkout -b feature/<name>
write code
git add .
git commit -m "message"

git checkout main
git pull origin main
git checkout feature/<name>
git merge main     (resolve conflicts if any)

git push origin feature/<name>
Create Pull Request on GitHub
Get approval → merge to main
Delete branch



🔚 END-TO-END WORKFLOW SUMMARY (MOST IMPORTANT)


git clone repo
git checkout -b feature/<name>
write code
git add .
git commit -m "message"

git checkout main
git pull origin main
git checkout feature/<name>
git merge main     (resolve conflicts if any)

git push origin feature/<name>
Create Pull Request on GitHub
Get approval → merge to main
Delete branch







======
.
Replace Placeholders: Open the docker-compose.yml file and replace YOUR_DB_USERNAME and YOUR_DB_PASSWORD with your desired credentials.
2.
Build the JARs: Before running Docker Compose, you must build your Spring Boot projects to create the .jar files. Run this command from the root of your project:
Shell Script
mvn clean install
3.
Run Docker Compose: Once the build is complete, start your entire application stack with a single command from the project root:
Shell Script
docker-compose up --build
Your entire e-commerce application will now be running, with each service in its own container. You can open Docker Desktop to see all the individual containers and images.