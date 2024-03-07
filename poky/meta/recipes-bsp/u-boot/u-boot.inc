SUMMARY = "Universal Boot Loader for embedded devices"
PROVIDES = "virtual/bootloader"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "${@bb.utils.contains('UBOOT_ENV_SUFFIX', 'scr', 'u-boot-mkimage-native', '', d)}"

inherit uboot-config uboot-extlinux-config uboot-sign deploy python3native kernel-arch

DEPENDS += "swig-native"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS} ${DEBUG_PREFIX_MAP}" V=1'
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'
EXTRA_OEMAKE += 'STAGING_INCDIR=${STAGING_INCDIR_NATIVE} STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE}'

PACKAGECONFIG ??= "openssl"
# u-boot will compile its own tools during the build, with specific
# configurations (aka when CONFIG_FIT_SIGNATURE is enabled) openssl is needed as
# a host build dependency.
PACKAGECONFIG[openssl] = ",,openssl-native"

# Allow setting an additional version string that will be picked up by the
# u-boot build system and appended to the u-boot version.  If the .scmversion
# file already exists it will not be overwritten.
UBOOT_LOCALVERSION ?= ""

# Default name of u-boot initial env, but enable individual recipes to change
# this value.
UBOOT_INITIAL_ENV ?= "${PN}-initial-env"

require u-boot-configure.inc

