PACKAGES:append = " ${PN}-mount"
RDEPENDS:${PN}-mount:append:class-target:df-nfs = " nfs-utils-mount"
