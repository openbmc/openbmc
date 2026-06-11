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
           file://0001-include-stdc-predef.patch \
           "
SRC_URI[sha256sum] = "432d5e7ea8b0116dd39f2eca7bc11d0eed77faa6b77ea526ace89907c23ea4a0"

# configure hardcodes /usr/bin search path bypassing HOSTTOOLS
CACHED_CONFIGUREVARS += "ac_cv_path_NROFF=/bin/false"

BBCLASSEXTEND = "native nativesdk"
