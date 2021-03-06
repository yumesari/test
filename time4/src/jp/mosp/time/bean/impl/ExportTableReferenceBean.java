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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.mosp.framework.base.MospException;
import jp.mosp.framework.base.MospParams;
import jp.mosp.framework.constant.MospConst;
import jp.mosp.framework.utils.DateUtility;
import jp.mosp.framework.utils.MospUtility;
import jp.mosp.platform.base.PlatformBean;
import jp.mosp.platform.bean.human.HumanSearchBeanInterface;
import jp.mosp.platform.bean.human.RetirementReferenceBeanInterface;
import jp.mosp.platform.bean.system.SectionReferenceBeanInterface;
import jp.mosp.platform.bean.workflow.WorkflowReferenceBeanInterface;
import jp.mosp.platform.constant.PlatformConst;
import jp.mosp.platform.constant.PlatformFileConst;
import jp.mosp.platform.dao.file.ExportDaoInterface;
import jp.mosp.platform.dao.file.ExportFieldDaoInterface;
import jp.mosp.platform.dao.human.impl.PfmHumanDao;
import jp.mosp.platform.dto.file.ExportDtoInterface;
import jp.mosp.platform.dto.file.ExportFieldDtoInterface;
import jp.mosp.platform.dto.human.HumanDtoInterface;
import jp.mosp.platform.dto.workflow.WorkflowDtoInterface;
import jp.mosp.platform.utils.MonthUtility;
import jp.mosp.platform.utils.WorkflowUtility;
import jp.mosp.time.base.TimeBean;
import jp.mosp.time.bean.AttendanceListReferenceBeanInterface;
import jp.mosp.time.bean.CutoffUtilBeanInterface;
import jp.mosp.time.bean.ExportTableReferenceBeanInterface;
import jp.mosp.time.bean.PaidHolidayDataReferenceBeanInterface;
import jp.mosp.time.bean.PaidHolidayTransactionReferenceBeanInterface;
import jp.mosp.time.bean.TimeRecordReferenceBeanInterface;
import jp.mosp.time.constant.TimeConst;
import jp.mosp.time.constant.TimeFileConst;
import jp.mosp.time.dao.settings.ExportDataDaoInterface;
import jp.mosp.time.dao.settings.impl.TmdAttendanceDao;
import jp.mosp.time.dao.settings.impl.TmdHolidayDataDao;
import jp.mosp.time.dao.settings.impl.TmdPaidHolidayDao;
import jp.mosp.time.dao.settings.impl.TmdStockHolidayDao;
import jp.mosp.time.dao.settings.impl.TmdTotalTimeDao;
import jp.mosp.time.dto.settings.CutoffDtoInterface;
import jp.mosp.time.dto.settings.HolidayRequestDtoInterface;
import jp.mosp.time.dto.settings.PaidHolidayDataDtoInterface;
import jp.mosp.time.dto.settings.PaidHolidayTransactionDtoInterface;
import jp.mosp.time.dto.settings.TimeRecordDtoInterface;
import jp.mosp.time.dto.settings.TimeSettingDtoInterface;
import jp.mosp.time.dto.settings.impl.AttendanceListDto;
import jp.mosp.time.portal.bean.impl.PortalTimeCardBean;
import jp.mosp.time.utils.TimeUtility;

/**
 * エクスポートテーブル参照クラス。
 */
public class ExportTableReferenceBean extends PlatformBean implements ExportTableReferenceBeanInterface {
	
	/**
	 * エクスポートコード。
	 */
	protected String										exportCode;
	
	/**
	 * 開始年。
	 */
	protected int											startYear;
	
	/**
	 * 開始月。
	 */
	protected int											startMonth;
	
	/**
	 * 終了年。
	 */
	protected int											endYear;
	
	/**
	 * 終了月。
	 */
	protected int											endMonth;
	
	/**
	 * 締日コード。
	 */
	protected String										cutoffCode;
	
	/**
	 * 勤務地コード。
	 */
	protected String										workPlaceCode;
	
	/**
	 * 雇用契約コード。
	 */
	protected String										employmentCode;
	
	/**
	 * 所属コード。
	 */
	protected String										sectionCode;
	
	/**
	 * 職位コード。
	 */
	protected String										positionCode;
	
	/**
	 * 下位所属含むチェックボックス。
	 */
	protected int											ckbNeedLowerSection;
	
	/**
	 * エクスポートマスタDAO。
	 */
	private ExportDaoInterface								exportDao;
	
	/**
	 * エクスポートフィールドマスタDAO。
	 */
	private ExportFieldDaoInterface							exportFieldDao;
	
	/**
	 * エクスポートデータDAO。
	 */
	private ExportDataDaoInterface							exportDataDao;
	
	/**
	 * 締日ユーティリティクラス。
	 */
	private CutoffUtilBeanInterface							cutoffUtil;
	
	/**
	 * 勤怠一覧参照クラス。
	 */
	private AttendanceListReferenceBeanInterface			attendanceList;
	
	/**
	 * 打刻データ参照クラス。
	 */
	private TimeRecordReferenceBeanInterface				timeRecord;
	
	/**
	 * 人事マスタ検索クラス。<br>
	 */
	protected HumanSearchBeanInterface						humanSearch;
	
	/**
	 * 所属マスタ参照クラス。<br>
	 */
	protected SectionReferenceBeanInterface					section;
	
	/**
	 * 休暇申請情報参照クラス。<br>
	 */
	protected HolidayRequestReferenceBean					holidayRequestReference;
	
	/**
	 * 有給休暇付与情報参照クラス。<br>
	 */
	protected PaidHolidayDataReferenceBeanInterface			paidHolidayDataReference;
	
	/**
	 * ワークフロー情報参照クラス。<br>
	 */
	protected WorkflowReferenceBeanInterface				workflowReference;
	
	/**
	 * 有給休暇手動付与参照クラス。<br>
	 */
	protected PaidHolidayTransactionReferenceBeanInterface	paidHolidayTransactionReference;
	
	/**
	 * 人事退社情報参照クラス。
	 */
	RetirementReferenceBeanInterface						retirementReference;
	
	/**
	 * MosPアプリケーション設定キー(エクスポート時間フォーマット)。
	 */
	protected static final String							APP_EXPORT_TIME_FORMAT	= "ExportTimeFormat";
	
	/**
	 * 操作時間数(-1年)
	 */
	protected static final int								addPrevious				= -1;
	
	
	/**
	 * {@link PlatformBean#PlatformBean()}を実行する。<br>
	 */
	public ExportTableReferenceBean() {
		super();
	}
	
	/**
	 * {@link PlatformBean#PlatformBean(MospParams, Connection)}を実行する。<br>
	 * @param mospParams MosPパラメータクラス
	 * @param connection DBコネクション
	 */
	public ExportTableReferenceBean(MospParams mospParams, Connection connection) {
		super(mospParams, connection);
	}
	
