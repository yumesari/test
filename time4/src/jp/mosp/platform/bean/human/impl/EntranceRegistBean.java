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
package jp.mosp.platform.bean.human.impl;

import java.sql.Connection;

import jp.mosp.framework.base.MospException;
import jp.mosp.framework.base.MospParams;
import jp.mosp.platform.bean.human.EntranceRegistBeanInterface;
import jp.mosp.platform.bean.human.base.PlatformHumanBean;
import jp.mosp.platform.constant.PlatformMessageConst;
import jp.mosp.platform.dao.human.EntranceDaoInterface;
import jp.mosp.platform.dao.human.RetirementDaoInterface;
import jp.mosp.platform.dto.human.EntranceDtoInterface;
import jp.mosp.platform.dto.human.RetirementDtoInterface;
import jp.mosp.platform.dto.human.impl.PfaHumanEntranceDto;

/**
 * 人事入社情報登録クラス。
 */
public class EntranceRegistBean extends PlatformHumanBean implements EntranceRegistBeanInterface {
	
	/**
	 * 人事入社情報DAOクラス。<br>
	 */
	EntranceDaoInterface dao;
	
	
	/**
	 * {@link PlatformHumanBean#PlatformHumanBean()}を実行する。<br>
	 */
	public EntranceRegistBean() {
		super();
	}
	
	/**
	 * {@link PlatformHumanBean#PlatformHumanBean(MospParams, Connection)}を実行する。<br>
	 * @param mospParams MosPパラメータクラス
	 * @param connection DBコネクション
	 */
	public EntranceRegistBean(MospParams mospParams, Connection connection) {
		super(mospParams, connection);
	}
	
	@Override
	public void initBean() throws MospException {
		super.initBean();
		// DAO準備
		dao = (EntranceDaoInterface)createDao(EntranceDaoInterface.class);
	}
	
	@Override
	public EntranceDtoInterface getInitDto() {
		return new PfaHumanEntranceDto();
	}
	
	@Override
	public void insert(EntranceDtoInterface dto) throws MospException {
		// DTOの妥当性確認
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
		dto.setPfaHumanEntranceId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	@Override
	public void regist(EntranceDtoInterface dto) throws MospException {
		// レコード識別ID確認
		if (dto.getPfaHumanEntranceId() == 0) {
			// 新規登録
			insert(dto);
		} else {
			// 更新
			update(dto);
		}
	}
	
	@Override
	public void delete(EntranceDtoInterface dto) throws MospException {
		// 削除情報の検証
		checkDelete(dto);
		if (mospParams.hasErrorMessage()) {
			return;
		}
		// 論理削除
		logicalDelete(dao, dto.getPfaHumanEntranceId());
	}
	
	@Override
	public void validate(EntranceDtoInterface dto, Integer row) {
		// 必須確認(入社日)
		checkRequired(dto.getEntranceDate(), getNameEntranceDate(), row);
	}
	
	/**
	 * 更新を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException インスタンスの取得、或いはSQL実行に失敗した場合
	 */
	public void update(EntranceDtoInterface dto) throws MospException {
		// DTOの妥当性確認
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
		logicalDelete(dao, dto.getPfaHumanEntranceId());
		// レコード識別ID最大値をインクリメントしてDTOに設定
		dto.setPfaHumanEntranceId(dao.nextRecordId());
		// 登録処理
		dao.insert(dto);
	}
	
	/**
	 * 新規登録時の確認処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkInsert(EntranceDtoInterface dto) throws MospException {
		// 対象レコードが重複していないかを確認
		checkDuplicateInsert(dao.findForInfo(dto.getPersonalId()));
	}
	
	/**
	 * 更新時の確認処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkUpdate(EntranceDtoInterface dto) throws MospException {
		// 対象レコード識別IDのデータが削除されていないかを確認
		checkExclusive(dao, dto.getPfaHumanEntranceId());
		// 社員妥当性確認
		checkEmployeeForUpdate(dto);
	}
	
	/**
	 * 削除時の確認処理を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException SQLの作成に失敗した場合、或いはSQL例外が発生した場合
	 */
	protected void checkDelete(EntranceDtoInterface dto) throws MospException {
		// 対象レコード識別IDのデータが削除されていないかを確認
		checkExclusive(dao, dto.getPfaHumanEntranceId());
		// 社員妥当性確認
		checkEmployeeForDelete(dto);
	}
	
	/**
	 * 社員妥当性確認を行う。<br>
	 * 退職情報の確認を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException インスタンスの取得、或いはSQL実行に失敗した場合
	 */
	protected void checkEmployeeForUpdate(EntranceDtoInterface dto) throws MospException {
		// 退職情報DAO準備
		RetirementDaoInterface retirementDao = (RetirementDaoInterface)createDao(RetirementDaoInterface.class);
		// 退職情報取得
		RetirementDtoInterface retirementDto = retirementDao.findForInfo(dto.getPersonalId());
		// 退職情報確認(退職情報が存在しており、或いは退職より後に入社)
		if (retirementDto != null && retirementDto.getRetirementDate().before(dto.getEntranceDate())) {
			// 入社情報より退職情報が前になってしまう場合のメッセージを追加
			addInvalidOrderMessage(getNameEntranceDate(), getNameRetirementDate());
		}
	}
	
	/**
	 * 社員妥当性確認を行う。<br>
	 * 退職情報の確認を行う。<br>
	 * @param dto 対象DTO
	 * @throws MospException インスタンスの取得、或いはSQL実行に失敗した場合
	 */
	protected void checkEmployeeForDelete(EntranceDtoInterface dto) throws MospException {
		// 退職情報DAO準備
		RetirementDaoInterface retirementDao = (RetirementDaoInterface)createDao(RetirementDaoInterface.class);
		// 退職情報取得
		RetirementDtoInterface retirementDto = retirementDao.findForInfo(dto.getPersonalId());
		// 退職情報確認(退職情報が存在していたら削除不能)
		if (retirementDto != null) {
			// 退職情報が存在しているため入社情報が削除できない場合のメッセージを追加
			addDeleteEntranceFailedMessage();
		}
	}
	
	/**
	 * 退職情報が存在しているため入社情報が削除できない場合のメッセージを設定する。<br>
	 */
	protected void addDeleteEntranceFailedMessage() {
		mospParams.addErrorMessage(PlatformMessageConst.MSG_CANT_DELETE_ENTRANCE);
	}
	
}
