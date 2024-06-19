SUMMARY = "External system Cortex-M3 Firmware"
DESCRIPTION = "Firmware to be loaded and run in External System Harness in\
               support to the main application CPU."
HOMEPAGE = "https://git.linaro.org/landing-teams/working/arm/external-system.git"
DEPENDS = "gcc-arm-none-eabi-native"
INHIBIT_DEFAULT_DEPS="1"
LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://license.md;md5=e44b2531cd6ffe9dece394dbe988d9a0 \
                    file://cmsis/LICENSE.txt;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = "gitsm://git.gitlab.arm.com/arm-reference-solutions/corstone1000/external_system/rtx.git;protocol=https;branch=master \
           file://0001-tools-gen_module_code-atomically-rewrite-the-generat.patch"
SRCREV = "8c9dca74b104ff6c9722fb0738ba93dd3719c080"
PV .= "+git"

COMPATIBLE_MACHINE = "(corstone1000)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

# PRODUCT is passed to the Makefile to specify the platform to be used.
PRODUCT = "corstone-1000"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# remove once arm-none-eabi-gcc updates to 13 or newer like poky
DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

LDFLAGS[unexport] = "1"

do_compile() {
    oe_runmake -C ${S} V=y \
        BUILD_PATH=${B} \
	PRODUCT=${PRODUCT} \
	CROSS_COMPILE=arm-none-eabi- \
        all
}

do_compile[cleandirs] = "${B}"

do_install() {
    install -D -p -m 0644 ${B}/product/${PRODUCT}/firmware/release/bin/firmware.bin ${D}${nonarch_base_libdir}/firmware/es_flashfw.bin
    install -D -p -m 0644 ${B}/product/${PRODUCT}/firmware/release/bin/firmware.elf ${D}${nonarch_base_libdir}/firmware/es_flashfw.elf
}

FILES:${PN} = "${nonarch_base_libdir}/firmware/es_flashfw.bin"
FILES:${PN}-elf = "${nonarch_base_libdir}/firmware/es_flashfw.elf"
PACKAGES += "${PN}-elf"
INSANE_SKIP:${PN}-elf += "arch"

SYSROOT_DIRS += "${nonarch_base_libdir}/firmware"

inherit deploy

do_deploy() {
    cp -rf ${D}${nonarch_base_libdir}/firmware/* ${DEPLOYDIR}/
}
addtask deploy after do_install
