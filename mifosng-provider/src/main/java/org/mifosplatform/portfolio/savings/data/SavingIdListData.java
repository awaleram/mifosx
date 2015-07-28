package org.mifosplatform.portfolio.savings.data;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;

public class SavingIdListData {

	private final Long savingId;
	private final LocalDate maxTransactionDate;
	private final  LocalDate startFeeChargeDate;
	private final LocalDate activateOnDate;

	public SavingIdListData(final Long savingId, final LocalDate maxTransactionDate, final LocalDate startFeeChargeDate, final LocalDate activateOnDate) {
		super();
		this.savingId = savingId;
		this.maxTransactionDate = maxTransactionDate;
		this.startFeeChargeDate = startFeeChargeDate;
		this.activateOnDate = activateOnDate;
	}

	public static SavingIdListData instance(Long savingId, LocalDate maxTransactionDate, LocalDate startFeeChargeDate){
		return new SavingIdListData(savingId, maxTransactionDate, startFeeChargeDate, null);
		}

	
	public static SavingIdListData insatanceForAllSavingId(Long savingId, LocalDate activateOnDate, LocalDate startFeeChargDate){
		return new SavingIdListData(savingId, null , startFeeChargDate, activateOnDate);
	}
	
	public LocalDate getMaxTransactionDate() {
		return this.maxTransactionDate;
	}

	public LocalDate getActivateOnDate() {
		return this.activateOnDate;
	}

	public LocalDate getStartFeeChargeDate() {
		return this.startFeeChargeDate;
	}
	public Long getSavingId() {
		return this.savingId;
	}
}
