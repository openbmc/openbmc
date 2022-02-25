SUMMARY = "Utilities for Advanced Power Management"
DESCRIPTION = "The Advanced Power Management (APM) support provides \
access to battery status information and a set of tools for managing \
notebook power consumption."
HOMEPAGE = "http://apenwarr.ca/apmd/"
SECTION = "base"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://apm.h;beginline=6;endline=18;md5=7d4acc1250910a89f84ce3cc6557c4c2"
DEPENDS = "libtool-cross"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160728T043443Z/pool/main/a/${BPN}/${BPN}_3.2.2.orig.tar.gz;name=tarball \
           file://legacy.patch \
           file://libtool.patch \
           file://unlinux.patch \
           file://wexitcode.patch \
           file://linkage.patch \
           file://init \
           file://default \
           file://apmd_proxy \
           file://apmd_proxy.conf \
           file://apmd.service"

SRC_URI[tarball.md5sum] = "b1e6309e8331e0f4e6efd311c2d97fa8"
SRC_URI[tarball.sha256sum] = "7f7d9f60b7766b852881d40b8ff91d8e39fccb0d1d913102a5c75a2dbb52332d"

# for this package we're mostly interested in tracking debian patches,
# and not in the upstream version where all development has effectively stopped
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/a/apmd/"
UPSTREAM_CHECK_REGEX = "(?P<pver>((\d+\.*)+)-((\d+\.*)+))\.(diff|debian\.tar)\.(gz|xz)"

S = "${WORKDIR}/apmd-3.2.2.orig"

inherit update-rc.d systemd

INITSCRIPT_NAME = "apmd"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE:${PN} = "apmd.service"
SYSTEMD_AUTO_ENABLE = "disable"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile() {
	# apmd doesn't use whole autotools. Just libtool for installation
	oe_runmake apm apmd
}

do_install() {
	install -d ${D}${sysconfdir}
	install -d ${D}${sysconfdir}/apm
	install -d ${D}${sysconfdir}/apm/event.d
	install -d ${D}${sysconfdir}/apm/other.d
	install -d ${D}${sysconfdir}/apm/suspend.d
	install -d ${D}${sysconfdir}/apm/resume.d
	install -d ${D}${sysconfdir}/apm/scripts.d
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sbindir}
	install -d ${D}${bindir}
	install -d ${D}${libdir}
	install -d ${D}${datadir}/apmd
	install -d ${D}${includedir}

	install -m 4755 ${S}/.libs/apm ${D}${bindir}/apm
	install -m 0755 ${S}/.libs/apmd ${D}${sbindir}/apmd
	install -m 0755 ${WORKDIR}/apmd_proxy ${D}${sysconfdir}/apm/
	install -m 0644 ${WORKDIR}/apmd_proxy.conf ${D}${datadir}/apmd/
	install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/apmd
	oe_libinstall -so libapm ${D}${libdir}
	install -m 0644 apm.h ${D}${includedir}

	sed -e 's,/usr/sbin,${sbindir},g; s,/etc,${sysconfdir},g;' ${WORKDIR}/init > ${D}${sysconfdir}/init.d/apmd
	chmod 755 ${D}${sysconfdir}/init.d/apmd

	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/apmd.service ${D}${systemd_system_unitdir}/
	sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' \
		-e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_system_unitdir}/apmd.service
}

PACKAGES =+ "libapm apm"

FILES:libapm = "${libdir}/libapm${SOLIBS}"
FILES:apm = "${bindir}/apm*"
