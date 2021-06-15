SUMMARY = "PYNQ Helloworld for Ultra96"
LICENSE = "BSD"

LIC_FILES_CHKSUM = "file://LICENSE;md5=f9990fcc34ccf1f82ccf1bc5a1cc3bfc"

RDEPENDS_${PN} += "\
	python3-pynq \
	python3-pillow \
	libstdc++ \
	"

SRC_URI = "git://github.com/Xilinx/PYNQ-HelloWorld.git;protocol=https \
    file://0001-fix-repo_board_folder-variable.patch \
    file://0001-resizer_PL-notebooks-for-ZCU104-and-Ultra96-changed.patch \
"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_ultra96 = "${MACHINE}"

SRC_URI[md5sum] = "ac1bfe94a18301b26ae5110ea26ca596"
SRC_URI[sha256sum] = "f522c54c9418d1b1fdb6098cd7139439d47b041900000812c51200482d423460"

SRCREV = "0e10a7ee06c3e7d873f4468e06e523e2d58d07f8"
S = "${WORKDIR}/git"

inherit xilinx-pynq

do_configure[noexec]="1"
do_compile[noexec]="1"

do_install() {
    install -d ${D}/${PYNQ_NOTEBOOK_DIR}/helloworld
    install -d ${D}/${PYNQ_NOTEBOOK_DIR}/helloworld/bitstream

    cp -r ${S}/boards/${BOARD_NAME}/resizer/notebooks/* ${D}/${PYNQ_NOTEBOOK_DIR}/helloworld
    cp -r ${S}/boards/${BOARD_NAME}/resizer/bitstream/* ${D}/${PYNQ_NOTEBOOK_DIR}/helloworld/bitstream

}

PACKAGE_ARCH_ultra96 = "${BOARD_ARCH}"
