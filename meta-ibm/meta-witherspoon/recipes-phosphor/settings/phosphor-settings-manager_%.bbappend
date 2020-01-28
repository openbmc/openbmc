FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append_ibm-ac-server = " file://TPMEnable-default-true.override.yml"
SRC_URI_append_ibm-ac-server += " file://ClearHostSecurityKeys-default-zero.override.yml"
SRC_URI_append_mihawk = " file://TPMEnable-default-true.override.yml"
