FILESEXTRAPATHS:prepend:buv-runbmc := "${THISDIR}/${PN}:"

#SRC_URI:append:buv-runbmc = " file://0028-MCTP-Daemon-D-Bus-interface-definition.patch"
SRC_URI:append:buv-runbmc = " file://0001-Software-Add-MCU-VersionPurpose.patch"
