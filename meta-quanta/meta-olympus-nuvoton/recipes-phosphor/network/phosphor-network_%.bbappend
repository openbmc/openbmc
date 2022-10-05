FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

DEPENDS:append:olympus-nuvoton = " nlohmann-json boost"

PACKAGECONFIG:remove:olympus-nuvoton = "default-link-local-autoconf"
PACKAGECONFIG:remove:olympus-nuvoton = "default-ipv6-accept-ra"

SRC_URI:append:olympus-nuvoton = " file://0001-Run-after-xyz-openbmc_project-user-path-created.patch"
#SRC_URI:append:olympus-nuvoton = " file://0002-Adding-channel-specific-privilege-to-network.patch"
