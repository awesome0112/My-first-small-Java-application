package Controller;

import Entity.Comment;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CommentController {
    @FXML
    private Label comment;

    @FXML
    private Label customerName;

    public void setData(Comment tmpComment) {
        comment.setText(tmpComment.getComment());
        customerName.setText(tmpComment.getUserName());
    }
}
