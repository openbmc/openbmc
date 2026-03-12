SUMMARY = "Hafnium"
DESCRIPTION = "A reference Secure Partition Manager (SPM) for systems that implement the Armv8.4-A Secure-EL2 extension"
DEPENDS = "gn-native ninja-native bison-native bc-native dtc-native openssl-native clang-native lld-native"

LICENSE = "BSD-3-Clause & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=782b40c14bad5294672c500501edc103"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

SRC_URI = "gitsm://git.trustedfirmware.org/hafnium/hafnium.git;protocol=https;branch=master \
           file://0001-arm-hafnium-fix-kernel-tool-linking.patch  \
           file://0001-work-around-visibility-issue.patch;patchdir=third_party/dtc \
          "
SRCREV = "ce12c6e53838f1cf07d50b616b72db57a81539a4"
B = "${WORKDIR}/build"

COMPATIBLE_MACHINE ?= "invalid"
COMPATIBLE_MACHINE:qemuarm64-secureboot = "qemuarm64"

# Default build 'reference'
HAFNIUM_PROJECT ?= "reference"

# Platform must be set for each machine
HAFNIUM_PLATFORM ?= "invalid"
HAFNIUM_PLATFORM:qemuarm64-secureboot = "secure_qemu_aarch64"

# do_deploy will install everything listed in this variable. It is set by
# default to hafnium
HAFNIUM_INSTALL_TARGET ?= "hafnium"

# set project to build
EXTRA_OEMAKE += "PROJECT=${HAFNIUM_PROJECT}"

EXTRA_OEMAKE += "OUT_DIR=${B}"

EXTRA_OEMAKE += "PLATFORM=${HAFNIUM_PLATFORM}"

# Don't use prebuilt binaries for gn and ninja
EXTRA_OEMAKE += "GN=${STAGING_BINDIR_NATIVE}/gn NINJA=${STAGING_BINDIR_NATIVE}/ninja"

prune_prebuilts() {
    for dir in dtc gn ninja qemu; do
        rm -rf ${S}/prebuilts/*/$dir
    done
}
do_unpack[postfuncs] += "prune_prebuilts"

do_configure() {
    oe_runmake -C ${S} ${B}/build.ninja
}
do_configure[cleandirs] += "${B}"

do_compile() {
    ninja -v ${PARALLEL_MAKE} root
}
do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"

do_install() {
    cd ${B}/${HAFNIUM_PLATFORM}_clang
    install -d -m 755 ${D}/firmware
    for bldfile in ${HAFNIUM_INSTALL_TARGET}; do
        install -m 0755 $bldfile.bin $bldfile.elf ${D}/firmware/
    done
}

FILES:${PN} = "/firmware/*.bin"
FILES:${PN}-dbg = "/firmware/*.elf"
SYSROOT_DIRS += "/firmware"
INSANE_SKIP:${PN} = "ldflags"
INSANE_SKIP:${PN}-dbg = "ldflags"
# Build paths are currently embedded
INSANE_SKIP:${PN}-dbg += "buildpaths"

do_deploy() {
    cp -rf ${D}/firmware/* ${DEPLOYDIR}/
}
addtask deploy after do_install
