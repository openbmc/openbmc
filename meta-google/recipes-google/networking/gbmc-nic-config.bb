SUMMARY = "Configured gBMC non-NCSI interface"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

GBMC_EXT_NICS ?= ""
GBMC_DHCP_RELAY ??= "${@'' if int(d.getVar('FLASH_SIZE')) < 65536 else '1'}"

inherit systemd

SRC_URI += " \
  file://50-gbmc-nic.rules.in \
  file://10-dhcp4.conf \
  file://10-l2br.conf \
  file://-bmc-nic.network.in \
  ${@'' if d.getVar('GBMC_DHCP_RELAY') != '1' else 'file://gbmc-nic-dhcrelay.sh.in'} \
  file://gbmc-nic-neigh.sh.in \
  file://gbmc-nic-cn.sh.in \
  file://gbmc-nic-ra.sh \
  file://gbmc-nic-ra@.service \
  file://gbmc-nic-devlab-config.sh.in \
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

DHCP = "false"
DHCP:local = "ipv4"

MFG_IMAGE = "${@'1' if "mfg" in d.getVar('OVERRIDES').split(':') else '0'}"

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

  ext_nic="${GBMC_EXT_NICS}"

  if [ "${MFG_IMAGE}" = "1" ]; then
    ext_nic="l2br"
  fi

  # for minimal mfg image, use the master bridge instead
  mondir=${D}${datadir}/gbmc-ip-monitor
  install -d -m0755 $mondir
  sed "s,@IFS@,$ext_nic,g" <${UNPACKDIR}/gbmc-nic-neigh.sh.in \
    >$mondir/gbmc-nic-neigh.sh

  # We don't need this, use l2br rules instead
  if [ "${MFG_IMAGE}" != "1" ]; then
    sed 's,@IF@,${GBMC_EXT_NICS},g' <${UNPACKDIR}/50-gbmc-nic.rules.in >$nftdir/50-gbmc-${GBMC_EXT_NICS}.rules
  fi
  # LLDP still on the EXT interface
  sed -e 's,@IF@,${GBMC_EXT_NICS},g' -e "s,@DHCP@,${DHCP},g" \
      <${UNPACKDIR}/-bmc-nic.network.in >$netdir/-bmc-${GBMC_EXT_NICS}.network

  ln -sv ../gbmc-nic-ra@.service $wantdir/gbmc-nic-ra@${ext_nic}.service

  if [ "${GBMC_DHCP_RELAY}" = 1 ]; then
    sed "s,@IFS@,$ext_nic,g" <${UNPACKDIR}/gbmc-nic-dhcrelay.sh.in \
      >$mondir/gbmc-nic-dhcrelay.sh
  fi
}

do_install:append:mfg() {
  # For mfg builds, enable l2 bridge on external interfaces.
  for intf in ${GBMC_EXT_NICS}; do
    install -d -m0755 $netdir/-bmc-$intf.network.d
    install -m0644 ${UNPACKDIR}/10-l2br.conf $netdir/-bmc-$intf.network.d/10-l2br.conf
  done
}

do_install:append:local() {
  # stop dhcp on external port as it will be on l2 bridge in mfg build
  [ "${MFG_IMAGE}" = "1" ] && return
  # For local builds, enable DHCP4 on all external interfaces.
  for intf in ${GBMC_EXT_NICS}; do
    install -d -m0755 $netdir/-bmc-$intf.network.d
    install -m0644 ${UNPACKDIR}/10-dhcp4.conf $netdir/-bmc-$intf.network.d/10-dhcp4.conf
  done

  mondir=${D}${datadir}/gbmc-ip-monitor
  install -d -m0755 $mondir
  sed 's,@IFS@,${GBMC_EXT_NICS},g' <${UNPACKDIR}/gbmc-nic-cn.sh.in \
    >$mondir/gbmc-nic-cn.sh
}

do_install:append:dev() {
  install -d -m0755 ${D}${bindir}
  sed 's,@IFS@,${GBMC_EXT_NICS},g' <${UNPACKDIR}/gbmc-nic-devlab-config.sh.in \
      >${D}${bindir}/gbmc-nic-devlab-config.sh
  chmod 755 ${D}${bindir}/gbmc-nic-devlab-config.sh
}
