SUMMARY = "Yet Another JSON Library."

DESCRIPTION = "YAJL is a small event-driven (SAX-style) JSON parser \
written in ANSI C, and a small validating JSON generator."

HOMEPAGE = "http://lloyd.github.com/yajl/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=39af6eb42999852bdd3ea00ad120a36d"

SRC_URI = "git://github.com/lloyd/yajl"
SRCREV = "a0ecdde0c042b9256170f2f8890dd9451a4240aa"

S = "${WORKDIR}/git"

inherit cmake lib_package

EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib', True).replace('lib', '')}"
