SUMMARY = "Configures the gbmc bridge and filter rules"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
  file://-bmc-gbmcbr.netdev \
  file://-bmc-gbmcbr.network.in \
  file://-bmc-gbmcbrdummy.netdev \
  file://-bmc-gbmcbrdummy.network \
  file://+-bmc-gbmcbrusb.network \
  file://50-gbmc-br.rules \
  file://gbmc-br-ula.sh \
  file://gbmc-br-from-ra.sh \
  file://gbmc-br-ensure-ra.sh \
  file://gbmc-br-ensure-ra.service \
  file://gbmc-br-hostname.sh \
  file://gbmc-br-hostname.service \
  file://gbmc-ip-from-ra.sh \
  file://gbmc-br-ip-from-ra.sh.in \
  file://gbmc-br-ip-from-ra.service \
  file://gbmc-br-gw-src.sh \
  file://gbmc-br-nft.sh \
  file://gbmc-br-dhcp.sh \
  file://50-gbmc-psu-hardreset.sh.in \
  file://gbmc-br-dhcp.service \
  file://gbmc-br-dhcp-term.sh \
  file://gbmc-br-dhcp-term.service \
  file://gbmc-br-lib.sh \
  file://gbmc-br-load-ip.service \
  file://gbmc-start-dhcp.sh \
  file://50-gbmc-br-cn-redirect.rules \
  "

FILES:${PN}:append = " \
  ${datadir}/gbmc-ip-monitor \
  ${datadir}/gbmc-br-dhcp \
  ${datadir}/gbmc-br-lib.sh \
  ${datadir}/gbmc-ip-from-ra.sh \
  ${systemd_unitdir}/network \
  ${sysconfdir}/nftables \
  "

RDEPENDS:${PN}:append = " \
  bash \
  dhcp-done \
  gbmc-ip-monitor \
  network-sh \
  ndisc6-rdisc6 \
  nftables-systemd \
  "

SYSTEMD_SERVICE:${PN} += " \
  gbmc-br-ensure-ra.service \
  gbmc-br-hostname.service \
  gbmc-br-dhcp.service \
  gbmc-br-dhcp-term.service \
  gbmc-br-load-ip.service \
  ${@"gbmc-br-ip-from-ra.service" if d.getVar('GBMC_BR_FIXED_OFFSET') != "" else ""} \
  "

GBMC_BR_MAC_ADDR ?= ""

# Enables the assignment of IP address and hostname by discovering the
# machine name and BMC prefix from another BMC on the bridge network.
# This is intended only to be used when there is a single expansion tray
# on the BMC network. If more than one machine uses this feature with the
# same offset in the same machine network, it will collide with others.
GBMC_BR_FIXED_OFFSET ?= ""

# Generated via https://cd34.com/rfc4193/ based on a MAC from a machine I own
# and we allocated it downstream. Intended to only be used within a complete
# system of multiple network endpoints.
GBMC_ULA_PREFIX = "fdb5:0481:10ce:0"

# coordinated powercycle
GBMC_COORDINATED_POWERCYCLE ?= "true"

def mac_to_eui64(mac):
  if not mac:
    return ''
  b = [int(c, 16) for c in mac.split(':')]
  b[0] ^= 2
  b.insert(3, 0xfe)
  b.insert(3, 0xff)
  idx = range(0, len(b)-1, 2)
  return ':'.join([format((b[i] << 8) + b[i+1], '04x') for i in idx])

GBMC_BRIDGE_INTFS ?= ""

ethernet_bridge_install() {
  # install udev rules if any
  if [ -z "${GBMC_BRIDGE_INTFS}"]; then
    return
  fi
  cat /dev/null > ${WORKDIR}/-ether-bridge.network
  echo "[Match]" >> ${WORKDIR}/-ether-bridge.network
  echo "Name=${GBMC_BRIDGE_INTFS}" >>  ${WORKDIR}/-ether-bridge.network
  echo "[Network]" >> ${WORKDIR}/-ether-bridge.network
  echo "Bridge=gbmcbr" >> ${WORKDIR}/-ether-bridge.network

  install -d ${D}/${sysconfdir}/systemd/network
  install -m 0644 ${WORKDIR}/-ether-bridge.network ${D}/${sysconfdir}/systemd/network/
}

