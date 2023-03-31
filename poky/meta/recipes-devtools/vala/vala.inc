SUMMARY = "C#-like programming language for easing GObject programming"
HOMEPAGE = "http://vala-project.org"
DESCRIPTION = "Vala is a C#-like language dedicated to ease GObject programming. \
Vala compiles to plain C and has no runtime environment nor penalities whatsoever."
SECTION = "devel"
DEPENDS = "bison-native flex-native glib-2.0"

# Appending libxslt-native to dependencies has an effect
# of rebuilding the manual, which is very slow. Let's do this
# only when api-documentation distro feature is enabled.
DEPENDS:append:class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', 'libxslt-native', '', d)}"

# vala-native contains a native version of vapigen, which we use instead of the target one
DEPENDS:append:class-target = " vala-native"
BBCLASSEXTEND = "native"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

SHRT_VER = "${@d.getVar('PV').split('.')[0]}.${@d.getVar('PV').split('.')[1]}"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/${BPN}/${SHRT_VER}/${BP}.tar.xz"
inherit autotools pkgconfig upstream-version-is-even

FILES:${PN} += "${datadir}/${BPN}-${SHRT_VER}/vapi ${libdir}/${BPN}-${SHRT_VER}/"
FILES:${PN}-doc += "${datadir}/devhelp"

# .gir files from gobject-introspection are installed to ${libdir} when multilib is enabled
GIRDIR_OPT = "${@'--girdir=${STAGING_LIBDIR}/gir-1.0' if d.getVar('MULTILIBS') else ''}"

do_configure:prepend:class-target() {
        # Write out a vapigen wrapper that will be provided by pkg-config file installed in target sysroot
        # The wrapper will call a native vapigen
        cat > ${B}/vapigen-wrapper << EOF
#!/bin/sh
vapigen-${SHRT_VER} ${GIRDIR_OPT} "\$@"
EOF
        chmod +x ${B}/vapigen-wrapper
}

EXTRA_OECONF += " --disable-valadoc"

# Vapigen wrapper needs to be available system-wide, because it will be used
# to build vapi files from all other packages with vala support
do_install:append:class-target() {
        install -d ${D}${bindir_crossscripts}/
        install ${B}/vapigen-wrapper ${D}${bindir_crossscripts}/
}

# Put vapigen wrapper into target sysroot so that it can be used when building
# vapi files.
SYSROOT_DIRS += "${bindir_crossscripts}"

inherit multilib_script
MULTILIB_SCRIPTS = "${PN}:${bindir}/vala-gen-introspect-0.56"

SYSROOT_PREPROCESS_FUNCS:append:class-target = " vapigen_sysroot_preprocess"
vapigen_sysroot_preprocess() {
        # Tweak the vapigen name in the vapigen pkgconfig file, so that it picks
        # up our wrapper.
        sed -i \
           -e "s|vapigen=.*|vapigen=${bindir_crossscripts}/vapigen-wrapper|" \
           ${SYSROOT_DESTDIR}${libdir}/pkgconfig/vapigen-${SHRT_VER}.pc
}

SSTATE_SCAN_FILES += "vapigen-wrapper"

PACKAGE_PREPROCESS_FUNCS += "vala_package_preprocess"

vala_package_preprocess () {
	rm -rf ${PKGD}${bindir_crossscripts}
}
