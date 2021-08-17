FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

SRC_URI:append:kudo = " file://kudo.cfg"
