SUMMARY = "runtime performance analyzer"
HOMEPAGE = "https://github.com/iipeace/guider"
BUGTRACKER = "https://github.com/iipeace/guider/issues"
AUTHOR = "Peace Lee <ipeace5@gmail.com>"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

PV = "3.9.6+git${SRCPV}"
PR = "r0"

SRC_URI = "git://github.com/iipeace/${BPN}"
#SRCREV = "${AUTOREV}"
SRCREV = "fef25c41efb9bde0614ea477d0b90bd9565ae0b4"

S = "${WORKDIR}/git"
R = "${RECIPE_SYSROOT}"

inherit ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "distutils", "", d)}

GUIDER_OBJ = "guider.pyc"
GUIDER_SCRIPT = "guider"

do_install() {
    python ${S}/setup.py install

    install -d ${D}${bindir}
    install -v -m 0755 ${STAGING_BINDIR_NATIVE}/${GUIDER_SCRIPT} ${D}${bindir}/${GUIDER_SCRIPT}

    install -d ${D}${datadir}/${BPN}
    install -v -m 0755 ${STAGING_LIBDIR_NATIVE}/python${PYTHON_BASEVERSION}/site-packages/${BPN}/${GUIDER_OBJ} ${D}${datadir}/${BPN}/${GUIDER_OBJ}
}

RDEPENDS_${PN} = "python-ctypes python-shell \
                  python-json python-subprocess"
python() {
    if 'meta-python2' not in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe('Requires meta-python2 to be present.')
}
