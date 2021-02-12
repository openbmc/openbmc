SUMMARY  = "Control process attributes through prctl"
DESCRIPTION = "The linux prctl function allows you to control specific characteristics of a \
process' behaviour. Usage of the function is fairly messy though, due to \
limitations in C and linux. This module provides a nice non-messy python(ic) \
interface."
SECTION = "devel/python"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1475481f9ec754d758859bd2c75f6f6f"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "5e12e398eb5c4e30d7b29b02458c76d2cc780700"
PV = "1.8.1+git${SRCPV}"

SRC_URI = "git://github.com/seveas/python-prctl;branch=main\
           file://0001-support-cross-complication.patch \
"
inherit setuptools3 python3native

DEPENDS += "libcap"

