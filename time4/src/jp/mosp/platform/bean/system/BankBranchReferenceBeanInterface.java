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
package jp.mosp.platform.bean.system;

import jp.mosp.framework.base.MospException;
import jp.mosp.platform.dto.system.BankBranchDtoInterface;

/**
 * 銀行支店マスタ参照インターフェース。<br>
 */
public interface BankBranchReferenceBeanInterface {
	
	/**
	 * 銀行コードと検索値から合致するプルダウン用配列を取得する。<br>
	 * 表示内容は、コード＋名称。<br>
	 * @param bankCode 銀行コード
	 * @param value 検索値
	 * @param needBlank     空白行要否(true：空白行要、false：空白行不要)
	 * @return プルダウン用配列
	 * @throws MospException インスタンスの取得或いはSQL実行に失敗した場合
	 */
	String[][] getSelectArray(String bankCode, String value, boolean needBlank) throws MospException;
	
	/**
	 * 銀行コードと支店コードから銀行支店情報を取得する。
	 * @param bankCode 銀行コード
	 * @param branchCode 支店コード
	 * @return 銀行支店情報
	 * @throws MospException インスタンスの取得に失敗した場合
	 */
	BankBranchDtoInterface findForKey(String bankCode, String branchCode) throws MospException;
	
	/**
	 * 銀行コードと支店コードから支店名称を取得する。
	 * @param bankCode 銀行コード
	 * @param branchCode 銀行支店コード
	 * @return 銀行名称
	 * @throws MospException インスタンスの取得に失敗した場合
	 */
	String getBranchName(String bankCode, String branchCode) throws MospException;
	
}
