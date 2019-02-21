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
package jp.mosp.time.settings.action;

import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.mosp.framework.base.BaseVo;
import jp.mosp.framework.base.MospException;
import jp.mosp.time.base.TimeAction;
import jp.mosp.time.bean.ApplicationReferenceBeanInterface;
import jp.mosp.time.bean.PaidHolidayDataGrantBeanInterface;
import jp.mosp.time.bean.PaidHolidayDataReferenceBeanInterface;
import jp.mosp.time.bean.PaidHolidayDataRegistBeanInterface;
import jp.mosp.time.bean.PaidHolidayGrantReferenceBeanInterface;
import jp.mosp.time.bean.PaidHolidayGrantRegistBeanInterface;
import jp.mosp.time.bean.PaidHolidayReferenceBeanInterface;
import jp.mosp.time.bean.impl.PaidHolidayDataSearchBean;
import jp.mosp.time.dto.settings.ApplicationDtoInterface;
import jp.mosp.time.dto.settings.PaidHolidayDataDtoInterface;
import jp.mosp.time.dto.settings.PaidHolidayDtoInterface;
import jp.mosp.time.dto.settings.PaidHolidayGrantDtoInterface;
import jp.mosp.time.settings.vo.PaidHolidayDataGrantCardVo;

/**
 * 有給休暇データの修正を行う。<br>
 * <br>
 * 以下のコマンドを扱う。<br>
 * <ul><li>
 * {@link #CMD_SELECT_SHOW}
 * </li><li>
 * {@link #CMD_REGIST}
 * </li><li>
 * {@link #CMD_DELETE}
 * </li></ul>
 */
public class PaidHolidayDataGrantCardAction extends TimeAction {
	
	/**
	 * 選択表示コマンド。<br>
	 * <br>
	 * 有給休暇付与修正画面を表示する。<br>
	 */
	public static final String	CMD_SELECT_SHOW	= "TM041001";
	
	/**
	 * 登録コマンド。<br>
	 * <br>
	 * 入力した内容を基に有給休暇データを登録する。<br>
	 * 入力必須項目が入力されていない状態で登録を行おうとした場合はエラーメッセージにて通知、処理は実行されない。<br>
	 */
	public static final String	CMD_REGIST		= "TM041004";
	
	/**
	 * 削除コマンド。<br>
	 * <br>
	 */
	public static final String	CMD_DELETE		= "TM041005";
	
	
	@Override
	protected BaseVo getSpecificVo() {
		return new PaidHolidayDataGrantCardVo();
	}
	
	@Override
	public void action() throws MospException {
		// コマンド毎の処理
		if (mospParams.getCommand().equals(CMD_SELECT_SHOW)) {
			// 選択表示
			prepareVo(false, false);
			select();
		} else if (mospParams.getCommand().equals(CMD_REGIST)) {
			// 登録
			prepareVo();
			regist();
		} else if (mospParams.getCommand().equals(CMD_DELETE)) {
			// 削除
			prepareVo();
			delete();
		} else {
			throwInvalidCommandException();
		}
	}
	
	/**
	 * 選択表示処理を行う。<br>
	 * @throws MospException 例外発生時
	 */
	protected void select() throws MospException {
		String personalId = getTargetPersonalId();
		Date targetDate = getTargetDate();
		setEmployeeInfo(personalId, targetDate);
		// 有給休暇データ設定
		setPaidHolidayDataList();
	}
	
	/**
	 * 登録処理を行う。<br>
	 * @throws MospException 例外発生時
	 */
	protected void regist() throws MospException {
		// VO取得
		PaidHolidayDataGrantCardVo vo = (PaidHolidayDataGrantCardVo)mospParams.getVo();
		PaidHolidayDataReferenceBeanInterface paidHolidayData = timeReference().paidHolidayData();
		PaidHolidayDataRegistBeanInterface paidHolidayDataRegist = time().paidHolidayDataRegist();
		for (int i = 0; i < vo.getAryRecordId().length; i++) {
			PaidHolidayDataDtoInterface dto = paidHolidayData.findForKey(vo.getAryRecordId()[i]);
			if (dto == null) {
				// 新規登録
				dto = paidHolidayDataRegist.getInitDto();
				insert(dto, i);
			} else {
				// 履歴更新
				update(dto, i);
			}
			if (mospParams.hasErrorMessage()) {
				// 更新失敗メッセージ設定
				addUpdateFailedMessage();
				return;
			}
			paidHolidayGrantRegist(dto);
			if (mospParams.hasErrorMessage()) {
				// 更新失敗メッセージ設定
				addUpdateFailedMessage();
				return;
			}
		}
		// コミット
		commit();
		// 更新成功メッセージ設定
		addUpdateMessage();
		// 有給休暇データ設定
		setPaidHolidayDataList();
	}
	
