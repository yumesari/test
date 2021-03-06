<%--
MosP - Mind Open Source Project    http://www.mosp.jp/
Copyright (C) MIND Co., Ltd.       http://www.e-mind.co.jp/

This program is free software: you can redistribute it and/or
modify it under the terms of the GNU Affero General Public License
as published by the Free Software Foundation, either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page
language = "java"
pageEncoding = "UTF-8"
buffer = "256kb"
autoFlush = "false"
errorPage = "/jsp/common/error.jsp"
%><%@ page
import = "jp.mosp.framework.constant.MospConst"
import = "jp.mosp.framework.base.MospParams"
import = "jp.mosp.framework.utils.HtmlUtility"
import = "jp.mosp.platform.system.vo.EmploymentMasterVo"
import = "jp.mosp.platform.system.action.EmploymentMasterAction"
import = "jp.mosp.platform.comparator.base.ActivateDateComparator"
import = "jp.mosp.platform.comparator.base.InactivateComparator"
import = "jp.mosp.platform.comparator.base.EmploymentContractCodeComparator"
import = "jp.mosp.platform.comparator.system.EmploymentMasterEmployNameComparator"
import = "jp.mosp.platform.comparator.system.EmploymentMasterEmployAbbrComparator"
import = "jp.mosp.platform.constant.PlatformConst"
import = "jp.mosp.platform.system.constant.PlatformSystemConst"
import = "jp.mosp.platform.utils.PlatformUtility"
%><%
MospParams params = (MospParams)request.getAttribute(MospConst.ATT_MOSP_PARAMS);
EmploymentMasterVo vo = (EmploymentMasterVo)params.getVo();
%>
<div class="List" id="divEdit">
	<table class="InputTable">
		<tr>
			<th class="EditTableTh" colspan="6">
				<jsp:include page="<%= PlatformSystemConst.PATH_SYSTEM_EDIT_HEADER_JSP %>" flush="false" />
			</th>
		</tr>
		<tr>
			<td class="TitleTd"><span class="RequiredLabel">*&nbsp;</span><span><%=params.getName("ActivateDate")%></span></td>
			<td class="InputTd">
				<input type="text" class="Number4RequiredTextBox" id="txtEditActivateYear" name="txtEditActivateYear" value="<%=HtmlUtility.escapeHTML(vo.getTxtEditActivateYear())%>" />&nbsp;<label for="txtEditActivateYear"><%=params.getName("Year")%></label>
				<input type="text" class="Number2RequiredTextBox" id="txtEditActivateMonth" name="txtEditActivateMonth" value="<%=HtmlUtility.escapeHTML(vo.getTxtEditActivateMonth())%>" />&nbsp;<label for="txtEditActivateMonth"><%=params.getName("Month")%></label>
				<input type="text" class="Number2RequiredTextBox" id="txtEditActivateDay" name="txtEditActivateDay" value="<%=HtmlUtility.escapeHTML(vo.getTxtEditActivateDay())%>" />&nbsp;<label for="txtEditActivateDay"><%=params.getName("Day")%></label>
			</td>
			<td class="TitleTd"><span class="RequiredLabel">*&nbsp;</span><span><label for="txtEditEmploymentCode"><%=params.getName("EmploymentContract","Code")%></label></span></td>
			<td class="InputTd" id="inputCode">
				<input type="text" class="Code10RequiredTextBox" id="txtEditEmploymentCode" name="txtEditEmploymentCode" value="<%=HtmlUtility.escapeHTML(vo.getTxtEditEmploymentCode())%>" />
			</td>
			<td class="TitleTd"><span class="RequiredLabel">*&nbsp;</span><span><label for="txtEditEmploymentName"><%=params.getName("EmploymentContract","Name")%></label></span></td>
			<td class="InputTd">
				<input type="text" class="Name15RequiredTextBox" id="txtEditEmploymentName" name="txtEditEmploymentName" value="<%=HtmlUtility.escapeHTML(vo.getTxtEditEmploymentName())%>" />
			</td>
		</tr>
		<tr>
			<td class="TitleTd"><span class="RequiredLabel">*&nbsp;</span><span><label for="txtEditEmploymentAbbr"><%=params.getName("EmploymentContract","Abbreviation") %></label></span></td>
			<td class="InputTd">
				<input type="text" class="Byte6RequiredTextBox" id="txtEditEmploymentAbbr" name="txtEditEmploymentAbbr" value="<%=HtmlUtility.escapeHTML(vo.getTxtEditEmploymentAbbr())%>"/>
			</td>
			<td class="TitleTd"><span><label for="pltEditInactivate"><%=params.getName("Effectiveness","Slash","Inactivate")%></label></span></td>
			<td class="InputTd">
				<select class="InactivateRequiredPullDown" id="pltEditInactivate" name="pltEditInactivate">
					<%= HtmlUtility.getSelectOption(params, PlatformConst.CODE_KEY_INACTIVATE_FLAG, vo.getPltEditInactivate(), false) %>
				</select>
			</td>
			<td class="Blank" colspan="2"></td>
		</tr>
	</table>
	<table class="ButtonTable">
		<tr>
			<td class="ButtonTd">
				<button type="button" class="Name2Button" id="btnRegist" onclick="submitRegist(event, 'divEdit', null, '<%= EmploymentMasterAction.CMD_REGIST %>')"><%=params.getName("Insert")%></button>
			</td>
		</tr>
	</table>
