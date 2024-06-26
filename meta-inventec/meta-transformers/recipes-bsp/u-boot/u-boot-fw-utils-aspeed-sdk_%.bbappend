FILESEXTRAPATHS:append := "${THISDIR}/${BPN}:"
# In order to reuse and easily maintain, we use the same patch files among u-boot-aspeed-sdk
FILESEXTRAPATHS:append := "${THISDIR}/u-boot-aspeed-sdk:"


SRC_URI:append = " file://fw_env.config \
                   file://transformers-ast2600.cfg \
                 "

do_install:append () {
        install -d ${D}${sysconfdir}
        install -m 0644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
        install -m 0644 ${UNPACKDIR}/fw_env.config ${S}/tools/env/fw_env.config
}

