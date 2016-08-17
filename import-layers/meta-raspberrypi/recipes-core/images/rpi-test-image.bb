# Base this image on rpi-basic-image
include rpi-basic-image.bb

IMAGE_INSTALL_append = " packagegroup-rpi-test"
