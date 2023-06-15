SUMMARY = "Libcomps is alternative for yum.comps library (which is for managing rpm package groups)."
HOMEPAGE = "https://github.com/rpm-software-management/libcomps"
DESCRIPTION = "Libcomps is alternative for yum.comps library. It's written in pure C as library and there's bindings for python2 and python3."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/rpm-software-management/libcomps.git;branch=master;protocol=https \
           file://0001-libcomps-Use-Py_hash_t-instead-of-long-in-PyCOMPS_ha.patch \
           file://0002-Do-not-set-PYTHON_INSTALL_DIR-by-running-python.patch \
           "

SRCREV = "9322bdcf06630cc094f094f944d7d0e2cb798b73"

S = "${WORKDIR}/git"

inherit cmake setuptools3-base

DEPENDS += "libxml2 expat libcheck"

EXTRA_OECMAKE = " -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3"
OECMAKE_SOURCEPATH = "${S}/libcomps"

BBCLASSEXTEND = "native nativesdk"

