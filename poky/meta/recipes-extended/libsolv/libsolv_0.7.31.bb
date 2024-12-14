SUMMARY = "Library for solving packages and reading repositories"
DESCRIPTION = "This is libsolv, a free package dependency solver using a satisfiability algorithm for solving packages and reading repositories"
HOMEPAGE = "https://github.com/openSUSE/libsolv"
BUGTRACKER = "https://github.com/openSUSE/libsolv/issues"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=62272bd11c97396d4aaf1c41bc11f7d8"

DEPENDS = "expat zlib zstd"

SRC_URI = "git://github.com/openSUSE/libsolv.git;branch=master;protocol=https \
           file://0001-utils-Conside-musl-when-wrapping-qsort_r.patch \
"

SRCREV = "95aef5cc57d0f51782e55b6903b30ec93afbfa15"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??= "${@bb.utils.contains('PACKAGE_CLASSES','package_rpm','rpm','',d)}"
PACKAGECONFIG[rpm] = "-DENABLE_RPMMD=ON -DENABLE_RPMDB=ON,,rpm"

EXTRA_OECMAKE = "-DMULTI_SEMANTICS=ON -DENABLE_COMPLEX_DEPS=ON -DENABLE_ZSTD_COMPRESSION=ON"

PACKAGES =+ "${PN}-tools ${PN}ext"

FILES:${PN}-tools = "${bindir}/*"
FILES:${PN}ext = "${libdir}/${PN}ext.so.*"

BBCLASSEXTEND = "native nativesdk"
