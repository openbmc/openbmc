# Copyright (C) 2018 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Crypto and TLS for C++11"
HOMEPAGE = "https://botan.randombit.net"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=bf361fc63df3fa25652ee82c43b7601a"
SECTION = "libs"


#v2.7.0
SRCREV = "5874000d42c338ec95a7ff24cdc0c64e70f967b5"
SRC_URI = "git://github.com/randombit/botan.git"

S = "${WORKDIR}/git"

inherit python3native siteinfo lib_package

CPU ?= "${TARGET_ARCH}"
CPU_x86 = "x86_32"
CPU_armv7a = "armv7"
CPU_armv7ve = "armv7"

do_configure() {
	python3 ${S}/configure.py \
	--prefix="${D}${prefix}" \
	--cpu="${CPU}" \
	--cc-bin="${CXX}" \
	--cxxflags="${CXXFLAGS}" \
	--ldflags="${LDFLAGS}" \
	--with-endian=${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', 'little', 'big', d)} \
	${@bb.utils.contains("TUNE_FEATURES","neon","","--disable-neon",d)} \
	--with-sysroot-dir=${STAGING_DIR_TARGET} \
	--with-build-dir="${B}" \
	--optimize-for-size \
	--with-stack-protector \
	--enable-shared-library \
	--with-python-versions=3 \
	${EXTRA_OECONF}
}

do_compile() {
	oe_runmake
}
do_install() {
	oe_runmake install
	sed -i -e "s|${D}||g" ${D}${libdir}/pkgconfig/botan-2.pc
}

PACKAGES += "${PN}-python3"

FILES_${PN}-python3 = "${libdir}/python3"

RDEPENDS_${PN}-python3 += "python3"

