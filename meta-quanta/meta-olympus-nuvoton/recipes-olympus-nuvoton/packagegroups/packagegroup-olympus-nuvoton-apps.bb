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

RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-mgmt"
RPROVIDES_${PN}-fans += "virtual-obmc-fan-mgmt"
RPROVIDES_${PN}-flash += "virtual-obmc-flash-mgmt"
RPROVIDES_${PN}-system += "virtual-obmc-system-mgmt"

SUMMARY_${PN}-fans = "OLYMPUS NUVOTON Fans"
RDEPENDS_${PN}-fans = " \
        phosphor-pid-control \
        "

SUMMARY_${PN}-system = "OLYMPUS NUVOTON System"
RDEPENDS_${PN}-system = " \
        phosphor-webui \
        obmc-ikvm \
        obmc-console \
        dhcpcd \
        phosphor-ipmi-fru \
        phosphor-ipmi-ipmb \
        ipmitool \
        first-boot-set-psu \
        phosphor-image-signing \
        openssl-bin \
        loadsvf \
        iptables \
        olympus-nuvoton-iptable-save \
        phosphor-post-code-manager \
        adm1278-hotswap-power-cycle \
        google-ipmi-sys \
        mac-address \
        "
