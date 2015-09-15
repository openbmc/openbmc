require m4.inc

EXTRA_OECONF += "--without-libsigsegv-prefix"

LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504\
	            file://examples/COPYING;md5=4031593b2166d6c47cae282d944a7ede"

SRC_URI += "file://ac_config_links.patch \
            file://remove-gets.patch \
           "

SRC_URI_append_class-target = "\
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
	   "
SRC_URI[md5sum] = "a5e9954b1dae036762f7b13673a2cf76"
SRC_URI[sha256sum] = "3ce725133ee552b8b4baca7837fb772940b25e81b2a9dc92537aeaf733538c9e"
