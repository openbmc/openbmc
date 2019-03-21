FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Replace the default whitelist for GSJ board.
SRC_URI += " file://gsj-ipmid-whitelist.conf"

WHITELIST_CONF_remove = " ${S}/host-ipmid-whitelist.conf"
WHITELIST_CONF_append = " ${WORKDIR}/gsj-ipmid-whitelist.conf"
