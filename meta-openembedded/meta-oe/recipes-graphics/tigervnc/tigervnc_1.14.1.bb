DESCRIPTION = "TigerVNC remote display system"
HOMEPAGE = "http://www.tigervnc.com/"
LICENSE = "GPL-2.0-or-later"
SECTION = "x11/utils"
DEPENDS = "xserver-xorg gnutls jpeg libxtst gettext-native fltk libpam"
RDEPENDS:${PN} = "coreutils hicolor-icon-theme perl bash xkbcomp"

LIC_FILES_CHKSUM = "file://LICENCE.TXT;md5=75b02c2872421380bbd47781d2bd75d3"

S = "${WORKDIR}/git"

inherit autotools cmake features_check pkgconfig

REQUIRED_DISTRO_FEATURES = "x11 pam"

B = "${S}"

SRCREV = "1b4af5c586eb7a30a38c82fd088c1fa47a83e72f"

SRC_URI = "git://github.com/TigerVNC/tigervnc.git;branch=1.14-branch;protocol=https \
           file://0001-do-not-build-tests-sub-directory.patch \
           file://0002-add-missing-dynamic-library-to-FLTK_LIBRARIES.patch \
           file://0003-tigervnc-add-fPIC-option-to-COMPILE_FLAGS.patch \
"

# Keep sync with xorg-server in oe-core
XORG_PN ?= "xorg-server"
XORG_PV ?= "21.1.15"
SRC_URI += "${XORG_MIRROR}/individual/xserver/${XORG_PN}-${XORG_PV}.tar.xz;name=xorg"
XORG_S = "${UNPACKDIR}/${XORG_PN}-${XORG_PV}"
SRC_URI[xorg.sha256sum] = "841c82901282902725762df03adbbcd68153d4cdfb0d61df0cfd73ad677ae089"

# It is the directory containing the Xorg source for the
# machine on which you are building TigerVNC.
XSERVER_SOURCE_DIR="${S}/unix/xserver"

do_patch[postfuncs] += "do_patch_xserver"
do_patch_xserver () {
    subdirs="Xext xkb GL hw/xquartz/bundle hw/xfree86/common man doc"
    for i in ${subdirs}; do
        install -d ${XSERVER_SOURCE_DIR}/$i
    done

    sources="hw/xquartz/bundle/cpprules.in man/Xserver.man doc/smartsched \
             xserver.ent.in xkb/README.compiled \
             hw/xfree86/xorgconf.cpp hw/xfree86/Xorg.sh.in"
    for i in ${sources}; do
        install -m 0644 ${XORG_S}/$i ${XSERVER_SOURCE_DIR}/$i;
    done

    cd ${XORG_S}
    find . -type f | egrep '.*\.(c|h|am|ac|inc|m4|h.in|pc.in|man.pre|pl|txt)$' | \
    xargs tar cf - | (cd ${XSERVER_SOURCE_DIR} && tar xf -)

    cd ${XSERVER_SOURCE_DIR}
    xserverpatch="${S}/unix/xserver21.patch"
    echo "Apply $xserverpatch"
    patch -p1 -b --suffix .vnc < $xserverpatch
}

EXTRA_OECONF = "--disable-xorg --disable-xnest --disable-xvfb --disable-dmx \
        --disable-xwin --disable-xephyr --disable-kdrive --with-pic \
        --disable-static --disable-xinerama \
        --with-xkb-output=${localstatedir}/lib/xkb \
        --disable-glx --disable-dri --disable-dri2 \
        --disable-config-hal \
        --disable-config-udev \
        --without-dtrace \
        --disable-unit-tests \
        --disable-devel-docs \
        --disable-selective-werror \
        --disable-xshmfence \
        --disable-config-udev \
        --disable-dri3 \
        --disable-libunwind \
        --without-xmlto \
        --enable-systemd-logind=no \
        --disable-xinerama \
        --disable-xwayland \
"

EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '-DCMAKE_INSTALL_UNITDIR=${systemd_unitdir}', '-DINSTALL_SYSTEMD_UNITS=OFF', d)}"

do_configure:append () {
    olddir=`pwd`
    cd ${XSERVER_SOURCE_DIR}

    rm -rf aclocal-copy/
    rm -f aclocal.m4

    export ACLOCALDIR="${XSERVER_SOURCE_DIR}/aclocal-copy"
    mkdir -p ${ACLOCALDIR}/
    if [ -d ${STAGING_DATADIR_NATIVE}/aclocal ]; then
        cp-noerror ${STAGING_DATADIR_NATIVE}/aclocal/ ${ACLOCALDIR}/
    fi
    if [ -d ${STAGING_DATADIR}/aclocal -a "${STAGING_DATADIR_NATIVE}/aclocal" != "${STAGING_DATADIR}/aclocal" ]; then
        cp-noerror ${STAGING_DATADIR}/aclocal/ ${ACLOCALDIR}/
    fi
    ACLOCAL="aclocal --aclocal-path=${ACLOCALDIR}/" autoreconf -Wcross --verbose --install --force ${EXTRA_AUTORECONF} $acpaths || bbfatal "autoreconf execution failed."
    chmod +x ./configure
    ${CACHED_CONFIGUREVARS} ./configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
    cd $olddir
}

do_compile:append () {
    olddir=`pwd`
    cd ${XSERVER_SOURCE_DIR}

    oe_runmake

    cd $olddir
}

do_install:append() {
    olddir=`pwd`
    cd ${XSERVER_SOURCE_DIR}/hw/vnc

    oe_runmake 'DESTDIR=${D}' install

    cd $olddir
}

FILES:${PN} += " \
    ${libdir}/xorg/modules/extensions \
    ${datadir}/icons \
    ${datadir}/metainfo \
    ${systemd_unitdir} \
"

FILES:${PN}-dbg += "${libdir}/xorg/modules/extensions/.debug"
