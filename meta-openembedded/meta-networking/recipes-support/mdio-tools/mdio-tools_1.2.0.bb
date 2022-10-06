require mdio-tools.inc

DEPENDS += "libmnl"

S = "${WORKDIR}/git"

inherit pkgconfig autotools

RDEPENDS:${PN} = "kernel-module-mdio-netlink"
