# Base this image on rpi-basic-image
include rpi-basic-image.bb

COMPATIBLE_MACHINE = "^rpi$"

IMAGE_INSTALL_append = " packagegroup-rpi-test"
