/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.savings.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.portfolio.charge.data.ChargeData;
import org.mifosplatform.portfolio.charge.domain.ChargeTimeType;
import org.mifosplatform.portfolio.charge.exception.SavingsAccountChargeNotFoundException;
import org.mifosplatform.portfolio.charge.service.ChargeDropdownReadPlatformService;
import org.mifosplatform.portfolio.charge.service.ChargeEnumerations;
import org.mifosplatform.portfolio.common.service.DropdownReadPlatformService;
import org.mifosplatform.portfolio.savings.data.SavingIdListData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountAnnualFeeData;
import org.mifosplatform.portfolio.savings.data.SavingsAccountChargeData;
import org.mifosplatform.portfolio.savings.data.SavingsIdOfChargeData;
import org.mifosplatform.portfolio.savings.domain.SavingsAccountStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SavingsAccountChargeReadPlatformServiceImpl implements SavingsAccountChargeReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService;
    private final DropdownReadPlatformService dropdownReadPlatformService;

    // mappers
    private final SavingsAccountChargeDueMapper chargeDueMapper;

    @Autowired
    public SavingsAccountChargeReadPlatformServiceImpl(final PlatformSecurityContext context,
            final ChargeDropdownReadPlatformService chargeDropdownReadPlatformService, final RoutingDataSource dataSource,
            final DropdownReadPlatformService dropdownReadPlatformService) {
        this.context = context;
        this.chargeDropdownReadPlatformService = chargeDropdownReadPlatformService;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.chargeDueMapper = new SavingsAccountChargeDueMapper();
        this.dropdownReadPlatformService = dropdownReadPlatformService;
    }

    
    private static final class SavingIdListDataMapper implements RowMapper<SavingIdListData>{

		@Override
		public SavingIdListData mapRow(final ResultSet rs,  @SuppressWarnings("unused") final int rowNum) throws SQLException{
			
			final Long savingId = rs.getLong("savingId");
			final LocalDate timePeriode = JdbcSupport.getLocalDate(rs, "timePeriode");
			final LocalDate startFeeChargeDate = JdbcSupport.getLocalDate(rs, "startFeeChargeDate"); 
			return SavingIdListData.instance(savingId,timePeriode,startFeeChargeDate);
		}
    }
    
    
    private static final class SavingIdListForDepositeLateChargeDataMapper implements RowMapper<SavingIdListData>{
    	@Override
    	public SavingIdListData mapRow(final ResultSet rs,  @SuppressWarnings("unused") final int rowNum) throws SQLException{
    		final Long savingId = rs.getLong("savingId");
    		final LocalDate activationOnDate = JdbcSupport.getLocalDate(rs, "activationOnDate");
    		final LocalDate startFeeChargeDate = JdbcSupport.getLocalDate(rs, "startFeeChargeDate");
    		return SavingIdListData.insatanceForAllSavingId(savingId, activationOnDate, startFeeChargeDate);
    	}
    }
    
    
    private static final class SavingsIdOfChargeDataMapper implements RowMapper<SavingsIdOfChargeData>{
    	@Override
    	public SavingsIdOfChargeData mapRow(final ResultSet rs,  @SuppressWarnings("unused") final int rowNum) throws SQLException{
    		final Long savingId = rs.getLong("savingId");
    		return SavingsIdOfChargeData.instance(savingId);
    	}
    }
    
    private static final class SavingsIdOfChargeDataWithDueDataMapper implements RowMapper<SavingsIdOfChargeData>{
    	@Override
    	public SavingsIdOfChargeData mapRow(final ResultSet rs,  @SuppressWarnings("unused") final int rowNum) throws SQLException{
    		final LocalDate dueDate = JdbcSupport.getLocalDate(rs, "dueDate");
    		return SavingsIdOfChargeData.instanceForDueDate(dueDate);
    	}
    }
    
    private static final class SavingsAccountChargeMapper implements RowMapper<SavingsAccountChargeData> {

        public String schema() {
            return "sc.id as id, c.id as chargeId, sc.savings_account_id as accountId, c.name as name, "
                    + "sc.amount as amountDue, "
                    + "sc.amount_paid_derived as amountPaid, "
                    + "sc.amount_waived_derived as amountWaived, "
                    + "sc.amount_writtenoff_derived as amountWrittenOff, "
                    + "sc.amount_outstanding_derived as amountOutstanding, "
                    + "sc.calculation_percentage as percentageOf, sc.calculation_on_amount as amountPercentageAppliedTo, "
                    + "sc.charge_time_enum as chargeTime, "
                    + "sc.is_penalty as penalty, "
                    + "sc.charge_due_date as dueAsOfDate, "
                    + "sc.fee_on_month as feeOnMonth, "
                    + "sc.fee_on_day as feeOnDay, sc.fee_interval as feeInterval, "
                    + "sc.charge_calculation_enum as chargeCalculation, "
                    + "sc.is_active as isActive, sc.inactivated_on_date as inactivationDate, "
                    + "c.currency_code as currencyCode, oc.name as currencyName, "
                    + "oc.decimal_places as currencyDecimalPlaces, oc.currency_multiplesof as inMultiplesOf, oc.display_symbol as currencyDisplaySymbol, "
                    + "oc.internationalized_name_code as currencyNameCode from m_charge c "
                    + "join m_organisation_currency oc on c.currency_code = oc.code "
                    + "join m_savings_account_charge sc on sc.charge_id = c.id ";
        }

        @Override
        public SavingsAccountChargeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long chargeId = rs.getLong("chargeId");
            final Long accountId = rs.getLong("accountId");
            final String name = rs.getString("name");
            final BigDecimal amount = rs.getBigDecimal("amountDue");
            final BigDecimal amountPaid = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountPaid");
            final BigDecimal amountWaived = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountWaived");
            final BigDecimal amountWrittenOff = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountWrittenOff");
            final BigDecimal amountOutstanding = rs.getBigDecimal("amountOutstanding");

            final BigDecimal percentageOf = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "percentageOf");
            final BigDecimal amountPercentageAppliedTo = JdbcSupport.getBigDecimalDefaultToZeroIfNull(rs, "amountPercentageAppliedTo");

            final String currencyCode = rs.getString("currencyCode");
            final String currencyName = rs.getString("currencyName");
            final String currencyNameCode = rs.getString("currencyNameCode");
            final String currencyDisplaySymbol = rs.getString("currencyDisplaySymbol");
            final Integer currencyDecimalPlaces = JdbcSupport.getInteger(rs, "currencyDecimalPlaces");
            final Integer inMultiplesOf = JdbcSupport.getInteger(rs, "inMultiplesOf");

            final CurrencyData currency = new CurrencyData(currencyCode, currencyName, currencyDecimalPlaces, inMultiplesOf,
                    currencyDisplaySymbol, currencyNameCode);

            final int chargeTime = rs.getInt("chargeTime");
            final EnumOptionData chargeTimeType = ChargeEnumerations.chargeTimeType(chargeTime);

            final LocalDate dueAsOfDate = JdbcSupport.getLocalDate(rs, "dueAsOfDate");
            final Integer feeInterval = JdbcSupport.getInteger(rs, "feeInterval");
            MonthDay feeOnMonthDay = null;
            final Integer feeOnMonth = JdbcSupport.getInteger(rs, "feeOnMonth");
            final Integer feeOnDay = JdbcSupport.getInteger(rs, "feeOnDay");
            if (feeOnDay != null && feeOnMonth != null) {
                feeOnMonthDay = new MonthDay(feeOnMonth, feeOnDay);
            }

            final int chargeCalculation = rs.getInt("chargeCalculation");
            final EnumOptionData chargeCalculationType = ChargeEnumerations.chargeCalculationType(chargeCalculation);
            final boolean penalty = rs.getBoolean("penalty");
            final Boolean isActive = rs.getBoolean("isActive");
            final LocalDate inactivationDate = JdbcSupport.getLocalDate(rs, "inactivationDate");

            final Collection<ChargeData> chargeOptions = null;

            return SavingsAccountChargeData.instance(id, chargeId, accountId, name, currency, amount, amountPaid, amountWaived,
                    amountWrittenOff, amountOutstanding, chargeTimeType, dueAsOfDate, chargeCalculationType, percentageOf,
                    amountPercentageAppliedTo, chargeOptions, penalty, feeOnMonthDay, feeInterval, isActive, inactivationDate);
        }
    }

    @Override
    public ChargeData retrieveSavingsAccountChargeTemplate() {
        this.context.authenticatedUser();

        final List<EnumOptionData> allowedChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService.retrieveCalculationTypes();
        final List<EnumOptionData> allowedChargeTimeOptions = this.chargeDropdownReadPlatformService.retrieveCollectionTimeTypes();
        final List<EnumOptionData> loansChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveLoanCalculationTypes();
        final List<EnumOptionData> loansChargeTimeTypeOptions = this.chargeDropdownReadPlatformService.retrieveLoanCollectionTimeTypes();
        final List<EnumOptionData> savingsChargeCalculationTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCalculationTypes();
        final List<EnumOptionData> savingsChargeTimeTypeOptions = this.chargeDropdownReadPlatformService
                .retrieveSavingsCollectionTimeTypes();

        final List<EnumOptionData> feeFrequencyOptions = this.dropdownReadPlatformService.retrievePeriodFrequencyTypeOptions();

        // TODO AA : revisit for merge conflict - Not sure method signature
        return ChargeData.template(null, allowedChargeCalculationTypeOptions, null, allowedChargeTimeOptions, null,
                loansChargeCalculationTypeOptions, loansChargeTimeTypeOptions, savingsChargeCalculationTypeOptions,
                savingsChargeTimeTypeOptions, feeFrequencyOptions);
    }

    @Override
    public SavingsAccountChargeData retrieveSavingsAccountChargeDetails(final Long id, final Long savingsAccountId) {
        try {
            this.context.authenticatedUser();

            final SavingsAccountChargeMapper rm = new SavingsAccountChargeMapper();

            final String sql = "select " + rm.schema() + " where sc.id=? and sc.savings_account_id=?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { id, savingsAccountId });
        } catch (final EmptyResultDataAccessException e) {
            throw new SavingsAccountChargeNotFoundException(savingsAccountId);
        }
    }
    
    @Override
    public SavingsIdOfChargeData retriveOneWithMaxOfDueDate(Long savingId){
    	
    	final SavingsIdOfChargeDataWithDueDataMapper rm = new SavingsIdOfChargeDataWithDueDataMapper();
    	try{
    	String sql = " select max(msach.charge_due_date) as dueDate "
    		    	+ " from m_savings_account_charge msach where msach.charge_time_enum = 12 and "
                    + "  msach.savings_account_id = " + savingId;
    	
    	return this.jdbcTemplate.queryForObject(sql,rm,new Object[]{});
    	}catch(final EmptyResultDataAccessException e){
    		return null;
    	}
    	
    }
    
    @Override 
    public Collection<SavingIdListData> retriveAllSavingIdForApplyDepositeLateCharge(){
    	final SavingIdListForDepositeLateChargeDataMapper rm = new SavingIdListForDepositeLateChargeDataMapper();
    	String sql = " select msa.id as savingId, msa.activatedon_date as activationOnDate, msa.start_saving_deposite_late_fee_date as startFeeChargeDate "  
                    + " from m_savings_account msa "
                    + " left join m_savings_product_charge mspc on msa.product_id = mspc.savings_product_id "
                    + " left join m_charge mch on mspc.charge_id = mch.id "
                    + " where mch.charge_time_enum = 12 "
                    + " and msa.status_enum = 300 "
                    + " and msa.product_id = mspc.savings_product_id ";
    	
    		return this.jdbcTemplate.query(sql, rm, new Object[]{});
    }
    
    
    @Override
    public Collection<SavingsIdOfChargeData> retriveAllSavingIdHavingDepositCharge(String startDate, String endDate,  Long frequency){
    	final SavingsIdOfChargeDataMapper rm = new SavingsIdOfChargeDataMapper();
        String sql = new String();

        if(frequency == 0){
        	sql = " select a.savingId from "
                    + " (select msac.savings_account_id as savingId, max(msac.charge_due_date) as days from m_savings_account_charge msac "
                    + " where msac.charge_time_enum = 12 "
                    + " and msac.is_active = 1 "
                    + " group by msac.savings_account_id )a "
                    + " where a.days between ('" + startDate + "') and now()";
        }
        else if(frequency == 1){
    		sql = " select a.savingId from "
                    + " (select msac.savings_account_id as savingId, max(week(msac.charge_due_date)) as days from m_savings_account_charge msac "
                    + " where msac.charge_time_enum = 12 "
                    + " and msac.is_active = 1 "
                    + " group by msac.savings_account_id )a "
                    + " where a.days between week('" + startDate + "') and week(now())";
    	}
    	else if(frequency == 2){
    		sql = " select a.savingId from "
                    + " (select msac.savings_account_id as savingId, max(month(msac.charge_due_date)) as days from m_savings_account_charge msac "
                    + " where msac.charge_time_enum = 12 "
                    + " and msac.is_active = 1 "
                    + " group by msac.savings_account_id )a "
                    + " where a.days between month('" + startDate + "') and month(now())";
    	}
    	else if(frequency == 3){
    		  sql = " select a.savingId from "
                    + " (select msac.savings_account_id as savingId, max(year(msac.charge_due_date)) as days from m_savings_account_charge msac "
                    + " where msac.charge_time_enum = 12 "
                    + " and msac.is_active = 1 "
                    + " group by msac.savings_account_id )a "
                    + " where a.days between year('" + startDate + "') and year(now())";
    	}
		return this.jdbcTemplate.query(sql, rm, new Object[]{});
    	
    }
    
    
    @Override
    public Collection<SavingIdListData> retriveSavingAccountForApplySavingDepositeFee(final String startDate, final String endDate,  Long frequency){
    	
    	final SavingIdListDataMapper rm = new SavingIdListDataMapper();
    	String sql = new String();
    	if(frequency == 0 || frequency == 3){
    		  sql = "select a.savingId as savingId, a.Txn as timePeriode, a.startFeeChargeDate from (select msa.id as savingId, MAX(mst.transaction_date) as days,"
    				 + " max(mst.transaction_date) as Txn, msa.start_saving_deposite_late_fee_date as startFeeChargeDate from m_savings_product msp "
    				 + " left join m_savings_product_charge mspc on mspc.savings_product_id = msp.id "
    			     + " left join m_charge mch on mspc.charge_id = mch.id "
    			     + " left join m_savings_account msa on msp.id = msa.product_id "
    			     + " left join m_savings_account_transaction mst on mst.savings_account_id = msa.id "
    			     + " where mspc.savings_product_id = msa.product_id "
    			     + " and mst.transaction_type_enum = 1 "
    			     + " and msa.status_enum = 300 "
    				 + " group by msa.id ) a "
    			     + " where a.days NOT BETWEEN ('" + startDate + "') AND now()";
    	}
    	else if(frequency == 1){
    		 sql = "select a.savingId as savingId, a.Txn as timePeriode, a.startFeeChargeDate  from (select msa.id as savingId, MAX(week(mst.transaction_date)) as days,"
    				 + " max(mst.transaction_date) as Txn,  msa.start_saving_deposite_late_fee_date as startFeeChargeDate from m_savings_product msp "
    		         + " left join m_savings_product_charge mspc on mspc.savings_product_id = msp.id "
    		         + " left join m_charge mch on mspc.charge_id = mch.id "
    		         + " left join m_savings_account msa on msp.id = msa.product_id "
    		         + " left join m_savings_account_transaction mst on mst.savings_account_id = msa.id "
    			     + " where mspc.savings_product_id = msa.product_id "
    		         + " and mst.transaction_type_enum = 1 "
    		         + " and msa.status_enum = 300 "
    		         + " group by msa.id ) a "
    		         + " where a.days NOT BETWEEN week('" + startDate + "') AND week(now())";
    	}
    	else if(frequency == 2){
              sql = "select a.savingId as savingId, a.Txn as timePeriode, a.startFeeChargeDate  from (select msa.id as savingId, MAX(month(mst.transaction_date)) as days,"
            		 + " max(mst.transaction_date) as Txn, msa.start_saving_deposite_late_fee_date as startFeeChargeDate from m_savings_product msp "
                     + " left join m_savings_product_charge mspc on mspc.savings_product_id = msp.id "
                     + " left join m_charge mch on mspc.charge_id = mch.id "
                     + " left join m_savings_account msa on msp.id = msa.product_id "
                     + " left join m_savings_account_transaction mst on mst.savings_account_id = msa.id "
	                 + " where mspc.savings_product_id = msa.product_id "
                     + " and mst.transaction_type_enum = 1 "
                     + " and msa.status_enum = 300 "
                     + " group by msa.id ) a "
                     + " where a.days NOT BETWEEN MONTH('" + startDate + "') AND MONTH(now())";
    	}
    	
    	/*else if (frequency == 3){
    		 sql = "select a.savingId as savingId, a.Txn as timePeriode, a.startFeeChargeDate  from (select msa.id as savingId, MAX(year(mst.transaction_date)) as days, "
    				 + " max(mst.transaction_date) as Txn, msa.start_saving_deposite_late_fee_date as startFeeChargeDate from m_savings_product msp "
    		         + " left join m_savings_product_charge mspc on mspc.savings_product_id = msp.id "
    		         + " left join m_charge mch on mspc.charge_id = mch.id "
    		         + " left join m_savings_account msa on msp.id = msa.product_id "
    		         + " left join m_savings_account_transaction mst on mst.savings_account_id = msa.id "
    			     + " where mspc.savings_product_id = msa.product_id "
    		         + " and mst.transaction_type_enum = 1 "
    		         + " and msa.status_enum = 300 "
    		         + " group by msa.id ) a "
    		         + " where a.days NOT BETWEEN year('" + startDate + "') AND year('" + endDate +"')";
    	}*/
    	
    	return this.jdbcTemplate.query(sql, rm, new Object[]{});
    	
    }
    
    

    @Override
    public Collection<SavingsAccountChargeData> retrieveSavingsAccountCharges(final Long loanId, final String status) {
        this.context.authenticatedUser();

        final SavingsAccountChargeMapper rm = new SavingsAccountChargeMapper();
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select ").append(rm.schema()).append(" where sc.savings_account_id=? ");
        if (status.equalsIgnoreCase("active")) {
            sqlBuilder.append(" and sc.is_active = 1 ");
        } else if (status.equalsIgnoreCase("inactive")) {
            sqlBuilder.append(" and sc.is_active = 0 ");
        }
        sqlBuilder.append(" order by sc.charge_time_enum ASC, sc.charge_due_date ASC, sc.is_penalty ASC");

        return this.jdbcTemplate.query(sqlBuilder.toString(), rm, new Object[] { loanId });
    }

    private static final class SavingsAccountChargeDueMapper implements RowMapper<SavingsAccountAnnualFeeData> {

        private final String schemaSql;

        public SavingsAccountChargeDueMapper() {
            final StringBuilder sqlBuilder = new StringBuilder(200);
            sqlBuilder.append("sac.id as id, ");
            sqlBuilder.append("sa.id as accountId, ");
            sqlBuilder.append("sa.account_no as accountNo, ");
            sqlBuilder.append("sac.charge_due_date as dueDate ");
            sqlBuilder.append("from m_savings_account_charge sac join m_savings_account sa on sac.savings_account_id = sa.id ");

            this.schemaSql = sqlBuilder.toString();
        }

        public String schema() {
            return this.schemaSql;
        }

        @Override
        public SavingsAccountAnnualFeeData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final Long accountId = rs.getLong("accountId");
            final String accountNo = rs.getString("accountNo");
            final LocalDate annualFeeNextDueDate = JdbcSupport.getLocalDate(rs, "dueDate");

            return SavingsAccountAnnualFeeData.instance(id, accountId, accountNo, annualFeeNextDueDate);
        }
    }

    @Override
    public Collection<SavingsAccountAnnualFeeData> retrieveChargesWithAnnualFeeDue() {
        final String sql = "select " + this.chargeDueMapper.schema() + " where sac.charge_due_date is not null and sac.charge_time_enum = "
                + ChargeTimeType.ANNUAL_FEE.getValue() + " and sac.charge_due_date <= NOW() and sa.status_enum = "
                + SavingsAccountStatusType.ACTIVE.getValue();

        return this.jdbcTemplate.query(sql, this.chargeDueMapper, new Object[] {});
    }

    @Override
    public Collection<SavingsAccountAnnualFeeData> retrieveChargesWithDue() {
        final String sql = "select "
                + this.chargeDueMapper.schema()
                + " where sac.charge_due_date is not null and sac.charge_due_date <= NOW() and sac.waived = 0 and sac.is_paid_derived=0 and sac.is_active=1 and sa.status_enum = "
                + SavingsAccountStatusType.ACTIVE.getValue() + " order by sac.charge_due_date ";

        return this.jdbcTemplate.query(sql, this.chargeDueMapper, new Object[] {});

    }
    

}
