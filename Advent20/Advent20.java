import java.util.*;
import java.io.*;
class Advent20{
	static Tile entrance;
	static Tile exit;
	public static void main(String[] args){
		int thou = 1000, mil = thou * thou;
		ArrayList<Tile> partOne = Layout.part_one();
		for(Tile t : partOne){ if(t.value().equals("AA")){ entrance = t; }else if(t.value().equals("ZZ")){ exit = t; }}
		entrance.step(0);
// 		Part I:		580
		println("Part I:\t\t" + exit.steps());
		entrance.reset();
		Maze base = new Maze(0,new ArrayList<Integer>(),"");
		for(Tile t : base.tiles()){
			if(t.value().equals("AA")) entrance = t;
			if(t.value().equals("ZZ")) exit = t;
		}
 		ArrayList<Integer> path = traverse(base,exit);
		Tile[][] map = Layout.map();
		ArrayList<Tile> tiles = new ArrayList<>();
		for(Tile[] T : map) for(Tile t : T) if(t.isPortal()) tiles.add(t);
		Tile start = null;
		int voyage = 0;
		String journey = "";
		for(int i = 0; i < path.size(); i++){
			int layer = path.get(i) % thou;
			int y = Math.abs((path.get(i)) / thou) % thou;
			int x = Math.abs(path.get(i)) / mil;
			if(start != null){
				Tile standin = map[y][x];
				if(!start.reachable().keySet().contains(standin)) for(Tile t : start.reachable().keySet()){
					if(standin.value().equals(t.value())){
						standin = t;
						break;
					}
				}
				if(journey.length() > 0) journey += " + 1 + ";
				journey += start.reachable().get(standin);
				if(voyage > 0) voyage += 1;
				voyage += start.reachable().get(standin);
			}
			start = map[y][x];
			start.stepP2(0);
			for(Tile t : tiles) start.reach(t);
			start.reset();
			println(layer + ") " + start.value() + ": " + start.reachable().toString());
		}
		println(journey);
		println("Part II:\t" + voyage);
	}

	static ArrayList<Integer> traverse(Maze start,Tile Start){
		ArrayList<Integer> successfulPath = null;
		HashMap<Tile[],Integer> history = new HashMap<>();
		HashMap<Tile[],Maze> result = start.traverse(Start);
		HashMap<Tile[],Maze> breadth = new HashMap<>(result);
		while(successfulPath == null){
			HashMap<Tile[],Maze> newBreadth = new HashMap<>();
			for(Tile[] key : breadth.keySet()){
				boolean found = false;
				for(Tile[] Key : history.keySet()){
					if(key[1].ID() == Key[1].ID() && breadth.get(key).layer() == history.get(Key)){
						found = true;
						break;
					}
				}
				if(found) continue;
				history.put(key,breadth.get(key).layer());
				result = breadth.get(key).traverse(key[1].teleport());
				for(Tile[] T : result.keySet()){
					Maze passAlong = result.get(T);
					boolean same = true;
					if(T[1].value().equals(Start.value().equals("AA") ? "ZZ" : "AA")){
						successfulPath = passAlong.getPath();
						break;
					}else if(!T[1].value().equals(Start.value())){
						for(Tile[] t : newBreadth.keySet()){
							if(T[1].ID() == t[1].ID() && passAlong.layer() == newBreadth.get(t).layer()){
								same = true;
								break;
							}
						}
						if(!same) continue;
						for(Maze m : breadth.values()){
							if(m == passAlong) break;
							same = m != passAlong && m.layer() == passAlong.layer();
							if(same && m.getPath() == passAlong.getPath()){
								passAlong = m;
								break;
							}
							if(same) same = m.getPath().size() == passAlong.getPath().size();
							for(int i = 0; i < m.getPath().size() && same; i++){
								same = m.getPath().get(i) == passAlong.getPath().get(i);
							}
							if(same){
								passAlong = m;
								break;
							}
						}
						newBreadth.put(T,passAlong);
					}
				}
			}
			breadth = newBreadth;
		}
		return successfulPath;
	}

	public static void print(Object val){ System.out.print(val); }
	public static void println(Object val){ System.out.println(val); }
	public static void println(){ System.out.println(); }
}
