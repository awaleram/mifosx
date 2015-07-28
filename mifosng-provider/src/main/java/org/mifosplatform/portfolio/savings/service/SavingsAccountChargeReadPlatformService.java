/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.savings.data.SavingIdListData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountAnnualFeeData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountChargeData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountData;
import org.mifosplatform.portfolio.savings.data.SavingsIdOfChargeData;

public interface SavingsAccountChargeReadPlatformService {

    ChargeData retrieveSavingsAccountChargeTemplate();

    Collection<SavingsAccountChargeData> retrieveSavingsAccountCharges(Long savingsAccountId, String status);

    SavingsAccountChargeData retrieveSavingsAccountChargeDetails(Long savingsAccountChargeId, Long savingsAccountId);

    Collection<SavingsAccountAnnualFeeData> retrieveChargesWithAnnualFeeDue();

    Collection<SavingsAccountAnnualFeeData> retrieveChargesWithDue();
    Collection<SavingsIdOfChargeData> retriveAllSavingIdHavingDepositCharge(String startDate, String endDate,  Long frequency);
    Collection<SavingIdListData> retriveSavingAccountForApplySavingDepositeFee(final String startDateofMonth, final String endDateOfMonth,  Long frequency);

    Collection<SavingIdListData> retriveAllSavingIdForApplyDepositeLateCharge();
    SavingsIdOfChargeData retriveOneWithMaxOfDueDate(Long savingId);
    
}
