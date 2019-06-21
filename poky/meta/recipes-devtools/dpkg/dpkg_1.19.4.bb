require dpkg.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "${DEBIAN_MIRROR}/main/d/${BPN}/${BPN}_${PV}.tar.xz \
           file://noman.patch \
           file://remove-tar-no-timestamp.patch \
           file://arch_pm.patch \
           file://add_armeb_triplet_entry.patch \
           file://0002-Adapt-to-linux-wrs-kernel-version-which-has-characte.patch \
           file://0003-Our-pre-postinsts-expect-D-to-be-set-when-running-in.patch \
           file://0004-The-lutimes-function-doesn-t-work-properly-for-all-s.patch \
           file://0006-add-musleabi-to-known-target-tripets.patch \
           file://0007-dpkg-deb-build.c-Remove-usage-of-clamp-mtime-in-tar.patch \
           file://0001-dpkg-Support-muslx32-build.patch \
           file://pager.patch \
           "
SRC_URI_append_class-native = " \
                                file://tweak-options-require-tar-1.27.patch \
"

SRC_URI[md5sum] = "1e4420409426d8c58bbe13a8e07c0c0b"
SRC_URI[sha256sum] = "c15234e98655689586bff2d517a6fdc6135d139c54d52ae9cfa6a90007fee0ae"
