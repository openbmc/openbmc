SUMMARY = "Phosphor OpenBMC pre-init scripts for static-norootfs layout"
DESCRIPTION = "Phosphor OpenBMC filesystem mount implementation for static-norootfs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SOURCE_FILES = "\
    10-early-mounts \
    20-udev \
    21-factory-reset \
    30-ubiattach-or-format \
    50-mount-persistent \
    "
SRC_URI += "\
    file://init \
    file://remount-filesystem-readonly \
    ${@' '.join(\
        [ 'file://' + x for x in d.getVar('SOURCE_FILES', True).split()])} \
    "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

NOROOTFS_PERSISTENT_DIRS = "\
    var \
    etc \
    home \
    ${@ bb.utils.contains('ROOT_HOME', '/home/root', '', d.getVar('ROOT_HOME')[1:], d)} \
    "

inherit allarch
inherit update-alternatives

PKG_INSTALL_DIR = "${libexecdir}/${BPN}"
FILES:${PN} += "${PKG_INSTALL_DIR}"

do_install() {
    install -d ${D}${PKG_INSTALL_DIR}/initfiles
    install -m 0755 ${S}/init ${D}${PKG_INSTALL_DIR}/init
    install -m 0755 ${S}/remount-filesystem-readonly ${D}${PKG_INSTALL_DIR}/remount-filesystem-readonly

    for f in ${SOURCE_FILES} ; do
        install -m 0755 ${S}/$f ${D}${PKG_INSTALL_DIR}/initfiles/$f
    done

    # Create persistent mount points and add to mount script.
    for mountpoint in ${NOROOTFS_PERSISTENT_DIRS} ; do
        mkdir -p ${D}/$mountpoint
        touch ${D}/$mountpoint/.keep.mount-persistent
    done
    sed -i "s#@NOROOTFS_PERSISTENT_DIRS@#${NOROOTFS_PERSISTENT_DIRS}#" \
        ${D}${PKG_INSTALL_DIR}/initfiles/50-mount-persistent
}

RDEPENDS:${PN} += " \
    ${@d.getVar('PREFERRED_PROVIDER_u-boot-fw-utils', True) or \
        'u-boot-fw-utils'} \
    ${VIRTUAL-RUNTIME_base-utils} \
    mtd-utils-ubifs \
    udev \
"

FILES:${PN}:append = " ${@ " ".join([ '/' + x + '/.keep.mount-persistent' for x in d.getVar('NOROOTFS_PERSISTENT_DIRS').split() ])}"

ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
# Use a number higher than the systemd init alternative so that
# ours is enabled instead.
ALTERNATIVE_PRIORITY[init] ?= "400"

ALTERNATIVE:${PN} = "init"
ALTERNATIVE_TARGET[init] = "${PKG_INSTALL_DIR}/init"
