SUMMARY = "Miscellaneous utilities specific to Debian"
SUMMARY_${PN}-cron = "Cron scripts to control automatic debsum checking"
DESCRIPTION = "A tool for verification of installed package files against \
MD5 checksums debsums can verify the integrity of installed package files \
against MD5 checksums installed by the package, or generated from a .deb \
archive."
DESCRIPTION_${PN}-cron = "Cron scripts to control automatic system integrity \
checking via debsums."
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=770d751553e6559e9eaefd2e11ccf7e9"

SRC_URI = "http://snapshot.debian.org/archive/debian/20170530T212108Z/pool/main/d/debsums/debsums_2.2.2.tar.xz"
SRC_URI[md5sum] = "82b0710855a7e5212d4358163a269e79"
SRC_URI[sha256sum] = "aa61896f93a6bbfe0161c21dcd67529ae8e1ec8c3ccf244523c52c4ad8253d97"

# the package is taken from snapshots.debian.org; that source is static and goes stale
# so we check the latest upstream from a directory that does get updated
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/d/${BPN}/"

inherit perlnative gettext

do_install() {
        install -d ${D}/${sysconfdir}/cron.daily ${D}/${sysconfdir}/cron.weekly
        install -d ${D}/${sysconfdir}/cron.monthly ${D}${sbindir} ${D}${bindir}
        install -d ${D}${mandir}/man1 ${D}${mandir}/man8
        install -m 0755 debsums ${D}${bindir}/
        install -m 0755 rdebsums ${D}${bindir}/
        install -m 0755 debsums_init ${D}${sbindir}
        install -m 0644 man/debsums.1 ${D}${mandir}/man1/
        install -m 0644 man/rdebsums.1 ${D}${mandir}/man1/
        install -m 0644 man/debsums_init.8 ${D}${mandir}/man8/
        install -m 0644 debian/cron.daily \
                ${D}/${sysconfdir}/cron.daily/debsums
        install -m 0644 debian/cron.weekly \
                ${D}/${sysconfdir}/cron.weekly/debsums
        install -m 0644 debian/cron.monthly \
                ${D}/${sysconfdir}/cron.monthly/debsums
        # Must exist, defaults to empty.
        touch ${D}/${sysconfdir}/debsums-ignore
}

PACKAGES =+ "${PN}-cron"

RDEPENDS_${PN} = "dpkg dpkg-perl libfile-fnmatch-perl perl \
                  perl-module-constant perl-module-digest-md5 \
                  perl-module-errno perl-module-fcntl \
                  perl-module-file-basename perl-module-file-copy \
                  perl-module-file-find perl-module-file-glob \
                  perl-module-file-path perl-module-file-spec \
                  perl-module-file-temp perl-module-getopt-long \
                  perl-module-posix"

FILES_${PN}-cron = "${sysconfdir}/cron.*"
