//<editor-fold defaultstate="collapsed" desc="imports">
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Random;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
//</editor-fold>

public class Main extends Application {

    //<editor-fold defaultstate="collapsed" desc="variables">
    private Stage stage;

    Double strokeWidth = null;

    private Pane workspace;
    private GridPane controls;
    private ColorPicker cpShapeFill;
    private ColorPicker cpStrokeFill;
    private Slider slSize;
    private Slider slAmountCopies;
    private ComboBox<String> cbShapeType = setComboBox();
    private Label coordinates;
    private Paint savedColor;
    private Image pattern = null;
    private final Button exitBtn = new Button("Exit");
    private final Button clearBtn = new Button("Clear");
    private final Button loadBtn = new Button("Load image");
    private final CheckBox chbUseImage = new CheckBox("Use image?");
    private final CheckBox chbSetStroke = new CheckBox("Set Stroke?");
    private final CheckBox chbCreateCopies = new CheckBox("Create Copies?");
    private final Label shapeFillLBL = new Label("Shape Fill");
    private final Label shapeSizeLBL = new Label("Shape Size");
    private final Label shapeTypeLBL = new Label("Shape Type");
    private final Label amountOfCopiesLBL = new Label("Amount of copies");
    private final TextField strokeWidthtf = new TextField();
//</editor-fold>

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        VBox root = new VBox();
        cpShapeFill = new ColorPicker(Color.RED);
        cpStrokeFill = new ColorPicker(Color.BLUE);
        slSize = new Slider(0, 50, 10);
        slAmountCopies = new Slider(1, 8, 3);
        createWorkspace();
        createControls();
        setSliderSize();
        setSliderAmountCopies();
        setExitBtn();
        setLoadBtn();
        setClearBtn();
        strokeWidthtf.setPromptText("Stroke Width");

        root.getChildren().addAll(workspace, controls);
        VBox.setVgrow(workspace, Priority.ALWAYS);
        Scene scene = new Scene(root);

        scene.widthProperty().addListener(obs -> {
            workspace.getChildren().clear();
            workspace.getChildren().add(coordinates);
        });

        scene.heightProperty().addListener(obs -> {
            workspace.getChildren().clear();
            workspace.getChildren().add(coordinates);
        });

