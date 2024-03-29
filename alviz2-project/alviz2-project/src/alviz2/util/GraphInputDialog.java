
package alviz2.util;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.stage.StageBuilder;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.geometry.Point2D;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;

import java.util.Set;
import java.util.HashSet;

import org.jgrapht.Graph;

import alviz2.graph.Node;
import alviz2.graph.Edge;

public class GraphInputDialog {

	private Graph<Node, Edge> graph;
	private PointIndex2D ptIdx;
	double maxX, maxY;
	double canvasWidth, canvasHeight;

	public GraphInputDialog(Graph<Node, Edge> graph, PointIndex2D idx) {
		this.graph = graph;
		ptIdx = idx;

		maxX = Double.MIN_VALUE;
		maxY = Double.MIN_VALUE;

		for (Node n: this.graph.vertexSet()) {
			Point2D p = n.getPosition();
			maxX = maxX > p.getX() ? maxX : p.getX();
			maxY = maxY > p.getY() ? maxY : p.getY();
		}
		canvasHeight = Screen.getPrimary().getVisualBounds().getHeight() * 0.7;
		canvasWidth = Screen.getPrimary().getVisualBounds().getWidth() * 0.7;
	}

	public Set<Node> getNodes(String title, String query, final Set<Node> blackList, final boolean multiSelect) {
		final Set<Node> selList = new HashSet<Node>();
		final double xFactor = canvasWidth/(maxX+10);
		final double yFactor = canvasHeight/(maxY+10);
		final Canvas c = new Canvas(canvasWidth, canvasHeight);

		GraphicsContext gc = c.getGraphicsContext2D();
		gc.clearRect(0, 0, canvasWidth, canvasHeight);
		gc.setFill(Color.DARKGRAY);
		gc.setStroke(Color.DARKGRAY);

		for (Edge e: graph.edgeSet()) {
			Point2D ps = e.getPositionS();
			Point2D pd = e.getPositionD();
			Point2D nps = new Point2D(ps.getX(), ps.getY());
			Point2D npd = new Point2D(pd.getX(), pd.getY());
			gc.strokeLine(ps.getX() * xFactor, ps.getY() * yFactor, npd.getX() * xFactor, npd.getY() * yFactor);
		}

		for (Node n: graph.vertexSet()) {
			Point2D p = n.getPosition();
			gc.fillOval(p.getX() * xFactor, p.getY() * yFactor, 5, 5);
		}

		gc.setFill(Color.BLACK);
		for (Node n : blackList) {
			Point2D p = n.getPosition();
			gc.fillOval(p.getX() * xFactor, p.getY() * yFactor, 5, 5);
		}

		GridPane root = GridPaneBuilder.create()
									   .hgap(10)
									   .vgap(10)
									   .prefHeight(Region.USE_COMPUTED_SIZE)
									   .prefWidth(Region.USE_COMPUTED_SIZE)
									   .build();
		Scene sc = new Scene(root);
		final Stage modalDlg = StageBuilder.create()
									 .title(title)
									 .resizable(false)
									 .style(StageStyle.UTILITY)
									 .build();
		modalDlg.initModality(Modality.APPLICATION_MODAL);
		modalDlg.setScene(sc);

		root.add(new Label(query), 1, 1);
		root.add(c, 1, 2);
		Button bt = new Button("Cancel");
		bt.setCancelButton(true);
		bt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				selList.clear();
				modalDlg.close();
			}
		});
		root.add(bt, 2, 2);
		root.setConstraints(bt, 2, 2, 1, 1, HPos.LEFT, VPos.TOP);
		bt = new Button("Ok");
		bt.setDefaultButton(true);
		bt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				modalDlg.close();
			}
		});
		root.add(bt, 2, 1);

		c.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent evt) {
				if (evt.getButton() == MouseButton.SECONDARY) {
					GraphicsContext gc = c.getGraphicsContext2D();
					gc.setFill(Color.BLACK);
					for (Node n : selList) {
						gc.fillOval(n.getPosition().getX() * xFactor, n.getPosition().getY() * yFactor, 5, 5);
					}

					selList.clear();
					return;
				}

				double sx = evt.getX() / xFactor;
				double sy = evt.getY() / yFactor;
				Point2D q = new Point2D(sx, sy);
				Node nr = ptIdx.nearest(q);
				Point2D pt = nr.getPosition();
				if (pt.distance(q) > 5 || blackList.contains(nr)) {
					return;
				}

				GraphicsContext gc = c.getGraphicsContext2D();
				if (evt.isControlDown() && multiSelect) {
					selList.add(nr);
				}
				else {
					gc.setFill(Color.DARKGRAY);
					for (Node n : selList) {
						gc.fillOval(n.getPosition().getX() * xFactor, n.getPosition().getY() * yFactor, 5, 5);
					}

					selList.clear();
					selList.add(nr);
				}

				gc.setFill(Color.BLUE);
				gc.fillOval(pt.getX() * xFactor, pt.getY() * yFactor, 5, 5);
			}
		});

		modalDlg.showAndWait();

		return selList;
	}
}