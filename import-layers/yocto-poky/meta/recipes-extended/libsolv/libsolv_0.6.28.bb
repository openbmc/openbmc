SUMMARY = "Library for solving packages and reading repositories"
HOMEPAGE = "https://github.com/openSUSE/libsolv"
BUGTRACKER = "https://github.com/openSUSE/libsolv/issues"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=62272bd11c97396d4aaf1c41bc11f7d8"

DEPENDS = "expat zlib rpm"

SRC_URI = "git://github.com/openSUSE/libsolv.git \
           file://0001-repo_rpmdb.c-increase-MAX_HDR_CNT-and-MAX_HDR_DSIZE.patch \
          "
SRC_URI_append_libc-musl = " file://0001-Add-fallback-fopencookie-implementation.patch \
                             file://0002-Fixes-to-internal-fopencookie-implementation.patch \
                           "

SRCREV = "b8a9ddd88eb4e0ab351eb55a53186b5dc5ac0825"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DLIB=${baselib} -DMULTI_SEMANTICS=ON -DENABLE_RPMMD=ON -DENABLE_RPMDB=ON -DENABLE_COMPLEX_DEPS=ON"

PACKAGES =+ "${PN}-tools ${PN}ext"

FILES_${PN}-dev += "${datadir}/cmake/Modules/FindLibSolv.cmake"
FILES_${PN}-tools = "${bindir}/*"
FILES_${PN}ext = "${libdir}/${PN}ext.so.*"

BBCLASSEXTEND = "native nativesdk"
