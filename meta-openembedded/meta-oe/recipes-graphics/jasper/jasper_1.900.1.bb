SUMMARY = "Jpeg 2000 implementation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baa697d7510288a9cdcce9bd7edaf9bc"

PR = "r1"

SRC_URI = "git://github.com/mdadams/jasper.git;protocol=https"
SRCREV = "b13b8c86be870107f83b0a9a4b77557cb2b65d69"

S = "${WORKDIR}/git"

inherit autotools lib_package

PACKAGECONFIG ??= ""
PACKAGECONFIG[jpeg] = "--enable-libjpeg,--disable-libjpeg,jpeg"
PACKAGECONFIG[opengl] = "--enable-opengl,--disable-opengl,freeglut"

EXTRA_OECONF = "--enable-shared"
