require mdio-tools.inc

DEPENDS += "virtual/kernel libmnl"


inherit pkgconfig autotools

RRECOMMENDS:${PN} = "kernel-module-mdio-netlink"
