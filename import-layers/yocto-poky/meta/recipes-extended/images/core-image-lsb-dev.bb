require core-image-lsb.bb

DESCRIPTION = "Basic image without X support suitable for development work. It \
can be used for customization and implementations that conform to Linux \
Standard Base (LSB)."

IMAGE_FEATURES += "dev-pkgs"
