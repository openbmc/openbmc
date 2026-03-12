SUMMARY = "Universal Boot Loader for embedded devices"
PROVIDES = "virtual/bootloader"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "${@bb.utils.contains('UBOOT_ENV_SUFFIX', 'scr', 'u-boot-mkimage-native', '', d)}"
DEPENDS += "${@ 'u-boot-mkenvimage-native' if bb.utils.to_boolean(d.getVar('UBOOT_INITIAL_ENV_BINARY')) else ''}"

inherit uboot-config uboot-extlinux-config uboot-sign deploy python3native kernel-arch

DEPENDS += "swig-native"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} V=1'
EXTRA_OEMAKE += 'CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS} ${DEBUG_PREFIX_MAP}"'
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
EXTRA_OEMAKE += 'STAGING_INCDIR=${STAGING_INCDIR_NATIVE} STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE}'

PACKAGECONFIG ??= "openssl"
# u-boot will compile its own tools during the build, with specific
# configurations (aka when CONFIG_FIT_SIGNATURE is enabled) openssl is needed as
# a host build dependency.
PACKAGECONFIG[openssl] = ",,openssl-native"

CVE_PRODUCT = "denx:u-boot"

# Allow setting an additional version string that will be picked up by the
# u-boot build system and appended to the u-boot version.  If the .scmversion
# file already exists it will not be overwritten.
UBOOT_LOCALVERSION ?= ""

# Default name of u-boot initial env, but enable individual recipes to change
# this value.
UBOOT_INITIAL_ENV ?= "${PN}-initial-env"

require u-boot-configure.inc

UBOOT_ARCH_DIR = "${@'arm' if d.getVar('UBOOT_ARCH').startswith('arm') else d.getVar('UBOOT_ARCH')}"
do_compile () {
    unset LDFLAGS
    unset CFLAGS
    unset CPPFLAGS

    if [ ! -e ${B}/.scmversion -a ! -e ${S}/.scmversion ]
    then
        echo ${UBOOT_LOCALVERSION} > ${B}/.scmversion
        echo ${UBOOT_LOCALVERSION} > ${S}/.scmversion
    fi

    if [ -n "${UBOOT_CONFIG}" -o -n "${UBOOT_DELTA_CONFIG}" ]
    then
        unset i j
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    uboot_compile_config $i $config $type
                fi
            done
            unset j
        done
        unset i
    else
        uboot_compile
    fi

    if [ -n "${UBOOT_ENV}" ]
    then
        # If UBOOT_ENV_SUFFIX is set to scr the script is packaged as a uImage otherwise it gets installed verbatim.
        if [ "${UBOOT_ENV_SUFFIX}" = "scr" ]
        then
            ${UBOOT_MKIMAGE} -C none -A ${UBOOT_ARCH} -T script -d ${UNPACKDIR}/${UBOOT_ENV_SRC} ${B}/${UBOOT_ENV_BINARY}
        else
            install -m 644 ${UNPACKDIR}/${UBOOT_ENV_SRC} ${B}/${UBOOT_ENV_BINARY}
        fi
    fi
}

uboot_compile_config () {
    i=$1
    config=$2
    type=$3

    builddir="${config}-${type}"

    config_make_opts=$(uboot_config_get_indexed_value "${UBOOT_CONFIG_MAKE_OPTS}" $i)
    config_binary=$(uboot_config_get_indexed_value "${UBOOT_CONFIG_BINARY}" $i)

    oe_runmake -C ${S} O=${B}/${builddir} ${config_make_opts} ${UBOOT_MAKE_OPTS} ${UBOOT_MAKE_TARGET}

    uboot_compile_config_copy_binary $config $type $config_binary

    # Generate the uboot-initial-env
    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
        oe_runmake -C ${S} O=${B}/${builddir} u-boot-initial-env
        cp ${B}/${builddir}/u-boot-initial-env ${B}/${builddir}/u-boot-initial-env-${type}
        if [ "${UBOOT_INITIAL_ENV_BINARY}" = "1" ] ; then
            uboot_compile_initial_env_binary
            cp ${B}/${builddir}/u-boot-initial-env.bin ${B}/${builddir}/u-boot-initial-env-${type}.bin
        fi
    fi
}

uboot_compile_config_copy_binary () {
    config=$1
    type=$2
    binary=$3

    builddir="${config}-${type}"

    cp ${B}/${builddir}/${binary} ${B}/${builddir}/${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX}
}

