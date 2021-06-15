require core-image-sato.bb

DESCRIPTION = "Image with Sato support that includes everything within \
core-image-sato plus meta-toolchain, development headers and libraries to \
form a standalone SDK."
HOMEPAGE = "https://www.yoctoproject.org/"

IMAGE_FEATURES += "dev-pkgs tools-sdk \
	tools-debug eclipse-debug tools-profile tools-testapps debug-tweaks ssh-server-openssh"

IMAGE_INSTALL += "kernel-devsrc"

