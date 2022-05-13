FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://0001-set-watchdog-Interval-value-to-three-minutes.patch"
SRC_URI:append:olympus-nuvoton = " file://0001-support-type-uint8-uint16-uint64-for-inventory-manag.patch"
SRC_URI:append:olympus-nuvoton = " file://0001-Software-Add-MCU-VersionPurpose.patch"

# from Intel repo
SRC_URI:append:olympus-nuvoton = " file://0024-Add-the-pre-timeout-interrupt-defined-in-IPMI-spec.patch"
SRC_URI:append:olympus-nuvoton = " file://0025-Add-PreInterruptFlag-properity-in-DBUS.patch"
SRC_URI:append:olympus-nuvoton = " file://0028-MCTP-Daemon-D-Bus-interface-definition.patch"
SRC_URI:append:olympus-nuvoton = " file://0032-update-meson-build-for-MCTP-interfaces.patch"
