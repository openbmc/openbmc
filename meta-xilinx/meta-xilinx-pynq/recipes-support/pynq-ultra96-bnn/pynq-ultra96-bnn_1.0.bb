DESCRIPTION = "Recipe to install BNN notebooks and PYNQ dependencies for ultra96"
SUMMARY = "BNN example jupyter notebooks with PYNQ"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f625d3c898c18035639b6d6943b6a9c"

SRC_URI = "git://github.com/Xilinx/BNN-PYNQ.git;protocol=https \
    file://0001-BNN-Notebooks-changed-default-picture-location.patch \
    file://0001-Update-default-notebooks-path-from-home-xilinx-to-us.patch \
"

SRCREV = "32eed91994228d1042a16c692047dfe81e47d498"

inherit xilinx-pynq

RDEPENDS_${PN}-notebooks += "\
    python3-pynq \
    python3-pillow \
    libstdc++ \
    libcma \
    bash \
"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_ultra96 = "${MACHINE}"

S = "${WORKDIR}/git"

do_configure[noexec]="1"
do_compile[noexec]="1"

def get_board(d):
    x = str(d.getVar("BOARD_NAME", "True"))
    return x.lower()

do_install () {
    JUPYTER_NOTEBOOKS=${D}${PYNQ_NOTEBOOK_DIR}
    install -d ${JUPYTER_NOTEBOOKS}/bnn_examples

    install -d ${JUPYTER_NOTEBOOKS}/bnn_examples/bitstreams
    install -d ${JUPYTER_NOTEBOOKS}/bnn_examples/bitstreams/${@get_board(d)}
    cp -r ${S}/bnn/bitstreams/${@get_board(d)}/* ${JUPYTER_NOTEBOOKS}/bnn_examples/bitstreams/${@get_board(d)}/

    install -d ${JUPYTER_NOTEBOOKS}/bnn_examples/libraries
    install -d ${JUPYTER_NOTEBOOKS}/bnn_examples/libraries/${@get_board(d)}
    cp -r ${S}/bnn/libraries/${@get_board(d)}/* ${JUPYTER_NOTEBOOKS}/bnn_examples/libraries/${@get_board(d)}/

    cp -r ${S}/bnn/params ${JUPYTER_NOTEBOOKS}/bnn_examples/
    cp -r ${S}/bnn/src ${JUPYTER_NOTEBOOKS}/bnn_examples/
    cp -r ${S}/bnn/__init__.py ${JUPYTER_NOTEBOOKS}/bnn_examples/
    cp -r ${S}/bnn/bnn.py ${JUPYTER_NOTEBOOKS}/bnn_examples/
    cp -r ${S}/MANIFEST.in ${JUPYTER_NOTEBOOKS}/bnn_examples/
    cp -r ${S}/setup.py ${JUPYTER_NOTEBOOKS}/bnn_examples/

    cp -r ${S}/notebooks/* ${JUPYTER_NOTEBOOKS}/bnn_examples/
}

PACKAGE_ARCH_ultra96 = "${BOARD_ARCH}"
