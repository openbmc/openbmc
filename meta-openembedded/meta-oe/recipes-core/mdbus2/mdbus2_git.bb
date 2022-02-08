SUMMARY = "Mickey's DBus Introspection and Interaction Utility V2"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07"

DEPENDS = "readline"

PV = "2.3.3+git${SRCPV}"

SRC_URI = "git://github.com/freesmartphone/mdbus.git;protocol=http;branch=master;protocol=https"
SRCREV = "28202692d0b441000f4ddb8f347f72d1355021aa"

S = "${WORKDIR}/git"

inherit autotools vala

EXTRA_OECONF += "--enable-vala"
