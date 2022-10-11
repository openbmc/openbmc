FILESEXTRAPATHS:prepend:scm-npcm845 := "${THISDIR}/${PN}:"

SRC_URI:append:scm-npcm845 = " \
    file://0024-Add-the-pre-timeout-interrupt-defined-in-IPMI-spec.patch \
    file://0025-Add-PreInterruptFlag-properity-in-DBUS.patch \
"