</div>
<div class="List" id="divSearch">
	<table class="InputTable" id="tblBaseSettingSearch">
		<tr>
			<th class="ListTableTh" colspan="6"><span class="TitleTh"><%=params.getName("Search")%></span></th>
		</tr>
		<tr>
			<td class="TitleTd"><span class="RequiredLabel">*&nbsp;</span><span><%=params.getName("ActivateDate")%></span></td>
			<td class="InputTd">
				<input type="text" class="Number4RequiredTextBox" id="txtSearchActivateYear"  name="txtSearchActivateYear" value="<%=HtmlUtility.escapeHTML(vo.getTxtSearchActivateYear())%>"/>&nbsp;<label for="txtSearchActivateYear"><%=params.getName("Year")%></label>
				<input type="text" class="Number2RequiredTextBox" id="txtSearchActivateMonth" name="txtSearchActivateMonth" value="<%=HtmlUtility.escapeHTML(vo.getTxtSearchActivateMonth())%>"/>&nbsp;<label for="txtSearchActivateMonth"><%=params.getName("Month")%></label>
				<input type="text" class="Number2RequiredTextBox" id="txtSearchActivateDay" name="txtSearchActivateDay" value="<%=HtmlUtility.escapeHTML(vo.getTxtSearchActivateDay())%>"/>&nbsp;<label for="txtSearchActivateDay"><%=params.getName("Day")%></label>
			</td>
			<td class="TitleTd"><span><label for="txtSearchEmploymentCode"><%=params.getName("EmploymentContract")%><%=params.getName("Code")%></label></span></td>
			<td class="InputTd">
				<input type="text" class="Code10TextBox" id="txtSearchEmploymentCode" name="txtSearchEmploymentCode" value="<%=HtmlUtility.escapeHTML(vo.getTxtSearchEmploymentCode())%>"/>
			</td>
			<td class="TitleTd"><span><label for="txtSearchEmploymentName"><%=params.getName("EmploymentContract")%><%=params.getName("Name")%></label></span></td>
			<td class="InputTd">
				<input type="text" class="Name15TextBox" id="txtSearchEmploymentName" name="txtSearchEmploymentName" value="<%=HtmlUtility.escapeHTML(vo.getTxtSearchEmploymentName())%>" />
			</td>
		</tr>
		<tr>
			<td class="TitleTd" ><span><label for="txtSearchEmploymentAbbr"><%=params.getName("EmploymentContract","Abbreviation") %></label></span></td>
			<td class="InputTd" >
				<input type="text" class="Byte6TextBox" id="txtSearchEmploymentAbbr" name="txtSearchEmploymentAbbr" value="<%=HtmlUtility.escapeHTML(vo.getTxtSearchEmploymentAbbr())%>" />
			</td>
			<td class="TitleTd"><span><label for="pltSearchInactivate"><%=params.getName("Effectiveness","Slash","Inactivate")%></label></span></td>
			<td class="InputTd">
				<select class="InactivatePullDown" id="pltSearchInactivate" name="pltSearchInactivate">
					<%= HtmlUtility.getSelectOption(params, PlatformConst.CODE_KEY_INACTIVATE_FLAG, vo.getPltSearchInactivate(), true) %>
				</select>
			</td>
			<td class="Blank" colspan="2"></td>
		</tr>
	</table>
	<table class="ButtonTable">
		<tr>
			<td class="ButtonTd">
				<button type="button" class="Name2Button" onclick="submitForm(event, 'divSearch', null, '<%=EmploymentMasterAction.CMD_SEARCH%>')"><%=params.getName("Search")%></button>
			</td>
		</tr>
	</table>
