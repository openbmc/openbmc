FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# The 32MB NOR, 64MB and 128MB NOR layouts use the same configuration
SRC_URI:append = " \
    file://fw_env_flash_nor.config \
    file://fw_env_ast2700_nor.config \
    file://fw_env_ast2600_mmc.config \
    file://fw_env_ast2700_ufs.config \
"

ENV_CONFIG_FILE = "fw_env_flash_nor.config"
ENV_CONFIG_FILE:aspeed-g7 = "fw_env_ast2700_nor.config"

ENV_CONFIG_FILE:df-phosphor-mmc = "fw_env_ast2600_mmc.config"
ENV_CONFIG_FILE:df-phosphor-mmc:aspeed-g7 = "${@bb.utils.contains('MACHINE_FEATURES', 'mf-ufs', \
                'fw_env_ast2700_ufs.config', 'fw_env_ast2600_mmc.config', d)}"

do_install:append () {
    install -d ${D}${sysconfdir}
    install -m 644 ${UNPACKDIR}/${ENV_CONFIG_FILE} ${D}${sysconfdir}/fw_env.config

    # Compatibility links: meta-phosphor hardcodes /sbin/fw_printenv and
    # /sbin/fw_setenv (clear-once.service, phosphor-static-norootfs-init),
    # matching the historical u-boot-fw-utils install path. .
    install -d ${D}${base_sbindir}
    ln -sr ${D}${bindir}/fw_printenv ${D}${base_sbindir}/fw_printenv
    ln -sr ${D}${bindir}/fw_setenv ${D}${base_sbindir}/fw_setenv
}

FILES:${PN}-bin:append = " \
    ${base_sbindir}/fw_printenv \
    ${base_sbindir}/fw_setenv \
"

# Install it with the tools so they can fall back when flash has no valid env.
RDEPENDS:${PN}-bin:append:aspeed-g7 = " u-boot-aspeed-env"

# Pull in the tools and environment config together
RDEPENDS:${PN}:append = " ${PN}-bin"

# fw_env.config is machine specific
PACKAGE_ARCH = "${MACHINE_ARCH}"
