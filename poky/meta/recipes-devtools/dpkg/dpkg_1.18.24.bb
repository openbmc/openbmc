require dpkg.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "http://snapshot.debian.org/archive/debian/20170518T093838Z/pool/main/d/dpkg/dpkg_1.18.24.tar.xz \
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
           file://0001-dpkg-Support-muslx32-build.patch \
           file://0001-arch-Add-support-for-riscv64-CPU.patch \
           "
SRC_URI_append_class-native = " file://glibc2.5-sync_file_range.patch "

SRC_URI[md5sum] = "02e8af8faf1e689228da806c3e8c6882"
SRC_URI[sha256sum] = "d853081d3e06bfd46a227056e591f094e42e78fa8a5793b0093bad30b710d7b4"
