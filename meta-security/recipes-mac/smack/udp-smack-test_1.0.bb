SUMMARY = "Binary used to test smack udp sockets"
DESCRIPTION = "Server and client binaries used to test smack attributes on UDP sockets"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://udp_server.c \
           file://udp_client.c \
           file://test_smack_udp_sockets.sh \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_compile() {
    ${CC} udp_client.c ${LDFLAGS} -o udp_client
    ${CC} udp_server.c ${LDFLAGS} -o udp_server
}

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${sbindir}
    install -m 0755 udp_server ${D}${bindir}
    install -m 0755 udp_client ${D}${bindir}
    install -m 0755 test_smack_udp_sockets.sh ${D}${sbindir}
}
