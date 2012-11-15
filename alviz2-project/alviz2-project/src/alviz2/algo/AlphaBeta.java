
package alviz2.algo;

import alviz2.app.ColorPalette;
import alviz2.graph.Edge;
import alviz2.graph.Node;
import alviz2.util.AlgorithmRequirements;
import alviz2.util.GraphInit;
import alviz2.util.GraphType;
import java.util.*;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;

@AlgorithmRequirements (
	graphType = GraphType.GAME_TREE,
	graphInitOptions = {}
)

public class AlphaBeta implements Algorithm<Node, Edge> {

	private Graph<Node, Edge> graph;
	private XYChart<Number, Number> chart;
	private Node.PropChanger npr;
	private Edge.PropChanger epr;
	private Node start;
	private LinkedList<Node> open;
	private Set<Node> closed;
	private Map<Node, Node> parents;
	private XYChart.Series<Number, Number> openListSeries;
	private XYChart.Series<Number, Number> closedListSeries;
	private int iterCnt;
	private ColorPalette palette;
        private Stack<Node> stack; 
        boolean flag = true;
        int count =0;
	public static class VFac implements VertexFactory<Node> {
		int id;
		
		private VFac() {
			id = 0;
		}

		@Override public Node createVertex() {
			return new Node(id++);
		}
	}

	public static class EFac implements EdgeFactory<Node, Edge> {
		int id;

		private EFac() {
			id = 0;
		}

		@Override public Edge createEdge(Node s, Node d) {
			return new Edge(id++, s, d);
		}
	}

	public AlphaBeta() {
		graph = null;
		chart = null;
		start = null;
		open = new LinkedList<>();
		closed = new HashSet<>();
		parents = new HashMap<>();
		openListSeries = new XYChart.Series<>();
		closedListSeries = new XYChart.Series<>();
		iterCnt = 0;
		palette = ColorPalette.getInstance();
                stack = new Stack();
	}

	@Override
	public void setGraph(Graph<Node,Edge> graph, Node.PropChanger npr, Edge.PropChanger epr, Set<Node> start, Set<Node> goals) {
		this.graph = graph;
		this.epr = epr;
		this.npr = npr;
		
		for (Node n : graph.vertexSet()) {
                        npr.setVisible(n, true);
                        if(flag)
                        {
                            this.start = n;
                            flag = false;
                            count =1;
                        }
                
                       
                        double random = Math.ceil((Math.random()*50));
                        List<Node> neighborListOf = Graphs.neighborListOf(this.graph, n);
                        if(neighborListOf.size() == 1)
                        n.setCost(random-25);
                        System.out.println(n.getCost()+ " " +n.getIsMax());
		}

		/*for (Edge e : graph.edgeSet()) {
			epr.setVisible(e, true);
		}*/

		stack.push(this.start);
                parents.put(this.start, null);
		npr.setFillColor(this.start, palette.getColor("node.open"));
	}

	@Override
	public void setChart(XYChart<Number, Number> chart) {
		this.chart = chart;
		this.chart.setTitle("Depth First Search");
		openListSeries.setName("Size of open list");
		closedListSeries.setName("Size of closed list");
		this.chart.getData().add(openListSeries);
		this.chart.getData().add(closedListSeries);
	}

	@Override public VertexFactory<Node> getVertexFactory()
	{
		return new VFac();
	}

	@Override public EdgeFactory<Node,Edge> getEdgeFactory()
	{
		return new EFac();
	}

	@Override
	public boolean executeSingleStep() {
            if(stack.empty())
            {
                return false;
            }
            
        Node peek = stack.peek();
        if(!closed.contains(peek))//If Closed does not contain peek node generate and push childrens in stack
        {
                List<Node> neighborListOf = Graphs.neighborListOf(graph, peek);
                for (Node nn : neighborListOf) {
                        
				if(parents.containsKey(peek)&& parents.get(peek)!=null &&parents.get(peek).equals(nn))
                                {
                                    peek.setAlpha(nn.getAlpha());
                                    peek.setBeta(nn.getBeta());
                                    continue ;
                                }
                                else
                                {
                                    stack.push(nn);
                                    npr.setFillColor(nn, palette.getColor("node.open"));
                                    //epr.setStrokeColor(graph.getEdge(peek, nn), palette.getColor("edge.open"));
                                    parents.put(nn, peek);
                                    nn.setAlpha(peek.getAlpha());
                                    nn.setBeta(peek.getBeta());
                                }
		}
                        npr.setFillColor(peek, palette.getColor("node.closed"));
                        closed.add(peek);
        }
        
        if(peek.equals(stack.peek())) //If Peeked node is still at top of stack
        {
                Node popped = stack.pop();
                npr.setFillColor(popped, palette.getColor("node.path"));
                Node dad = parents.get(popped);
                if(dad!= null)
                {
                    epr.setStrokeColor(graph.getEdge(popped, dad), Color.DARKGRAY);
                    epr.setVisible(graph.getEdge(popped, dad), true);
                if(Graphs.neighborListOf(graph, popped).size()==1)//If it is leaf
                {
                    
                    if(popped.getIsMax())
                    {
                        popped.setAlpha(popped.getCost());
                        if(popped.getCost()<dad.getBeta())
                        {
                            
                            dad.setBeta(popped.getCost());
                            
                        }
                        if(dad.getAlpha() >= dad.getBeta())
                            {
                        
                            dad.setCost(dad.getAlpha());
                            while(!stack.pop().equals(dad));
                            }
                    }
                    else
                    {
                        popped.setBeta(popped.getCost());
                        if(popped.getCost()>dad.getAlpha())
                        {
                            dad.setAlpha(popped.getCost());
                        
                        }
                        if(dad.getAlpha() >= dad.getBeta())
                            {
                        
                            dad.setCost(dad.getBeta());
                            while(!stack.pop().equals(dad));
                            }
                    }
                    
                }    
                else    //Else it is not a leaf
                {
                    if(popped.getIsMax())
                    {
                        popped.setCost(popped.getAlpha());
                        if(popped.getCost()<dad.getBeta())
                        {
                            dad.setBeta(popped.getCost());
                        
                        }
                        if(dad.getAlpha() >= dad.getBeta())
                            {
                        
                            dad.setCost(dad.getAlpha());
                            while(!stack.pop().equals(dad));
                            }
                    }
                    else
                    {
                        popped.setCost(popped.getBeta());
                        if(popped.getCost()>dad.getAlpha())
                        {
                            dad.setAlpha(popped.getCost());
                        
                        }
                        if(dad.getAlpha() >= dad.getBeta())
                            {
                        
                            dad.setCost(dad.getBeta());
                            while(!stack.pop().equals(dad));
                            }
                    }
                }
       }
                else
                {
                List<Node> neighborListOf = Graphs.neighborListOf(graph, popped);
                for(Node n : neighborListOf)
                {
                    if(popped.getCost()<n.getCost())
                    {
                        popped.setCost(n.getCost());
                    }
                }
                }
        }
                
        	return true;
        }

	@Override
	public void cleanup() {
	}
}