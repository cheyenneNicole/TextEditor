
package lab18;

public class FamilyText {
    private String familyText;
    private String text;

    public FamilyText(String familyText, String text) {
        this.familyText = familyText;
        this.text = text;
    }

    public String getFamilyText() {
        return familyText;
    }

    public void setFamilyText(String familyText) {
        this.familyText = familyText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
