package org.mifosplatform.portfolio.investment.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.mifosplatform.portfolio.investment.data.LoanInvestmentData;
import org.mifosplatform.portfolio.investment.data.SavingInvestmentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class InvestmentReadPlatformServiceImpl implements
		InvestmentReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	public static RoutingDataSource dataSource;

	@Autowired
	public InvestmentReadPlatformServiceImpl(RoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<SavingInvestmentData> retriveLoanAccountsBySavingId(
			Long savingId) throws SQLException {

		final SavingInvestmentDataMapper mapper = new SavingInvestmentDataMapper();
		final String schema = " select " + mapper.savingAccountsSchema()
				+ " where ms.saving_id =" + savingId;

		List<SavingInvestmentData> data = this.jdbcTemplate.query(schema,
				mapper);

		return data;

	}

	@Override
	public List<LoanInvestmentData> retriveSavingAccountsByLoanId(Long loanId) {
		// TODO Auto-generated method stub
		final LoanInvestmentDataMapper mapper = new LoanInvestmentDataMapper();
		final String schema = " select " + mapper.loanAccountSchema()
				+ " where msi.loan_id = " + loanId + " group by msi.saving_id ";

		List<LoanInvestmentData> data = this.jdbcTemplate.query(schema, mapper);
		return data;
	}

	@Override
	public Long retriveSavingInvestmentId(Long savingId, Long loanId) {

		final String schema = "select ms.id from m_investment ms "
				+ " where ms.saving_id = " + savingId + " and ms.loan_id = "
				+ loanId;

		Long data = this.jdbcTemplate.queryForLong(schema);

		return data;
	}

	@Override
	public Long retriveLoanInvestmentId(Long loanId, Long savingId) {

		final String schema = "select ms.id from m_investment ms "
				+ " where ms.loan_id = " + loanId + " and ms.saving_id = "
				+ savingId;

		Long data = this.jdbcTemplate.queryForLong(schema);
		return data;

	}

	@Override
	public List<Long> retriveLoanIdBySavingId(Long savingId) {

		final SavingInvestmentDataMapper mapp = new SavingInvestmentDataMapper();
		final String schema = "select ms.loan_id from m_investment ms"
				+ " where ms.saving_id = " + savingId;

		List<Long> data = this.jdbcTemplate.queryForList(schema, null,
				Long.class);
		return data;
	}

	@Override
	public List<Long> retriveSavingIdByLoanId(Long loanId) {

		final String schema = " select ms.saving_id from m_investment ms "
				+ " where ms.loan_id = " + loanId;
		List<Long> data = this.jdbcTemplate.queryForList(schema, null,
				Long.class);

		return data;
	}

	private static final class SavingInvestmentDataMapper implements
			RowMapper<SavingInvestmentData> {

		public String savingAccountsSchema() {
			return "ml.id as loan_id, ml.account_no as accountno, cl.display_name as name, ml.approved_principal as loanammount, mpl.name as productname, ms.invested_amount as investedAmount"
					+ " from m_investment ms "
					+ " left join m_loan ml on ms.loan_id = ml.id left join m_client cl on ml.client_id = cl.id left join m_product_loan mpl on ml.product_id = mpl.id ";
		}

		/*
		 * public String loanAccountSchema(){ return
		 * "ms.loan_id from m_saving_investment ms"; }
		 */

		@Override
		public SavingInvestmentData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			final Long loan_id = rs.getLong("loan_id");
			final String accountno = rs.getString("accountno");
			final String name = rs.getString("name");
			final Long loanammount = rs.getLong("loanammount");
			final String productname = rs.getString("productname");
			final Long investedAmount = rs.getLong("investedAmount");

			List<SavingInvestmentData> savingInvestmentData = null;
			final SavingInvestmentData data = SavingInvestmentData.instance(
					loan_id, accountno, name, loanammount, productname, null,
					null, investedAmount);
			// TODO Auto-generated method stub
			return data;
		}

	}

	private static final class LoanInvestmentDataMapper implements
			RowMapper<LoanInvestmentData> {

		public String loanAccountSchema() {

			return "msi.saving_id as saving_id , mp.display_name as name, msp.name as productname, msa.account_no as accountno, msa.account_balance_derived as savingammount, msi.invested_amount as investedamount  from m_investment msi "
					+ " left join m_savings_account msa on msi.saving_id = msa.id "
					+ " left join m_savings_product msp on msa.product_id = msp.id left join m_group mp on msa.group_id = mp.id";
		}

		@Override
		public LoanInvestmentData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			final Long saving_id = rs.getLong("saving_id");
			final String accountno = rs.getString("accountno");
			final String name = rs.getString("name");
			final Long savingammount = rs.getLong("savingammount");
			final String productname = rs.getString("productname");
			final Long investedAmount = rs.getLong("investedamount");

			final LoanInvestmentData data = LoanInvestmentData.intance(
					saving_id, name, accountno, savingammount, productname,
					investedAmount);

			return data;
		}

	}

}
