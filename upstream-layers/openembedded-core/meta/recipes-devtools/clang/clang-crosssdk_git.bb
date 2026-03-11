# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "SDK Cross compiler wrappers for LLVM based C/C++ compiler"
HOMEPAGE = "http://clang.llvm.org/"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0-with-LLVM-exception;md5=0bcd48c3bdfef0c9d9fd17726e4b7dab"
SECTION = "devel"

PN = "clang-crosssdk-${SDK_SYS}"
BPN = "clang"

require common-clang.inc
require common-source.inc
inherit crosssdk
TOOLCHAIN = "clang"
DEPENDS += "clang-native nativesdk-clang-glue virtual/nativesdk-cross-binutils virtual/nativesdk-libc"

do_install() {
    install -d ${D}${bindir}
	for tool in clang clang++ clang-tidy lld ld.lld llvm-profdata \
		llvm-nm llvm-ar llvm-as llvm-ranlib llvm-strip llvm-objcopy llvm-objdump llvm-readelf \
		llvm-addr2line llvm-dwp llvm-size llvm-strings llvm-cov
	do
		ln -sf ../$tool ${D}${bindir}/${TARGET_PREFIX}$tool
	done
}
SSTATE_SCAN_FILES += "*-clang *-clang++ *-llvm-profdata *-lld *-ld.lld \
                      *-llvm-nm *-llvm-ar *-llvm-as *-llvm-ranlib *-llvm-strip \
                      *-llvm-objcopy *-llvm-objdump *-llvm-readelf *-llvm-addr2line \
                      *-llvm-dwp *-llvm-size *-llvm-strings *-llvm-cov"
sysroot_stage_all () {
	sysroot_stage_dir ${D}${bindir} ${SYSROOT_DESTDIR}${bindir}
}

PACKAGES = ""
