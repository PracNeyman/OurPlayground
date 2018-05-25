package sample;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.util.Duration;
import sun.font.TrueTypeFont;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;

public class Controller {
    @FXML
    public Button start_btn;
    public Button select_data_btn;
    public ComboBox problem_type;
    public ComboBox regularition_rate;
    public Label epoch;
    public ComboBox learning_rate;
    public HBox param_bar;
    public ComboBox activation;
    public ComboBox regularition;
    public Pane canvas;
    public HBox label_bar;
    public HBox layer_bar;
    public VBox output_bar;
    public GridPane whole_pane;

    private InputLayer inputLayer = new InputLayer();
    private HiddenLayers hiddenLayers = new HiddenLayers();
    private ImageView outputLayer;
    private Group rects = new Group();

    private boolean useCirclePic = true;

    private Group lines = new Group();
    private int inputNum = 5;
    private List<Integer> hiddenNums = new ArrayList<>();       //记录每个隐藏层的神经元个数

    private ParallelTransition parallelTransition = new ParallelTransition();
    private boolean createLine = true;

    private double input_left;
    private double input_top;
    private double input_bottom;
    private double hidden_left;
    private double output_left;
    private double radius = 20;
    private double btn_height = 35;


    public void init(){
        input_left = 100;
        hidden_left = 100+100;
        input_top = canvas.getLayoutY();
        input_bottom = canvas.getLayoutY()+canvas.getPrefHeight();

        output_left = 100+100+280;

        hiddenNums.add(4);
        hiddenNums.add(5);
        hiddenNums.add(3);

        addChildrenToCanvas();

    }

    void addChildrenToCanvas(){
        rects.getChildren().clear();
        parallelTransition.getChildren().clear();

        getInput();
        getHidden();
        getOutput();


        canvas.getChildren().clear();
        canvas.getChildren().addAll(lines,rects, inputLayer.circleGroup,inputLayer.labelGroup,outputLayer);
        for(Group group:hiddenLayers.hiddenAddOrSub){
            canvas.getChildren().add(group);
        }
        for(Group group:hiddenLayers.hiddenList){
            canvas.getChildren().add(group);
        }
    }

    void getInput(){
        int num = inputNum;
        Group circleGroup = new Group();
        double hspace = (hidden_left - input_left - 2 * radius)/2;
        double vspace = (input_bottom - input_top - num * 2*radius) / (1+num);

        String[] inputNames = {"X1","X2","X1*X2","sin(X1)","sin(X2)"};
        for(int i=0;i<num;i++){
            Circle circle = new Circle(radius);
            circle.setFill(new ImagePattern(new Image(Main.class.getResourceAsStream("I.png"))));
            circle.setLayoutX(input_left + hspace);
            circle.setLayoutY(input_top+(vspace+2*radius)*i+vspace+btn_height);
            circleGroup.getChildren().add(circle);
        }
        Group labelGroup = new Group();
        for(int i=0;i<num;i++){
            Label label = new Label(inputNames[i%num]);
            label.setLayoutY(circleGroup.getChildren().get(i).getLayoutY()+radius*1/2-btn_height/2);
            label.setLayoutX(input_left-hspace*2.8);
            labelGroup.getChildren().add(label);
        }
        inputLayer.circleGroup = circleGroup;
        inputLayer.labelGroup = labelGroup;
    }

