SUMMARY = "name server information handler"
DESCRIPTION = "Resolvconf is a framework for keeping track of the system's \
information about currently available nameservers. It sets \
itself up as the intermediary between programs that supply \
nameserver information and programs that need nameserver \
information."
SECTION = "console/network"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"
AUTHOR = "Thomas Hood"
HOMEPAGE = "http://packages.debian.org/resolvconf"
RDEPENDS_${PN} = "bash"

SRC_URI = "git://salsa.debian.org/debian/resolvconf.git;protocol=https;branch=unstable \
           file://fix-path-for-busybox.patch \
           file://99_resolvconf \
           "

SRCREV = "1dda36d8465e335c60190c41e7185d782da1bd7b"

S = "${WORKDIR}/git"

# the package is taken from snapshots.debian.org; that source is static and goes stale
# so we check the latest upstream from a directory that does get updated
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/r/resolvconf/"

inherit allarch

do_compile () {
	:
}

do_install () {
	install -d ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${WORKDIR}/99_resolvconf ${D}${sysconfdir}/default/volatiles
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/tmpfiles.d
		echo "d /run/${BPN}/interface - - - -" \
		     > ${D}${sysconfdir}/tmpfiles.d/resolvconf.conf
	fi
	install -d ${D}${base_libdir}/${BPN}
	install -d ${D}${sysconfdir}/${BPN}
	ln -snf ${localstatedir}/run/${BPN} ${D}${sysconfdir}/${BPN}/run
	install -d ${D}${sysconfdir} ${D}${base_sbindir}
	install -d ${D}${mandir}/man8 ${D}${docdir}/${P}
	cp -pPR etc/resolvconf ${D}${sysconfdir}/
	chown -R root:root ${D}${sysconfdir}/
	install -m 0755 bin/resolvconf ${D}${base_sbindir}/
	install -m 0755 bin/list-records ${D}${base_libdir}/${BPN}
	install -d ${D}/${sysconfdir}/network/if-up.d
	install -m 0755 debian/resolvconf.000resolvconf.if-up ${D}/${sysconfdir}/network/if-up.d/000resolvconf
	install -d ${D}/${sysconfdir}/network/if-down.d
	install -m 0755 debian/resolvconf.resolvconf.if-down ${D}/${sysconfdir}/network/if-down.d/resolvconf
	install -m 0644 README ${D}${docdir}/${P}/
	install -m 0644 man/resolvconf.8 ${D}${mandir}/man8/
}

pkg_postinst_${PN} () {
	if [ -z "$D" ]; then
		if command -v systemd-tmpfiles >/dev/null; then
			systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/resolvconf.conf
		elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
			${sysconfdir}/init.d/populate-volatile.sh update
		fi
	fi
}

FILES_${PN} += "${base_libdir}/${BPN}"
