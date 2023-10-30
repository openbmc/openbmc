require mdio-tools.inc

DEPENDS += "virtual/kernel libmnl"

S = "${WORKDIR}/git"

inherit pkgconfig autotools

RDEPENDS:${PN} = "kernel-module-mdio-netlink"
