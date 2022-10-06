FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " file://0001-suuport-sync-mac-from-eeprom.patch"
SRC_URI:append:scm-npcm845 = " file://config.json"

PACKAGECONFIG:append:scm-npcm845 = " nic-ethtool"
PACKAGECONFIG:append:scm-npcm845 = " sync-mac-eeprom"
PACKAGECONFIG:remove:scm-npcm845 = "default-link-local-autoconf"
DEPENDS:append:scm-npcm845 = " nlohmann-json"
PACKAGECONFIG[sync-mac-eeprom] = "-Dsync-mac-eeprom=true,-Dsync-mac-eeprom=false,,"

FILES:${PN}:append:scm-npcm845 = " ${datadir}/network/config.json"
do_install:append:scm-npcm845() {
    install -m 0644 -D ${WORKDIR}/config.json \
        ${D}${datadir}/network/config.json
}
