SUMMARY = "A command line tool for updating and displaying info about boot loaders"
DESCRIPTION = "grubby is a command line tool for updating and displaying information \
about the configuration files for the grub, lilo, elilo (ia64), yaboot (powerpc) and \
zipl (s390) boot loaders. It is primarily designed to be used from scripts which install \
new kernels and need to find information about the current boot environment. \
"
HOMEPAGE = "https://github.com/rhboot/grubby"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

DEPENDS = "popt util-linux"
DEPENDS_append_libc-musl = " libexecinfo"

S = "${WORKDIR}/git"
SRCREV = "79c5cfa02c567efdc5bb18cdd584789e2e35aa23"
SRC_URI = "git://github.com/rhboot/grubby.git;protocol=https;branch=master \
           file://grubby-rename-grub2-editenv-to-grub-editenv.patch \
           file://run-ptest \
           file://0001-Add-another-variable-LIBS-to-provides-libraries-from.patch \
           file://0002-include-paths.h-for-_PATH_MOUNTED.patch \
           "

RDEPENDS_${PN} += "dracut"

inherit autotools-brokensep ptest

EXTRA_OEMAKE = "-e 'CC=${CC}' 'LDFLAGS=${LDFLAGS}' LIBS='${LIBS}'"

LIBS_libc-musl = "-lexecinfo"
LIBS ?= ""
do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    cp -r ${S}/test ${S}/test.sh ${D}${PTEST_PATH}
    sed -i 's|./grubby|grubby|' ${D}${PTEST_PATH}/test.sh
}

RDEPENDS_${PN} += "bash"
RDEPENDS_${PN}-ptest = "util-linux-getopt bash"

COMPATIBLE_HOST = '(x86_64.*|i.86.*)-(linux|freebsd.*)'
