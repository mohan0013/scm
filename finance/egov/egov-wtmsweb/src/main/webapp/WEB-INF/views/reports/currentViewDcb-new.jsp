<!-- #-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#------------------------------------------------------------------------------- -->
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-primary" data-collapsed="0">

			<form:form method="post" modelAttribute="dcbReport"
				id="editmeterWate11rConnectionform"
				cssClass="form-horizontal form-groups-bordered"
				enctype="multipart/form-data">
				<div class="page-container" id="page-container">
					<form:hidden id="mode" path="" name="mode" value="${mode}" />
					<input type="hidden" id="consumerCode" name="consumerCode"
						value="${waterConnectionDetails.connection.consumerCode}" /> <input
						type="hidden" id="totalRcptAmt" name="totalRcptAmt"
						value="${totalRcptAmt}" />
					<div class="panel-heading">
						<div class="panel-title">
							<spring:message code="lbl.basicdetails" />
						</div>
					</div>
					<jsp:include page="../application/commonappdetails-view.jsp" />
					<tr>
						<td class="blueborderfortd">&nbsp;</td>
						<td class="blueborderfortd">&nbsp;</td>
						<div class="panel-heading">
							<div class="panel-title">View Dcb</div>
						</div>
					</tr>
					<tr>
						<td colspan="8">
							<table
								class="table table-bordered datatable dt-responsive table-hover multiheadertbl"
								id="tbldcbdrilldown">
								<tr>
									<th class="bluebgheadtd" width="20%" colspan="1">
										Year-Month</th>
									<th class="bluebgheadtd" width="30%" align="center" colspan="1">
										Demand</th>

									<th class="bluebgheadtd" width="30%" align="center" colspan="1">
										Collection</th>
									<th class="bluebgheadtd" width="30%" align="center" colspan="1">
										Balance</th>
								</tr>
								<tr>
									<th>Installments</th>
									<th>Water charges</th>

									<th>Water charges</th>

									<th>Water charges</th>


								</tr>

								<c:choose>
									<c:when test="${!dcbReport.records.isEmpty()}">
										<c:forEach items="${dcbReport.records}" var="var1"
											varStatus="counter">
											<tr>
												<td class="blueborderfortd">
													<div align="center">
														<c:out value="${var1.key}" />
													</div>
												</td>
												<td class="blueborderfortd">
													<div align="right">
														<c:forEach items="${var1.value.demands}" var="var2"
															varStatus="counter">
															<c:out value="${var2.value}" />
														</c:forEach>
													</div>
												</td>

												<td class="blueborderfortd">
													<div align="right">
														<c:forEach items="${var1.value.collections}" var="var3"
															varStatus="counter">
															<c:out value="${var3.value}" />
														</c:forEach>
													</div>
												</td>

												<td class="blueborderfortd">
													<div align="right">
														<c:forEach items="${var1.value.rebates}" var="var4"
															varStatus="counter">
															<c:out value="${var4.value}" />
														</c:forEach>
													</div>
												</td>
											</tr>
										</c:forEach>
									</c:when>
								</c:choose>
								<tr>
								<tr>
									<td class="blueborderfortd">
										<div align="right">
											<b>Total:</b>
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">
											<span class="bold"> <c:out
													value="${dcbReport.totalDmdTax}" />
											</span>
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="right">
											<span class="bold"> <c:out
													value="${dcbReport.totalColTax}" />
											</span>
										</div>
									</td>

									<td class="blueborderfortd">
										<div align="right">

											<span class="bold"> <c:out
													value="${dcbReport.totalBalance}" />
											</span>
										</div>
									</td>

									<td class="blueborderfortd">&nbsp;</td>

									<td class="blueborderfortd">&nbsp;</td>
								</tr>
								<tr>
									<td class="blueborderfortd">&nbsp;</td>
									<td class="blueborderfortd">&nbsp;</td>
									<td class="blueborderfortd">
										<div align="right">
											<b>Tax Due:</b>
										</div>
									</td>

									<td class="blueborderfortd">
										<div align="right">

											<span class="bold"> <c:out
													value="${dcbReport.totalBalance}" />
											</span>
										</div>
									</td>

									<td class="blueborderfortd">&nbsp;</td>

									<td class="blueborderfortd">&nbsp;</td>
								</tr>
								<tr>
									<td class="blueborderfortd">&nbsp;</td>
									<td class="blueborderfortd">&nbsp;</td>
									<td class="blueborderfortd">
										<div align="right">
											<b>Total Balance:</b>
										</div>
									</td>

									<td class="blueborderfortd">
										<div align="right">

											<span class="bold"> <c:out
													value="${dcbReport.totalBalance}" />
											</span>
										</div>
									</td>

									<td class="blueborderfortd">&nbsp;</td>

									<td class="blueborderfortd">&nbsp;</td>
								</tr>
							</table>
						<td class="blueborderfortd">&nbsp;</td>
						<td class="blueborderfortd">&nbsp;</td>

					</tr>
					<c:if test="${!activeRcpts.isEmpty()}">
						<td colspan="9"><div class="panel-heading">
								<div class="panel-title">Payment Details</div>
							</div></td>
						<tr>
							<td colspan="3">
								<table
									class="table table-bordered datatable dt-responsive table-hover multiheadertbl"
									id="tbldcbdrilldown">

									<tr>
									<th class="bluebgheadtd" align="center">Receipt Number</th>
									<th class="bluebgheadtd" align="center">Receipt Date</th>
									<th class="bluebgheadtd" align="center">Receipt TotalAmount</th>
									</tr>
									<c:forEach items="${activeRcpts}" var="rcpt" varStatus="r">

										<input type="hidden" id="${rcpt.receiptNumber}"
											value="${rcpt.receiptNumber}" />
										<tr>
											<td class="blueborderfortd">
												<div align="center">

													<%--  <a href="javascript:void(0)" onclick="redirecttocoll(this);"> <c:out value="${rcpt.receiptNumber}" /> </a>
											 --%>
													<a
														href="/../collection/citizen/onlineReceipt-viewReceipt.action?receiptNumber=<c:out value="${rcpt.receiptNumber}" />&consumerCode=<c:out value="${consumerCode}" />&serviceCode=WT"
														target="_blank"> <c:out value="${rcpt.receiptNumber}" />
													</a>
												</div>
											</td>
											<td class="blueborderfortd">
												<div align="center">
													<!-- <s:date format="%{@org.egov.ptis.property.utils.PropertyTaxConstants@DATE_FORMAT_DDMMYYY}" name="#rcpt.getReceiptDate()" var="rcptDate" /> -->
													<fmt:formatDate pattern="dd/MM/yyyy"
														value="${rcpt.receiptDate}" />

												</div>
											</td>
											<td class="blueborderfortd">
												<div align="center">
													<c:out value="${rcpt.receiptAmt}" />
												</div>
											</td>
										</tr>
									</c:forEach>

									<tr>
										<td class="blueborderfortd">&nbsp;</td>

										<td class="blueborderfortd">
											<div align="right">
												<span class="bold">Receipt Total: </span>
											</div>
										</td>
										<td class="blueborderfortd">
											<div align="center">
												<span class="bold"> <c:out value="${totalRcptAmt}" />
												</span>
											</div>
										</td>
									</tr>
								</table>
					</c:if>
					<table class="table table-bordered datatable dt-responsive table-hover multiheadertbl"
								id="tbldcbdrilldown">
								<td colspan="9"><div class="panel-heading">
										<div class="panel-title">Dishonoured/Cancelled Receipt
											Details</div>
									</div></td>
								

								<tr>
									<th class="bluebgheadtd" align="center">Receipt Number</th>
									<th class="bluebgheadtd" align="center">Receipt Date</th>
									<th class="bluebgheadtd" align="center">Receipt TotalAmount</th>
								</tr>
								<c:forEach items="${cancelRcpt}" var="actr" varStatus="r22">
									<input type="hidden" id="${actr.receiptNumber}"
										value="${actr.receiptNumber}" />
									<tr>
										<td class="blueborderfortd">
											<div align="center">

												<a
													href="/../collection/citizen/onlineReceipt-viewReceipt.action?receiptNumber=<c:out value="${rcpt.receiptNumber}" />&consumerCode=<c:out value="${consumerCode}" />&serviceCode=WT"
													target="_blank"> <c:out value="${actr.receiptNumber}" />
												</a>

											</div>
										</td>
										<td class="blueborderfortd">
											<div align="center">
												<fmt:formatDate pattern="dd/MM/yyyy"
													value="${actr.receiptDate}" />
											</div>
										</td>
										<td class="blueborderfortd">
											<div align="center">
												<c:out value="${actr.receiptAmt}" />
											</div>
										</td>
									</tr>
								</c:forEach>
								<tr>
									<td class="blueborderfortd">&nbsp;</td>

									<td class="blueborderfortd">
										<div align="right">
											<span class="bold">Receipt Total: </span>
										</div>
									</td>
									<td class="blueborderfortd">
										<div align="center">
											<span class="bold"> <c:out
													value="${CanceltotalRcptAmt}" />
											</span>
										</div>
									</td>
								</tr>
							</table>
						
					<div class="row">
						<div class="text-center">
							<a href="javascript:void(0);" class="btn btn-primary"
								onclick="self.close()"> <spring:message code='lbl.close' />
							</a>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
</div>


<script src="<c:url value='/resources/js/app/applicationsuccess.js'/>"></script>