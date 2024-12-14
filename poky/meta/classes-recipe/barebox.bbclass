#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit kernel-arch deploy cml1 pkgconfig

LICENSE ?= "GPL-2.0-only"

PROVIDES += "virtual/bootloader"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "bison-native flex-native"

S = "${WORKDIR}/barebox-${PV}"
B = "${WORKDIR}/build"

require conf/image-uefi.conf

# For some platforms and configuration, the barebox build process will require
# additional host tools that can be activated/deactivated here.
PACKAGECONFIG ??= "openssl libusb fit"

PACKAGECONFIG[openssl] = ",,openssl-native"
PACKAGECONFIG[libusb] = ",,libusb1-native"
PACKAGECONFIG[fit] = ",,u-boot-tools-native dtc-native"

export KBUILD_BUILD_USER ?= "oe-user"
export KBUILD_BUILD_HOST ?= "oe-host"

# unlike the kernel, barebox may build against host tools like openssl
export HOST_EXTRACFLAGS

def get_layer_rev(path):
    try:
        rev, _ = bb.process.run("git describe --match='' --always --dirty --broken", cwd=path)
    except bb.process.ExecutionError:
        rev = ""
    return rev.strip()

BAREBOX_BUILDSYSTEM_VERSION[doc] = "Build system version to add to the barebox image. By default this is the git description of the containing layer."
BAREBOX_BUILDSYSTEM_VERSION ??= "${@get_layer_rev(os.path.dirname(d.getVar('FILE')))}"

BAREBOX_FIRMWARE_DIR[doc] = "Overwrite barebox' firmware blobs search directory (CONFIG_EXTRA_FIRMWARE_DIR) with this path, default ${B}/firmware"
BAREBOX_FIRMWARE_DIR ??= "${B}/firmware"

EXTRA_OEMAKE = " \
    CROSS_COMPILE=${TARGET_PREFIX} -C ${S} O=${B} \
    BUILDSYSTEM_VERSION=${BAREBOX_BUILDSYSTEM_VERSION} \
    CONFIG_EXTRA_FIRMWARE_DIR=${BAREBOX_FIRMWARE_DIR} \
    PKG_CONFIG=pkg-config-native \
    CROSS_PKG_CONFIG=pkg-config \
"

BAREBOX_CONFIG[doc] = "The barebox kconfig defconfig file. Not used if a file called defconfig is added to the SRC_URI."
BAREBOX_CONFIG ?= ""

# set sensible default configs for some of oe-core's QEMU MACHINEs
BAREBOX_CONFIG:qemuarm = "multi_v7_defconfig"
BAREBOX_CONFIG:qemuarm64 = "multi_v8_defconfig"
BAREBOX_CONFIG:qemux86-64 = "efi_defconfig"

# prevent from acting as non-buildable provider
python () {
    bareboxconfig = d.getVar('BAREBOX_CONFIG')
    bareboxdefconfig = 'file://defconfig' in d.getVar('SRC_URI')

    if not bareboxconfig and not bareboxdefconfig:
        raise bb.parse.SkipRecipe("BAREBOX_CONFIG must be set in the %s machine configuration or file://defconfig must be given in SRC_URI." % d.getVar("MACHINE"))
}

barebox_do_configure() {
        if [ -e ${UNPACKDIR}/defconfig ]; then
                cp ${UNPACKDIR}/defconfig ${B}/.config
        else
                if [ -n "${BAREBOX_CONFIG}" ]; then
                        oe_runmake ${BAREBOX_CONFIG}
                else
                        bbfatal "No defconfig given. Either add file 'file://defconfig' to SRC_URI or set BAREBOX_CONFIG"
                fi
        fi

        ${S}/scripts/kconfig/merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
        cml1_do_configure
}

BAREBOX_ENV_DIR[doc] = "Overlay the barebox built-in environment with the environment provided by the BSP if specified."
BAREBOX_ENV_DIR ??= "${UNPACKDIR}/env/"

