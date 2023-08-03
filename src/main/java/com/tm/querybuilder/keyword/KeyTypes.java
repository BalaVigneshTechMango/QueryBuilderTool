package com.tm.querybuilder.keyword;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessResourceFailureException;

import com.tm.querybuilder.constant.QueryConstants;
import com.tm.querybuilder.pojo.FilterDataPOJO;
import com.tm.querybuilder.pojo.OrderByPOJO;

public class KeyTypes {

	@Value("${query.limit}")
	private int limit;

	@Value("${query.limit}")
	private int offset;

	private static final Logger LOGGER = LoggerFactory.getLogger(KeyTypes.class);

	public String getLimit(FilterDataPOJO filterData) {
		StringBuilder querBuilder = new StringBuilder();
		try {
			if (filterData.getLimit() > 0) {
				querBuilder.append(QueryConstants.LIMIT).append(filterData.getLimit());
			} else {
				querBuilder.append(QueryConstants.LIMIT).append(limit);
			}
			if (filterData.getPageNo() > 0 && filterData.getLimit() > 0) {
				querBuilder.append(QueryConstants.OFFSET).append(filterData.getLimit() * (filterData.getPageNo() - 1));
			} else if (filterData.getPageNo() > 0) {
				querBuilder.append(QueryConstants.OFFSET).append(limit * (filterData.getPageNo() - 1));
			} else {
				querBuilder.append(QueryConstants.OFFSET).append(offset);
			}
		} catch (Exception exception) {
			LOGGER.error(" ** An error occurred while get limit and offset   **");
			throw new DataAccessResourceFailureException(" ** An error occurred while get limit and offset  **",
					exception);
		}
		LOGGER.debug("get limit and offset : {}", querBuilder);
		return querBuilder.toString();
	}

	public String getColumnOrderBy(List<OrderByPOJO> orderByPOJO) {
		StringBuilder columnBuilder = new StringBuilder();
		StringBuilder orderBy = new StringBuilder();
		try {
			orderBy.append(QueryConstants.ORDERBY);
			for (OrderByPOJO columnList : orderByPOJO) {
				if (!columnBuilder.isEmpty()) {
					columnBuilder.append(",");
				}
				columnBuilder.append(columnList.getOrderColumnName()).append(" ").append(columnList.getOrderType());
			}
			orderBy.append(columnBuilder.toString());
		} catch (Exception exception) {
			LOGGER.error(" ** An error occurred while get column order by service impl **");
			throw new DataAccessResourceFailureException(
					" ** An error occurred while get column order by service impl **", exception);
		}
		LOGGER.debug("get column order by  : {}", orderBy);
		return orderBy.toString();
	}

}
