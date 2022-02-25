SUMMARY = "Name Service Switch module for resolving the local hostname"
DESCRIPTION = "plugin for the GNU Name Service Switch (NSS) functionality of \
the GNU C Library (glibc) providing host name resolution for the locally \
configured system hostname as returned by gethostname(2)."
HOMEPAGE = "http://0pointer.de/lennart/projects/nss-myhostname/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRC_URI = "http://0pointer.de/lennart/projects/nss-myhostname/nss-myhostname-${PV}.tar.gz \
           "

SRC_URI[md5sum] = "d4ab9ac36c053ab8fb836db1cbd4a48f"
SRC_URI[sha256sum] = "2ba744ea8d578d1c57c85884e94a3042ee17843a5294434d3a7f6c4d67e7caf2"

inherit autotools features_check

COMPATIBLE_HOST:libc-musl = 'null'

# The systemd has its own copy of nss-myhostname
CONFLICT_DISTRO_FEATURES = "systemd"

pkg_postinst:${PN} () {
	sed -e '/^hosts:/s/\s*\<myhostname\>//' \
		-e 's/\(^hosts:.*\)\(\<files\>\)\(.*\)\(\<dns\>\)\(.*\)/\1\2 myhostname \3\4\5/' \
		-i $D${sysconfdir}/nsswitch.conf
}

pkg_prerm:${PN} () {
	sed -e '/^hosts:/s/\s*\<myhostname\>//' \
		-e '/^hosts:/s/\s*myhostname//' \
		-i $D${sysconfdir}/nsswitch.conf
}