	@Override
	public void initBean() throws MospException {
		exportDao = (ExportDaoInterface)createDao(ExportDaoInterface.class);
		exportFieldDao = (ExportFieldDaoInterface)createDao(ExportFieldDaoInterface.class);
		exportDataDao = (ExportDataDaoInterface)createDao(ExportDataDaoInterface.class);
		cutoffUtil = (CutoffUtilBeanInterface)createBean(CutoffUtilBeanInterface.class);
		attendanceList = (AttendanceListReferenceBeanInterface)createBean(AttendanceListReferenceBeanInterface.class);
		timeRecord = (TimeRecordReferenceBeanInterface)createBean(TimeRecordReferenceBeanInterface.class);
		humanSearch = (HumanSearchBeanInterface)createBean(HumanSearchBeanInterface.class);
		section = (SectionReferenceBeanInterface)createBean(SectionReferenceBeanInterface.class);
		holidayRequestReference = (HolidayRequestReferenceBean)createBean(HolidayRequestReferenceBean.class);
		paidHolidayDataReference = (PaidHolidayDataReferenceBeanInterface)createBean(
				PaidHolidayDataReferenceBeanInterface.class);
		workflowReference = (WorkflowReferenceBeanInterface)createBean(WorkflowReferenceBeanInterface.class);
		paidHolidayTransactionReference = (PaidHolidayTransactionReferenceBeanInterface)createBean(
				PaidHolidayTransactionReferenceBeanInterface.class);
		retirementReference = (RetirementReferenceBeanInterface)createBean(RetirementReferenceBeanInterface.class);
	}
	
