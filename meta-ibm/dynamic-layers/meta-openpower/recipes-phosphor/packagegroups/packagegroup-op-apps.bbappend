RDEPENDS:${PN}-system:remove:witherspoon = "croserver"

# No host firmware related features for huygens wanted yet
RDEPENDS:${PN}-flash:remove:huygens = " openpower-software-manager"
