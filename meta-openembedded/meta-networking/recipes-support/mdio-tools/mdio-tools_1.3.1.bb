require mdio-tools.inc

DEPENDS += "virtual/kernel libmnl"

S = "${WORKDIR}/git"

inherit pkgconfig autotools

RRECOMMENDS:${PN} = "kernel-module-mdio-netlink"
