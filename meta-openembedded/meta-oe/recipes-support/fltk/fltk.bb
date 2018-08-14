require ${BPN}.inc

DEPENDS = "alsa-lib zlib jpeg libpng libxext libxft"

inherit distro_features_check binconfig lib_package gtk-icon-cache mime
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0003-CMake-build-Force-shared-libs-with-unsuffixed-names.patch"

EXTRA_OECMAKE = " \
    -DOPTION_BUILD_SHARED_LIBS=ON \
    -DOPTION_USE_THREADS=ON \
    -DOPTION_USE_XDBE=ON \
    -DOPTION_USE_XFT=ON \
    -DFLTK_CONFIG_PATH=${libdir}/cmake \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'opengl', d)}"

PACKAGECONFIG[examples] = "-DOPTION_BUILD_EXAMPLES=ON,-DOPTION_BUILD_EXAMPLES=OFF,"
PACKAGECONFIG[cairo] = "-DOPTION_CAIRO=ON,-DOPTION_CAIRO=OFF,cairo"
PACKAGECONFIG[opengl] = "-DOPTION_USE_GL=ON,-DOPTION_USE_GL=OFF,virtual/libgl"
PACKAGECONFIG[xinerama] = "-DOPTION_USE_XINERAMA=ON,-DOPTION_USE_XINERAMA=OFF,libxinerama"
PACKAGECONFIG[xfixes] = "-DOPTION_USE_XFIXES=ON,-DOPTION_USE_XFIXES=OFF,libxfixes"
PACKAGECONFIG[xcursor] = "-DOPTION_USE_XCURSOR=ON,-DOPTION_USE_XCURSOR=OFF,libxcursor"

do_install_append() {
    sed -i -e 's,${STAGING_DIR_HOST},,g' ${D}${bindir}/fltk-config
}

python populate_packages_prepend () {
    if (d.getVar('DEBIAN_NAMES')):
        d.setVar('PKG_${BPN}', 'libfltk${PV}')
}

LEAD_SONAME = "libfltk.so"

# .desktop / icons / mime only necessary for fluid app
FILES_${PN}-bin += " \
    ${datadir}/applications \
    ${datadir}/icons \
    ${datadir}/mime \
"

# cmake files
FILES_${PN}-dev += "${datadir}/fltk"
