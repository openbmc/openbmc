SUMMARY = "A from-scratch implementation of pstack using DWARF debugging \
and unwind information. Works for C/C++, Go, Rust, and Python."

HOMEPAGE = "https://github.com/peadar/pstack"
SECTION = "devel/pstack"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=671019a96ba80415b696240ed2ca5e80"

DEPENDS = "tcl virtual/libx11 libxt zip-native"

SRC_URI = "git://github.com/peadar/pstack;branch=v2.12-maint;tag=v${PV};protocol=https;tag=v${PV}"
SRCREV = "98c4ddb7f0faa5221ea0a5f38105fc2a2f5cbfe7"

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
