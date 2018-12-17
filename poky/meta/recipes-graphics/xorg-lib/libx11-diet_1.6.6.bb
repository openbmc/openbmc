require libx11.inc

DESCRIPTION += " Support for XCMS and XLOCALE is disabled in \
this version."

SRC_URI += "file://X18NCMSstubs.patch \
            file://fix-disable-xlocale.patch \
            file://fix-utf8-wrong-define.patch \
           "

RPROVIDES_${PN}-dev = "libx11-dev"
RPROVIDES_${PN}-locale = "libx11-locale"

SRC_URI[md5sum] = "6b0f83e851b3b469dd660f3a95ac3e42"
SRC_URI[sha256sum] = "65fe181d40ec77f45417710c6a67431814ab252d21c2e85c75dd1ed568af414f"

EXTRA_OECONF += "--disable-xlocale"

PACKAGECONFIG ??= ""
