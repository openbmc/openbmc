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
SRC_URI[md5sum] = "22e945a0476e388e6f78bfc8d6e1192c"
SRC_URI[sha256sum] = "2811d70c0b000f2ca91b7cb1a37203134441743c4fcc9c37b0b687f328611064"

S = "${WORKDIR}/${BPN}-${PV}"

BBCLASSEXTEND = "native nativesdk"
