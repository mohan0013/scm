import React from "react";
import { useTranslation } from "react-i18next";
import { useQuery } from "react-query";

const useApplicationStatus = (select, isEnabled = true, statusMap=[]) => {
  const { t } = useTranslation();

  const userInfo = Digit.UserService.getUser();
  const userRoles = userInfo.info.roles.map((roleData) => roleData.code);

  const workflowOrder = [
    "CREATED",
    "PENDING_APPL_FEE_PAYMENT",
    "ASSING_DSO",
    "PENDING_DSO_APPROVAL",
    "DSO_REJECTED",
    "DSO_INPROGRESS",
    "REJECTED",
    "CANCELED",
    "COMPLETED",
    "CITIZEN_FEEDBACK_PENDING",
  ];

  const DSO = Digit.UserService.hasAccess(["FSM_DSO"]);
  const allowedStatusForDSO = ["PENDING_DSO_APPROVAL", "DSO_INPROGRESS", "COMPLETED", "DSO_REJECTED"];

  const tenantId = Digit.ULBService.getCurrentTenantId();
  const fetch = async () => {
    let WorkflowService = await Digit.WorkflowService.init(tenantId, "FSM");
    return workflowOrder.map(
      (status) => WorkflowService.BusinessServices[0].states?.filter((workflowDetails) => status === workflowDetails?.state)[0]
    );
  };

  const roleWiseSelect = (WorkflowService) => {
    const response = WorkflowService.filter((state) => state.applicationStatus)
      .filter((status) => {
        if (status.actions === null) return 0;
        const ref = status.actions.reduce((prev, curr) => [...prev, ...curr.roles], []);
        const res = [userRoles, ref].reduce((a, c) => a.filter((i) => c.includes(i)));
        return res.length;
      })
      .map((state) => {
        const roles = state.actions?.map((e) => e.roles)?.flat();
        return {
          name: t(`CS_COMMON_FSM_${state.applicationStatus}`),
          code: state.applicationStatus,
          id: statusMap?.filter(e => e.applicationstatus === state.applicationStatus)?.[0]?.statusid,
          roles,
        };
      })
    return response;
  };

  const defaultSelect = (WorkflowService) => {
    let applicationStatus = WorkflowService.filter((state) => state.applicationStatus).map((state) => {
      const roles = state.actions?.map((e) => e.roles)?.flat();
      return {
        name: t(`CS_COMMON_FSM_${state.applicationStatus}`),
        code: state.applicationStatus,
        id: statusMap?.filter(e => e.applicationstatus === state.applicationStatus)?.[0]?.statusid,
        roles,
      };
    });

    // // console.log("find filter status",DSO ? allowedStatusForDSO.map(item => applicationStatus.filter(status => status.code === item)[0] ) : applicationStatus);
    return DSO ? allowedStatusForDSO.map((item) => applicationStatus.filter((status) => status.code === item)[0]) : applicationStatus;
  };
  return useQuery(
    ["APPLICATION_STATUS", isEnabled],
    () => fetch(),
    select ? { select: roleWiseSelect, enabled: isEnabled } : { select: defaultSelect, enabled: isEnabled }
  );
};

export default useApplicationStatus;
