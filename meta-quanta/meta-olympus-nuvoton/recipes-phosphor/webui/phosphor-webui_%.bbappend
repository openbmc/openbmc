FILESEXTRAPATHS:append:olympus-nuvoton := ":${THISDIR}/${PN}"

SRC_URI:append:olympus-nuvoton = " \
    file://0001-Fixed-cannot-show-warning-high-and-critical-high.patch \
    file://0002-Modify-bios-update-confirm-dialogue.patch \
    file://0003-disable_show_delete_while_activating.patch \
    file://0006-server-control-support-bmc-last-reboot-cause-informa.patch \
"
