SUMMARY = "A graph construction, manipulation, and processing framework"
DESCRIPTION = "ADE Framework is a graph construction, manipulation, \
and processing framework. ADE Framework is suitable for \
organizing data flow processing and execution."
HOMEPAGE = "https://github.com/opencv/ade"

SRC_URI = "git://github.com/opencv/ade.git;branch=master;protocol=https"
SRCREV = "ffc83ad372d72b16732f201d8a9d1d80dd16515b"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+[a-z]?)"

inherit cmake

EXTRA_OECMAKE += " -DCMAKE_BUILD_TYPE=Release"

FILES:${PN}-dev += "${datadir}/${BPN}/*.cmake"
