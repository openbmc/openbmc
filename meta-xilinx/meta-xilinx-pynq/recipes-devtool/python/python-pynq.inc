SUMMARY = "Xilinx PYNQ Library"
HOMEPAGE = "http://pynq.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b42e39ad2ddbad7e8ad47f3eee6feff5"

SRC_URI[md5sum] = "9950894100c20293e921090b76ca9f17"
SRC_URI[sha256sum] = "4f719e2753093d1e79931755c624d160cf342c03af49e027d09abe75bdc8e8c5"

RDEPENDS_${PN} += "\
	${PYTHON_PN}-core \
	${PYTHON_PN}-asyncio \
	${PYTHON_PN}-cffi \
	${PYTHON_PN}-json \
	${PYTHON_PN}-math \
	${PYTHON_PN}-mmap \
	${PYTHON_PN}-multiprocessing \
	${PYTHON_PN}-numpy \
	${PYTHON_PN}-pycparser \
	${PYTHON_PN}-resource \
	${PYTHON_PN}-setuptools \
	${PYTHON_PN}-threading \
	${PYTHON_PN}-xml \
	${PYTHON_PN}-audio \
	lmsensors-libsensors \
	libdrm \
	libcma \
	pynq-overlay \
	"

inherit update-rc.d xilinx-pynq

DEPENDS += " libdrm boost libcma"

SRC_URI += "file://pl_server_init \
	file://0001-Fix-3.6-ism-in-Xlnk-class.patch \
	file://0002-Avoid-deleteing-notebooks.patch \
	file://0003-Use-sysroot-for-Displayport-library.patch \
"

FILESEXTRAPATHS_prepend := "${THISDIR}:"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "pl_server_init"
INITSCRIPT_PARAMS = "start 99 S ."

do_install_append() {
	install -d ${D}${INIT_D_DIR}
	install -m 755 ${WORKDIR}/pl_server_init ${D}${INIT_D_DIR}/pl_server_init
	rm -rf ${D}${PYNQ_NOTEBOOK_DIR}_*
}


FILES_${PN} += " ${INIT_D_DIR}/pl_server_init "
