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
package jp.mosp.platform.bean.system.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.mosp.framework.base.MospException;
import jp.mosp.framework.base.MospParams;
import jp.mosp.platform.base.PlatformBean;
import jp.mosp.platform.bean.system.WorkPlaceRegistBeanInterface;
import jp.mosp.platform.dao.system.WorkPlaceDaoInterface;
import jp.mosp.platform.dto.human.HumanDtoInterface;
import jp.mosp.platform.dto.system.WorkPlaceDtoInterface;
import jp.mosp.platform.dto.system.impl.PfmWorkPlaceDto;

/**
 * 勤務地マスタ登録クラス。
 */
public class WorkPlaceRegistBean extends PlatformBean implements WorkPlaceRegistBeanInterface {
	
	/**
	 * 郵便番号1項目帳。<br>
	 */
	protected static final int	LEN_POSTAL_CODE_1	= 3;
	
	/**
	 * 郵便番号2項目帳。<br>
	 */
	protected static final int	LEN_POSTAL_CODE_2	= 4;
	
	/**
	 * 勤務地マスタDAOクラス。<br>
	 */
	WorkPlaceDaoInterface		dao;
	
	
	/**
	 * {@link PlatformBean#PlatformBean()}を実行する。<br>
	 */
	public WorkPlaceRegistBean() {
		super();
	}
	
	/**
	 * {@link PlatformBean#PlatformBean(MospParams, Connection)}を実行する。<br>
	 * @param mospParams MosP処理情報
	 * @param connection DBコネクション
	 */
	public WorkPlaceRegistBean(MospParams mospParams, Connection connection) {
		super(mospParams, connection);
	}
	
	@Override
	public void initBean() throws MospException {
		// DAO準備
		dao = (WorkPlaceDaoInterface)createDao(WorkPlaceDaoInterface.class);
	}
	
	@Override
	public WorkPlaceDtoInterface getInitDto() {
		return new PfmWorkPlaceDto();
	}
	
