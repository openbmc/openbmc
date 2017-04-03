SUMMARY = "sbefifo tools"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools pkgconfig

S = "${WORKDIR}/git"
SRC_URI += "git:///home/bradleyb/projects/openbmc/sbefifo"
SRCREV="c2bb60c0d65d45ab0585e695efc9a1447372c9fa"
