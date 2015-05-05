
public class Planner {
	
	public static void startPlanner(){
		Domain domain = new Domain();
		domain.Name = "wumpus";
		domain.parsePredicates("(adj ?i ?j - pos) (at ?i - pos) (safe ?i - pos) (wumpus-at ?x - pos) (alive) (stench ?i - pos)"
				+ " (gold-at ?i - pos) (got-the-treasure) (breeze ?i - pos) (pit-at ?p - pos)");
		domain.extract("constants p1-1 p1-2 p1-3 p1-4 p1-5 p2-1 p2-2 p2-3 p2-4 p2-5 p3-1 p3-2 p3-3 p3-4 p3-5 p4-1 p4-2 p4-3 p4-4 p4-5"
				+ " p5-1 p5-2 p5-3 p5-4 p5-5 - pos");
		
		/*Action Move*/
		Action a = new Action();
		a.Name = "move";
		a.parseParameters("?i - pos ?j - pos");
		a.parsePreconditions("(adj ?i ?j) (at ?i) (alive) (safe ?j) ");
		a.parseEffects("(not (at ?i)) (at ?j)");
		domain.addActions(a);
		
		/*Action Move*/
		Action b = new Action();
		b.Name = "smell_wumpus";
		b.parseParameters("?pos - pos");
		b.parsePreconditions("(alive) (at ?pos)");
		b.parseEffects("(stench ?pos)");
		domain.addActions(b);
		
		/*Action Move*/
		Action c = new Action();
		c.Name = "feel-breeze";
		c.parseParameters("?pos - pos");
		c.parsePreconditions("(alive) (at ?pos)");
		c.parseEffects("(breeze ?pos)");
		domain.addActions(c);
		
		/*Action Move*/
		Action d = new Action();
		d.Name = "grab";
		d.parseParameters("?i - pos");
		d.parsePreconditions("(at ?i) (gold-at ?i) (alive)");
		d.parseEffects("(got-the-treasure) (not (gold-at ?i))");
		domain.addActions(d);
		
		System.out.println("Done parsing.");
	}
}
