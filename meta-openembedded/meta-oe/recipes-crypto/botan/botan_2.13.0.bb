SUMMARY = "Crypto and TLS for C++11"
HOMEPAGE = "https://botan.randombit.net"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=a02e03c8fa2c5e7b9b3fcc1b9811fd3b"
SECTION = "libs"

SRC_URI = "https://botan.randombit.net/releases/Botan-${PV}.tar.xz"
SRC_URI[md5sum] = "9f90c7d523778be5f23f3ce79046ba35"
SRC_URI[sha256sum] = "f57ae42a41e1091bca58f44f41addebd9a390b651603952c881ec89d50187e90"

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
