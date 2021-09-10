SUMMARY = "Command-line interface"
DESCRIPTION = "YADRO OpenBMC Command Line Interface for end users"
HOMEPAGE = "https://github.com/YADRO-KNS/obmc-yadro-cli"
PR = "r1"
PV = "1.0+git${SRCPV}"

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

# Dependencies
DEPENDS = "phosphor-user-manager"
RDEPENDS:${PN} = " \
    ${VIRTUAL-RUNTIME_base-utils} \
    bash \
    obmc-yadro-fwupdate \
    obmc-yadro-lssensors \
    obmc-yadro-netconfig \
    phosphor-debug-collector-dreport \
    phosphor-debug-collector-scripts \
    sudo \
    systemd \
"
# Some platforms also require some additional packages like
#   ipmitool, obmc-yadro-lsinventory, obmc-yadro-backup ...
# They should be appended by bbappend file in their layers

# Directory with command handlers
FILES:${PN} += "${datadir}/cli"

MACHINE_NAME ?= "${MACHINE}"
# Custom installation procedure
do_install() {
  ${B}/install.sh \
    --dir ${D} \
    --machine ${@'${MACHINE_NAME}'.split('-')[0]} \
    --admin priv-admin \
    --operator priv-operator \
    --user priv-user
}

# Source code repository
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/YADRO-KNS/obmc-yadro-cli"
SRCREV = "76743c3706516bae6b2bb8749706b4ea673e9358"
