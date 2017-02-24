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

SRC_URI[md5sum] = "b6e60f326a3fce91bea1a6fe4700af58"
SRC_URI[sha256sum] = "b6b159c0c6ec8a7f349ea2a75d8b960efa346c462c1ac4921f1ac0de85a9f5d6"