	@Override
	public void insert(WorkPlaceDtoInterface dto) throws MospException {
		// DTO妥当性確認
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
		dto.setPfmWorkPlaceId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	@Override
	public void add(WorkPlaceDtoInterface dto) throws MospException {
		// DTO妥当性確認
		validate(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 履歴追加情報の検証
		checkAdd(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// レコード識別ID最大値をインクリメントしてDTOに設定
		dto.setPfmWorkPlaceId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	@Override
	public void update(WorkPlaceDtoInterface dto) throws MospException {
		// DTO妥当性確認
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
		logicalDelete(dao, dto.getPfmWorkPlaceId());
		// レコード識別ID最大値をインクリメントしてDTOに設定
		dto.setPfmWorkPlaceId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	@Override
	public void update(long[] idArray, Date activateDate, int inactivateFlag) throws MospException {
		// レコード識別ID配列の妥当性確認
		validateAryId(idArray);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 一括更新処理
		for (String code : getCodeList(idArray)) {
			// 対象勤務地における有効日の情報を取得
			WorkPlaceDtoInterface dto = dao.findForKey(code, activateDate);
			// 存在確認(存在しなければ履歴追加、存在すれば履歴更新)
			if (dto == null) {
				// 対象勤務地における有効日以前で最新の情報を取得
				dto = dao.findForInfo(code, activateDate);
				// 対象勤務地情報確認
				if (dto == null) {
					// 有効日以前に情報が存在しない場合
					addNoCodeBeforeActivateDateMessage(code);
					continue;
				}
				// DTOに有効日、無効フラグを設定
				dto.setActivateDate(activateDate);
				dto.setInactivateFlag(inactivateFlag);
				// DTO妥当性確認
				validate(dto);
				// 履歴追加情報の検証
				checkAdd(dto);
				if (mospParams.hasErrorMessage()) {
					// エラーが存在したら履歴追加処理をしない
					continue;
				}
				// レコード識別ID最大値をインクリメントしてDTOに設定
				dto.setPfmWorkPlaceId(dao.nextRecordId());
				// 登録処理
				dao.insert(dto);
			} else {
				// DTOに無効フラグを設定
				dto.setInactivateFlag(inactivateFlag);
				// DTO妥当性確認
				validate(dto);
				// 履歴更新情報の検証
				checkUpdate(dto);
				if (mospParams.hasErrorMessage()) {
					// エラーが存在したら履歴更新処理をしない
					continue;
				}
				// 論理削除
				logicalDelete(dao, dto.getPfmWorkPlaceId());
				// レコード識別ID最大値をインクリメントしてDTOに設定
				dto.setPfmWorkPlaceId(dao.nextRecordId());
				// 登録処理
				dao.insert(dto);
			}
		}
	}
	
	@Override
	public void delete(long[] idArray) throws MospException {
		// レコード識別ID配列の妥当性確認
		validateAryId(idArray);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 対象勤務地リストの中身を削除
		for (long id : idArray) {
			// 削除対象勤務地を設定している社員がいないかを確認
			checkDelete((WorkPlaceDtoInterface)dao.findForKey(id, true));
			if (mospParams.hasErrorMessage()) {
				// エラーが存在したら履歴削除処理をしない
				continue;
			}
			// 論理削除
			logicalDelete(dao, id);
		}
	}
	
	/**
	 * 新規登録時の確認処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkInsert(WorkPlaceDtoInterface dto) throws MospException {
		// 対象レコードの有効日が重複していないかを確認
		checkDuplicateInsert(dao.findForHistory(dto.getWorkPlaceCode()));
	}
	
	/**
	 * 履歴追加時の確認処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkAdd(WorkPlaceDtoInterface dto) throws MospException {
		// 対象レコードの有効日が重複していないかを確認
		checkDuplicateAdd(dao.findForKey(dto.getWorkPlaceCode(), dto.getActivateDate()));
		// 無効フラグ確認
		if (isDtoActivate(dto)) {
			return;
		}
		// 履歴追加対象コードの履歴情報を取得
		List<WorkPlaceDtoInterface> list = dao.findForHistory(dto.getWorkPlaceCode());
		// 生じる無効期間による履歴追加確認要否を取得
		if (needCheckTermForAdd(dto, list) == false) {
			// 無効期間は発生しない
			return;
		}
		// 確認するべき人事マスタリストを取得
		List<HumanDtoInterface> humanList = getHumanListForCheck(dto, list);
		// コード使用確認
		checkCodeIsUsed(dto.getWorkPlaceCode(), humanList);
	}
	
	/**
	 * 履歴更新時の確認処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkUpdate(WorkPlaceDtoInterface dto) throws MospException {
		// 対象レコード識別IDのデータが削除されていないかを確認
		checkExclusive(dao, dto.getPfmWorkPlaceId());
		// 無効フラグ確認
		if (isDtoActivate(dto)) {
			return;
		}
		// 更新元データの無効フラグ確認
		if (isDtoActivate(dao.findForKey(dto.getPfmWorkPlaceId(), true)) == false) {
			// 更新元データが更新前から無効であれば無効期間は発生しない
			return;
		}
		// 更新対象コードの履歴情報を取得
		List<WorkPlaceDtoInterface> list = dao.findForHistory(dto.getWorkPlaceCode());
		// 確認するべき人事マスタリストを取得
		List<HumanDtoInterface> humanList = getHumanListForCheck(dto, list);
		// コード使用確認
		checkCodeIsUsed(dto.getWorkPlaceCode(), humanList);
	}
	
	/**
	 * 削除時の確認処理を行う。<br>
	 * 削除対象勤務地を設定している社員がいないかの確認を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkDelete(WorkPlaceDtoInterface dto) throws MospException {
		// 対象レコード識別IDのデータが削除されていないかを確認
		checkExclusive(dao, dto.getPfmWorkPlaceId());
		// 対象DTOの無効フラグ確認
		if (isDtoActivate(dto) == false) {
			// 削除対象が無効であれば無効期間は発生しない
			return;
		}
		// 削除対象コードの履歴情報を取得
		List<WorkPlaceDtoInterface> list = dao.findForHistory(dto.getWorkPlaceCode());
		// 生じる無効期間による削除確認要否を取得
		if (needCheckTermForDelete(dto, list) == false) {
			// 無効期間は発生しない
			return;
		}
		// 確認するべき人事マスタリストを取得
		List<HumanDtoInterface> humanList = getHumanListForCheck(dto, list);
		// コード使用確認
		checkCodeIsUsed(dto.getWorkPlaceCode(), humanList);
	}
	
	/**
	 * 勤務地コードリストを取得する。<br>
	 * @param idArray レコード識別ID配列
	 * @return 勤務地コードリスト
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected List<String> getCodeList(long[] idArray) throws MospException {
		// リスト準備
		List<String> list = new ArrayList<String>();
		// レコード識別IDからDTOを取得し、コードをリストへ追加
		for (long id : idArray) {
			// レコード識別IDから対象DTOを取得
			WorkPlaceDtoInterface dto = (WorkPlaceDtoInterface)dao.findForKey(id, false);
			// 対象コードをリストへ追加
			list.add(dto.getWorkPlaceCode());
		}
		return list;
	}
	
	/**
	 * 人事マスタリスト内に対象コードが使用されている情報がないかの確認を行う。<br>
	 * @param code 対象コード
	 * @param list 人事マスタリスト
	 */
	protected void checkCodeIsUsed(String code, List<HumanDtoInterface> list) {
		// 人事マスタリストの中身を確認
		for (HumanDtoInterface dto : list) {
			// 対象コード確認
			if (code.equals(dto.getWorkPlaceCode())) {
				// メッセージ設定
				addCodeIsUsedMessage(code, dto);
			}
		}
	}
	
	/**
	 * 情報の妥当性を確認する。<br>
	 * @param dto 対象DTO
	 */
	protected void validate(WorkPlaceDtoInterface dto) {
		// 郵便番号1が空でない場合
		if (!dto.getPostalCode1().isEmpty()) {
			// 3桁か確認
			checkInputLength(dto.getPostalCode1(), LEN_POSTAL_CODE_1, mospParams.getName("PostalCode") + "(3桁)", null);
		}
		// 郵便番号2が空でない場合
		if (!dto.getPostalCode2().isEmpty()) {
			// 4桁か確認
			checkInputLength(dto.getPostalCode2(), LEN_POSTAL_CODE_2, mospParams.getName("PostalCode") + "(4桁)", null);
		}
	}
	
}
