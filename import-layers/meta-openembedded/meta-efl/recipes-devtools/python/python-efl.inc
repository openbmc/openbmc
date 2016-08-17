DESCRIPTION = "${PN} bindings"

LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"
# there is also GPLv3.0 in COPYING;md5=d32239bcb673463ab874e80d47fae504 but no file seems to use that

AUTHOR = "Gustavo Sverzut Barbieri <barbieri@gmail.com>"
# NOTE: Due to a bug in distutils, even if we don't use pyrex but cython,
# we need to build pyrex otherwise cython doesn't get called to build
# the extension modules.
DEPENDS = "python-cython-native python-pyrex-native python-numeric python-dbus lua efl elementary"
RDEPENDS_${PN} += "python-lang"

PROVIDES = "python-ecore python-eldbus python-edje python-elementary python-emotion python-evas"

# necessary to let the call for python-config succeed
export BUILD_SYS
export HOST_SYS

inherit e-base distutils pkgconfig

SRCVER = "${PV}"

SRC_URI = "\
    ${E_RELEASES}/bindings/python/${SRCNAME}-${SRCVER}.tar.gz \
"
S = "${WORKDIR}/${SRCNAME}-${SRCVER}"

do_install_append() {
    # drop all .pyo, pyc files
    find ${D}${libdir}/${PYTHON_DIR}/site-packages -name \*.pyo -o -name \*.pyc | xargs rm -f
}

PACKAGES += "python-ecore python-eldbus python-edje python-elementary python-emotion python-evas python-ethumb ${PN}-examples"

FILES_${PN} = " \
    ${libdir}/${PYTHON_DIR}/site-packages/python_efl*egg-info \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/eo.so \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/utils \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/__init__.py \
"
FILES_${PN}-dbg += "${libdir}/${PYTHON_DIR}/site-packages/*.egg/*/*/.debug"
FILES_${PN}-dev += "${libdir}/${PYTHON_DIR}/site-packages/*/*.la"

FILES_${PN}-examples = "${datadir}/${PN}/examples"

FILES_python-ecore = " \
    ${libdir}/${PYTHON_DIR}/site-packages/ecore/ \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/ecore*.so \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/ecore \
"
FILES_python-eldbus = " \
    ${libdir}/${PYTHON_DIR}/site-packages/e_dbus/ \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/dbus_mainloop.so \
"
FILES_python-edje = " \
    ${libdir}/${PYTHON_DIR}/site-packages/edje/ \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/edje*.so \
"
FILES_python-elementary = " \
    ${libdir}/${PYTHON_DIR}/site-packages/elementary/ \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/elementary/ \
"
FILES_python-emotion = " \
    ${libdir}/${PYTHON_DIR}/site-packages/emotion/ \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/emotion.so \
"
FILES_python-evas = " \
    ${libdir}/${PYTHON_DIR}/site-packages/evas/ \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/evas.so \
"
FILES_python-ethumb = " \
    ${libdir}/${PYTHON_DIR}/site-packages/ethumb/ \
    ${libdir}/${PYTHON_DIR}/site-packages/efl/ethumb*so \
"
