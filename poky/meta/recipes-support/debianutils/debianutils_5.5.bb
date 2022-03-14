SUMMARY = "Miscellaneous utilities specific to Debian"
DESCRIPTION = "Provides a number of small utilities which are used \
primarily by the installation scripts of Debian packages, although \
you may use them directly. "
HOMEPAGE = "https://packages.debian.org/sid/debianutils"
BUGTRACKER = "https://bugs.debian.org/cgi-bin/pkgreport.cgi?pkg=debianutils;dist=unstable"
SECTION = "base"
LICENSE = "GPLv2 & SMAIL_GPL"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=9b912cd0cc654134c0ef3424a0705b94"

SRC_URI = "git://salsa.debian.org/debian/debianutils.git;protocol=https;branch=master \
           "

SRCREV = "4c420893485ad07d771c327ef899819d4846408f"

inherit autotools update-alternatives

S = "${WORKDIR}/git"

# Disable po4a (translated manpages) sub-directory, as that requires po4a to build
do_configure:prepend() {
    sed -i -e 's:po4a::g' ${S}/Makefile.am
}


do_install:append() {
    if [ "${base_bindir}" != "${bindir}" ]; then
        # Debian places some utils into ${base_bindir} as does busybox
        install -d ${D}${base_bindir}
        for app in run-parts; do
            mv ${D}${bindir}/$app ${D}${base_bindir}/$app
        done
    fi
}

# Note that we package the update-alternatives name.
#
PACKAGES =+ "${PN}-run-parts"
FILES:${PN}-run-parts = "${base_bindir}/run-parts.debianutils"

RDEPENDS:${PN} += "${PN}-run-parts"
RDEPENDS:${PN}:class-native = ""

ALTERNATIVE_PRIORITY = "30"
ALTERNATIVE:${PN} = "add-shell installkernel remove-shell savelog which"

ALTERNATIVE_PRIORITY_${PN}-run-parts = "60"
ALTERNATIVE:${PN}-run-parts = "run-parts"

ALTERNATIVE:${PN}-doc = "which.1"
ALTERNATIVE_LINK_NAME[which.1] = "${mandir}/man1/which.1"

ALTERNATIVE_LINK_NAME[add-shell] = "${sbindir}/add-shell"
ALTERNATIVE_LINK_NAME[installkernel] = "${sbindir}/installkernel"
ALTERNATIVE_LINK_NAME[remove-shell] = "${sbindir}/remove-shell"
ALTERNATIVE_LINK_NAME[run-parts] = "${base_bindir}/run-parts"
ALTERNATIVE_LINK_NAME[savelog] = "${bindir}/savelog"
ALTERNATIVE_LINK_NAME[which] = "${bindir}/which"

BBCLASSEXTEND = "native"
