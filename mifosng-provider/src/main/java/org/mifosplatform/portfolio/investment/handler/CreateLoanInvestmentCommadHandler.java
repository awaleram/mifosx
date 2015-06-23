package org.mifosplatform.portfolio.investment.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.investment.domain.Investment;
import org.mifosplatform.portfolio.investment.service.InvestmentWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity="LOANINVESTMENT", action= "CREATE")
public class CreateLoanInvestmentCommadHandler implements NewCommandSourceHandler {

    final InvestmentWritePlatformService loanInvestment;
    
    @Autowired
    public CreateLoanInvestmentCommadHandler(InvestmentWritePlatformService loanInvestment) {
        super();
        this.loanInvestment = loanInvestment;
    }


    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        
        return this.loanInvestment.addLoanInvestment(command.getLoanId(), command);
        
    }

}
