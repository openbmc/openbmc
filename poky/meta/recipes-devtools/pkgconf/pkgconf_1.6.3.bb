SUMMARY = "pkgconf provides compiler and linker configuration for development frameworks."
DESCRIPTION = "pkgconf is a program which helps to configure compiler and linker \
flags for development frameworks. It is similar to pkg-config from \
freedesktop.org, providing additional functionality while also maintaining \
compatibility."
HOMEPAGE = "http://pkgconf.org"
BUGTRACKER = "https://github.com/pkgconf/pkgconf/issues"
SECTION = "devel"
PROVIDES += "pkgconfig"
RPROVIDES_${PN} += "pkgconfig"

# The pkgconf license seems to be functionally equivalent to BSD-2-Clause or
# ISC, but has different wording, so needs its own name.
LICENSE = "pkgconf"
LIC_FILES_CHKSUM = "file://COPYING;md5=2214222ec1a820bd6cc75167a56925e0"

SRC_URI = "\
    https://distfiles.dereferenced.org/pkgconf/pkgconf-${PV}.tar.xz \
    file://pkg-config-wrapper \
    file://pkg-config-native.in \
    file://pkg-config-esdk.in \
"
SRC_URI[md5sum] = "f93fb1be95a5cb62e43c219c82b5791a"
SRC_URI[sha256sum] = "61f0b31b0d5ea0e862b454a80c170f57bad47879c0c42bd8de89200ff62ea210"

inherit autotools

EXTRA_OECONF += "--with-pkg-config-dir='${libdir}/pkgconfig:${datadir}/pkgconfig'"

do_install_append () {
    # Install a wrapper which deals, as much as possible with pkgconf vs
    # pkg-config compatibility issues.
    install -m 0755 "${WORKDIR}/pkg-config-wrapper" "${D}${bindir}/pkg-config"
}

do_install_append_class-native () {
    # Install a pkg-config-native wrapper that will use the native sysroot instead
    # of the MACHINE sysroot, for using pkg-config when building native tools.
    sed -e "s|@PATH_NATIVE@|${PKG_CONFIG_PATH}|" \
        < ${WORKDIR}/pkg-config-native.in > ${B}/pkg-config-native
    install -m755 ${B}/pkg-config-native ${D}${bindir}/pkg-config-native
    sed -e "s|@PATH_NATIVE@|${PKG_CONFIG_PATH}|" \
        -e "s|@LIBDIR_NATIVE@|${PKG_CONFIG_LIBDIR}|" \
        < ${WORKDIR}/pkg-config-esdk.in > ${B}/pkg-config-esdk
    install -m755 ${B}/pkg-config-esdk ${D}${bindir}/pkg-config-esdk
}

# When using the RPM generated automatic package dependencies, some packages
# will end up requiring 'pkgconfig(pkg-config)'.  Allow this behavior by
# specifying an appropriate provide.
RPROVIDES_${PN} += "pkgconfig(pkg-config)"

# Include pkg.m4 in the main package, leaving libpkgconf dev files in -dev
FILES_${PN}-dev_remove = "${datadir}/aclocal"
FILES_${PN} += "${datadir}/aclocal"

BBCLASSEXTEND += "native nativesdk"

pkgconf_sstate_fixup_esdk () {
   if [ "${BB_CURRENTTASK}" = "populate_sysroot_setscene" -a "${WITHIN_EXT_SDK}" = "1" ] ; then
       pkgconfdir="${SSTATE_INSTDIR}/recipe-sysroot-native/${bindir_native}"
       mv $pkgconfdir/pkg-config $pkgconfdir/pkg-config.real
       lnr $pkgconfdir/pkg-config-esdk $pkgconfdir/pkg-config
       sed -i -e "s|^pkg-config|pkg-config.real|" $pkgconfdir/pkg-config-native
   fi
}

SSTATEPOSTUNPACKFUNCS_append_class-native = " pkgconf_sstate_fixup_esdk"
