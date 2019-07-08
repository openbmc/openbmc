SUMMARY = "WireGuard is an extremely simple yet fast and modern VPN"
DESCRIPTION="WireGuard is a secure network tunnel, operating at layer 3, \
implemented as a kernel virtual network interface for Linux, which aims to \
replace both IPsec for most use cases, as well as popular user space and/or \
TLS-based solutions like OpenVPN, while being more secure, more performant, \
and easier to use."
SECTION = "networking"
HOMEPAGE = "https://www.wireguard.io/"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://../COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://git.zx2c4.com/WireGuard/snapshot/WireGuard-${PV}.tar.xz"
SRC_URI[md5sum] = "8a0a232f65d5d86dd408efd9ac30bcf9"
SRC_URI[sha256sum] = "edd13c7631af169e3838621b1a1bff3ef73cf7bc778eec2bd55f7c1089ffdf9b"

S = "${WORKDIR}/WireGuard-${PV}/src/"
