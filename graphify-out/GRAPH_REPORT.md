# Graph Report - OOTS_Capstone_Project  (2026-09-21)

## Corpus Check
- Corpus is ~23,028 words - fits in a single context window. You may not need a graph.

## Summary
- 352 nodes · 847 edges · 11 communities (9 shown, 2 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 132 edges (avg confidence: 0.81)
- Token cost: 1,500 input · 850 output

## Community Hubs (Navigation)
- GUI Presentation & Swing Views
- Exceptions & File I/O Telemetry
- Vehicle Fleet & Reporting Models
- Signal Monitoring & Canvas Renderer
- Architecture & Control Strategies
- Generic Repositories & Concurrency
- Bootstrap & System Configuration
- TCP Networking & Server Commands
- Emergency Dispatch & Authentication
- Intersection Grid & Congestion Dynamics
- UDP Emergency Telemetry Broadcast

## God Nodes (most connected - your core abstractions)
1. `TrafficControllerService` - 52 edges
2. `Intersection` - 50 edges
3. `TrafficSignal` - 34 edges
4. `Vehicle` - 32 edges
5. `SystemConfig` - 23 edges
6. `GenericRepository` - 23 edges
7. `SensorData` - 22 edges
8. `EmergencyVehicle` - 18 edges
9. `TrafficSystemException` - 16 edges
10. `IntersectionCanvas` - 16 edges

## Surprising Connections (you probably didn't know these)
- `Smart Traffic Capstone Project Overview` --references--> `Layered Architecture`  [EXTRACTED]
  README.md → docs/architecture.md
- `LoginForm` --references--> `TrafficControllerService`  [EXTRACTED]
  src/com/trafficsmart/gui/LoginForm.java → src/com/trafficsmart/service/TrafficControllerService.java
- `SignalControlPanel` --references--> `SignalState`  [EXTRACTED]
  src/com/trafficsmart/gui/SignalControlPanel.java → src/com/trafficsmart/model/SignalState.java
- `EmergencyVehicle` --inherits--> `Vehicle`  [EXTRACTED]
  src/com/trafficsmart/model/EmergencyVehicle.java → src/com/trafficsmart/model/Vehicle.java
- `Intersection` --implements--> `Monitorable`  [EXTRACTED]
  src/com/trafficsmart/model/Intersection.java → src/com/trafficsmart/model/Monitorable.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Emergency Priority Clearance Pipeline** — docs_architecture_emergency_override_flow, src_com_trafficsmart_net_emergencysignaludp, src_com_trafficsmart_strategy_emergencyprioritystrategy, src_com_trafficsmart_model_emergencyvehicle [INFERRED 0.95]

## Communities (11 total, 2 thin omitted)

### Community 0 - "GUI Presentation & Swing Views"
Cohesion: 0.06
Nodes (32): DefaultListModel, DefaultTableModel, java.awt.Color, java.awt.Graphics, java.awt.Graphics2D, javax.swing.DefaultListModel, javax.swing.JComboBox, javax.swing.JFrame (+24 more)

### Community 1 - "Exceptions & File I/O Telemetry"
Cohesion: 0.09
Nodes (10): Concurrent Threading & Lock Model, SensorReadException, TrafficSystemException, Override, SensorData, Socket, MonitoringClient, Override (+2 more)

### Community 2 - "Vehicle Fleet & Reporting Models"
Cohesion: 0.08
Nodes (8): java.util.regex.Pattern, Override, StandardVehicle, Override, Vehicle, ReportGenerator, AnalyticsUtils, SystemIntegrationTest

### Community 3 - "Signal Monitoring & Canvas Renderer"
Cohesion: 0.09
Nodes (5): Monitorable, Override, TrafficSignal, Override, SignalTimerThread

### Community 4 - "Architecture & Control Strategies"
Cohesion: 0.09
Nodes (11): Layered Architecture, Signal Control Strategy Pattern, FunctionalInterface, Smart Traffic Capstone Project Overview, SignalFailureException, EmergencyVehicle, Override, DynamicDensityStrategy (+3 more)

### Community 5 - "Generic Repositories & Concurrency"
Cohesion: 0.12
Nodes (4): java.util.concurrent.locks.Lock, GenericRepository, VehicleRepository, SimulationEngine

### Community 6 - "Bootstrap & System Configuration"
Cohesion: 0.10
Nodes (3): MainApplication, SystemConfig, Override

### Community 7 - "TCP Networking & Server Commands"
Cohesion: 0.13
Nodes (12): TCP Command Protocol, java.net.ServerSocket, java.net.Socket, ServerSocket, next(), SignalState, GREEN, RED (+4 more)

### Community 8 - "Emergency Dispatch & Authentication"
Cohesion: 0.14
Nodes (11): DatagramSocket, Emergency Override Sequence Flow, NIET 40-Mark Evaluation Rubric, java.net.DatagramSocket, javax.swing.JPasswordField, javax.swing.JTextField, JPasswordField, JTextField (+3 more)

## Knowledge Gaps
- **9 isolated node(s):** `RED`, `YELLOW`, `GREEN`, `Emergency Override Sequence Flow`, `TCP Command Protocol` (+4 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 85 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `TrafficControllerService` connect `GUI Presentation & Swing Views` to `Exceptions & File I/O Telemetry`, `Vehicle Fleet & Reporting Models`, `Signal Monitoring & Canvas Renderer`, `Architecture & Control Strategies`, `Generic Repositories & Concurrency`, `Bootstrap & System Configuration`, `TCP Networking & Server Commands`, `Emergency Dispatch & Authentication`, `Intersection Grid & Congestion Dynamics`?**
  _High betweenness centrality (0.298) - this node is a cross-community bridge._
- **Why does `Intersection` connect `Intersection Grid & Congestion Dynamics` to `GUI Presentation & Swing Views`, `Exceptions & File I/O Telemetry`, `Vehicle Fleet & Reporting Models`, `Signal Monitoring & Canvas Renderer`, `Architecture & Control Strategies`, `Generic Repositories & Concurrency`, `TCP Networking & Server Commands`?**
  _High betweenness centrality (0.200) - this node is a cross-community bridge._
- **Why does `TrafficSignal` connect `Signal Monitoring & Canvas Renderer` to `GUI Presentation & Swing Views`, `Exceptions & File I/O Telemetry`, `Architecture & Control Strategies`, `Generic Repositories & Concurrency`, `TCP Networking & Server Commands`, `Intersection Grid & Congestion Dynamics`?**
  _High betweenness centrality (0.119) - this node is a cross-community bridge._
- **What connects `RED`, `YELLOW`, `GREEN` to the rest of the system?**
  _9 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `GUI Presentation & Swing Views` be split into smaller, more focused modules?**
  _Cohesion score 0.06378378378378378 - nodes in this community are weakly interconnected._
- **Should `Exceptions & File I/O Telemetry` be split into smaller, more focused modules?**
  _Cohesion score 0.0898989898989899 - nodes in this community are weakly interconnected._
- **Should `Vehicle Fleet & Reporting Models` be split into smaller, more focused modules?**
  _Cohesion score 0.07777777777777778 - nodes in this community are weakly interconnected._