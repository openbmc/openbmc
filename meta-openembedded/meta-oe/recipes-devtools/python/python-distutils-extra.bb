require python-distutils-extra.inc

inherit setuptools

DISTUTILS_INSTALL_ARGS += "--install-lib=${libdir}/${PYTHON_DIR}/site-packages"

RDEPENDS_${PN} = "\
    python-distutils \
"
