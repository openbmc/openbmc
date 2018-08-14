require tar.inc

PACKAGECONFIG ??= ""
PACKAGECONFIG[acl] = "--with-posix-acls, --without-posix-acls, acl,"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI += "file://remove-gets.patch \
            file://musl_dirent.patch \
           "
SRC_URI[md5sum] = "8404e4c1fc5a3000228ab2b8ad674a65"
SRC_URI[sha256sum] = "87592b86cb037c554375f5868bdd3cc57748aef38d6cb741c81065f0beac63b7"

do_install_append_libc-musl() {
	rm -f ${D}${libdir}/charset.alias
	rmdir ${D}${libdir}
}
