# To Do

## Features

 - Support multiple Tile layers

 - Support slopes with different gradients

 - Support large entities on slopes (currently untested)

## Bugs

 - Slopes:
    - Non-slope-traversing Entities (e.g. projectiles) collide at the wrong place
    - Occasional jittering at the bottom of ceiling slopes
    - Jittering when a slope leads into a ceiling
    - Strange behaviour when colliding with the wrong side of a slope tile
    - Strange behaviour when colliding with the left/right corners of a diamond

## Tech Debt

 - Move more code from demo project to engine?

 - Improve test coverage (use reflection to test private methods)

## Demo

 - Slopes

 - Level reader

 - Allow changing the viewport size