uboot_compile () {
    oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_OPTS} ${UBOOT_MAKE_TARGET}

    # Generate the uboot-initial-env
    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
        oe_runmake -C ${S} O=${B} u-boot-initial-env
        if [ "${UBOOT_INITIAL_ENV_BINARY}" = "1" ] ; then
            uboot_compile_initial_env_binary
        fi
    fi
}

uboot_compile_initial_env_binary() {
    redund=""
    if [ "${UBOOT_INITIAL_ENV_BINARY_REDUND}" = "1" ] ; then
        redund="-r"
    fi
    uboot-mkenvimage -s ${UBOOT_INITIAL_ENV_BINARY_SIZE} ${redund} -o ${B}/${builddir}/u-boot-initial-env.bin ${B}/${builddir}/u-boot-initial-env
}

do_install () {
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    uboot_install_config $config $type
                fi
            done
            unset j
        done
        unset i
    else
        uboot_install
    fi

    if [ -n "${UBOOT_ELF}" ]
    then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        uboot_install_elf_config $config $type
                    fi
                done
                unset j
            done
            unset i
        else
            uboot_install_elf
        fi
    fi

    if [ -e ${UNPACKDIR}/fw_env.config ] ; then
        install -d ${D}${sysconfdir}
        install -m 644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    fi

    if [ -n "${SPL_BINARY}" ]
    then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        uboot_install_spl_config $config $type
                    fi
                done
                unset j
            done
            unset i
        else
            uboot_install_spl
        fi
    fi

    if [ -n "${UBOOT_ENV}" ]
    then
        install -m 644 ${B}/${UBOOT_ENV_BINARY} ${D}/boot/${UBOOT_ENV_IMAGE}
        ln -sf ${UBOOT_ENV_IMAGE} ${D}/boot/${UBOOT_ENV_BINARY}
    fi

    if [ "${UBOOT_EXTLINUX}" = "1" ]
    then
        install -Dm 0644 ${UBOOT_EXTLINUX_CONFIG} ${D}/${UBOOT_EXTLINUX_INSTALL_DIR}/${UBOOT_EXTLINUX_CONF_NAME}
    fi
}

uboot_install_config () {
    config=$1
    type=$2

    builddir="${config}-${type}"

    install -D -m 644 ${B}/${builddir}/${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARYNAME}-${type}-${UBOOT_VERSION}.${UBOOT_SUFFIX}
    ln -sf ${UBOOT_BINARYNAME}-${type}-${UBOOT_VERSION}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARY}-${type}
    ln -sf ${UBOOT_BINARYNAME}-${type}-${UBOOT_VERSION}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARY}

    # Install the uboot-initial-env
    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
        install -D -m 644 ${B}/${builddir}/u-boot-initial-env-${type} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION}
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${type}
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}
    fi
}

uboot_install () {
    install -D -m 644 ${B}/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
    ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}

    # Install the uboot-initial-env
    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
        install -D -m 644 ${B}/u-boot-initial-env ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}-${UBOOT_VERSION}
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${UBOOT_VERSION} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${UBOOT_VERSION} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}
    fi
}

uboot_install_elf_config () {
    config=$1
    type=$2

    builddir="${config}-${type}"

    install -m 644 ${B}/${builddir}/${UBOOT_ELF} ${D}/boot/u-boot-${type}-${UBOOT_VERSION}.${UBOOT_ELF_SUFFIX}
    ln -sf u-boot-${type}-${UBOOT_VERSION}.${UBOOT_ELF_SUFFIX} ${D}/boot/${UBOOT_BINARY}-${type}
    ln -sf u-boot-${type}-${UBOOT_VERSION}.${UBOOT_ELF_SUFFIX} ${D}/boot/${UBOOT_BINARY}
}

uboot_install_elf () {
    install -m 644 ${B}/${UBOOT_ELF} ${D}/boot/${UBOOT_ELF_IMAGE}
    ln -sf ${UBOOT_ELF_IMAGE} ${D}/boot/${UBOOT_ELF_BINARY}
}

