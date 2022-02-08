SUMMARY = "Yet Another JSON Library."

DESCRIPTION = "YAJL is a small event-driven (SAX-style) JSON parser \
written in ANSI C, and a small validating JSON generator."

HOMEPAGE = "http://lloyd.github.com/yajl/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=da2e9aa80962d54e7c726f232a2bd1e8"

# Use 1.0.12 tag
SRCREV = "17b1790fb9c8abbb3c0f7e083864a6a014191d56"
SRC_URI = "git://github.com/lloyd/yajl;nobranch=1;protocol=https"

inherit cmake lib_package

S = "${WORKDIR}/git"

EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')}"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>1(\.\d+)+)"
