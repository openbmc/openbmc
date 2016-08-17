require libx11.inc
inherit gettext

BBCLASSEXTEND = "native nativesdk"

SRC_URI += "file://disable_tests.patch \
            file://libX11-Add-missing-NULL-check.patch \
           "

SRC_URI[md5sum] = "2e36b73f8a42143142dda8129f02e4e0"
SRC_URI[sha256sum] = "cf31a7c39f2f52e8ebd0db95640384e63451f9b014eed2bb7f5de03e8adc8111"
