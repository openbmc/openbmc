SUMMARY = "FIGlet is a program that creates large characters out of ordinary screen characters"
HOMEPAGE = "http://www.figlet.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1688bcd97b27704f1afcac7336409857"

SRC_URI = "git://github.com/cmatsuoka/figlet.git;branch=master;protocol=https \
           file://0001-build-add-autotools-support-to-allow-easy-cross-comp.patch"
SRCREV = "5bbcd7383a8c3a531299b216b0c734e1495c6db3"
S = "${WORKDIR}/git"
PV = "2.2.5+git${SRCPV}"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
