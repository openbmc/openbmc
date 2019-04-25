SUMMARY = "GNU file archiving program"
DESCRIPTION = "GNU tar saves many files together into a single tape \
or disk archive, and can restore individual files from the archive."
HOMEPAGE = "http://www.gnu.org/software/tar/"
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/tar/tar-${PV}.tar.bz2 \
           file://remove-gets.patch \
           file://musl_dirent.patch \
"

SRC_URI[md5sum] = "77afa35b696c8d760331fa0e12c2fac9"
SRC_URI[sha256sum] = "577bd4463eea103bdfc662fc385789e2228dbeb399a1d0b98571ed9ce044f763"

inherit autotools gettext texinfo

PACKAGECONFIG ??= ""
PACKAGECONFIG_append_class-target = " ${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)}"

PACKAGECONFIG[acl] = "--with-posix-acls,--without-posix-acls,acl"

EXTRA_OECONF += "DEFAULT_RMT_DIR=${base_sbindir}"

# Let aclocal use the relative path for the m4 file rather than the
# absolute since tar has a lot of m4 files, otherwise there might
# be an "Argument list too long" error when it is built in a long/deep
# directory.
acpaths = "-I ./m4"

do_install () {
    autotools_do_install
    ln -s tar ${D}${bindir}/gtar
}

do_install_append_class-target() {
    if [ "${base_bindir}" != "${bindir}" ]; then
        install -d ${D}${base_bindir}
        mv ${D}${bindir}/tar ${D}${base_bindir}/tar
        mv ${D}${bindir}/gtar ${D}${base_bindir}/gtar
        rmdir ${D}${bindir}/
    fi
}

PACKAGES =+ "${PN}-rmt"

FILES_${PN}-rmt = "${base_sbindir}/rmt*"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN} = "tar"
ALTERNATIVE_${PN}-rmt = "rmt"
ALTERNATIVE_${PN}_class-nativesdk = ""
ALTERNATIVE_${PN}-rmt_class-nativesdk = ""

ALTERNATIVE_LINK_NAME[tar] = "${base_bindir}/tar"
ALTERNATIVE_LINK_NAME[rmt] = "${base_sbindir}/rmt"

PROVIDES_append_class-native = " tar-replacement-native"
NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

BBCLASSEXTEND = "native nativesdk"
