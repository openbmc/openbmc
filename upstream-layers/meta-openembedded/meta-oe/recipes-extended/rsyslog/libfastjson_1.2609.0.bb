SUMMARY = "A fork of json-c library"
HOMEPAGE = "https://github.com/rsyslog/libfastjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a958bb07122368f3e1d9b2efe07d231f"

DEPENDS = ""

SRC_URI = "git://github.com/rsyslog/libfastjson.git;protocol=https;branch=main;tag=v${PV}"

SRCREV = "c2329f89006600703711271c6c39fe5181286264"

EXTRA_OECONF += "LIBS='-lm'"

CVE_PRODUCT = "rsyslog:libfastjson"

inherit autotools
