FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:ibm-ac-server = " file://ibm-ac-server.cfg"
SRC_URI:append:ibm-enterprise = " file://ibm-enterprise.cfg"
