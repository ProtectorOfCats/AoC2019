import java.util.*;
import java.time.*;

class Tile{
	private int x,y,steps = Integer.MAX_VALUE;
	private String type;
	private Tile[] neighbors = new Tile[4];
	private ArrayList<Tile> keys = null;
	private HashMap<Tile,Integer> reachable = new HashMap<>();
	private Tile mirror = null;
	private boolean touched = false;
	private static int maxX,maxY;
	public Tile(int X, int Y){
		x = X; y = Y;
		maxX = Math.max(maxX,x); maxY = Math.max(maxY,y);
	}

	public Tile(int X, int Y, String t){
		x = X; y = Y;
		maxX = Math.max(maxX,x); maxY = Math.max(maxY,y);
		if(t.equals(" ")){
			type = "╲";
		}else if(t.equals(".")){
			type = " ";
		}else{
			type = t;
		}
		if(isWall() || isVoid()) neighbors = null;
	}

	public ArrayList<Tile> keys(){
		if(keys == null){
			keys = new ArrayList<>(reachable.keySet());
			for(int i = reachable.size() - 1; i >= 0; i--){
				if(keys.get(i).innerCircle()){
					keys.add(keys.remove(i));
				}
			}
		}
		return keys;
	}

	public void quantum(Tile t){
		mirror = t;
	}

	public Tile teleport(){
		return mirror;
	}

	public int ID(){ return (x * 1000) + y; }

	public HashMap<Tile,Integer> reachable(){ return reachable; }

	public void reach(Tile t){
		if(t != this && t.steps() != Integer.MAX_VALUE) reachable.put(t,t.steps);
	}

	public void reset(){
		if(steps != Integer.MAX_VALUE){
			steps = Integer.MAX_VALUE;
			if(neighbors != null) for(Tile n : neighbors) if(n != null) n.reset();
			if(mirror != null) for(Tile n : mirror.neighbors) if(n != null) n.reset();
		}
	}

	public void stepP2(int i){
		if(i < steps){
			if(isPortal()){
				steps = i - 1;
			}else{
				steps = i;
			}
			if(i == 0 || !isPortal()){
				for(Tile n : neighbors){
					if(n != null){
						n.stepP2(steps + 1);
					}
				}
			}
		}
	}

	public void step(int i){
		if(i < steps){
			if(isPortal()){
				steps = i - 1;
			}else{
				steps = i;
			}
			for(Tile n : neighbors) if(n != null) n.step(steps + 1);
			if(mirror != null) for(Tile n : mirror.neighbors) if(n != null) n.step(steps + 1);
		}
	}

	public void addNeighbor(Tile t, int index){
		if((!isWall() && !isVoid())){
			if((!t.isWall() && !t.isVoid())){
				neighbors[index] = t;
			}else{
				neighbors[index] = null;
			}
		}
	}

	public void totalWipe(){
		internalReset();
		resetNeighbors();
		resetReachable();
	}

	public void internalReset(){
		steps = Integer.MAX_VALUE;
	}

	public void resetNeighbors(){
		for(int i = 0; neighbors != null && i < 4; i++){
			neighbors[i] = null;
		}
	}

	public void resetReachable(){
		reachable.clear();
	}

	public void blockOff(){
		if(touched) return;
		blockOff(0);
	}

	public boolean blockOff(int X){
		boolean iAmWorthIt = isPortal();
		if(iAmWorthIt) touched = true;
		boolean pathIsWorthIt = false;
		if(X < steps){
			steps = X;
			boolean thisPathIsWorthIt;
			for(int i = 0; neighbors != null && i < neighbors.length; i++){
				Tile t = neighbors[i];
				if(t != null && X + 1 < t.steps){
					thisPathIsWorthIt = t.blockOff(X + 1);
					if(thisPathIsWorthIt){
						pathIsWorthIt = true;
					}else{
						if(t.block()) neighbors[i] = null;
					}
				}
			}
		}
		return iAmWorthIt || pathIsWorthIt;
	}

	private boolean block(){
		boolean nextToPortal = false;
		for(int i = 0; !nextToPortal && i < neighbors.length; i++){
			if(neighbors[i] != null && neighbors[i].isPortal()) nextToPortal = true;
		}
		if(!nextToPortal){
			type = "╳";
			for(int i = 0; i < 4; i++){
				if(neighbors[i] != null){
					neighbors[i].neighbors[(i + 2) % 4] = null;
				}
			}
			neighbors = null;
		}
		return neighbors == null;
	}

	public Tile copy(){
		if(isVoid() || isWall()) return this;
		Tile newTile = new Tile(x,y);
		newTile.type = type;
		return newTile;
	}

	public int steps(){ return steps; }
	public int x(){ return x; }
	public int y(){ return y; }
	public String toString(){ return type/* "(" + x + "," + y + ")" */; }
	public String value(){ return type; }

	public boolean innerCircle(){ return (x > 0 && x < maxX) && (y > 0 && y < maxY); }
	public boolean outerCircle(){ return !innerCircle(); }
	public boolean isVoid(){ return type.equals("╲") || type.equals("▒"); }
	public boolean isWall(){ return !(isClear() || isPortal() || isVoid()); }
	public boolean isClear(){ return type.equals(" "); }
	public boolean isPortal(){ return type.matches("[A-Z]{2}"); }
}
