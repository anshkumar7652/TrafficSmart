# UML Class Diagram — Smart Traffic Monitoring And Management System

> Auto-generated from project guidelines. Covers all domain entities, interfaces, strategies, repositories, services, networking, and GUI components.

---

## Core Domain Model

```mermaid
classDiagram
    direction TB

    %% ─────────────── ENUMS ───────────────
    class SignalState {
        <<enumeration>>
        RED
        YELLOW
        GREEN
        -int duration
        +getDuration() int
        +next() SignalState
        +toString() String
    }

    %% ─────────────── INTERFACES ───────────────
    class Monitorable {
        <<interface>>
        +getStatus() String
        +getMetrics() Map~String, Object~
        +getLastUpdated() long
        +isHealthy() boolean
    }

    class SignalControlStrategy {
        <<interface>>
        <<FunctionalInterface>>
        +calculateGreenDuration(SensorData data, Intersection intersection) int
    }

    class Serializable {
        <<interface>>
    }

    class Comparable~T~ {
        <<interface>>
        +compareTo(T other) int
    }

    %% ─────────────── ABSTRACT CLASSES ───────────────
    class Vehicle {
        <<abstract>>
        -String id
        -String plateNumber
        -String type
        -double speed
        -String laneId
        -long timestamp
        +Vehicle(String id, String plateNumber, String type)
        +getId() String
        +getPlateNumber() String
        +getType() String
        +getSpeed() double
        +setSpeed(double speed) void
        +getLaneId() String
        +setLaneId(String laneId) void
        +getTimestamp() long
        +describe() String
        +calculatePriority()* int
        +getVehicleCategory()* String
        +toString() String
        +compareTo(Vehicle other) int
    }

    %% ─────────────── CONCRETE MODEL CLASSES ───────────────
    class StandardVehicle {
        -String fuelType
        +StandardVehicle(String id, String plateNumber, String fuelType)
        +getFuelType() String
        +calculatePriority() int
        +getVehicleCategory() String
    }

    class EmergencyVehicle {
        -String emergencyType
        -boolean sirenActive
        -int priority
        +EmergencyVehicle(String id, String plateNumber, String emergencyType, int priority)
        +getEmergencyType() String
        +isSirenActive() boolean
        +setSirenActive(boolean active) void
        +getPriority() int
        +calculatePriority() int
        +getVehicleCategory() String
    }

    class TrafficSignal {
        -String signalId
        -String intersectionId
        -SignalState currentState
        -int greenDuration
        -SignalControlStrategy strategy
        +TrafficSignal(String signalId, String intersectionId)
        +getSignalId() String
        +getCurrentState() SignalState
        +setCurrentState(SignalState state) void
        +getGreenDuration() int
        +setStrategy(SignalControlStrategy strategy) void
        +cycleNext() void
        +overrideToGreen() void
        +getStatus() String
        +getMetrics() Map~String, Object~
        +isHealthy() boolean
    }

    class Intersection {
        -String intersectionId
        -String name
        -String location
        -List~TrafficSignal~ signals
        -int[][] laneDensityMatrix
        -double congestionLevel
        +Intersection(String id, String name, String location)
        +getIntersectionId() String
        +getName() String
        +getSignals() List~TrafficSignal~
        +addSignal(TrafficSignal signal) void
        +getLaneDensityMatrix() int[][]
        +setLaneDensityMatrix(int[][] matrix) void
        +getCongestionLevel() double
        +recalculateCongestion() void
        +getStatus() String
        +getMetrics() Map~String, Object~
        +compareTo(Intersection other) int
    }

    class SensorData {
        -String sensorId
        -String intersectionId
        -int vehicleCount
        -double avgSpeed
        -long timestamp
        +SensorData(String sensorId, String intersectionId, int vehicleCount, double avgSpeed)
        +getSensorId() String
        +getIntersectionId() String
        +getVehicleCount() int
        +getAvgSpeed() double
        +getTimestamp() long
    }

    %% ─────────────── RELATIONSHIPS ───────────────
    Vehicle <|-- StandardVehicle : extends
    Vehicle <|-- EmergencyVehicle : extends
    Vehicle ..|> Comparable~T~ : implements
    Vehicle ..|> Serializable : implements
    TrafficSignal ..|> Monitorable : implements
    Intersection ..|> Monitorable : implements
    Intersection ..|> Comparable~T~ : implements
    SensorData ..|> Serializable : implements
    TrafficSignal --> SignalState : uses
    TrafficSignal --> SignalControlStrategy : delegates to
    Intersection "1" *-- "1..*" TrafficSignal : contains
```

---

## Strategy Pattern

