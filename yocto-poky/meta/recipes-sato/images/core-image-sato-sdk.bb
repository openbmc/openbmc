require core-image-sato.bb

DESCRIPTION = "Image with Sato support that includes everything within \
core-image-sato plus meta-toolchain, development headers and libraries to \
form a standalone SDK."

QT4PKG = "qt4-pkgs"
QT4PKG_mips64 = ""
QT4PKG_mips64n32 = ""

IMAGE_FEATURES += "dev-pkgs tools-sdk ${QT4PKG} \
	tools-debug eclipse-debug tools-profile tools-testapps debug-tweaks ssh-server-openssh"

IMAGE_INSTALL += "kernel-devsrc"

