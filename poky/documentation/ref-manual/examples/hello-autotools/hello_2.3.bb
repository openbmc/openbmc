DESCRIPTION = "GNU Helloworld application"
SECTION = "examples"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=adefda309052235aa5d1e99ce7557010"

SRC_URI = "${GNU_MIRROR}/hello/hello-${PV}.tar.bz2"

inherit autotools
