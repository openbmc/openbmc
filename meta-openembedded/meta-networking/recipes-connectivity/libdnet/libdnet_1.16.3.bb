SUMMARY = "dumb networking library"
HOMEPAGE = "https://github.com/ofalk/libdnet"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0036c1b155f4e999f3e0a373490b5db9"

SRC_URI = "git://github.com/ofalk/libdnet.git;nobranch=1;protocol=https \
           file://0001-configure-Replace-use-of-AC_EGREP_CPP.patch \
           file://0001-configure-Use-pkg-config-variable-to-find-check-incl.patch"
SRCREV = "a03043bec8e66b240a45555e37147b22db182c21"

UPSTREAM_CHECK_GITTAGREGEX = "libdnet-(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit autotools multilib_script pkgconfig

DEPENDS += "libcheck"

EXTRA_AUTORECONF += "-I ./config"
BBCLASSEXTEND = "native"

MULTILIB_SCRIPTS = "${PN}:${bindir}/dnet-config"