	@Override
	public List<String[]> export() throws MospException {
		// エクスポート情報取得
		ExportDtoInterface exportDto = exportDao.findForKey(exportCode);
		// 情報確認
		if (exportDto == null) {
			return null;
		}
		// エクスポートフィールドマスタリストを取得
		List<ExportFieldDtoInterface> exportFieldDtoList = exportFieldDao.findForList(exportCode);
		// リスト確認
		if (exportFieldDtoList.isEmpty()) {
			return null;
		}
		// エクスポート時間フォーマット区分
		int format = getExportTimeFormat();
		// リスト準備
		List<String[]> list = new ArrayList<String[]>();
		// ヘッダが有りの場合
		if (exportDto.getHeader() == 1) {
			// ヘッダリスト準備
			List<String> headerList = new ArrayList<String>();
			// エクスポートフィールドマスタリスト毎に処理
			for (ExportFieldDtoInterface exportFieldDto : exportFieldDtoList) {
				// ヘッダ名設定
				headerList.add(mospParams.getProperties().getCodeItemName(exportDto.getExportTable(),
						exportFieldDto.getFieldName()));
			}
			// 1行目にヘッダ名列追加
			list.add(headerList.toArray(new String[0]));
		}
		// 対象年月及び締日から締期間初日及び最終日を取得
		Date startDate = cutoffUtil.getCutoffFirstDate(cutoffCode, startYear, startMonth);
		Date endDate = cutoffUtil.getCutoffLastDate(cutoffCode, endYear, endMonth);
		// 対象日取得
		Date firstTargetDate = MonthUtility.getYearMonthTargetDate(startYear, startMonth, mospParams);
		// SQL準備
		ResultSet rs = null;
		// データ区分確認フラグ準備
		boolean isAttendance = TimeFileConst.CODE_EXPORT_TYPE_TMD_ATTENDANCE.equals(exportDto.getExportTable());
		boolean isTotalTime = TimeFileConst.CODE_EXPORT_TYPE_TMD_TOTAL_TIME.equals(exportDto.getExportTable());
		boolean isPaidHoliday = TimeFileConst.CODE_EXPORT_TYPE_TMD_PAID_HOLIDAY.equals(exportDto.getExportTable());
		boolean isStockHoliday = TimeFileConst.CODE_EXPORT_TYPE_TMD_STOCK_HOLIDAY.equals(exportDto.getExportTable());
		boolean isHolidayData = TimeFileConst.CODE_EXPORT_TYPE_TMD_HOLIDAY.equals(exportDto.getExportTable());
		boolean isAttendanceBook = TimeFileConst.CODE_EXPORT_TYPE_ATTENDANCE_BOOK.equals(exportDto.getExportTable());
		if (isAttendance) {
			// 勤怠データ
			rs = exportDataDao.findForAttendance(startDate, endDate, cutoffCode, workPlaceCode, employmentCode,
					sectionCode, ckbNeedLowerSection, positionCode);
		}
		if (isTotalTime) {
			// 勤怠集計データ
			rs = exportDataDao.findForTotalTime(startDate, endDate, cutoffCode, workPlaceCode, employmentCode,
					sectionCode, ckbNeedLowerSection, positionCode);
		}
		if (isPaidHoliday) {
			// 有給休暇データ
			rs = exportDataDao.findForPaidHoliday(startDate, endDate, cutoffCode, workPlaceCode, employmentCode,
					sectionCode, ckbNeedLowerSection, positionCode);
		}
		if (isStockHoliday) {
			// ストック休暇データ
			rs = exportDataDao.findForStockHoliday(startDate, endDate, cutoffCode, workPlaceCode, employmentCode,
					sectionCode, ckbNeedLowerSection, positionCode);
		}
		if (isHolidayData) {
			// 休暇データ
			rs = exportDataDao.findForHolidayData(startDate, endDate, cutoffCode, workPlaceCode, employmentCode,
					sectionCode, ckbNeedLowerSection, positionCode);
		}
		// 出勤簿データ
		if (isAttendanceBook) {
			// 人事情報検索条件設定(在職)
			humanSearch.setStartDate(startDate);
			humanSearch.setEndDate(endDate);
			humanSearch.setTargetDate(endDate);
			humanSearch.setWorkPlaceCode(workPlaceCode);
			humanSearch.setEmploymentContractCode(employmentCode);
			humanSearch.setSectionCode(sectionCode);
			humanSearch.setPositionCode(positionCode);
			// 検索条件設定(状態)
			humanSearch.setStateType(PlatformConst.EMPLOYEE_STATE_PRESENCE);
			// 検索条件設定(下位所属要否) 下位所属含むチェックボックスで判定
			if (ckbNeedLowerSection == 1) {
				humanSearch.setNeedLowerSection(true);
			} else {
				humanSearch.setNeedLowerSection(false);
			}
			// 検索条件設定(兼務要否)
			humanSearch.setNeedConcurrent(false);
			// 検索条件設定(操作区分)
			humanSearch.setOperationType(MospConst.OPERATION_TYPE_REFER);
			// 人事情報検索(在職)
			List<HumanDtoInterface> presenceHumanList = humanSearch.search();
			
			// 人事情報検索条件設定(休職)
			// 検索条件設定(状態)
			humanSearch.setStateType(PlatformConst.EMPLOYEE_STATE_SUSPEND);
			// 人事情報検索(休職)
			List<HumanDtoInterface> suspendHumanList = humanSearch.search();
			
			// 人事情報検索(在職+休職)
			List<HumanDtoInterface> humanList = new ArrayList<HumanDtoInterface>();
			humanList.addAll(presenceHumanList);
			humanList.addAll(suspendHumanList);
			
			// 人事情報確認
			if (humanList.isEmpty()) {
				return list;
			}
			// 人事情報毎に処理
			for (HumanDtoInterface humanDto : humanList) {
				// 締日コードがある場合
				if (!cutoffCode.isEmpty()) {
					// 締日情報取得
					CutoffDtoInterface cutoff = cutoffUtil.getCutoffForPersonalId(humanDto.getPersonalId(), endDate);
					// 締日がない又は同じでない場合
					if (cutoff == null || !cutoffCode.equals(cutoff.getCutoffCode())) {
						// エラーメッセージ削除
						mospParams.getErrorMessageList().clear();
						continue;
					}
				}
				// 対象日の設定
				Date targetDate = startDate;
				int idx = 0;
				// 締期間毎に処理
				while (endDate.after(targetDate)) {
					// 対象月に対する月操作
					targetDate = DateUtility.addMonth(startDate, idx);
					idx++;
					// 対象日が最終日を超えていた場合
					if (endDate.compareTo(targetDate) < 0) {
						break;
					}
					// 実績一覧を取得
					List<AttendanceListDto> actualList = attendanceList.getActualList(humanDto.getPersonalId(),
							targetDate);
					// 実績一覧確認
					if (actualList == null || actualList.isEmpty()) {
						// エラーメッセージ削除
						mospParams.getErrorMessageList().clear();
						continue;
					}
					// 対象社員出力リスト取得
					List<String[]> AttandanceBookList = getFieldValue(humanDto, exportFieldDtoList, actualList);
					// CSVデータをCSVデータリストに追加
					list.addAll(AttandanceBookList);
				}
			}
			exportDataDao.closers();
			return list;
		}
		try {
			while (rs != null && rs.next()) {
				List<String> dataList = new ArrayList<String>();
				if (!cutoffCode.isEmpty()) {
					String personalId = "";
					if (isAttendance) {
						// 勤怠データ
						personalId = rs.getString(TmdAttendanceDao.COL_PERSONAL_ID);
						firstTargetDate = rs.getDate(TmdAttendanceDao.COL_WORK_DATE);
					}
					if (isTotalTime) {
						// 勤怠集計データ
						personalId = rs.getString(TmdTotalTimeDao.COL_PERSONAL_ID);
						firstTargetDate = rs.getDate(TmdTotalTimeDao.COL_CALCULATION_DATE);
					}
					if (isPaidHoliday) {
						// 有給休暇データ
						personalId = rs.getString(TmdPaidHolidayDao.COL_PERSONAL_ID);
						firstTargetDate = rs.getDate(TmdPaidHolidayDao.COL_ACTIVATE_DATE);
						// 退職日取得
						Date retirementDate = retirementReference.getRetireDate(personalId);
						if (retirementDate != null
								&& DateUtility.isTermContain(firstTargetDate, retirementDate, null)) {
							// 退職日が存在し、有効日が退職日より後の場合
							continue;
						}
					}
					if (isStockHoliday) {
						// ストック休暇データ
						personalId = rs.getString(TmdStockHolidayDao.COL_PERSONAL_ID);
						firstTargetDate = rs.getDate(TmdStockHolidayDao.COL_ACTIVATE_DATE);
					}
					if (isHolidayData) {
						// 休暇データ
						personalId = rs.getString(TmdHolidayDataDao.COL_PERSONAL_ID);
						firstTargetDate = rs.getDate(TmdHolidayDataDao.COL_ACTIVATE_DATE);
					}
					// 勤怠設定情報を取得
					TimeSettingDtoInterface timeSettingDto = cutoffUtil.getTimeSettingNoMessage(personalId,
							firstTargetDate);
					if (timeSettingDto == null) {
						continue;
					}
					if (!cutoffCode.equals(timeSettingDto.getCutoffCode())) {
						continue;
					}
				}
				// エクスポートフィールドマスタリスト毎に処理
				for (ExportFieldDtoInterface exportFieldDto : exportFieldDtoList) {
					String fieldName = exportFieldDto.getFieldName();
					// 個人ID及び対象日準備
					String personalId = "";
					Date targetDate = null;
					// 個人ID及び対象日設定
					if (isAttendance) {
						// 勤怠データ
						personalId = rs.getString(TmdAttendanceDao.COL_PERSONAL_ID);
						targetDate = rs.getDate(TmdAttendanceDao.COL_WORK_DATE);
					}
					if (isTotalTime) {
						// 勤怠集計データ
						personalId = rs.getString(TmdTotalTimeDao.COL_PERSONAL_ID);
						targetDate = rs.getDate(TmdTotalTimeDao.COL_CALCULATION_DATE);
					}
					if (isPaidHoliday) {
						// 有給休暇データ
						personalId = rs.getString(TmdPaidHolidayDao.COL_PERSONAL_ID);
						targetDate = rs.getDate(TmdPaidHolidayDao.COL_ACTIVATE_DATE);
					}
					if (isStockHoliday) {
						// ストック休暇データ
						personalId = rs.getString(TmdStockHolidayDao.COL_PERSONAL_ID);
						targetDate = rs.getDate(TmdStockHolidayDao.COL_ACTIVATE_DATE);
					}
					if (isHolidayData) {
						// 休暇データ
						personalId = rs.getString(TmdHolidayDataDao.COL_PERSONAL_ID);
						targetDate = rs.getDate(TmdHolidayDataDao.COL_ACTIVATE_DATE);
					}
					if (isAttendance) {
						// 勤怠データ
						if (TmdAttendanceDao.COL_WORK_DATE.equals(fieldName)) {
							// 勤務日
							dataList.add(DateUtility.getStringDate(rs.getDate(fieldName)));
							continue;
						}
						// 始業時刻（丸め打刻）
						if (TmdAttendanceDao.COL_START_TIME.equals(fieldName)) {
							// 丸め時刻追加
							dataList.add(
									DateUtility.getStringDateAndTime(rs.getTimestamp(TmdAttendanceDao.COL_START_TIME)));
							continue;
						}
						// 始業時刻(実打刻)
						if (TmdAttendanceDao.COL_ACTUAL_START_TIME.equals(fieldName)) {
							// 実打刻追加
							dataList.add(DateUtility
								.getStringDateAndTime(rs.getTimestamp(TmdAttendanceDao.COL_ACTUAL_START_TIME)));
							continue;
						}
						// 始業時刻(ポータル打刻)
						if (TimeFileConst.FIELD_TIME_ROCODE_START_TIME.equals(fieldName)) {
							String timeRecodeStartTime = "";
							// 打刻データを取得
							TimeRecordDtoInterface recodeDto = timeRecord.findForKey(personalId, targetDate,
									TimeBean.TIMES_WORK_DEFAULT, PortalTimeCardBean.RECODE_START_WORK);
							// 打刻データがある場合
							if (recodeDto != null) {
								// 打刻データ取得
								timeRecodeStartTime = DateUtility.getStringDateAndTime(recodeDto.getRecordTime());
							}
							// 打刻データ追加
							dataList.add(timeRecodeStartTime);
							continue;
						}
						// 終業時刻（丸め打刻）
						if (TmdAttendanceDao.COL_END_TIME.equals(fieldName)) {
							dataList
								.add(DateUtility.getStringDateAndTime(rs.getTimestamp(TmdAttendanceDao.COL_END_TIME)));
							continue;
						}
						// 終業時刻(実打刻)
						if (TmdAttendanceDao.COL_ACTUAL_END_TIME.equals(fieldName)) {
							// 実打刻追加
							dataList.add(DateUtility
								.getStringDateAndTime(rs.getTimestamp(TmdAttendanceDao.COL_ACTUAL_END_TIME)));
							continue;
						}
						// 終業時刻(ポータル打刻)
						if (TimeFileConst.FIELD_TIME_ROCODE_END_TIME.equals(fieldName)) {
							String timeRecodeStartTime = "";
							// 打刻データを取得
							TimeRecordDtoInterface recodeDto = timeRecord.findForKey(personalId, targetDate,
									TimeBean.TIMES_WORK_DEFAULT, PortalTimeCardBean.RECODE_END_WORK);
							// 打刻データがある場合
							if (recodeDto != null) {
								timeRecodeStartTime = DateUtility.getStringDateAndTime(recodeDto.getRecordTime());
							}
							// 打刻データ取得
							dataList.add(timeRecodeStartTime);
							continue;
						}
						if (
						// 遅刻時間
						TmdAttendanceDao.COL_LATE_TIME.equals(fieldName)
								// 実遅刻時間
								|| TmdAttendanceDao.COL_ACTUAL_LATE_TIME.equals(fieldName)
								// 遅刻30分以上時間
								|| TmdAttendanceDao.COL_LATE_THIRTY_MINUTES_OR_MORE_TIME.equals(fieldName)
								// 遅刻30分未満時間
								|| TmdAttendanceDao.COL_LATE_LESS_THAN_THIRTY_MINUTES_TIME.equals(fieldName)
								// 早退時間
								|| TmdAttendanceDao.COL_LEAVE_EARLY_TIME.equals(fieldName)
								// 実早退時間
								|| TmdAttendanceDao.COL_ACTUAL_LEAVE_EARLY_TIME.equals(fieldName)
								// 早退30分以上時間
								|| TmdAttendanceDao.COL_LEAVE_EARLY_THIRTY_MINUTES_OR_MORE_TIME.equals(fieldName)
								// 早退30分未満時間
								|| TmdAttendanceDao.COL_LEAVE_EARLY_LESS_THAN_THIRTY_MINUTES_TIME.equals(fieldName)
								// 勤務時間
								|| TmdAttendanceDao.COL_WORK_TIME.equals(fieldName)
								// 所定労働時間
								|| TmdAttendanceDao.COL_GENERAL_WORK_TIME.equals(fieldName)
								// 所定労働時間内労働時間
								|| TmdAttendanceDao.COL_WORK_TIME_WITHIN_PRESCRIBED_WORK_TIME.equals(fieldName)
								// 契約勤務時間
								|| TmdAttendanceDao.COL_CONTRACT_WORK_TIME.equals(fieldName)
								// 無給時短時間
								|| TmdAttendanceDao.COL_SHORT_UNPAID.equals(fieldName)
								// 休憩時間
								|| TmdAttendanceDao.COL_REST_TIME.equals(fieldName)
								// 法定外休憩時間
								|| TmdAttendanceDao.COL_OVER_REST_TIME.equals(fieldName)
								// 深夜休憩時間
								|| TmdAttendanceDao.COL_NIGHT_REST_TIME.equals(fieldName)
								// 法定休出休憩時間
								|| TmdAttendanceDao.COL_LEGAL_HOLIDAY_REST_TIME.equals(fieldName)
								// 所定休出休憩時間
								|| TmdAttendanceDao.COL_PRESCRIBED_HOLIDAY_REST_TIME.equals(fieldName)
								// 公用外出時間
								|| TmdAttendanceDao.COL_PUBLIC_TIME.equals(fieldName)
								// 私用外出時間
								|| TmdAttendanceDao.COL_PRIVATE_TIME.equals(fieldName)
								// 分単位休暇A時間
								|| TmdAttendanceDao.COL_MINUTELY_HOLIDAY_A_TIME.equals(fieldName)
								// 分単位休暇B時間
								|| TmdAttendanceDao.COL_MINUTELY_HOLIDAY_B_TIME.equals(fieldName)
								// 残業時間
								|| TmdAttendanceDao.COL_OVERTIME.equals(fieldName)
								// 前残業時間
								|| TmdAttendanceDao.COL_OVERTIME_BEFORE.equals(fieldName)
								// 後残業時間
								|| TmdAttendanceDao.COL_OVERTIME_AFTER.equals(fieldName)
								// 法定内残業時間
								|| TmdAttendanceDao.COL_OVERTIME_IN.equals(fieldName)
								// 法定外残業時間
								|| TmdAttendanceDao.COL_OVERTIME_OUT.equals(fieldName)
								// 平日法定時間内残業時間
								|| TmdAttendanceDao.COL_WORKDAY_OVERTIME_IN.equals(fieldName)
								// 平日法定時間外残業時間
								|| TmdAttendanceDao.COL_WORKDAY_OVERTIME_OUT.equals(fieldName)
								// 所定休日法定時間内残業時間
								|| TmdAttendanceDao.COL_PRESCRIBED_HOLIDAY_OVERTIME_IN.equals(fieldName)
								// 所定休日法定時間外残業時間
								|| TmdAttendanceDao.COL_PRESCRIBED_HOLIDAY_OVERTIME_OUT.equals(fieldName)
								// 深夜勤務時間
								|| TmdAttendanceDao.COL_LATE_NIGHT_TIME.equals(fieldName)
								// 深夜所定労働時間内時間
								|| TmdAttendanceDao.COL_NIGHT_WORK_WITHIN_PRESCRIBED_WORK.equals(fieldName)
								// 深夜時間外時間
								|| TmdAttendanceDao.COL_NIGHT_OVERTIME_WORK.equals(fieldName)
								// 深夜休日労働時間
								|| TmdAttendanceDao.COL_NIGHT_WORK_ON_HOLIDAY.equals(fieldName)
								// 所定休日勤務時間
								|| TmdAttendanceDao.COL_SPECIFIC_WORK_TIME.equals(fieldName)
								// 法定休日勤務時間
								|| TmdAttendanceDao.COL_LEGAL_WORK_TIME.equals(fieldName)
								// 減額対象時間
								|| TmdAttendanceDao.COL_DECREASE_TIME.equals(fieldName)
								// 法定休出時間(代休あり)
								|| TmdAttendanceDao.COL_LEGAL_HOLIDAY_WORK_TIME_WITH_COMPENSATION_DAY.equals(fieldName)
								// 法定休出時間(代休なし)
								|| TmdAttendanceDao.COL_LEGAL_HOLIDAY_WORK_TIME_WITHOUT_COMPENSATION_DAY
									.equals(fieldName)
								// 所定休出時間(代休あり)
								|| TmdAttendanceDao.COL_PRESCRIBED_HOLIDAY_WORK_TIME_WITH_COMPENSATION_DAY
									.equals(fieldName)
								// 所定休出時間(代休なし)
								|| TmdAttendanceDao.COL_PRESCRIBED_HOLIDAY_WORK_TIME_WITHOUT_COMPENSATION_DAY
									.equals(fieldName)
								// 法定労働時間内残業時間(代休あり)
								|| TmdAttendanceDao.COL_OVERTIME_IN_WITH_COMPENSATION_DAY.equals(fieldName)
								// 法定労働時間内残業時間(代休なし)
								|| TmdAttendanceDao.COL_OVERTIME_IN_WITHOUT_COMPENSATION_DAY.equals(fieldName)
								// 法定労働時間外残業時間(代休あり)
								|| TmdAttendanceDao.COL_OVERTIME_OUT_WITH_COMPENSATION_DAY.equals(fieldName)
								// 法定労働時間外残業時間(代休なし)
								|| TmdAttendanceDao.COL_OVERTIME_OUT_WITHOUT_COMPENSATION_DAY.equals(fieldName)
								// 所定労働時間内法定休日労働時間
								|| TmdAttendanceDao.COL_STATUTORY_HOLIDAY_WORK_TIME_IN.equals(fieldName)
								// 所定労働時間外法定休日労働時間
								|| TmdAttendanceDao.COL_STATUTORY_HOLIDAY_WORK_TIME_OUT.equals(fieldName)
								// 所定労働時間内所定休日労働時間
								|| TmdAttendanceDao.COL_PRESCRIBED_HOLIDAY_WORK_TIME_IN.equals(fieldName)
								// 所定労働時間外所定休日労働時間
								|| TmdAttendanceDao.COL_PRESCRIBED_HOLIDAY_WORK_TIME_OUT.equals(fieldName)) {
							// 項目追加
							dataList.add(getExportTime(rs.getInt(fieldName), format));
							continue;
						}
					}
					// TODO
					if (isTotalTime) {
						// 勤怠集計データ
						if (TmdTotalTimeDao.COL_CALCULATION_DATE.equals(fieldName)) {
							// 集計日
							dataList.add(DateUtility.getStringDate(rs.getDate(fieldName)));
							continue;
						}
						if (
						// 勤務時間
						TmdTotalTimeDao.COL_WORK_TIME.equals(fieldName)
								// 所定勤務時間
								|| TmdTotalTimeDao.COL_SPECIFIC_WORK_TIME.equals(fieldName)
								// 契約勤務時間
								|| TmdTotalTimeDao.COL_CONTRACT_WORK_TIME.equals(fieldName)
								// 無給時短時間
								|| TmdTotalTimeDao.COL_SHORT_UNPAID.equals(fieldName)
								// 休憩時間
								|| TmdTotalTimeDao.COL_REST_TIME.equals(fieldName)
								// 深夜休憩時間
								|| TmdTotalTimeDao.COL_REST_LATE_NIGHT.equals(fieldName)
								// 所定休出休憩時間
								|| TmdTotalTimeDao.COL_REST_WORK_ON_SPECIFIC_HOLIDAY.equals(fieldName)
								// 法定休出休憩時間
								|| TmdTotalTimeDao.COL_REST_WORK_ON_HOLIDAY.equals(fieldName)
								// 公用外出時間
								|| TmdTotalTimeDao.COL_PUBLIC_TIME.equals(fieldName)
								// 私用外出時間
								|| TmdTotalTimeDao.COL_PRIVATE_TIME.equals(fieldName)
								// 分単位休暇A時間
								|| TmdTotalTimeDao.COL_MINUTELY_HOLIDAY_A_TIME.equals(fieldName)
								// 分単位休暇B時間
								|| TmdTotalTimeDao.COL_MINUTELY_HOLIDAY_B_TIME.equals(fieldName)
								// 残業時間
								|| TmdTotalTimeDao.COL_OVERTIME.equals(fieldName)
								// 法定内残業時間
								|| TmdTotalTimeDao.COL_OVERTIME_IN.equals(fieldName)
								// 法定外残業時間
								|| TmdTotalTimeDao.COL_OVERTIME_OUT.equals(fieldName)
								// 深夜時間
								|| TmdTotalTimeDao.COL_LATE_NIGHT.equals(fieldName)
								// 深夜所定労働時間内時間
								|| TmdTotalTimeDao.COL_NIGHT_WORK_WITHIN_PRESCRIBED_WORK.equals(fieldName)
								// 深夜時間外時間
								|| TmdTotalTimeDao.COL_NIGHT_OVERTIME_WORK.equals(fieldName)
								// 深夜休日労働時間
								|| TmdTotalTimeDao.COL_NIGHT_WORK_ON_HOLIDAY.equals(fieldName)
								// 所定休出時間
								|| TmdTotalTimeDao.COL_WORK_ON_SPECIFIC_HOLIDAY.equals(fieldName)
								// 法定休出時間
								|| TmdTotalTimeDao.COL_WORK_ON_HOLIDAY.equals(fieldName)
								// 減額対象時間
								|| TmdTotalTimeDao.COL_DECREASE_TIME.equals(fieldName)
								// 45時間超残業時間
								|| TmdTotalTimeDao.COL_FORTY_FIVE_HOUR_OVERTIME.equals(fieldName)
								// 合計遅刻時間
								|| TmdTotalTimeDao.COL_LATE_TIME.equals(fieldName)
								// 遅刻30分以上時間
								|| TmdTotalTimeDao.COL_LATE_THIRTY_MINUTES_OR_MORE_TIME.equals(fieldName)
								// 遅刻30分未満時間
								|| TmdTotalTimeDao.COL_LATE_LESS_THAN_THIRTY_MINUTES_TIME.equals(fieldName)
								// 合計早退時間
								|| TmdTotalTimeDao.COL_LEAVE_EARLY_TIME.equals(fieldName)
								// 早退30分以上時間
								|| TmdTotalTimeDao.COL_LEAVE_EARLY_THIRTY_MINUTES_OR_MORE_TIME.equals(fieldName)
								// 早退30分未満時間
								|| TmdTotalTimeDao.COL_LEAVE_EARLY_LESS_THAN_THIRTY_MINUTES_TIME.equals(fieldName)
								// 60時間超残業時間
								|| TmdTotalTimeDao.COL_SIXTY_HOUR_OVERTIME.equals(fieldName)
								// 平日時間外時間
								|| TmdTotalTimeDao.COL_WEEK_DAY_OVERTIME.equals(fieldName)
								// 所定休日時間外時間
								|| TmdTotalTimeDao.COL_SPECIFIC_OVERTIME.equals(fieldName)
								// 所定労働時間内法定休日労働時間
								|| TmdTotalTimeDao.COL_STATUTORY_HOLIDAY_WORK_TIME_IN.equals(fieldName)
								// 所定労働時間外法定休日労働時間
								|| TmdTotalTimeDao.COL_STATUTORY_HOLIDAY_WORK_TIME_OUT.equals(fieldName)
								// 所定労働時間内所定休日労働時間
								|| TmdTotalTimeDao.COL_PRESCRIBED_HOLIDAY_WORK_TIME_IN.equals(fieldName)
								// 所定労働時間外所定休日労働時間
								|| TmdTotalTimeDao.COL_PRESCRIBED_HOLIDAY_WORK_TIME_OUT.equals(fieldName)
								// 週40時間超勤務時間
								|| TmdTotalTimeDao.COL_WEEKLY_OVER_FORTY_HOUR_WORK_TIME.equals(fieldName)
								// 法定内残業時間(週40時間超除く)
								|| TmdTotalTimeDao.COL_OVERTIME_IN_NO_WEEKLY_FORTY.equals(fieldName)
								// 法定外残業時間(週40時間超除く)
								|| TmdTotalTimeDao.COL_OVERTIME_OUT_NO_WEEKLY_FORTY.equals(fieldName)
								// 平日残業合計時間
								|| TmdTotalTimeDao.COL_WEEK_DAY_OVERTIME_TOTAL.equals(fieldName)
								// 平日時間内時間(週40時間超除く)
								|| TmdTotalTimeDao.COL_WEEK_DAY_OVERTIME_IN_NO_WEEKLY_FORTY.equals(fieldName)
								// 平日時間外時間(週40時間超除く)
								|| TmdTotalTimeDao.COL_WEEK_DAY_OVERTIME_OUT_NO_WEEKLY_FORTY.equals(fieldName)
								// 平日時間内時間
								|| TmdTotalTimeDao.COL_WEEK_DAY_OVERTIME_IN.equals(fieldName)
								// 汎用項目1(数値)
								|| TmdTotalTimeDao.COL_GENERAL_INT_ITEM1.equals(fieldName)) {
							// 項目追加
							dataList.add(getExportTime(rs.getInt(fieldName), format));
							continue;
						}
						// TODO 計算するものは、ここに処理を加えるのか
					}
					if (isPaidHoliday && (TmdPaidHolidayDao.COL_ACTIVATE_DATE.equals(fieldName)
							|| TmdPaidHolidayDao.COL_ACQUISITION_DATE.equals(fieldName)
							|| TmdPaidHolidayDao.COL_LIMIT_DATE.equals(fieldName))) {
						// 有給休暇データ
						// 有効日・取得日・期限日
						dataList.add(DateUtility.getStringDate(rs.getDate(fieldName)));
						continue;
					}
					if (isPaidHoliday && (TimeFileConst.FIELD_CARYYOVER_DAY.equals(fieldName)
							|| TimeFileConst.FIELD_CARYYOVER_HOUR.equals(fieldName))) {
						// 有給休暇データ
						// 前年度繰越日数・前年度繰越時間数
						// 昨年度の有給休暇付与日設定
						Date acquisitionDate = rs.getDate(TmdPaidHolidayDao.COL_ACQUISITION_DATE);
						String carryoverTime = getCarryoverTime(personalId, acquisitionDate, fieldName, true);
						
						dataList.add(carryoverTime);
						continue;
					}
					if (isStockHoliday && (TmdStockHolidayDao.COL_ACTIVATE_DATE.equals(fieldName)
							|| TmdStockHolidayDao.COL_ACQUISITION_DATE.equals(fieldName)
							|| TmdStockHolidayDao.COL_LIMIT_DATE.equals(fieldName))) {
						// ストック休暇データ
						// 有効日・取得日・期限日
						dataList.add(DateUtility.getStringDate(rs.getDate(fieldName)));
						continue;
					}
					if (isHolidayData && (TmdHolidayDataDao.COL_ACTIVATE_DATE.equals(fieldName)
							|| TmdHolidayDataDao.COL_HOLIDAY_LIMIT_DATE.equals(fieldName))) {
						// 休暇データ
						// 有効日・取得期限
						dataList.add(DateUtility.getStringDate(rs.getDate(fieldName)));
						continue;
					}
					if (TimeFileConst.FIELD_FULL_NAME.equals(fieldName)) {
						// 氏名
						dataList.add(MospUtility.getHumansName(rs.getString(PfmHumanDao.COL_FIRST_NAME),
								rs.getString(PfmHumanDao.COL_LAST_NAME)));
						continue;
					}
					if (PlatformFileConst.FIELD_SECTION_NAME.equals(fieldName)) {
						// 所属名称
						dataList.add(section.getSectionName(rs.getString(PfmHumanDao.COL_SECTION_CODE), targetDate));
						continue;
					}
					if (PlatformFileConst.FIELD_SECTION_DISPLAY.equals(fieldName)) {
						// 所属表示名称
						dataList.add(section.getSectionDisplay(rs.getString(PfmHumanDao.COL_SECTION_CODE), targetDate));
						continue;
					}
					// 追加項目が存在する場合
					if (doAdditionalLogic(TimeConst.CODE_KEY_ADD_EXPORTTABLEREFERENCEBEAN_EXPORT, personalId,
							targetDate, dataList, fieldName)) {
						continue;
					}
					// 項目追加フィールド設定
					if (addExtraField(personalId, targetDate, dataList, fieldName, rs)) {
						continue;
					}
					dataList.add(rs.getString(fieldName));
				}
				list.add(dataList.toArray(new String[0]));
			}
		} catch (SQLException e) {
			throw new MospException(e);
		}
		exportDataDao.closers();
		return list;
	}
	
