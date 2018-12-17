SUMMARY = "A font editor"
HOMEPAGE = "http://fontforge.github.io/en-US/"
LICENSE = "BSD-3-Clause & GPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING.gplv3;md5=d32239bcb673463ab874e80d47fae504 \
    file://LICENSE;md5=3f922b42ed0033fa0fd4cd3268f6429c \
"

DEPENDS = "glib-2.0 pango giflib tiff libxml2 jpeg python libtool uthash gnulib gettext-native"
DEPENDS_append_class-target = " libxi"

inherit autotools pkgconfig pythonnative distro_features_check gettext

REQUIRED_DISTRO_FEATURES_append_class-target = " x11"

SRC_URI = "git://github.com/${BPN}/${BPN}.git"
# tag 20170731
SRCREV = "b9149c13e8f9464fc21473f1f676b36a2130775d"
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
    ${datadir}/mime \
    ${datadir}/icons \
"

FILES_${PN}-python = "${PYTHON_SITEPACKAGES_DIR} ${datadir}/${PN}/python"
RDEPENDS_${PN}-python = "python"

# for e.g kde's oxygen-fonts
BBCLASSEXTEND = "native"
