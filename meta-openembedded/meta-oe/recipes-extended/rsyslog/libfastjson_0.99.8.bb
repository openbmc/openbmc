SUMMARY = "A fork of json-c library"
HOMEPAGE = "https://github.com/rsyslog/libfastjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a958bb07122368f3e1d9b2efe07d231f"

DEPENDS = ""

SRC_URI = "git://github.com/rsyslog/libfastjson.git;protocol=https \
           file://0001-fix-jump-misses-init-gcc-8-warning.patch"

SRCREV = "4758b1caf69ada911ef79e1d80793fe489b98dff"

S = "${WORKDIR}/git"

inherit autotools
