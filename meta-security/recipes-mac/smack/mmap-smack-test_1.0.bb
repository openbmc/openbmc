SUMMARY = "Mmap binary used to test smack mmap attribute"
DESCRIPTION = "Mmap binary used to test smack mmap attribute"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://mmap.c" 

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_compile() {
    ${CC} mmap.c ${LDFLAGS} -o mmap_test
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 mmap_test ${D}${bindir}
}
