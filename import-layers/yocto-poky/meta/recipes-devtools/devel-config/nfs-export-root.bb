SUMMARY = "Configuration script to export target rootfs filesystem"
DESCRIPTION = "Enables NFS access from any host to the entire filesystem (for development purposes)."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PR = "r1"

SRC_URI = "file://exports"

S = "${WORKDIR}"

do_install() {
	install -d ${D}${sysconfdir}
	install -m 0644 exports ${D}${sysconfdir}/
}

RDEPENDS_${PN} = "packagegroup-core-nfs-server"
