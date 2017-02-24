SUMMARY = "A command line tool for updating and displaying info about boot loaders"
DESCRIPTION = "grubby is a command line tool for updating and displaying information \
about the configuration files for the grub, lilo, elilo (ia64), yaboot (powerpc) and \
zipl (s390) boot loaders. It is primarily designed to be used from scripts which install \
new kernels and need to find information about the current boot environment. \
"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

DEPENDS = "popt util-linux"

SRC_URI = "https://github.com/rhinstaller/${BPN}/archive/${PV}-1.tar.gz;downloadfilename=${BPN}-${PV}-1.tar.gz \
           file://grubby-rename-grub2-editenv-to-grub-editenv.patch \
           file://run-ptest \
"

SRC_URI[md5sum] = "1005907b275d6d93368d045274537d86"
SRC_URI[sha256sum] = "85f1c678484f74c8978e8643451594967defce463a86c35cb1ee56d12767a9df"

S = "${WORKDIR}/${BPN}-${PV}-1"

RDEPENDS_${PN} += "dracut"

inherit autotools-brokensep ptest

EXTRA_OEMAKE = "-e 'CC=${CC}' 'LDFLAGS=${LDFLAGS}'"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    cp -r ${S}/test ${S}/test.sh ${D}${PTEST_PATH}
    sed -i 's|./grubby|grubby|' ${D}${PTEST_PATH}/test.sh
}

RDEPENDS_${PN}-ptest = "util-linux-getopt bash"

COMPATIBLE_HOST = '(x86_64.*|i.86.*)-(linux|freebsd.*)'
