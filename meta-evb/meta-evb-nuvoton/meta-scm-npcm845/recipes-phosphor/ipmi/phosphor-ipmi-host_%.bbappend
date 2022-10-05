FILESEXTRAPATHS:append:scm-npcm845 := "${THISDIR}/${PN}:"

EXTRA_OECONF:append:scm-npcm845 = " --disable-i2c-whitelist-check"
EXTRA_OECONF:append:scm-npcm845 = " --enable-sel_logger_clears_sel"

# Add send/get message support
# ipmid <-> ipmb <-> i2c
# SRC_URI:append:scm-npcm845 = " file://0002-Support-bridging-commands.patch"

PACKAGECONFIG:append:scm-npcm845 = " dynamic-sensors"

# avoid build error after remove ipmi-fru
WHITELIST_CONF:scm-npcm845 = "${S}/host-ipmid-whitelist.conf"
