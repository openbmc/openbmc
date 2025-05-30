# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Clang/LLVM based C/C++ compiler (cross-canadian for ${TARGET_ARCH} target)"
HOMEPAGE = "http://clang.llvm.org/"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0-with-LLVM-exception;md5=0bcd48c3bdfef0c9d9fd17726e4b7dab"
SECTION = "devel"

PN = "clang-cross-canadian-${TRANSLATED_TARGET_ARCH}"

require common-clang.inc
require common-source.inc
inherit cross-canadian

DEPENDS += "nativesdk-clang binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} virtual/nativesdk-cross-binutils virtual/nativesdk-libc"
# We have to point gcc at a sysroot but we don't need to rebuild if this changes
# e.g. we switch between different machines with different tunes.
EXTRA_OECONF_PATHS[vardepsexclude] = "TUNE_PKGARCH"
TARGET_ARCH[vardepsexclude] = "TUNE_ARCH"

do_install() {
	install -d ${D}${bindir}
	for tool in clang clang++ clang-tidy lld ld.lld llvm-profdata \
		llvm-nm llvm-ar llvm-as llvm-ranlib llvm-strip llvm-objcopy llvm-objdump llvm-readelf \
		llvm-addr2line llvm-dwp llvm-size llvm-strings llvm-cov
	do
		ln -sf ../$tool ${D}${bindir}/${TARGET_PREFIX}$tool
	done
}
SSTATE_SCAN_FILES += "*-clang *-clang++ *-llvm-profdata *-llvm-ar \
                      *-llvm-ranlib *-llvm-nm *-lld *-ld.lld *-llvm-as *-llvm-strip \
                      *-llvm-objcopy *-llvm-objdump *-llvm-readelf *-llvm-addr2line \
                      *-llvm-dwp *-llvm-size *-llvm-strings *-llvm-cov"
do_install:append() {
	cross_canadian_bindirlinks
}
