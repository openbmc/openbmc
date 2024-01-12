SUMMARY = "A font editor"
HOMEPAGE = "http://fontforge.github.io/en-US/"
LICENSE = "BSD-3-Clause & GPL-3.0-only"
LIC_FILES_CHKSUM = " \
    file://COPYING.gplv3;md5=d32239bcb673463ab874e80d47fae504 \
    file://LICENSE;md5=d042f3d2a8fd7208b704a499168e3c89 \
"

DEPENDS = "python3 glib-2.0 pango giflib tiff libxml2 jpeg libtool uthash gettext-native libspiro"
DEPENDS:append:class-target = " libxi"

inherit cmake pkgconfig python3native python3targetconfig features_check gettext gtk-icon-cache mime mime-xdg

REQUIRED_DISTRO_FEATURES:append:class-target = " x11"

# tag 20220308
SRCREV = "a1dad3e81da03d5d5f3c4c1c1b9b5ca5ebcfcecf"
SRC_URI = "git://github.com/${BPN}/${BPN}.git;branch=master;protocol=https \
           file://0001-include-sys-select-on-non-glibc-platforms.patch \
           file://0001-fontforgeexe-Use-env-to-find-fontforge.patch \
           file://0001-cmake-Use-alternate-way-to-detect-libm.patch \
           file://0001-Fix-Translations-containing-invalid-directives-hs.patch \
"
S = "${WORKDIR}/git"

EXTRA_OECMAKE = "-DENABLE_DOCS=OFF"
PACKAGECONFIG = "readline"
PACKAGECONFIG[readline] = "-DENABLE_READLINE=ON,-DENABLE_READLINE=OFF,readline"

CFLAGS += "-fno-strict-aliasing"
LDFLAGS += "-lpython${PYTHON_BASEVERSION}${PYTHON_ABI}"
BUILD_LDFLAGS += "-lpython${PYTHON_BASEVERSION}${PYTHON_ABI}"

#do_configure:prepend() {
# uthash sources are expected in uthash/src
#    mkdir -p ${S}/uthash/src
#    cp ${STAGING_INCDIR}/ut*.h ${S}/uthash/src
#}

PACKAGES =+ "${PN}-python"

FILES:${PN} += " \
    ${datadir}/appdata \
    ${datadir}/metainfo \
    ${datadir}/mime \
"

FILES:${PN}-python = "${PYTHON_SITEPACKAGES_DIR} ${datadir}/${BPN}/python"
RDEPENDS:${PN}-python = "python3"

# for e.g kde's oxygen-fonts
BBCLASSEXTEND = "native"
