require core-image-sato.bb

DESCRIPTION = "Image with Sato for development work. It includes everything \
within core-image-sato plus a native toolchain, application development and \
testing libraries, profiling and debug symbols."
HOMEPAGE = "https://www.yoctoproject.org/"

IMAGE_FEATURES += "dev-pkgs"
