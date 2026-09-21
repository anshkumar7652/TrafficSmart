# 📋 Capstone Project Guidelines — Agent Reference Document

> **Project**: Smart Traffic Monitoring And Management System
> **Topic No.**: 76
> **Course**: Object Oriented Techniques using Java (OOTS)
> **Institution**: NIET Greater Noida
> **JDK**: 17+ (LTS) — No external frameworks allowed

---

## 1. Purpose of This Document

This file is the **single source of truth** for any AI agent working on this capstone project. Before writing ANY code, generating ANY file, or making ANY architectural decision — **read this document in full**. Every class, method, and design choice must comply with the rules below.

---

## 2. Academic Evaluation Criteria (40 Marks Total)

| Component | Marks | What the Evaluator Checks |
|---|---|---|
| **Problem Understanding & OOAD** | 5 | Clear problem statement, domain analysis, use-case identification, UML class diagrams |
| **Core OOP Implementation** | 8 | Inheritance hierarchies, abstract classes, interfaces, polymorphism, encapsulation, enums |
| **Packages, Exceptions & Strings** | 6 | Multi-package architecture, custom checked/unchecked exceptions, `StringBuilder`/regex string processing |
| **Multithreading & Concurrency** | 5 | Thread creation (`Runnable`/`Thread`), synchronization (`synchronized`/`Lock`), daemon threads, thread lifecycle |
| **I/O Streams** | 4 | Both **Byte Streams** (`DataInputStream`/`DataOutputStream`, `FileInputStream`/`FileOutputStream`) AND **Character Streams** (`BufferedReader`/`PrintWriter`, `FileReader`/`FileWriter`) |
| **Socket Programming (Networking)** | 4 | TCP (`Socket`/`ServerSocket`) client-server communication AND UDP (`DatagramSocket`/`DatagramPacket`) |
| **GUI (Swing)** | 4 | `JFrame`, `JPanel`, `JTabbedPane`, `JTable`, custom `paintComponent()`, event listeners, layout managers |
| **Generics** | 2 | Generic classes (`<T>`), bounded types (`<T extends Comparable<T>>`), generic methods |
| **Collections Framework** | 2 | Minimum 4 distinct types: `ArrayList`, `LinkedList`, `HashMap`/`TreeMap`, `HashSet`/`TreeSet` |
| **Lambdas & Functional Interfaces** | 2 | Lambda expressions, `Predicate`, `Function`, `Consumer`, method references, stream-like operations |
| **Code Quality, Comments & Viva** | 8 | Clean code, Javadoc, inline comments explaining logic, ability to explain code verbally |

> **CAUTION**: Zero marks are awarded for any unit whose concepts are entirely missing from the codebase. Every single row above MUST have demonstrable code.

---

## 3. Syllabus Unit → Code Mapping (Mandatory Checklist)

Every item below **MUST** exist in the final codebase. Use this as a build checklist.

### Unit 1 — OOAD & Java Basics
- [ ] Problem statement documented in code comments or a `README.md`
- [ ] At least one UML class diagram (can be Mermaid in markdown or a `.png`)
- [ ] `main()` entry point in `MainApplication.java`
- [ ] Console/CLI fallback mode (the app must work without the GUI for basic operations)
- [ ] Primitive types, type casting, and operator usage demonstrated
- [ ] Control flow: `if-else`, `switch`, `for`, `while`, `for-each`

### Unit 2 — OOP, Arrays, Lambdas
- [ ] **Abstract class**: `Vehicle` with at least 2 abstract methods
- [ ] **Concrete subclasses**: `StandardVehicle`, `EmergencyVehicle` (minimum 2 levels)
- [ ] **Interface**: `Monitorable` (with default method), `SignalControlStrategy` (functional interface)
- [ ] **Polymorphism**: Method overriding + runtime polymorphic dispatch demonstrated
- [ ] **Encapsulation**: All fields `private`, accessed via getters/setters
- [ ] **Enums**: `SignalState` (`RED`, `YELLOW`, `GREEN`)
- [ ] **Arrays**: At least one jagged/2D array (lane density matrix)
- [ ] **Lambda expressions**: Filtering, sorting, or transforming collections using lambdas
- [ ] **Method references**: At least 2 uses (e.g., `System.out::println`, `Vehicle::getSpeed`)
- [ ] **Functional interfaces**: `Predicate<Vehicle>`, `Comparator<Intersection>`, custom `@FunctionalInterface`

