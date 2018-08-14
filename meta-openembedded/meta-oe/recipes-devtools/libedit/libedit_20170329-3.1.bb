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
SRC_URI[md5sum] = "c57a0690e62ef523c083598730272cfd"
SRC_URI[sha256sum] = "91f2d90fbd2a048ff6dad7131d9a39e690fd8a8fd982a353f1333dd4017dd4be"

S = "${WORKDIR}/${BPN}-${PV}"
