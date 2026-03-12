DESCRIPTION = "TigerVNC remote display system"
HOMEPAGE = "http://www.tigervnc.com/"
LICENSE = "GPL-2.0-or-later"
SECTION = "x11/utils"
DEPENDS = "gettext-native xserver-xorg gnutls nettle jpeg pixman libxtst fltk libpam libx11 libxdamage libxfixes libxrandr xkbcomp"
RDEPENDS:${PN} = "coreutils hicolor-icon-theme perl bash xkbcomp"

LIC_FILES_CHKSUM = "file://LICENCE.TXT;md5=75b02c2872421380bbd47781d2bd75d3"


inherit autotools cmake features_check pkgconfig systemd

REQUIRED_DISTRO_FEATURES = "x11 pam"

# For ease we do in-tree builds right now. It should be possible to do
# out-of-tree builds.
B = "${S}"

SRCREV = "dc50022844dfad0a0e195d54b8499fcf242fff0c"

SRC_URI = "git://github.com/TigerVNC/tigervnc.git;branch=1.16-branch;protocol=https \
           file://0001-do-not-build-tests-sub-directory.patch \
           file://0002-add-missing-dynamic-library-to-FLTK_LIBRARIES.patch \
           file://0003-tigervnc-add-fPIC-option-to-COMPILE_FLAGS.patch \
"

# Keep sync with xorg-server in oe-core
XORG_PN ?= "xorg-server"
XORG_PV ?= "21.1.21"
SRC_URI += "${XORG_MIRROR}/individual/xserver/${XORG_PN}-${XORG_PV}.tar.xz;name=xorg"
XORG_S = "${UNPACKDIR}/${XORG_PN}-${XORG_PV}"
SRC_URI[xorg.sha256sum] = "c0cbe5545b3f645bae6024b830d1d1154a956350683a4e52b2fff5b0fa1ab519"

# It is the directory containing the Xorg source for the
# machine on which you are building TigerVNC.
XSERVER_SOURCE_DIR = "${S}/unix/xserver"
AUTOTOOLS_SCRIPT_PATH = "${XSERVER_SOURCE_DIR}"

do_patch[postfuncs] += "do_patch_xserver"
do_patch_xserver () {
    # Put the xserver source in the right place in the tigervnc source tree
    cp -rfl ${XORG_S}/* ${XSERVER_SOURCE_DIR}
    # Apply the patch to integrate the vnc server
    patch -p1 -b --suffix .vnc --directory ${XSERVER_SOURCE_DIR} <${S}/unix/xserver21.patch
}

# It is very easy to miss xserver updates in oe-core, and this recipe's xserver
# gets out of sync due to this. Hopefully this warning will help.
do_configure[prefuncs] += "do_verify_xserver_version"
do_verify_xserver_version() {
    OE_CORE_XSERVER_VERSION=$(pkg-config --modversion xorg-server)
    if [ "$OE_CORE_XSERVER_VERSION" != "${XORG_PV}" ]; then
        bbwarn "TigerVNC xorg-server version (${XORG_PV}) is different from oe-core's xorg-xserver version ($OE_CORE_XSERVER_VERSION)"
    fi
}

EXTRA_OECONF = "--disable-xorg --disable-xnest --disable-xvfb \
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
"

EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '-DCMAKE_INSTALL_UNITDIR=${systemd_system_unitdir}', '-DINSTALL_SYSTEMD_UNITS=OFF', d)}"

do_configure:append () {
    autotools_do_configure
}

do_compile:append () {
    oe_runmake 'TIGERVNC_BUILDDIR=${B}'
}

do_install:append() {
    oe_runmake 'TIGERVNC_BUILDDIR=${B}' -C ${B}/hw/vnc 'DESTDIR=${D}' install
}

FILES:${PN} += " \
    ${libdir}/xorg/modules/extensions \
    ${datadir}/icons \
    ${datadir}/metainfo \
"

#If user want to enable service of vncserver, vncserver@:<display>.service is needed and further steps are listed in unit file itself.
SYSTEMD_SERVICE:${PN} ?= "vncserver@.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

CVE_STATUS[CVE-2014-8241] = "fixed-version: The vulnerable code is not present in the used version (1.15.0)"
CVE_STATUS[CVE-2023-6377] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
CVE_STATUS[CVE-2023-6478] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
CVE_STATUS[CVE-2025-26594] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
CVE_STATUS[CVE-2025-26595] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
CVE_STATUS[CVE-2025-26596] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
CVE_STATUS[CVE-2025-26597] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
CVE_STATUS[CVE-2025-26598] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
CVE_STATUS[CVE-2025-26599] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
CVE_STATUS[CVE-2025-26600] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
CVE_STATUS[CVE-2025-26601] = "fixed-version: The vulnerable code is not present in the used xserver version (21.1.18)"
