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
package jp.mosp.time.bean.impl;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import jp.mosp.framework.base.MospException;
import jp.mosp.framework.base.MospParams;
import jp.mosp.platform.base.PlatformBean;
import jp.mosp.time.base.TimeBean;
import jp.mosp.time.bean.GoOutReferenceBeanInterface;
import jp.mosp.time.constant.TimeConst;
import jp.mosp.time.dao.settings.GoOutDaoInterface;
import jp.mosp.time.dto.settings.GoOutDtoInterface;

/**
 * 勤怠データ外出情報参照クラス。
 */
public class GoOutReferenceBean extends TimeBean implements GoOutReferenceBeanInterface {
	
	/**
	 *  勤怠データ外出情報マスタDAOクラス。<br>
	 */
	GoOutDaoInterface dao;
	
	
	/**
	 * {@link PlatformBean#PlatformBean()}を実行する。<br>
	 */
	public GoOutReferenceBean() {
		super();
	}
	
	/**
	 * {@link PlatformBean#PlatformBean(MospParams, Connection)}を実行する。<br>
	 * @param mospParams MosPパラメータクラス
	 * @param connection DBコネクション
	 */
	public GoOutReferenceBean(MospParams mospParams, Connection connection) {
		super(mospParams, connection);
	}
	
	@Override
	public void initBean() throws MospException {
		dao = (GoOutDaoInterface)createDao(GoOutDaoInterface.class);
	}
	
	@Override
	public GoOutDtoInterface findForKey(String personalId, Date workDate, int timesWork, int type, int times)
			throws MospException {
		return dao.findForKey(personalId, workDate, timesWork, type, times);
	}
	
	@Override
	public List<GoOutDtoInterface> getGoOutList(String personalId, Date workDate, int timesWork, int goOutType)
			throws MospException {
		return dao.findForList(personalId, workDate, timesWork, goOutType);
	}
	
	@Override
	public List<GoOutDtoInterface> getGoOutTypeList(String personalId, Date workDate, int goOutType)
			throws MospException {
		return dao.findForHistoryList(personalId, workDate, goOutType);
	}
	
	@Override
	public List<GoOutDtoInterface> getPrivateGoOutList(String personalId, Date workDate) throws MospException {
		return getGoOutList(personalId, workDate, TIMES_WORK_DEFAULT, TimeConst.CODE_GO_OUT_PRIVATE);
	}
	
	@Override
	public List<GoOutDtoInterface> getPublicGoOutList(String personalId, Date workDate) throws MospException {
		return getGoOutList(personalId, workDate, TIMES_WORK_DEFAULT, TimeConst.CODE_GO_OUT_PUBLIC);
	}
	
	@Override
	public List<GoOutDtoInterface> getMinutelyHolidayAList(String personalId, Date workDate) throws MospException {
		return getGoOutList(personalId, workDate, TIMES_WORK_DEFAULT, TimeConst.CODE_GO_OUT_MINUTELY_HOLIDAY_A);
	}
	
	@Override
	public List<GoOutDtoInterface> getMinutelyHolidayBList(String personalId, Date workDate) throws MospException {
		return getGoOutList(personalId, workDate, TIMES_WORK_DEFAULT, TimeConst.CODE_GO_OUT_MINUTELY_HOLIDAY_B);
	}
	
}