</div>
<%= HtmlUtility.getListInfoFlex(params, vo.getList(), vo.getPageCommand(), vo.getDataPerPage(), vo.getSelectIndex()) %>
<div class="FixList" id="divList">
	<table class="LeftListTable" id="list">
		<tr>
			<th class="ListSelectTh" id="thButton"></th>
			<th class="ListSortTh" id="thActivateDate" onclick="submitTransfer(event, null, null, new Array('<%=PlatformConst.PRM_TRANSFERRED_SORT_KEY%>', '<%=ActivateDateComparator.class.getName()%>'), '<%=EmploymentMasterAction.CMD_SORT%>');"><%=params.getName("ActivateDate")%><%= PlatformUtility.getSortMark(ActivateDateComparator.class.getName(), params) %></th>
			<th class="ListSortTh" id="thEmploymentCode" onclick="submitTransfer(event, null, null, new Array('<%= PlatformConst.PRM_TRANSFERRED_SORT_KEY %>', '<%= EmploymentContractCodeComparator.class.getName() %>'), '<%= EmploymentMasterAction.CMD_SORT %>');"><%= params.getName("EmploymentContract","Code") %><%= PlatformUtility.getSortMark(EmploymentContractCodeComparator.class.getName(), params) %></th>
			<th class="ListSortTh" id="thEmploymentName" onclick="submitTransfer(event, null, null, new Array('<%= PlatformConst.PRM_TRANSFERRED_SORT_KEY %>', '<%= EmploymentMasterEmployNameComparator.class.getName() %>'), '<%= EmploymentMasterAction.CMD_SORT %>');"><%= params.getName("EmploymentContract","Name") %><%= PlatformUtility.getSortMark(EmploymentMasterEmployNameComparator.class.getName(), params) %></th>
			<th class="ListSortTh" id="thEmploymentAbbr" onclick="submitTransfer(event, null, null, new Array('<%= PlatformConst.PRM_TRANSFERRED_SORT_KEY %>', '<%= EmploymentMasterEmployAbbrComparator.class.getName() %>'), '<%= EmploymentMasterAction.CMD_SORT %>');"><%=params.getName("EmploymentContract","Abbreviation") %><%= PlatformUtility.getSortMark(EmploymentMasterEmployAbbrComparator.class.getName(), params) %></th>
			<th class="ListSortTh" id="thInactivate" onclick="submitTransfer(event, null, null, new Array('<%= PlatformConst.PRM_TRANSFERRED_SORT_KEY %>', '<%= InactivateComparator.class.getName() %>'), '<%= EmploymentMasterAction.CMD_SORT %>');"><%= params.getName("EffectivenessExistence","Slash","InactivateExistence") %><%= PlatformUtility.getSortMark(InactivateComparator.class.getName(), params) %></th>
			<th class="ListSelectTh" id="thSelect">
<%
if (vo.getList().size() > 0) {
%>

				<input type="checkbox" onclick="doAllBoxChecked(this);" />
<%
}
%>
			</th>
		</tr>
