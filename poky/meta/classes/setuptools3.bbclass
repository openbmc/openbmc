inherit setuptools3-base

B = "${WORKDIR}/build"

SETUPTOOLS_BUILD_ARGS ?= ""
SETUPTOOLS_INSTALL_ARGS ?= "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"

SETUPTOOLS_PYTHON = "python3"
SETUPTOOLS_PYTHON:class-native = "nativepython3"

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
        build --build-base=${B} ${SETUPTOOLS_BUILD_ARGS} || \
        bbfatal_log "'${PYTHON_PN} setup.py build ${SETUPTOOLS_BUILD_ARGS}' execution failed."
}
setuptools3_do_compile[vardepsexclude] = "MACHINE"

setuptools3_do_install() {
        cd ${SETUPTOOLS_SETUP_PATH}
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py \
        build --build-base=${B} install --skip-build ${SETUPTOOLS_INSTALL_ARGS} || \
        bbfatal_log "'${PYTHON_PN} setup.py install ${SETUPTOOLS_INSTALL_ARGS}' execution failed."

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
setuptools3_do_install[vardepsexclude] = "MACHINE"

EXPORT_FUNCTIONS do_configure do_compile do_install

export LDSHARED="${CCLD} -shared"
DEPENDS += "python3-setuptools-native"

