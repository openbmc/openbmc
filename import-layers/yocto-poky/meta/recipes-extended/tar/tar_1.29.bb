require tar.inc

PACKAGECONFIG ??= ""
PACKAGECONFIG[acl] = "--with-posix-acls, --without-posix-acls, acl,"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI += "file://remove-gets.patch \
            file://musl_dirent.patch \
            file://CVE-2016-6321.patch \
           "
SRC_URI[md5sum] = "955cd533955acb1804b83fd70218da51"
SRC_URI[sha256sum] = "236b11190c0a3a6885bdb8d61424f2b36a5872869aa3f7f695dea4b4843ae2f2"

do_install_append_libc-musl() {
	rm -f ${D}${libdir}/charset.alias
	rmdir ${D}${libdir}
}