	/**
	 * 対象社員の出勤簿情報を取得する。
	 * @param human 対象社員
	 * @param exportFieldDtoList フィールドリスト
	 * @param actualList 実績一覧リスト
	 * @return 項目値
	 * @throws MospException インスタンスの取得、或いはSQL実行に失敗した場合
	 */
	protected List<String[]> getFieldValue(HumanDtoInterface human, List<ExportFieldDtoInterface> exportFieldDtoList,
			List<AttendanceListDto> actualList) throws MospException {
		// フィールドリスト準備
		List<String[]> fieldList = new ArrayList<String[]>();
		// 実績一覧毎に処理
		for (AttendanceListDto attendanceListDto : actualList) {
			// データ配列準備
			String[] fieldValue = new String[exportFieldDtoList.size()];
			// 対象日取得
			Date workDate = attendanceListDto.getWorkDate();
			// フィールド毎に処理
			for (int i = 0; i < exportFieldDtoList.size(); i++) {
				// 項目名取得
				String fieldName = exportFieldDtoList.get(i).getFieldName();
				// 日付設定
				if (TimeFileConst.FIELD_SHEDULE_DAY.equals(fieldName)) {
					fieldValue[i] = DateUtility.getStringDateAndDay(workDate);
					continue;
				}
				// 項目毎に追加
				if (PlatformFileConst.FIELD_EMPLOYEE_CODE.equals(fieldName)) {
					fieldValue[i] = human.getEmployeeCode();
					continue;
				}
				if (TimeFileConst.FIELD_FULL_NAME.equals(fieldName)) {
					fieldValue[i] = MospUtility.getHumansName(human.getFirstName(), human.getLastName());
					continue;
				}
				if (PlatformFileConst.FIELD_SECTION_CODE.equals(fieldName)) {
					fieldValue[i] = human.getSectionCode();
					continue;
				}
				if (PlatformFileConst.FIELD_SECTION_NAME.equals(fieldName)) {
					fieldValue[i] = section.getSectionName(human.getSectionCode(), workDate);
					continue;
				}
				if (PlatformFileConst.FIELD_SECTION_DISPLAY.equals(fieldName)) {
					fieldValue[i] = section.getSectionDisplay(human.getSectionCode(), workDate);
					continue;
				}
				if (TimeFileConst.FIELD_WORK_TYPE_CODE.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getWorkTypeCode();
					continue;
				}
				if (TimeFileConst.FIELD_WORK_TYPE_ABBR.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getWorkTypeAbbr();
					continue;
				}
				if (TimeFileConst.FIELD_START_TIME.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getStartTimeString();
					continue;
				}
				if (TimeFileConst.FIELD_END_TIME.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getEndTimeString();
					continue;
				}
				if (TimeFileConst.FIELD_WORK_TIME.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getWorkTimeString();
					continue;
				}
				if (TimeFileConst.FIELD_REST_TIME.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getRestTimeString();
					continue;
				}
				if (TimeFileConst.FIELD_PRIVATE_TIME.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getPrivateTimeString();
					continue;
				}
				if (TimeFileConst.FIELD_LATE_EARLY_TIME.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getLateLeaveEarlyTimeString();
					continue;
				}
				if (TimeFileConst.FIELD_OVER_TIME_IN.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getOvertimeInString();
					continue;
				}
				if (TimeFileConst.FIELD_OVER_TIME_OUT.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getOvertimeOutString();
					continue;
				}
				if (TimeFileConst.FIELD_WORK_ON_HOLIDAY.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getHolidayWorkTimeString();
					continue;
				}
				if (TimeFileConst.FIELD_LAST_NIGHT.equals(fieldName)) {
					fieldValue[i] = attendanceListDto.getLateNightTimeString();
					continue;
				}
				if (TimeFileConst.FIELD_TIME_REMARKS.equals(fieldName)) {
					fieldValue[i] = MospUtility.concat(attendanceListDto.getRemark(),
							attendanceListDto.getTimeComment());
					continue;
				}
			}
			fieldList.add(fieldValue);
		}
		return fieldList;
	}
	
