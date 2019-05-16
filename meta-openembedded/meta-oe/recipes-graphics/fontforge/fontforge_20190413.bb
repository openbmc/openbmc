SUMMARY = "A font editor"
HOMEPAGE = "http://fontforge.github.io/en-US/"
LICENSE = "BSD-3-Clause & GPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING.gplv3;md5=d32239bcb673463ab874e80d47fae504 \
    file://LICENSE;md5=5f9637af5c51f2e8d06385ef38eb48f1 \
"

DEPENDS = "glib-2.0 pango giflib tiff libxml2 jpeg python libtool uthash gnulib gettext-native"
DEPENDS_append_class-target = " libxi"

inherit autotools pkgconfig pythonnative distro_features_check gettext gtk-icon-cache mime

REQUIRED_DISTRO_FEATURES_append_class-target = " x11"

SRC_URI = "git://github.com/${BPN}/${BPN}.git"
# tag 20190413
SRCREV = "7f6f1d021fdfea7789972f9534ba3241616d8dfc"
S = "${WORKDIR}/git"

EXTRA_OECONF_append_class-native = " with_x=no"

do_configure_prepend() {
    # uthash sources are expected in uthash/src
    currdir=`pwd`
    cd ${S}

    mkdir -p uthash/src
    cp ${STAGING_INCDIR}/ut*.h uthash/src

    # avoid bootstrap cloning gnulib on every configure
    cat >.gitmodules <<EOF
[submodule "gnulib"]
       path = gnulib
       url = git://git.sv.gnu.org/gnulib
EOF
    cp -rf ${STAGING_DATADIR}/gnulib ${S}

    # --force to avoid errors on reconfigure e.g if recipes changed we depend on
    # | bootstrap: running: libtoolize --quiet
    # | libtoolize:   error: 'libltdl/COPYING.LIB' exists: use '--force' to overwrite
    # | ...
    ./bootstrap --force

    cd $currdir
}

PACKAGES =+ "${PN}-python"

RPROVIDES_${PN}-dbg += "${PN}-python-dbg"

FILES_${PN} += " \
    ${datadir}/appdata \
    ${datadir}/metainfo \
    ${datadir}/mime \
"

FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR} ${datadir}/${BPN}/python"
RDEPENDS_${PN}-python = "python"

# for e.g kde's oxygen-fonts
BBCLASSEXTEND = "native"
