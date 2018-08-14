require core-image-lsb.bb

DESCRIPTION = "Basic image without X support suitable for Linux Standard Base \
(LSB) implementations. It includes the full meta-toolchain, plus development \
headers and libraries to form a standalone SDK."

IMAGE_FEATURES += "tools-sdk dev-pkgs tools-debug eclipse-debug tools-profile tools-testapps debug-tweaks"

IMAGE_INSTALL += "kernel-devsrc"

# Reduce this to try and keep below the 4GB image size for now
IMAGE_OVERHEAD_FACTOR = "1.2"
