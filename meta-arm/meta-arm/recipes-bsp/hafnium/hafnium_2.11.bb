SUMMARY = "Hafnium"
DESCRIPTION = "A reference Secure Partition Manager (SPM) for systems that implement the Armv8.4-A Secure-EL2 extension"
DEPENDS = "gn-native ninja-native bison-native bc-native dtc-native openssl-native"

LICENSE = "BSD-3-Clause & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=782b40c14bad5294672c500501edc103"

PACKAGE_ARCH = "${MACHINE_ARCH}"


CLANGNATIVE = ""
CLANGNATIVE:runtime-llvm = "clang-native"

inherit deploy python3native pkgconfig ${CLANGNATIVE}

SRC_URI = "gitsm://git.trustedfirmware.org/hafnium/hafnium.git;protocol=https;branch=master \
           file://0001-arm-hafnium-fix-kernel-tool-linking.patch  \
           file://0001-Use-pkg-config-native-to-find-the-libssl-headers.patch;patchdir=third_party/linux \
           file://0001-work-around-visibility-issue.patch;patchdir=third_party/dtc \
          "
SRCREV = "2bef7ab3895c48d39b84ab58179b2d0de5156b8b"
S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

COMPATIBLE_MACHINE ?= "invalid"
COMPATIBLE_MACHINE:qemuarm64 = "qemuarm64"

# Default build 'reference'
HAFNIUM_PROJECT ?= "reference"

# Platform must be set for each machine
HAFNIUM_PLATFORM ?= "invalid"
HAFNIUM_PLATFORM:qemuarm64 = "qemu_aarch64"

# do_deploy will install everything listed in this variable. It is set by
# default to hafnium
HAFNIUM_INSTALL_TARGET ?= "hafnium"

# set project to build
EXTRA_OEMAKE += "PROJECT=${HAFNIUM_PROJECT}"

EXTRA_OEMAKE += "OUT_DIR=${B}"

# Don't use prebuilt binaries for gn and ninja
EXTRA_OEMAKE += "GN=${STAGING_BINDIR_NATIVE}/gn NINJA=${STAGING_BINDIR_NATIVE}/ninja"

do_configure[cleandirs] += "${B}"

do_compile() {
    oe_runmake -C ${S}
}

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

python() {
    # https://developer.trustedfirmware.org/T898
    if d.getVar("BUILD_ARCH") != "x86_64":
        raise bb.parse.SkipRecipe("Cannot be built on non-x86-64 hosts")
}

EXCLUDE_FROM_WORLD = "1"
