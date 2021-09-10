SUMMARY = "Configures ncsi for a gBMC system"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += " \
  file://50-gbmc-ncsi.rules.in \
  file://gbmc-ncsi-sslh.socket.in \
  file://gbmc-ncsi-sslh.service \
  file://gbmc-ncsi-nft.sh.in \
  file://gbmc-ncsi-br-pub-addr.sh.in \
  file://gbmc-ncsi-set-nicenabled.service.in \
  "

S = "${WORKDIR}"

RDEPENDS:${PN} += " \
  gbmc-ip-monitor \
  ncsid \
  nftables-systemd \
  sslh \
  "

FILES:${PN} += " \
  ${datadir}/gbmc-ip-monitor \
  ${systemd_unitdir} \
  "

SYSTEMD_SERVICE:${PN} += " \
  gbmc-ncsi-sslh.service \
  gbmc-ncsi-sslh.socket \
  gbmc-ncsi-set-nicenabled.service \
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

  sed "s,@NCSI_IF@,$if_name,g" ${WORKDIR}/gbmc-ncsi-set-nicenabled.service.in \
    >${D}${systemd_system_unitdir}/gbmc-ncsi-set-nicenabled.service
}
