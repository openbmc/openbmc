inherit distutils3-base

DISTUTILS_BUILD_ARGS ?= ""
DISTUTILS_BUILD_EXT_ARGS ?= ""
DISTUTILS_STAGE_HEADERS_ARGS ?= "--install-dir=${STAGING_INCDIR}/${PYTHON_DIR}"
DISTUTILS_STAGE_ALL_ARGS ?= "--prefix=${STAGING_DIR_HOST}${prefix} \
    --install-data=${STAGING_DATADIR}"
DISTUTILS_INSTALL_ARGS ?= "--prefix=${D}/${prefix} \
    --install-data=${D}/${datadir}"

distutils3_do_compile() {
        if [ ${BUILD_SYS} != ${HOST_SYS} ]; then
                SYS=${MACHINE}
        else
                SYS=${HOST_SYS}
        fi
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        BUILD_SYS=${BUILD_SYS} HOST_SYS=${SYS} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py \
        build ${DISTUTILS_BUILD_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py build_ext execution failed."
}
distutils3_do_compile[vardepsexclude] = "MACHINE"

distutils3_stage_headers() {
        install -d ${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR}
        if [ ${BUILD_SYS} != ${HOST_SYS} ]; then
                SYS=${MACHINE}
        else
                SYS=${HOST_SYS}
        fi
        BUILD_SYS=${BUILD_SYS} HOST_SYS=${SYS} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install_headers ${DISTUTILS_STAGE_HEADERS_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py install_headers execution failed."
}
distutils3_stage_headers[vardepsexclude] = "MACHINE"

distutils3_stage_all() {
        if [ ${BUILD_SYS} != ${HOST_SYS} ]; then
                SYS=${MACHINE}
        else
                SYS=${HOST_SYS}
        fi
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        install -d ${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR}
        PYTHONPATH=${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR} \
        BUILD_SYS=${BUILD_SYS} HOST_SYS=${SYS} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install ${DISTUTILS_STAGE_ALL_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py install (stage) execution failed."
}
distutils3_stage_all[vardepsexclude] = "MACHINE"

distutils3_do_install() {
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        if [ ${BUILD_SYS} != ${HOST_SYS} ]; then
                SYS=${MACHINE}
        else
                SYS=${HOST_SYS}
        fi
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR} \
        BUILD_SYS=${BUILD_SYS} HOST_SYS=${SYS} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install --install-lib=${D}/${PYTHON_SITEPACKAGES_DIR} ${DISTUTILS_INSTALL_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py install execution failed."

        # support filenames with *spaces*
        find ${D} -name "*.py" -exec grep -q ${D} {} \; -exec sed -i -e s:${D}::g {} \;

        if test -e ${D}${bindir} ; then	
            for i in ${D}${bindir}/* ; do \
                sed -i -e s:${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN}:${bindir}/env\ ${PYTHON_PN}:g $i
                sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g $i
            done
        fi

        if test -e ${D}${sbindir}; then
            for i in ${D}${sbindir}/* ; do \
                sed -i -e s:${STAGING_BINDIR_NATIVE}/python-${PYTHON_PN}/${PYTHON_PN}:${bindir}/env\ ${PYTHON_PN}:g $i
                sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g $i
            done
        fi

        rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/easy-install.pth
        
        #
        # FIXME: Bandaid against wrong datadir computation
        #
        if test -e ${D}${datadir}/share; then
            mv -f ${D}${datadir}/share/* ${D}${datadir}/
            rmdir ${D}${datadir}/share
        fi
}
distutils3_do_install[vardepsexclude] = "MACHINE"

EXPORT_FUNCTIONS do_compile do_install

export LDSHARED="${CCLD} -shared"
