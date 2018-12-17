# Inherit this class in recipes to enable building their introspection files

# python3native is inherited to prevent introspection tools being run with
# host's python 3 (they need to be run with native python 3)
#
# This also sets up autoconf-based recipes to build introspection data (or not),
# depending on distro and machine features (see gobject-introspection-data class).
inherit python3native gobject-introspection-data
EXTRA_OECONF_prepend_class-target = "${@bb.utils.contains('GI_DATA_ENABLED', 'True', '--enable-introspection', '--disable-introspection', d)} "

# When building native recipes, disable introspection, as it is not necessary,
# pulls in additional dependencies, and makes build times longer
EXTRA_OECONF_prepend_class-native = "--disable-introspection "
EXTRA_OECONF_prepend_class-nativesdk = "--disable-introspection "

UNKNOWN_CONFIGURE_WHITELIST_append = " --enable-introspection --disable-introspection"

# Generating introspection data depends on a combination of native and target
# introspection tools, and qemu to run the target tools.
DEPENDS_append_class-target = " gobject-introspection gobject-introspection-native qemu-native prelink-native"

# Even though introspection is disabled on -native, gobject-introspection package is still
# needed for m4 macros.
DEPENDS_append_class-native = " gobject-introspection-native"
DEPENDS_append_class-nativesdk = " gobject-introspection-native"

# This is used by introspection tools to find .gir includes
export XDG_DATA_DIRS = "${STAGING_DATADIR}"

do_configure_prepend_class-target () {
    # introspection.m4 pre-packaged with upstream tarballs does not yet
    # have our fixes
    mkdir -p ${S}/m4
    cp ${STAGING_DIR_TARGET}/${datadir}/aclocal/introspection.m4 ${S}/m4
}

# .typelib files are needed at runtime and so they go to the main package (so
# they'll be together with libraries they support).
FILES_${PN}_append = " ${libdir}/girepository-*/*.typelib" 
    
# .gir files go to dev package, as they're needed for developing (but not for
# running) things that depends on introspection.
FILES_${PN}-dev_append = " ${datadir}/gir-*/*.gir ${libdir}/gir-*/*.gir"
