SUMMARY = "F Virtual Window Manager "
HOMEPAGE = "http://www.fvwm.org/"
SECTION = "x11/wm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=363fbcfb59124689af72c914560eaf6e"

DEPENDS = " \
    bison-native \
    flex-native \
    freetype-native \
    gettext-native \
    libxslt-native \
    fontconfig \
    libice \
    libpng \
    librsvg \
    libsm \
    libxau \
    libxcb \
    libxcursor \
    libxdmcp \
    libxext \
    libxfixes \
    libxft \
    libxinerama \
    libxml2 \
    libxrender \
    libxt \
    virtual/libx11 \
    xrandr \
    zlib \
"

PV = "2.6.7+git${SRCPV}"

SRC_URI = " \
    git://github.com/fvwmorg/fvwm.git;protocol=https \
    file://0001-Fix-compilation-for-disabled-gnome.patch \
    file://0002-Avoid-absolute-symlinks.patch \
"

SRCREV = "597a4e296da4f21e71a17facab297e016a3a80a8"

S = "${WORKDIR}/git"

inherit autotools gettext update-alternatives pkgconfig pythonnative perlnative distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

ALTERNATIVE_${PN} = "x-window-manager"
ALTERNATIVE_TARGET[x-window-manager] = "${bindir}/fvwm"
ALTERNATIVE_PRIORITY[x-window-manager] = "20"

EXTRA_OECONF = " \
    --disable-bidi \
    --disable-fontconfigtest \
    --disable-freetypetest \
    --disable-htmldoc \
    --disable-imlibtest \
    --disable-mandoc \
    --disable-nls \
    --disable-perllib \
    --disable-rsvg \
    --disable-shape \
    --disable-sm \
    --disable-xfttest \
    --with-imlib-exec-prefix=/nonexistent \
    --with-imlib-prefix=/nonexistent \
    --without-ncurses-library \
    --without-readline-library \
    --without-rplay-library \
    --without-stroke-library \
    --without-termcap-library \
    --without-xpm-library \
    ac_cv_func_mkstemp=no \
    has_safety_mkstemp=yes \
"

# show the exact commands in the log file
EXTRA_OEMAKE = " \
    V=1 \
"

do_install_append() {
    install -d -m 0755 ${D}/${sysconfdir}/xdg/fvwm
    # You can install the config file here

    install -d -m 0755 ${D}/${datadir}/fvwm
    touch ${D}/${datadir}/fvwm/ConfigFvwmDefaults
}

# the only needed packages (note: locale packages are automatically generated
# as well)
PACKAGES = " \
    ${PN} \
    ${PN}-dbg \
"

# minimal set of binaries
FILES_${PN} = " \
    ${bindir}/fvwm \
    ${bindir}/fvwm-root \
    ${datadir}/fvwm/ConfigFvwmDefaults \
"

RDEPENDS_${PN} = " \
    xuser-account \
"

# by default a lot of stuff is installed and it's not easy to control what to
# install, so install everything, but skip the check
INSANE_SKIP_${PN} = " \
    installed-vs-shipped \
"
