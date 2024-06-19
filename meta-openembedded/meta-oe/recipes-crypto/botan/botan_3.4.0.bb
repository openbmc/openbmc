SUMMARY = "Crypto and TLS for C++11"
HOMEPAGE = "https://botan.randombit.net"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=f5254d3abe90ec5bb82c5694ff751546"
SECTION = "libs"

SRC_URI = "https://botan.randombit.net/releases/Botan-${PV}.tar.xz"
SRC_URI[sha256sum] = "71843afcc0a2c585f8f33fa304f0b58ae4b9c5d8306f894667b3746044277557"

S = "${WORKDIR}/Botan-${PV}"

inherit python3native siteinfo lib_package

CPU ?= "${TARGET_ARCH}"
CPU:x86 = "x86_32"
CPU:armv7a = "armv7"
CPU:armv7ve = "armv7"

do_configure() {
	python3 ${S}/configure.py \
	--prefix="${D}${exec_prefix}" \
	--libdir="${D}${libdir}" \
	--cpu="${CPU}" \
	--cc-bin="${CXX}" \
	--cxxflags="${CXXFLAGS}" \
	--ldflags="${LDFLAGS}" \
	--with-endian=${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', 'little', 'big', d)} \
	${@bb.utils.contains("TUNE_FEATURES","neon","","--disable-neon",d)} \
	--with-sysroot-dir=${STAGING_DIR_HOST} \
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
	sed -i -e "s|${D}||g" ${D}${libdir}/pkgconfig/botan-3.pc
}

PACKAGES += "${PN}-python3"

FILES:${PN}-python3 = "${libdir}/python3"

RDEPENDS:${PN}-python3 += "python3"

COMPATIBLE_HOST:riscv32 = "null"
