SUMMARY = "The lsscsi command lists information about SCSI devices in Linux"
HOMEPAGE = "http://sg.danny.cz/scsi/lsscsi.html"
LICENSE = "GPL-2.0-only"
SECTION = "base"
LIC_FILES_CHKSUM="file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://sg.danny.cz/scsi/${BP}.tgz"

SRC_URI[sha256sum] = "0a800e9e94dca2ab702d65d72777ae8cae078e3d74d0bcbed64ba0849e8029a1"

inherit autotools

S = "${WORKDIR}/lsscsi-${PV}"
