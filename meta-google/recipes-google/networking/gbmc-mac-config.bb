SUMMARY = "Configures MAC addresses on a gBMC system"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += " \
  file://gbmc-mac-config.service \
  file://gbmc-mac-config.sh.in \
  "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += " \
  bash \
  ipmi-fru-sh \
  "

FILES:${PN} += "${systemd_unitdir}"

SYSTEMD_SERVICE:${PN} += "gbmc-mac-config.service"

GBMC_MAC_EEPROM_OF_NAME ?= ""

# Maps the MAC address offset from the base address to an interface name
# in bash associative array syntax.
#   Ex. "[0]=eth0 [2]=eth2"
GBMC_MAC_IF_MAP ?= ""

do_install:append() {
  if [ -z '${GBMC_MAC_EEPROM_OF_NAME}' ]; then
    echo 'Missing GBMC_MAC_EEPROM_OF_NAME' >&2
    exit 1
  fi

  # Build time dictionary sanity check
  bash -c "declare -A dict=(${GBMC_MAC_IF_MAP})"

  sed gbmc-mac-config.sh.in \
    -e 's#@EEPROM@#${GBMC_MAC_EEPROM_OF_NAME}#' \
    -e "s#@NUM_TO_INTFS@#${GBMC_MAC_IF_MAP}#" \
    >gbmc-mac-config.sh

  install -d -m0755 ${D}${libexecdir}
  install -m0755 gbmc-mac-config.sh ${D}${libexecdir}/

  install -d -m0755 ${D}${systemd_system_unitdir}
  install -m0644 gbmc-mac-config.service ${D}${systemd_system_unitdir}/
}
