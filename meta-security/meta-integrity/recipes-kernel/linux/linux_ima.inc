FILESEXTRAPATHS:append := "${THISDIR}/linux:"

SRC_URI += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'file://ima.scc', '', d)} \
"

do_configure:append() {
    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'yes', '', d)}" = "yes" ] && [ -f .config ] ; then
        sed -i "s|^CONFIG_SYSTEM_TRUSTED_KEYS=.*|CONFIG_SYSTEM_TRUSTED_KEYS=\"${IMA_EVM_ROOT_CA}\"|" .config
    fi
}

KERNEL_FEATURES:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'modsign', ' features/ima/modsign.scc', '', d)}"

inherit ${@bb.utils.contains('DISTRO_FEATURES', 'modsign', 'kernel-modsign', '', d)}