    //每次变化隐藏层都要执行此函数，传入的参数是每层隐藏层的神经单元个数
    void getHidden(){
        List<Group> circleGroup = hiddenLayers.hiddenList;
        List<Group> btnGroup = hiddenLayers.hiddenAddOrSub;

        //重新调整位置
        circleGroup.clear();
        btnGroup.clear();
        lines.getChildren().clear();
        hiddenLayers.hspace = (output_left-hidden_left - 2* radius)/(hiddenNums.size());

        for(int i=0;i<hiddenNums.size();i++){
            Button addBtn = new Button("+");
            Button subBtn = new Button("-");
            addBtn.setLayoutX(hidden_left + i * (hiddenLayers.hspace + 2 * radius));
            addBtn.setLayoutY(-18);
            subBtn.setLayoutX(hidden_left + i * (hiddenLayers.hspace + 2 * radius) + hiddenLayers.hspace/2);
            subBtn.setLayoutY(-18);

            int finalI = i;
            subBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("点击的是"+finalI);
                    if(hiddenNums.get(finalI)==1)
                        return;
                    hiddenNums.set(finalI,hiddenNums.get(finalI)-1);
                    addChildrenToCanvas();
                }
            });

            addBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(hiddenNums.get(finalI)==5)
                        return;
                    hiddenNums.set(finalI,hiddenNums.get(finalI)+1);
                    addChildrenToCanvas();
                }
            });

            Group columnBtnGroup = new Group();
            columnBtnGroup.getChildren().addAll(addBtn,subBtn);
            btnGroup.add(columnBtnGroup);
        }
