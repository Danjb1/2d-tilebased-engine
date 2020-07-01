# To Do

## Features

 - Support level extensions (e.g. multiple tile layers)

 - Support slopes with different gradients

 - Support large entities on slopes (currently untested)

## Bugs

 - Slopes (see Slope class):
    - Hitbox behaves strangely in very tight sloped tunnels
    - Hitbox clips through solid blocks if "wedged" between a slope and a solid block
    - Hitbox can clip through the "back" of a slope (not yet supported)
    - Hitbox can fall through a slope if there is no solid tile immediately below it
    - Wide Hitboxes can clip through a wall at the top of a slope

## Tech Debt

 - Rename package to something less generic

 - Don't log to System.out

 - Move more code from demo project to engine?

 - Reduce duplication between the various slope classes

 - Improve test coverage (use reflection to test private methods)

## Demo

 - Slopes

 - Level reader

 - Allow changing the viewport size

 - Add LWJGL demo
