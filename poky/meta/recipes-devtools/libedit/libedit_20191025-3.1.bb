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
SRC_URI[md5sum] = "2d6568467080cfd75e715d045102b544"
SRC_URI[sha256sum] = "6dff036660d478bfaa14e407fc5de26d22da1087118c897b1a3ad2e90cb7bf39"

BBCLASSEXTEND = "native nativesdk"
