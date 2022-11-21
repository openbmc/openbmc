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
SRC_URI[sha256sum] = "f0925a5adf4b1bf116ee19766b7daa766917aec198747943b1c4edf67a4be2bb"

BBCLASSEXTEND = "native nativesdk"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE:${PN}-doc = "history.3"
ALTERNATIVE_LINK_NAME[history.3] = "${mandir}/man3/history.3"
