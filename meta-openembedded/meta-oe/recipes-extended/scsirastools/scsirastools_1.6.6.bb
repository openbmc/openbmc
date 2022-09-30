SUMMARY = "Linux SCSi tools to service maintain disk storage devices"
DESCRIPTION = "scsirastools were designed to add to the Serviceability of \
               SCSI devices under Linux so that the system does not have \
               to be rebooted or taken out of service to perform common \
               maintenance or service functions. It handles SCSI, \
               Linux SW RAID, SAS, SATA, and USB devices via SCSI emulation."
HOMEPAGE = "http://scsirastools.sourceforge.net/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=687ea108478d26152ae46eb29d9d1545"

DEPENDS += "groff-native"

SRC_URI = "http://prdownloads.sourceforge.net/scsirastools/scsirastools-${PV}.tar.gz \
           file://mdadm.patch;apply=no \
           file://print-format.patch \
"
SRC_URI[md5sum] = "6271a61b2ce40aaf33ef61775148cda1"
SRC_URI[sha256sum] = "e7b997e75decb06a650c42c35cd63d0c94c34e39cf133c723337b0eeabbfdf6a"

inherit autotools update-rc.d

# mdadm Makefile has CC set to gcc, hence override CC to ${CC}
EXTRA_OEMAKE += "CC='${CC}' CFLAGS='${CFLAGS} -D_LARGEFILE64_SOURCE=1' sbindir=${base_sbindir}"

do_configure:append() {
	oe_runmake -C mdadm.d mdadm-1.3.0
	patch -p0 < ${WORKDIR}/mdadm.patch
}
INITSCRIPT_PACKAGES = "${PN}-diskmon ${PN}-raidmon"
INITSCRIPT_NAME:${PN}-diskmon = "sgdisk"
INITSCRIPT_PARAMS:${PN}-diskmon = "defaults 80 20"
INITSCRIPT_NAME:${PN}-raidmon = "sgraid"
INITSCRIPT_PARAMS:${PN}-raidmon = "defaults 80 20"

PACKAGES =+ "${PN}-diskmon"
PACKAGES =+ "${PN}-raidmon"

RPROVIDES:${PN}-dbg += "${PN}-diskmon-dbg ${PN}-raidmon-dbg"

FILES:${PN}-diskmon = "${sbindir}/sgdiskmon ${sysconfdir}/init.d/sgdisk"

FILES:${PN}-raidmon = "${sbindir}/sgraidmon ${sysconfdir}/init.d/sgraid"

RDEPENDS:${PN} += "bash"
RDEPENDS:${PN}-diskmon += "${PN} bash"
RDEPENDS:${PN}-raidmon += "${PN} bash"
