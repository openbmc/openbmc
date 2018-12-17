SUMMARY = "BSD replacement for libreadline"
DESCRIPTION = "Command line editor library providing generic line editing, \
history, and tokenization functions"
HOMEPAGE = "http://www.thrysoee.dk/editline/"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=1e4228d0c5a9093b01aeaaeae6641533"

DEPENDS = "ncurses"

inherit autotools

# upstream site does not allow wget's User-Agent
FETCHCMD_wget += "-U bitbake"
SRC_URI = "http://www.thrysoee.dk/editline/${BPN}-${PV}.tar.gz \
           file://stdc-predef.patch \
          "
SRC_URI[md5sum] = "97679319742f45d6cdcd6075511b14ac"
SRC_URI[sha256sum] = "c41bea8fd140fb57ba67a98ec1d8ae0b8ffa82f4aba9c35a87e5a9499e653116"

S = "${WORKDIR}/${BPN}-${PV}"
