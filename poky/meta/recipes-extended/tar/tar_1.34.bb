SUMMARY = "GNU file archiving program"
DESCRIPTION = "GNU tar saves many files together into a single tape \
or disk archive, and can restore individual files from the archive."
HOMEPAGE = "http://www.gnu.org/software/tar/"
SECTION = "base"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/tar/tar-${PV}.tar.bz2 \
           file://CVE-2022-48303.patch \
"

SRC_URI[sha256sum] = "b44cc67f8a1f6b0250b7c860e952b37e8ed932a90bd9b1862a511079255646ff"

inherit autotools gettext texinfo

PACKAGECONFIG ??= ""
PACKAGECONFIG:append:class-target = " ${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)}"

PACKAGECONFIG[acl] = "--with-posix-acls,--without-posix-acls,acl"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux"

EXTRA_OECONF += "DEFAULT_RMT_DIR=${sbindir}"

CACHED_CONFIGUREVARS += "tar_cv_path_RSH=no"

# Let aclocal use the relative path for the m4 file rather than the
# absolute since tar has a lot of m4 files, otherwise there might
# be an "Argument list too long" error when it is built in a long/deep
# directory.
acpaths = "-I ./m4"

do_install () {
    autotools_do_install
    ln -s tar ${D}${bindir}/gtar
}

do_install:append:class-target() {
    if [ "${base_bindir}" != "${bindir}" ]; then
        install -d ${D}${base_bindir}
        mv ${D}${bindir}/tar ${D}${base_bindir}/tar
        mv ${D}${bindir}/gtar ${D}${base_bindir}/gtar
        rmdir ${D}${bindir}/
    fi
}

PACKAGES =+ "${PN}-rmt"

FILES:${PN}-rmt = "${sbindir}/rmt*"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "tar"
ALTERNATIVE:${PN}-rmt = "rmt"
ALTERNATIVE:${PN}:class-nativesdk = ""
ALTERNATIVE:${PN}-rmt:class-nativesdk = ""

ALTERNATIVE_LINK_NAME[tar] = "${base_bindir}/tar"
ALTERNATIVE_LINK_NAME[rmt] = "${sbindir}/rmt"

PROVIDES:append:class-native = " tar-replacement-native"
NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

BBCLASSEXTEND = "native nativesdk"

# Avoid false positives from CVEs in node-tar package
# For example CVE-2021-{32803,32804,37701,37712,37713}
CVE_PRODUCT = "gnu:tar"
