package com.goods2go.repositories;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.goods2go.models.ShipmentSize;
import com.goods2go.models.ShipmentSubscription;

public class ShipmentSubscriptionSpecs {
	
	public static Specification<ShipmentSubscription> idIn(List<Long> ids) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return root.get("id").in(ids);
            }
        };
    }
	
	public static Specification<ShipmentSubscription> sizeIn(Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return root.get("maxsize").in(sss);
            }
        };
    }
	
	public static Specification<ShipmentSubscription> sizeIsNotEmpty() {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.isNotNull(root.get("maxsize"));
            }
        };
    }
	
	public static Specification<ShipmentSubscription> sizeIsEmpty() {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.isNull(root.get("maxsize"));
            }
        };
    }
	
	public static Specification<ShipmentSubscription> laterOrEqualsThanPickupFromDate(Date from) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.greaterThanOrEqualTo(root.get("pickupfrom"), from);
            }
        };
    }
	
	public static Specification<ShipmentSubscription> beforeOrEqualsLDeliverUntilDate(Date to) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.lessThanOrEqualTo(root.get("deliveruntil"), to);
            }
        };
    }
	
	public static Specification<ShipmentSubscription> pickupFromDateIsEmpty() {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.isNull(root.get("pickupfrom"));
            }
        };
    }
	
	public static Specification<ShipmentSubscription> pickupFromDateIsNotEmpty() {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.isNotNull(root.get("pickupfrom"));
            }
        };
    }
	
	public static Specification<ShipmentSubscription> deliverUntilDateIsEmpty() {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.isNull(root.get("deliveruntil"));
            }
        };
    }
	
	public static Specification<ShipmentSubscription> deliverUntilDateIsNotEmpty() {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.isNotNull(root.get("deliveruntil"));
            }
        };
    }
	
	public static Specification<ShipmentSubscription> allGiven(List<Long> ids, Date from, Date toPick, Date to, Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.and(
            			root.get("id").in(ids),
            			builder.isNotNull(root.get("maxsize")),
            			builder.isNotNull(root.get("pickupfrom")),
            			builder.isNotNull(root.get("deliveruntil")),
            			root.get("maxsize").in(sss),
            			builder.greaterThanOrEqualTo(root.get("pickupfrom"), from),
            			builder.lessThanOrEqualTo(root.get("pickupfrom"), toPick),
            			builder.lessThanOrEqualTo(root.get("deliveruntil"), to));
            	
            }
        };
    }
	
	public static Specification<ShipmentSubscription> allGivenButSizes(List<Long> ids, Date from, Date toPick, Date to) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.and(
            			root.get("id").in(ids),
            			builder.isNull(root.get("maxsize")),
            			builder.isNotNull(root.get("pickupfrom")),
            			builder.isNotNull(root.get("deliveruntil")),
            			builder.greaterThanOrEqualTo(root.get("pickupfrom"), from),
            			builder.lessThanOrEqualTo(root.get("pickupfrom"), toPick),
            			builder.lessThanOrEqualTo(root.get("deliveruntil"), to));
            	
            }
        };
    }
	
	public static Specification<ShipmentSubscription> allGivenButUntilDate(List<Long> ids, Date from, Date toPick, Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.and(
            			root.get("id").in(ids),
            			builder.isNotNull(root.get("maxsize")),
            			builder.isNotNull(root.get("pickupfrom")),
            			builder.isNull(root.get("deliveruntil")),
            			root.get("maxsize").in(sss),
            			builder.lessThanOrEqualTo(root.get("pickupfrom"), toPick),
            			builder.greaterThanOrEqualTo(root.get("pickupfrom"), from));
            	
            }
        };
    }
	
	public static Specification<ShipmentSubscription> allGivenButUntilDateAndSizes(List<Long> ids, Date from, Date toPick) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.and(
            			root.get("id").in(ids),
            			builder.isNull(root.get("maxsize")),
            			builder.isNotNull(root.get("pickupfrom")),
            			builder.isNull(root.get("deliveruntil")),
            			builder.lessThanOrEqualTo(root.get("pickupfrom"), toPick),
            			builder.greaterThanOrEqualTo(root.get("pickupfrom"), from));
            	
            }
        };
    }
	
	public static Specification<ShipmentSubscription> allGivenButPickupFromDate(List<Long> ids, Date to, Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.and(
            			root.get("id").in(ids),
            			builder.isNotNull(root.get("maxsize")),
            			builder.isNull(root.get("pickupfrom")),
            			builder.isNotNull(root.get("deliveruntil")),
            			root.get("maxsize").in(sss),
            			builder.lessThanOrEqualTo(root.get("deliveruntil"), to));
            	
            }
        };
    }
	
	public static Specification<ShipmentSubscription> allGivenButPickupFromToDate(List<Long> ids, Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.and(
            			root.get("id").in(ids),
            			builder.isNotNull(root.get("maxsize")),
            			builder.isNull(root.get("pickupfrom")),
            			builder.isNull(root.get("deliveruntil")),
            			root.get("maxsize").in(sss));
            	
            }
        };
    }
	
	public static Specification<ShipmentSubscription> allGivenButPickupFromDateAndSizes(List<Long> ids, Date to) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.and(
            			root.get("id").in(ids),
            			builder.isNull(root.get("maxsize")),
            			builder.isNull(root.get("pickupfrom")),
            			builder.isNotNull(root.get("deliveruntil")),
            			builder.lessThanOrEqualTo(root.get("deliveruntil"), to));
            	
            }
        };
    }
	
	public static Specification<ShipmentSubscription> onlyIdsGiven(List<Long> ids) {
        return new Specification<ShipmentSubscription>() {
            public Predicate toPredicate(
                Root<ShipmentSubscription> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.and(
            			root.get("id").in(ids),
            			builder.isNull(root.get("maxsize")),
            			builder.isNull(root.get("pickupfrom")),
            			builder.isNull(root.get("deliveruntil")));
            	
            }
        };
    }

}
