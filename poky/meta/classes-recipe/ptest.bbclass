#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

SUMMARY:${PN}-ptest ?= "${SUMMARY} - Package test files"
DESCRIPTION:${PN}-ptest ?= "${DESCRIPTION}  \
This package contains a test directory ${PTEST_PATH} for package test purposes."

PTEST_PATH ?= "${libdir}/${BPN}/ptest"
PTEST_BUILD_HOST_FILES ?= "Makefile"
PTEST_BUILD_HOST_PATTERN ?= ""
PTEST_PARALLEL_MAKE ?= "${PARALLEL_MAKE}"
PTEST_PARALLEL_MAKEINST ?= "${PARALLEL_MAKEINST}"
EXTRA_OEMAKE:prepend:task-compile-ptest-base = "${PTEST_PARALLEL_MAKE} "
EXTRA_OEMAKE:prepend:task-install-ptest-base = "${PTEST_PARALLEL_MAKEINST} "

FILES:${PN}-ptest += "${PTEST_PATH}"
SECTION:${PN}-ptest = "devel"
ALLOW_EMPTY:${PN}-ptest = "1"
PTEST_ENABLED = "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', '1', '0', d)}"
PTEST_ENABLED:class-native = ""
PTEST_ENABLED:class-nativesdk = ""
PTEST_ENABLED:class-cross-canadian = ""
RDEPENDS:${PN}-ptest += "${PN}"
RDEPENDS:${PN}-ptest:class-native = ""
RDEPENDS:${PN}-ptest:class-nativesdk = ""
RRECOMMENDS:${PN}-ptest += "ptest-runner"

PACKAGES =+ "${@bb.utils.contains('PTEST_ENABLED', '1', '${PN}-ptest', '', d)}"

require conf/distro/include/ptest-packagelists.inc

do_configure_ptest() {
    :
}

do_configure_ptest_base() {
    do_configure_ptest
}

do_compile_ptest() {
    :
}

do_compile_ptest_base() {
    do_compile_ptest
}

do_install_ptest() {
    :
}

do_install_ptest_base() {
    if [ -f ${UNPACKDIR}/run-ptest ]; then
        install -D ${UNPACKDIR}/run-ptest ${D}${PTEST_PATH}/run-ptest
    fi

    grep -q install-ptest: Makefile 2>/dev/null && oe_runmake DESTDIR=${D}${PTEST_PATH} install-ptest

    do_install_ptest
    chown -R root:root ${D}${PTEST_PATH}

    # Strip build host paths from any installed Makefile
    for filename in ${PTEST_BUILD_HOST_FILES}; do
        for installed_ptest_file in $(find ${D}${PTEST_PATH} -type f -name $filename); do
            bbnote "Stripping host paths from: $installed_ptest_file"
            sed -e 's#${HOSTTOOLS_DIR}/*##g' \
                -e 's#${WORKDIR}/*=#.=#g' \
                -e 's#${WORKDIR}/*##g' \
                -i $installed_ptest_file
            if [ -n "${PTEST_BUILD_HOST_PATTERN}" ]; then
               sed -E '/${PTEST_BUILD_HOST_PATTERN}/d' \
                   -i $installed_ptest_file
            fi
        done
    done
}

PTEST_BINDIR_PKGD_PATH = "${PKGD}${PTEST_PATH}/bin"

# This function needs to run after apply_update_alternative_renames because the
# aforementioned function will update the ALTERNATIVE_LINK_NAME flag. Append is
# used here to make this function to run as late as possible.
PACKAGE_PREPROCESS_FUNCS:append = "${@bb.utils.contains('PTEST_BINDIR', '1', \
                                    bb.utils.contains('PTEST_ENABLED', '1', ' ptest_update_alternatives', '', d), '', d)}"

python ptest_update_alternatives() {
    """
    This function will generate the symlinks in the PTEST_BINDIR_PKGD_PATH
    to match the renamed binaries by update-alternatives.
    """

    if not bb.data.inherits_class('update-alternatives', d) \
           or not update_alternatives_enabled(d):
        return

    bb.note("Generating symlinks for ptest")
    bin_paths = { d.getVar("bindir"), d.getVar("base_bindir"),
                   d.getVar("sbindir"), d.getVar("base_sbindir") }
    ptest_bindir = d.getVar("PTEST_BINDIR_PKGD_PATH")
    os.mkdir(ptest_bindir)
    for pkg in (d.getVar('PACKAGES') or "").split():
        alternatives = update_alternatives_alt_targets(d, pkg)
        for alt_name, alt_link, alt_target, _ in alternatives:
            # Some alternatives are for man pages,
            # check if the alternative is in PATH
            if os.path.dirname(alt_link) in bin_paths:
                os.symlink(alt_target, os.path.join(ptest_bindir, alt_name))
}

do_configure_ptest_base[dirs] = "${B}"
do_compile_ptest_base[dirs] = "${B}"
do_install_ptest_base[dirs] = "${B}"
do_install_ptest_base[cleandirs] = "${D}${PTEST_PATH}"

addtask configure_ptest_base after do_configure before do_compile
addtask compile_ptest_base   after do_compile   before do_install
addtask install_ptest_base   after do_install   before do_package do_populate_sysroot

python () {
    if not bb.data.inherits_class('native', d) and not bb.data.inherits_class('cross', d):
        d.setVarFlag('do_install_ptest_base', 'fakeroot', '1')
        d.setVarFlag('do_install_ptest_base', 'umask', '022')

    # Remove all '*ptest_base' tasks when ptest is not enabled
    if not(d.getVar('PTEST_ENABLED') == "1"):
        for i in ['do_configure_ptest_base', 'do_compile_ptest_base', 'do_install_ptest_base']:
            bb.build.deltask(i, d)
}

QARECIPETEST[missing-ptest] = "package_qa_check_missing_ptest"
def package_qa_check_missing_ptest(pn, d, messages):
    # This checks that ptest package is actually included
    # in standard oe-core ptest images - only for oe-core recipes
    if not 'meta/recipes' in d.getVar('FILE') or not(d.getVar('PTEST_ENABLED') == "1"):
        return

    enabled_ptests = " ".join([d.getVar('PTESTS_FAST'), d.getVar('PTESTS_SLOW'), d.getVar('PTESTS_PROBLEMS')]).split()
    if pn.replace(d.getVar('MLPREFIX'), '') not in enabled_ptests:
        oe.qa.handle_error("missing-ptest", "supports ptests but is not included in oe-core's ptest-packagelists.inc", d)
