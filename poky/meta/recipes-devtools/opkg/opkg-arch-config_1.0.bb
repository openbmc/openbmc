SUMMARY = "Architecture-dependent configuration for opkg"
HOMEPAGE = "https://git.yoctoproject.org/opkg/"
LICENSE = "MIT"
PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_compile() {
	mkdir -p ${S}/${sysconfdir}/opkg/

	archconf=${S}/${sysconfdir}/opkg/arch.conf

	rm -f $archconf
	ipkgarchs="${ALL_MULTILIB_PACKAGE_ARCHS}"
	priority=1
	for arch in $ipkgarchs; do 
		echo "arch $arch $priority" >> $archconf
		priority=$(expr $priority + 5)
	done
}


do_install () {
	install -d ${D}${sysconfdir}/opkg
	install -m 0644  ${S}/${sysconfdir}/opkg/* ${D}${sysconfdir}/opkg/
}

FILES:${PN} = "${sysconfdir}/opkg/ "

CONFFILES:${PN} += "${sysconfdir}/opkg/arch.conf"

RREPLACES:${PN} = "opkg-config-base"
RCONFLICTS:${PN} = "opkg-config-base"
RPROVIDES:${PN} = "opkg-config-base"
