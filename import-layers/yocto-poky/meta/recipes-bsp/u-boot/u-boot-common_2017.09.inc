HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "c98ac3487e413c71e5d36322ef3324b21c6f60f9"

SRC_URI = "git://git.denx.de/u-boot.git \
    file://MPC8315ERDB-enable-DHCP.patch \
    file://10m50-update-device-tree.patch \
"

S = "${WORKDIR}/git"
