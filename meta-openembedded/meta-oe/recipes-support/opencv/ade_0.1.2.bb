SUMMARY = "A graph construction, manipulation, and processing framework"
DESCRIPTION = "ADE Framework is a graph construction, manipulation, \
and processing framework. ADE Framework is suitable for \
organizing data flow processing and execution."
HOMEPAGE = "https://github.com/opencv/ade"

SRC_URI = "git://github.com/opencv/ade.git;branch=master;protocol=https"

SRCREV = "1e02d7486bdb9c87993d91b9910e7cc6c4ddbf66"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit cmake

S = "${WORKDIR}/git"

EXTRA_OECMAKE += " -DCMAKE_BUILD_TYPE=Release"

FILES:${PN}-dev += "${datadir}/${BPN}/*.cmake"
