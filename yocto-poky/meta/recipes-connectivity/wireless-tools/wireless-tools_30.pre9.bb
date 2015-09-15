SUMMARY = "Tools for the Linux Standard Wireless Extension Subsystem"
HOMEPAGE = "http://www.hpl.hp.com/personal/Jean_Tourrilhes/Linux/Tools.html"
LICENSE = "GPLv2 & (LGPLv2.1 | MPL-1.1 | BSD)"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
			file://iwconfig.c;beginline=1;endline=12;md5=cf710eb1795c376eb10ea4ff04649caf \
			file://iwevent.c;beginline=59;endline=72;md5=d66a10026d4394f0a5b1c5587bce4537 \
			file://sample_enc.c;beginline=1;endline=4;md5=838372be07874260b566bae2f6ed33b6"
SECTION = "base"
PE = "1"

SRC_URI = "http://www.hpl.hp.com/personal/Jean_Tourrilhes/Linux/wireless_tools.${PV}.tar.gz \
           file://wireless-tools.if-pre-up \
           file://zzz-wireless.if-pre-up \
           file://remove.ldconfig.call.patch \
           file://man.patch \
           file://avoid_strip.patch \
           file://ldflags.patch \
          "
SRC_URI[md5sum] = "ca91ba7c7eff9bfff6926b1a34a4697d"
SRC_URI[sha256sum] = "abd9c5c98abf1fdd11892ac2f8a56737544fe101e1be27c6241a564948f34c63"

S = "${WORKDIR}/wireless_tools.30"

CFLAGS =+ "-I${S}"
EXTRA_OEMAKE = "-e 'BUILD_SHARED=y' \
		'INSTALL_DIR=${D}${base_sbindir}' \
		'INSTALL_LIB=${D}${libdir}' \
		'INSTALL_INC=${D}${includedir}' \
		'INSTALL_MAN=${D}${mandir}'"

do_compile() {
	oe_runmake all libiw.a
}

do_install() {
	oe_runmake PREFIX=${D} install-iwmulticall install-dynamic install-man install-hdr
	install -d ${D}${sbindir}
	install -m 0755 ifrename ${D}${sbindir}/ifrename
	# Disabled by RP - 20/8/08 - We don't seem to need/use these
	#install -d ${D}${sysconfdir}/network/if-pre-up.d
	#install ${WORKDIR}/wireless-tools.if-pre-up ${D}${sysconfdir}/network/if-pre-up.d/wireless-tools
	#install ${WORKDIR}/zzz-wireless.if-pre-up ${D}${sysconfdir}/network/if-pre-up.d/zzz-wireless
}

PACKAGES = "libiw-dbg ifrename-dbg ${PN}-dbg \
libiw libiw-dev libiw-doc ifrename-doc ifrename ${PN} ${PN}-doc"

FILES_libiw-dbg = "${libdir}/.debug/*.so.*"
FILES_ifrename-dbg = "${sbindir}/.debug/ifrename"
FILES_libiw = "${libdir}/*.so.*"
FILES_libiw-dev = "${libdir}/*.a ${libdir}/*.so ${includedir}"
FILES_libiw-doc = "${mandir}/man7"
FILES_ifrename = "${sbindir}/ifrename"
FILES_ifrename-doc = "${mandir}/man8/ifrename.8 ${mandir}/man5/iftab.5"
FILES_${PN} = "${bindir} ${sbindir}/iw* ${base_sbindir} ${base_bindir} ${sysconfdir}/network"
FILES_${PN}-doc = "${mandir}"
