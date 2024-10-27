package cz.cvut.kbss.ear.Helpdesk.service;

import cz.cvut.kbss.ear.Helpdesk.dao.CommentDao;
import cz.cvut.kbss.ear.Helpdesk.dao.RequestDao;
import cz.cvut.kbss.ear.Helpdesk.exception.InvalidStateException;
import cz.cvut.kbss.ear.Helpdesk.model.Comment;
import cz.cvut.kbss.ear.Helpdesk.model.Request;
import cz.cvut.kbss.ear.Helpdesk.model.RequestState;
import cz.cvut.kbss.ear.Helpdesk.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


@Service
public class CommentService {

    final CommentDao commentDao;

    private final RequestDao requestDao;

    @Autowired
    public CommentService(CommentDao commentDao, RequestDao requestDao) {
        this.commentDao = commentDao;
        this.requestDao = requestDao;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_COMMON', 'ROLE_CUSTOMER', 'ROLE_MANAGER')")
    public Comment create(User user, String text, Request request) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(text);
        Objects.requireNonNull(request);
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(user);
        comment.setDateCreated(LocalDateTime.now());
        if (comment.getText().isEmpty())
            throw new IllegalArgumentException("Comment text cannot be empty");
        if (request.getState() == RequestState.CLOSED)
            throw new InvalidStateException("Cannot comment on closed request");
        request.addComment(comment);
        commentDao.persist(comment);
        requestDao.update(request);
        return comment;
    }
}

