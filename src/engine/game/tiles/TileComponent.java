package engine.game.tiles;

import engine.game.Component;

public class TileComponent extends Component {

    public TileComponent(String key) {
        super(key);
    }

    public void onAttach(Tile tile) {
        // Do nothing by default
    }

}