### Unit 3 — Packages, Exception Handling, Strings
- [ ] **Minimum 7 packages**: `app`, `config`, `model`, `service`, `net`, `gui`, `exception`, `util`, `repository`, `strategy`
- [ ] **Custom checked exception**: `TrafficSystemException` (extends `Exception`)
- [ ] **Custom unchecked exception**: `SignalFailureException` (extends `RuntimeException`)
- [ ] **Custom exception with cause chaining**: `SensorReadException` wrapping an `IOException`
- [ ] **try-catch-finally**: At least 3 distinct usages across different classes
- [ ] **try-with-resources**: Used for ALL stream/socket operations
- [ ] **Multi-catch**: At least 1 usage (`catch (IOException | ParseException e)`)
- [ ] **throws declaration**: On methods that can fail (I/O, network)
- [ ] **StringBuilder**: Used in `ReportGenerator` for building formatted reports
- [ ] **String.format() / printf**: Used for log line formatting
- [ ] **Regex (Pattern/Matcher)**: At least 1 usage (e.g., validating vehicle plate numbers)

### Unit 4 — Multithreading, I/O Streams, Networking
#### Multithreading
- [ ] **Runnable implementation**: `SimulationEngine implements Runnable`
- [ ] **Thread subclass**: At least one `extends Thread` example (e.g., `SignalTimerThread`)
- [ ] **synchronized block/method**: Protecting shared intersection state
- [ ] **ReentrantLock**: Used in at least one critical section
- [ ] **Daemon thread**: Simulation background thread set as daemon
- [ ] **Thread.sleep()**: Used for simulation tick timing
- [ ] **Thread lifecycle**: `start()`, `interrupt()`, `join()` demonstrated
- [ ] **volatile keyword**: At least 1 usage for a shared flag (e.g., `volatile boolean running`)

#### I/O Streams
- [ ] **Byte streams (output)**: `DataOutputStream` writing binary sensor logs
- [ ] **Byte streams (input)**: `DataInputStream` reading binary sensor logs
- [ ] **Character streams (output)**: `PrintWriter`/`BufferedWriter` writing CSV/text reports
- [ ] **Character streams (input)**: `BufferedReader`/`FileReader` reading config/CSV files
- [ ] **Serialization**: At least 1 class implements `Serializable` (e.g., `SensorData`)
- [ ] **File class**: `File.exists()`, `File.mkdirs()` for directory management

#### Networking (Sockets)
- [ ] **TCP Server**: `ServerSocket` accepting client connections in `TrafficServer.java`
- [ ] **TCP Client**: `Socket` connecting to server in `MonitoringClient.java`
- [ ] **Protocol**: Text-based command protocol (e.g., `GET_STATUS`, `SET_SIGNAL`, `EMERGENCY`)
- [ ] **UDP**: `DatagramSocket` + `DatagramPacket` for emergency broadcast in `EmergencySignalUDP.java`
- [ ] **Multi-client support**: Server handles multiple clients (one thread per client)

### Unit 5 — GUI, Generics, Collections
#### Swing GUI
- [ ] **JFrame**: Main application window (`MainDashboard`)
- [ ] **JPanel with custom painting**: `IntersectionCanvas` overriding `paintComponent(Graphics g)`
- [ ] **JTabbedPane**: Dashboard with tabs (Monitor, Control, Reports, Logs)
- [ ] **JTable**: Displaying vehicle data or signal logs
- [ ] **JMenuBar + JMenu**: File menu (Export, Import, Exit)
- [ ] **Form components**: `JTextField`, `JComboBox`, `JButton`, `JLabel`, `JTextArea`
- [ ] **Event listeners**: `ActionListener`, `MouseListener` or `MouseAdapter`
- [ ] **Layout managers**: `BorderLayout`, `GridBagLayout` or `GridLayout`, `FlowLayout`
- [ ] **javax.swing.Timer**: For animation ticking (signal phase cycling on canvas)
- [ ] **JOptionPane**: Confirmation dialogs, error alerts
- [ ] **Login screen**: `LoginForm` with basic credential validation

#### Generics
- [ ] **Generic class**: `GenericRepository<T>` with CRUD operations
- [ ] **Bounded type parameter**: `<T extends Comparable<T>>` for sorted retrieval
- [ ] **Generic method**: At least 1 standalone generic method (e.g., `<T> List<T> filterBy(...)`)
- [ ] **Wildcard**: At least 1 usage of `<? extends T>` or `<? super T>`

