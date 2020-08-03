# To Do

## Features

 - Support level extensions (e.g. multiple tile layers)

 - Support slopes with different gradients

 - Support large entities on slopes (currently untested)

 - Hitbox should have a list of listeners

 - HitboxListener should inform listeners when the Hitbox goes out of bounds in any direction

## Bugs

 - Slopes (see Slope class):
    - Hitbox behaves strangely in very tight sloped tunnels
    - Hitbox clips through solid blocks if "wedged" between a slope and a solid block
    - Hitbox can clip through the "back" of a slope (not yet supported)
    - Hitbox can fall through a slope if there is no solid tile immediately below it
    - Wide Hitboxes can clip through a wall at the top of a slope

## Tech Debt

 - Reduce duplication between the various slope classes

 - Improve test coverage (use reflection to test private methods)

 - Allow Level to be subclassed instead of relying exclusively on components to add functionality?

 - Tile type constants in PhysicsTile are not extensible

 - Should Components be destroyed immediately when deleted?

 - Move HitboxListener functionality out of Entity class into components?

 - Try to simplify slope physics

## Demo

 - Slopes

 - Level reader

 - Allow changing the viewport size

 - Add LWJGL demo
