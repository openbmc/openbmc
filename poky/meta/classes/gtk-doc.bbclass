# Helper class to pull in the right gtk-doc dependencies and configure
# gtk-doc to enable or disable documentation building (which requries the
# use of usermode qemu).

# This variable is set to True if api-documentation is in
# DISTRO_FEATURES and qemu-usermode is in MACHINE_FEATURES, and False otherwise.
#
# It should be used in recipes to determine whether gtk-doc based documentation should be built,
# so that qemu use can be avoided when necessary.
GTKDOC_ENABLED ?= "${@bb.utils.contains('DISTRO_FEATURES', 'api-documentation', \
                      bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'True', 'False', d), 'False', d)}"

EXTRA_OECONF_prepend_class-target = "${@bb.utils.contains('GTKDOC_ENABLED', 'True', '--enable-gtk-doc --enable-gtk-doc-html --disable-gtk-doc-pdf', \
                                                                                    '--disable-gtk-doc', d)} "

# When building native recipes, disable gtkdoc, as it is not necessary,
# pulls in additional dependencies, and makes build times longer
EXTRA_OECONF_prepend_class-native = "--disable-gtk-doc "
EXTRA_OECONF_prepend_class-nativesdk = "--disable-gtk-doc "

# Even though gtkdoc is disabled on -native, gtk-doc package is still
# needed for m4 macros.
DEPENDS_append = " gtk-doc-native"

# The documentation directory, where the infrastructure will be copied.
# gtkdocize has a default of "." so to handle out-of-tree builds set this to $S.
GTKDOC_DOCDIR ?= "${S}"

export STAGING_DIR_HOST

inherit python3native pkgconfig qemu
DEPENDS_append = "${@' qemu-native' if d.getVar('GTKDOC_ENABLED') == 'True' else ''}"

do_configure_prepend () {
	# Need to use ||true as this is only needed if configure.ac both exists
	# and uses GTK_DOC_CHECK.
	gtkdocize --srcdir ${S} --docdir ${GTKDOC_DOCDIR} || true
}

do_compile_prepend_class-target () {
    if [ ${GTKDOC_ENABLED} = True ]; then
        # Write out a qemu wrapper that will be given to gtkdoc-scangobj so that it
        # can run target helper binaries through that.
        qemu_binary="${@qemu_wrapper_cmdline(d, '$STAGING_DIR_HOST', ['\$GIR_EXTRA_LIBS_PATH','$STAGING_DIR_HOST/${libdir}','$STAGING_DIR_HOST/${base_libdir}'])}"
        cat > ${B}/gtkdoc-qemuwrapper << EOF
#!/bin/sh
# Use a modules directory which doesn't exist so we don't load random things
# which may then get deleted (or their dependencies) and potentially segfault
export GIO_MODULE_DIR=${STAGING_LIBDIR}/gio/modules-dummy

GIR_EXTRA_LIBS_PATH=\`find ${B} -name *.so -printf "%h\n"|sort|uniq| tr '\n' ':'\`\$GIR_EXTRA_LIBS_PATH
GIR_EXTRA_LIBS_PATH=\`find ${B} -name .libs| tr '\n' ':'\`\$GIR_EXTRA_LIBS_PATH

if [ -d ".libs" ]; then
    $qemu_binary ".libs/\$@"
else
    $qemu_binary "\$@"
fi

if [ \$? -ne 0 ]; then
    echo "If the above error message is about missing .so libraries, then setting up GIR_EXTRA_LIBS_PATH in the recipe should help."
    echo "(typically like this: GIR_EXTRA_LIBS_PATH=\"$""{B}/something/.libs\" )"
    exit 1
fi
EOF
        chmod +x ${B}/gtkdoc-qemuwrapper
    fi
}
