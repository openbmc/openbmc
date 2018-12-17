require libx11.inc
inherit gettext

BBCLASSEXTEND = "native nativesdk"

SRC_URI += "file://disable_tests.patch \
            file://Fix-hanging-issue-in-_XReply.patch \
           "
do_configure_append () {
    sed -i -e "/X11_CFLAGS/d" ${B}/src/util/Makefile
}

SRC_URI[md5sum] = "6b0f83e851b3b469dd660f3a95ac3e42"
SRC_URI[sha256sum] = "65fe181d40ec77f45417710c6a67431814ab252d21c2e85c75dd1ed568af414f"
