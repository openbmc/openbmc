SUMMARY = "Configures the gbmc bridge and filter rules"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
  file://-bmc-gbmcbr.netdev.in \
  file://-bmc-gbmcbr.network \
  file://-bmc-gbmcbrdummy.netdev \
  file://-bmc-gbmcbrdummy.network \
  file://+-bmc-gbmcbrusb.network \
  file://ipmi.service.in \
  file://50-gbmc-br.rules \
  "

FILES_${PN}_append = " \
  ${systemd_unitdir}/network \
  ${sysconfdir}/nftables \
  ${sysconfdir}/avahi/services \
  "

RDEPENDS_${PN}_append = " \
  mstpd-mstpd \
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
    sed -i 's,@MAC@,MACAddress=${GBMC_BR_MAC_ADDR},' ${WORKDIR}/-bmc-gbmcbr.netdev.in
	addr=${GBMC_ULA_PREFIX}:${@mac_to_eui64(GBMC_BR_MAC_ADDR)}/64
    sed -i "s,@ADDR@,Address=$addr," ${WORKDIR}/-bmc-gbmcbr.netdev.in
  else
    sed -i '/@MAC@/d' ${WORKDIR}/-bmc-gbmcbr.netdev.in
    sed -i '/@ADDR@/d' ${WORKDIR}/-bmc-gbmcbr.netdev.in
  fi

  install -m0644 ${WORKDIR}/-bmc-gbmcbr.netdev.in $netdir/-bmc-gbmcbr.netdev
  install -m0644 ${WORKDIR}/-bmc-gbmcbr.network $netdir/
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
}
