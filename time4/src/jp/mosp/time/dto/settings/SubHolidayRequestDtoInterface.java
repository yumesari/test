/*
 * MosP - Mind Open Source Project    http://www.mosp.jp/
 * Copyright (C) MIND Co., Ltd.       http://www.e-mind.co.jp/
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package jp.mosp.time.dto.settings;

import java.util.Date;

import jp.mosp.framework.base.BaseDtoInterface;
import jp.mosp.platform.dto.base.RequestDtoInterface;
import jp.mosp.time.dto.base.HolidayRangeDtoInterface;

/**
 * 代休申請DTOインターフェース
 */
public interface SubHolidayRequestDtoInterface extends BaseDtoInterface, RequestDtoInterface, HolidayRangeDtoInterface {
	
	/**
	 * @return レコード識別ID。
	 */
	long getTmdSubHolidayRequestId();
	
	/**
	 * @return 代休日。
	 */
	Date getRequestDate();
	
	/**
	 * @return 出勤日。
	 */
	Date getWorkDate();
	
	/**
	 * @return 勤務回数。
	 */
	int getTimesWork();
	
	/**
	 * @return 代休種別。
	 */
	int getWorkDateSubHolidayType();
	
	/**
	 * @param tmdSubHolidayRequestId セットする レコード識別ID。
	 */
	void setTmdSubHolidayRequestId(long tmdSubHolidayRequestId);
	
	/**
	 * @param requestDate セットする 代休日。
	 */
	void setRequestDate(Date requestDate);
	
	/**
	 * @param workDate セットする 出勤日。
	 */
	void setWorkDate(Date workDate);
	
	/**
	 * @param timesWork セットする 勤務回数。
	 */
	void setTimesWork(int timesWork);
	
	/**
	 * @param workDateSubHolidayType セットする 代休種別。
	 */
	void setWorkDateSubHolidayType(int workDateSubHolidayType);
	
}
