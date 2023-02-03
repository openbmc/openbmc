FILESEXTRAPATHS:append:scm-npcm845 := "${THISDIR}/${PN}:"
SRC_URI:append:scm-npcm845 = " file://0001-Revert-Remove-HMAC-SHA1-from-Authentication-Integrit.patch"
SRC_URI:append:scm-npcm845 = " file://0002-Add-RemoteIPAddr-support.patch"
SRC_URI:append:scm-npcm845 = " file://0003-add-server-type-and-oem-id-to-meet-MS-spec.patch"
SRC_URI:append:scm-npcm845 = " file://0004-set-channel-security-keys.patch"

RMCPP_IFACE = "end0"
