
package alviz2.graph.factory;

import java.util.List;
import java.util.LinkedList;
import javafx.geometry.Point2D;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.generate.CompleteGraphGenerator;

import alviz2.graph.factory.GraphFactory;
import alviz2.graph.Node;
import alviz2.graph.Edge;
import alviz2.util.InputDialog;

public class GameTree implements GraphFactory {

	public String getName() {
		return "Game Tree";
	}

	@Override
	public <N extends Node, E extends Edge> Graph<N,E> createGraph(VertexFactory<N> vfac, EdgeFactory<N,E> efac) {
		Integer inp = InputDialog.getIntegerInput("Game Tree Factory", "No. of levels?", 2, Integer.MAX_VALUE);
		if (inp == null) {
			return null;
		}

		int levels = inp;
		final double levelSep = ((2 << levels-1) * (5.0+5.0)) / (Math.PI * levels);
		final double shift = levels * levelSep;
		Graph<N,E> graph = new SimpleGraph<>(efac);
		List<N> parents = new LinkedList<>();
		List<N> children = new LinkedList<>();
		Node.PropChanger npr = Node.PropChanger.create();

		N n = vfac.createVertex();
		npr.setPosition(n, polarToCartesian(0, 0, shift));
		graph.addVertex(n);
		parents.add(n);
                n.setChildNo(0);
                n.setIsMax(true);
                n.setAlpha(Double.NEGATIVE_INFINITY);
                n.setBeta(Double.POSITIVE_INFINITY);

		for(int i=1; i<=levels; i++) {
			final double curRadius = i*levelSep;
			double th = 0;
			final double thDelta = Math.PI / parents.size();

			for (N p : parents) {
				N c1 = vfac.createVertex();
				N c2 = vfac.createVertex();
                                if(i%2==0)
                                {
                                    c1.setIsMax(true);
                                    c2.setIsMax(true);
                                    c1.setChildNo(1);
                                    c2.setChildNo(2);
                                }
                                else
                                {
                                    c1.setIsMax(false);
                                    c2.setIsMax(false);
                                    c1.setChildNo(1);
                                    c2.setChildNo(2);
                                }
				npr.setPosition(c1, polarToCartesian(curRadius, th + 0.5 * thDelta, shift));
				npr.setPosition(c2, polarToCartesian(curRadius, th + 1.5 * thDelta, shift));
				th += 2 * thDelta;
				graph.addVertex(c1);
				graph.addVertex(c2);
				graph.addEdge(p, c1);
				graph.addEdge(p, c2);
				children.add(c1);
				children.add(c2);
			}
			parents = children;
			children = new LinkedList<>();
		}

		return graph;
	}

	private Point2D polarToCartesian(double r, double theta, double shift) {
		Point2D p = new Point2D(r * Math.cos(theta) + shift, r * Math.sin(theta) + shift);
		return p;
	}
}