SUMMARY = "Helper tool used when compiling"
DESCRIPTION = "pkg-config is a helper tool used when compiling applications and libraries. It helps determined \
the correct compiler/link options.  It is also language-agnostic."
HOMEPAGE = "http://pkg-config.freedesktop.org/wiki/"
BUGTRACKER = "http://bugs.freedesktop.org/buglist.cgi?product=pkg-config"
SECTION = "console/utils"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV = "edf8e6f0ea77ede073f07bff0d2ae1fc7a38103b"
PV = "0.29.2+git${SRCPV}"

SRC_URI = "git://anongit.freedesktop.org/pkg-config \
           file://pkg-config-esdk.in \
           file://pkg-config-native.in \
           file://fix-glib-configure-libtool-usage.patch \
           file://0001-glib-gettext.m4-Update-AM_GLIB_GNU_GETTEXT-to-match-.patch \
           "

S = "${WORKDIR}/git"

inherit autotools

# Because of a faulty test, the current auto mode always evaluates to no,
# so just continue that behaviour.
#
EXTRA_OECONF += "--disable-indirect-deps"

PACKAGECONFIG ??= "glib"
PACKAGECONFIG_class-native = ""
PACKAGECONFIG_class-nativesdk = ""

PACKAGECONFIG[glib] = "--without-internal-glib,--with-internal-glib,glib-2.0 pkgconfig-native"

acpaths = "-I ."

BBCLASSEXTEND = "native nativesdk"

# Set an empty dev package to ensure the base PN package gets
# the pkg.m4 macros, pkgconfig does not deliver any other -dev
# files.
FILES_${PN}-dev = ""
FILES_${PN} += "${datadir}/aclocal/pkg.m4"

# When using the RPM generated automatic package dependencies, some packages
# will end up requiring 'pkgconfig(pkg-config)'.  Allow this behavior by
# specifying an appropriate provide.
RPROVIDES_${PN} += "pkgconfig(pkg-config)"

# Install a pkg-config-native wrapper that will use the native sysroot instead
# of the MACHINE sysroot, for using pkg-config when building native tools.
do_install_append_class-native () {
    sed -e "s|@PATH_NATIVE@|${PKG_CONFIG_PATH}|" \
        -e "s|@LIBDIR_NATIVE@|${PKG_CONFIG_LIBDIR}|" \
        < ${WORKDIR}/pkg-config-native.in > ${B}/pkg-config-native
    install -m755 ${B}/pkg-config-native ${D}${bindir}/pkg-config-native
    sed -e "s|@PATH_NATIVE@|${PKG_CONFIG_PATH}|" \
        -e "s|@LIBDIR_NATIVE@|${PKG_CONFIG_LIBDIR}|" \
        < ${WORKDIR}/pkg-config-esdk.in > ${B}/pkg-config-esdk
    install -m755 ${B}/pkg-config-esdk ${D}${bindir}/pkg-config-esdk
}

pkgconfig_sstate_fixup_esdk () {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot_setscene" -a "${WITHIN_EXT_SDK}" = "1" ] ; then
		pkgconfdir="${SSTATE_INSTDIR}/recipe-sysroot-native/${bindir_native}"
		mv $pkgconfdir/pkg-config $pkgconfdir/pkg-config.real
		lnr $pkgconfdir/pkg-config-esdk $pkgconfdir/pkg-config
		sed -i -e "s|^pkg-config|pkg-config.real|" $pkgconfdir/pkg-config-native
	fi
}

SSTATEPOSTUNPACKFUNCS_append_class-native = " pkgconfig_sstate_fixup_esdk"
