SUMMARY = "A fork of json-c library"
HOMEPAGE = "https://github.com/rsyslog/libfastjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a958bb07122368f3e1d9b2efe07d231f"

DEPENDS = ""

SRC_URI = "git://github.com/rsyslog/libfastjson.git;protocol=https;branch=master"

SRCREV = "3a8402c1de7c7747c95229db26d8d32fb85a7a52"

S = "${WORKDIR}/git"

CVE_PRODUCT = "rsyslog:libfastjson"

inherit autotools
