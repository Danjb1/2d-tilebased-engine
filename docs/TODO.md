# To Do

## Features

 - Support level extensions (e.g. multiple tile layers)

 - Support slopes with different gradients

 - Support large entities on slopes (currently untested)

## Bugs

 - Slopes:
    - Hitbox behaves strangely when colliding with 2 slope tiles in the same column
        - Corners of a slope "diamond"
        - Very tight sloped tunnels (especially when jumping!)
    - Hitbox clips through solid blocks if "wedged" between a slope and a solid block
    - Hitbox can clip through the "back" of a slope (not yet supported)
    - Hitbox can fall through a slope if there is no solid tile immediately below it
    - Hitboxes with width > height can clip through a wall at the top of a slope

## Tech Debt

 - Don't log to System.out

 - Move more code from demo project to engine?

 - Reduce duplication between the various slope classes

 - Improve test coverage (use reflection to test private methods)

## Demo

 - Slopes

 - Level reader

 - Allow changing the viewport size
