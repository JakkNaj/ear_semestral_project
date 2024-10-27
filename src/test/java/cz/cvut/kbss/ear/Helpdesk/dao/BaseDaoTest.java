package cz.cvut.kbss.ear.Helpdesk.dao;

import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import cz.cvut.kbss.ear.Helpdesk.model.Comment;
import cz.cvut.kbss.ear.Helpdesk.exception.PersistenceException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackages = "cz.cvut.kbss.ear.Helpdesk.dao")
@ActiveProfiles("test")
public class BaseDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentDao commentDao;

    @Test
    public void persistSavesSpecifiedInstance() {
        final Comment comment = generateComment();
        commentDao.persist(comment);
        assertNotNull(comment.getId());

        final Comment result = em.find(Comment.class, comment.getId());
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
    }

    private static Comment generateComment() {
        final Comment comment = new Comment();
        comment.setText("Test Comment " + Generator.randomInt());
        comment.setDateCreated(LocalDateTime.now());
        return comment;
    }

    @Test
    public void findRetrievesInstanceByIdentifier() {
        final Comment comment = generateComment();
        em.persistAndFlush(comment);
        assertNotNull(comment.getId());

        final Comment result = commentDao.find(comment.getId());
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
    }

    @Test
    public void findAllRetrievesAllInstancesOfType() {
        final Comment commentOne = generateComment();
        em.persistAndFlush(commentOne);
        final Comment commentTwo = generateComment();
        em.persistAndFlush(commentTwo);

        final List<Comment> result = commentDao.findAll();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(commentOne.getId())));
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(commentTwo.getId())));
    }

    @Test
    public void updateUpdatesExistingInstance() {
        final Comment comment = generateComment();
        em.persistAndFlush(comment);

        final Comment update = new Comment();
        update.setId(comment.getId());
        final String newName = "New Comment Text" + Generator.randomInt();
        update.setText(newName);
        commentDao.update(update);

        final Comment result = commentDao.find(comment.getId());
        assertNotNull(result);
        assertEquals(comment.getText(), result.getText());
    }

    @Test
    public void removeRemovesSpecifiedInstance() {
        final Comment comment = generateComment();
        em.persistAndFlush(comment);
        assertNotNull(em.find(Comment.class, comment.getId()));
        em.detach(comment);

        commentDao.remove(comment);
        assertNull(em.find(Comment.class, comment.getId()));
    }

    @Test
    public void removeDoesNothingWhenInstanceDoesNotExist() {
        final Comment comment = generateComment();
        comment.setId(123);
        assertNull(em.find(Comment.class, comment.getId()));

        commentDao.remove(comment);
        assertNull(em.find(Comment.class, comment.getId()));
    }

    @Test
    public void exceptionOnPersistInWrappedInPersistenceException() {
        final Comment comment = generateComment();
        em.persistAndFlush(comment);
        em.remove(comment);
        assertThrows(PersistenceException.class, () -> commentDao.update(comment));
    }

    @Test
    public void existsReturnsTrueForExistingIdentifier() {
        final Comment comment = generateComment();
        em.persistAndFlush(comment);
        assertTrue(commentDao.exists(comment.getId()));
        assertFalse(commentDao.exists(-1));
    }
}
