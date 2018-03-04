package engine.game;

public class TestUtils {

    /**
     * Creates a Level from the given String input.
     * 
     * @param width
     * @param height
     * @param rows
     * @return
     */
    public static Level createLevel(int width, int height, String... rows) {
        
        int[][] tiles = new int[width][height];
        int y = 0;
        
        for (String row : rows) {
            
            int x = 0;
            String[] tilesThisRow = row.split("\\s+");
            
            for (String tile : tilesThisRow) {
                tiles[x][y] = Integer.parseInt(tile);
                x++;
            }
            
            y++;
        }
        
        return new Level(new TileLayer(tiles));
    }

}
