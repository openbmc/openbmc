DESCRIPTION = "TigerVNC remote display system"
HOMEPAGE = "http://www.tigervnc.com/"
LICENSE = "GPLv2+"
SECTION = "x11/utils"
DEPENDS = "xserver-xorg gnutls jpeg libxtst gettext-native fltk"
RDEPENDS_${PN} = "coreutils hicolor-icon-theme perl"

LIC_FILES_CHKSUM = "file://LICENCE.TXT;md5=75b02c2872421380bbd47781d2bd75d3"

S = "${WORKDIR}/git"

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11"

inherit autotools cmake
B = "${S}"

SRCREV = "4739493b635372bd40a34640a719f79fa90e4dba"

SRC_URI = "git://github.com/TigerVNC/tigervnc.git;branch=1.10-branch;protocol=https \
           file://0002-do-not-build-tests-sub-directory.patch \
           file://0003-add-missing-dynamic-library-to-FLTK_LIBRARIES.patch \
           file://0004-tigervnc-add-fPIC-option-to-COMPILE_FLAGS.patch \
"

# Keep sync with xorg-server in oe-core
XORG_PN ?= "xorg-server"
XORG_PV ?= "1.20.6"
SRC_URI += "${XORG_MIRROR}/individual/xserver/${XORG_PN}-${XORG_PV}.tar.bz2;name=xorg"
XORG_S = "${WORKDIR}/${XORG_PN}-${XORG_PV}"
SRC_URI[xorg.md5sum] = "a98170084f2c8fed480d2ff601f8a14b"
SRC_URI[xorg.sha256sum] = "6316146304e6e8a36d5904987ae2917b5d5b195dc9fc63d67f7aca137e5a51d1"

# It is the directory containing the Xorg source for the
# machine on which you are building TigerVNC.
XSERVER_SOURCE_DIR="${S}/unix/xserver"

do_patch[postfuncs] += "do_patch_xserver"
do_patch_xserver () {
    for subdir in Xext xkb GL hw/xquartz/bundle hw/xfree86/common; do
        install -d ${XSERVER_SOURCE_DIR}/$subdir
    done

    for subdir in hw/dmx/doc man doc hw/dmx/doxygen; do
        install -d ${XSERVER_SOURCE_DIR}/$subdir
    done

    sources="hw/xquartz/bundle/cpprules.in man/Xserver.man doc/smartsched \
             hw/dmx/doxygen/doxygen.conf.in xserver.ent.in xkb/README.compiled \
             hw/xfree86/xorgconf.cpp hw/xfree86/Xorg.sh.in"
    for i in ${sources}; do
        install -m 0644 ${XORG_S}/$i ${XSERVER_SOURCE_DIR}/$i;
    done

    cd ${XORG_S}
    find . -type f | egrep '.*\.(c|h|am|ac|inc|m4|h.in|pc.in|man.pre|pl|txt)$' | \
    xargs tar cf - | (cd ${XSERVER_SOURCE_DIR} && tar xf -)

    cd ${XSERVER_SOURCE_DIR}
    xserverpatch="${S}/unix/xserver120.patch"
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

do_configure_append () {
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
    ACLOCAL="aclocal --system-acdir=${ACLOCALDIR}/" autoreconf -Wcross --verbose --install --force ${EXTRA_AUTORECONF} $acpaths || bbfatal "autoreconf execution failed."
    chmod +x ./configure
    ${CACHED_CONFIGUREVARS} ./configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
    cd $olddir
}

do_compile_append () {
    olddir=`pwd`
    cd ${XSERVER_SOURCE_DIR}

    oe_runmake

    cd $olddir
}

do_install_append() {
    olddir=`pwd`
    cd ${XSERVER_SOURCE_DIR}/hw/vnc

    oe_runmake 'DESTDIR=${D}' install

    cd $olddir
}

FILES_${PN} += " \
    ${libdir}/xorg/modules/extensions \
    ${datadir}/icons \
"

FILES_${PN}-dbg += "${libdir}/xorg/modules/extensions/.debug"
