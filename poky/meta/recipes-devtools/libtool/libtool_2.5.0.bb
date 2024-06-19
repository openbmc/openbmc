require libtool-${PV}.inc

SRC_URI += "file://multilib.patch"

RDEPENDS:${PN} += "bash"

#
# We want the results of libtool-cross preserved - don't stage anything ourselves.
#
SYSROOT_DIRS_IGNORE += " \
    ${bindir} \
    ${datadir}/aclocal \
    ${datadir}/libtool/build-aux \
"

ACLOCALEXTRAPATH:class-target = ""

do_install:append () {
        sed -e 's@--sysroot=${STAGING_DIR_HOST}@@g' \
            -e "s@${DEBUG_PREFIX_MAP}@@g" \
            -e 's@${STAGING_DIR_HOST}@@g' \
            -e 's@${STAGING_DIR_NATIVE}@@g' \
            -e 's@^\(sys_lib_search_path_spec="\).*@\1${libdir} ${base_libdir}"@' \
            -e 's@^\(compiler_lib_search_dirs="\).*@\1${libdir} ${base_libdir}"@' \
            -e 's@^\(compiler_lib_search_path="\).*@\1${libdir} ${base_libdir}"@' \
            -e 's@^\(predep_objects="\).*@\1"@' \
            -e 's@^\(postdep_objects="\).*@\1"@' \
            -e "s@${HOSTTOOLS_DIR}/@@g" \
            -i ${D}${bindir}/libtool
}

inherit multilib_script

MULTILIB_SCRIPTS = "${PN}:${bindir}/libtool"
