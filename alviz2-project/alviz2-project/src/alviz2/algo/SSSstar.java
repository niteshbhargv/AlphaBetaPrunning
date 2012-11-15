
package alviz2.algo;

import alviz2.algo.misc.Descriptor;
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

public class SSSstar implements Algorithm<Node, Edge> {

	private Graph<Node, Edge> graph;
	private XYChart<Number, Number> chart;
	private Node.PropChanger npr;
	private Edge.PropChanger epr;
	private Node start;
	
	private Map<Node, Node> parents;
        
	private XYChart.Series<Number, Number> openListSeries;
	private XYChart.Series<Number, Number> closedListSeries;
	
	private ColorPalette palette;
        
        boolean flag = true;
        LinkedList<Descriptor> open;
        
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

	public SSSstar() {
		graph = null;
		chart = null;
		start = null;
		open = new LinkedList<>();
	
		parents = new HashMap<>();

		openListSeries = new XYChart.Series<>();
		closedListSeries = new XYChart.Series<>();
	
		palette = ColorPalette.getInstance();
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

                        }
                
                       
                        double random = Math.ceil((Math.random()*50));
                        List<Node> neighborListOf = Graphs.neighborListOf(this.graph, n);
                        if(neighborListOf.size() == 1)
                        n.setCost(random-25);
                        
		}

		/*for (Edge e : graph.edgeSet()) {
			epr.setVisible(e, true);
		}*/

		open.addFirst(new Descriptor(this.start,false,Double.POSITIVE_INFINITY));
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
            Descriptor remElement = open.removeFirst();
            npr.setFillColor(remElement.getId(),palette.getColor("node.default"));
            if(remElement.getId().equals(this.start) && remElement.isIsSolved())
            {
                this.start.setCost(remElement.gethValue());
                return false;
            }
            
            if(!remElement.isIsSolved())    //Removed Element is Live
            {
                
                if(Graphs.neighborListOf(graph, remElement.getId()).size() == 1)//Removed Element is Leaf
                {
                    System.out.println("Live Leaf1");
                    remElement.setIsSolved(true);
                    if(remElement.getId().getCost()>remElement.gethValue());
                    else
                        remElement.sethValue(remElement.getId().getCost());
                    Iterator<Descriptor> iterator = open.iterator();
                    int index =0;
                        while(iterator.hasNext())
                        {
                            if(iterator.next().gethValue()<remElement.gethValue() )
                            break;
                            ++index;
                        }
                        open.add(index, remElement);
                        npr.setFillColor(remElement.getId(),palette.getColor("node.closed"));
                        if(parents.containsKey(remElement.getId()) && parents.get(remElement.getId()) !=null)
                        epr.setStrokeColor(graph.getEdge(remElement.getId(),parents.get(remElement.getId() )), palette.getColor("node.closed"));
                        epr.setVisible(graph.getEdge(remElement.getId(),parents.get(remElement.getId() )), true);
                        System.out.println("Live Leaf");
                }
                else if(!remElement.getId().getIsMax())//Removed Element is Non Leaf Min
                {
                    System.out.println("Andar Aaaya");
                    List<Node> neighborListOf = Graphs.neighborListOf(graph,remElement.getId());
                    for(Node nn : neighborListOf)
                    {
                        if( parents.containsKey(remElement.getId())&& parents.get(remElement.getId())!=null &&parents.get(remElement.getId()).equals(nn))
                        {
                            continue;
                        }
                        else if(nn.getChildNo() == 1 )
                        {
                            System.out.println("Neighbor loop live non leaf min");
                            open.addFirst(new Descriptor(nn, false, remElement.gethValue()));
                            npr.setFillColor(nn, palette.getColor("node.open"));
                            epr.setStrokeColor(graph.getEdge(remElement.getId(),nn),palette.getColor("edge.open"));
                            epr.setVisible(graph.getEdge(remElement.getId(),nn), true);
                            parents.put(nn,remElement.getId());
                            break;
                        }
                    }
                    System.out.println("Live Non Leaf Min");
                }
                else //Non Leaf Max
                {
                    System.out.println("Neighbor loop live non leaf Max");
                    List<Node> neighborListOf = Graphs.neighborListOf(graph,remElement.getId());
                    int i=1;
                    for(Node nn : neighborListOf)
                    {
                        if(parents.containsKey(remElement.getId())&& parents.get(remElement.getId())!=null &&parents.get(remElement.getId()).equals(nn))
                        {
                            continue;
                        }
                        else
                        {
                            parents.put(nn,remElement.getId());
                            open.addFirst(new Descriptor(nn, false, remElement.gethValue()));
                            npr.setFillColor(nn, palette.getColor("node.open"));
                            epr.setStrokeColor(graph.getEdge(remElement.getId(),nn),palette.getColor("edge.open"));
                            epr.setVisible(graph.getEdge(remElement.getId(),nn), true);
                        }
                        
                    }
                    System.out.println("Live Non Leaf Max");
                }
            }
            else    //Solved 
            {
                if(remElement.getId().getIsMax()) //Max Element
                {
                    if(parents.containsKey(remElement.getId()))
                            {
                                    Node dad = parents.get(remElement.getId());
                                    if(remElement.getId().getChildNo()==2)
                                    {
                                        open.addFirst(new Descriptor(dad, true, remElement.gethValue()));
                                        npr.setFillColor(dad, palette.getColor("node.closed"));
                                        epr.setStrokeColor(graph.getEdge(remElement.getId(),dad),palette.getColor("edge.closed"));
                                        epr.setVisible(graph.getEdge(remElement.getId(),dad), true);
                                        
                                    }
                                    else
                                    {
                                    List<Node> neighborListOf = Graphs.neighborListOf(graph, dad);
                                        for(Node nn : neighborListOf)
                                        {
                                            if(parents.containsKey(dad)&& parents.get(dad)!=null &&parents.get(dad).equals(nn))
                                            {
                                                continue;
                                            }
                                            if(nn.getChildNo()== 2)
                                            {
                                                open.addFirst(new Descriptor(nn, false, remElement.gethValue()));
                                                npr.setFillColor(nn, palette.getColor("node.open"));
                                                epr.setStrokeColor(graph.getEdge(dad,nn),palette.getColor("edge.open"));
                                                epr.setVisible(graph.getEdge(dad,nn), true);
                                                parents.put(nn, dad);
                                            }
                                        }
                                
                                    }
                        
                }
                    System.out.println("Solved Max");
            }
              else  //Min Element
                {
                    Node dad = parents.get(remElement.getId());
                    open.addFirst(new Descriptor(dad, true, remElement.gethValue()));
                    npr.setFillColor(dad, palette.getColor("node.closed"));
                        epr.setStrokeColor(graph.getEdge(remElement.getId(),dad), palette.getColor("node.closed"));
                        epr.setVisible(graph.getEdge(remElement.getId(),dad), true);
                    System.out.println("Solved Min");
                }
            }
        
                
        	return true;
        }

	@Override
	public void cleanup() {
	}
}