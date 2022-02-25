SUMMARY = "Helper tool used when compiling"
DESCRIPTION = "pkg-config is a helper tool used when compiling applications and libraries. It helps determined \
the correct compiler/link options.  It is also language-agnostic."
HOMEPAGE = "http://pkg-config.freedesktop.org/wiki/"
BUGTRACKER = "http://bugs.freedesktop.org/buglist.cgi?product=pkg-config"
SECTION = "console/utils"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRCREV = "d97db4fae4c1cd099b506970b285dc2afd818ea2"
PV = "0.29.2+git${SRCPV}"

SRC_URI = "git://gitlab.freedesktop.org/pkg-config/pkg-config.git;branch=master;protocol=https \
           file://pkg-config-esdk.in \
           file://pkg-config-native.in \
           file://0001-glib-gettext.m4-Update-AM_GLIB_GNU_GETTEXT-to-match-.patch \
           file://0001-autotools-remove-support-for-the-__int64-type.-See-1.patch \
           file://0001-autotools-use-C99-printf-format-specifiers-on-Window.patch \
           "

S = "${WORKDIR}/git"

inherit autotools

# Because of a faulty test, the current auto mode always evaluates to no,
# so just continue that behaviour.
#
EXTRA_OECONF += "--disable-indirect-deps"

PACKAGECONFIG ??= "glib"
PACKAGECONFIG:class-native = ""
PACKAGECONFIG:class-nativesdk = ""

PACKAGECONFIG[glib] = "--without-internal-glib,--with-internal-glib,glib-2.0 pkgconfig-native"

acpaths = "-I ."

BBCLASSEXTEND = "native nativesdk"

# Set an empty dev package to ensure the base PN package gets
# the pkg.m4 macros, pkgconfig does not deliver any other -dev
# files.
FILES:${PN}-dev = ""
FILES:${PN} += "${datadir}/aclocal/pkg.m4"

# When using the RPM generated automatic package dependencies, some packages
# will end up requiring 'pkgconfig(pkg-config)'.  Allow this behavior by
# specifying an appropriate provide.
RPROVIDES:${PN} += "pkgconfig(pkg-config)"

# Install a pkg-config-native wrapper that will use the native sysroot instead
# of the MACHINE sysroot, for using pkg-config when building native tools.
do_install:append:class-native () {
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
		ln -rs $pkgconfdir/pkg-config-esdk $pkgconfdir/pkg-config
		sed -i -e "s|^pkg-config|pkg-config.real|" $pkgconfdir/pkg-config-native
	fi
}

SSTATEPOSTUNPACKFUNCS:append:class-native = " pkgconfig_sstate_fixup_esdk"
