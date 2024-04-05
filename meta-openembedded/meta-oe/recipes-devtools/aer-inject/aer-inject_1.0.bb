SUMMARY = "Inject PCIE AER errors on the software level into a running Linux kernel."
DESCRIPTION = "\
aer-inject allows to inject PCIE AER errors on the software \
level into a running Linux kernel. This is intended for \
validation of the PCIE driver error recovery handler and \
PCIE AER core handler."
HOMEPAGE = "https://git.kernel.org/cgit/linux/kernel/git/gong.chen/aer-inject.git/"
SECTION = "pcie/misc"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://README;beginline=25;endline=38;md5=643c2332ec702691a87ba6ea9499b2d6"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/gong.chen/aer-inject.git;protocol=https;branch=master \
           file://0001-include-libgen.h-for-basename.patch \
"
SRCREV = "9bd5e2c7886fca72f139cd8402488a2235957d41"

S = "${WORKDIR}/git"

DEPENDS = "bison-native"

do_compile() {
    oe_runmake CFLAGS="-Wall -D_GNU_SOURCE"
}

do_install() {
    oe_runmake 'DESTDIR=${D}' 'PREFIX=${prefix}' install
}
FILES:${PN} += "${prefix}/aer-inject"
BBCLASSEXTEND = "native nativesdk"
