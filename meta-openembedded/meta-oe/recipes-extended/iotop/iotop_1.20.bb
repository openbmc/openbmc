SUMMARY = "A top utility for I/O"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48e7be78bd2671d08c9c3bad71f1cfaa"

DEPENDS = "ncurses"

SRC_URI = "https://github.com/Tomas-M/iotop/releases/download/v1.20/iotop-1.20.tar.xz"
SRC_URI[sha256sum] = "e0227dd4b71ce3ffe50225b85cf9abb38a99c1d2dff69e3f1db7d059d7490d51"
UPSTREAM_CHECK_URI = "https://github.com/Tomas-M/iotop/releases"

inherit pkgconfig

EXTRA_OEMAKE = "V=1 STRIP=true"

# Workaround BFD linker crash with clang on arm
# revisit when upgrading binutils and see if its fixed
LDFLAGS:append:toolchain-clang:arm = " -fuse-ld=lld"

do_install() {
    oe_runmake install DESTDIR=${D}
}
