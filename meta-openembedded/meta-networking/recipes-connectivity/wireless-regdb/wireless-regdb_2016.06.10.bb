SUMMARY = "Wireless Central Regulatory Domain Database"
HOMEPAGE = "http://wireless.kernel.org/en/developers/Regulatory/CRDA"
SECTION = "net"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=07c4f6dea3845b02a18dc00c8c87699c"

SRC_URI = "https://www.kernel.org/pub/software/network/${BPN}/${BP}.tar.xz"
SRC_URI[md5sum] = "d282cce92b6e692e8673e2bd97adf33b"
SRC_URI[sha256sum] = "cfedf1c3521b3c8f32602f25ed796e96e687c3441a00e7c050fedf7fd4f1b8b7"

inherit bin_package

do_install() {
    install -d -m0755 ${D}${libdir}/crda
    install -d -m0755 ${D}${sysconfdir}/wireless-regdb/pubkeys
    install -m 0644 regulatory.bin ${D}${libdir}/crda/regulatory.bin
    install -m 0644 sforshee.key.pub.pem ${D}${sysconfdir}/wireless-regdb/pubkeys/sforshee.key.pub.pem
}

RSUGGESTS_${PN} = "crda"
