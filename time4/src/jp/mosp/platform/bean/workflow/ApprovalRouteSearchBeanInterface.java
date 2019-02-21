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
package jp.mosp.platform.bean.workflow;

import java.util.Date;
import java.util.List;

import jp.mosp.framework.base.MospException;
import jp.mosp.platform.dto.workflow.ApprovalRouteDtoInterface;

/**
 * 承認ルートマスタ検索インターフェース。
 */
public interface ApprovalRouteSearchBeanInterface {
	
	/**
	 * 条件による検索。
	 * <p>
	 * 検索条件から承認ルートマスタリストを取得。
	 * </p>
	 * @return 承認ルートマスタ検索リスト
	 * @throws MospException インスタンスの取得、SQLの作成及び実行に失敗した場合
	 */
	List<ApprovalRouteDtoInterface> getSearchList() throws MospException;
	
	/**
	 * @param activateDate セットする 有効日。
	 */
	void setActivateDate(Date activateDate);
	
	/**
	 * @param approvalCount セットする 階層数。
	 */
	void setApprovalCount(String approvalCount);
	
	/**
	 * @param routeCode セットする ルートコード。
	 */
	void setRouteCode(String routeCode);
	
	/**
	 * @param routeName セットする ルート名称。
	 */
	void setRouteName(String routeName);
	
	/**
	 * @param unitCode セットする ユニットコード。
	 */
	void setUnitCode(String unitCode);
	
	/**
	 * @param unitName セットする ユニット名称。
	 */
	void setUnitName(String unitName);
	
	/**
	 * @param inactivateFlag セットする 有効無効フラグ。
	 */
	void setInactivateFlag(String inactivateFlag);
	
}
