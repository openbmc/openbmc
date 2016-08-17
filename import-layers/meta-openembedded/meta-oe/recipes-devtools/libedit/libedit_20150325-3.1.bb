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
SRC_URI = "http://www.thrysoee.dk/editline/${BPN}-${PV}.tar.gz"

S = "${WORKDIR}/${BPN}-${PV}"

SRC_URI[md5sum] = "43cdb5df3061d78b5e9d59109871b4f6"
SRC_URI[sha256sum] = "c88a5e4af83c5f40dda8455886ac98923a9c33125699742603a88a0253fcc8c5"
