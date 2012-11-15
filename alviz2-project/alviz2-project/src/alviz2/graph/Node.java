
package alviz2.graph;

import java.util.Set;
import java.util.HashSet;

import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

import alviz2.app.ColorPalette;

public class Node {

	private int id;
	private Point2D pos;
	Color fillColor;
	double cost;
	boolean visible;
        double alpha;
        double beta;
        boolean isMax;
        private int childNo;

    public boolean getIsMax() {
        return isMax;
    }

    public void setIsMax(boolean isMax) {
        this.isMax = isMax;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public int getChildNo() {
        return childNo;
    }

    public void setChildNo(int childNo) {
        this.childNo = childNo;
    }

    
        
        

	public static class PropChanger {
		private Set<Node> changedNodes;

		private PropChanger() {
			changedNodes = new HashSet<>();
		}

		public static PropChanger create() {
			return new PropChanger();
		}

		public void setFillColor(Node n, Color c) {
			changedNodes.add(n);
			n.setFillColor(c);
		}

		public void setVisible(Node n, boolean v) {
			changedNodes.add(n);
			n.setVisible(v);
		}

		public void setPosition(Node n, Point2D pt) {
			n.setPosition(pt);
		}

		public Set<Node> getChangedNodes() {
			return changedNodes;
		}

		public void clearChangedNodes() {
			changedNodes.clear();
		}
	}

	public Node(int id) {
		this.id = id;
		pos = new Point2D(0,0);
		fillColor = ColorPalette.getInstance().getColor("node.default");
		cost = 0;
		visible = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isVisible() {
		return visible;
	}

	private void setVisible(boolean v) {
		visible = v;
	}

	public final double getCost() {
		return cost;
	}

	public final void setCost(double c) {
		cost = c;
	}

	public final Color getFillColor() {
		return fillColor;
	}

	private void setFillColor(Color c) {
		fillColor = c;
	}

	public final Point2D getPosition() {
		return pos;
	}

	private void setPosition(Point2D pt) {
		pos = pt;
	}
	
}