do_savedefconfig() {
	bbplain "Saving defconfig to:\n${B}/defconfig"
	oe_runmake -C ${B} savedefconfig
}
do_savedefconfig[nostamp] = "1"
addtask savedefconfig after do_configure
UBOOT_ARCH_DIR = "${@'arm' if d.getVar('UBOOT_ARCH').startswith('arm') else d.getVar('UBOOT_ARCH')}"
do_compile () {
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'ld-is-gold', d)}" ]; then
        sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' ${S}/config.mk
    fi

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
        unset i j k
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    oe_runmake -C ${S} O=${B}/${config} ${UBOOT_MAKE_TARGET}
                    for binary in ${UBOOT_BINARIES}; do
                        k=$(expr $k + 1);
                        if [ $k -eq $i ]; then
                            cp ${B}/${config}/${binary} ${B}/${config}/${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX}
                        fi
                    done

                    # Generate the uboot-initial-env
                    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
                        oe_runmake -C ${S} O=${B}/${config} u-boot-initial-env
                        cp ${B}/${config}/u-boot-initial-env ${B}/${config}/u-boot-initial-env-${type}
                    fi

                    unset k
                fi
            done
            unset j
        done
        unset i
    else
        oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_TARGET}

        # Generate the uboot-initial-env
        if [ -n "${UBOOT_INITIAL_ENV}" ]; then
            oe_runmake -C ${S} O=${B} u-boot-initial-env
        fi
    fi

    if [ -n "${UBOOT_ENV}" ] && [ "${UBOOT_ENV_SUFFIX}" = "scr" ]
    then
        ${UBOOT_MKIMAGE} -C none -A ${UBOOT_ARCH} -T script -d ${WORKDIR}/${UBOOT_ENV_SRC} ${WORKDIR}/${UBOOT_ENV_BINARY}
    fi
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
                    install -D -m 644 ${B}/${config}/${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARY}-${type}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARY}

                    # Install the uboot-initial-env
                    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
                        install -D -m 644 ${B}/${config}/u-boot-initial-env-${type} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${type}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}
                    fi
                fi
            done
            unset j
        done
        unset i
    else
        install -D -m 644 ${B}/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
        ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}

        # Install the uboot-initial-env
        if [ -n "${UBOOT_INITIAL_ENV}" ]; then
            install -D -m 644 ${B}/u-boot-initial-env ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR}
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}-${MACHINE}
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR} ${D}/${sysconfdir}/${UBOOT_INITIAL_ENV}
        fi
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
                        install -m 644 ${B}/${config}/${UBOOT_ELF} ${D}/boot/u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${D}/boot/${UBOOT_BINARY}-${type}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${D}/boot/${UBOOT_BINARY}
                    fi
                done
                unset j
            done
            unset i
        else
            install -m 644 ${B}/${UBOOT_ELF} ${D}/boot/${UBOOT_ELF_IMAGE}
            ln -sf ${UBOOT_ELF_IMAGE} ${D}/boot/${UBOOT_ELF_BINARY}
        fi
    fi

    if [ -e ${WORKDIR}/fw_env.config ] ; then
        install -d ${D}${sysconfdir}
        install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
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
                         install -m 644 ${B}/${config}/${SPL_BINARY} ${D}/boot/${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX}
                         ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${D}/boot/${SPL_BINARYFILE}-${type}
                         ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${D}/boot/${SPL_BINARYFILE}
                    fi
                done
                unset j
            done
            unset i
        else
            install -m 644 ${B}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}
            ln -sf ${SPL_IMAGE} ${D}/boot/${SPL_BINARYFILE}
        fi
    fi

    if [ -n "${UBOOT_ENV}" ]
    then
        install -m 644 ${WORKDIR}/${UBOOT_ENV_BINARY} ${D}/boot/${UBOOT_ENV_IMAGE}
        ln -sf ${UBOOT_ENV_IMAGE} ${D}/boot/${UBOOT_ENV_BINARY}
    fi

    if [ "${UBOOT_EXTLINUX}" = "1" ]
    then
        install -Dm 0644 ${UBOOT_EXTLINUX_CONFIG} ${D}/${UBOOT_EXTLINUX_INSTALL_DIR}/${UBOOT_EXTLINUX_CONF_NAME}
    fi
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
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    install -D -m 644 ${B}/${config}/${UBOOT_BINARYNAME}-${type}.${UBOOT_SUFFIX} ${DEPLOYDIR}/${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
                    cd ${DEPLOYDIR}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}-${type}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}-${type}
                    ln -sf ${UBOOT_BINARYNAME}-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}

                    # Deploy the uboot-initial-env
                    if [ -n "${UBOOT_INITIAL_ENV}" ]; then
                        install -D -m 644 ${B}/${config}/u-boot-initial-env-${type} ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR}
                        cd ${DEPLOYDIR}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}
                        ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${type}-${PV}-${PR} ${UBOOT_INITIAL_ENV}-${type}
                    fi
                fi
            done
            unset j
        done
        unset i
    else
        install -D -m 644 ${B}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}

        cd ${DEPLOYDIR}
        rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}

        # Deploy the uboot-initial-env
        if [ -n "${UBOOT_INITIAL_ENV}" ]; then
            install -D -m 644 ${B}/u-boot-initial-env ${DEPLOYDIR}/${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR}
            cd ${DEPLOYDIR}
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR} ${UBOOT_INITIAL_ENV}-${MACHINE}
            ln -sf ${UBOOT_INITIAL_ENV}-${MACHINE}-${PV}-${PR} ${UBOOT_INITIAL_ENV}
        fi
    fi

    if [ -e ${WORKDIR}/fw_env.config ] ; then
        install -D -m 644 ${WORKDIR}/fw_env.config ${DEPLOYDIR}/fw_env.config-${MACHINE}-${PV}-${PR}
        cd ${DEPLOYDIR}
        ln -sf fw_env.config-${MACHINE}-${PV}-${PR} fw_env.config-${MACHINE}
        ln -sf fw_env.config-${MACHINE}-${PV}-${PR} fw_env.config
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
                        install -m 644 ${B}/${config}/${UBOOT_ELF} ${DEPLOYDIR}/u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}-${type}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}-${type}
                        ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
                    fi
                done
                unset j
            done
            unset i
        else
            install -m 644 ${B}/${UBOOT_ELF} ${DEPLOYDIR}/${UBOOT_ELF_IMAGE}
            ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
            ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
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
                        install -m 644 ${B}/${config}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX}
                        rm -f ${DEPLOYDIR}/${SPL_BINARYFILE} ${DEPLOYDIR}/${SPL_SYMLINK}
                        ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_BINARYFILE}-${type}
                        ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_BINARYFILE}
                        ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_SYMLINK}-${type}
                        ln -sf ${SPL_BINARYNAME}-${type}-${PV}-${PR}${SPL_DELIMITER}${SPL_SUFFIX} ${DEPLOYDIR}/${SPL_SYMLINK}
                    fi
                done
                unset j
            done
            unset i
        else
            install -m 644 ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
            ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_BINARYNAME}
            ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_SYMLINK}
        fi
    fi

    if [ -n "${UBOOT_ENV}" ]
    then
        install -m 644 ${WORKDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_IMAGE}
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

addtask deploy before do_build after do_compile
