# Automated Defect Triage Assistant (Maintenance System)

An Object-Oriented Java application simulating an automated production-line maintenance system for an automotive assembly plant.

## Overview
The application automates the triage, classification, and assignment of equipment failures reported on an automotive shop floor. Using a rule-based engine, the system prioritizes issues and assigns the most suitable maintenance engineer based on technical domain, experience level, and real-time workload.

## Key Features
- **Automated Rule-Based Triage:** Automatically parses incoming defect descriptions to categorize severity (**4 levels**) and assign urgency (**4 priority queues**).
- **Intelligent Task Routing:** Dispatches tasks to available engineers using workload-balancing logic and specialization constraints.
- **Lifecycle & History Tracking:** Monitors defects across **7 distinct lifecycle states**, maintaining an immutable audit log.
- **Automated Reporting:** Generates comprehensive technical resolution reports and plant-wide maintenance metrics.
- **Robust OOP Architecture:** Designed following UML principles (inheritance, composition, aggregation) and thoroughly validated via unit tests.

## Tech Stack
- **Language:** Java
- **Concepts:** Object-Oriented Design (OOD), UML, Rule-Based Automation, JUnit
