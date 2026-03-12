# SPDX-License-Identifier: MIT
#
# Copyright (c) 2020 Arm Limited
#

SUMMARY = "Trusted Firmware for Cortex-M"
DESCRIPTION = "Trusted Firmware-M"
HOMEPAGE = "https://trustedfirmware-m.readthedocs.io/"
PROVIDES = "virtual/trusted-firmware-m"

UPSTREAM_CHECK_GITTAGREGEX = "^TF-Mv(?P<pver>\d+(\.\d+)+)$"

# Note to future readers of this recipe: until the CMakeLists don't abuse
# installation (see do_install) there is no point in trying to inherit
# cmake here. You can easily short-circuit the toolchain but the install
# is so convoluted there's no gain.

inherit python3native firmware

# Baremetal and we bring a compiler below
INHIBIT_DEFAULT_DEPS = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

# At present, TF-M Select other GNU Arm compiler versions instead of 11.2:
# https://git.trustedfirmware.org/TF-M/trusted-firmware-m.git/tree/docs/getting_started/tfm_getting_started.rst#n214
#
# See tools/requirements.txt for Python dependencies
DEPENDS += "cmake-native \
            ninja-native \
            gcc-arm-none-eabi-native \
            python3-cbor2-native \
            python3-click-native \
            python3-cryptography-native \
            python3-pyasn1-native \
            python3-imgtool-native \
            python3-jinja2-native \
            python3-pyyaml-native \
            python3-pyhsslms-native \
            python3-ecdsa-native \
            python3-kconfiglib-native \
"

B = "${WORKDIR}/build"

# Build for debug (set TFM_DEBUG to 1 to activate)
TFM_DEBUG ?= "${FIRMWARE_DEBUG_BUILD}"

# Platform can be overridden in the machine configuration.
TFM_PLATFORM ?= "${FIRMWARE_PLATFORM}"
python() {
    if not d.getVar("TFM_PLATFORM"):
        raise bb.parse.SkipRecipe("TFM_PLATFORM needs to be set")
}

PACKAGECONFIG ??= ""
# Whether to integrate the test suite
PACKAGECONFIG[test-secure] = "-DTEST_S=ON,-DTEST_S=OFF"
PACKAGECONFIG[test-nonsecure] = "-DTEST_NS=ON,-DTEST_NS=OFF"

# Currently we only support using the Arm binary GCC
EXTRA_OECMAKE += "-DTFM_TOOLCHAIN_FILE=${S}/toolchain_GNUARM.cmake"

# Don't let FetchContent download more sources during do_configure
EXTRA_OECMAKE += "-DFETCHCONTENT_FULLY_DISCONNECTED=ON"

# Add platform parameters
EXTRA_OECMAKE += "-DTFM_PLATFORM=${TFM_PLATFORM}"

# Handle TFM_DEBUG parameter
EXTRA_OECMAKE += "${@bb.utils.contains('TFM_DEBUG', '1', '-DCMAKE_BUILD_TYPE=Debug', '-DCMAKE_BUILD_TYPE=Release', d)}"

EXTRA_OECMAKE += "\
    -DCMSIS_PATH=${S}/external/cmsis \
    -DMBEDCRYPTO_PATH=${S}/external/mbedtls \
    -DMCUBOOT_PATH=${S}/external/mcuboot \
    -DQCBOR_PATH=${S}/external/qcbor \
    -DT_COSE_PATH=${S}/external/t_cose \
    -DTFM_EXTRAS_REPO_PATH=${S}/external/tfm-extras \
    -DTFM_TEST_REPO_PATH=${S}/external/tf-m-tests \
    "

export CMAKE_BUILD_PARALLEL_LEVEL = "${@oe.utils.parallel_make(d, False)}"

AS[unexport] = "1"
CC[unexport] = "1"
LD[unexport] = "1"

# python3-cryptography needs the legacy provider, so set OPENSSL_MODULES to the
# right path until this is relocated automatically.
export OPENSSL_MODULES = "${STAGING_LIBDIR_NATIVE}/ossl-modules"

do_configure[cleandirs] = "${B}"
do_configure() {
    cmake -GNinja -S ${S} -B ${B} ${EXTRA_OECMAKE} ${PACKAGECONFIG_CONFARGS}
}

# Invoke install here as there's no point in splitting compile from install: the
# first thing the build does is 'install' inside the build tree thus causing a
# rebuild. It also overrides the install prefix to be in the build tree, so you
# can't use the usual install prefix variables.
do_compile() {
    cmake --build ${B} -- install
}
do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"

do_install() {
    # TODO install headers and static libraries when we know how they're used
    install -d -m 755 ${D}${FIRMWARE_DIR}
    find ${B}/bin \( -name '*.bin' -o -name '*.elf' \) -type f | while read -r file; do
        install -m 0644 "$file" "${D}${FIRMWARE_DIR}"
    done
}

# Build paths are currently embedded because it's impossible to pass -fdebug-prefix-map
INSANE_SKIP:${PN} += "buildpaths"
INSANE_SKIP:${PN}-dbg += "buildpaths"
# Target binaries will be 32-bit Arm
INSANE_SKIP:${PN} += "arch"
INSANE_SKIP:${PN}-dbg += "arch"
