# Copyright (C) 2014 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "SDK Cross compiler wrappers for LLVM based C/C++ compiler"
HOMEPAGE = "http://clang.llvm.org/"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0-with-LLVM-exception;md5=0bcd48c3bdfef0c9d9fd17726e4b7dab"
SECTION = "devel"

inherit_defer nativesdk
DEPENDS += "nativesdk-clang"

do_install() {
    install -d ${D}${prefix_nativesdk}
    cd ${D}${prefix_nativesdk}
    ln -s ..${libdir} .
    ln -s ..${includedir} .
    cd ..
    ln -s .${base_libdir} .
}

sysroot_stage_all () {
	sysroot_stage_dir ${D} ${SYSROOT_DESTDIR}
}

FILES:${PN} += "${prefix_nativesdk} ${base_libdir_nativesdk}"
FILES:${PN}-dbg = ""

deltask do_configure
deltask do_compile
deltask do_patch
deltask do_fetch
deltask do_unpack
deltask do_create_spdx
deltask do_create_package_spdx
deltask do_create_runtime_spdx
