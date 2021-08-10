FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Replace the default whitelist for GSJ board.
SRC_URI:append:gsj = " file://gsj-ipmid-whitelist.conf"

WHITELIST_CONF:remove:gsj = " ${S}/host-ipmid-whitelist.conf"
WHITELIST_CONF:append:gsj = " ${WORKDIR}/gsj-ipmid-whitelist.conf"
