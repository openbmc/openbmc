SUMMARY = "A from-scratch implementation of pstack using DWARF debugging \
and unwind information. Works for C/C++, Go, Rust, and Python."

HOMEPAGE = "https://github.com/peadar/pstack"
SECTION = "devel/pstack"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=671019a96ba80415b696240ed2ca5e80"

DEPENDS = "tcl virtual/libx11 libxt zip-native"

SRC_URI = "git://github.com/peadar/pstack;branch=master;tag=v${PV};protocol=https \
           file://0001-reader.h-include-cstdint-for-uintmax_t-intmax_t.patch \
          "
SRCREV = "b78d16e9497fd36350e4e8bd37f31f8dab5b5651"

PACKAGES =+ "${PN}-lib"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

# isn't getting picked up by shlibs code
RDEPENDS:${PN} += "tk-lib"
RDEPENDS:${PN}:class-native = ""

BBCLASSEXTEND = "native nativesdk"

inherit binconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

SYSROOT_DIRS += "${bindir_crossscripts}"

# Fix some paths that might be used by Tcl extensions
BINCONFIG_GLOB = "*Config.sh"
