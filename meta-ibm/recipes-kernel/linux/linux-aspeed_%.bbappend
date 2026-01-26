FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:ibm-ac-server = " file://witherspoon.cfg"
SRC_URI:append:ibm-enterprise = " file://ibm-enterprise.cfg"
