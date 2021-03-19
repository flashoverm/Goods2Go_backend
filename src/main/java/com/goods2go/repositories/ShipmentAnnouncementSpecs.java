package com.goods2go.repositories;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.goods2go.models.ShipmentAnnouncement;
import com.goods2go.models.ShipmentSize;

public class ShipmentAnnouncementSpecs {
	
	public static Specification<ShipmentAnnouncement> betweenFromToRangeEPickupLDeliveryDate(Date from, Date to) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.and(
            			builder.lessThanOrEqualTo(root.get("earliestpickupdate"), from),
            			builder.greaterThanOrEqualTo(root.get("latestdeliverydate"), to)
            			);
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> laterOrEqualsThanEPickupDate(Date from) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.lessThanOrEqualTo(root.get("earliestpickupdate"), from);
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> beforeOrEqualsLDeliveryDate(Date to) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	return builder.greaterThanOrEqualTo(root.get("latestdeliverydate"), to);
            }
        };
    }

	public static Specification<ShipmentAnnouncement> idIn(List<Long> ids) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return root.get("id").in(ids);
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> sizeIn(Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return root.get("size").in(sss);
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> equalsSize(ShipmentSize ss) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.equal(root.get("size"), ss);
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> allGiven(List<Long> ids, Date from, Date to, Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.and(
            			root.get("id").in(ids),
            			root.get("size").in(sss),
            			//builder.greaterThanOrEqualTo(root.get("earliestpickupdate"), from),
            			//builder.lessThanOrEqualTo(root.get("latestdeliverydate"), to));
            			builder.lessThanOrEqualTo(root.get("earliestpickupdate"), from),
            			builder.greaterThanOrEqualTo(root.get("latestpickupdate"), from),
            			builder.greaterThanOrEqualTo(root.get("latestdeliverydate"), to));
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> allGivenButSizes(List<Long> ids, Date from, Date to) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.and(
            			root.get("id").in(ids),
            			builder.lessThanOrEqualTo(root.get("earliestpickupdate"), from),
            			builder.greaterThanOrEqualTo(root.get("latestpickupdate"), from),
            			builder.greaterThanOrEqualTo(root.get("latestdeliverydate"), to));
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> allGivenButPickupFromDate(List<Long> ids, Date to, Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.and(
            			root.get("id").in(ids),
            			root.get("size").in(sss),
            			builder.greaterThanOrEqualTo(root.get("latestdeliverydate"), to));
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> allGivenButPickupFromDateAndSizes(List<Long> ids, Date to) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.and(
            			root.get("id").in(ids),
            			builder.greaterThanOrEqualTo(root.get("latestdeliverydate"), to));
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> allGivenButDeliverUntilDate(List<Long> ids, Date from, Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.and(
            			root.get("id").in(ids),
            			root.get("size").in(sss),
            			builder.greaterThanOrEqualTo(root.get("latestpickupdate"), from),
            			builder.lessThanOrEqualTo(root.get("earliestpickupdate"), from));
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> allGivenButDeliverUntilDateAndSizes(List<Long> ids, Date from) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.and(
            			root.get("id").in(ids),
            			builder.greaterThanOrEqualTo(root.get("latestpickupdate"), from),
            			builder.lessThanOrEqualTo(root.get("earliestpickupdate"), from));
            }
        };
    }
	
	public static Specification<ShipmentAnnouncement> onlyIdsAndSizesGiven(List<Long> ids, Iterable<ShipmentSize> sss) {
        return new Specification<ShipmentAnnouncement>() {
            public Predicate toPredicate(
                Root<ShipmentAnnouncement> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {
            	
            	return builder.and(
            			root.get("id").in(ids),
            			root.get("size").in(sss));
            }
        };
    }

}