<%
for (int i = 0; i < vo.getAryLblEmploymentCode().length; i++) {
%>
		<tr>
			<td class="ListSelectTd" id="tdButton">
				<button type="button" class="Name2Button"
					onclick="submitTransfer(event, null, null, new Array('<%= PlatformConst.PRM_TRANSFERRED_ACTIVATE_DATE %>', '<%= HtmlUtility.escapeHTML(vo.getAryLblActivateDate(i)) %>', '<%= PlatformConst.PRM_TRANSFERRED_CODE %>' , '<%= HtmlUtility.escapeHTML(vo.getAryLblEmploymentCode()[i]) %>'), '<%= EmploymentMasterAction.CMD_EDIT_MODE %>')"><%= params.getName("Select") %></button>
			</td>
			<td class="ListInputTd" ><%= HtmlUtility.escapeHTML(vo.getAryLblActivateDate(i)) %></td>
			<td class="ListInputTd" ><%= HtmlUtility.escapeHTML(vo.getAryLblEmploymentCode(i)) %></td>
			<td class="ListInputTd" ><%= HtmlUtility.escapeHTML(vo.getAryLblEmploymentName(i)) %></td>
			<td class="ListInputTd" ><%= HtmlUtility.escapeHTML(vo.getAryLblEmploymentAbbr(i)) %></td>
			<td class="ListSelectTd"><%= HtmlUtility.escapeHTML(vo.getAryLblInactivate(i)) %></td>
			<td class="ListSelectTd"><input type="checkbox" class="SelectCheckBox" name="ckbSelect" value="<%= vo.getAryCkbRecordId()[i] %>" <%= HtmlUtility.getChecked(vo.getAryCkbRecordId()[i], vo.getCkbSelect()) %> /></td>
		</tr>
<%
}
%>	
<%
if (vo.getList().size() > 0) {
%>
		<tr>
			<th class=UnderTd colspan="7">
				<span class="TableButtonSpan">
					<button type="button" class="Name4Button" id="btnDelete" onclick="submitDelete(event, 'divList', checkExtra, '<%= EmploymentMasterAction.CMD_DELETE %>')"><%= params.getName("History","Delete") %></button>
				</span>
			</th>
		</tr>
<%
}
%>
	</table>
</div>
<%
if (vo.getList().size() == 0) {
	return;
}
%>
<%= HtmlUtility.getListInfoFlex(params, vo.getList(), vo.getPageCommand(), vo.getDataPerPage(), vo.getSelectIndex()) %>
<div class="List" id="divUpdate">
	<table class="InputTable">
		<tr>
			<th class="UpdateTableTh" colspan="4"><span class="TitleTh"><%= params.getName("Bulk","Update") %></span></th>
		</tr>
		<tr>
			<td class="TitleTd"><span><%= params.getName("ActivateDate") %></span></td>
			<td class="InputTd">
				<input type="text" class="Number4RequiredTextBox" id="txtUpdateActivateYear" name="txtUpdateActivateYear" value="<%= HtmlUtility.escapeHTML(vo.getTxtUpdateActivateYear()) %>" />&nbsp;<label for="txtUpdateActivateYear"><%= params.getName("Year") %></label>
				<input type="text" class="Number2RequiredTextBox" id="txtUpdateActivateMonth" name="txtUpdateActivateMonth" value="<%= HtmlUtility.escapeHTML(vo.getTxtUpdateActivateMonth()) %>" />&nbsp;<label for="txtUpdateActivateMonth"><%= params.getName("Month") %></label>
				<input type="text" class="Number2RequiredTextBox" id="txtUpdateActivateDay" name="txtUpdateActivateDay" value="<%= HtmlUtility.escapeHTML(vo.getTxtUpdateActivateDay()) %>" />&nbsp;<label for="txtUpdateActivateDay"><%= params.getName("Day") %></label>
			</td>
			<td class="TitleTd"><span><%= params.getName("Effectiveness","Slash","Inactivate") %></span></td>
			<td class="InputTd">
				<select id="pltUpdateInactivate" name="pltUpdateInactivate">
					<%= HtmlUtility.getSelectOption(params, PlatformConst.CODE_KEY_INACTIVATE_FLAG, vo.getPltUpdateInactivate(), false) %>
				</select>
			</td>
		</tr>
	</table>
	<table class="ButtonTable">
		<tr>
			<td class="ButtonTd">
				<button type="button" class="Name2Button" id="btnEditUpdate" onclick="submitRegist(event, 'divUpdate', checkExtra, '<%= EmploymentMasterAction.CMD_BATCH_UPDATE %>')"><%= params.getName("Update") %></button>
			</td>
		</tr>
	</table>
</div>
<div class="MoveUpLink" id="divMoveUp">
	<a onclick="pageToTop();"><%= params.getName("UpperTriangular","TopOfPage") %></a>
</div>