```mermaid
classDiagram
    direction LR

    class SignalControlStrategy {
        <<interface>>
        <<FunctionalInterface>>
        +calculateGreenDuration(SensorData, Intersection) int
    }

    class DynamicDensityStrategy {
        -int minGreen
        -int maxGreen
        -double densityThreshold
        +DynamicDensityStrategy(int minGreen, int maxGreen)
        +calculateGreenDuration(SensorData, Intersection) int
    }

    class EmergencyPriorityStrategy {
        -int emergencyGreenDuration
        +EmergencyPriorityStrategy(int emergencyGreenDuration)
        +calculateGreenDuration(SensorData, Intersection) int
    }

    SignalControlStrategy <|.. DynamicDensityStrategy : implements
    SignalControlStrategy <|.. EmergencyPriorityStrategy : implements
```

---

## Generics & Repository Layer

```mermaid
classDiagram
    direction TB

    class GenericRepository~T~ {
        -HashMap~String, T~ store
        -ArrayList~T~ orderedList
        -HashSet~String~ idSet
        +add(String id, T entity) void
        +getById(String id) T
        +getAll() List~T~
        +remove(String id) boolean
        +exists(String id) boolean
        +count() int
        +clear() void
        +filterBy(Predicate~T~ predicate) List~T~
        +getSorted(Comparator~T~ comparator) List~T~
        +findSorted() TreeSet~T~
    }

    class VehicleRepository {
        +VehicleRepository()
        +findByPlate(String plate) Vehicle
        +findEmergencyVehicles() List~EmergencyVehicle~
        +getSpeedSorted() List~Vehicle~
    }

    GenericRepository~T~ <|-- VehicleRepository : extends
    GenericRepository~T~ --> "HashMap" : uses
    GenericRepository~T~ --> "ArrayList" : uses
    GenericRepository~T~ --> "HashSet" : uses
    GenericRepository~T~ --> "TreeSet" : uses
```

---

## Service & Threading Layer

```mermaid
classDiagram
    direction TB

    class Runnable {
        <<interface>>
        +run() void
    }

    class Thread {
        +start() void
        +interrupt() void
        +join() void
        +setDaemon(boolean) void
    }

    class SimulationEngine {
        -volatile boolean running
        -ReentrantLock intersectionLock
        -List~Intersection~ intersections
        -GenericRepository~Vehicle~ vehicleRepo
        +SimulationEngine(List~Intersection~ intersections)
        +run() void
        +stop() void
        -simulateTick() void
        -generateVehicles() void
        -updateDensities() void
    }

    class SignalTimerThread {
        -TrafficSignal signal
        -volatile boolean active
        +SignalTimerThread(TrafficSignal signal)
        +run() void
        +deactivate() void
    }

    class TrafficControllerService {
        -GenericRepository~Intersection~ intersectionRepo
        -GenericRepository~Vehicle~ vehicleRepo
        -SimulationEngine simulationEngine
        -LinkedList~String~ eventQueue
        +TrafficControllerService()
        +startSimulation() void
        +stopSimulation() void
        +getIntersectionStatus(String id) String
        +setSignalState(String intersectionId, String signalId, SignalState state) void
        +handleEmergency(String intersectionId, String vehicleId) void
        +getReport() String
    }

    class ReportGenerator {
        +generateTrafficReport(List~Intersection~ data) String
        +exportToCsv(List~SensorData~ data, String filePath) void
        +validatePlateNumber(String plate) boolean
    }

    SimulationEngine ..|> Runnable : implements
    SignalTimerThread --|> Thread : extends
    TrafficControllerService --> SimulationEngine : manages
    TrafficControllerService --> GenericRepository~T~ : uses
    TrafficControllerService --> ReportGenerator : delegates
```

---

## Networking Layer

```mermaid
classDiagram
    direction LR

    class TrafficServer {
        -ServerSocket serverSocket
        -int port
        -TrafficControllerService controller
        -volatile boolean running
        +TrafficServer(int port, TrafficControllerService controller)
        +start() void
        +stop() void
        -handleClient(Socket clientSocket) void
        -processCommand(String command) String
    }

    class MonitoringClient {
        -Socket socket
        -BufferedReader reader
        -PrintWriter writer
        +MonitoringClient(String host, int port)
        +connect() void
        +sendCommand(String command) String
        +disconnect() void
        +main(String[] args) void
    }

    class EmergencySignalUDP {
        -DatagramSocket socket
        -int port
        -TrafficControllerService controller
        +EmergencySignalUDP(int port, TrafficControllerService controller)
        +startListening() void
        +broadcastEmergency(String intersectionId, String vehicleId, int priority) void
        -parsePacket(DatagramPacket packet) String[]
        +stop() void
    }

    TrafficServer --> TrafficControllerService : uses
    EmergencySignalUDP --> TrafficControllerService : uses
    MonitoringClient ..> TrafficServer : connects via TCP
```

---

## Exception Hierarchy

