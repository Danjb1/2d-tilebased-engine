# Getting Started

These steps are all that is needed to get a simple game up and running.

## Initialise Display

Extend `Launcher` and provide your own implementation for window creation and
rendering using your preferred libraries.

## Handle Input

Listen for input using your preferred library, and pass events to the launcher's
`input` field. This will make the latest input available to the current state
each frame. 

## Create a State

The launcher has one active `State` at any time, which is updated and rendered
each frame. This may be a loading screen, a menu, or the game itself.

A basic game state should instantiate the `Logic` and a `Camera` (if
required), and override the following methods:

 - `processInput()`: Process the user input from the last frame.

 - `update()`: Update the `Logic` and `Camera`.
 
 - `render()` Tell some renderer to render the game.

## Start the Game!

In your `main` method, instantiate your launcher, load your initial state, and
call `launcher.start()`.

## Add Entities

To add an Entity to the world, subclass Entity and use the following:

    MyEntity entity = new MyEntity(x, y);
    logic.addEntity(entity);

Components can be added to Entities to add new properties and behaviour. For
example, you might add a graphical component to an Entity to allow it to be
rendered.

## Camera Tracking

To make the camera track a particular Entity, use the following code:

    camera.trackEntity(entity);
    camera.teleportToDestination();