	/**
	 * 新規登録処理を行う。<br>
	 * @param dto 対象DTO
	 * @param i インデックス
	 * @throws MospException 例外発生時
	 */
	protected void insert(PaidHolidayDataDtoInterface dto, int i) throws MospException {
		// VO取得
		PaidHolidayDataGrantCardVo vo = (PaidHolidayDataGrantCardVo)mospParams.getVo();
		PaidHolidayDataRegistBeanInterface regist = time().paidHolidayDataRegist();
		ApplicationReferenceBeanInterface application = timeReference().application();
		PaidHolidayReferenceBeanInterface paidHoliday = timeReference().paidHoliday();
		// 付与日
		Date grantDate = getDate(vo.getAryLblGrantDate()[i]);
		ApplicationDtoInterface applicationDto = application.findForPerson(vo.getPersonalId(), grantDate);
		application.chkExistApplication(applicationDto, grantDate);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		PaidHolidayDtoInterface paidHolidayDto = paidHoliday.getPaidHolidayInfo(applicationDto.getPaidHolidayCode(),
				grantDate);
		paidHoliday.chkExistPaidHoliday(paidHolidayDto, grantDate);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// DTOに値を設定
		setDtoFields(dto, paidHolidayDto);
		setDtoFields(dto, i, true);
		// 相関チェック
		regist.checkModify(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 新規追加処理
		regist.insert(dto);
	}
	
	/**
	 * 履歴更新処理を行う。<br>
	 * @param dto 対象DTO
	 * @param i インデックス
	 * @throws MospException 例外発生時
	 */
	protected void update(PaidHolidayDataDtoInterface dto, int i) throws MospException {
		PaidHolidayDataRegistBeanInterface regist = time().paidHolidayDataRegist();
		// DTOに値を設定
		setDtoFields(dto, i, false);
		// 相関チェック
		regist.checkModify(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 更新処理
		regist.update(dto);
	}
	
	/**
	 * 削除処理を行う。<br>
	 * @throws MospException インスタンスの取得或いはSQL実行に失敗した場合
	 */
	protected void delete() throws MospException {
		// VO取得
		PaidHolidayDataGrantCardVo vo = (PaidHolidayDataGrantCardVo)mospParams.getVo();
		// クラス取得
		PaidHolidayDataRegistBeanInterface regist = time().paidHolidayDataRegist();
		PaidHolidayDataReferenceBeanInterface paidHolidayData = timeReference().paidHolidayData();
		// 削除対象有給休暇情報取得
		PaidHolidayDataDtoInterface dto = paidHolidayData.findForKey(vo.getAryRecordId()[getTransferredIndex()]);
		if (dto == null) {
			// 削除失敗メッセージ設定
			addDeleteHistoryFailedMessage();
			return;
		}
		// 削除チェック
		regist.checkDeleteConfirm(dto);
		if (mospParams.hasErrorMessage()) {
			// 削除失敗メッセージ設定
			addDeleteHistoryFailedMessage();
			return;
		}
		// 削除
		regist.delete(dto);
		if (mospParams.hasErrorMessage()) {
			// 削除失敗メッセージ設定
			addDeleteHistoryFailedMessage();
			return;
		}
		paidHolidayGrantDelete(dto.getAcquisitionDate());
		if (mospParams.hasErrorMessage()) {
			// 削除失敗メッセージ設定
			addDeleteHistoryFailedMessage();
			return;
		}
		// コミット
		commit();
		// 削除成功メッセージ設定
		addDeleteMessage();
		// 有給休暇データ設定
		setPaidHolidayDataList();
	}
	
	/**
	 * 有給休暇付与登録処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException 例外発生時
	 */
	protected void paidHolidayGrantRegist(PaidHolidayDataDtoInterface dto) throws MospException {
		// VO取得
		PaidHolidayDataGrantCardVo vo = (PaidHolidayDataGrantCardVo)mospParams.getVo();
		PaidHolidayGrantReferenceBeanInterface paidHolidayGrant = timeReference().paidHolidayGrant();
		PaidHolidayGrantRegistBeanInterface paidHolidayGrantRegist = time().paidHolidayGrantRegist();
		PaidHolidayGrantDtoInterface paidHolidayGrantDto = paidHolidayGrant.findForKey(vo.getPersonalId(),
				dto.getAcquisitionDate());
		if (paidHolidayGrantDto == null) {
			paidHolidayGrantDto = paidHolidayGrantRegist.getInitDto();
		}
		setDtoFields(paidHolidayGrantDto, dto);
		// 登録処理
		paidHolidayGrantRegist.regist(paidHolidayGrantDto);
	}
	
	/**
	 * 有給休暇付与削除処理を行う。<br>
	 * @param grantDate 付与日
	 * @throws MospException 例外発生時
	 */
	protected void paidHolidayGrantDelete(Date grantDate) throws MospException {
		// VO取得
		PaidHolidayDataGrantCardVo vo = (PaidHolidayDataGrantCardVo)mospParams.getVo();
		PaidHolidayGrantReferenceBeanInterface paidHolidayGrant = timeReference().paidHolidayGrant();
		PaidHolidayGrantRegistBeanInterface paidHolidayGrantRegist = time().paidHolidayGrantRegist();
		PaidHolidayGrantDtoInterface dto = paidHolidayGrant.findForKey(vo.getPersonalId(), grantDate);
		if (dto == null) {
			return;
		}
		// 削除処理
		paidHolidayGrantRegist.delete(dto);
	}
	
	/**
	 * 有給休暇データリストを設定する。<br>
	 * @throws MospException 例外発生時
	 */
	protected void setPaidHolidayDataList() throws MospException {
		// VO取得
		PaidHolidayDataGrantCardVo vo = (PaidHolidayDataGrantCardVo)mospParams.getVo();
		// クラス準備
		PaidHolidayDataReferenceBeanInterface paidHolidayData = timeReference().paidHolidayData();
		PaidHolidayDataGrantBeanInterface paidHolidayDataGrant = time().paidHolidayDataGrant();
		// 有給休暇データ取得
		PaidHolidayDataDtoInterface dto = paidHolidayData.findForKey(vo.getPersonalId(), vo.getTargetDate(),
				vo.getTargetDate());
		// 有給休暇情報確認フラグ
		boolean granted = dto != null;
		// 有給休暇情報がない場合
		if (!granted) {
			// 有給休暇データを生成
			dto = paidHolidayDataGrant.create(vo.getPersonalId(), vo.getTargetDate(), false);
		}
		// リスト準備
		List<PaidHolidayDataDtoInterface> list = new ArrayList<PaidHolidayDataDtoInterface>();
		if (dto == null) {
			// VOに設定
			setVoFields(list, granted);
			return;
		}
		// 有給休暇データリスト毎に処理
		for (PaidHolidayDataDtoInterface paidHolidayDataDto : paidHolidayData
			.getPaidHolidayDataInfoAllList(vo.getPersonalId(), vo.getTargetDate())) {
			// 取得日が同じ場合
			if (paidHolidayDataDto.getAcquisitionDate().equals(dto.getAcquisitionDate())) {
				continue;
			}
			// リスト追加
			list.add(paidHolidayDataDto);
		}
		list.add(dto);
		// VOに設定
		setVoFields(list, granted);
	}
	
	/**
	 * DTOの値をVO(編集項目)に設定する。<br>
	 * @param list 対象リスト
	 * @param isGranted 付与済の場合true、そうでない場合false
	 */
	protected void setVoFields(List<PaidHolidayDataDtoInterface> list, boolean isGranted) {
		// VO取得
		PaidHolidayDataGrantCardVo vo = (PaidHolidayDataGrantCardVo)mospParams.getVo();
		// 配列準備
		long[] aryRecordId = new long[list.size()];
		String[] aryLblGrantDate = new String[list.size()];
		String[] aryLblActivateDate = new String[list.size()];
		String[] aryLblExpirationDate = new String[list.size()];
		String[] aryLblGrantDays = new String[list.size()];
		String[] aryTxtGrantDays = new String[list.size()];
		Format format = getNumberFormat();
		for (int i = 0; i < list.size(); i++) {
			PaidHolidayDataDtoInterface dto = list.get(i);
			aryRecordId[i] = dto.getTmdPaidHolidayId();
			aryLblGrantDate[i] = getStringDateAndDay(dto.getAcquisitionDate());
			aryLblActivateDate[i] = getStringDateAndDay(dto.getActivateDate());
			aryLblExpirationDate[i] = getStringDateAndDay(dto.getLimitDate());
			aryLblGrantDays[i] = mospParams.getName("Hyphen");
			if (i != list.size() - 1 || isGranted) {
				StringBuffer sb = new StringBuffer();
				sb.append(getGrantDays(dto.getHoldDay(), format));
				sb.append(mospParams.getName("Day"));
				aryLblGrantDays[i] = sb.toString();
			}
			aryTxtGrantDays[i] = getGrantDays(Math.floor(dto.getHoldDay()), format);
		}
		// 配列をVOに設定
		vo.setAryRecordId(aryRecordId);
		vo.setAryLblGrantDate(aryLblGrantDate);
		vo.setAryLblActivateDate(aryLblActivateDate);
		vo.setAryLblExpirationDate(aryLblExpirationDate);
		vo.setAryLblGrantDays(aryLblGrantDays);
		vo.setAryTxtGrantDays(aryTxtGrantDays);
	}
	
	/**
	 * VO(編集項目)の値をDTOに設定する。<br>
	 * @param dto 対象DTO
	 * @param i インデックス
	 * @param isInsert 新規登録フラグ(true：新規登録、false：更新)
	 */
	protected void setDtoFields(PaidHolidayDataDtoInterface dto, int i, boolean isInsert) {
		// VO取得
		PaidHolidayDataGrantCardVo vo = (PaidHolidayDataGrantCardVo)mospParams.getVo();
		// 新規登録の場合
		if (isInsert) {
			Date grantDate = getDate(vo.getAryLblGrantDate()[i]);
			dto.setPersonalId(vo.getPersonalId());
			dto.setAcquisitionDate(grantDate);
			dto.setActivateDate(grantDate);
			dto.setLimitDate(getDate(vo.getAryLblExpirationDate()[i]));
		}
		dto.setHoldDay(Double.parseDouble(vo.getAryTxtGrantDays()[i]));
	}
	
	/**
	 * VO(編集項目)の値をDTOに設定する。<br>
	 * @param dto 対象DTO
	 * @param paidHolidayDto 有給休暇設定DTO
	 */
	protected void setDtoFields(PaidHolidayDataDtoInterface dto, PaidHolidayDtoInterface paidHolidayDto) {
		dto.setDenominatorDayHour(paidHolidayDto.getTimeAcquisitionLimitTimes());
		dto.setTemporaryFlag(1);
	}
	
	/**
	 * VO(編集項目)の値をDTOに設定する。<br>
	 * @param dto 対象DTO
	 * @param paidHolidayDataDto 有給休暇データDTO
	 */
	protected void setDtoFields(PaidHolidayGrantDtoInterface dto, PaidHolidayDataDtoInterface paidHolidayDataDto) {
		// VO取得
		PaidHolidayDataGrantCardVo vo = (PaidHolidayDataGrantCardVo)mospParams.getVo();
		dto.setPersonalId(vo.getPersonalId());
		dto.setGrantDate(paidHolidayDataDto.getAcquisitionDate());
		dto.setGrantStatus(PaidHolidayDataSearchBean.GRANTED);
	}
	
	/**
	 * 付与日数を取得する。
	 * @param grantDays 付与日数
	 * @param format フォーマット
	 * @return 付与日数
	 */
	protected String getGrantDays(double grantDays, Format format) {
		return format.format(grantDays);
	}
	
}
