SUMMARY = "Miscellaneous utilities specific to Debian"
DESCRIPTION = "Provides a number of small utilities which are used \
primarily by the installation scripts of Debian packages, although \
you may use them directly. "
HOMEPAGE = "https://packages.debian.org/sid/debianutils"
BUGTRACKER = "https://bugs.debian.org/cgi-bin/pkgreport.cgi?pkg=debianutils;dist=unstable"
SECTION = "base"
LICENSE = "GPLv2 & SMAIL_GPL"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=9b912cd0cc654134c0ef3424a0705b94"

SRC_URI = "http://snapshot.debian.org/archive/debian/20200929T025235Z/pool/main/d/${BPN}/${BPN}_${PV}.tar.xz"
# the package is taken from snapshots.debian.org; that source is static and goes stale
# so we check the latest upstream from a directory that does get updated
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/d/${BPN}/"

SRC_URI[sha256sum] = "3b680e81709b740387335fac8f8806d71611dcf60874e1a792e862e48a1650de"

inherit autotools update-alternatives

S = "${WORKDIR}/debianutils"
do_configure_prepend() {
    sed -i -e 's:tempfile.1 which.1:which.1:g' ${S}/Makefile.am
}

do_install_append() {
    if [ "${base_bindir}" != "${bindir}" ]; then
        # Debian places some utils into ${base_bindir} as does busybox
        install -d ${D}${base_bindir}
        for app in run-parts tempfile; do
            mv ${D}${bindir}/$app ${D}${base_bindir}/$app
        done
    fi
}

# Note that we package the update-alternatives name.
#
PACKAGES =+ "${PN}-run-parts"
FILES_${PN}-run-parts = "${base_bindir}/run-parts.debianutils"

RDEPENDS_${PN} += "${PN}-run-parts"
RDEPENDS_${PN}_class-native = ""

ALTERNATIVE_PRIORITY = "30"
ALTERNATIVE_${PN} = "add-shell installkernel remove-shell savelog tempfile which"

ALTERNATIVE_PRIORITY_${PN}-run-parts = "60"
ALTERNATIVE_${PN}-run-parts = "run-parts"

ALTERNATIVE_${PN}-doc = "which.1"
ALTERNATIVE_LINK_NAME[which.1] = "${mandir}/man1/which.1"

ALTERNATIVE_LINK_NAME[add-shell] = "${sbindir}/add-shell"
ALTERNATIVE_LINK_NAME[installkernel] = "${sbindir}/installkernel"
ALTERNATIVE_LINK_NAME[remove-shell] = "${sbindir}/remove-shell"
ALTERNATIVE_LINK_NAME[run-parts] = "${base_bindir}/run-parts"
ALTERNATIVE_LINK_NAME[savelog] = "${bindir}/savelog"
ALTERNATIVE_LINK_NAME[tempfile] = "${base_bindir}/tempfile"
ALTERNATIVE_LINK_NAME[which] = "${bindir}/which"

BBCLASSEXTEND = "native"
