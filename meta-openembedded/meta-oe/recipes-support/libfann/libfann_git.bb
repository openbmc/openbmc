SUMMARY = "Fast Artificial Neural Network (FANN) Library"
DESCRIPTION = "Fast Artificial Neural Network (FANN) Library is a free open source neural network library, which implements multilayer artificial neural networks in C with support for both fully connected and sparsely connected networks."
HOMEPAGE = "https://github.com/libfann/fann"
SECTION = "libs/devel"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=f14599a2f089f6ff8c97e2baa4e3d575"

inherit cmake

SRCREV ?= "7ec1fc7e5bd734f1d3c89b095e630e83c86b9be1"
SRC_URI = "git://github.com/libfann/fann.git;branch=master;protocol=https \
          "

PV = "2.2.0+git${SRCPV}"

S = "${WORKDIR}/git"

EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')}"