        workspace.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            if (workspace.getChildren().size() <= 1) {
                clearBtn.setDisable(true);
            } else {
                clearBtn.setDisable(false);
            }
        });

        stage.setOnCloseRequest(we -> {
            closeRequest(we);
        });
        stage.setScene(scene);
        stage.show();
    }

    private Pane createWorkspace() {
        workspace = new Pane();
        workspace.setPrefSize(1200, 600);
        workspace.setBackground(new Background(new BackgroundFill(Paint.valueOf("C0FFC0"), CornerRadii.EMPTY, Insets.EMPTY)));
        setCoordinates();
        workspace.setOnMouseClicked(this::handleMouseClickedOnPane);
        workspace.setOnMouseMoved(this::updateCoordinates);
        workspace.setOnMouseDragged(this::updateCoordinates);
        workspace.getChildren().add(coordinates);
        return workspace;
    }

    private GridPane createControls() {
        controls = new GridPane();
        controls.setPrefHeight(120);
        controls.setBackground(new Background(new BackgroundFill(Paint.valueOf("FFFFB4"), CornerRadii.EMPTY, Insets.EMPTY)));
        controls.setHgap(15);

        controls.add(exitBtn, 0, 0);
        controls.add(clearBtn, 0, 1);
        controls.add(loadBtn, 1, 0);
        controls.add(chbUseImage, 1, 1);
        controls.add(chbSetStroke, 2, 0);
        controls.add(cpStrokeFill, 2, 1);
        controls.add(strokeWidthtf, 2, 2);
        controls.add(shapeSizeLBL, 3, 0);
        controls.add(slSize, 3, 1);
        controls.add(chbCreateCopies, 3, 2);
        controls.add(shapeFillLBL, 4, 0);
        controls.add(cpShapeFill, 4, 1);
        controls.add(amountOfCopiesLBL, 4, 2);
        controls.add(shapeTypeLBL, 5, 0);
        controls.add(cbShapeType, 5, 1);
        controls.add(slAmountCopies, 5, 2);

        controls.getChildren().forEach(item -> GridPane.setHalignment(item, HPos.CENTER));

        controls.setAlignment(Pos.CENTER);
        return controls;
    }

    private void setSliderSize() {
        slSize.setShowTickLabels(true);
        slSize.setShowTickMarks(true);
        slSize.setSnapToTicks(true);
        slSize.setBlockIncrement(10);
        slSize.setMajorTickUnit(10);
        slSize.setMinorTickCount(9);
        slSize.setPrefWidth(200);
    }

    private void setSliderAmountCopies() {
        slAmountCopies.setShowTickLabels(true);
        slAmountCopies.setShowTickMarks(true);
        slAmountCopies.setSnapToTicks(true);
        slAmountCopies.setBlockIncrement(1);
        slAmountCopies.setMajorTickUnit(1);
        slAmountCopies.setPrefWidth(200);
    }

    private ComboBox<String> setComboBox() {
        cbShapeType = new ComboBox(FXCollections.observableArrayList("Circle", "Rectangle", "Polygon"));
        cbShapeType.setValue("Circle");
        return cbShapeType;
    }

    private void setCoordinates() {
        coordinates = new Label("[0000 : 0000]");
    }

    private void updateCoordinates(MouseEvent t) {
        coordinates.setText(String.format("[%04.0f : %04.0f]", t.getSceneX(), t.getSceneY()));
    }

    private void handleMouseClickedOnPane(MouseEvent t) {
        if (t.getButton().equals(MouseButton.PRIMARY) && chbCreateCopies.isSelected()) {
            Random random = new Random();
            for (int i = 0; i < slAmountCopies.getValue(); i++) {
                double randomX = t.getX() + random.nextDouble() * 200 - 100;
                double randomY = t.getY() + random.nextDouble() * 200 - 100;
                Shape shape = createShapeBasedOnComboBox(randomX, randomY, slSize.getValue(), cpShapeFill.getValue());
                createEventHandlers(shape);
            }
        } else if (t.getButton() == MouseButton.PRIMARY) {
            handleDraw(t);
        }

    }

    private void handleDraw(MouseEvent t) {
        Shape shape = createShapeBasedOnComboBox(t.getX(), t.getY(), slSize.getValue(), cpShapeFill.getValue());
        createEventHandlers(shape);
    }

    private void createEventHandlers(Shape shape) {
        shape.setOnMouseEntered(e -> handlerMouseEntered(shape));
        shape.setOnMouseExited(e -> handlerMouseExited(shape));
        shape.setOnMouseClicked(e -> handlerMouseClicked(e, shape));
    }

    private void handlerMouseEntered(Shape shape) {
        savedColor = shape.getFill();
        shape.setFill(Color.BLACK);
    }

    private void handlerMouseExited(Shape shape) {
        shape.setFill(savedColor);
    }

    private void handlerMouseClicked(MouseEvent e, Shape shape) {
        if (e.getButton() == MouseButton.SECONDARY) {
            deleteShape(shape);
        }
    }

    private void deleteShape(Shape shape) {
        workspace.getChildren().remove(shape);
    }

    private Shape createShapeBasedOnComboBox(double x, double y, double value, Color value0) {
        Shape shape = null;
        switch (cbShapeType.getValue()) {
            case "Circle":
                shape = drawCircle(x, y, value, value0);
                break;
            case "Rectangle":
                shape = drawRectangle(x, y, value, value0);
                break;
            case "Polygon":
                shape = drawPolygon(x, y, value, value0);
                break;

        }
        if (pattern != null && chbUseImage.isSelected() && shape != null) {
            shape.setFill(new ImagePattern(pattern));
        }
        return shape;
    }

    private Shape drawCircle(double x, double y, double value, Color value0) {
        Circle circle = new Circle(x, y, value, value0);
        setStrokeFill(circle);
        return circle;
    }

    private Shape drawRectangle(double x, double y, double value, Color value0) {
        Rectangle rectangle = new Rectangle(x - value / 2, y - value / 2, value, value);
        rectangle.setFill(value0);
        setStrokeFill(rectangle);
        return rectangle;
    }

    private Shape drawPolygon(double x, double y, double value, Color value0) {
        Polygon polygon = new Polygon(x - value / 2, y, x - value / 4, y - value / 2, x + value /
                4, y - value / 2, x + value / 2, y, x + value / 4, y + value / 2, x - value / 4, y + value / 2);
        polygon.setFill(value0);
        setStrokeFill(polygon);
        return polygon;
    }

    private void closeRequest(WindowEvent we) {
        Alert closeAlert = new Alert(AlertType.CONFIRMATION);
        closeAlert.setTitle("Close??");
        closeAlert.setContentText("Are you sure you wanna close?");
        Optional<ButtonType> result = closeAlert.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            Platform.exit();
        } else {
            we.consume();
        }
    }

    private void setExitBtn() {
        exitBtn.setOnAction(e -> {
            closeRequest(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        exitBtn.setPrefWidth(50);
    }

    private void setLoadBtn() {
        loadBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg")
            );

            File file = fc.showOpenDialog(stage);

            if (file != null) {
                try {
                    pattern = new Image(new FileInputStream(file));
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setContentText("The file was successfully loaded");
                    alert.show();
                } catch (FileNotFoundException ex) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("An error occurred while loading the file \n Plese select the correct file");
                    alert.show();
                }
            }
        });
    }

    private void setClearBtn() {
        clearBtn.setOnAction(e -> {
            workspace.getChildren().clear();
            workspace.getChildren().add(coordinates);
        });
        clearBtn.setDisable(true);
    }

    private void setStrokeFill(Shape shape) {
        if (chbSetStroke.isSelected()) {
            try {
                strokeWidth = Double.valueOf(strokeWidthtf.getText());

                if (strokeWidth < 1 || strokeWidth > 30) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Please enter a number within <1, 30>");
                    alert.showAndWait();
                } else {
                    shape.setStroke(cpStrokeFill.getValue());
                    shape.setStrokeWidth(strokeWidth);
                    workspace.getChildren().add(shape);
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Enter a number");
                alert.showAndWait();
            }
        } else {
            workspace.getChildren().add(shape);
        }
    }

}