do_install() {
  netdir=${D}${systemd_unitdir}/network
  install -d -m0755 $netdir

  if [ ! -z "${GBMC_BR_MAC_ADDR}" ]; then
    sfx='${@mac_to_eui64(GBMC_BR_MAC_ADDR)}'
    addr="[Address]\nAddress=${GBMC_ULA_PREFIX}:$sfx/64\nPreferredLifetime=0\n"
    addr="$addr[Address]\nAddress=fe80::$sfx/64\nPreferredLifetime=0"
    sed -i "s,@ADDR@,$addr," ${WORKDIR}/-bmc-gbmcbr.network.in
  else
    sed -i '/@ADDR@/d' ${WORKDIR}/-bmc-gbmcbr.network.in
  fi

  ethernet_bridge_install

  install -m0644 ${WORKDIR}/-bmc-gbmcbr.netdev $netdir/
  install -m0644 ${WORKDIR}/-bmc-gbmcbr.network.in $netdir/-bmc-gbmcbr.network
  install -m0644 ${WORKDIR}/-bmc-gbmcbrdummy.netdev $netdir/
  install -m0644 ${WORKDIR}/-bmc-gbmcbrdummy.network $netdir/
  install -m0644 ${WORKDIR}/+-bmc-gbmcbrusb.network $netdir/

  nftables_dir=${D}${sysconfdir}/nftables
  install -d -m0755 "$nftables_dir"
  install -m0644 ${WORKDIR}/50-gbmc-br.rules $nftables_dir/
  install -m0644 ${WORKDIR}/50-gbmc-br-cn-redirect.rules $nftables_dir/

  mondir=${D}${datadir}/gbmc-ip-monitor
  install -d -m0755 "$mondir"
  install -m0644 ${WORKDIR}/gbmc-br-ula.sh "$mondir"/
  install -m0644 ${WORKDIR}/gbmc-br-from-ra.sh "$mondir"/
  install -m0644 ${WORKDIR}/gbmc-br-gw-src.sh "$mondir"/
  install -m0644 ${WORKDIR}/gbmc-br-nft.sh "$mondir"/

  install -d -m0755 ${D}${libexecdir}
  install -m0755 ${WORKDIR}/gbmc-br-ensure-ra.sh ${D}${libexecdir}/
  install -m0755 ${WORKDIR}/gbmc-br-hostname.sh ${D}${libexecdir}/
  install -m0755 ${WORKDIR}/gbmc-br-dhcp.sh ${D}${libexecdir}/
  install -m0755 ${WORKDIR}/gbmc-br-dhcp-term.sh ${D}${libexecdir}/
  install -d -m0755 ${D}${systemd_system_unitdir}
  install -m0644 ${WORKDIR}/gbmc-br-ensure-ra.service ${D}${systemd_system_unitdir}/
  install -m0644 ${WORKDIR}/gbmc-br-hostname.service ${D}${systemd_system_unitdir}/
  install -m0644 ${WORKDIR}/gbmc-br-dhcp.service ${D}${systemd_system_unitdir}/
  install -m0644 ${WORKDIR}/gbmc-br-dhcp-term.service ${D}${systemd_system_unitdir}/
  install -m0644 ${WORKDIR}/gbmc-br-load-ip.service ${D}${systemd_system_unitdir}/
  install -d -m0755 ${D}${datadir}/gbmc-br-dhcp

  sed 's,@COORDINATED_POWERCYCLE@,${GBMC_COORDINATED_POWERCYCLE},' ${WORKDIR}/50-gbmc-psu-hardreset.sh.in >${WORKDIR}/50-gbmc-psu-hardreset.sh
  install -m0644 ${WORKDIR}/50-gbmc-psu-hardreset.sh ${D}${datadir}/gbmc-br-dhcp/

  install -m0644 ${WORKDIR}/gbmc-br-lib.sh ${D}${datadir}/
  install -m0644 ${WORKDIR}/gbmc-ip-from-ra.sh ${D}${datadir}/

  install -d ${D}/${bindir}
  install -m0755 ${WORKDIR}/gbmc-start-dhcp.sh ${D}${bindir}/

  if [ -n "${GBMC_BR_FIXED_OFFSET}" ]; then
    sed 's,@IP_OFFSET@,${GBMC_BR_FIXED_OFFSET},' ${WORKDIR}/gbmc-br-ip-from-ra.sh.in >${WORKDIR}/gbmc-br-ip-from-ra.sh
    install -m0755 ${WORKDIR}/gbmc-br-ip-from-ra.sh ${D}${libexecdir}/
    install -m0644 ${WORKDIR}/gbmc-br-ip-from-ra.service ${D}${systemd_system_unitdir}/
  fi
}

do_rm_work:prepend() {
  # HACK: Work around broken do_rm_work not properly calling rm with `--`
  # It doesn't like filenames that start with `-`
  rm -rf -- ${WORKDIR}/-*
}
