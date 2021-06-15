SUMMARY = "F Virtual Window Manager "
HOMEPAGE = "http://www.fvwm.org/"
SECTION = "x11/wm"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f8204787357db6ea518dcc9b6cf08388"

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

SRC_URI = " \
    git://github.com/fvwmorg/fvwm.git;protocol=https \
    file://0001-Fix-compilation-for-disabled-gnome.patch \
"

SRCREV = "88eab6dc16da6e5dd25fe97fbb56b96ef0d58657"

S = "${WORKDIR}/git"

inherit autotools gettext update-alternatives pkgconfig python3native perlnative features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

ALTERNATIVE_${PN} = "x-window-manager"
ALTERNATIVE_TARGET[x-window-manager] = "${bindir}/fvwm"
ALTERNATIVE_PRIORITY[x-window-manager] = "20"

EXTRA_OECONF = " \
    --disable-bidi \
    --disable-fontconfigtest \
    --disable-htmldoc \
    --disable-imlibtest \
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
