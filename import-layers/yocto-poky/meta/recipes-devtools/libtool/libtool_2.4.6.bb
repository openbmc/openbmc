require libtool-${PV}.inc

RDEPENDS_${PN} += "bash"

#
# We want the results of libtool-cross preserved - don't stage anything ourselves.
#
SYSROOT_DIRS_BLACKLIST += " \
    ${bindir} \
    ${datadir}/aclocal \
    ${datadir}/libtool/build-aux \
"

do_install_append () {
        sed -e 's@--sysroot=${STAGING_DIR_HOST}@@g' \
            -e 's@${STAGING_DIR_HOST}@@g' \
            -e 's@${STAGING_DIR_NATIVE}@@g' \
            -e 's@^\(sys_lib_search_path_spec="\).*@\1${libdir} ${base_libdir}"@' \
            -e 's@^\(compiler_lib_search_dirs="\).*@\1${libdir} ${base_libdir}"@' \
            -e 's@^\(compiler_lib_search_path="\).*@\1${libdir} ${base_libdir}"@' \
            -e 's@^\(predep_objects="\).*@\1"@' \
            -e 's@^\(postdep_objects="\).*@\1"@' \
            -i ${D}${bindir}/libtool
}
