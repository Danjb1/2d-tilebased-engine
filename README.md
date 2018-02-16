# 2D Game Engine

A 2D tile-based game engine using axis-aligned bounding boxes.

## Features

**:balloon: Ultra-lightweight**

Handles state management, physics, entities and camera tracking out-of-the-box, with no external dependencies.
 
**:mount_fuji: Slope support**

Supports 45 degree floor and ceiling slopes, and new tile types can easily be added.

**:electric_plug: Extendible component-based entity system**

Entities can easily be extended with additional properties and behaviour using a flexible component-based system.

**:books: Easily integrate with any GUI / rendering / input library**

Window creation, rendering and input handling are abstracted; the engine is not tied to any existing libraries.

## Getting Started

### Initialise Display

Write your own implementation for `Launcher.createDisplay()` to create a
window using your preferred library.

### Handle Input

Write your own implementation for `State.pollInput()` to get the user's
input using your preferred library.

### Create a Level

Create your own level and feed it into the logic in `Launcher.main`.

### Add Entities

To add an Entity to the world, subclass Entity and use the following:

    MyEntity entity = new MyEntity(x, y, width, height);
    logic.addEntity(entity);

Components can be added to Entities to add new properties and behaviour. For
example, you might add a graphical component to an Entity to allow it to be
rendered.

### Implement Rendering

Write your own implementation for `GameState.render()` to render the level
and any Entities with graphical components attached to them.

### Camera Tracking

To make the camera track a particular Entity, use the following code:

    camera.trackEntity(entity);
    camera.teleportToDestination();
