inherit entity-utils
SUMMARY = "OpenBMC for OLYMPUS NUVOTON system - Applications"
PR = "r1"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " \
        ${PN}-chassis \
        ${PN}-fans \
        ${PN}-flash \
        ${PN}-system \
        "

PROVIDES += "virtual/obmc-chassis-mgmt"
PROVIDES += "virtual/obmc-fan-mgmt"
PROVIDES += "virtual/obmc-flash-mgmt"
PROVIDES += "virtual/obmc-system-mgmt"

RPROVIDES:${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES:${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES:${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES:${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY:${PN}-chassis = "OLYMPUS NUVOTON Chassis"
RDEPENDS:${PN}-chassis = " \
        x86-power-control \
        "

SUMMARY:${PN}-fans = "OLYMPUS NUVOTON Fans"
RDEPENDS:${PN}-fans = " \
        phosphor-pid-control \
        "

SUMMARY:${PN}-flash = "OLYMPUS NUVOTON Flash"
RDEPENDS:${PN}-flash = " \
        phosphor-ipmi-flash \
        ipmi-bios-update \
        ipmi-bmc-update \
        "

SUMMARY:${PN}-system = "OLYMPUS NUVOTON System"
RDEPENDS:${PN}-system = " \
        webui-vue \
        obmc-ikvm \
        obmc-console \
        phosphor-host-postd \
        phosphor-ipmi-ipmb \
        phosphor-ipmi-blobs \
        ipmitool \
        phosphor-sel-logger \
        phosphor-node-manager-proxy \
        phosphor-image-signing \
        openssl-bin \
        openssl-engines \
        loadsvf \
        asd \
        iptables \
        bmc-time-sync \
        phosphor-post-code-manager \
        adm1278-hotswap-power-cycle \
        google-ipmi-sys \
        mac-address \
        smbios-mdr \
        loadmcu \
        usb-network \
	nuvoton-ipmi-oem \
        olympus-nuvoton-iptable-restore \
        srvcfg-manager \
        iperf3 \
        "
RDEPENDS:${PN}-system:append = " \
        ${@entity_enabled(d, '', 'first-boot-set-psu')} \
        "

#RDEPENDS:${PN}-system:append:olympus-entity = " \
#        intel-ipmi-oem \
#        "
