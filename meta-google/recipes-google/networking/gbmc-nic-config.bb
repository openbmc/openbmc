SUMMARY = "Configured gBMC non-NCSI interface"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

GBMC_EXT_NICS ?= ""
GBMC_DHCP_RELAY ??= "${@'' if int(d.getVar('FLASH_SIZE')) < 65536 else '1'}"

inherit systemd

SRC_URI += " \
  file://50-gbmc-nic.rules \
  file://50-gbmc-nic.rules.in \
  file://10-dhcp4.conf \
  file://-bmc-nic.network.in \
  file://gbmc-nic-dhcrelay.sh.in \
  file://gbmc-nic-neigh.sh.in \
  file://gbmc-nic-ra.sh \
  file://gbmc-nic-ra@.service \
  file://gbmc-nic-devlab-config.sh.in \
  ${@'' if d.getVar('GBMC_DHCP_RELAY') != '1' else 'file://-bmc-gbmcbrnicdhcp.netdev'} \
  ${@'' if d.getVar('GBMC_DHCP_RELAY') != '1' else 'file://-bmc-gbmcbrnicdhcp.network'} \
  ${@'' if d.getVar('GBMC_DHCP_RELAY') != '1' else 'file://-bmc-gbmcnicdhcp.netdev'} \
  ${@'' if d.getVar('GBMC_DHCP_RELAY') != '1' else 'file://-bmc-gbmcnicdhcp.network'} \
  ${@'' if d.getVar('GBMC_DHCP_RELAY') != '1' else 'file://gbmc-nic-dhcrelay@.service'} \
  "
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

FILES:${PN} += " \
  ${systemd_unitdir}/network \
  ${sysconfdir}/nftables \
  ${systemd_system_unitdir} \
  ${datadir}/gbmc-ip-monitor \
  ${bindir}/gbmc-nic-devlab-config.sh \
  "

RDEPENDS:${PN}:append = " \
  bash \
  gbmc-ip-monitor \
  gbmc-net-common \
  nftables-systemd \
  "

do_install() {
  netdir=${D}${systemd_unitdir}/network
  install -d -m0755 $netdir
  nftdir=${D}${sysconfdir}/nftables
  install -d -m0755 $nftdir
  unitdir=${D}${systemd_system_unitdir}
  install -d -m0755 $unitdir
  wantdir=$unitdir/multi-user.target.wants
  install -d -m0755 $wantdir

  install -d -m0755 ${D}${libexecdir}
  install -m0755 ${UNPACKDIR}/gbmc-nic-ra.sh ${D}${libexecdir}/
  install -m0644 ${UNPACKDIR}/gbmc-nic-ra@.service $unitdir/

  mondir=${D}${datadir}/gbmc-ip-monitor
  install -d -m0755 $mondir
  sed 's,@IFS@,${GBMC_EXT_NICS},g' <${UNPACKDIR}/gbmc-nic-neigh.sh.in \
    >$mondir/gbmc-nic-neigh.sh

  for intf in ${GBMC_EXT_NICS}; do
    sed "s,@IF@,$intf,g" <${UNPACKDIR}/50-gbmc-nic.rules.in >$nftdir/50-gbmc-$intf.rules
    sed "s,@IF@,$intf,g" <${UNPACKDIR}/-bmc-nic.network.in >$netdir/-bmc-$intf.network
    ln -sv ../gbmc-nic-ra@.service $wantdir/gbmc-nic-ra@$intf.service
  done

  if [ "${GBMC_DHCP_RELAY}" = 1 ]; then
    install -m0644 ${UNPACKDIR}/-bmc-gbmcbrnicdhcp.network $netdir/
    install -m0644 ${UNPACKDIR}/-bmc-gbmcbrnicdhcp.netdev $netdir/
    install -m0644 ${UNPACKDIR}/-bmc-gbmcnicdhcp.network $netdir/
    install -m0644 ${UNPACKDIR}/-bmc-gbmcnicdhcp.netdev $netdir/
    install -m0644 ${UNPACKDIR}/50-gbmc-nic.rules $nftdir/
    install -m0644 ${UNPACKDIR}/gbmc-nic-dhcrelay@.service $unitdir/

    sed 's,@IFS@,${GBMC_EXT_NICS},g' <${UNPACKDIR}/gbmc-nic-dhcrelay.sh.in \
      >$mondir/gbmc-nic-dhcrelay.sh
  fi
}

do_install:append:local() {
  # For local builds, enable DHCP4 on all external interfaces.
  for intf in ${GBMC_EXT_NICS}; do
    install -d -m0755 $netdir/-bmc-$intf.network.d
    install -m0644 ${WORKDIR}/10-dhcp4.conf $netdir/-bmc-$intf.network.d/10-dhcp4.conf
  done
}

do_install:append:dev() {
  install -d -m0755 ${D}${bindir}
  sed 's,@IFS@,${GBMC_EXT_NICS},g' <${WORKDIR}/gbmc-nic-devlab-config.sh.in \
      >${D}${bindir}/gbmc-nic-devlab-config.sh
  chmod 755 ${D}${bindir}/gbmc-nic-devlab-config.sh
}
