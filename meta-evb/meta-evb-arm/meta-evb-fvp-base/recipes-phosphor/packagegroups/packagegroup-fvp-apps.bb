SUMMARY = "OpenBMC for FVP - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-flash \
        ${PN}-system \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"
PROVIDES += "virtual/obmc-user-mgmt"

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"
RPROVIDES:${PN}-user += "virtual-user-system-mgmt"

SUMMARY:${PN}-chassis = "FVP Chassis"
RDEPENDS:${PN}-chassis = " \
        "

SUMMARY:${PN}-user = "FVP User"
RDEPENDS:${PN}-user = " \
        "

SUMMARY:${PN}-flash = "FVP Flash"
RDEPENDS:${PN}-flash = " \
        "

SUMMARY:${PN}-system = "FVP System"
RDEPENDS:${PN}-system = " \
        bmcweb \
        webui-vue \
        phosphor-host-postd \
        phosphor-post-code-manager \
        smbios-mdr \
        phosphor-ipmi-blobs \
        "