#### Collections Framework
- [ ] **ArrayList**: Vehicle list, signal log entries
- [ ] **LinkedList**: Event queue / notification queue
- [ ] **HashMap**: Intersection lookup by ID, signal state mapping
- [ ] **HashSet**: Unique sensor IDs, unique vehicle plates
- [ ] **TreeSet** or **TreeMap**: Sorted priority queue for congestion ranking
- [ ] **Iterator**: Explicit iterator usage in at least 1 place
- [ ] **Collections utility**: `Collections.sort()`, `Collections.unmodifiableList()`, etc.

---

## 4. Project Structure & Naming Conventions

### 4.1 Directory Layout

```
OOTS_Capstone_Project/
├── guidelines.md              ← THIS FILE (do not modify)
├── README.md                  ← Project overview, how to compile & run
├── docs/
│   ├── uml_class_diagram.md   ← Mermaid UML diagram
│   └── architecture.md        ← System architecture documentation
├── src/
│   └── com/
│       └── trafficsmart/
│           ├── app/            ← Entry point
│           ├── config/         ← System configuration constants
│           ├── exception/      ← Custom exceptions
│           ├── model/          ← Domain entities (POJOs, enums, abstract classes)
│           ├── strategy/       ← Strategy pattern implementations
│           ├── repository/     ← Generic data repositories
│           ├── service/        ← Business logic, simulation engine
│           ├── net/            ← Socket networking (TCP server, client, UDP)
│           ├── util/           ← File I/O handlers, analytics utilities
│           └── gui/            ← Swing UI components
├── data/
│   ├── config/                 ← Configuration files (.properties, .csv)
│   ├── logs/                   ← Runtime log output (binary + text)
│   └── exports/                ← Exported reports
├── test/
│   └── com/
│       └── trafficsmart/       ← JUnit test classes mirroring src structure
└── lib/                        ← JUnit JAR (only allowed external dependency)
```

### 4.2 File Naming

| Entity | Convention | Example |
|---|---|---|
| Java class file | PascalCase, matches class name exactly | `EmergencyVehicle.java` |
| Interface | PascalCase, adjective or noun | `Monitorable.java`, `SignalControlStrategy.java` |
| Enum | PascalCase | `SignalState.java` |
| Package | all lowercase, dot-separated | `com.trafficsmart.model` |
| Config files | lowercase with hyphens | `system-config.properties` |
| Data files | lowercase with underscores | `sensor_log_2026.dat` |

### 4.3 Java Naming Conventions

| Element | Convention | Example |
|---|---|---|
| Classes | PascalCase | `TrafficControllerService` |
| Interfaces | PascalCase | `SignalControlStrategy` |
| Methods | camelCase, verb-first | `calculateDensity()`, `getSignalState()` |
| Variables | camelCase | `vehicleCount`, `isRunning` |
| Constants | UPPER_SNAKE_CASE | `MAX_VEHICLES_PER_LANE`, `DEFAULT_GREEN_DURATION` |
| Enum values | UPPER_SNAKE_CASE | `RED`, `YELLOW`, `GREEN` |
| Type parameters | Single uppercase letter | `<T>`, `<E>`, `<K, V>` |
| Packages | all lowercase | `com.trafficsmart.net` |

---

## 5. Coding Standards & Rules

### 5.1 Absolute Constraints

> **CAUTION**: Violating ANY of these rules will result in marks deduction or disqualification.

1. **Pure Core Java ONLY** — No Spring, no Maven/Gradle build tools, no external libraries except JUnit for testing. Compilation must work with raw `javac`.
2. **JDK 17+ LTS** — Use modern Java features (records are allowed but not required; `var` is acceptable; text blocks are fine).
3. **No `java.util.stream` as a substitute for lambdas** — Streams are welcome BUT you MUST also show raw lambda usage with functional interfaces (e.g., passing a `Predicate<Vehicle>` to a filter method you wrote yourself).
4. **No placeholder / stub code** — Every method must have a real implementation. Empty method bodies are forbidden.
5. **No hardcoded file paths** — Use `System.getProperty("user.dir")` or relative paths via the `data/` directory.
6. **No `System.exit()` in library code** — Only allowed in `MainApplication.main()`.

### 5.2 Code Quality Requirements

1. **Javadoc on every public class and method**:
   ```java
   /**
    * Represents a traffic signal at an intersection.
    * Manages phase cycling and emergency override logic.
    *
    * @author Ansh
    * @version 1.0
    */
   public class TrafficSignal { ... }
   ```

2. **Inline comments** explaining non-obvious logic (especially threading, synchronization, and socket protocols).

