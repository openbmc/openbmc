require uclibc.inc
require uclibc-package.inc
require uclibc-git.inc

STAGINGCC = "gcc-cross-initial-${TARGET_ARCH}"
STAGINGCC_class-nativesdk = "gcc-crosssdk-initial-${TARGET_ARCH}"

PROVIDES += "virtual/libc virtual/${TARGET_PREFIX}libc-for-gcc"

DEPENDS = "virtual/${TARGET_PREFIX}binutils \
           virtual/${TARGET_PREFIX}gcc-initial \
           virtual/${TARGET_PREFIX}libc-initial \
           linux-libc-headers ncurses-native \
           libgcc-initial kern-tools-native"

RDEPENDS_${PN}-dev = "linux-libc-headers-dev"
RPROVIDES_${PN}-dev += "libc-dev virtual-libc-dev"
# uclibc does not really have libsegfault but then using the one from glibc is also not
# going to work. So we pretend that we have it to make bitbake not pull other recipes
# to satisfy this dependency for the images/tasks

RPROVIDES_${PN} += "libsegfault rtld(GNU_HASH)"
