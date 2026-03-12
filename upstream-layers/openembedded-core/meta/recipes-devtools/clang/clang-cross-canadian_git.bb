# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Clang/LLVM based C/C++ compiler (cross-canadian for ${TARGET_ARCH} target)"
HOMEPAGE = "http://clang.llvm.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SECTION = "devel"

PN = "clang-cross-canadian-${TRANSLATED_TARGET_ARCH}"

inherit cross-canadian

DEPENDS = "nativesdk-clang binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} virtual/nativesdk-cross-binutils virtual/nativesdk-libc ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', 'nativesdk-lld', '', d)}"

do_install() {
	install -d ${D}${bindir}
	for tool in clang clang++ clang-tidy lld ld.lld llvm-profdata \
		llvm-nm llvm-ar llvm-as llvm-ranlib llvm-strip llvm-objcopy llvm-objdump llvm-readelf \
		llvm-addr2line llvm-dwp llvm-size llvm-strings llvm-cov
	do
		ln -sf ../$tool ${D}${bindir}/${TARGET_PREFIX}$tool
	done
}

do_install:append() {
	cross_canadian_bindirlinks
}
