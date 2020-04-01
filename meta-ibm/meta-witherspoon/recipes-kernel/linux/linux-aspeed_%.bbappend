FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI_append_ibm-ac-server = " file://witherspoon.cfg"
SRC_URI_append_rainier = " file://rainier.cfg"
SRC_URI_append_mihawk = " file://mihawk.cfg"
