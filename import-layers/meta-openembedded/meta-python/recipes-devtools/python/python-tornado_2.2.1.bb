DESCRIPTION = "Tornado is an open source version of the scalable, non-blocking web server and tools that power FriendFeed."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README;md5=e7fb9954003d7cd93322ccf282210d1c"

PV = "2.2.1+git${SRCPV}"

SRCREV = "c501917eba46dec30b1f2ef12497dffc4beec505"
SRC_URI = "git://github.com/facebook/tornado.git;branch=branch2.2 \
           file://0001-disable-AI_ADDRCONFIG-flag.patch \
"

S = "${WORKDIR}/git"

inherit setuptools


