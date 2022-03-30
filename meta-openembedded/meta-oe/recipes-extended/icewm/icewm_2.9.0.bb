DESCRIPTION = "Ice Window Manager (IceWM)"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4a26952467ef79a7efca4a9cf52d417b"

SRC_URI = "https://github.com/ice-wm/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.lz \
           file://0001-configure.ac-skip-running-test-program-when-cross-co.patch \
           "
SRC_URI[sha256sum] = "c76a8c9965a1edde4f2446b47ee17c8564e0e20f3d8474465f6d4c54d1125ac4"

UPSTREAM_CHECK_URI = "https://github.com/ice-wm/${BPN}/releases"

inherit autotools pkgconfig gettext perlnative features_check qemu update-alternatives
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF += "--with-libdir=${datadir}/icewm \
                --with-cfgdir=${sysconfdir}/icewm \
                --with-docdir=${docdir}/icewm \
                --enable-fribidi \
                --enable-xinerama \
                --enable-shape"

DEPENDS = "asciidoc-native fontconfig fribidi gdk-pixbuf imlib2	libxft libxpm libxrandr \
    libxinerama libice libsm libx11 libxext libxrender libxcomposite libxdamage \
    libxfixes"
DEPENDS:append = " qemu-native"
RDEPENDS:${PN} = "perl fribidi"

do_compile:prepend:class-target() {

    cd ${B}
    oe_runmake -C src genpref

    qemu_binary="${@qemu_wrapper_cmdline(d, '${STAGING_DIR_TARGET}',['${B}/src/.libs','${STAGING_DIR_TARGET}/${libdir}','${STAGING_DIR_TARGET}/${base_libdir}'])}"
    cat >qemuwrapper <<EOF
#!/bin/sh
${qemu_binary} src/genpref "\$@"
EOF
    chmod +x qemuwrapper
    ./qemuwrapper > src/preferences
}

ALTERNATIVE:${PN} = "x-session-manager"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/icewm-session"
ALTERNATIVE_PRIORITY_${PN} = "100"

FILES:${PN} += "${datadir}/xsessions"
