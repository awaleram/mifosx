package org.mifosplatform.portfolio.investment.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.investment.service.InvestmentWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "SAVINGINVESTMENT", action = "CREATE")
public class CreateSavingInvestmentCommandHandler implements NewCommandSourceHandler{
    
    private final InvestmentWritePlatformService savingInvestmentWriteService;
    
    @Autowired
    public CreateSavingInvestmentCommandHandler(InvestmentWritePlatformService savingInvestmentWriteService) {
        super();
        this.savingInvestmentWriteService = savingInvestmentWriteService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
      return this.savingInvestmentWriteService.addSavingsInvestment(command.entityId(), command);
    }
    

}
