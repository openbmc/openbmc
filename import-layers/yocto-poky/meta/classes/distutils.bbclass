inherit distutils-base

DISTUTILS_BUILD_ARGS ?= ""
DISTUTILS_STAGE_HEADERS_ARGS ?= "--install-dir=${STAGING_INCDIR}/${PYTHON_DIR}"
DISTUTILS_STAGE_ALL_ARGS ?= "--prefix=${STAGING_DIR_HOST}${prefix} \
    --install-data=${STAGING_DATADIR}"
DISTUTILS_INSTALL_ARGS ?= "--prefix=${D}/${prefix} \
    --install-data=${D}/${datadir}"

distutils_do_compile() {
         STAGING_INCDIR=${STAGING_INCDIR} \
         STAGING_LIBDIR=${STAGING_LIBDIR} \
         BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
         ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py build ${DISTUTILS_BUILD_ARGS} || \
         bbfatal_log "${PYTHON_PN} setup.py build execution failed."
}

distutils_stage_headers() {
        install -d ${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR}
        BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install_headers ${DISTUTILS_STAGE_HEADERS_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py install_headers execution failed."
}

distutils_stage_all() {
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        install -d ${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR}
        PYTHONPATH=${STAGING_DIR_HOST}${PYTHON_SITEPACKAGES_DIR} \
        BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install ${DISTUTILS_STAGE_ALL_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py install (stage) execution failed."
}

distutils_do_install() {
        install -d ${D}${PYTHON_SITEPACKAGES_DIR}
        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR} \
        BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install --install-lib=${D}/${PYTHON_SITEPACKAGES_DIR} ${DISTUTILS_INSTALL_ARGS} || \
        bbfatal_log "${PYTHON_PN} setup.py install execution failed."

        # support filenames with *spaces*
        # only modify file if it contains path  and recompile it
        find ${D} -name "*.py" -exec grep -q ${D} {} \; -exec sed -i -e s:${D}::g {} \; -exec ${STAGING_BINDIR_NATIVE}/python-native/python -mcompileall {} \;

        if test -e ${D}${bindir} ; then	
            for i in ${D}${bindir}/* ; do \
                if [ ${PN} != "${BPN}-native" ]; then
                	sed -i -e s:${STAGING_BINDIR_NATIVE}/python-native/python:${bindir}/env\ python:g $i
		fi
                sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g $i
            done
        fi

        if test -e ${D}${sbindir}; then
            for i in ${D}${sbindir}/* ; do \
                if [ ${PN} != "${BPN}-native" ]; then
                	sed -i -e s:${STAGING_BINDIR_NATIVE}/python-native/python:${bindir}/env\ python:g $i
		fi
                sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g $i
            done
        fi

        rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/easy-install.pth
        rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/site.py*
        
        #
        # FIXME: Bandaid against wrong datadir computation
        #
        if test -e ${D}${datadir}/share; then
            mv -f ${D}${datadir}/share/* ${D}${datadir}/
            rmdir ${D}${datadir}/share
        fi

	# Fix backport modules
	if test -e ${STAGING_LIBDIR}/${PYTHON_DIR}/site-packages/backports/__init__.py && test -e ${D}${PYTHON_SITEPACKAGES_DIR}/backports/__init__.py; then
	   rm ${D}${PYTHON_SITEPACKAGES_DIR}/backports/__init__.py;
	   rm ${D}${PYTHON_SITEPACKAGES_DIR}/backports/__init__.pyc;
	fi
}

EXPORT_FUNCTIONS do_compile do_install

export LDSHARED="${CCLD} -shared"
