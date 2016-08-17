# Base this image on rpi-hwup-image
include rpi-hwup-image.bb

SPLASH = "psplash-raspberrypi"

IMAGE_FEATURES += "ssh-server-dropbear splash"
