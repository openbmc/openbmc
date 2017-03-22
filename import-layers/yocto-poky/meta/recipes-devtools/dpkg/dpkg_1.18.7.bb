require dpkg.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160509T100042Z/pool/main/d/${BPN}/${BPN}_${PV}.tar.xz \
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
           "
SRC_URI_append_class-native = " file://glibc2.5-sync_file_range.patch "

SRC_URI[md5sum] = "073dbf2129a54b0fc627464bf8af4a1b"
SRC_URI[sha256sum] = "ace36d3a6dc750a42baf797f9e75ec580a21f92bb9ff96b482100755d6d9b87b"