barebox_do_compile () {
        export userccflags="${TARGET_LDFLAGS}${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
        unset LDFLAGS
        unset CFLAGS
        unset CPPFLAGS
        unset CXXFLAGS
        unset MACHINE
        # Allow to use ${UNPACKDIR} in kconfig options to include additionally fetched files
        export UNPACKDIR=${UNPACKDIR}

        if [ -d ${BAREBOX_ENV_DIR} ]; then
                BAREBOX_DEFAULT_ENV="$(grep ^CONFIG_DEFAULT_ENVIRONMENT_PATH .config | cut -d '=' -f 2 | tr -d '"')"
                oe_runmake CONFIG_DEFAULT_ENVIRONMENT_PATH="\"${BAREBOX_DEFAULT_ENV} ${BAREBOX_ENV_DIR}\""
        else
                oe_runmake
        fi
}

BAREBOX_BINARY[doc] = "Specify the barebox binary to install. If not specified all barebox artifacts are installed."
BAREBOX_BINARY ??= "${@'barebox.efi' if d.getVar('EFI_PROVIDER') == 'barebox' else ''}"
BAREBOX_SUFFIX[doc] = "Specify the suffix for ${BAREBOX_IMAGE}."
BAREBOX_SUFFIX ??= "img"
BAREBOX_IMAGE[doc] = "A unique barebox image name. Unused if ${BAREBOX_BINARY} is not set."
BAREBOX_IMAGE_DEFAULT ?= "${PN}-${MACHINE}-${PV}-${PR}.${BAREBOX_SUFFIX}"
BAREBOX_IMAGE ?= "${@'${EFI_BOOT_IMAGE}' if d.getVar('EFI_PROVIDER') == 'barebox' else '${BAREBOX_IMAGE_DEFAULT}'}"

BAREBOX_INSTALL_PATH ?= "${@'${EFI_FILES_PATH}' if d.getVar('EFI_PROVIDER') == 'barebox' else '/boot'}"

barebox_do_install () {
        if [ -n "${BAREBOX_BINARY}" ]; then

                BAREBOX_BIN=${B}/${BAREBOX_BINARY}
                if [ ! -f "${BAREBOX_BIN}" ]; then
                        BAREBOX_BIN=${B}/images/${BAREBOX_BINARY}
                fi
                if [ ! -f "${BAREBOX_BIN}" ]; then
                        bbfatal "Failed to locate ${BAREBOX_BINARY}"
                fi

                install -D -m 644 ${BAREBOX_BIN} ${D}${BAREBOX_INSTALL_PATH}/${BAREBOX_IMAGE}
                ln -sf ${BAREBOX_IMAGE} ${D}${BAREBOX_INSTALL_PATH}/${BAREBOX_BINARY}
        else
                install -d ${D}${BAREBOX_INSTALL_PATH}/
                for image in $(cat ${B}/barebox-flash-images); do
                        install -m 644 ${B}/${image} ${D}${BAREBOX_INSTALL_PATH}/
                done
        fi
}
FILES:${PN} = "${BAREBOX_INSTALL_PATH}"

barebox_do_deploy () {
        if [ -n "${BAREBOX_BINARY}" ]; then

                BAREBOX_BIN=${B}/${BAREBOX_BINARY}
                if [ ! -f "${BAREBOX_BIN}" ]; then
                        BAREBOX_BIN=${B}/images/${BAREBOX_BINARY}
                fi

                install -D -m 644 ${BAREBOX_BIN} ${DEPLOYDIR}/${BAREBOX_IMAGE}
                ln -sf ${BAREBOX_IMAGE} ${DEPLOYDIR}/${BAREBOX_BINARY}
        else
                for image in $(cat ${B}/barebox-flash-images); do
                        cp ${B}/${image} ${DEPLOYDIR}
                done
        fi
}
addtask deploy after do_compile

EXPORT_FUNCTIONS do_configure do_compile do_install do_deploy
