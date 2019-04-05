require libx11.inc

SRC_URI += "file://disable_tests.patch \
            file://Fix-hanging-issue-in-_XReply.patch \
           "

inherit gettext

do_configure_append () {
    sed -i -e "/X11_CFLAGS/d" ${B}/src/util/Makefile
}

BBCLASSEXTEND = "native nativesdk"
