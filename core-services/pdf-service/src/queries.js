const { Pool } = require('pg');
import logger from "./config/logger";
import producer from "./kafka/producer";
import envVariables from "./EnvironmentVariables";
import PDFMerger from 'pdf-merger-js';
import { fileStoreAPICall } from "./utils/fileStoreAPICall";
import fs, {
  exists
} from "fs";

const pool = new Pool({
  user: envVariables.DB_USER,
  host: envVariables.DB_HOST,
  database: envVariables.DB_NAME,
  password: envVariables.DB_PASSWORD,
  port: envVariables.DB_PORT,
})

let createJobKafkaTopic = envVariables.KAFKA_CREATE_JOB_TOPIC;
const uuidv4 = require("uuid/v4");

export const getFileStoreIds = (
  jobid,
  tenantId,
  isconsolidated,
  entityid,
  callback
) => {
  var searchquery = "";
  var queryparams = [];
  var next = 1;
  var jobidPresent = false;
  searchquery = "SELECT * FROM egov_pdf_gen WHERE";

  if (jobid != undefined && jobid.length > 0) {
    searchquery += ` jobid = ANY ($${next++})`;
    queryparams.push(jobid);
    jobidPresent = true;
  }

  if (entityid != undefined && entityid.trim() !== "") {
    if (jobidPresent) searchquery += " and";
    searchquery += ` entityid = ($${next++})`;
    queryparams.push(entityid);
  }

  if (tenantId != undefined && tenantId.trim() !== "") {
    searchquery += ` and tenantid = ($${next++})`;
    queryparams.push(tenantId);
  }

  if (isconsolidated != undefined && isconsolidated.trim() !== "") {
    var ifTrue = isconsolidated === "true" || isconsolidated === "True";
    var ifFalse = isconsolidated === "false" || isconsolidated === "false";
    if (ifTrue || ifFalse) {
      searchquery += ` and isconsolidated = ($${next++})`;
      queryparams.push(ifTrue);
    }
  }
  searchquery = `SELECT pdf_1.* FROM egov_pdf_gen pdf_1 INNER JOIN (SELECT entityid, max(endtime) as MaxEndTime from (`+searchquery+`) as pdf_2 group by entityid) pdf_3 ON pdf_1.entityid = pdf_3.entityid AND pdf_1.endtime = pdf_3.MaxEndTime`;
  pool.query(searchquery, queryparams, (error, results) => {
    if (error) {
      logger.error(error.stack || error);
      callback({
        status: 400,
        message: `error occured while searching records in DB : ${error.message}`
      });
    } else {
      if (results && results.rows.length > 0) {
        var searchresult = [];
        results.rows.map(crow => {
          searchresult.push({
            filestoreids: crow.filestoreids,
            jobid: crow.jobid,
            tenantid: crow.tenantid,
            createdtime: crow.createdtime,
            endtime: crow.endtime,
            totalcount: crow.totalcount,
            key: crow.key,
            documentType: crow.documenttype,
            moduleName: crow.modulename
          });
        });
        logger.info(results.rows.length + " matching records found in search");
        callback({ status: 200, message: "Success", searchresult });
      } else {
        logger.error("no result found in DB search");
        callback({ status: 404, message: "no matching result found" });
      }
    }
  });
};

export const insertStoreIds = (
  dbInsertRecords,
  jobid,
  filestoreids,
  tenantId,
  starttime,
  successCallback,
  errorCallback,
  totalcount,
  key,
  documentType,
  moduleName
) => {
  var payloads = [];
  var endtime = new Date().getTime();
  var id = uuidv4();
  payloads.push({
    topic: createJobKafkaTopic,
    messages: JSON.stringify({ jobs: dbInsertRecords })
  });
  producer.send(payloads, function(err, data) {
    if (err) {
      logger.error(err.stack || err);
      errorCallback({
        message: `error while publishing to kafka: ${err.message}`
      });
    } else {
      logger.info("jobid: " + jobid + ": published to kafka successfully");
      successCallback({
        message: "Success",
        jobid: jobid,
        filestoreIds: filestoreids,
        tenantid: tenantId,
        starttime,
        endtime,
        totalcount,
        key,
        documentType,
        moduleName
      });
    }
  });
};

