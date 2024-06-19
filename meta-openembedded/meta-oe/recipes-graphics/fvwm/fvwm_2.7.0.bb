SUMMARY = "F Virtual Window Manager "
HOMEPAGE = "http://www.fvwm.org/"
SECTION = "x11/wm"
LICENSE = "GPL-2.0-only"
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
    file://0001-configure-Do-not-require-support-for-implicit-ints.patch \
    file://0002-acinclude.m4-Add-missing-unistd.h-to-AM_SAFETY_CHECK.patch \
    file://0003-configure-Further-defang-the-Werror-check.patch \
"

SRCREV = "7baf540e56fb1a3e91752acba872a88543529d46"

S = "${WORKDIR}/git"

inherit autotools gettext update-alternatives pkgconfig python3native perlnative features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

ALTERNATIVE:${PN} = "x-window-manager"
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
# clang treats them as errors by default now starting with 15.0+
CFLAGS += "-Wno-error=int-conversion -Wno-error=implicit-int"

do_install:append() {
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
FILES:${PN} = " \
    ${bindir}/fvwm \
    ${bindir}/fvwm-root \
    ${datadir}/fvwm/ConfigFvwmDefaults \
"

FILES:${PN}-extra = " \
    ${bindir} \
    ${libexecdir} \
    ${sysconfdir}/xdg/fvwm \
"
FILES:${PN}-doc = " \
    ${mandir} \
    ${datadir}/fvwm \
"
RDEPENDS:${PN} = " \
    xuser-account \
"
RDEPENDS:${PN}-extra += "\
    perl \
    python3-core \
"
