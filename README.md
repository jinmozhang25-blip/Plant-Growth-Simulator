# Plant-Growth-Simulator

A Java Swing-based interactive desktop application that simulates plant growth under varying environmental conditions. Users can adjust temperature, pH, and simulation speed to observe how different plant species respond to stress and optimal conditions in real-time.

# Overview

This simulator visualizes the growth of a plant from a seed to maturity using ecological models. It demonstrates how environmental factors—specifically **temperature** and **pH**—act as limiting factors for plant development.

The application features live statistical dashboards, a growth progress bar, and an interactive historical chart that tracks mass accumulation and health status over time.

---

# Features

- Multiple Plant Species – Choose from Lettuce, Tomato, or Basil, each with distinct environmental tolerances and growth rates.
- Real-time Environmental Controls – Adjust temperature (0–45°C) and pH (0–14) via sliders, with live warnings if values fall outside the plant's viable range.
- Adjustable Simulation Speed – Speed up or slow down time (0x – 10x) to observe long-term growth patterns quickly or examine daily changes closely.
- Interactive Growth Charts – A dynamic bar chart visualizes mass accumulation over time, color-coded by historical health status (Thriving, Stressed, Dying, Dead).
- Health Status Dashboard – Displays current day, growth rate, health status (with color indicators), total size, and a progress bar showing completion toward maximum size.
- Simulation Controls – Pause, resume, or reset the simulation at any point.

---

# How the Model Works

## Growth Engine (Logistic Growth)
The plant's growth follows the classic **logistic growth equation**:

dM/dt = r * M * (1 - M/K)


- `M` = Current mass (g)
- `K` = Maximum size (carrying capacity) for the species
- `r` = Effective growth rate (determined by environmental factors)

## Environmental Limiting (Liebig’s Law of the Minimum)
The effective growth rate `r` is derived from the plant's baseline maximum relative growth rate (`rMax`) multiplied by the **minimum** of two environmental factors:


Both factors range from **0.0** (lethal) to **1.0** (optimal), calculated based on how far the current condition is from the species-specific minimum, optimum, and maximum thresholds.

## Stress & Mortality
If either environmental factor drops to **0.0**, the plant begins accumulating "stress days." After **3 consecutive simulated days** of lethal conditions, the plant dies.

---

# Plant Species Included

| Species  | Temp Min | Temp Opt | Temp Max | pH Min | pH Opt | pH Max | rMax (relative) | Max Size (g) |
| Lettuce  | 4.0°C    | 20.0°C   | 35.0°C   | 5.5    | 6.5    | 7.5    | 0.27            | 40.0         |
| Tomato   | 10.0°C   | 25.0°C   | 35.0°C   | 5.0    | 6.0    | 7.0    | 0.15            | 200.0        |
| Basil    | 15.0°C   | 22.0°C   | 30.0°C   | 5.5    | 6.0    | 7.5    | 0.25            | 30.0         |

---

# How to Run

## Prerequisites
- **Java Development Kit (JDK)** 17 or higher
- A terminal or command prompt

## Compilation & Execution
1. Clone or download all source files into a single directory:

ChartPanel.java
Main.java
PlantSimulatorGUI.java
PlantSpecs.java
SimulationState.java

Run:
java Main


# Limitations & Educational Disclaimer
This simulator is built for educational and demonstrative purposes to illustrate concepts like logistic growth and Liebig's Law of the Minimum. It is not a scientifically accurate crop growth model. Key simplifications include:

No seedling lag phase – Germination vigor and stored seed energy are not modeled.

Delayed mortality – Acute lethal stress (e.g., freezing) causes death over days, whereas in reality it occurs within hours.

Fixed carrying capacity – Maximum size (K) is static; stress does not reduce the plant's final achievable mass.

Independent environmental factors – Temperature and pH act separately; interactive effects (e.g., pH altering nutrient uptake rates at different temperatures) are ignored.

Symmetric response curves – Temperature and pH tolerances are modeled as perfect triangles (linear slopes), which oversimplifies real plant physiology.

Use this tool to explore relative behaviors, not to predict actual agricultural yields or plant survival outcomes.
