package ugmaybachengine;

public class LevelScene extends Scene{

    public LevelScene(){
        System.out.println("Inside leve scene");
        Window.get().setR(1);
        Window.get().setG(1);
        Window.get().setB(1);
        //Window.get().setA(1);
    }

    @Override
    public void update(float dt) {

    }
}
