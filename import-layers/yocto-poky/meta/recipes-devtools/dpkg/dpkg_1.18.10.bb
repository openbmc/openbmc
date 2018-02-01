require dpkg.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160731T221931Z/pool/main/d/${BPN}/${BPN}_${PV}.tar.xz \
           file://noman.patch \
           file://remove-tar-no-timestamp.patch \
           file://arch_pm.patch \
           file://dpkg-configure.service \
           file://add_armeb_triplet_entry.patch \
           file://0002-Adapt-to-linux-wrs-kernel-version-which-has-characte.patch \
           file://0003-Our-pre-postinsts-expect-D-to-be-set-when-running-in.patch \
           file://0004-The-lutimes-function-doesn-t-work-properly-for-all-s.patch \
           file://0005-dpkg-compiler.m4-remove-Wvla.patch \
           file://0006-add-musleabi-to-known-target-tripets.patch \
           file://0007-dpkg-deb-build.c-Remove-usage-of-clamp-mtime-in-tar.patch \
           "
SRC_URI_append_class-native = " file://glibc2.5-sync_file_range.patch "

SRC_URI[md5sum] = "ccff17730c0964428fc186ded2f2f401"
SRC_URI[sha256sum] = "025524da41ba18b183ff11e388eb8686f7cc58ee835ed7d48bd159c46a8b6dc5"
