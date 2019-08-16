DESCRIPTION = "Ice Window Manager (IceWM)"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=4a26952467ef79a7efca4a9cf52d417b"

SRC_URI = "https://github.com/ice-wm/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.xz \
           file://0001-configure.ac-skip-running-test-program-when-cross-co.patch \
           "
SRC_URI[md5sum] = "6eba94a7935a0531d2c14eeb1426aeef"
SRC_URI[sha256sum] = "f1c1344b20a9e8635143f70ee27930b55f813c15ca61f84d77584d311b6ac027"

inherit autotools pkgconfig gettext perlnative distro_features_check qemu
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF += "--with-libdir=${datadir}/icewm \
                --with-cfgdir=${sysconfdir}/icewm \
                --with-docdir=${docdir}/icewm \
                --enable-fribidi \
                --enable-xinerama \
                --enable-shape"

DEPENDS = "asciidoc-native fontconfig gdk-pixbuf libxft libxpm libxrandr libxinerama libice libsm libx11 libxext libxrender"
DEPENDS_append = " qemu-native"
RDEPENDS_${PN} = "perl fribidi"

do_compile_prepend_class-target() {

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

FILES_${PN} += "${datadir}/xsessions"
