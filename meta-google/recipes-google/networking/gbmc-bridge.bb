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
  file://ipmi.service.in \
  file://50-gbmc-br.rules \
  file://gbmc-br-ula.sh \
  file://gbmc-br-from-ra.sh \
  file://gbmc-br-ensure-ra.sh \
  file://gbmc-br-ensure-ra.service \
  file://gbmc-br-gw-src.sh \
  file://gbmc-br-nft.sh \
  file://gbmc-br-dhcp.sh \
  file://gbmc-br-dhcp.service \
  file://gbmc-br-dhcp-term.sh \
  file://gbmc-br-dhcp-term.service \
  "

FILES:${PN}:append = " \
  ${datadir}/gbmc-ip-monitor \
  ${systemd_unitdir}/network \
  ${sysconfdir}/nftables \
  ${sysconfdir}/avahi/services \
  "

RDEPENDS:${PN}:append = " \
  bash \
  dhcp-done \
  gbmc-ip-monitor \
  mstpd-mstpd \
  network-sh \
  ndisc6-rdisc6 \
  "

SYSTEMD_SERVICE:${PN} += " \
  gbmc-br-ensure-ra.service \
  gbmc-br-dhcp.service \
  gbmc-br-dhcp-term.service \
  "

GBMC_BR_MAC_ADDR ?= ""

# Generated via https://cd34.com/rfc4193/ based on a MAC from a machine I own
# and we allocated it downstream. Intended to only be used within a complete
# system of multiple network endpoints.
GBMC_ULA_PREFIX = "fdb5:0481:10ce:0"

def mac_to_eui64(mac):
  if not mac:
    return ''
  b = [int(c, 16) for c in mac.split(':')]
  b[0] ^= 2
  b.insert(3, 0xfe)
  b.insert(3, 0xff)
  idx = range(0, len(b)-1, 2)
  return ':'.join([format((b[i] << 8) + b[i+1], '04x') for i in idx])

do_install() {
  netdir=${D}${systemd_unitdir}/network
  install -d -m0755 $netdir

  if [ ! -z "${GBMC_BR_MAC_ADDR}" ]; then
    sfx='${@mac_to_eui64(GBMC_BR_MAC_ADDR)}'
    addr="Address=${GBMC_ULA_PREFIX}:$sfx/64\nAddress=fe80::$sfx/64"
    sed -i "s,@ADDR@,$addr," ${WORKDIR}/-bmc-gbmcbr.network.in
  else
    sed -i '/@ADDR@/d' ${WORKDIR}/-bmc-gbmcbr.network.in
  fi

  install -m0644 ${WORKDIR}/-bmc-gbmcbr.netdev $netdir/
  install -m0644 ${WORKDIR}/-bmc-gbmcbr.network.in $netdir/-bmc-gbmcbr.network
  install -m0644 ${WORKDIR}/-bmc-gbmcbrdummy.netdev $netdir/
  install -m0644 ${WORKDIR}/-bmc-gbmcbrdummy.network $netdir/
  install -m0644 ${WORKDIR}/+-bmc-gbmcbrusb.network $netdir/

  nftables_dir=${D}${sysconfdir}/nftables
  install -d -m0755 "$nftables_dir"
  install -m0644 ${WORKDIR}/50-gbmc-br.rules $nftables_dir/

  avahi_dir=${D}${sysconfdir}/avahi/services
  install -d -m 0755 "$avahi_dir"
  sed -i 's,@MACHINE@,${MACHINE},g' ${WORKDIR}/ipmi.service.in
  sed -i 's,@EXTRA_ATTRS@,,g' ${WORKDIR}/ipmi.service.in
  sed 's,@NAME@,bmc,g' ${WORKDIR}/ipmi.service.in >${avahi_dir}/bmc.ipmi.service
  sed 's,@NAME@,${MACHINE}-bmc,g' ${WORKDIR}/ipmi.service.in >${avahi_dir}/${MACHINE}-bmc.ipmi.service

  mondir=${D}${datadir}/gbmc-ip-monitor
  install -d -m0755 "$mondir"
  install -m0644 ${WORKDIR}/gbmc-br-ula.sh "$mondir"/
  install -m0644 ${WORKDIR}/gbmc-br-from-ra.sh "$mondir"/
  install -m0644 ${WORKDIR}/gbmc-br-gw-src.sh "$mondir"/
  install -m0644 ${WORKDIR}/gbmc-br-nft.sh "$mondir"/

  install -d -m0755 ${D}${libexecdir}
  install -m0755 ${WORKDIR}/gbmc-br-ensure-ra.sh ${D}${libexecdir}/
  install -m0755 ${WORKDIR}/gbmc-br-dhcp.sh ${D}${libexecdir}/
  install -m0755 ${WORKDIR}/gbmc-br-dhcp-term.sh ${D}${libexecdir}/
  install -d -m0755 ${D}${systemd_system_unitdir}
  install -m0644 ${WORKDIR}/gbmc-br-ensure-ra.service ${D}${systemd_system_unitdir}/
  install -m0644 ${WORKDIR}/gbmc-br-dhcp.service ${D}${systemd_system_unitdir}/
  install -m0644 ${WORKDIR}/gbmc-br-dhcp-term.service ${D}${systemd_system_unitdir}/
}

do_rm_work:prepend() {
  # HACK: Work around broken do_rm_work not properly calling rm with `--`
  # It doesn't like filenames that start with `-`
  rm -rf -- ${WORKDIR}/-*
}
