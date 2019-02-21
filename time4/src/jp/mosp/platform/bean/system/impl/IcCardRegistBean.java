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
import jp.mosp.platform.bean.portal.UserCheckBeanInterface;
import jp.mosp.platform.bean.system.IcCardRegistBeanInterface;
import jp.mosp.platform.dao.system.IcCardDaoInterface;
import jp.mosp.platform.dto.system.IcCardDtoInterface;
import jp.mosp.platform.dto.system.impl.PfmIcCardDto;

/**
 * ICカードマスタ登録クラス。
 */
public class IcCardRegistBean extends PlatformBean implements IcCardRegistBeanInterface {
	
	/**
	 * ICカードマスタDAOクラス。<br>
	 */
	protected IcCardDaoInterface	dao;
	
	/**
	 * ユーザ確認クラス。
	 */
	UserCheckBeanInterface			userCheck;
	
	
	/**
	 * {@link PlatformBean#PlatformBean()}を実行する。<br>
	 */
	public IcCardRegistBean() {
		super();
	}
	
	/**
	 * {@link PlatformBean#PlatformBean(MospParams, Connection)}を実行する。<br>
	 * @param mospParams MosP処理情報
	 * @param connection DBコネクション
	 */
	public IcCardRegistBean(MospParams mospParams, Connection connection) {
		super(mospParams, connection);
	}
	
	@Override
	public void initBean() throws MospException {
		dao = (IcCardDaoInterface)createDao(IcCardDaoInterface.class);
		userCheck = (UserCheckBeanInterface)createBean(UserCheckBeanInterface.class);
	}
	
	@Override
	public IcCardDtoInterface getInitDto() {
		return new PfmIcCardDto();
	}
	
	@Override
	public void insert(IcCardDtoInterface dto) throws MospException {
		// DTO妥当性確認
		validate(dto, null);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 新規登録情報の検証
		checkInsert(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// レコード識別ID最大値をインクリメントしてDTOに設定
		dto.setPfmIcCardId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	/**
	 * 新規登録情報の検証
	 * @param dto ICカードマスタ情報
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	private void checkInsert(IcCardDtoInterface dto) throws MospException {
		// ICカードID重複確認
		checkDuplicateInsert(dao.findForHistory(dto.getIcCardId()));
		
	}
	
	@Override
	public void add(IcCardDtoInterface dto) throws MospException {
		// DTO妥当性確認
		validate(dto, null);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 履歴追加情報の検証
		checkAdd(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// レコード識別ID最大値をインクリメントしてDTOに設定
		dto.setPfmIcCardId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	/**
	 * 履歴追加情報の検証
	 * @param dto ICカードマスタ情報
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	private void checkAdd(IcCardDtoInterface dto) throws MospException {
		// 対象レコードの有効日が重複していないかを確認
		checkDuplicateAdd(dao.findForKey(dto.getIcCardId(), dto.getActivateDate()));
	}
	
	@Override
	public void update(IcCardDtoInterface dto) throws MospException {
		// DTO妥当性確認
		validate(dto, null);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 履歴更新情報の検証
		checkUpdate(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 論理削除
		logicalDelete(dao, dto.getPfmIcCardId());
		// レコード識別ID最大値をインクリメントしてDTOに設定
		dto.setPfmIcCardId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	/**
	 * 履歴更新情報の確認処理を行う。<br>
	 * @param dto ICカードマスタ情報
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	private void checkUpdate(IcCardDtoInterface dto) throws MospException {
		// 対象レコード識別IDのデータが削除されていないかを確認
		checkExclusive(dao, dto.getPfmIcCardId());
	}
	
	@Override
	public void delete(long[] idArray) throws MospException {
		// レコード識別ID配列の妥当性確認
		validateAryId(idArray);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 対象雇用契約リストの中身を削除
		for (long id : idArray) {
			// 削除対象雇用契約を設定している社員がいないかを確認
			checkDelete((IcCardDtoInterface)dao.findForKey(id, true));
			if (mospParams.hasErrorMessage()) {
				// エラーが存在したら履歴削除処理をしない
				continue;
			}
			// 論理削除
			logicalDelete(dao, id);
		}
	}
	
	/**
	 * 削除対象の検証
	 * @param dto ICカード情報
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	private void checkDelete(IcCardDtoInterface dto) throws MospException {
		// 対象レコード識別IDのデータが削除されていないかを確認
		checkExclusive(dao, dto.getPfmIcCardId());
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
			// 対象ICカード情報における有効日の情報を取得
			IcCardDtoInterface dto = dao.findForKey(code, activateDate);
			// 存在確認(存在しなければ履歴追加、存在すれば履歴更新)
			if (dto == null) {
				// 対象ICカード情報における有効日以前で最新の情報を取得
				dto = dao.findForCardIdInfo(code, activateDate);
				// 対象ICカード情報確認
				if (dto == null) {
					// 有効日以前に情報が存在しない場合
					addNoCodeBeforeActivateDateMessage(code);
					continue;
				}
				// DTOに有効日、無効フラグを設定
				dto.setActivateDate(activateDate);
				dto.setInactivateFlag(inactivateFlag);
				// DTO妥当性確認
				validate(dto, null);
				// 履歴追加情報の検証
				checkAdd(dto);
				if (mospParams.hasErrorMessage()) {
					// エラーが存在したら履歴追加処理をしない
					continue;
				}
				// レコード識別ID最大値をインクリメントしてDTOに設定
				dto.setPfmIcCardId(dao.nextRecordId());
				// 登録処理
				dao.insert(dto);
			} else {
				// DTOに無効フラグを設定
				dto.setInactivateFlag(inactivateFlag);
				// DTO妥当性確認
				validate(dto, null);
				// 履歴更新情報の検証
				checkUpdate(dto);
				if (mospParams.hasErrorMessage()) {
					// エラーが存在したら履歴更新処理をしない
					continue;
				}
				// 論理削除
				logicalDelete(dao, dto.getPfmIcCardId());
				// レコード識別ID最大値をインクリメントしてDTOに設定
				dto.setPfmIcCardId(dao.nextRecordId());
				// 登録処理
				dao.insert(dto);
			}
		}
	}
	
	/**
	 * コードリストを取得する。<br>
	 * @param idArray レコード識別ID配列
	 * @return 名称項目コードリスト
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected List<String> getCodeList(long[] idArray) throws MospException {
		// リスト準備
		List<String> list = new ArrayList<String>();
		// レコード識別IDからDTOを取得し、コードをリストへ追加
		for (long id : idArray) {
			// レコード識別IDから対象DTOを取得
			IcCardDtoInterface dto = (IcCardDtoInterface)dao.findForKey(id, false);
			// 対象コードをリストへ追加
			list.add(dto.getIcCardId());
		}
		return list;
	}
	
	@Override
	public void validate(IcCardDtoInterface dto, Integer row) throws MospException {
		// ユーザ妥当性の確認
		userCheck.checkUserEmployee(dto.getPersonalId(), dto.getActivateDate());
	}
	
}
