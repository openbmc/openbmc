SUMMARY = "A font editor"
HOMEPAGE = "http://fontforge.github.io/en-US/"
LICENSE = "BSD-3-Clause & GPL-3.0-only"
LIC_FILES_CHKSUM = " \
    file://COPYING.gplv3;md5=d32239bcb673463ab874e80d47fae504 \
    file://LICENSE;md5=d042f3d2a8fd7208b704a499168e3c89 \
"

DEPENDS = "python3 glib-2.0 pango giflib tiff libxml2 jpeg libtool uthash gettext-native libspiro"
DEPENDS:append:class-target = " gtkmm3"

inherit cmake pkgconfig python3native python3targetconfig features_check gettext gtk-icon-cache mime mime-xdg

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRCREV = "c41bdb922285f35defd1e1385adfd13bde1ab32a"
SRC_URI = "git://github.com/${BPN}/${BPN}.git;branch=master;protocol=https;tag=${PV} \
           file://0001-include-sys-select-on-non-glibc-platforms.patch \
           file://0001-fontforgeexe-Use-env-to-find-fontforge.patch \
           file://0001-cmake-Use-alternate-way-to-detect-libm.patch \
           file://CVE-2025-15279-1.patch \
           file://CVE-2025-15279-2.patch \
           file://CVE-2025-15275.patch \
           file://CVE-2025-15269.patch \
           file://CVE-2025-15270.patch \
           "

EXTRA_OECMAKE = "-DENABLE_DOCS=OFF"

# gui requires gtkmm3, which has no native version at the time of writing this comment
EXTRA_OECMAKE:append:class-native = " -DENABLE_GUI=OFF"

PACKAGECONFIG = "readline"
PACKAGECONFIG[readline] = "-DENABLE_READLINE=ON,-DENABLE_READLINE=OFF,readline"

CFLAGS += "-fno-strict-aliasing"
LDFLAGS += "-lpython${PYTHON_BASEVERSION}${PYTHON_ABI}"
BUILD_LDFLAGS += "-lpython${PYTHON_BASEVERSION}${PYTHON_ABI}"

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
