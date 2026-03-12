# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Cross compiler wrappers for LLVM based C/C++ compiler"
HOMEPAGE = "http://clang.llvm.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SECTION = "devel"

PN = "clang-cross-${TARGET_ARCH}"

inherit cross

DEPENDS = "clang-native virtual/cross-binutils ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', 'lld-native', '', d)}"

do_install() {
	install -d ${D}${bindir}
	for tool in clang-tidy lld ld.lld llvm-profdata \
            llvm-nm llvm-ar llvm-as llvm-ranlib llvm-strip llvm-objcopy llvm-objdump llvm-readelf \
            llvm-addr2line llvm-dwp llvm-size llvm-strings llvm-cov
	do
		if [ -x ${STAGING_BINDIR_NATIVE}/$tool ]; then
			ln -sf ../$tool ${D}${bindir}/${TARGET_PREFIX}$tool
		fi
	done
	# GNU Linker and assembler is needed in same directory as clang binaries else
	# it will fallback to host linker which is not desired
	install -m 0755 ${STAGING_BINDIR_NATIVE}/clang ${D}${bindir}/${TARGET_PREFIX}clang
	ln -sf ${TARGET_PREFIX}clang ${D}${bindir}/${TARGET_PREFIX}clang++
}
# clang driver being copied above is already stripped
INHIBIT_SYSROOT_STRIP = "1"
