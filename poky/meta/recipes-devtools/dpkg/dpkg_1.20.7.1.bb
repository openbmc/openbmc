require dpkg.inc
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://salsa.debian.org/dpkg-team/dpkg.git;protocol=https;branch=sid \
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
           file://0001-Add-support-for-riscv32-CPU.patch \
           file://0013-scripts-dpkg-fsys-usrunmess.pl-correct-shebang.patch \
           "

SRC_URI_append_class-native = " file://0001-build.c-ignore-return-of-1-from-tar-cf.patch"

SRCREV = "667bf0aeb92e0e7bb225ee273569c5e2389083bd"

S = "${WORKDIR}/git"
