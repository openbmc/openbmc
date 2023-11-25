SUMMARY = "The lsscsi command lists information about SCSI devices in Linux"
HOMEPAGE = "http://sg.danny.cz/scsi/lsscsi.html"
LICENSE = "GPL-2.0-only"
SECTION = "base"
LIC_FILES_CHKSUM="file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://sg.danny.cz/scsi/${BP}.tgz"

SRC_URI[sha256sum] = "619a2187405f02c5f57682f3478bffc75326803cd08839e39d434250c5518b15"

inherit autotools

S = "${WORKDIR}/lsscsi-${PV}r154"
