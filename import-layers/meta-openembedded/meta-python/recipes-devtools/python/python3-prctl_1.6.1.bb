SUMMARY  = "Control process attributes through prctl"
DESCRIPTION = "The linux prctl function allows you to control specific characteristics of a \
process' behaviour. Usage of the function is fairly messy though, due to \
limitations in C and linux. This module provides a nice non-messy python(ic) \
interface."
SECTION = "devel/python"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=5eb2f4bcd60326f83e5deb542372d52f"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "1107d0be7bec4b28c85c62c454882d16844c930a"
PV = "1.6.1+git${SRCPV}"

SRC_URI = "git://github.com/seveas/python-prctl;branch=master \
           file://0001-support-cross-complication.patch \
"
inherit setuptools3 python3native

DEPENDS += "libcap"

