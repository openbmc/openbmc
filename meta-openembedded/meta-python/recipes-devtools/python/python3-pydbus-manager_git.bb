SUMMARY = "DBus.ObjectManager implementation for pydbus"
AUTHOR = "Sébastien Corne"

LICENSE = "WTFPL"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fd5bb1dae91ba145745db55870be6a7"

inherit setuptools3

SRC_URI = "git://github.com/seebz/pydbus-manager.git"
SRCREV = "6b576b969cbda50521dca62a7df929167207f9fc"
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
