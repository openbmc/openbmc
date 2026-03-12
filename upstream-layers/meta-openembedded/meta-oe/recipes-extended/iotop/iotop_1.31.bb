SUMMARY = "A top utility for I/O"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=26b36b714daebf72b11715ba73efbad4"

DEPENDS = "ncurses"

SRC_URI = "git://github.com/Tomas-M/iotop.git;branch=master;protocol=https;tag=v${PV}"
SRCREV = "57890d671ba69ebc4a619a1cfe7d76cdd3d836a8"


inherit pkgconfig

EXTRA_OEMAKE = "V=1 STRIP=true"
# Fixes llvm-bc70b5.o: can't link soft-float modules with double-float modules
EXTRA_OEMAKE:append:toolchain-clang:riscv64 = " NO_FLTO=1"
EXTRA_OEMAKE:append:toolchain-clang:riscv32 = " NO_FLTO=1"

# Workaround BFD linker crash with clang on arm
# revisit when upgrading binutils and see if its fixed
LDFLAGS:append:toolchain-clang:arm = " -fuse-ld=lld"

do_install() {
    oe_runmake install DESTDIR=${D}
}
