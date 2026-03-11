require ${BPN}.inc

DEPENDS = "alsa-lib zlib jpeg libpng libxext libxft"

inherit features_check binconfig lib_package gtk-icon-cache mime mime-xdg
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0003-fluid-CMakeLists.txt-Do-not-export-fluid-target.patch"

UPSTREAM_CHECK_URI = "https://www.fltk.org/software.php"

EXTRA_OECMAKE = " \
    -DOPTION_BUILD_SHARED_LIBS=ON \
    -DOPTION_USE_THREADS=ON \
    -DFLTK_BUILD_TEST=OFF \
    -DOPTION_USE_XDBE=ON \
    -DOPTION_USE_XFT=ON \
    -DFLTK_CONFIG_PATH=${libdir}/cmake \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'opengl', d)}"

PACKAGECONFIG[cairo] = "-DOPTION_CAIRO=ON,-DOPTION_CAIRO=OFF,cairo"
PACKAGECONFIG[opengl] = "-DOPTION_USE_GL=ON,-DOPTION_USE_GL=OFF,virtual/libgl"
PACKAGECONFIG[xinerama] = "-DOPTION_USE_XINERAMA=ON,-DOPTION_USE_XINERAMA=OFF,libxinerama"
PACKAGECONFIG[xfixes] = "-DOPTION_USE_XFIXES=ON,-DOPTION_USE_XFIXES=OFF,libxfixes"
PACKAGECONFIG[xcursor] = "-DOPTION_USE_XCURSOR=ON,-DOPTION_USE_XCURSOR=OFF,libxcursor"

do_install:append() {
    sed -i -e 's,${TMPDIR},,g' ${D}${bindir}/fltk-config
    sed -i -e 's,${TMPDIR},,g' ${D}${datadir}/fltk/UseFLTK.cmake
    sed -i -e 's,${TMPDIR},,g' ${D}${datadir}/fltk/FLTK-Targets.cmake
}

python populate_packages:prepend () {
    if (d.getVar('DEBIAN_NAMES')):
        d.setVar('PKG:${BPN}', 'libfltk${PV}')
}

LEAD_SONAME = "libfltk.so"

# .desktop / icons / mime only necessary for fluid app
FILES:${PN}-bin += " \
    ${datadir}/applications \
    ${datadir}/icons \
    ${datadir}/mime \
"

# cmake files
FILES:${PN}-dev += "${datadir}/fltk"
