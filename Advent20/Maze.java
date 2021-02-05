import java.util.*;
class Maze{
	private int layer = -1;
	private ArrayList<Tile> tiles;
	private Tile entrance = null, exit = null;
	private ArrayList<Integer> path;
	private String tab;
	public Maze(int l, ArrayList<Integer> p, String T){
		tab = T;
		path = p;
		layer = l;
		if(l == 0){
			tiles = Layout.layer_zero();
		}else{
			tiles = Layout.not_layer_zero();
		}
		for(Tile t : tiles){
			if(t.value().equals("AA")) entrance = t;
			if(t.value().equals("ZZ")) exit = t;
			if(entrance != null && exit != null) break;
		}
	}

	public HashMap<Tile[],Maze> traverse(Tile start){
		if(layer == 0 || layer == 1){
			for(Tile t : tiles){
				if(t.ID() == start.ID()){
					if(t != start){
						start = t;
					}
					break;
				}
			}
		}
		HashMap<Tile[],Maze> retval = new HashMap<>();
		ArrayList<Integer> passOn = new ArrayList<>(path);
		passOn.add((start.ID() * 1000) + layer);
		ArrayList<Tile> keys = start.keys();
		for(int i = 0; i < keys.size(); i++){
			int newLayer = layer;
			if(keys.get(i).outerCircle()){
				newLayer--;
			}else if(keys.get(i).innerCircle()){
				newLayer++;
			}
			if(keys.get(i) == exit || keys.get(i) == entrance){
				passOn.add((keys.get(i).ID() * -1000) - 1);
				retval.put(new Tile[]{ null,keys.get(i) }, new Maze(newLayer,passOn,""));
				passOn.remove(passOn.size() - 1);
			}else if(newLayer >= 0){
				Maze subMaze = null;
				boolean equals = true;
				for(Maze m : retval.values()){
					if(m.layer == newLayer){
						subMaze = m;
						break;
					}
				}
				if(subMaze == null) subMaze = new Maze(newLayer,passOn,tab + "  ");
				retval.put(new Tile[]{ null,keys.get(i) },subMaze);
			}
		}
		return retval;
	}

	public int layer(){ return layer; }
	public ArrayList<Integer> getPath(){ return path; }
	public ArrayList<Tile> tiles(){ return tiles; }
}
