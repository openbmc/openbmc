FILESEXTRAPATHS:prepend := "${THISDIR}/corstone1000:"

SRC_URI:append = " \
                  file://0026-plat-add-corstone1000-platform-to-drivers-arm.patch \
                  file://0027-Add-MHU-driver.patch \
		  "


EXTRA_OECMAKE:append = "-DMM_COMM_BUFFER_ADDRESS="0x00000000 0x02000000" \
                                             -DMM_COMM_BUFFER_PAGE_COUNT="1" \
"
