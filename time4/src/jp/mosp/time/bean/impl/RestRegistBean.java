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
package jp.mosp.time.bean.impl;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import jp.mosp.framework.base.MospException;
import jp.mosp.framework.base.MospParams;
import jp.mosp.platform.base.PlatformBean;
import jp.mosp.time.bean.RestRegistBeanInterface;
import jp.mosp.time.constant.TimeConst;
import jp.mosp.time.dao.settings.RestDaoInterface;
import jp.mosp.time.dto.settings.AttendanceDtoInterface;
import jp.mosp.time.dto.settings.RestDtoInterface;
import jp.mosp.time.dto.settings.TimeSettingDtoInterface;
import jp.mosp.time.dto.settings.impl.TmdRestDto;
import jp.mosp.time.utils.TimeUtility;

/**
 * 勤怠データ休憩情報登録クラス。
 */
public class RestRegistBean extends PlatformBean implements RestRegistBeanInterface {
	
	/**
	 * 休憩マスタDAOクラス。<br>
	 */
	protected RestDaoInterface dao;
	
	
	/**
	 * {@link PlatformBean#PlatformBean()}を実行する。<br>
	 */
	public RestRegistBean() {
		super();
	}
	
	/**
	 * {@link PlatformBean#PlatformBean(MospParams, Connection)}を実行する。<br>
	 * @param mospParams MosPパラメータクラス
	 * @param connection DBコネクション
	 */
	public RestRegistBean(MospParams mospParams, Connection connection) {
		super(mospParams, connection);
	}
	
	@Override
	public void initBean() throws MospException {
		dao = (RestDaoInterface)createDao(RestDaoInterface.class);
	}
	
	@Override
	public RestDtoInterface getInitDto() {
		return new TmdRestDto();
	}
	
	@Override
	public void regist(RestDtoInterface dto) throws MospException {
		if (dao.findForKey(dto.getPersonalId(), dto.getWorkDate(), dto.getTimesWork(), dto.getRest()) == null) {
			// 新規登録
			insert(dto);
		} else {
			// 履歴追加
			update(dto);
		}
	}
	
	@Override
	public void insert(RestDtoInterface dto) throws MospException {
		// DTOの妥当性確認
		validate(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 新規登録情報の検証
		checkInsert(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// レコード識別ID最大値をインクリメントしてDTOに設定
		dto.setTmdRestId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	@Override
	public void update(RestDtoInterface dto) throws MospException {
		// DTOの妥当性確認
		validate(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 履歴更新情報の検証
		checkUpdate(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 論理削除
		logicalDelete(dao, dto.getTmdRestId());
		// レコード識別ID最大値をインクリメントしてDTOに設定
		dto.setTmdRestId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	@Override
	public void delete(String personalId, Date workDate, int timesWork) throws MospException {
		List<RestDtoInterface> list = dao.findForList(personalId, workDate, timesWork);
		for (RestDtoInterface dto : list) {
			checkDelete((RestDtoInterface)dao.findForKey(dto.getTmdRestId(), true));
			if (mospParams.hasErrorMessage()) {
				// エラーが存在したら削除処理をしない
				continue;
			}
			// 論理削除
			logicalDelete(dao, dto.getTmdRestId());
		}
	}
	
	@Override
	public void delete(String personalId, Date workDate, int timesWork, int rest) throws MospException {
		RestDtoInterface dto = dao.findForKey(personalId, workDate, timesWork, rest);
		checkDelete((RestDtoInterface)dao.findForKey(dto.getTmdRestId(), true));
		if (mospParams.hasErrorMessage()) {
			// エラーが存在したら削除処理をしない
			return;
		}
		// 論理削除
		logicalDelete(dao, dto.getTmdRestId());
	}
	
	/**
	 * 新規登録時の確認処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkInsert(RestDtoInterface dto) throws MospException {
		// 対象レコードの有効日が重複していないかを確認
		checkDuplicateInsert(dao.findForHistory(dto.getPersonalId(), dto.getWorkDate(), dto.getRest(),
				dto.getRestStart(), dto.getRestEnd()));
	}
	
	/**
	 * 履歴更新時の確認処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkUpdate(RestDtoInterface dto) throws MospException {
		// 対象レコード識別IDのデータが削除されていないかを確認
		checkExclusive(dao, dto.getTmdRestId());
	}
	
	/**
	 * 削除時の確認処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkDelete(RestDtoInterface dto) throws MospException {
		// 対象レコード識別IDのデータが削除されていないかを確認
		checkExclusive(dao, dto.getTmdRestId());
	}
	
	/**
	 * 登録情報の妥当性を確認する。
	 * @param dto 対象DTO
	 */
	protected void validate(RestDtoInterface dto) {
		// TODO 妥当性確認
	}
	
	@Override
	public int getCalcRestTime(Date restStartTime, Date restEndTime, TimeSettingDtoInterface timeSettingDto) {
		// 休憩開始時刻、休憩終了時刻、勤怠設定情報がない場合
		if (restStartTime == null || restEndTime == null || timeSettingDto == null) {
			return 0;
		}
		// 休憩開始時刻丸めた時間を取得
		Date roundRestStartTime = TimeUtility.getRoundMinute(restStartTime, timeSettingDto.getRoundDailyRestStart(),
				timeSettingDto.getRoundDailyRestStartUnit());
		// 休憩終了時刻丸めた時間を取得
		Date roundRestEndTime = TimeUtility.getRoundMinute(restEndTime, timeSettingDto.getRoundDailyRestEnd(),
				timeSettingDto.getRoundDailyRestEndUnit());
		// 休憩時間を丸めた時刻を取得
		return TimeUtility.getRoundMinute(TimeUtility.getDifferenceMinutes(roundRestStartTime, roundRestEndTime),
				timeSettingDto.getRoundDailyRestTime(), timeSettingDto.getRoundDailyRestTimeUnit());
	}
	
	@Override
	public void setRestStartEndTime(Date restStartTime, Date restEndTime, AttendanceDtoInterface attendanceDto,
			RestDtoInterface restDto) throws MospException {
		// 追加業務ロジック処理を行った場合
		if (doAdditionalLogic(TimeConst.CODE_KEY_ADD_RESTREGISTBEAN_SETRESTSTARTENDTIME, restStartTime, restEndTime,
				attendanceDto, restDto)) {
			// 通常の処理は行わない
			return;
		}
		// 休憩開始があり休憩終了がある場合
		if (restStartTime != null && restEndTime != null) {
			// 始業時刻が休憩開始時刻より後の場合
			if (attendanceDto.getStartTime() != null && attendanceDto.getStartTime().after(restStartTime)) {
				// 始業時刻を休憩開始時刻とする
				restStartTime = attendanceDto.getStartTime();
			}
			// 終業時刻が休憩終了時刻より前の場合
			if (attendanceDto.getEndTime() != null && attendanceDto.getEndTime().before(restEndTime)) {
				// 終業時刻を休憩終了時刻とする
				restEndTime = attendanceDto.getEndTime();
			}
			// 休憩終了時刻が休憩開始時刻より前の場合
			if (restEndTime.before(restStartTime)) {
				// 休憩開始時刻を休憩終了時刻とする
				restEndTime = restStartTime;
			}
			if (!restStartTime.equals(restEndTime)) {
				// 休憩開始時刻
				restDto.setRestStart(restStartTime);
				// 休憩終了時刻
				restDto.setRestEnd(restEndTime);
			}
		}
	}
}
