SUMMARY = "Universal Boot Loader for embedded devices"
PROVIDES = "virtual/bootloader"

B = "${WORKDIR}/build"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "kern-tools-native"

inherit uboot-config uboot-extlinux-config uboot-sign deploy cml1 python3native

DEPENDS += "swig-native"

EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" V=1'
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

# returns all the elements from the src uri that are .cfg files
def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.cfg'):
            sources_list.append(s)

    return sources_list

do_configure () {
    if [ -z "${UBOOT_CONFIG}" ]; then
        if [ -n "${UBOOT_MACHINE}" ]; then
            oe_runmake -C ${S} O=${B} ${UBOOT_MACHINE}
        else
            oe_runmake -C ${S} O=${B} oldconfig
        fi
        merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
        cml1_do_configure
    fi
}

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

    if [ -n "${UBOOT_CONFIG}" ]
    then
        unset i j k
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    oe_runmake -C ${S} O=${B}/${config} ${config}
                    oe_runmake -C ${S} O=${B}/${config} ${UBOOT_MAKE_TARGET}
                    for binary in ${UBOOT_BINARIES}; do
                        k=$(expr $k + 1);
                        if [ $k -eq $i ]; then
                            cp ${B}/${config}/${binary} ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}
                        fi
                    done
                    unset k
                fi
            done
            unset  j
        done
        unset  i
    else
        oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_TARGET}
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
                    install -d ${D}/boot
                    install -m 644 ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX} ${D}/boot/u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARY}-${type}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${D}/boot/${UBOOT_BINARY}
                fi
            done
            unset  j
        done
        unset  i
    else
        install -d ${D}/boot
        install -m 644 ${B}/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
        ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}
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
                         install -m 644 ${B}/${config}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}-${type}-${PV}-${PR}
                         ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${D}/boot/${SPL_BINARYNAME}-${type}
                         ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${D}/boot/${SPL_BINARYNAME}
                    fi
                done
                unset  j
            done
            unset  i
        else
            install -m 644 ${B}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}
            ln -sf ${SPL_IMAGE} ${D}/boot/${SPL_BINARYNAME}
        fi
    fi

    if [ -n "${UBOOT_ENV}" ]
    then
        install -m 644 ${UNPACKDIR}/${UBOOT_ENV_BINARY} ${D}/boot/${UBOOT_ENV_IMAGE}
        ln -sf ${UBOOT_ENV_IMAGE} ${D}/boot/${UBOOT_ENV_BINARY}
    fi

    if [ "${UBOOT_EXTLINUX}" = "1" ]
    then
        install -Dm 0644 ${UBOOT_EXTLINUX_CONFIG} ${D}/${UBOOT_EXTLINUX_INSTALL_DIR}/${UBOOT_EXTLINUX_CONF_NAME}
    fi

}

PACKAGE_BEFORE_PN += "${PN}-env"

RPROVIDES:${PN}-env += "u-boot-default-env"
FILES:${PN} = "/boot ${sysconfdir} ${datadir}"
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
                    install -d ${DEPLOYDIR}
                    install -m 644 ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX} ${DEPLOYDIR}/u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
                    cd ${DEPLOYDIR}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}-${type}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}-${type}
                    ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}
                fi
            done
            unset  j
        done
        unset  i
    else
        install -d ${DEPLOYDIR}
        install -m 644 ${B}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}
        cd ${DEPLOYDIR}
        rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
        ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}
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
                        install -m 644 ${B}/${config}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}-${type}-${PV}-${PR}
                        rm -f ${DEPLOYDIR}/${SPL_BINARYNAME} ${DEPLOYDIR}/${SPL_SYMLINK}-${type}
                        ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_BINARYNAME}-${type}
                        ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_BINARYNAME}
                        ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_SYMLINK}-${type}
                        ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_SYMLINK}
                    fi
                done
                unset  j
            done
            unset  i
        else
            install -m 644 ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
            rm -f ${DEPLOYDIR}/${SPL_BINARYNAME} ${DEPLOYDIR}/${SPL_SYMLINK}
            ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_BINARYNAME}
            ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_SYMLINK}
        fi
    fi


    if [ -n "${UBOOT_ENV}" ]
    then
        install -m 644 ${UNPACKDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_IMAGE}
        rm -f ${DEPLOYDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
        ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_BINARY}
        ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
    fi

    if [ "${UBOOT_EXTLINUX}" = "1" ]
    then
        install -m 644 ${UBOOT_EXTLINUX_CONFIG} ${DEPLOYDIR}/${UBOOT_EXTLINUX_SYMLINK}
        ln -sf ${UBOOT_EXTLINUX_SYMLINK} ${DEPLOYDIR}/${UBOOT_EXTLINUX_CONF_NAME}-${MACHINE}
        ln -sf ${UBOOT_EXTLINUX_SYMLINK} ${DEPLOYDIR}/${UBOOT_EXTLINUX_CONF_NAME}
    fi
}

addtask deploy before do_build after do_compile
