# To Do

## Features

 - Support level extensions (e.g. multiple tile layers)

 - Support slopes with different gradients

 - Support large entities on slopes (currently untested)

## Bugs

 - Slopes:
    - Hitbox can clip through a right ceiling slope if jumping into the very top of it
    - Hitbox can drop off a right slope and land on the solid block 2 tiles below it
    - Hitbox clips through solid blocks if "wedged" between a slope and a solid block
    - Hitbox can clip through the "back" of a slope (not yet supported)

## Tech Debt

 - Don't log to System.out

 - Move more code from demo project to engine?

 - Reduce duplication between the various slope classes

 - Improve test coverage (use reflection to test private methods)

## Demo

 - Slopes

 - Level reader

 - Allow changing the viewport size
