package org.mifosplatform.portfolio.investment.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.loanaccount.domain.Loan;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.mifosplatform.portfolio.investment.data.SavingInvestmentData;
import org.mifosplatform.portfolio.savings.domain.SavingsAccount;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_investment")
public class Investment extends AbstractPersistable<Long> {
    
    @Column(name = "saving_id", nullable = false)
    private Long savingId;
    
    @Column(name = "loan_id", nullable = false)
    private Long loanId;
    
    @Column(name = "invested_amount", scale = 6, precision = 19, nullable = true)
    private Long investedAmount;
    
    protected Investment() {
        //
    }

   
    public Investment(Long savingId, Long loanId, Long investedAmount) {
        // super();
        this.savingId = savingId;
        this.loanId = loanId;
        this.investedAmount = investedAmount;
    }

    
    public Long getInvestedAmount() {
		return this.investedAmount;
	}


	public void setInvestedAmount(Long investedAmount) {
		this.investedAmount = investedAmount;
	}


	public Long getSavingId() {
        return this.savingId;
    }


    
    public void setSavingId(Long savingId) {
        this.savingId = savingId;
    }


    
    public Long getLoanId() {
        return this.loanId;
    }


    
    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
       
}
    

    

