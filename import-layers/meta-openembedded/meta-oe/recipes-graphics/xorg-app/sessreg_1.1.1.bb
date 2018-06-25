require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "a simple program for managing utmp/wtmp entries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d938a70c8280c265a1ccd2954365d185"
SRC_URI += "file://0001-Makefile.am-Error-Fix.patch"
SRC_URI[md5sum] = "e475167a892b589da23edf8edf8c942d"
SRC_URI[sha256sum] = "78a76ae3f3f6a26547a34630d1b740f42344848b5fb6d7c9c7f7da255e35a6e1"

CFLAGS_append_libc-musl = " -D_WTMPX_FILE=WTMP_FILE -D_PATH_WTMPX=_PATH_WTMP -D_UTMPX_FILE=UTMP_FILE -D_PATH_UTMPX=_PATH_UTMP "

