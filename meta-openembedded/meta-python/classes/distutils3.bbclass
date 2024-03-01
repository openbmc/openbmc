inherit distutils3-base

B = "${WORKDIR}/build"
distutils_do_configure[cleandirs] = "${B}"

DISTUTILS_BUILD_ARGS ?= ""
DISTUTILS_INSTALL_ARGS ?= "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"

DISTUTILS_PYTHON = "python3"
DISTUTILS_PYTHON:class-native = "nativepython3"

DISTUTILS_SETUP_PATH ?= "${S}"

python __anonymous() {
    bb.warn("distutils3.bbclass is deprecated, please use setuptools3.bbclass instead")
}

distutils3_do_configure() {
    :
}

distutils3_do_compile() {
        cd ${DISTUTILS_SETUP_PATH}
        NO_FETCH_BUILD=1 \
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py \
        build --build-base=${B} ${DISTUTILS_BUILD_ARGS} || \
        bbfatal_log "'python3 setup.py build ${DISTUTILS_BUILD_ARGS}' execution failed."
}
distutils3_do_compile[vardepsexclude] = "MACHINE"

distutils3_do_install() {
        cd ${DISTUTILS_SETUP_PATH}
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR} \
        ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py \
        build --build-base=${B} install --skip-build ${DISTUTILS_INSTALL_ARGS} || \
        bbfatal_log "'python3 setup.py install ${DISTUTILS_INSTALL_ARGS}' execution failed."

        # support filenames with *spaces*
        find ${D} -name "*.py" -exec grep -q ${D} {} \; \
                               -exec sed -i -e s:${D}::g {} \;

        for i in ${D}${bindir}/* ${D}${sbindir}/*; do
            if [ -f "$i" ]; then
                sed -i -e s:${PYTHON}:${USRBINPATH}/env\ ${DISTUTILS_PYTHON}:g $i
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
distutils3_do_install[vardepsexclude] = "MACHINE"

EXPORT_FUNCTIONS do_configure do_compile do_install

export LDSHARED="${CCLD} -shared"
