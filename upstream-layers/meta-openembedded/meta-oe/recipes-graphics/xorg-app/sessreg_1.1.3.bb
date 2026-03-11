require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "a simple program for managing utmp/wtmp entries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d938a70c8280c265a1ccd2954365d185"
SRC_URI += "file://0001-Makefile.am-Error-Fix.patch"
SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "022acd5de8077dddc4f919961f79e102ecd5f3228a333681af5cd0e7344facc2"

CFLAGS:append:libc-musl = " -D_WTMPX_FILE=WTMP_FILE -D_PATH_WTMPX=_PATH_WTMP -D_UTMPX_FILE=UTMP_FILE -D_PATH_UTMPX=_PATH_UTMP "

