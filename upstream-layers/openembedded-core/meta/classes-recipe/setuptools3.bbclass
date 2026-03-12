#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit setuptools3-base python_pep517

DEPENDS += "python3-setuptools-native python3-wheel-native"

SETUPTOOLS_BUILD_ARGS ?= ""

SETUPTOOLS_SETUP_PATH ?= "${S}"

python do_check_backend() {
    import re
    filename = d.expand("${SETUPTOOLS_SETUP_PATH}/pyproject.toml")
    if os.path.exists(filename):
        for line in open(filename):
            match = re.match(r"build-backend\s*=\s*\W([\w.]+)\W", line)
            if not match: continue

            msg = f"inherits setuptools3 but has pyproject.toml with {match[1]}, use the correct class"
            if "pep517-backend" not in (d.getVar("INSANE_SKIP") or "").split():
                oe.qa.handle_error("pep517-backend", msg, d)
    oe.qa.exit_if_errors(d)
}
addtask check_backend after do_patch before do_configure

setuptools3_do_configure() {
    :
}
# This isn't nice, but is the best solutions to ensure clean builds for now.
# https://github.com/pypa/setuptools/issues/4732
do_configure[cleandirs] = "${SETUPTOOLS_SETUP_PATH}/build"

setuptools3_do_compile() {
        cd ${SETUPTOOLS_SETUP_PATH}

        export STAGING_INCDIR=${STAGING_INCDIR}
        export STAGING_LIBDIR=${STAGING_LIBDIR}

        nativepython3 setup.py --verbose \
            build ${@oe.utils.parallel_make_argument(d, "-j %d")} \
            bdist_wheel --dist-dir ${PEP517_WHEEL_PATH} \
            ${SETUPTOOLS_BUILD_ARGS}
}
setuptools3_do_compile[vardepsexclude] = "MACHINE"
do_compile[cleandirs] += "${PEP517_WHEEL_PATH}"

# This could be removed in the future but some recipes in meta-oe still use it
setuptools3_do_install() {
        python_pep517_do_install
}

EXPORT_FUNCTIONS do_configure do_compile do_install

export LDSHARED = "${CCLD} -shared"
