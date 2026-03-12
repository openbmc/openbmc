require dpkg.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://salsa.debian.org/dpkg-team/dpkg.git;protocol=https;branch=main;tag=${PV} \
           file://noman.patch \
           file://remove-tar-no-timestamp.patch \
           file://arch_pm.patch \
           file://add_armeb_triplet_entry.patch \
           file://run-ptest \
           file://0002-Adapt-to-linux-wrs-kernel-version-which-has-characte.patch \
           file://0001-script.c-avoid-use-of-chroot.patch \
           file://0004-The-lutimes-function-doesn-t-work-properly-for-all-s.patch \
           file://0006-add-musleabi-to-known-target-tripets.patch \
           file://0007-dpkg-deb-build.c-Remove-usage-of-clamp-mtime-in-tar.patch \
           file://0001-dpkg-Support-muslx32-build.patch \
           file://0001-Add-support-for-riscv32-CPU.patch \
           file://0001-lib-dpkg-options-dirs.c-set_rootfs-was-not-checking-.patch \
           "

SRC_URI:append:class-native = " file://0001-build.c-ignore-return-of-1-from-tar-cf.patch"

SRCREV = "89f266325cc8b5b7fd1ee417b78d498ee189565b"
