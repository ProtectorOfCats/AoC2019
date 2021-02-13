import java.util.*;
import java.io.*;
class Layout{
	private static ArrayList<Tile> allTiles = null, part_one = null, layer_zero = null, not_layer_zero = null;
	private static String p1 = null, lz = null, nlz = null;

	static ArrayList<Tile> part_one(){
		if(part_one == null){
			if(allTiles == null) buildList();
			ArrayList<Tile> tiles = copyList(allTiles);
			part_one = findPortals(tiles);
			p1 = buildString(tiles);
		}
		return part_one;
	}

	static Tile[][] map(){ if(allTiles == null){ buildList(); } return makeMap(new ArrayList<Tile>(allTiles)); }

	static String buildString(ArrayList<Tile> tiles){
		String retval = "";
		Tile[][] map = makeMap(tiles);
		for(int y = 0; y < map.length; y++){
			if(!map[y][0].isPortal()) retval += "╲";
			for(int x = 0; x < map[0].length; x++){
				if(x > 0 && map[y][x].isPortal() && map[y][x - 1].isVoid()){
					retval = retval.substring(0,retval.length() - 1);
					retval += map[y][x].value();
				}else if(x > 0 && x < map[0].length - 1 && map[y][x].isPortal() && map[y][x + 1].isVoid()){
					retval += map[y][x].value();
					x++;
				}else{
					retval += map[y][x].value();
				}
			}
			if(!map[y][map[0].length - 1].isPortal()) retval += "╲";
			retval += "\n";
		}
		retval += "\n";
		return retval;
	}

	static ArrayList<Tile> layer_zero(){
		if(layer_zero == null){
			if(allTiles == null) buildList();
			ArrayList<Tile> tiles = copyList(allTiles);
			ArrayList<Tile> portals = findPortals(tiles);
			for(Tile t : portals){
				if((!t.value().equals("AA") && !t.value().equals("ZZ")) && t.outerCircle()){
					int index = tiles.indexOf(t);
					tiles.set(index,new Tile(t.x(),t.y()," "));
				}
			}
			for(Tile t : tiles) t.totalWipe();
			makeMap(tiles);
			layer_zero = findPortals(tiles);
			for(Tile t : layer_zero){
				t.stepP2(0);
				for(Tile T : layer_zero) if(T != t) t.reach(T);
				t.reset();
			}
			lz = buildString(tiles);
		}
		return layer_zero;
	}

	static ArrayList<Tile> not_layer_zero(){
		if(not_layer_zero == null){
			if(allTiles == null) buildList();
			ArrayList<Tile> tiles = copyList(allTiles);
			ArrayList<Tile> portals = findPortals(tiles);
			for(Tile t : portals){
				if(t.value().equals("AA") || t.value().equals("ZZ")){
					int index = tiles.indexOf(t);
					tiles.set(index,new Tile(t.x(),t.y()," "));
				}
			}
			for(Tile t : tiles) t.totalWipe();
			makeMap(tiles);
			not_layer_zero = findPortals(tiles);
			for(Tile t : not_layer_zero){
				t.stepP2(0);
				for(Tile T : not_layer_zero) if(T != t) t.reach(T);
				t.reset();
			}
			nlz = buildString(tiles);
		}
		return not_layer_zero;
	}

	public static String partOneToString(){ if(p1 == null) part_one(); return p1; }
	public static String layerZeroToString(){ if(lz == null) layer_zero(); return lz; }
	public static String notLayerZeroToString(){ if(nlz == null) not_layer_zero(); return nlz; }

	private static void reset(ArrayList<Tile> tiles){ for(Tile t : tiles) t.reset(); }

	private static ArrayList<Tile> findPortals(ArrayList<Tile> tiles){
		ArrayList<Tile> portals = new ArrayList<>();
		for(Tile t : tiles){
			if(t.isPortal()) portals.add(t);
		}
		for(int a = 0; a < portals.size() - 1; a++){
			for(int b = a + 1; b < portals.size(); b++){
				if(portals.get(a).value().equals(portals.get(b).value())){
					portals.get(a).quantum(portals.get(b));
					portals.get(b).quantum(portals.get(a));
				}
			}
		}
		return portals;
	}

	private static ArrayList<Tile> copyList(ArrayList<Tile> list){
		ArrayList<Tile> retval = new ArrayList<>();
		for(Tile t : list) retval.add(t.copy());
		return retval;
	}

	private static void buildList(){
		allTiles = new ArrayList<>();
		ArrayList<char[]> rows = new ArrayList<>();
		try{
			BufferedReader br = new BufferedReader(new FileReader("input.txt"));
			String line;
			while((line = br.readLine()) != null){
				rows.add(line.toCharArray());
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		for(int a = 1; a < rows.size() - 1; a++){
			for(int b = 1; b < rows.get(a).length - 1; b++){
				String type = "";
				if(isLetter(rows.get(a)[b])){
					if(rows.get(a + 1)[b] == '.' || rows.get(a)[b + 1] == '.'){
						if(isLetter(rows.get(a - 1)[b]) && rows.get(a + 1)[b] == '.'){
							type += rows.get(a - 1)[b];
						}else if(isLetter(rows.get(a)[b - 1]) && rows.get(a)[b + 1] == '.'){
							type += rows.get(a)[b - 1];
						}
						type += rows.get(a)[b];
					}else if(rows.get(a - 1)[b] == '.' || rows.get(a)[b - 1] == '.'){
						type += rows.get(a)[b];
						if(isLetter(rows.get(a + 1)[b]) && rows.get(a - 1)[b] == '.'){
							type += rows.get(a + 1)[b];
						}else if(isLetter(rows.get(a)[b + 1]) && rows.get(a)[b - 1] == '.'){
							type += rows.get(a)[b + 1];
						}
					}else{
						type = " ";
					}
				}else{
					type = Character.toString(rows.get(a)[b]);
				}
				Tile newTile = new Tile(b - 1,a - 1,type);
				allTiles.add(newTile);
			}
		}
		makeMap(allTiles);
		ArrayList<Tile> portals = findPortals(allTiles);
		for(Tile t : portals) t.blockOff();
		reset(allTiles);
	}

	private static boolean isLetter(char c){
		if((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) return true;
		return false;
	}

	private static Tile[][] makeMap(ArrayList<Tile> tiles){
		int maxX = 0, maxY = 0;
		for(Tile t : tiles){
			maxX = Math.max(maxX,t.x());
			maxY = Math.max(maxY,t.y());
		}
		maxX++; maxY++;
		Tile[][] map = new Tile[maxY][maxX];
		for(Tile t : tiles) map[t.y()][t.x()] = t;
		for(int y = 1; y < map.length; y++){
			for(int x = 1; x < map[0].length; x++){
				map[y][x].addNeighbor(map[y - 1][x],0);
				map[y - 1][x].addNeighbor(map[y][x],2);
				map[y][x].addNeighbor(map[y][x - 1],3);
				map[y][x - 1].addNeighbor(map[y][x],1);
			}
		}
		return map;
	}
}
