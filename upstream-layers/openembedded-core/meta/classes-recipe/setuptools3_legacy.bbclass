#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# This class is for packages which use the deprecated setuptools behaviour,
# specifically custom install tasks which don't work correctly with bdist_wheel.
# This behaviour is deprecated in setuptools[1] and won't work in the future, so
# all users of this should consider their options: pure Python modules can use a
# modern Python tool such as build[2], or packages which are doing more (such as
# installing init scripts) should use a fully-featured build system such as Meson.
#
# [1] https://setuptools.pypa.io/en/latest/history.html#id142
# [2] https://pypi.org/project/build/

inherit setuptools3-base

B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

SETUPTOOLS_BUILD_ARGS ?= ""
SETUPTOOLS_INSTALL_ARGS ?= "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"

SETUPTOOLS_PYTHON = "python3"
SETUPTOOLS_PYTHON:class-native = "nativepython3"

SETUPTOOLS_SETUP_PATH ?= "${S}"

python do_check_backend() {
    """
    Check if this package has a pyproject.toml that specifies a PEP517 build backend, so should be
    using a different build class.
    """

    if "pep517-backend" in (d.getVar("INSANE_SKIP") or "").split():
        return

    try:
        import tomllib
    except ImportError:
        import bb._vendor.tomli as tomllib

    filename = d.expand("${SETUPTOOLS_SETUP_PATH}/pyproject.toml")
    if os.path.exists(filename):
        with open(filename, "rb") as f:
            toml = tomllib.load(f)

        try:
            backend = toml["build-system"]["build-backend"]
            msg = f"inherits setuptools3_legacy but has pyproject.toml specifying backend {backend}, use the correct class"
            oe.qa.handle_error("pep517-backend", msg, d)
            oe.qa.exit_if_errors(d)
        except KeyError:
            return
}
addtask check_backend after do_patch before do_configure

setuptools3_legacy_do_configure() {
    :
}

setuptools3_legacy_do_compile() {
        cd ${SETUPTOOLS_SETUP_PATH}
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py \
        build --build-base=${B} ${SETUPTOOLS_BUILD_ARGS} || \
        bbfatal_log "'python3 setup.py build ${SETUPTOOLS_BUILD_ARGS}' execution failed."
}
setuptools3_legacy_do_compile[vardepsexclude] = "MACHINE"

setuptools3_legacy_do_install() {
        cd ${SETUPTOOLS_SETUP_PATH}
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR}:$PYTHONPATH \
        ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py \
        build --build-base=${B} install --skip-build ${SETUPTOOLS_INSTALL_ARGS} || \
        bbfatal_log "'python3 setup.py install ${SETUPTOOLS_INSTALL_ARGS}' execution failed."

        # support filenames with *spaces*
        find ${D} -name "*.py" -exec grep -q ${D} {} \; \
                               -exec sed -i -e s:${D}::g {} \;

        for i in ${D}${bindir}/* ${D}${sbindir}/*; do
            if [ -f "$i" ]; then
                sed -i -e s:${PYTHON}:${USRBINPATH}/env\ ${SETUPTOOLS_PYTHON}:g $i
                sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g $i
            fi
        done

        rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/easy-install.pth

        #
        # FIXME: Bandaid against wrong datadir computation
        #
        if [ -e ${D}${datadir}/share ]; then
            mv -f ${D}${datadir}/share/* ${D}${datadir}/
            rmdir ${D}${datadir}/share
        fi
}
setuptools3_legacy_do_install[vardepsexclude] = "MACHINE"

EXPORT_FUNCTIONS do_configure do_compile do_install

export LDSHARED = "${CCLD} -shared"
DEPENDS += "python3-setuptools-native"

