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
package jp.mosp.platform.bean.exporter.impl;

import jp.mosp.framework.base.MospException;
import jp.mosp.platform.base.PlatformBean;
import jp.mosp.platform.bean.exporter.CsvCompilerInterface;
import jp.mosp.platform.bean.exporter.CsvExportManagerBeanInterface;
import jp.mosp.platform.constant.PlatformMessageConst;
import jp.mosp.platform.dto.exporter.CsvExportIntermediate;

/**
 * CSV出力処理管理クラス。
 */
public abstract class CsvExportManagerBean extends PlatformBean implements CsvExportManagerBeanInterface {
	
	@Override
	public void delivery(CsvCompilerInterface compiler) throws MospException {
		CsvExportIntermediate intermediate = compiler.compile();
		if (intermediate == null) {
			// 検索結果無しメッセージ設定
			mospParams.addMessage(PlatformMessageConst.MSG_NO_DATA);
			return;
		}
		if (mospParams.hasErrorMessage()) {
			// エラーメッセージが存在する場合、出力しない。
			return;
		}
		// ファイル名
		mospParams.setFileName(intermediate.getFilePrefix() + ".csv");
		// CSVデータリストをMosP処理情報に設定
		mospParams.setFile(intermediate.getOrangeSignalParams());
	}
	
}
