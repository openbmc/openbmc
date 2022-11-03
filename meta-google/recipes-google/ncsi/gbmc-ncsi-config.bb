SUMMARY = "Configures ncsi for a gBMC system"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += " \
  file://-bmc-gbmcbrncsidhcp.netdev \
  file://-bmc-gbmcbrncsidhcp.network \
  file://-bmc-gbmcncsidhcp.netdev \
  file://-bmc-gbmcncsidhcp.network \
  file://50-gbmc-ncsi.rules.in \
  file://gbmc-ncsi-dhcrelay.service.in \
  file://gbmc-ncsi-ip-from-ra.service.in \
  file://gbmc-ncsi-ip-from-ra.sh.in \
  file://gbmc-ncsi-smartnic-wa.sh.in \
  file://gbmc-ncsi-sslh.socket.in \
  file://gbmc-ncsi-sslh.service \
  file://gbmc-ncsi-nft.sh.in \
  file://gbmc-ncsi-br-pub-addr.sh.in \
  file://gbmc-ncsi-br-deprecated-ips.sh.in \
  file://gbmc-ncsi-set-nicenabled.service.in \
  file://gbmc-ncsi-alias.service.in \
  file://50-gbmc-ncsi-clear-ip.sh.in \
  "

S = "${WORKDIR}"

RDEPENDS:${PN} += " \
  bash \
  dhcp-relay \
  gbmc-ip-monitor \
  ncsid \
  network-sh \
  nftables-systemd \
  sslh \
  ndisc6-rdisc6 \
  "

FILES:${PN} += " \
  ${datadir}/gbmc-br-lib \
  ${datadir}/gbmc-ip-monitor \
  ${systemd_unitdir} \
  "

SYSTEMD_SERVICE:${PN} += " \
  gbmc-ncsi-dhcrelay.service \
  gbmc-ncsi-sslh.service \
  gbmc-ncsi-sslh.socket \
  gbmc-ncsi-set-nicenabled.service \
  gbmc-ncsi-ip-from-ra.service \
  "

do_install:append() {
  if_name='${GBMC_NCSI_IF_NAME}'
  if [ -z "$if_name" ]; then
    echo "Missing if_name" >&2
    exit 1
  fi

  install -d -m0755 ${D}${sysconfdir}/sysctl.d
  echo "net.ipv6.conf.$if_name.accept_dad=0" \
    >>${D}${sysconfdir}/sysctl.d/25-gbmc-ncsi.conf
  echo "net.ipv6.conf.$if_name.dad_transmits=0" \
    >>${D}${sysconfdir}/sysctl.d/25-gbmc-ncsi.conf

  install -d -m0755 ${D}${systemd_unitdir}/network
  install -m0644 ${WORKDIR}/-bmc-gbmcbrncsidhcp.netdev \
    ${D}${systemd_unitdir}/network/
  install -m0644 ${WORKDIR}/-bmc-gbmcbrncsidhcp.network \
    ${D}${systemd_unitdir}/network/
  install -m0644 ${WORKDIR}/-bmc-gbmcncsidhcp.netdev \
    ${D}${systemd_unitdir}/network/
  install -m0644 ${WORKDIR}/-bmc-gbmcncsidhcp.network \
    ${D}${systemd_unitdir}/network/

  netdir=${D}${systemd_unitdir}/network/00-bmc-$if_name.network.d
  install -d -m0755 "$netdir"
  echo '[Network]' >>"$netdir"/gbmc-ncsi.conf
  echo 'DHCP=false' >>"$netdir"/gbmc-ncsi.conf
  echo 'IPv6AcceptRA=false' >>"$netdir"/gbmc-ncsi.conf
  echo 'LLMNR=false' >>"$netdir"/gbmc-ncsi.conf
  echo 'MulticastDNS=false' >>"$netdir"/gbmc-ncsi.conf
  echo 'LinkLocalAddressing=ipv6' >>"$netdir"/gbmc-ncsi.conf

  nftdir=${D}${sysconfdir}/nftables
  install -d -m0755 "$nftdir"
  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/50-gbmc-ncsi.rules.in \
    >"$nftdir"/50-gbmc-ncsi.rules

  wantdir=${D}${systemd_system_unitdir}/multi-user.target.wants
  install -d -m0755 "$wantdir"
  ln -sv ../ncsid@.service "$wantdir"/ncsid@$if_name.service

  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-alias.service.in \
    >${D}${systemd_system_unitdir}/gbmc-ncsi-alias.service
  install -d -m0755 "${D}${systemd_system_unitdir}/nic-hostless@$if_name.target.wants"
  ln -sv ../gbmc-ncsi-alias.service "${D}${systemd_system_unitdir}/nic-hostless@$if_name.target.wants"/
  install -d -m0755 "${D}${systemd_system_unitdir}/nic-hostful@$if_name.target.wants"
  ln -sv ../gbmc-ncsi-alias.service "${D}${systemd_system_unitdir}/nic-hostful@$if_name.target.wants"/

  install -m 0644 ${WORKDIR}/gbmc-ncsi-sslh.service ${D}${systemd_system_unitdir}
  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-sslh.socket.in \
    >${D}${systemd_system_unitdir}/gbmc-ncsi-sslh.socket

  mondir=${D}${datadir}/gbmc-ip-monitor/
  install -d -m0755 $mondir
  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-nft.sh.in \
    >${WORKDIR}/gbmc-ncsi-nft.sh
  install -m644 ${WORKDIR}/gbmc-ncsi-nft.sh $mondir
  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-br-pub-addr.sh.in \
    >${WORKDIR}/gbmc-ncsi-br-pub-addr.sh
  install -m644 ${WORKDIR}/gbmc-ncsi-br-pub-addr.sh $mondir
  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-br-deprecated-ips.sh.in \
    >${WORKDIR}/gbmc-ncsi-br-deprecated-ips.sh
  install -m644 ${WORKDIR}/gbmc-ncsi-br-deprecated-ips.sh $mondir

  brlibdir=${D}${datadir}/gbmc-br-lib/
  install -d -m0755 $brlibdir
  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/50-gbmc-ncsi-clear-ip.sh.in \
    >${WORKDIR}/50-gbmc-ncsi-clear-ip.sh
  install -m644 ${WORKDIR}/50-gbmc-ncsi-clear-ip.sh $brlibdir

  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-set-nicenabled.service.in \
    >${D}${systemd_system_unitdir}/gbmc-ncsi-set-nicenabled.service

  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-dhcrelay.service.in \
    >${D}${systemd_system_unitdir}/gbmc-ncsi-dhcrelay.service

  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-ip-from-ra.service.in \
    >${WORKDIR}/gbmc-ncsi-ip-from-ra.service
  install -m0644 ${WORKDIR}/gbmc-ncsi-ip-from-ra.service ${D}${systemd_system_unitdir}
  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-ip-from-ra.sh.in \
    >${WORKDIR}/gbmc-ncsi-ip-from-ra.sh
  install -d -m0755 ${D}${libexecdir}
  install -m0755 ${WORKDIR}/gbmc-ncsi-ip-from-ra.sh ${D}${libexecdir}/

  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-smartnic-wa.sh.in \
    >${WORKDIR}/gbmc-ncsi-smartnic-wa.sh
  install -d -m0755 ${D}${bindir}
  install -m0755 ${WORKDIR}/gbmc-ncsi-smartnic-wa.sh ${D}${bindir}/
}

do_rm_work:prepend() {
  # HACK: Work around broken do_rm_work not properly calling rm with `--`
  # It doesn't like filenames that start with `-`
  rm -rf -- ${WORKDIR}/-*
}
