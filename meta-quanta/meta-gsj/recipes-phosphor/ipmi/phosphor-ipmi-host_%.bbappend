FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# Replace the default whitelist for GSJ board.
SRC_URI_append_gsj = " file://gsj-ipmid-whitelist.conf"

WHITELIST_CONF_remove_gsj = " ${S}/host-ipmid-whitelist.conf"
WHITELIST_CONF_append_gsj = " ${WORKDIR}/gsj-ipmid-whitelist.conf"
