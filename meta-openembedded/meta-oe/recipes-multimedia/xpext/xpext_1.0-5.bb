LICENSE= "MIT"
SUMMARY = "X Server Nokia 770 extensions library"
SECTION = "x11/libs"
DEPENDS = "virtual/libx11 libxext"

LIC_FILES_CHKSUM = "file://COPYING;md5=db043791349ba57ad1169e1c92477cb6"

SRC_URI = "http://repository.maemo.org/pool/maemo/ossw/source/x/${BPN}/${BPN}_${PV}.tar.gz \
           file://auxdir.patch;striplevel=0"
S = "${WORKDIR}/xpext-1.0"

inherit autotools pkgconfig features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

# Remove runtime dependency on empty package ${PN}
RDEPENDS_${PN}-dev = ""

SRC_URI[md5sum] = "1b0cb67b6f2bd7c4abef17648b062896"
SRC_URI[sha256sum] = "a3b06f5188fd9effd0799ae31352b3cd65cb913b964e2c1a923ffa9d3c08abbe"
