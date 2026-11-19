FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${BPN}:"

SRC_URI:append:kudo = " file://kudo.cfg"
