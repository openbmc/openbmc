SUMMARY = "A font editor"
HOMEPAGE = "http://fontforge.github.io/en-US/"
LICENSE = "BSD-3-Clause & GPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING.gplv3;md5=d32239bcb673463ab874e80d47fae504 \
    file://LICENSE;md5=d042f3d2a8fd7208b704a499168e3c89 \
"

DEPENDS = "glib-2.0 pango giflib tiff libxml2 jpeg python libtool uthash gettext-native"
DEPENDS_append_class-target = " libxi"

inherit autotools pkgconfig pythonnative distro_features_check gettext gtk-icon-cache mime

REQUIRED_DISTRO_FEATURES_append_class-target = " x11"

# tag 20190801
SRCREV = "ac635b818e38ddb8e7e2e1057330a32b4e25476e"
SRC_URI = "git://github.com/${BPN}/${BPN}.git \
           file://0001-include-sys-select-on-non-glibc-platforms.patch \
"
S = "${WORKDIR}/git"

EXTRA_OECONF += "--without-libuninameslist"
EXTRA_OECONF_append_class-native = " with_x=no"

#do_configure_prepend() {
# uthash sources are expected in uthash/src
#    mkdir -p ${S}/uthash/src
#    cp ${STAGING_INCDIR}/ut*.h ${S}/uthash/src
#}

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
