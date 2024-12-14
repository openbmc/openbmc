SUMMARY = "Middleware layer between GObject-using C libraries and language bindings"
DESCRIPTION = "GObject Introspection is a project for providing machine \
readable introspection data of the API of C libraries. This introspection \
data can be used in several different use cases, for example automatic code \
generation for bindings, API verification and documentation generation."
HOMEPAGE = "https://wiki.gnome.org/action/show/Projects/GObjectIntrospection"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gobject-introspection/issues"
SECTION = "libs"
LICENSE = "LGPL-2.0-or-later & GPL-2.0-or-later & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c434e8128a68bedd59b80b2ac1eb1c4a \
                    file://tools/compiler.c;endline=20;md5=fc5007fc20022720e6c0b0cdde41fabd \
                    file://giscanner/sourcescanner.c;endline=22;md5=194d6e0c1d00662f32d030ce44de8d39 \
                    file://girepository/giregisteredtypeinfo.c;endline=21;md5=661847611ae6979465415f31a759ba27 \
                    "

SRC_URI = "${GNOME_MIRROR}/${BPN}/${@oe.utils.trim_version("${PV}", 2)}/${BPN}-${PV}.tar.xz \
           "

SRC_URI[sha256sum] = "0f5a4c1908424bf26bc41e9361168c363685080fbdb87a196c891c8401ca2f09"

SRC_URI:append:class-native = " file://0001-Relocate-the-repository-directory-for-native-builds.patch"

inherit meson pkgconfig gtk-doc python3targetconfig qemu gobject-introspection-data upstream-version-is-even multilib_script

GTKDOC_MESON_OPTION = "gtk_doc"

MULTILIB_SCRIPTS = "${PN}:${bindir}/g-ir-annotation-tool ${PN}:${bindir}/g-ir-scanner"

# setuptools are required to provide distutils to build the tools
DEPENDS += " libffi zlib python3 python3-setuptools flex-native bison-native"
DEPENDS:append:class-native = " glib-2.0"
DEPENDS:append:class-target = " glib-2.0-initial"

# target build needs qemu to run temporary introspection binaries created
# on the fly by g-ir-scanner and a native version of itself to run
# native versions of its own tools during build.
DEPENDS:append:class-target = " gobject-introspection-native qemu-native"

# needed for writing out the qemu wrapper script
export STAGING_DIR_HOST
export B

PACKAGECONFIG ?= ""
PACKAGECONFIG[doctool] = "-Ddoctool=enabled,-Ddoctool=disabled,python3-mako,"

# Configure target build to use native tools of itself and to use a qemu wrapper
# and optionally to generate introspection data
EXTRA_OEMESON:class-target = " \
    -Dgi_cross_use_prebuilt_gi=true \
    -Dgi_cross_binary_wrapper=${B}/g-ir-scanner-qemuwrapper \
    -Dgi_cross_ldd_wrapper=${B}/g-ir-scanner-lddwrapper \
    -Dgi_cross_pkgconfig_sysroot_path=${PKG_CONFIG_SYSROOT_DIR} \
    ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '-Dbuild_introspection_data=true', '-Dbuild_introspection_data=false', d)} \
    ${@'-Dgir_dir_prefix=${libdir}' if d.getVar('MULTILIBS') else ''} \
"

do_configure:prepend:class-native() {
        # Tweak the native python scripts so that they don't refer to the
        # full path of native python binary (the solution is taken from glib-2.0 recipe)
        # This removes the risk of exceeding Linux kernel's shebang line limit (128 bytes)
        sed -i -e '1s,#!.*,#!${USRBINPATH}/env python3,' ${S}/tools/g-ir-tool-template.in
}

