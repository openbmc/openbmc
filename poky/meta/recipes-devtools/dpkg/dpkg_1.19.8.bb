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

SRC_URI[md5sum] = "9d170c8baa1aa36b09698c909f304508"
SRC_URI[sha256sum] = "2632c00b0cf0ea19ed7bd6700e6ec5faca93f0045af629d356dc03ad74ae6f10"
