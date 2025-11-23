# Campus Course & Records Manager (CCRM)
Submitted by : 
Name : Raghav Gupta
Reg No : 24BCE10427

# Folder Structure



CCRM/
├── src/ (all your Java files)
├── data/
│   ├── students.csv
│   └── courses.csv
├── bin/ (compiled classes)
├── screenshots/
│   ├── jdk-version.png
│   ├── main-menu.png
│   └── gpa-calculation.png
├── README.md
├── USAGE.md
└── (optional demo video)



## Project Overview
A console-based Java application for managing students, courses, enrollments, and grades with GPA calculation.

## How to Run
1. Ensure Java JDK 8+ is installed
2. Compile: `javac -d bin src/edu/ccrm/*.java src/edu/ccrm/domain/*.java src/edu/ccrm/service/*.java src/edu/ccrm/cli/*.java src/edu/ccrm/io/*.java src/edu/ccrm/util/*.java src/edu/ccrm/config/*.java src/edu/ccrm/exception/*.java`
3. Run: `java -cp bin edu.ccrm.Main`

## Evolution of Java
- 1996: Java 1.0 released
- 2004: Java 5 (Generics, Autoboxing)
- 2014: Java 8 (Lambdas, Streams)
- 2023: Java 21 (Virtual Threads)

## Java Platform Comparison
| Platform | Purpose | Use Case |
|----------|---------|----------|
| Java SE | Standard Edition | Desktop apps, CLI tools |
| Java EE | Enterprise Edition | Large-scale web apps |
| Java ME | Micro Edition | Embedded systems |

## JDK/JRE/JVM Architecture
- **JVM**: Executes bytecode (Write Once, Run Anywhere)
- **JRE**: JVM + Libraries (Runtime Environment)
- **JDK**: JRE + Development Tools (Compiler, Debugger)

## Windows Installation Steps
1. Download JDK from Oracle
2. Run installer
3. Set JAVA_HOME environment variable
4. Add JDK bin to PATH

## Eclipse Setup
1. File → New → Java Project
2. Name: "CCRM"
3. Import existing source code
4. Run Main.java

## Syllabus Topic Mapping
| Topic | Demonstration File |
|-------|-------------------|
| OOP Principles | Student.java, Person.java |
| Streams API | Student.java (GPA calculation) |
| Builder Pattern | Student.java, Course.java |
| Exception Handling | Custom exception classes |
| NIO.2 | FileService.java |
| Singleton Pattern | AppConfig.java |

## Enabling Assertions
```bash
java -ea -cp bin edu.ccrm.Main