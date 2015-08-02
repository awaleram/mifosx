package org.mifosplatform.portfolio.investment.data;


public class LoanInvestmentData {
    
    final Long saving_id;
    final String name;
    final String accountno;
    final Long savingamount;
    final String productname;
    final Long investedAmount;
    public LoanInvestmentData(Long saving_id, String name, String accountno, Long savingamount, String productname, Long investedAmount) {
        super();
        this.saving_id = saving_id;
        this.name = name;
        this.accountno = accountno;
        this.savingamount = savingamount;
        this.productname = productname;
        this.investedAmount = investedAmount;
    }
    
    public Long getSaving_id() {
        return this.saving_id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getAccountno() {
        return this.accountno;
    }
    
    public Long getSavingamount() {
        return this.savingamount;
    }
    
    public String getProductname() {
        return this.productname;
    }
   
    public Long getInvestedAmount() {
		return this.investedAmount;
	}

	public static LoanInvestmentData intance (Long saving_id, String name, String accountno, Long savingamount,
            String productname, Long investedAmount){
        return new LoanInvestmentData(saving_id, name, accountno, savingamount, productname, investedAmount);
    }
}