	/**
	 * エクスポート時間フォーマット設定情報取得。<br>
	 * @return エクスポート時間フォーマット設定情報
	 */
	protected int getExportTimeFormat() {
		return mospParams.getApplicationProperty(APP_EXPORT_TIME_FORMAT, 0);
	}
	
	/**
	 * エクスポート時間を取得する。<br>
	 * @param minute 分
	 * @param format エクスポート時間フォーマット
	 * @return エクスポート時間
	 */
	protected String getExportTime(int minute, int format) {
		if (format == TimeFileConst.CODE_EXPORT_TIME_FORMAT_MINUTES) {
			// 分単位の場合
			return getExportTimeMinute(minute);
		} else if (format == TimeFileConst.CODE_EXPORT_TIME_FORMAT_HOURS) {
			// 時間単位の場合
			return getExportTimeHour(minute);
		} else if (format == TimeFileConst.CODE_EXPORT_TIME_FORMAT_COLON_SEPARATED) {
			// コロン区切の場合
			return TimeUtility.getStringColonTime(mospParams, minute);
		} else if (format == TimeFileConst.CODE_EXPORT_TIME_FORMAT_DOT_SEPARATED) {
			// ドット区切の場合
			return TimeUtility.getStringPeriodTime(mospParams, minute);
		}
		return "";
	}
	
