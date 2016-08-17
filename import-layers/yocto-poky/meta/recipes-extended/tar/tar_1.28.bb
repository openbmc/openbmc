require tar.inc

PACKAGECONFIG ??= ""
PACKAGECONFIG[acl] = "--with-posix-acls, --without-posix-acls, acl,"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI += "file://remove-gets.patch \
            file://musl_dirent.patch \
           "
SRC_URI[md5sum] = "8f32b2bc1ed7ddf4cf4e4a39711341b0"
SRC_URI[sha256sum] = "60e4bfe0602fef34cd908d91cf638e17eeb09394d7b98c2487217dc4d3147562"

do_install_append_libc-musl() {
	rm -f ${D}${libdir}/charset.alias
	rmdir ${D}${libdir}
}
