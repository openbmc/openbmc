require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "a simple program for managing utmp/wtmp entries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d938a70c8280c265a1ccd2954365d185"
SRC_URI += "file://0001-Makefile.am-Error-Fix.patch"
SRC_URI[md5sum] = "e238c89dabc566e1835e1ecb61b605b9"
SRC_URI[sha256sum] = "551177657835e0902b5eee7b19713035beaa1581bbd3c6506baa553e751e017c"

CFLAGS_append_libc-musl = " -D_WTMPX_FILE=WTMP_FILE -D_PATH_WTMPX=_PATH_WTMP -D_UTMPX_FILE=UTMP_FILE -D_PATH_UTMPX=_PATH_UTMP "

