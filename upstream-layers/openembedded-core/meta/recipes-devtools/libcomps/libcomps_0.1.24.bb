SUMMARY = "Libcomps is alternative for yum.comps library (which is for managing rpm package groups)."
HOMEPAGE = "https://github.com/rpm-software-management/libcomps"
DESCRIPTION = "Libcomps is alternative for yum.comps library. It's written in pure C as library and there's bindings for python2 and python3."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/rpm-software-management/libcomps.git;branch=master;tag=${PV};protocol=https \
           file://0001-Do-not-set-PYTHON_INSTALL_DIR-by-running-python.patch \
           "

SRCREV = "8d439dd015d4c6f637e64e1160201345883138e9"

inherit cmake setuptools3-base

DEPENDS = "expat libxml2 zlib"

EXTRA_OECMAKE = "-DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} \
                 -DENABLE_DOCS=OFF \
                 -DENABLE_TESTS=OFF"

OECMAKE_SOURCEPATH = "${S}/libcomps"

BBCLASSEXTEND = "native nativesdk"
