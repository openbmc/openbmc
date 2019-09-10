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
           file://0001-readline.c-fix-cannot-get-history.patch \
          "
SRC_URI[md5sum] = "bec755c8044ad84b752dfe49a0b371d8"
SRC_URI[sha256sum] = "ac8f0f51c1cf65492e4d1e3ed2be360bda41e54633444666422fbf393bba1bae"

BBCLASSEXTEND = "native nativesdk"
