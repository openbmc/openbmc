SUMMARY = "The lsscsi command lists information about SCSI devices in Linux"
HOMEPAGE = "http://sg.danny.cz/scsi/lsscsi.html"
LICENSE = "GPLv2"
SECTION = "base"
LIC_FILES_CHKSUM="file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://sg.danny.cz/scsi/${BP}.tgz"

SRC_URI[md5sum] = "efb68946f51860e8eedc18d6784afdae"
SRC_URI[sha256sum] = "12bf1973014803c6fd6d547e7594a4c049f0eef3bf5d22190d4be29d7c09f3ca"

inherit autotools

S = "${WORKDIR}/lsscsi-${PV}"
