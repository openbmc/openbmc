require libx11.inc

DESCRIPTION += " Support for XCMS and XLOCALE is disabled in \
this version."

SRC_URI += "file://X18NCMSstubs.diff \
            file://fix-disable-xlocale.diff \
            file://fix-utf8-wrong-define.patch \
           "

RPROVIDES_${PN}-dev = "libx11-dev"
RPROVIDES_${PN}-locale = "libx11-locale"

SRC_URI[md5sum] = "0f618db70c4054ca67cee0cc156a4255"
SRC_URI[sha256sum] = "4d3890db2ba225ba8c55ca63c6409c1ebb078a2806de59fb16342768ae63435d"

EXTRA_OECONF += "--disable-xlocale"

PACKAGECONFIG ??= ""
