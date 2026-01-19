FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://fw_env.config"

do_install:append() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    # Create a symbolic link for those are using Aspeed u-boot-fw-utils
    install -d ${D}${base_sbindir}
    ln -s ${bindir}/fw_setenv ${D}${base_sbindir}/fw_setenv
}

FILES:${PN} += "${sysconfdir}/fw_env.config \
                ${base_sbindir}/fw_setenv \
                "
