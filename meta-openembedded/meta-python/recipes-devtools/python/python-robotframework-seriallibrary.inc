SUMMARY = "Robot Framework test library for serial connection"
HOMEPAGE = "https://github.com/whosaysni/robotframework-seriallibrary"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI[md5sum] = "b7c9565d54c30df7cd3f3c0e29adffa3"
SRC_URI[sha256sum] = "256ad60fc0b7df4be44d82c302f5ed8fad4935cda99e4b45942e3c88179d1e19"

SRC_URI = "https://files.pythonhosted.org/packages/ce/e1/1e337dc84a20b9d92d7b243d16bb9f6fc0084967eca7454228bb546efec7/robotframework-seriallibrary-0.3.1.tar.gz"
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pyserial \
    ${PYTHON_PN}-robotframework \
"

BBCLASSEXTEND = "native nativesdk"
