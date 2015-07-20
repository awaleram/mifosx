package org.mifosplatform.portfolio.savings.data;

import java.util.List;

public class SavingIdListData {

	private final Long savingId;

	public SavingIdListData(Long savingId) {
		super();
		this.savingId = savingId;
	}
	
	public Long getSavingId() {
		return this.savingId;
	}

	public static SavingIdListData instance(Long savingId){
		return new SavingIdListData(savingId);
		}
}
