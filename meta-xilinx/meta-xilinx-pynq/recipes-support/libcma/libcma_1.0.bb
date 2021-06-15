SUMMARY = "Recompile libcma against pynqlib c"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b42e39ad2ddbad7e8ad47f3eee6feff5"

CMA_ARCH_arm = "32"
CMA_ARCH_aarch64 = "64"

PYNQBRANCH ?= "image_v2.4"
SRCBRANCHARG = "${@['nobranch=1', 'branch=${PYNQBRANCH}'][d.getVar('PYNQBRANCH', True) != '']}"
PYNQURI ?= "git://github.com/Xilinx/PYNQ.git;protocol=https"

SRC_URI = "${PYNQURI};${SRCBRANCHARG}"

SRCREV ?= "3d659d374701b7c34fa702e7aa23f71f9113f826"

S="${WORKDIR}/git"

CMA_ARCH_arm = "32"
CMA_ARCH_aarch64 = "64"

do_install() {

  install -d ${D}/usr/lib/
  install -d ${D}/usr/include/

  cd ${S}/sdbuild/packages/libsds/libcma
  CMA_ARCH=${CMA_ARCH_${TARGET_ARCH}} make install DESTDIR=${D}
}

SOLIBS = ".so"
FILES_SOLIBSDEV = ""
FILES_${PN} += "/usr/lib/libcma.so /usr/include"

