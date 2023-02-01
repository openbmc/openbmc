SUMMARY = "OP-TEE Trusted OS"
DESCRIPTION = "Open Portable Trusted Execution Environment - Trusted side of the TEE"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c1f21c4f72f372ef38a5a4aee55ec173"

inherit deploy python3native
require optee.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os-3.19.0:"

CVE_PRODUCT = "linaro:op-tee op-tee:op-tee_os"

DEPENDS = "python3-pyelftools-native python3-cryptography-native"

DEPENDS:append:toolchain-clang = " compiler-rt"

SRC_URI = "git://github.com/OP-TEE/optee_os.git;branch=master;protocol=https"

SRC_URI:append = " \
    file://0006-allow-setting-sysroot-for-libgcc-lookup.patch \
    file://0007-allow-setting-sysroot-for-clang.patch \
    file://0008-no-warn-rwx-segments.patch \
   "

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

EXTRA_OEMAKE += " \
    PLATFORM=${OPTEEMACHINE} \
    CFG_${OPTEE_CORE}_core=y \
    CROSS_COMPILE_core=${HOST_PREFIX} \
    CROSS_COMPILE_ta_${OPTEE_ARCH}=${HOST_PREFIX} \
    NOWERROR=1 \
    ta-targets=ta_${OPTEE_ARCH} \
    O=${B} \
"
EXTRA_OEMAKE += " HOST_PREFIX=${HOST_PREFIX}"
EXTRA_OEMAKE += " CROSS_COMPILE64=${HOST_PREFIX}"

CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
CPPFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

do_compile:prepend() {
	PLAT_LIBGCC_PATH=$(${CC} -print-libgcc-file-name)
}

do_compile() {
    oe_runmake -C ${S} all
}
do_compile[cleandirs] = "${B}"

do_install() {
    #install core in firmware
    install -d ${D}${nonarch_base_libdir}/firmware/
    install -m 644 ${B}/core/*.bin ${B}/core/tee.elf ${D}${nonarch_base_libdir}/firmware/
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_deploy() {
    install -d ${DEPLOYDIR}/${MLPREFIX}optee
    install -m 644 ${D}${nonarch_base_libdir}/firmware/* ${DEPLOYDIR}/${MLPREFIX}optee
}

addtask deploy before do_build after do_install

SYSROOT_DIRS += "${nonarch_base_libdir}/firmware"

FILES:${PN} = "${nonarch_base_libdir}/firmware/"

# note: "textrel" is not triggered on all archs
INSANE_SKIP:${PN} = "textrel"
# Build paths are currently embedded
INSANE_SKIP:${PN} += "buildpaths"
INSANE_SKIP:${PN}-dev = "staticdev"
INHIBIT_PACKAGE_STRIP = "1"

