SUMMARY = "A fork of json-c library"
HOMEPAGE = "https://github.com/rsyslog/libfastjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a958bb07122368f3e1d9b2efe07d231f"

DEPENDS = ""

SRC_URI = "git://github.com/rsyslog/libfastjson.git;protocol=https"

SRCREV = "0293afb3913f760c449348551cca4d2df59c1a00"

S = "${WORKDIR}/git"

inherit autotools
