require libx11.inc

SRC_URI += "file://disable_tests.patch"

inherit gettext

BBCLASSEXTEND = "native nativesdk"
