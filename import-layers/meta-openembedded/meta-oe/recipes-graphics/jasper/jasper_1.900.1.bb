SUMMARY = "Jpeg 2000 implementation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baa697d7510288a9cdcce9bd7edaf9bc"

PR = "r1"

SRC_URI = "http://www.ece.uvic.ca/~mdadams/jasper/software/jasper-${PV}.zip"

inherit autotools lib_package

PACKAGECONFIG ??= ""
PACKAGECONFIG[jpeg] = "--enable-libjpeg,--disable-libjpeg,jpeg"

EXTRA_OECONF = "--enable-shared"

SRC_URI[md5sum] = "a342b2b4495b3e1394e161eb5d85d754"
SRC_URI[sha256sum] = "6b905a9c2aca2e275544212666eefc4eb44d95d0a57e4305457b407fe63f9494"
