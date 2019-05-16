SUMMARY = "IPTables based firewall scripts"
HOMEPAGE = "http://rocky.eld.leidenuniv.nl/joomla/index.php?option=com_content&view=article&id=45&Itemid=63"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://gpl_license.txt;md5=11c7b65c4a4acb9d5175f7e9bf99c403"

SRC_URI = "https://github.com/arno-iptables-firewall/aif/archive/${PV}.tar.gz"
SRC_URI[md5sum] = "71271c08299aacd45ceb9c9400082d03"
SRC_URI[sha256sum] = "e2c4fb88a25f0cae8308a3b2bb922b5e2f52f24d8309b24e15a527a6b326e703"

S = "${WORKDIR}/aif-${PV}"

inherit systemd

do_install() {
	install -d ${D}${sysconfdir} ${D}${sbindir} ${D}${bindir} ${D}${systemd_unitdir}/system ${D}${sysconfdir}/init.d
	install -d ${D}${datadir}/arno-iptables-firewall ${D}${sysconfdir}/arno-iptables-firewall
	cp -r ${S}${sysconfdir}/arno-iptables-firewall ${D}${sysconfdir}/
	install -m 0755 ${S}${sysconfdir}/init.d/arno-iptables-firewall ${D}${bindir}
	install -m 0755 ${S}/bin/arno-iptables-firewall ${D}${sbindir}
	install -m 0755 ${S}/bin/arno-fwfilter ${D}${bindir}
	cp -r ${S}/share/arno-iptables-firewall/* ${D}${datadir}/arno-iptables-firewall
	cp -r ${S}/etc/arno-iptables-firewall/* ${D}${sysconfdir}/arno-iptables-firewall
	install -m 0644 ${S}/${systemd_unitdir}/system/arno-iptables-firewall.service ${D}${systemd_unitdir}/system
	sed -i -e 's%/usr/local/sbin%${bindir}%g' ${D}${systemd_unitdir}/system/arno-iptables-firewall.service
	sed -i -e 's%/usr/local/sbin%${sbindir}%g' ${D}${bindir}/arno-iptables-firewall
	sed -i -e 's%/usr/local%${exec_prefix}%g' ${D}${sysconfdir}/arno-iptables-firewall/firewall.conf
	sed -i -e 's%#!/bin/bash%#!/bin/sh%g' ${D}${bindir}/arno-fwfilter
	sed -i -e 's%#!/bin/bash%#!/bin/sh%g' ${D}${datadir}/arno-iptables-firewall/plugins/traffic-accounting-helper
	sed -i -e 's%#!/bin/bash%#!/bin/sh%g' ${D}${datadir}/arno-iptables-firewall/plugins/dyndns-host-open-helper
}

SYSTEMD_SERVICE_${PN} = "arno-iptables-firewall.service"
FILES_${PN} += "${systemd_unitdir}/system/arno-iptables-firewall.service"
