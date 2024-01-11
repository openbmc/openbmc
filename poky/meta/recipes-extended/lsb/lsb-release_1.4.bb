SUMMARY = "lsb_release support for OpenEmbedded"
SECTION = "console/utils"
HOMEPAGE = "https://sourceforge.net/projects/lsb/files"
LICENSE = "GPL-2.0-or-later"

# lsb_release needs getopt
RDEPENDS:${PN} += "${VIRTUAL-RUNTIME_getopt}"

LIC_FILES_CHKSUM = "file://README;md5=12da544b1a3a5a1795a21160b49471cf"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/lsb/lsb_release/1.4/lsb-release-1.4.tar.gz \
           file://0001-fix-lsb_release-to-work-with-busybox-head-and-find.patch \
           file://0001-Remove-timestamp-from-manpage.patch \
           file://help2man-reproducibility.patch \
           "

SRC_URI[sha256sum] = "99321288f8d62e7a1d485b7c6bdccf06766fb8ca603c6195806e4457fdf17172"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/lsb/files/lsb_release/"
UPSTREAM_CHECK_REGEX = "/lsb_release/(?P<pver>(\d+[\.\-_]*)+)/"

CLEANBROKEN = "1"

do_install() {
	oe_runmake install prefix=${D}${root_prefix} mandir=${D}${datadir}/man/ DESTDIR=${D}

	mkdir -p ${D}${sysconfdir}/lsb-release.d

	echo "DISTRIB_ID=${DISTRO}" >> ${D}${sysconfdir}/lsb-release
	echo "DISTRIB_RELEASE=${DISTRO_VERSION}" >> ${D}${sysconfdir}/lsb-release
	if [ -n "${DISTRO_CODENAME}" ]; then
		echo "DISTRIB_CODENAME=${DISTRO_CODENAME}" >> ${D}${sysconfdir}/lsb-release
	fi
	echo "DISTRIB_DESCRIPTION=\"${DISTRO_NAME} ${DISTRO_VERSION}\"" >> ${D}${sysconfdir}/lsb-release
}

FILES:${PN} += "${base_libdir}"
