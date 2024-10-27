package cz.cvut.kbss.ear.Helpdesk.service;

import cz.cvut.kbss.ear.Helpdesk.dao.CommentDao;
import cz.cvut.kbss.ear.Helpdesk.dao.GenericDao;
import cz.cvut.kbss.ear.Helpdesk.dao.RequestDao;
import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.model.Comment;
import cz.cvut.kbss.ear.Helpdesk.model.Request;
import cz.cvut.kbss.ear.Helpdesk.model.RequestState;
import cz.cvut.kbss.ear.Helpdesk.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    private CommentService commentService;
    @Mock
    private CommentDao commentDao;

    private Generator generator = new Generator();

    @Mock
    private RequestDao requestDao;

    @BeforeEach
    public void init() {
        this.commentService = new CommentService(commentDao, requestDao);
    }

    @Test
    public void testCreateComment() {
        Comment c = new Comment();
        Request r = new Request();
        User author = generator.generateEmployee();
        r.setState(RequestState.NEW);
        String text = "Test text.";
        c.setAuthor(author);
        c.setText(text);
        c.setDateCreated(LocalDateTime.now());
        Comment comment = commentService.create(author, text, r);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentDao).persist(captor.capture());
        Assertions.assertEquals(text, comment.getText());
        Assertions.assertEquals(c.getText(), comment.getText());
        Assertions.assertEquals(c.getAuthor(), comment.getAuthor());
        Assertions.assertEquals(c.getId(), comment.getId());
    }

    @Test
    public void commentWithoutTextThrowsIllegalArgumentException() {
        Comment c = new Comment();
        Request r = new Request();
        User user = generator.generateEmployee();
        String text = "";
        c.setText(text);
        Assertions.assertThrows(IllegalArgumentException.class, () -> commentService.create(user, text, r));
    }
}
