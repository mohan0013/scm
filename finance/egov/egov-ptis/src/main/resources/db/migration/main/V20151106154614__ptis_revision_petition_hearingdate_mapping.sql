﻿insert into eg_roleaction (roleid, actionid) values ((select id from eg_role  where name  = 'Property Approver'), (select id from eg_action where name = 'PropTax Rev Petition Add hearing'));