SUMMARY = "Mobile IPv6 and NEMO for Linux"
DESCRIPTION = "UMIP is an open source implementation of Mobile IPv6 and NEMO \
Basic Support for Linux. It is released under the GPLv2 license. It supports \
the following IETF RFC: RFC6275 (Mobile IPv6), RFC3963 (NEMO), RFC3776 and \
RFC4877 (IPsec and IKEv2)."
HOMEPAGE = "http://umip.org/"
SECTION = "System Environment/Base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=073dc31ccb2ebed70db54f1e8aeb4c33"
DEPENDS = "rpm indent-native"

SRC_URI = "git://git.umip.org/umip.git"
SRCREV = "428974c2d0d8e75a2750a3ab0488708c5dfdd8e3"

S = "${WORKDIR}/git"
EXTRA_OE_CONF = "--enable-vt"

inherit autotools-brokensep

PARALLEL_MAKE = ""
