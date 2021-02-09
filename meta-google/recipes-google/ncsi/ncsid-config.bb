SUMMARY = "Configures ncsid for a gBMC system"
PR = "r1"
LICENSE = "CLOSED"

inherit systemd

SRC_URI += "file://50-ncsi.rules.in"

S = "${WORKDIR}"

RDEPENDS_${PN} += "ncsid"

FILES_${PN} += "${systemd_unitdir}"

do_install_append() {
  install -d -m0755 ${D}${sysconfdir}/sysctl.d
  echo "net.ipv6.conf.${NCSID_IF_NAME}.accept_dad=0" \
    >>${D}${sysconfdir}/sysctl.d/25-nodad.conf
  echo "net.ipv6.conf.${NCSID_IF_NAME}.dad_transmits=0" \
    >>${D}${sysconfdir}/sysctl.d/25-nodad.conf

  netdir=${D}${systemd_unitdir}/network/00-bmc-${NCSID_IF_NAME}.network.d
  install -d -m0755 "$netdir"
  echo '[Network]' >>"$netdir"/ncsi.conf
  echo 'DHCP=false' >>"$netdir"/ncsi.conf
  echo 'IPv6AcceptRA=false' >>"$netdir"/ncsi.conf
  echo 'LLMNR=false' >>"$netdir"/ncsi.conf
  echo 'MulticastDNS=false' >>"$netdir"/ncsi.conf
  echo 'LinkLocalAddressing=ipv6' >>"$netdir"/ncsi.conf

  nftdir=${D}${sysconfdir}/nftables
  install -d -m0755 "$nftdir"
  sed "s,@NCSI_IF@,${NCSID_IF_NAME}," ${WORKDIR}/50-ncsi.rules.in \
    >"$nftdir"/50-ncsi.rules

  wantdir=${D}${systemd_system_unitdir}/multi-user.target.wants
  install -d -m0755 "$wantdir"
  ln -sv ../ncsid@.service "$wantdir"/ncsid@${NCSID_IF_NAME}.service
}