```mermaid
classDiagram
    direction TB

    class Exception {
        <<java.lang>>
    }
    class RuntimeException {
        <<java.lang>>
    }

    class TrafficSystemException {
        -String errorCode
        +TrafficSystemException(String message)
        +TrafficSystemException(String message, Throwable cause)
        +getErrorCode() String
    }

    class SensorReadException {
        -String sensorId
        +SensorReadException(String message, String sensorId, IOException cause)
        +getSensorId() String
    }

    class SignalFailureException {
        -String signalId
        +SignalFailureException(String message, String signalId)
        +getSignalId() String
    }

    Exception <|-- TrafficSystemException : extends (checked)
    TrafficSystemException <|-- SensorReadException : extends (checked, cause-chained)
    RuntimeException <|-- SignalFailureException : extends (unchecked)
```

---

## GUI Component Tree

```mermaid
classDiagram
    direction TB

    class MainDashboard {
        -JTabbedPane tabbedPane
        -JMenuBar menuBar
        -TrafficControllerService controller
        +MainDashboard(TrafficControllerService controller)
        +initializeUI() void
        -createMenuBar() JMenuBar
        -setupTabs() void
    }

    class LoginForm {
        -JTextField usernameField
        -JPasswordField passwordField
        -JButton loginButton
        +LoginForm()
        +validateCredentials() boolean
        -onLoginSuccess() void
    }

    class IntersectionCanvas {
        -List~Intersection~ intersections
        -javax.swing.Timer animationTimer
        +IntersectionCanvas(List~Intersection~ intersections)
        +paintComponent(Graphics g) void
        -drawTrafficLight(Graphics2D g2d, TrafficSignal signal, int x, int y) void
        -drawVehicleQueue(Graphics2D g2d, int count, int x, int y) void
        -drawCongestionIndicator(Graphics2D g2d, double level, int x, int y) void
        +startAnimation() void
        +stopAnimation() void
    }

    class SignalControlPanel {
        -JComboBox~String~ intersectionSelector
        -JButton overrideButton
        +SignalControlPanel(TrafficControllerService controller)
        -onOverride(ActionEvent e) void
    }

    class SensorDataForm {
        -JTextField sensorIdField
        -JTextField vehicleCountField
        -JButton submitButton
        +SensorDataForm(TrafficControllerService controller)
        -onSubmit(ActionEvent e) void
    }

    class ReportViewPanel {
        -JTable reportTable
        -JTextArea reportText
        +ReportViewPanel(TrafficControllerService controller)
        +refreshReport() void
    }

    LoginForm ..> MainDashboard : opens on success
    MainDashboard *-- IntersectionCanvas : tab "Monitor"
    MainDashboard *-- SignalControlPanel : tab "Control"
    MainDashboard *-- SensorDataForm : tab "Sensors"
    MainDashboard *-- ReportViewPanel : tab "Reports"
```

---

## Full System Dependency Graph

```mermaid
graph TB
    subgraph "GUI Layer"
        LoginForm --> MainDashboard
        MainDashboard --> IntersectionCanvas
        MainDashboard --> SignalControlPanel
        MainDashboard --> SensorDataForm
        MainDashboard --> ReportViewPanel
    end

    subgraph "Network Layer"
        TrafficServer
        MonitoringClient
        EmergencySignalUDP
    end

    subgraph "Service Layer"
        TrafficControllerService
        SimulationEngine
        ReportGenerator
    end

    subgraph "Repository Layer"
        GenericRepository
        VehicleRepository
    end

    subgraph "Model Layer"
        Vehicle
        StandardVehicle
        EmergencyVehicle
        TrafficSignal
        Intersection
        SensorData
        SignalState
    end

    subgraph "Strategy Layer"
        SignalControlStrategy
        DynamicDensityStrategy
        EmergencyPriorityStrategy
    end

    subgraph "Utility Layer"
        FileIOHandler
        AnalyticsUtils
        SystemConfig
    end

    subgraph "Exception Layer"
        TrafficSystemException
        SensorReadException
        SignalFailureException
    end

    MainDashboard --> TrafficControllerService
    SignalControlPanel --> TrafficControllerService
    SensorDataForm --> TrafficControllerService
    ReportViewPanel --> TrafficControllerService

    TrafficServer --> TrafficControllerService
    EmergencySignalUDP --> TrafficControllerService

    TrafficControllerService --> SimulationEngine
    TrafficControllerService --> ReportGenerator
    TrafficControllerService --> GenericRepository
    TrafficControllerService --> VehicleRepository

    SimulationEngine --> Intersection
    SimulationEngine --> Vehicle

    ReportGenerator --> FileIOHandler
    ReportGenerator --> AnalyticsUtils

    TrafficSignal --> SignalControlStrategy
    DynamicDensityStrategy --> SensorData
    EmergencyPriorityStrategy --> SensorData

    VehicleRepository --> Vehicle
    GenericRepository --> SystemConfig

    TrafficControllerService --> TrafficSystemException
    TrafficControllerService --> SignalFailureException
    FileIOHandler --> SensorReadException
```

---

*Diagram rendered using Mermaid. View in any Mermaid-compatible markdown renderer.*