uboot_install_spl_config () {
    config=$1
    type=$2

    builddir="${config}-${type}"

    install -m 644 ${B}/${builddir}/${SPL_BINARY} ${D}/boot/${SPL_BINARYNAME}-${type}-${UBOOT_VERSION}${SPL_DELIMITER}${SPL_SUFFIX}
    ln -sf ${SPL_BINARYNAME}-${type}-${UBOOT_VERSION}${SPL_DELIMITER}${SPL_SUFFIX} ${D}/boot/${SPL_BINARYFILE}-${type}
    ln -sf ${SPL_BINARYNAME}-${type}-${UBOOT_VERSION}${SPL_DELIMITER}${SPL_SUFFIX} ${D}/boot/${SPL_BINARYFILE}
}

uboot_install_spl () {
    install -m 644 ${B}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}
    ln -sf ${SPL_IMAGE} ${D}/boot/${SPL_BINARYFILE}
}

PACKAGE_BEFORE_PN += "${PN}-env ${PN}-extlinux"

RPROVIDES:${PN}-env += "u-boot-default-env"
ALLOW_EMPTY:${PN}-env = "1"
FILES:${PN}-env = " \
    ${@ '${sysconfdir}/${UBOOT_INITIAL_ENV}*' if d.getVar('UBOOT_INITIAL_ENV') else ''} \
    ${sysconfdir}/fw_env.config \
"

FILES:${PN}-extlinux = "${UBOOT_EXTLINUX_INSTALL_DIR}/${UBOOT_EXTLINUX_CONF_NAME}"
RDEPENDS:${PN} += "${@bb.utils.contains('UBOOT_EXTLINUX', '1', '${PN}-extlinux', '', d)}"

SYSROOT_DIRS += "/boot"
FILES:${PN} = "/boot ${datadir}"
RDEPENDS:${PN} += "${PN}-env"

do_deploy () {
    if [ -n "${UBOOT_CONFIG}" ]
    then
        unset i j
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    uboot_deploy_config $config $type
                fi
            done
            unset j
        done
        unset i
    else
        uboot_deploy
    fi

    if [ -e ${UNPACKDIR}/fw_env.config ] ; then
        install -D -m 644 ${UNPACKDIR}/fw_env.config ${DEPLOYDIR}/fw_env.config-${MACHINE}-${UBOOT_VERSION}
        cd ${DEPLOYDIR}
        ln -sf fw_env.config-${MACHINE}-${UBOOT_VERSION} fw_env.config-${MACHINE}
        ln -sf fw_env.config-${MACHINE}-${UBOOT_VERSION} fw_env.config
    fi

    if [ -n "${UBOOT_ELF}" ]
    then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        uboot_deploy_elf_config $config $type
                    fi
                done
                unset j
            done
            unset i
        else
            uboot_deploy_elf
        fi
    fi

     if [ -n "${SPL_BINARY}" ]
     then
        if [ -n "${UBOOT_CONFIG}" ]
        then
            for config in ${UBOOT_MACHINE}; do
                i=$(expr $i + 1);
                for type in ${UBOOT_CONFIG}; do
                    j=$(expr $j + 1);
                    if [ $j -eq $i ]
                    then
                        uboot_deploy_spl_config $config $type
                    fi
                done
                unset j
            done
            unset i
        else
            uboot_deploy_spl
        fi
    fi

    if [ -n "${UBOOT_ENV}" ]
    then
        install -m 644 ${B}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_IMAGE}
        ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_BINARY}
        ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
    fi

    if [ "${UBOOT_EXTLINUX}" = "1" ]
    then
        install -m 644 ${UBOOT_EXTLINUX_CONFIG} ${DEPLOYDIR}/${UBOOT_EXTLINUX_SYMLINK}
        ln -sf ${UBOOT_EXTLINUX_SYMLINK} ${DEPLOYDIR}/${UBOOT_EXTLINUX_CONF_NAME}-${MACHINE}
        ln -sf ${UBOOT_EXTLINUX_SYMLINK} ${DEPLOYDIR}/${UBOOT_EXTLINUX_CONF_NAME}
    fi

    if [ -n "${UBOOT_DTB}" ]
    then
        install -m 644 ${B}/arch/${UBOOT_ARCH_DIR}/dts/${UBOOT_DTB_BINARY} ${DEPLOYDIR}/
    fi
}

