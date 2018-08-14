DISTUTILS_BUILD_ARGS ?= ""
DISTUTILS_STAGE_HEADERS_ARGS ?= "--install-dir=${STAGING_INCDIR}/${PYTHON_DIR}"
DISTUTILS_STAGE_ALL_ARGS ?= "--prefix=${STAGING_DIR_HOST}${prefix} \
    --install-data=${STAGING_DATADIR}"
DISTUTILS_INSTALL_ARGS ?= "--prefix=${D}/${prefix} \
    --install-data=${D}/${datadir}"

distutils_do_compile() {
         STAGING_INCDIR=${STAGING_INCDIR} \
         STAGING_LIBDIR=${STAGING_LIBDIR} \
         ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py build ${DISTUTILS_BUILD_ARGS} || \
         bbfatal_log "${PYTHON_PN} setup.py build_ext execution failed."
}

distutils_stage_headers() {
        install -d ${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR}
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install_headers ${DISTUTILS_STAGE_HEADERS_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py install_headers execution failed."
}

distutils_stage_all() {
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        install -d ${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR}
        PYTHONPATH=${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install ${DISTUTILS_STAGE_ALL_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py install (stage) execution failed."
}

distutils_do_install() {
        echo "Beginning ${PN} Install ..."
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        echo "Step 2 of ${PN} Install ..."
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        PYTHONPATH=${D}/${PYTHON_SITEPACKAGES_DIR} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install --install-lib=${D}/${PYTHON_SITEPACKAGES_DIR} ${DISTUTILS_INSTALL_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py install execution failed."

        echo "Step 3 of ${PN} Install ..."
        # support filenames with *spaces*
        find ${D} -name "*.py" -print0 | while read -d $'\0' i ; do \
            sed -i -e s:${D}::g $i
        done

        echo "Step 4 of ${PN} Install ..."
        if test -e ${D}${bindir} ; then	
            for i in ${D}${bindir}/* ; do \
                sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g $i
            done
        fi

        echo "Step 4 of ${PN} Install ..."
        if test -e ${D}${sbindir}; then
            for i in ${D}${sbindir}/* ; do \
                sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g $i
            done
        fi

        echo "Step 5 of ${PN} Install ..."
        rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/easy-install.pth
        
        #
        # FIXME: Bandaid against wrong datadir computation
        #
        if [ -e ${D}${datadir}/share ]; then
            mv -f ${D}${datadir}/share/* ${D}${datadir}/
        fi
}

#EXPORT_FUNCTIONS do_compile do_install

export LDSHARED="${CCLD} -shared"
