SUMMARY = "Build tools needed by external modules"
HOMEPAGE = "https://www.yoctoproject.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit kernel-arch linux-kernel-base
inherit pkgconfig

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_configure[depends] += "virtual/kernel:do_shared_workdir openssl-native:do_populate_sysroot"
do_compile[depends] += "virtual/kernel:do_compile_kernelmodules"

DEV_PKG_DEPENDENCY = ""

DEPENDS += "bc-native bison-native"
DEPENDS += "gmp-native"

EXTRA_OEMAKE = " HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" HOSTCPP="${BUILD_CPP}""
EXTRA_OEMAKE += " HOSTCXX="${BUILD_CXX} ${BUILD_CXXFLAGS} ${BUILD_LDFLAGS}" CROSS_COMPILE=${TARGET_PREFIX}"

KERNEL_LOCALVERSION = "${@get_kernellocalversion_file("${STAGING_KERNEL_BUILDDIR}")}"
export LOCALVERSION="${KERNEL_LOCALVERSION}"

# Build some host tools under work-shared.  CC, LD, and AR are probably
# not used, but this is the historical way of invoking "make scripts".
#
do_configure() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	for t in prepare scripts_basic scripts; do
		oe_runmake CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
		AR="${KERNEL_AR}" OBJCOPY="${KERNEL_OBJCOPY}" \
		STRIP="${KERNEL_STRIP}" \
		-C ${STAGING_KERNEL_DIR} O=${STAGING_KERNEL_BUILDDIR} $t
	done
}
