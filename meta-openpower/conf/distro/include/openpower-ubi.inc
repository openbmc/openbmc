require conf/distro/include/openpower-virtual-pnor.inc

DISTRO_FEATURES += "openpower-ubi-fs"
DISTROOVERRIDES .= ":df-openpower-ubi-fs"
