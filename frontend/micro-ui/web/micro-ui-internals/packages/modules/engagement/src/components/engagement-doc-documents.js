import { CardLabel, LabelFieldPair, TextInput, UploadFile } from "@egovernments/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { Controller } from "react-hook-form";

const SelectULB = ({ userType, t, onSelect, setValue, config, data, formData, register, errors, setError, clearErrors, formState, control }) => {
  const [fileStoreId, setFileStoreId] = useState(() => formData?.[config.key]?.filestoreId);
  const [file, setFile] = useState();
  const [urlDisabled, setUrlDisabled] = useState(false);
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [controllerProps, setProps] = useState({});

  // TO BE UNCOMMENTED IF BOTH URL AND FILESTORE ID CANT BE SEND
  const disableUrlField = () => {
    // setUrlDisabled(true);
    // setValue(config.key + ".documentLink", "");
  };

  const selectFile = (e, props) => {
    setFile(e.target.files[0]);
    setProps(props);
  };

  useEffect(() => {
    if (file) uploadFile();
  }, [file]);

  useEffect(() => {
    if (fileStoreId) disableUrlField();
    else setUrlDisabled(false);
    controllerProps?.onChange?.(fileStoreId);
  }, [fileStoreId, controllerProps]);

  useEffect(() => {
    // console.log(formData, "assac >>> sassa");
    // if(!fileStoreId && url !== formData?.[config.key]) setValue(config.key,url)
  }, [formData?.[config?.key]?.["documentLink"]]);

  const uploadFile = async () => {
    try {
      const response = await Digit.UploadServices.Filestorage("engagement", file, tenantId?.split(".")[0]);
      if (response?.data?.files?.length > 0) {
        setFileStoreId(response?.data?.files[0]?.fileStoreId);
      } else {
        setError(t("CS_FILE_UPLOAD_ERROR"));
      }
    } catch (err) {
      console.error("Modal -> err ", err);
    }
  };

  return (
    <React.Fragment>
      <LabelFieldPair>
        <CardLabel style={{ fontWeight: "bold" }}>{t("ES_COMMON_DOC_DOCUMENT") + "*"}</CardLabel>
        <div className="field">
          <Controller
            name={config.key + ".filestoreId"}
            control={control}
            render={(props) => (
              <UploadFile
                id={"city-engagement-doc"}
                onUpload={(d) => selectFile(d, props)}
                onDelete={() => {
                  setFileStoreId(null);
                }}
                showHintBelow={true}
                hintText={t("DOCUMENTS_ATTACH_RESTRICTIONS_SIZE")}
                message={fileStoreId ? `1 ${t(`CS_ACTION_FILEUPLOADED`)}` : t(`CS_ACTION_NO_FILEUPLOADED`)}
                textStyles={{ width: "100%" }}
                inputStyles={{ width: "280px" }}
              />
            )}
          />
        </div>
      </LabelFieldPair>

      <LabelFieldPair style={{ margin: "25px" }}>
        <CardLabel></CardLabel>
        <div style={{ textAlign: "center" }} className="field links-wrapper">
          <span style={{ color: "#505a5f", fontWeight: "bold" }} className="cell-text">
            {"(" + t("CS_COMMON_OR") + ")"}
          </span>
        </div>
      </LabelFieldPair>

      <LabelFieldPair>
        <CardLabel style={{ fontWeight: "bold" }}>{t("ES_COMMON_LINK_LABEL") + "*"}</CardLabel>
        <div className="field">
          <TextInput name={config.key + ".documentLink"} inputRef={register()} disable={urlDisabled} />
        </div>
      </LabelFieldPair>
    </React.Fragment>
  );
};

export default SelectULB;
