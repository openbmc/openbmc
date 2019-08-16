require recipes-graphics/xorg-app/xorg-app-common.inc

SUMMARY = "a simple program for managing utmp/wtmp entries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d938a70c8280c265a1ccd2954365d185"
SRC_URI += "file://0001-Makefile.am-Error-Fix.patch"
SRC_URI[md5sum] = "92be564d4be7d8aa7b5024057b715210"
SRC_URI[sha256sum] = "dfcb9e954273ace63879aec053c7880466f8755752a35aa9ced3fa5f04fd2c33"

CFLAGS_append_libc-musl = " -D_WTMPX_FILE=WTMP_FILE -D_PATH_WTMPX=_PATH_WTMP -D_UTMPX_FILE=UTMP_FILE -D_PATH_UTMPX=_PATH_UTMP "

