DEPENDS:append = " python3-pip-native"

def guess_pip_install_package_name(d):
    '''https://www.python.org/dev/peps/pep-0491/#escaping-and-unicode'''
    return (d.getVar('PYPI_PACKAGE') or d.getVar('PN')).replace('-', '_')

PIP_INSTALL_PACKAGE ?= "${@guess_pip_install_package_name(d)}"
PIP_INSTALL_DIST_PATH ?= "${B}/dist"
PYPA_WHEEL ??= "${PIP_INSTALL_DIST_PATH}/${PIP_INSTALL_PACKAGE}-${PV}-*.whl"

PIP_INSTALL_ARGS ?= "\
    -vvvv \
    --ignore-installed \
    --no-cache \
    --no-deps \
    --no-index \
    --root=${D} \
    --prefix=${prefix} \
"

pip_install_wheel_do_install:prepend () {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
}

export PYPA_WHEEL

PIP_INSTALL_PYTHON = "python3"
PIP_INSTALL_PYTHON:class-native = "nativepython3"

pip_install_wheel_do_install () {
    nativepython3 -m pip install ${PIP_INSTALL_ARGS} ${PYPA_WHEEL} ||
    bbfatal_log "Failed to pip install wheel. Check the logs."

    for i in ${D}${bindir}/* ${D}${sbindir}/*; do
        if [ -f "$i" ]; then
            sed -i -e "1s,#!.*nativepython3,#!${USRBINPATH}/env ${PIP_INSTALL_PYTHON}," $i
            sed -i -e "s:${PYTHON}:${USRBINPATH}/env\ ${PIP_INSTALL_PYTHON}:g" $i
            sed -i -e "s:${STAGING_BINDIR_NATIVE}:${bindir}:g" $i
            # Recompile after modifying it
            cd ${D}
            file=`echo $i | sed 's:^${D}/::'`
            ${STAGING_BINDIR_NATIVE}/python3-native/python3 -c "from py_compile import compile; compile('$file')"
            cd -
        fi
    done
}

EXPORT_FUNCTIONS do_install
