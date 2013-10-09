package com.sap.benefits.management.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.benefits.management.persistence.model.Benefit;
import com.sap.benefits.management.persistence.model.BenefitType;

public class BenefitDAO extends BasicDAO<Benefit> {
	
	private final Logger logger = LoggerFactory.getLogger(BenefitDAO.class);

	public BenefitDAO() {
		super();
	}

	public Benefit getByName(String name) {
		Benefit benefit = null;

		final EntityManager em = factory.createEntityManager();
		try {
			em.getTransaction().begin();

			final Query query = em.createQuery("select b from Benefit b where b.name = :name");
			query.setParameter("name", name); 
			
			benefit = (Benefit) query.getSingleResult();
		} catch (NoResultException e) {
			logger.error("Could not retrieve entity {} from table {}.", name, "Benefit"); 
		} finally{
			em.close();
		}

		return benefit;
	}
	
	@Override
	public Benefit save(Benefit benefit){
		Benefit existingBenefit = getByName(benefit.getName());
		if(existingBenefit == null){
			saveNew(benefit);
			return benefit;
		}
		
		final BenefitTypeDAO benefitTypeDAO = new BenefitTypeDAO();
		final Collection<BenefitType> types = benefit.getTypes();
		for (BenefitType benefitType : types) {
			benefitType.setBenefit(existingBenefit);
			benefitTypeDAO.saveNew(benefitType);
		}
		
		return existingBenefit;
	}

}