	/**
	 * 時間を分単位で取得する。<br>
	 * @param minute 分
	 * @return 時間(分単位)
	 */
	protected String getExportTimeMinute(int minute) {
		return Integer.toString(minute);
	}
	
	/**
	 * 時間を時間単位で取得する。<br>
	 * @param minute 分
	 * @return 時間(時間単位)
	 */
	protected String getExportTimeHour(int minute) {
		// 分単位を時間単位に変換して小数点以下第2位までの値を返す
		// TODO
		return "";
	}
	
	@Override
	public void setExportCode(String exportCode) {
		this.exportCode = exportCode;
	}
	
	@Override
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	
	@Override
	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}
	
	@Override
	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	
	@Override
	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}
	
	@Override
	public void setCutoffCode(String cutoffCode) {
		this.cutoffCode = cutoffCode;
	}
	
	@Override
	public void setWorkPlaceCode(String workPlaceCode) {
		this.workPlaceCode = workPlaceCode;
	}
	
	@Override
	public void setEmploymentCode(String employmentCode) {
		this.employmentCode = employmentCode;
	}
	
	@Override
	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	
	@Override
	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}
	
	@Override
	public void setCkbNeedLowerSection(int ckbNeedLowerSection) {
		this.ckbNeedLowerSection = ckbNeedLowerSection;
	}
	
	/**
	 * 追加フィールドを設定する。
	 * @param personalId 個人ID
	 * @param targetDate 対象日付
	 * @param dataList 出力内容
	 * @param fieldName フィールド名
	 * @param rs DB結果
	 * @return 処理結果(true：追加処理実施、false：追加処理なし)
	 * @throws MospException ここでは発生しない
	 */
	protected boolean addExtraField(String personalId, Date targetDate, List<String> dataList, String fieldName,
			ResultSet rs) throws MospException {
		return false;
	}
	
	/**
	 * 前年度繰越休暇日数、前年度繰越休暇時間数を取得する。
	 * @param personalId 個人ID
	 * @param acquisitionDate 今年度付与日
	 * @param fieldName エクスポート項目名
	 * @return 前年度繰越休暇日数または前年度繰越休暇時間数
	 * @throws MospException インスタンスの取得或いはSQL実行に失敗した場合
	 */
	@Override
	public String getCarryoverTime(String personalId, Date acquisitionDate, String fieldName, boolean status)
			throws MospException {
		String carryoverTime = "";
		// 繰越日数初期値設定
		double carryoverDay = 0;
		// 付与時間数初期値
		int grantHour = 0;
		// 1年前の日付(期間開始日)取得
		Date startDay = DateUtility.addYear(acquisitionDate, addPrevious);
		// 今年度付与日1日前(期間終了日)取得
		Date endDay = DateUtility.addDay(acquisitionDate, addPrevious);
		// 付与日以前の有給休暇付与情報リスト取得
		List<PaidHolidayDataDtoInterface> paidHolidayDataList = paidHolidayDataReference
			.findForAcquisitionList(personalId, startDay, endDay);
		// 付与情報毎に処理
		for (PaidHolidayDataDtoInterface paidHolidayDataDto : paidHolidayDataList) {
			// 繰越日数・時間に付与日を加算
			carryoverDay += paidHolidayDataDto.getHoldDay();
			grantHour += paidHolidayDataDto.getHoldHour();
		}
		// 前年度の有給休暇手動付与情報取得
		List<PaidHolidayTransactionDtoInterface> transactionList = paidHolidayTransactionReference
			.findForHistoryList(personalId);
		// 前年度手動付与フラグ準備
		boolean isBefore = false;
		// 手動付与情報毎に処理
		for (PaidHolidayTransactionDtoInterface transactionDto : transactionList) {
			// 有給取得日確認
			if (!DateUtility.isTermContain(transactionDto.getAcquisitionDate(), startDay, endDay)) {
				// 昨年度付与基準日≦有給休暇付与日＜今年度付与基準日でないなら
				continue;
			}
			// 前年度手動付与フラグ設定
			isBefore = true;
			// 繰越日数に付与日を加算
			carryoverDay += (transactionDto.getGivingDay() - transactionDto.getCancelDay());
			grantHour += (transactionDto.getGivingHour() - transactionDto.getCancelHour());
		}
		// 前年度自動付与情報が存在しなく、前年度手動付与がされていない場合
		if (paidHolidayDataList.isEmpty() && !isBefore) {
			return carryoverTime;
		}
		// 昨年度の休暇申請リスト取得
		List<HolidayRequestDtoInterface> holidayRequestList = holidayRequestReference
			.getHolidayRequestListOnWorkflow(personalId, startDay, endDay);
		// 休暇申請情報毎に処理
		double requestDate = 0;
		int requestHour = 0;
		for (HolidayRequestDtoInterface holidayRequestDto : holidayRequestList) {
			// 有給取得日確認
			if (!DateUtility.isTermContain(holidayRequestDto.getHolidayAcquisitionDate(), startDay, endDay)) {
				// 昨年度付与基準日≦有給休暇取得日＜今年度付与基準日でないなら
				continue;
			}
			// 休暇種別確認
			if (holidayRequestDto.getHolidayType1() != TimeConst.CODE_HOLIDAYTYPE_HOLIDAY) {
				// 休暇種別が有給休暇以外なら
				continue;
			}
			// ワークフロー情報取得
			WorkflowDtoInterface workflowDto = workflowReference.getLatestWorkflowInfo(holidayRequestDto.getWorkflow());
			if (status) {
				// 参照ステータスがtrueの場合
				if (!WorkflowUtility.isCompleted(workflowDto)) {
					// 承認済でない場合
					continue;
				}
			} else {
				// 参照ステータスがfalseの場合
				if (WorkflowUtility.isDraft(workflowDto) || WorkflowUtility.isWithDrawn(workflowDto)) {
					// 下書・取下の場合
					continue;
				}
			}
			if (holidayRequestDto.getHolidayRange() == TimeConst.CODE_HOLIDAY_RANGE_ALL) {
				// 全休なら休暇日数+1
				requestDate += 1;
			} else if (holidayRequestDto.getHolidayRange() == TimeConst.CODE_HOLIDAY_RANGE_TIME) {
				// 時間休なら休暇時間+1
				requestHour += 1;
			} else {
				// 午前休・午後休なら休暇日数+0.5
				requestDate += 0.5;
			}
		}
		// 休暇時間8時間ごとに休暇日数1日とする。
		int requestHourDay = 0;
		if (requestHour != 0 && grantHour < requestHour) {
			requestHourDay = requestHour / 8;
			// 休暇時間から休暇日数1日となった日数分の時間休8時間をマイナスする。
			requestHour = requestHour - requestHourDay * 8;
			// 時間休の残数が1～7時間なら計算用休暇日数に1日加算
			if (requestHour != 0) {
				requestHourDay += 1;
			}
		}
		// 昨年度付与日数から休暇日数をマイナスする。
		if (carryoverDay != 0 && requestHourDay != 0) {
			requestDate += requestHourDay;
		}
		carryoverDay = carryoverDay - requestDate;
		// エクスポート項目ごとに処理
		if (TimeFileConst.FIELD_CARYYOVER_DAY.equals(fieldName)) {
			// 繰越日数の場合
			carryoverTime = String.valueOf(carryoverDay);
		} else {
			// 繰越時間数の場合
			int caryyoverHour = grantHour;
			if (requestHour != 0) {
				if (grantHour > requestHour) {
					// 時間休付与時間が時間休申請時間より多い場合
					caryyoverHour = grantHour - requestHour;
				} else if (requestHour <= 8) {
					// 時間休申請時間が時間休付与時間より短く、8時間以下の場合
					caryyoverHour = 8 - requestHour;
				}
			}
			// 繰越時間数設定
			carryoverTime = String.valueOf(caryyoverHour);
		}
		return carryoverTime;
	}
}
