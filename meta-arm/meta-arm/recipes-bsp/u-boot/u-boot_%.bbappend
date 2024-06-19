FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:qemuarm64-secureboot = " file://qemuarm64.cfg"
SRC_URI:append:qemuarm-secureboot = " file://qemuarm.cfg"