do_configure:prepend:class-target() {
        # Write out a qemu wrapper that will be given to gi-scanner so that it
        # can run target helper binaries through that.
        qemu_binary="${@qemu_wrapper_cmdline(d, '$STAGING_DIR_HOST', ['\\$GIR_EXTRA_LIBS_PATH','.libs','$STAGING_DIR_HOST/${libdir}','$STAGING_DIR_HOST/${base_libdir}'])}"
        cat > ${B}/g-ir-scanner-qemuwrapper << EOF
#!/bin/sh
# Use a modules directory which doesn't exist so we don't load random things
# which may then get deleted (or their dependencies) and potentially segfault
export GIO_MODULE_DIR=${STAGING_LIBDIR}/gio/modules-dummy

$qemu_binary "\$@"
if [ \$? -ne 0 ]; then
    echo "If the above error message is about missing .so libraries, then setting up GIR_EXTRA_LIBS_PATH in the recipe should help."
    echo "(typically like this: GIR_EXTRA_LIBS_PATH=\"$""{B}/something/.libs\" )"
    exit 1
fi
EOF
        chmod +x ${B}/g-ir-scanner-qemuwrapper

        # Write out a wrapper for g-ir-scanner itself, which will be used when building introspection files
        # for glib-based packages. This wrapper calls the native version of the scanner, and tells it to use
        # a qemu wrapper for running transient target binaries produced by the scanner, and an include directory
        # from the target sysroot.
        cat > ${B}/g-ir-scanner-wrapper << EOF
#!/bin/sh
# This prevents g-ir-scanner from writing cache data to user's HOME dir
export GI_SCANNER_DISABLE_CACHE=1

g-ir-scanner --lib-dirs-envvar=GIR_EXTRA_LIBS_PATH --use-binary-wrapper=${STAGING_BINDIR}/g-ir-scanner-qemuwrapper --use-ldd-wrapper=${STAGING_BINDIR}/g-ir-scanner-lddwrapper --add-include-path=${STAGING_DATADIR}/gir-1.0 --add-include-path=${STAGING_LIBDIR}/gir-1.0 "\$@"
EOF
        chmod +x ${B}/g-ir-scanner-wrapper

        # Write out a wrapper for g-ir-compiler, which runs the target version of it through qemu.
        # g-ir-compiler writes out the raw content of a C struct to disk, and therefore is architecture dependent.
        cat > ${B}/g-ir-compiler-wrapper << EOF
#!/bin/sh
${STAGING_BINDIR}/g-ir-scanner-qemuwrapper ${STAGING_BINDIR}/g-ir-compiler "\$@"
EOF
        chmod +x ${B}/g-ir-compiler-wrapper

        # Write out a wrapper to use instead of ldd, which does not work when a binary is built
        # for a different architecture
        cat > ${B}/g-ir-scanner-lddwrapper << EOF
#!/bin/sh
\$OBJDUMP -p "\$@"
EOF
        chmod +x ${B}/g-ir-scanner-lddwrapper

        # Also tweak the target python scripts so that they don't refer to the
        # native version of python binary (the solution is taken from glib-2.0 recipe)
        sed -i -e '1s,#!.*,#!${USRBINPATH}/env python3,' ${S}/tools/g-ir-tool-template.in
}

do_compile:prepend() {
        # Needed to run g-ir unit tests, which won't be able to find the built libraries otherwise
        export GIR_EXTRA_LIBS_PATH=$B/.libs
}

do_install:prepend() {
        # This prevents g-ir-scanner from writing cache data to $HOME
        export GI_SCANNER_DISABLE_CACHE=1
}

# Our wrappers need to be available system-wide, because they will be used
# to build introspection files for all other gobject-based packages
do_install:append:class-target() {
        install -d ${D}${bindir}/
        install ${B}/g-ir-scanner-qemuwrapper ${D}${bindir}/
        install ${B}/g-ir-scanner-wrapper ${D}${bindir}/
        install ${B}/g-ir-compiler-wrapper ${D}${bindir}/
        install ${B}/g-ir-scanner-lddwrapper ${D}${bindir}/
}

# we need target versions of introspection tools in sysroot so that they can be run via qemu
# when building introspection files in other packages
SYSROOT_DIRS:append:class-target = " ${bindir}"

