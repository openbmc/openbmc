SUMMARY = "Library for solving packages and reading repositories"
HOMEPAGE = "https://github.com/openSUSE/libsolv"
BUGTRACKER = "https://github.com/openSUSE/libsolv/issues"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=62272bd11c97396d4aaf1c41bc11f7d8"

DEPENDS = "expat zlib"

SRC_URI = "git://github.com/openSUSE/libsolv.git \
"

SRCREV = "5205c5683ce2c8b8288534059ba40a318a2a161d"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??= "rpm"
PACKAGECONFIG[rpm] = "-DENABLE_RPMMD=ON -DENABLE_RPMDB=ON,,rpm"

EXTRA_OECMAKE = "-DMULTI_SEMANTICS=ON -DENABLE_COMPLEX_DEPS=ON"

PACKAGES =+ "${PN}-tools ${PN}ext"

FILES_${PN}-tools = "${bindir}/*"
FILES_${PN}ext = "${libdir}/${PN}ext.so.*"

BBCLASSEXTEND = "native nativesdk"
