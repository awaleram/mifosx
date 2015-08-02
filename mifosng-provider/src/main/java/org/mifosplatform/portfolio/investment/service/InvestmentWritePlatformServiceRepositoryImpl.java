package org.mifosplatform.portfolio.investment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.mifosplatform.portfolio.investment.domain.InvestmentRepositoryWrapper;
import org.mifosplatform.portfolio.investment.domain.Investment;
import org.mifosplatform.portfolio.investment.domain.InvestmentRepositoryWrapper;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepository;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentWritePlatformServiceRepositoryImpl implements InvestmentWritePlatformService {

    private final PlatformSecurityContext context;
    private final InvestmentRepositoryWrapper repositoryWrapper;
    private final SavingsAccountRepositoryWrapper savingsaccount;
    private final SavingsAccountRepository savingAccount;
    private final LoanRepositoryWrapper loanRepository;
    private final InvestmentReadPlatformService savingInvestment;

    @Autowired
    public InvestmentWritePlatformServiceRepositoryImpl(PlatformSecurityContext context,
            InvestmentRepositoryWrapper repositoryWrapper, SavingsAccountRepositoryWrapper savingsaccount,
            LoanRepositoryWrapper loanRepository, SavingsAccountRepository savingAccount,
            InvestmentReadPlatformService savingInvestment) {

        this.context = context;
        this.repositoryWrapper = repositoryWrapper;
        this.loanRepository = loanRepository;
        this.savingsaccount = savingsaccount;
        this.savingAccount = savingAccount;
        this.savingInvestment = savingInvestment;
    }

    @Override
    public CommandProcessingResult addSavingsInvestment(Long savingsId, JsonCommand command) {
        // TODO Auto-generated method stub

        // final AppUser user = this.context.authenticatedUser();

        final Long savingId = command.longValueOfParameterNamed("savingId");
        final String[] loanIds = command.arrayValueOfParameterNamed("loanId");
        final String[] investedAmounts = command.arrayValueOfParameterNamed("investedAmounts");
        final List<Long> investedAmount = new ArrayList<Long>();
        final List<Long> loanId = new ArrayList<Long>();
        List<Long> existingLoanIds = new ArrayList<Long>();
        List<Long> newLoanIds = new ArrayList<Long>();
        List<Long> newInvestedAmount = new ArrayList<Long>();
        Long totalAmount = null;

        Long id = null;

        if (loanIds != null) {
            for (String Id : loanIds) {
                id = Long.parseLong(Id);
                loanId.add(id);
            }
            for(String investment : investedAmounts){
                totalAmount = Long.parseLong(investment);
                
                investedAmount.add(totalAmount);
              }
        }

        existingLoanIds = this.savingInvestment.retriveLoanIdBySavingId(savingId);

        if (existingLoanIds.isEmpty()) {
            newLoanIds = loanId;
            newInvestedAmount = investedAmount;

        }

        else {
            for (int i = 0; i < loanId.size(); i++) {
                int count = 0;
                for (int j = 0; j < existingLoanIds.size(); j++) {
                    if (loanId.get(i).equals(existingLoanIds.get(j))) {
                        //
                        count++;
                    }
                }

                if (count == 0) {
                    newLoanIds.add(loanId.get(i));
                    newInvestedAmount.add(investedAmount.get(i));
                }

            }
        }

        for (int i = 0; i < newLoanIds.size(); i++) {

            Investment savingInvestment = new Investment(savingId, newLoanIds.get(i), newInvestedAmount.get(i));
            this.repositoryWrapper.save(savingInvestment);

        }

        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult addLoanInvestment(Long loanId, JsonCommand command) {
        // TODO Auto-generated method stub

        Long loanid = command.longValueOfParameterNamed("loanId");
        final String[] savingIds = command.arrayValueOfParameterNamed("savingId");
        final String[] investedAmounts = command.arrayValueOfParameterNamed("investedAmounts");
        final List<Long> investedAmount = new ArrayList<Long>();
        final List<Long> savingId = new ArrayList<Long>();
        List<Long> existingSavingId = new ArrayList<Long>();
        List<Long> newSavingId = new ArrayList<Long>();
        List<Long> newInvestedAmount = new ArrayList<Long>();        
        Long id = null;
        BigDecimal amount = null;
        Long totalAmount = null;
        if (savingIds != null) {
            for (String Id : savingIds) {

                id = Long.parseLong(Id);
                savingId.add(id);
                
                
            }
            for(String investment : investedAmounts){
              totalAmount = Long.parseLong(investment);
              
              investedAmount.add(totalAmount);
            }
        }

        existingSavingId = this.savingInvestment.retriveSavingIdByLoanId(loanid);
        
        if(existingSavingId == null){
            newSavingId = savingId;
        }
        else{
            
            for (int i = 0; i < savingId.size(); i++) {
                int count = 0;
                for (int j = 0; j < existingSavingId.size(); j++) {
                    if (savingId.get(i).equals(existingSavingId.get(j))) {
                        //
                        count++;
                    }
                }

                if (count == 0) {
                    newSavingId.add(savingId.get(i));
                    newInvestedAmount.add(investedAmount.get(i));
                }

            }
            
            
        }
        
        
        for (int i = 0; i < newSavingId.size(); i++) {

            Investment savingInvestment = new Investment(newSavingId.get(i), loanid, newInvestedAmount.get(i));
            this.repositoryWrapper.save(savingInvestment);

        }

        return new CommandProcessingResultBuilder().build();
        
    }

    @Override
    public CommandProcessingResult deleteInvestmentBasedOnMapping(Long savingId, Long loanId) {

        Long id = null;

        id = this.savingInvestment.retriveSavingInvestmentId(savingId, loanId);

        Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);

        this.repositoryWrapper.delete(savingInvestment);

        // TODO Auto-generated method stub
        return new CommandProcessingResultBuilder().build();
    }

    @Override
    public CommandProcessingResult deleteLoanInvestment(Long loanId, Long savingId) {

        Long id = null;
        id = this.savingInvestment.retriveLoanInvestmentId(loanId, savingId);
        Investment savingInvestment = this.repositoryWrapper.findWithNotFoundDetection(id);
        this.repositoryWrapper.delete(savingInvestment);

        return new CommandProcessingResultBuilder().build();
    }

}
