SUMMARY = "Configure network interfaces for parallel routing"
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/networking/bonding"
SECTION = "net"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://ifenslave.c;beginline=8;endline=12;md5=a9f0bd2324cdc1b36d1f44f0e643a62a"

SRC_URI = "http://ftp.debian.org/debian/pool/main/i/${BPN}-2.6/${BPN}-2.6_${PV}.orig.tar.gz"
SRC_URI[md5sum] = "56126cd1013cefe0ce6f81613e677bdd"
SRC_URI[sha256sum] = "7917bf34de80a2492eb225adf9168c83a4854ac8a008ed0fd5b3fd147ccd3041"

do_compile() {
    ${CC} ifenslave.c -o ifenslave
}
do_install() {
    install -d "${D}${sbindir}"
    install -m 755 "${S}/ifenslave" "${D}${sbindir}/"
}
