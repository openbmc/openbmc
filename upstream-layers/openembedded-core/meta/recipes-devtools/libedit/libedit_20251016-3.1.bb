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
SRC_URI[sha256sum] = "21362b00653bbfc1c71f71a7578da66b5b5203559d43134d2dd7719e313ce041"

# configure hardcodes /usr/bin search path bypassing HOSTTOOLS
CACHED_CONFIGUREVARS += "ac_cv_path_NROFF=/bin/false"

BBCLASSEXTEND = "native nativesdk"
