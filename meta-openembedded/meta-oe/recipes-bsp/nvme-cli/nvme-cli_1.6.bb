SUMMARY = "NVMe management command line interface"
AUTHOR = "Stefan Wiehler <stefan.wiehler@missinglinkelectronics.com>"
HOMEPAGE = "https://github.com/linux-nvme/nvme-cli"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8264535c0c4e9c6c335635c4026a8022"
DEPENDS = "util-linux"
PV .= "+git${SRCPV}"

SRC_URI = "git://github.com/linux-nvme/nvme-cli.git \
           file://0001-Makefile-fix-bash-completion-install-path.patch \
           "
SRCREV = "642d426faf8a67ed01e90f7c35c0d967f8cc52a3"

S = "${WORKDIR}/git"

inherit bash-completion

do_install() {
    oe_runmake PREFIX=${prefix} DESTDIR=${D} install
}
