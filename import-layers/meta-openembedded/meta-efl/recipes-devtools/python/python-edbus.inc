DESCRIPTION = "${PN} bindings"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"
AUTHOR = "Gustavo Sverzut Barbieri <barbieri@gmail.com>"
# NOTE: Due to a bug in distutils, even if we don't use pyrex but cython,
# we need to build pyrex otherwise cython doesn't get called to build
# the extension modules.
DEPENDS = "python-cython-native python-pyrex-native python-numeric eina edbus python-dbus"
RDEPENDS_${PN} += "python-lang python-dbus"

# necessary to let the call for python-config succeed
export BUILD_SYS
export HOST_SYS

inherit e-base autotools pkgconfig distutils-base

SRCNAME = "python-e_dbus"
SRCVER = "${PV}"

SRC_URI = "\
    ${E_MIRROR}/BINDINGS/python/${SRCNAME}-${SRCVER}.tar.bz2 \
"
S = "${WORKDIR}/${SRCNAME}-${SRCVER}"


do_configure_prepend() {
    # prefix CYTHON_.*_INCLUDEDIR with path to STAGING_DIR_HOST also use $PKG_CONFIG instead of pkg-config directly
    sed -i "s#=\`\$PKG_CONFIG --variable=includedir \"python-evas#=${STAGING_DIR_HOST}\`\$PKG_CONFIG --variable=includedir \"python-evas#g" ${S}/configure.ac
}

do_install_append() {
    if [ -e examples ]; then
        for i in `find examples -name "*.edc"`; do
            cd ${S}/`dirname $i`
            echo "Generating .edj file for $i..."
            edje_cc `basename $i`
            echo "Removing sources in this directory..."
            rm -f *.edc *.png *.ttf *.jpeg
        done
        cd ${S}
        install -d ${D}${datadir}/${PN}/
        cp -R --no-dereference --preserve=mode,links -v examples ${D}${datadir}/${PN}/
        find ${D}${datadir}/${PN}/examples -name ".svn" | xargs rm -rf
    fi
}

FILES_${PN}-dbg += "${libdir}/${PYTHON_DIR}/site-packages/*.egg/*/*/.debug"

PACKAGES += "${PN}-examples"
FILES_${PN}-examples = "${datadir}/${PN}/examples"
