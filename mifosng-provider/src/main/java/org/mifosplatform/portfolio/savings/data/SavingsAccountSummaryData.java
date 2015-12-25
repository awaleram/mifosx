/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.data;

import java.math.BigDecimal;

import org.mifosplatform.organisation.monetary.data.CurrencyData;

/**
 * Immutable data object representing Savings Account summary information.
 */
@SuppressWarnings("unused")
public class SavingsAccountSummaryData {

    private final CurrencyData currency;
    private final BigDecimal totalDeposits;
    private final BigDecimal totalWithdrawals;
    private final BigDecimal totalWithdrawalFees;
    private final BigDecimal totalAnnualFees;
    private final BigDecimal totalInterestEarned;
    private final BigDecimal totalInterestPosted;
    private BigDecimal accountBalance;
    private final BigDecimal totalFeeCharge;
    private final BigDecimal totalPenaltyCharge;

    private BigDecimal overdraftLimit;
    private BigDecimal onHoldFunds;
    private BigDecimal availableFunds;
    
    public SavingsAccountSummaryData(final CurrencyData currency, final BigDecimal totalDeposits, final BigDecimal totalWithdrawals,
            final BigDecimal totalWithdrawalFees, final BigDecimal totalAnnualFees, final BigDecimal totalInterestEarned,
            final BigDecimal totalInterestPosted, final BigDecimal accountBalance, final BigDecimal totalFeeCharge,
            final BigDecimal totalPenaltyCharge) {
        this.currency = currency;
        this.totalDeposits = totalDeposits;
        this.totalWithdrawals = totalWithdrawals;
        this.totalWithdrawalFees = totalWithdrawalFees;
        this.totalAnnualFees = totalAnnualFees;
        this.totalInterestEarned = totalInterestEarned;
        this.totalInterestPosted = totalInterestPosted;
        this.accountBalance = accountBalance;
        this.totalFeeCharge = totalFeeCharge;
        this.totalPenaltyCharge = totalPenaltyCharge;
        this.overdraftLimit = null;
        this.availableFunds = null;
        this.onHoldFunds = null;
    }
    
    
    public SavingsAccountSummaryData(final BigDecimal accountBalance, final BigDecimal overdraftLimit, final BigDecimal onHoldFunds){
           this.accountBalance = accountBalance;
           this.onHoldFunds = onHoldFunds;
           this.overdraftLimit = overdraftLimit;
    	   this.currency = null;
           this.totalDeposits = null;
           this.totalWithdrawals = null;
           this.totalWithdrawalFees = null;
           this.totalAnnualFees = null;
           this.totalInterestEarned = null;
           this.totalInterestPosted = null; 
           this.totalFeeCharge = null;
           this.totalPenaltyCharge = null;
           this.availableFunds = getAvailableFunds(accountBalance, overdraftLimit, onHoldFunds);
    }
    
    
    public BigDecimal getAvailableFunds(final BigDecimal accountBalance, final BigDecimal overdraftLimit, final BigDecimal onHoldFunds){
    	this.accountBalance = accountBalance;
    	this.overdraftLimit = overdraftLimit;
    	this.onHoldFunds = onHoldFunds;
    	
    	if(this.overdraftLimit == null){
    		this.overdraftLimit = BigDecimal.ZERO;
    	}
    	if(this.onHoldFunds == null){
    		this.onHoldFunds = BigDecimal.ZERO;
    	}
    	this.availableFunds = (this.accountBalance.add(this.overdraftLimit)).subtract(this.onHoldFunds);
    	
		return this.availableFunds;
    	 
    }


	public BigDecimal getAvailableFunds() {
		return this.availableFunds;
	}


	public void setAvailableFunds(BigDecimal availableFunds) {
		this.availableFunds = availableFunds;
	}
}