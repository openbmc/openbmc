#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Common infrastructure for Python packages that use PEP-517 compliant packaging.
# https://www.python.org/dev/peps/pep-0517/
#
# This class will build a wheel in do_compile, and use pypa/installer to install
# it in do_install.

DEPENDS:append = " python3-build-native python3-installer-native"

# Where to execute the build process from
PEP517_SOURCE_PATH ?= "${S}"

# The directory where wheels will be written
PEP517_WHEEL_PATH ?= "${WORKDIR}/dist"

# Other options to pass to build
PEP517_BUILD_OPTS ?= ""

# The interpreter to use for installed scripts
PEP517_INSTALL_PYTHON = "python3"
PEP517_INSTALL_PYTHON:class-native = "nativepython3"

# pypa/installer option to control the bytecode compilation
INSTALL_WHEEL_COMPILE_BYTECODE ?= "--compile-bytecode=0"

# PEP517 doesn't have a specific configure step, so set an empty do_configure to avoid
# running base_do_configure.
python_pep517_do_configure () {
    :
}

# When we have Python 3.11 we can parse pyproject.toml to determine the build
# API entry point directly
python_pep517_do_compile () {
    nativepython3 -m build --no-isolation --wheel --outdir ${PEP517_WHEEL_PATH} ${PEP517_SOURCE_PATH} ${PEP517_BUILD_OPTS}
}
do_compile[cleandirs] += "${PEP517_WHEEL_PATH}"

python_pep517_do_install () {
    COUNT=$(find ${PEP517_WHEEL_PATH} -name '*.whl' -maxdepth 1 | wc -l)
    if test $COUNT -eq 0; then
        bbfatal No wheels found in ${PEP517_WHEEL_PATH}
    elif test $COUNT -gt 1; then
        bbfatal More than one wheel found in ${PEP517_WHEEL_PATH}, this should not happen
    fi

    nativepython3 -m installer ${INSTALL_WHEEL_COMPILE_BYTECODE} --interpreter "${USRBINPATH}/env ${PEP517_INSTALL_PYTHON}" --destdir=${D} ${PEP517_WHEEL_PATH}/*.whl
}

# A manual do_install that just uses unzip for bootstrapping purposes. Callers should DEPEND on unzip-native.
python_pep517_do_bootstrap_install () {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    unzip -d ${D}${PYTHON_SITEPACKAGES_DIR} ${PEP517_WHEEL_PATH}/*.whl
}

EXPORT_FUNCTIONS do_configure do_compile do_install
