# This provides the logic for creating the desired u-boot config,
# accounting for any *.cfg files added to SRC_URI.  It's separated
# from u-boot.inc for use by recipes that need u-boot properly
# configured but aren't doing a full build of u-boot itself (such as
# its companion tools).

inherit uboot-config cml1

DEPENDS += "kern-tools-native"

CONFIGURE_FILES = "${@d.getVar('UBOOT_MACHINE') or '.config'}"

do_configure () {
    if [ -n "${UBOOT_CONFIG}" ]; then
        unset i j
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]; then
                    uboot_configure_config $config $type
                fi
            done
            unset j
        done
        unset i
    else
        uboot_configure
    fi
}

uboot_configure_config () {
    config=$1
    type=$2

    builddir="${config}-${type}"

    config_make_opts=$(uboot_config_get_indexed_value "${UBOOT_CONFIG_MAKE_OPTS}" $i)
    config_fragments=$(uboot_config_get_indexed_value "${UBOOT_CONFIG_FRAGMENTS}" $i)

    oe_runmake -C ${S} O=${B}/${builddir} ${config_make_opts} ${UBOOT_MAKE_OPTS} ${config} ${config_fragments} ${UBOOT_FRAGMENTS}
    if [ -n "${@' '.join(find_cfgs(d))}" ]; then
        merge_config.sh -m -O ${B}/${builddir} ${B}/${builddir}/.config ${@" ".join(find_cfgs(d))}
        oe_runmake -C ${S} O=${B}/${builddir} oldconfig
    fi
}

uboot_configure () {
    if [ -n "${UBOOT_MACHINE}" ]; then
        oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_OPTS} ${UBOOT_MACHINE} ${UBOOT_FRAGMENTS}
    else
        oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_OPTS} oldconfig
    fi
    merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
    cml1_do_configure
}