//        String[] picNames = {"0.png","1.png","2.png","3.png","4.png"};
        String[] picNames = {"G","L","D","N","L"};
        int picIndex = -1;

        for(int i=0;i<hiddenNums.size();i++){
            picIndex = (picIndex+1)%5;
            int num = hiddenNums.get(i);
            double vspace = (input_bottom - input_top - radius*2*num) / num;
            Group curColumn = new Group();
            String colorStr = MyUtil.getRandomColor();
            for(int j = 0;j<num;j++) {
                Circle circle = new Circle(radius);

                if(useCirclePic) {
                    circle.setFill(Color.valueOf(colorStr));
                    circle.setFill(new ImagePattern(new Image(Main.class.getResourceAsStream(picNames[picIndex]+".png"))));
                    RotateTransition rt1;
                    RotateTransition rt2;
                    rt1 = new RotateTransition(Duration.millis(500), circle);
                    rt1.setByAngle(360f);
                    rt1.setCycleCount(1);
                    rt1.setAutoReverse(true);
                    rt2 = new RotateTransition(Duration.millis(500), circle);
                    rt2.setToAngle(0f);
                    rt2.setCycleCount(1);
                    rt2.setAutoReverse(true);
                    circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            rt2.stop();
                            rt1.play();
                        }
                    });
                    circle.setOnMouseExited(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            rt1.stop();
                            rt2.play();
                        }
                    });
                }
                else {
                circle.setFill(Color.valueOf(colorStr));
                }
                circle.setLayoutX(hidden_left + i * (hiddenLayers.hspace + 2 * radius) + hiddenLayers.hspace/2);
                circle.setLayoutY(input_top + j * (vspace + 2 * radius)+vspace/2+btn_height);
                curColumn.getChildren().add(circle);
            }
            circleGroup.add(curColumn);
            //如果当前是第一层隐藏层后的其他层，那么将这层与前一层相连
            if(i>0){
                addLineBtw2Layer(circleGroup.get(circleGroup.size()-2),curColumn);
            }
        }
        //如果当前已经添加了隐藏层，那么取出第一层隐藏层的每一个神经元与输入层进行贝塞尔曲线连接
        if(!circleGroup.isEmpty())
            addLineBtw2Layer(inputLayer.circleGroup, circleGroup.get(0));
    }

    void getOutput(){
        Image image = new Image(getClass().getResourceAsStream("image.jpg"));
        outputLayer = new ImageView(image);
        outputLayer.setLayoutX(output_left+280);
        outputLayer.setLayoutY(0);
        outputLayer.setFitWidth(100);
        outputLayer.setFitHeight(100);
        if(hiddenNums.isEmpty()){
            addLineBtwLayerAndOutput(inputLayer.circleGroup);
        }else{
            addLineBtwLayerAndOutput(hiddenLayers.hiddenList.get(hiddenLayers.hiddenList.size()-1));
        }
    }

    void addLineBtwLayerAndOutput(Group group1){

        String curColor = MyUtil.getRandomColor();
        for (int i = 0; i < group1.getChildren().size(); i++) {
            Circle inputCircle = (Circle) group1.getChildren().get(i);
            CubicCurve cubicCurve = new CubicCurve();
            cubicCurve.getStrokeDashArray().addAll(30d, 20d);
            cubicCurve.setStartX(inputCircle.getLayoutX());
            cubicCurve.setStartY(inputCircle.getLayoutY());
            cubicCurve.setControlX1((inputCircle.getLayoutX()+outputLayer.getLayoutX())*5/11);
            cubicCurve.setControlY1((inputCircle.getLayoutY()+outputLayer.getLayoutY())*5/11);
            cubicCurve.setControlX2((inputCircle.getLayoutX()+outputLayer.getLayoutX())*6/11);
            cubicCurve.setControlY2((inputCircle.getLayoutY()+outputLayer.getLayoutY())*6/11);
            cubicCurve.setEndX(outputLayer.getLayoutX()+outputLayer.getFitWidth()/2);
            cubicCurve.setEndY(outputLayer.getLayoutY()+outputLayer.getFitHeight()/2);

            cubicCurve.setStroke(Color.valueOf(curColor));
            cubicCurve.setStrokeWidth(2);
            cubicCurve.setStrokeLineCap(StrokeLineCap.ROUND);
            cubicCurve.setFill(Color.CORNSILK.deriveColor(0, 0, 1, 0));
            if(createLine)
                lines.getChildren().add(cubicCurve);
            double len = Math.sqrt(Math.pow(cubicCurve.getEndY()-cubicCurve.getStartY(),2)+Math.pow(cubicCurve.getEndX()-cubicCurve.getStartX(),2));
            //len略小于线的总长度
            double rectWidth = 20;
            for(int j = 0;j<len/ (0.9*rectWidth);j++) {
                Rectangle rectangle = new Rectangle(inputCircle.getLayoutX(), inputCircle.getLayoutY(), rectWidth, 3);
                rectangle.setArcHeight(0.5);
                rectangle.setArcWidth(0.5);
                rectangle.setFill(Color.valueOf(curColor));
                Path path = new Path();
                path.getElements().add(new MoveTo(cubicCurve.getStartX(), cubicCurve.getStartY()));
                path.getElements().add(new CubicCurveTo(cubicCurve.getControlX1(), cubicCurve.getControlY1(), cubicCurve.getControlX2(), cubicCurve.getControlY2(), cubicCurve.getEndX(), cubicCurve.getEndY()));
                PathTransition pathTransition = new PathTransition();
                pathTransition.setDuration(Duration.millis(4000));
                pathTransition.setPath(path);
                pathTransition.setNode(rectangle);
                pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                pathTransition.setCycleCount(Timeline.INDEFINITE);
                pathTransition.setAutoReverse(false);
                pathTransition.setDelay(Duration.millis(200*j));
                rects.getChildren().add(rectangle);
                parallelTransition.getChildren().add(pathTransition);
            }
        }
    }

    //在两层之间添加贝塞尔曲线
    void addLineBtw2Layer(Group group1, Group group2){
        for (int i = 0; i < group1.getChildren().size(); i++) {
            Circle inputCircle = (Circle) group1.getChildren().get(i);
            String curColor = MyUtil.getRandomColor();
            for (int j = 0; j < group2.getChildren().size();j++){
                Circle hiddenCircle = (Circle) group2.getChildren().get(j);
                CubicCurve cubicCurve = new CubicCurve();
                cubicCurve.getStrokeDashArray().addAll(25d, 10d);
                cubicCurve.setStartX(inputCircle.getLayoutX());
                cubicCurve.setStartY(inputCircle.getLayoutY());
                cubicCurve.setControlX1((inputCircle.getLayoutX()+hiddenCircle.getLayoutX())*7/15);
                cubicCurve.setControlY1((inputCircle.getLayoutY()+hiddenCircle.getLayoutY())*7/15);
                cubicCurve.setControlX2((inputCircle.getLayoutX()+hiddenCircle.getLayoutX())*8/15);
                cubicCurve.setControlY2((inputCircle.getLayoutY()+hiddenCircle.getLayoutY())*8/15);
//                cubicCurve.setControlX1(inputCircle.getLayoutX());
//                cubicCurve.setControlY1(inputCircle.getLayoutY());
//                cubicCurve.setControlX2(inputCircle.getLayoutX());
//                cubicCurve.setControlY2(inputCircle.getLayoutY());
                cubicCurve.setEndX(hiddenCircle.getLayoutX());
                cubicCurve.setEndY(hiddenCircle.getLayoutY());

                cubicCurve.setStroke(Color.valueOf(curColor));
                cubicCurve.setStrokeWidth(2);
                cubicCurve.setStrokeLineCap(StrokeLineCap.ROUND);
                cubicCurve.setFill(Color.CORNSILK.deriveColor(0, 0, 1, 0));
                if(createLine)
                    lines.getChildren().add(cubicCurve);
                double len = Math.sqrt(Math.pow(cubicCurve.getEndY()-cubicCurve.getStartY(),2)+Math.pow(cubicCurve.getEndX()-cubicCurve.getStartX(),2));
                double rectWidth = 15;
                for(int k = 0;k<len/ (1.2*rectWidth);k++) {
                    Rectangle rectangle = new Rectangle(inputCircle.getLayoutX(), inputCircle.getLayoutY(), rectWidth, 3);
                    rectangle.setArcHeight(0.5);
                    rectangle.setArcWidth(0.5);
                    rectangle.setFill(Color.valueOf(curColor));
                    Path path = new Path();
                    path.getElements().add(new MoveTo(cubicCurve.getStartX(), cubicCurve.getStartY()));
                    path.getElements().add(new CubicCurveTo(cubicCurve.getControlX1(), cubicCurve.getControlY1(), cubicCurve.getControlX2(), cubicCurve.getControlY2(), cubicCurve.getEndX(), cubicCurve.getEndY()));
                    PathTransition pathTransition = new PathTransition();
                    pathTransition.setDuration(Duration.millis(3500));
                    pathTransition.setPath(path);
                    pathTransition.setNode(rectangle);
                    pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                    pathTransition.setCycleCount(Timeline.INDEFINITE);
                    pathTransition.setAutoReverse(false);
                    pathTransition.setDelay(Duration.millis(k*280));
                    rects.getChildren().add(rectangle);
                    parallelTransition.getChildren().add(pathTransition);
                }
            }
        }
    }

    public void select_btn(ActionEvent actionEvent) {
    }

    public void start(ActionEvent actionEvent) {
        createLine = false;
        parallelTransition.stop();
        parallelTransition.getChildren().clear();
        addChildrenToCanvas();
        System.out.println(parallelTransition.getChildren().size());
        parallelTransition.setCycleCount(Timeline.INDEFINITE);
        parallelTransition.play();
        createLine = true;
    }

    public void subLayer(ActionEvent actionEvent) {
        if(hiddenNums.size()==0)
            return;
        hiddenNums.remove(hiddenNums.size()-1);

        addChildrenToCanvas();
    }

    public void addLayer(ActionEvent actionEvent) {
        if(hiddenNums.size()*2*radius>output_left-hidden_left)
            return;
        hiddenNums.add(new Random().nextInt(4)+1);

        addChildrenToCanvas();
    }
}
class HiddenLayers extends Control{
    double hspace;
    List<Group> hiddenList = new ArrayList<>();
    List<Group> hiddenAddOrSub = new ArrayList<>();
}

class InputLayer extends Control{
    Group labelGroup = new Group();
    Group circleGroup = new Group();
}