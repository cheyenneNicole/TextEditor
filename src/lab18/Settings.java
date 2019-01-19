
package lab18;

public class Settings {
        private String text;
    private double height;
    private double width;

    public Settings() {
        this.text = "Please type here";
        this.height = 600;
        this.width = 500;
    }

    public Settings(String text, double height, double width) {
        this.text = text;
        this.height = height;
        this.width = width;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "Settings{" + "text=" + text + ", height=" + height + ", width=" + width + '}';
    }
}
