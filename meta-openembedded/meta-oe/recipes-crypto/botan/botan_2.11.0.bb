SUMMARY = "Crypto and TLS for C++11"
HOMEPAGE = "https://botan.randombit.net"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=f4c145d4d70a3244a053c9f90d4841fc"
SECTION = "libs"

SRC_URI = "https://botan.randombit.net/releases/Botan-${PV}.tar.xz"
SRC_URI[md5sum] = "2dba13b0d25d0e9d2a6f9867c62c9f2e"
SRC_URI[sha256sum] = "f7874da2aeb8c018fd77df40b2137879bf90b66f5589490c991e83fb3e8094be"

S = "${WORKDIR}/Botan-${PV}"

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
