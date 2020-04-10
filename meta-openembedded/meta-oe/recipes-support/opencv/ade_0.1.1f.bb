SUMMARY = "A graph construction, manipulation, and processing framework"
DESCRIPTION = "ADE Framework is a graph construction, manipulation, \
and processing framework. ADE Framework is suitable for \
organizing data flow processing and execution."
HOMEPAGE = "https://github.com/opencv/ade"

SRC_URI = "git://github.com/opencv/ade.git \
           file://0001-use-GNUInstallDirs-for-detecting-install-paths.patch \
           "

SRCREV = "58b2595a1a95cc807be8bf6222f266a9a1f393a9"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit cmake

S = "${WORKDIR}/git"

EXTRA_OECMAKE += " -DCMAKE_BUILD_TYPE=Release"

FILES_${PN}-dev += "${datadir}/${BPN}/*.cmake"
