package org.mifosplatform.portfolio.savings.data;

import org.joda.time.LocalDate;


public class SavingsIdOfChargeData {

    final Long savingId;
    final LocalDate dueDate;

	public SavingsIdOfChargeData(Long savingId, LocalDate dueDate) {
        super();
        this.savingId = savingId;
        this.dueDate = dueDate;
    }

    
    public Long getSavingId() {
        return this.savingId;
    }
    

    public LocalDate getDueDate() {
		return this.dueDate;
	}
    
    public static SavingsIdOfChargeData instance(Long savingId){
        return new SavingsIdOfChargeData(savingId,null);
    }
    
    public static SavingsIdOfChargeData instanceForDueDate(LocalDate dueDate){
    	return new SavingsIdOfChargeData(null, dueDate);
    }
    
}