3. **Consistent indentation**: 4 spaces, no tabs.

4. **Maximum line length**: 120 characters.

5. **One class per file** (inner classes are exceptions).

6. **Imports**: No wildcard imports (`import java.util.*` is forbidden). Use specific imports.

7. **Resource management**: ALL `Closeable` resources MUST use try-with-resources.

8. **Error messages**: All exceptions must carry meaningful messages:
   ```java
   throw new SignalFailureException(
       "Signal ID " + signalId + " failed to transition from "
       + currentState + " to " + targetState
   );
   ```

### 5.3 Design Patterns to Demonstrate

| Pattern | Where | Purpose |
|---|---|---|
| **Strategy** | `SignalControlStrategy` interface + `DynamicDensityStrategy`, `EmergencyPriorityStrategy` | Swappable signal timing algorithms |
| **Observer** (lightweight) | Signal state change → GUI update notification | Decoupling model from view |
| **Repository** | `GenericRepository<T>` | Abstracting data storage with generics |
| **Singleton** | `SystemConfig` | Single configuration instance |
| **Template Method** | `Vehicle.describe()` calling abstract hooks | Consistent vehicle description with subclass-specific details |

---

## 6. Domain-Specific Requirements

### 6.1 Core Entities

| Entity | Type | Key Fields |
|---|---|---|
| `Vehicle` | Abstract Class | `id`, `plateNumber`, `type`, `speed`, `laneId`, `timestamp` |
| `StandardVehicle` | Concrete Class | Inherits Vehicle, adds `fuelType` |
| `EmergencyVehicle` | Concrete Class | Inherits Vehicle, adds `emergencyType`, `sirenActive`, `priority` |
| `TrafficSignal` | Class | `signalId`, `intersectionId`, `currentState`, `greenDuration`, `strategy` |
| `Intersection` | Class | `intersectionId`, `name`, `location`, `signals[]`, `lanes[][]`, `congestionLevel` |
| `SensorData` | Class (Serializable) | `sensorId`, `intersectionId`, `vehicleCount`, `avgSpeed`, `timestamp` |
| `SignalState` | Enum | `RED`, `YELLOW`, `GREEN` with `duration` field and `next()` method |

### 6.2 Functional Features

1. **Real-Time Simulation**: Background threads simulate vehicles arriving/departing intersections with configurable density.
2. **Adaptive Signal Control**: Signal green-phase duration adjusts based on sensor-reported vehicle density (Strategy pattern).
3. **Emergency Override**: Emergency vehicles trigger immediate green-phase on their approach lane (UDP broadcast → server → signal override).
4. **Live Dashboard**: Swing GUI shows animated traffic lights, vehicle counts, congestion heat indicators, and real-time logs.
5. **Remote Monitoring**: A TCP client can connect to the server, query intersection status, and issue manual signal overrides.
6. **Data Persistence**: Configuration in `.properties` (char streams), sensor logs in `.dat` (byte streams), reports in `.csv`/`.txt` (char streams).
7. **Report Generation**: Generate formatted traffic analytics reports with `StringBuilder`, `String.format()`, and regex validation.

### 6.3 Networking Protocol

```
TCP Command Protocol (text-based, newline-delimited):
──────────────────────────────────────────────────────
Client → Server:
  GET_STATUS <intersection_id>
  SET_SIGNAL <intersection_id> <signal_id> <RED|YELLOW|GREEN>
  GET_REPORT
  EMERGENCY <intersection_id> <vehicle_id>
  QUIT

Server → Client:
  OK <data>
  ERROR <message>
  ALERT <message>

UDP Emergency Broadcast:
──────────────────────────────────────────────────────
Packet payload: "EMERGENCY:<intersection_id>:<vehicle_id>:<priority>"
```

---

## 7. Compilation & Execution Commands

All commands assume the working directory is the project root (`OOTS_Capstone_Project/`).

```bash
# Compile all sources
javac -d out -sourcepath src src/com/trafficsmart/app/MainApplication.java

# Run the application (GUI mode)
java -cp out com.trafficsmart.app.MainApplication

# Run the application (CLI/Server mode)
java -cp out com.trafficsmart.app.MainApplication --mode=server --port=9090

# Run the monitoring client
java -cp out com.trafficsmart.net.MonitoringClient localhost 9090

# Compile and run tests (JUnit 5)
javac -d out-test -cp "out;lib/junit-platform-console-standalone.jar" -sourcepath test test/com/trafficsmart/**/*.java
java -jar lib/junit-platform-console-standalone.jar --class-path "out;out-test" --scan-class-path
```

