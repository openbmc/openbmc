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
    git://github.com/fvwmorg/fvwm.git;protocol=https;branch=master \
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
    sed -i -e 's:${STAGING_BINDIR_NATIVE}/perl-native/perl:${USRBINPATH}/env perl:g' ${D}${bindir}/fvwm-*
    sed -i -e 's:${STAGING_BINDIR_NATIVE}/perl-native/perl:${USRBINPATH}/env perl:g' ${D}${libexecdir}/fvwm/*/Fvwm*
    sed -i -e 's:${STAGING_BINDIR_NATIVE}/python3-native/python3:${USRBINPATH}/env python3:g' ${D}${bindir}/fvwm-menu-desktop
}

# the only needed packages (note: locale packages are automatically generated
# as well)
PACKAGES = " \
    ${PN} \
    ${PN}-extra \
    ${PN}-doc \
    ${PN}-dbg \
"

# minimal set of binaries
FILES_${PN} = " \
    ${bindir}/fvwm \
    ${bindir}/fvwm-root \
    ${datadir}/fvwm/ConfigFvwmDefaults \
"

FILES_${PN}-extra = " \
    ${bindir} \
    ${libexecdir} \
    ${sysconfdir}/xdg/fvwm \
"
FILES_${PN}-doc = " \
    ${mandir} \
    ${datadir}/fvwm \
"

RDEPENDS_${PN} = " \
    xuser-account \
"
RDEPENDS_${PN}-extra += "\
    perl \
    python3-core \
"
