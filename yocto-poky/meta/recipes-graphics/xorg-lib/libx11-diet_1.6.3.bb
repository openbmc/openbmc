require libx11.inc

DESCRIPTION += " Support for XCMS and XLOCALE is disabled in \
this version."

SRC_URI += "file://X18NCMSstubs.diff \
            file://fix-disable-xlocale.diff \
            file://fix-utf8-wrong-define.patch \
           "

RPROVIDES_${PN}-dev = "libx11-dev"
RPROVIDES_${PN}-locale = "libx11-locale"

SRC_URI[md5sum] = "2e36b73f8a42143142dda8129f02e4e0"
SRC_URI[sha256sum] = "cf31a7c39f2f52e8ebd0db95640384e63451f9b014eed2bb7f5de03e8adc8111"

EXTRA_OECONF += "--disable-xlocale"

PACKAGECONFIG ??= ""