export async function insertRecords(bulkPdfJobId, totalPdfRecords, currentPdfRecords, userid) {
  try {
    const result = await pool.query('select * from egov_bulk_pdf_info where jobid = $1', [bulkPdfJobId]);
    if(result.rowCount<1){
      const insertQuery = 'INSERT INTO egov_bulk_pdf_info(jobid, uuid, recordscompleted, totalrecords, createdtime, filestoreid, lastmodifiedby, lastmodifiedtime) VALUES ($1, $2, $3, $4, $5, $6, $7, $8)';
      const curentTimeStamp = new Date().getTime();
      await pool.query(insertQuery,[bulkPdfJobId, userid, currentPdfRecords, totalPdfRecords, curentTimeStamp, null, userid, curentTimeStamp]);
    }
    else{
      var recordscompleted = parseInt(result.rows[0].recordscompleted);
      var totalrecords = parseInt(result.rows[0].totalrecords);
      if(recordscompleted < totalrecords){
        const updateQuery = 'UPDATE egov_bulk_pdf_info SET recordscompleted = recordscompleted + $1, lastmodifiedby = $2, lastmodifiedtime = $3 WHERE jobid = $4';
        const curentTimeStamp = new Date().getTime();
        await pool.query(updateQuery,[currentPdfRecords, userid, curentTimeStamp, bulkPdfJobId]);
      }
    }
  } catch (err) {
    logger.error(err.stack || err);
  } 
}

export async function mergePdf(bulkPdfJobId, tenantId, userid){

  try {
    const updateResult = await pool.query('select * from egov_bulk_pdf_info where jobid = $1', [bulkPdfJobId]);
    var recordscompleted = parseInt(updateResult.rows[0].recordscompleted);
    var totalrecords = parseInt(updateResult.rows[0].totalrecords);
    
    if(recordscompleted == totalrecords){
      var merger = new PDFMerger();
      //var baseFolder = envVariables.SAVE_PDF_DIR + bulkPdfJobId + '/';
      var baseFolder = process.cwd() + '/' + bulkPdfJobId + '/';
    
      let fileNames = fs.readdirSync(baseFolder);
      //console.log('Files to be merged: ',fileNames);
      (async () => {
        try {
          for (let i = 0; i < fileNames.length; i++){
            //console.log(baseFolder+fileNames[i]);
            merger.add(baseFolder+fileNames[i]);            //merge all pages. parameter is the path to file and filename.
          }
          await merger.save(baseFolder+'/output.pdf');        //save under given name and reset the internal document
        } catch (err) {
          logger.error(err.stack || err);
        }
      
        var mergePdfData = fs.createReadStream(baseFolder+'output.pdf');
        await fileStoreAPICall('output.pdf', tenantId, mergePdfData).then((filestoreid) => {
          const updateQuery = 'UPDATE egov_bulk_pdf_info SET filestoreid = $1, lastmodifiedby = $2, lastmodifiedtime = $3 WHERE jobid = $4';
          const curentTimeStamp = new Date().getTime();
          pool.query(updateQuery,[filestoreid, userid, curentTimeStamp, bulkPdfJobId]);
        }).catch((err) => {
          logger.error(err.stack || err);
        });

        fs.rmdirSync(baseFolder, { recursive: true });

      })();
    }
  } catch (err) {
    logger.error(err.stack || err);
  }
  
}


export async function getBulkPdfRecordsDetails(userid, offset, limit){
  try {
    let data = [];
    const result = await pool.query('select * from egov_bulk_pdf_info where uuid = $1 limit $2 offset $3', [userid, limit, offset]);
    if(result.rowCount>=1){
      
      for(let row of result.rows){
        let value = {
          jobid: row.jobid,
          uuid: row.uuid,
          totalrecords: row.totalrecords,
          recordscompleted: row.recordscompleted,
          filestoreid: row.filestoreid,
          createdtime: row.createdtime,
          lastmodifiedby: row.lastmodifiedby,
          lastmodifiedtime: row.lastmodifiedtime
        };
        data.push(value);
      }
    }
    return data;
    
  } catch (err) {
    logger.error(err.stack || err);
    
  }

}