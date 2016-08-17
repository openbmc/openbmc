HOMEPAGE = "http://gnome.org"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "libs"
LICENSE = "LGPLv2+ & GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=90d577535a3898e1ae5dbf0ae3509a8c \
                    file://tools/compiler.c;endline=20;md5=fc5007fc20022720e6c0b0cdde41fabd \
                    file://giscanner/sourcescanner.c;endline=22;md5=194d6e0c1d00662f32d030ce44de8d39 \
                    file://girepository/giregisteredtypeinfo.c;endline=21;md5=661847611ae6979465415f31a759ba27"

SRC_URI = "${GNOME_MIRROR}/${BPN}/1.46/${BPN}-${PV}.tar.xz \
           file://0001-Prefix-pkg-config-paths-with-PKG_CONFIG_SYSROOT_DIR-.patch \
           file://0001-giscanner-add-use-binary-wrapper-option.patch \
           file://0001-giscanner-add-a-use-ldd-wrapper-option.patch \
           file://0001-configure.ac-add-host-gi-gi-cross-wrapper-and-gi-ldd.patch \
           "
SRC_URI[md5sum] = "adb40a31c7c80b65b0f4c8fd71b493dc"
SRC_URI[sha256sum] = "6658bd3c2b8813eb3e2511ee153238d09ace9d309e4574af27443d87423e4233"

inherit autotools pkgconfig gtk-doc pythonnative qemu gobject-introspection-data
BBCLASSEXTEND = "native"

# necessary to let the call for python-config from configure.ac succeed
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

# needed for writing out the qemu wrapper script
export STAGING_DIR_HOST
export B

DEPENDS_append = " libffi zlib glib-2.0 python"

# target build needs qemu to run temporary introspection binaries created
# on the fly by g-ir-scanner and a native version of itself to run
# native versions of its own tools during build.
# Also prelink-rtld is used to find out library dependencies of introspection binaries
# (standard ldd doesn't work when cross-compiling).
DEPENDS_class-target_append = " gobject-introspection-native qemu-native prelink-native"

SSTATE_SCAN_FILES += "g-ir-scanner-qemuwrapper g-ir-scanner-wrapper g-ir-compiler-wrapper g-ir-scanner-lddwrapper Gio-2.0.gir"

do_configure_prepend_class-native() {
        # Tweak the native python scripts so that they don't refer to the
        # full path of native python binary (the solution is taken from glib-2.0 recipe)
        # This removes the risk of exceeding Linux kernel's shebang line limit (128 bytes)
        sed -i -e '1s,#!.*,#!${USRBINPATH}/env nativepython,' ${S}/tools/g-ir-tool-template.in
}

do_configure_prepend_class-target() {
        # Write out a qemu wrapper that will be given to gi-scanner so that it
        # can run target helper binaries through that.
        qemu_binary="${@qemu_wrapper_cmdline(d, '$STAGING_DIR_HOST', ['\$GIR_EXTRA_LIBS_PATH','.libs','$STAGING_DIR_HOST/${libdir}','$STAGING_DIR_HOST/${base_libdir}'])}"
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
# This prevents g-ir-scanner from writing cache data to $HOME
export GI_SCANNER_DISABLE_CACHE=1

g-ir-scanner --use-binary-wrapper=${STAGING_BINDIR}/g-ir-scanner-qemuwrapper --use-ldd-wrapper=${STAGING_BINDIR}/g-ir-scanner-lddwrapper --add-include-path=${STAGING_DATADIR}/gir-1.0 "\$@"
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
prelink-rtld --root=$STAGING_DIR_HOST "\$@"
EOF
        chmod +x ${B}/g-ir-scanner-lddwrapper

        # Also tweak the target python scripts so that they don't refer to the
        # native version of python binary (the solution is taken from glib-2.0 recipe)
        sed -i -e '1s,#!.*,#!${USRBINPATH}/env python,' ${S}/tools/g-ir-tool-template.in
}

# Configure target build to use native tools of itself and to use a qemu wrapper
# and optionally to generate introspection data
EXTRA_OECONF_class-target += "--enable-host-gi \
                              --enable-gi-cross-wrapper=${B}/g-ir-scanner-qemuwrapper \
                              --enable-gi-ldd-wrapper=${B}/g-ir-scanner-lddwrapper \
                              ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '--enable-introspection-data', '--disable-introspection-data', d)} \
                             "


do_compile_prepend_class-target() {
        # This prevents g-ir-scanner from writing cache data to $HOME
        export GI_SCANNER_DISABLE_CACHE=1

        # Needed to run g-ir unit tests, which won't be able to find the built libraries otherwise
        export GIR_EXTRA_LIBS_PATH=$B/.libs
}

# Our wrappers need to be available system-wide, because they will be used 
# to build introspection files for all other gobject-based packages
do_install_append_class-target() {
        install -d ${D}${bindir}/
        install ${B}/g-ir-scanner-qemuwrapper ${D}${bindir}/
        install ${B}/g-ir-scanner-wrapper ${D}${bindir}/
        install ${B}/g-ir-compiler-wrapper ${D}${bindir}/
        install ${B}/g-ir-scanner-lddwrapper ${D}${bindir}/
}

# .typelib files are needed at runtime and so they go to the main package
FILES_${PN}_append = " ${libdir}/girepository-*/*.typelib"

# .gir files go to dev package, as they're needed for developing (but not for running)
# things that depends on introspection.
FILES_${PN}-dev_append = " ${datadir}/gir-*/*.gir"

# These are used by gobject-based packages
# to generate transient introspection binaries
FILES_${PN}-dev_append = " ${datadir}/gobject-introspection-1.0/gdump.c \
               ${datadir}/gobject-introspection-1.0/Makefile.introspection"

# These are used by dependent packages (e.g. pygobject) to build their
# testsuites.
FILES_${PN}-dev_append = " ${datadir}/gobject-introspection-1.0/tests/*.c \
                   ${datadir}/gobject-introspection-1.0/tests/*.h"

FILES_${PN}-dbg += "${libdir}/gobject-introspection/giscanner/.debug/"
FILES_${PN}-staticdev += "${libdir}/gobject-introspection/giscanner/*.a"

# we need target versions of introspection tools in sysroot so that they can be run via qemu
# when building introspection files in other packages
SYSROOT_PREPROCESS_FUNCS_append_class-target += "gi_binaries_sysroot_preprocess"

gi_binaries_sysroot_preprocess() {
        sysroot_stage_dir ${D}${bindir} ${SYSROOT_DESTDIR}${bindir}

        # Also, tweak the binary names in introspection pkgconfig file, so that it picks up our 
        # wrappers which do the cross-compile and qemu magic.
        sed -i \
           -e "s|g_ir_scanner=.*|g_ir_scanner=${bindir}/g-ir-scanner-wrapper|" \
           -e "s|g_ir_compiler=.*|g_ir_compiler=${bindir}/g-ir-compiler-wrapper|" \
           ${SYSROOT_DESTDIR}${libdir}/pkgconfig/gobject-introspection-1.0.pc
}