---

## 8. Documentation Deliverables

The following documents must exist in the final submission:

| File | Location | Contents |
|---|---|---|
| `README.md` | Project root | Project title, team info, how to compile, how to run, screenshots |
| `docs/uml_class_diagram.md` | `docs/` | Mermaid-rendered UML class diagram of all entities and relationships |
| `docs/architecture.md` | `docs/` | System architecture, package responsibilities, threading model, network protocol |
| `guidelines.md` | Project root | THIS FILE — agent reference (do not modify during implementation) |

---

## 9. Anti-Patterns & Common Mistakes to Avoid

> **WARNING**: These are the most common reasons for marks deduction. Read carefully.

| Mistake | Correct Approach |
|---|---|
| Using only `ArrayList` everywhere | Use at least 4 distinct collection types with justification |
| Putting all classes in one package | Minimum 7 packages with clear separation of concerns |
| Only `try-catch`, never `try-with-resources` | Use `try-with-resources` for ALL I/O and socket operations |
| Empty `catch` blocks (swallowing exceptions) | Always log or rethrow with context |
| GUI with no custom painting | Must have `paintComponent()` override with `Graphics2D` drawing |
| Threads without synchronization | Every shared mutable state must be protected |
| Only TCP or only UDP | Must demonstrate BOTH TCP and UDP |
| Only byte streams or only character streams | Must demonstrate BOTH byte and character streams |
| Lambdas only inside `stream().filter()` | Show raw lambda assignment to functional interface variables |
| Generic class without bounded types | Include at least one `<T extends SomeType>` |
| No `volatile` or `Lock` usage | Must show beyond just `synchronized` keyword |
| Wildcard imports (`import java.util.*`) | Use specific imports for every class |
| Hardcoded IP addresses / ports | Use config file or command-line arguments |

---

## 10. Agent Behavioral Rules

When an AI agent is building this project, it MUST:

1. **Read this file first** before writing any code.
2. **Check items off** the Unit checklists (Section 3) mentally before declaring a phase complete.
3. **Never create files outside** the defined directory structure (Section 4.1).
4. **Never introduce external dependencies** beyond JUnit.
5. **Write Javadoc** on every public class and method — no exceptions.
6. **Test compilation** after completing each phase by running `javac`.
7. **Preserve this file** — never modify or delete `guidelines.md`.
8. **Cross-reference** the evaluation criteria table (Section 2) before finalizing any phase to ensure nothing is missed.
9. **Use meaningful commit-style messages** when describing changes.
10. **When in doubt**, choose the approach that demonstrates MORE syllabus concepts, not fewer.

---

## 11. Quick Reference Card

```
┌──────────────────────────────────────────────────────────┐
│           SMART TRAFFIC MONITORING SYSTEM                │
│              Quick Compliance Check                      │
├──────────────────────────────────────────────────────────┤
│  [ ] Abstract class + 2 subclasses                      │
│  [ ] 2+ Interfaces (1 functional)                       │
│  [ ] Enum with fields + methods                         │
│  [ ] 7+ packages                                        │
│  [ ] 3+ custom exceptions (checked + unchecked)         │
│  [ ] StringBuilder + String.format + regex              │
│  [ ] try-with-resources + multi-catch                   │
│  [ ] Runnable + Thread subclass                         │
│  [ ] synchronized + ReentrantLock + volatile            │
│  [ ] Daemon thread                                      │
│  [ ] DataInputStream/DataOutputStream (byte)            │
│  [ ] BufferedReader/PrintWriter (character)             │
│  [ ] Serializable class                                 │
│  [ ] TCP Server + Client (ServerSocket/Socket)          │
│  [ ] UDP (DatagramSocket/DatagramPacket)                │
│  [ ] JFrame + JTabbedPane + JTable + JMenuBar           │
│  [ ] Custom paintComponent() with Graphics2D            │
│  [ ] javax.swing.Timer for animation                    │
│  [ ] GenericRepository<T> with bounded type             │
│  [ ] ArrayList + LinkedList + HashMap + HashSet + TreeSet│
│  [ ] Lambda + method reference + Predicate/Function     │
│  [ ] Javadoc on all public classes/methods              │
│  [ ] No wildcard imports                                │
│  [ ] No external dependencies (except JUnit)            │
└──────────────────────────────────────────────────────────┘
```

---

*Last updated: 2026-09-21 | Version: 1.0*
