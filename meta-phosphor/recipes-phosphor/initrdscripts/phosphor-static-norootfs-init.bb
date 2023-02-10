SUMMARY = "Phosphor OpenBMC pre-init scripts for static-norootfs layout"
DESCRIPTION = "Phosphor OpenBMC filesystem mount implementation for static-norootfs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SOURCE_FILES = "\
    init \
    10-early-mounts \
    20-udev \
    21-factory-reset \
    30-ubiattach-or-format \
    50-mount-persistent \
    "
SRC_URI += "\
    ${@' '.join(\
        [ 'file://' + x for x in d.getVar('SOURCE_FILES', True).split()])} \
    "

S = "${WORKDIR}"

inherit allarch
inherit update-alternatives

PKG_INSTALL_DIR="${libexecdir}/${BPN}"
FILES:${PN} += "${PKG_INSTALL_DIR}"

do_install() {
    install -d ${D}${PKG_INSTALL_DIR}

    for f in ${SOURCE_FILES} ; do
        install -m 0755 ${S}/$f ${D}${PKG_INSTALL_DIR}/$f
    done
}

RDEPENDS:${PN} += " \
    ${@d.getVar('PREFERRED_PROVIDER_u-boot-fw-utils', True) or \
        'u-boot-fw-utils'} \
    ${VIRTUAL-RUNTIME_base-utils} \
    mtd-utils-ubifs \
    udev \
"

ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
# Use a number higher than the systemd init alternative so that
# ours is enabled instead.
ALTERNATIVE_PRIORITY[init] ?= "400"

ALTERNATIVE:${PN} = "init"
ALTERNATIVE_TARGET[init] = "${PKG_INSTALL_DIR}/init"
