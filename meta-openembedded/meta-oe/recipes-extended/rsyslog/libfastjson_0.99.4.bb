SUMMARY = "A fork of json-c library"
HOMEPAGE = "https://github.com/rsyslog/libfastjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a958bb07122368f3e1d9b2efe07d231f"

DEPENDS = ""

SRC_URI = "git://github.com/rsyslog/libfastjson.git;protocol=https"

SRCREV = "6e057a094cb225c9d80d8d6e6b1f36ca88a942dd"

S = "${WORKDIR}/git"

inherit autotools
