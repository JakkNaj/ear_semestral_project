package cz.cvut.kbss.ear.Helpdesk.dao;

import cz.cvut.kbss.ear.Helpdesk.model.Comment;
import cz.cvut.kbss.ear.Helpdesk.model.Customer;
import cz.cvut.kbss.ear.Helpdesk.model.Request;
import cz.cvut.kbss.ear.Helpdesk.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentDao extends BaseDao<Comment>{

    protected CommentDao() {
        super(Comment.class);
    }

    public List<Comment> findByUser(User user){
        try {
            return em.createNamedQuery("Comment.findByCustomer", Comment.class)
                    .setParameter("author", user)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
