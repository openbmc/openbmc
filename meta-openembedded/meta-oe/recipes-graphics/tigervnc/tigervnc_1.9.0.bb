DESCRIPTION = "TigerVNC remote display system"
HOMEPAGE = "http://www.tigervnc.com/"
LICENSE = "GPLv2+"
SECTION = "x11/utils"
DEPENDS = "xserver-xorg gnutls jpeg libxtst gettext-native fltk"
RDEPENDS_${PN} = "chkconfig coreutils hicolor-icon-theme"

LIC_FILES_CHKSUM = "file://LICENCE.TXT;md5=75b02c2872421380bbd47781d2bd75d3"

S = "${WORKDIR}/git"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "x11"

inherit autotools cmake
B = "${S}"

SRCREV = "6f2301d08e64a965ad36b401ec8dc2b24bc47075"

SRC_URI = "git://github.com/TigerVNC/tigervnc.git;branch=1.9-branch \
           file://0001-tigervnc-remove-includedir.patch \
           file://0002-do-not-build-tests-sub-directory.patch \
           file://0003-add-missing-dynamic-library-to-FLTK_LIBRARIES.patch \
           file://0004-tigervnc-add-fPIC-option-to-COMPILE_FLAGS.patch \
"

# Keep sync with xorg-server in oe-core
XORG_PN ?= "xorg-server"
XORG_PV ?= "1.19.6"
SRC_URI += "${XORG_MIRROR}/individual/xserver/${XORG_PN}-${XORG_PV}.tar.bz2;name=xorg"
XORG_S = "${WORKDIR}/${XORG_PN}-${XORG_PV}"
SRC_URI[xorg.md5sum] = "3e47777ff034a331aed2322b078694a8"
SRC_URI[xorg.sha256sum] = "a732502f1db000cf36a376cd0c010ffdbf32ecdd7f1fa08ba7f5bdf9601cc197"

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
    xserverpatch="${S}/unix/xserver119.patch"
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
