SUMMARY = "Rename the network device name"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

GBMC_ETHER_MAP ?= ""

inherit systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

FILES:${PN} += "${systemd_unitdir}"

do_install() {
  netdir=${D}${systemd_unitdir}/network
  install -d -m0755 $netdir

  # install dev renaming files if any
  if [ -z "${GBMC_ETHER_MAP}"]; then
    return
  fi
  devmap="${GBMC_ETHER_MAP}"
  for str in $devmap
  do
    devaddr="$(echo "${str}" | cut -d'|' -f1)"
    devname="$(echo "${str}" | cut -d'|' -f2)"
    echo "[Match]" > ${UNPACKDIR}/30-netdev-${devname}.link
    echo "Path=*-${devaddr}" >> ${UNPACKDIR}/30-netdev-${devname}.link
    echo "[Link]"  >> ${UNPACKDIR}/30-netdev-${devname}.link
    echo "Name=${devname}" >> ${UNPACKDIR}/30-netdev-${devname}.link
    install -m0644 ${UNPACKDIR}/30-netdev-${devname}.link ${netdir}
  done
}

