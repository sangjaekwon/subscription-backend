package project.subscription.repository.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import project.subscription.dto.SubscriptionDto;
import project.subscription.dto.request.SubscriptionSearchCondition;
import project.subscription.dto.request.SubscriptionSortType;
import project.subscription.entity.User;
import project.subscription.repository.SubscriptionQueryRepository;

import java.time.LocalDate;
import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static project.subscription.entity.QSubscription.subscription;


@Repository
public class SubscriptionQueryRepositoryImpl implements SubscriptionQueryRepository {

    private final JPAQueryFactory queryFactory;

    public SubscriptionQueryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<SubscriptionDto> searchPageSubscriptionsByCondition(User user, SubscriptionSearchCondition condition,
                                                                    Pageable pageable) {
        List<SubscriptionDto> result = queryFactory
                .select(constructor(SubscriptionDto.class, subscription))
                .from(subscription)
                .where(subscription.user.eq(user), nameEq(condition.getSubscriptionName()))
                .orderBy(sortSub(condition.getSortType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(subscription.count())
                .from(subscription)
                .where(subscription.user.eq(user), (nameEq(condition.getSubscriptionName())));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }


    public Page<SubscriptionDto> findPageSubscriptions(User user, Pageable pageable) {
        List<SubscriptionDto> result = queryFactory
                .select(constructor(SubscriptionDto.class, subscription))
                .from(subscription)
                .where(subscription.user.eq(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(subscription.count())
                .where(subscription.user.eq(user))
                .from(subscription);

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);

    }

    public Page<SubscriptionDto> findPageSubscriptionsDueSoon(User user, LocalDate date, Pageable pageable) {
        List<SubscriptionDto> result = queryFactory
                .select(constructor(SubscriptionDto.class, subscription))
                .from(subscription)
                .where(subscription.user.eq(user), subscription.dday.loe(date))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(subscription.count())
                .from(subscription)
                .where(subscription.user.eq(user), subscription.dday.loe(date));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?> sortSub(SubscriptionSortType sortType) {
        if (sortType == null) {
            return null;
        }

        return switch (sortType) {
            case PRICE_DESC -> subscription.price.desc();
            case NAME_DESC -> subscription.name.desc();
            case DDAY_DESC -> subscription.dday.desc();
            case PRICE_ASC -> subscription.price.asc();
            case NAME_ASC -> subscription.name.asc();
            case DDAY_ASC -> subscription.dday.asc();
        };
    }

    private BooleanExpression nameEq(String name) {
        return StringUtils.hasText(name) ? subscription.name.eq(name) : null;
    }
}
