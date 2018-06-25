SUMMARY = "The lsscsi command lists information about SCSI devices in Linux"
HOMEPAGE = "http://sg.danny.cz/scsi/lsscsi.html"
LICENSE = "GPLv2"
SECTION = "base"
LIC_FILES_CHKSUM="file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://sg.danny.cz/scsi/${BP}.tgz"

SRC_URI[md5sum] = "4a39e3b09cd422e2cae3edbaf56b3176"
SRC_URI[sha256sum] = "025d009a1af42bc5b2fca664c44c9ecdfd754356e4a44f5c6aced2420afadd50"

inherit autotools