uboot_deploy_config () {
    config=$1
    type=$2

    builddir="${config}-${type}"

    install -D -m 644 ${B}/${builddir}/${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX} ${DEPLOYDIR}/${UBOOT_BINARYNAME}-${type}-${UBOOT_VERSION}.${UBOOT_SUFFIX}
    cd ${DEPLOYDIR}
    ln -sf ${UBOOT_BINARYNAME}-${type}-${UBOOT_VERSION}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}-${type}
    ln -sf ${UBOOT_BINARYNAME}-${type}-${UBOOT_VERSION}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_BINARYNAME}-${type}-${UBOOT_VERSION}.${UBOOT_SUFFIX} ${UBOOT_BINARY}-${type}
    ln -sf ${UBOOT_BINARYNAME}-${type}-${UBOOT_VERSION}.${UBOOT_SUFFIX} ${UBOOT_BINARY}

    # Deploy the uboot-initial-env
    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
        install -D -m 644 ${B}/${builddir}/u-boot-initial-env-${type} ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION}
        if [ "${UBOOT_INITIAL_ENV_BINARY}" = "1" ]; then
            install -D -m 644 ${B}/${builddir}/u-boot-initial-env-${type}.bin ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION}.bin
        fi
        cd ${DEPLOYDIR}
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION} ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION} ${UBOOT_INITIAL_ENV}-${type}
        if [ "${UBOOT_INITIAL_ENV_BINARY}" = "1" ]; then
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION}.bin ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}.bin
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${UBOOT_VERSION}.bin ${UBOOT_INITIAL_ENV}-${type}.bin
        fi
    fi
}

uboot_deploy () {
    install -D -m 644 ${B}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}

    cd ${DEPLOYDIR}
    rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
    ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}

    # Deploy the uboot-initial-env
    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
        install -D -m 644 ${B}/u-boot-initial-env ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-${UBOOT_VERSION}
        if [ "${UBOOT_INITIAL_ENV_BINARY}" = "1" ]; then
            install -D -m 644 ${B}/u-boot-initial-env.bin ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-${UBOOT_VERSION}.bin
        fi
        cd ${DEPLOYDIR}
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${UBOOT_VERSION} ${UBOOT_INITIAL_ENV}-${MACHINE}
        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${UBOOT_VERSION} ${UBOOT_INITIAL_ENV}
        if [ "${UBOOT_INITIAL_ENV_BINARY}" = "1" ]; then
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${UBOOT_VERSION}.bin ${UBOOT_INITIAL_ENV}-${MACHINE}.bin
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${UBOOT_VERSION}.bin ${UBOOT_INITIAL_ENV}.bin
        fi
    fi
}

uboot_deploy_elf_config () {
    config=$1
    type=$2

    builddir="${config}-${type}"

    install -m 644 ${B}/${builddir}/${UBOOT_ELF} ${DEPLOYDIR}/u-boot-${type}-${UBOOT_VERSION}.${UBOOT_ELF_SUFFIX}
    ln -sf u-boot-${type}-${UBOOT_VERSION}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}-${type}
    ln -sf u-boot-${type}-${UBOOT_VERSION}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
    ln -sf u-boot-${type}-${UBOOT_VERSION}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}-${type}
    ln -sf u-boot-${type}-${UBOOT_VERSION}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
}

uboot_deploy_elf () {
    install -m 644 ${B}/${UBOOT_ELF} ${DEPLOYDIR}/${UBOOT_ELF_IMAGE}
    ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
    ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
}

uboot_deploy_spl_config () {
    config=$1
    type=$2

    builddir="${config}-${type}"

    install -m 644 ${B}/${builddir}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}-${type}-${UBOOT_VERSION}${SPL_DELIMITER}${SPL_SUFFIX}
    rm -f ${DEPLOYDIR}/${SPL_BINARYFILE} ${DEPLOYDIR}/${SPL_SYMLINK}
    ln -sf ${SPL_BINARYNAME}-${type}-${UBOOT_VERSION}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_BINARYFILE}-${type}
    ln -sf ${SPL_BINARYNAME}-${type}-${UBOOT_VERSION}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_BINARYFILE}
    ln -sf ${SPL_BINARYNAME}-${type}-${UBOOT_VERSION}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_SYMLINK}-${type}
    ln -sf ${SPL_BINARYNAME}-${type}-${UBOOT_VERSION}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_SYMLINK}
}

uboot_deploy_spl () {
    install -m 644 ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
    ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_BINARYNAME}
    ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_SYMLINK}
}

addtask deploy before do_build after do_compile
