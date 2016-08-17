SUMMARY = "ATA over Ethernet Tools"
DESCRIPTION = " \
The aoetools are programs for users of the ATA over Ethernet (AoE)network \
storage protocol, a simple protocol for using storage over anethernet LAN. \
The vblade program (storage target) exports a blockdevice using AoE. \
"
HOMEPAGE = "http://sourceforge.net/projects/${BPN}"
SECTION = "admin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
RRECOMMENDS_${PN} = "kernel-module-aoe"

SRC_URI = "http://sourceforge.net/projects/${BPN}/files/${BPN}/${BPN}-${PV}.tar.gz \
           file://aoe-stat-no-bashism.patch \
        "
SRC_URI[md5sum] = "bff30daa988a65f69d4448ce4726a6db"
SRC_URI[sha256sum] = "fb5e2cd0de7644cc1ec04ee3aeb43211cf7445a0c19e13d6b3ed5a8fbdf215ff"

# EXTRA_OEMAKE is typically: -e MAKEFLAGS=
# the -e causes problems as CFLAGS is modified in the Makefile.
EXTRA_OEMAKE = ""

do_install() {
    oe_runmake DESTDIR=${D} install
}
