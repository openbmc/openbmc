FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG_append_ibm-ac-server = " policy-interface"
PACKAGECONFIG_append_mihawk = " policy-interface"
