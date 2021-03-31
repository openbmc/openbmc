SUMMARY = "BSD replacement for libreadline"
DESCRIPTION = "Command line editor library providing generic line editing, \
history, and tokenization functions"
HOMEPAGE = "http://www.thrysoee.dk/editline/"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1e4228d0c5a9093b01aeaaeae6641533"

DEPENDS = "ncurses"

inherit autotools

SRC_URI = "http://www.thrysoee.dk/editline/${BP}.tar.gz \
           file://stdc-predef.patch \
          "
SRC_URI[sha256sum] = "2283f741d2aab935c8c52c04b57bf952d02c2c02e651172f8ac811f77b1fc77a"

BBCLASSEXTEND = "native nativesdk"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE_${PN}-doc = "history.3"
ALTERNATIVE_LINK_NAME[history.3] = "${mandir}/man3/history.3"