SYSROOT_PREPROCESS_FUNCS:append:class-target = " gi_binaries_sysroot_preprocess"
gi_binaries_sysroot_preprocess() {
        # Tweak the binary names in the introspection pkgconfig file, so that it
        # picks up our wrappers which do the cross-compile and qemu magic.
        sed -i \
           -e "s|g_ir_scanner=.*|g_ir_scanner=${bindir}/g-ir-scanner-wrapper|" \
           -e "s|g_ir_compiler=.*|g_ir_compiler=${bindir}/g-ir-compiler-wrapper|" \
           ${SYSROOT_DESTDIR}${libdir}/pkgconfig/gobject-introspection-1.0.pc
}

SYSROOT_PREPROCESS_FUNCS:append = " gi_ldsoconf_sysroot_preprocess"
gi_ldsoconf_sysroot_preprocess () {
	mkdir -p ${SYSROOT_DESTDIR}${bindir}
	dest=${SYSROOT_DESTDIR}${bindir}/postinst-ldsoconf-${PN}
	echo "#!/bin/sh" > $dest
	echo "mkdir -p ${STAGING_DIR_TARGET}${sysconfdir}" >> $dest
	echo "echo ${base_libdir} >> ${STAGING_DIR_TARGET}${sysconfdir}/ld.so.conf" >> $dest
	echo "echo ${libdir} >> ${STAGING_DIR_TARGET}${sysconfdir}/ld.so.conf" >> $dest
	chmod 755 $dest
}

# Remove wrapper files from the package, only used for cross-compiling
PACKAGE_PREPROCESS_FUNCS += "gi_package_preprocess"
gi_package_preprocess() {
	rm -f ${PKGD}${bindir}/g-ir-scanner-qemuwrapper
	rm -f ${PKGD}${bindir}/g-ir-scanner-wrapper
	rm -f ${PKGD}${bindir}/g-ir-compiler-wrapper
	rm -f ${PKGD}${bindir}/g-ir-scanner-lddwrapper
}

SSTATE_SCAN_FILES += "g-ir-scanner-qemuwrapper g-ir-scanner-wrapper g-ir-compiler-wrapper g-ir-scanner-lddwrapper Gio-2.0.gir postinst-ldsoconf-${PN}"

PACKAGES =+ "\
    ${PN}-tools \
"

FILES:${PN}-tools = "\
    ${bindir} \
    ${libdir}/gobject-introspection/giscanner \
"

# .typelib files are needed at runtime and so they go to the main package
FILES:${PN}:append = " ${libdir}/girepository-*/*.typelib"

# .gir files go to dev package, as they're needed for developing (but not for running)
# things that depends on introspection.
FILES:${PN}-dev:append = " ${datadir}/gir-*/*.gir ${libdir}/gir-*/*.gir"
FILES:${PN}-dev:append = " ${datadir}/gir-*/*.rnc"

# These are used by gobject-based packages
# to generate transient introspection binaries
FILES:${PN}-dev:append = " ${datadir}/gobject-introspection-1.0/gdump.c \
                           ${datadir}/gobject-introspection-1.0/Makefile.introspection"

# These are used by dependent packages (e.g. pygobject) to build their
# testsuites.
FILES:${PN}-dev:append = " ${datadir}/gobject-introspection-1.0/tests/*.c \
                           ${datadir}/gobject-introspection-1.0/tests/*.h"

FILES:${PN}-dbg += "${libdir}/gobject-introspection/giscanner/.debug/"
FILES:${PN}-staticdev += "${libdir}/gobject-introspection/giscanner/*.a"

# glib-2.0 is required for libgirepository
RDEPENDS:${PN} = "glib-2.0"

# setuptools can be removed when upstream removes all uses of distutils
RDEPENDS:${PN}-tools = "\
    glib-2.0 \
    python3-pickle \
    python3-setuptools \
    python3-xml \
"

BBCLASSEXTEND = "native"
