require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "a simple program for managing utmp/wtmp entries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=82cc7e3245e8b11a5259108ba5b7e3dc"

SRC_URI_EXT = "xz"
SRC_URI[sha256sum] = "4e85b52f4f65a93449753bfb00ceb5d77f3317e24d07dc23147f2f116c785350"

CFLAGS:append:libc-musl = " -D_WTMPX_FILE=WTMP_FILE -D_PATH_WTMPX=_PATH_WTMP -D_UTMPX_FILE=UTMP_FILE -D_PATH_UTMPX=_PATH_UTMP "

