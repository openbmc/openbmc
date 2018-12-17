SUMMARY  = "Control process attributes through prctl"
DESCRIPTION = "The linux prctl function allows you to control specific characteristics of a \
process' behaviour. Usage of the function is fairly messy though, due to \
limitations in C and linux. This module provides a nice non-messy python(ic) \
interface."
SECTION = "devel/python"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=23ff9f50449d4bd0e513df16e4d9755f"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "57cd0a7cad76e8f8792eea22ee5b5d17bae0a90f"
PV = "1.7+git${SRCPV}"

SRC_URI = "git://github.com/seveas/python-prctl;branch=master \
           file://0001-support-cross-complication.patch \
"
inherit setuptools3 python3native

DEPENDS += "libcap"

