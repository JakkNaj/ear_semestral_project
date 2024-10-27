package cz.cvut.kbss.ear.Helpdesk.dao;

import cz.cvut.kbss.ear.Helpdesk.dao.criteria.RequestCriteria;
import cz.cvut.kbss.ear.Helpdesk.exception.PersistenceException;
import cz.cvut.kbss.ear.Helpdesk.model.*;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.security.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class RequestDao extends BaseDao<Request>{
    public RequestDao() {
        super(Request.class);
    }

    public void persistTrackedTime(TrackedTime tt) {
        Objects.requireNonNull(tt);
        try {
            em.persist(tt);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public List<Request> findByType(DepartmentType type){
        try {
            return em.createNamedQuery("Request.findByType", Request.class)
                    .setParameter("type", type)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Request> findByPriority(RequestPriority prio){
        try {
            return em.createNamedQuery("Request.findByPriority", Request.class)
                    .setParameter("priority", prio)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Request> findByCustomer(Customer customer){
        try {
            return em.createNamedQuery("Request.findByCustomer", Request.class)
                    .setParameter("customer", customer)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Request> findByCustomerOpened(Customer customer){
        try {
            return em.createNamedQuery("Request.findByCustomerOpened", Request.class)
                    .setParameter("customer", customer)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Request> findUntilDeadline(LocalDateTime ldt) {
        try {
            return em.createQuery("select req from Request req where req.deadline < :localDateTime", Request.class)
                    .setParameter("localDateTime", ldt)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Request> findByState(RequestState state) {
        try {
            return em.createQuery("select req from Request req where req.state = :state", Request.class)
                    .setParameter("state", state)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Request> findByExactCriteria(RequestCriteria criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Request> query = builder.createQuery(Request.class);
        Root<Request> root = query.from(Request.class);

        List<Predicate> predicates = new ArrayList<>();

        addEqualsPredicate(builder, root.get("type"), criteria.getType(), predicates);
        addEqualsPredicate(builder, root.get("priority"), criteria.getPriority(), predicates);
        addEqualsPredicate(builder, root.get("state"), criteria.getState(), predicates);

        if (criteria.getCustomerEmail() != null) {
            Join<Request, Customer> customerJoin = root.join("customer");
            predicates.add(builder.equal(customerJoin.get("email"), criteria.getCustomerEmail()));
        }

        if (criteria.getDateCreated() != null) {
            LocalDateTime startOfDay = criteria.getDateCreated().toLocalDate().atStartOfDay();
            predicates.add(builder.lessThanOrEqualTo(root.get("dateCreated"), startOfDay));
        }

        query.where(predicates.toArray(new Predicate[0]));

        query.orderBy(builder.desc(root.get("dateCreated")));

        return em.createQuery(query).getResultList();
    }

    private <T> void addEqualsPredicate(CriteriaBuilder builder, Path<T> path, T value, List<Predicate> predicates) {
        if (value != null)
            predicates.add(builder.equal(path, value));
    }

    public void deleteAll() {
        em.createNativeQuery("DELETE FROM request").executeUpdate();
    }

}
