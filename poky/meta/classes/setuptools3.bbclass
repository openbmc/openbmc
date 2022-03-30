inherit setuptools3-base python_pep517

# bdist_wheel builds in ./dist
#B = "${WORKDIR}/build"

SETUPTOOLS_BUILD_ARGS ?= ""

SETUPTOOLS_SETUP_PATH ?= "${S}"

setuptools3_do_configure() {
    :
}

setuptools3_do_compile() {
        cd ${SETUPTOOLS_SETUP_PATH}
        NO_FETCH_BUILD=1 \
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py \
        bdist_wheel --verbose --dist-dir ${PEP517_WHEEL_PATH} ${SETUPTOOLS_BUILD_ARGS} || \
        bbfatal_log "'${PYTHON_PN} setup.py bdist_wheel ${SETUPTOOLS_BUILD_ARGS}' execution failed."
}
setuptools3_do_compile[vardepsexclude] = "MACHINE"
do_compile[cleandirs] += "${PEP517_WHEEL_PATH}"

setuptools3_do_install() {
        python_pep517_do_install
}

EXPORT_FUNCTIONS do_configure do_compile do_install

export LDSHARED="${CCLD} -shared"
DEPENDS += "python3-setuptools-native python3-wheel-native"
