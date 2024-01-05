# NOTE: mosh-server requires a UTF-8 locale, but there's no way to add
# an explicit dependency for this so you need to ensure this is in your
# image yourself when you install mosh-server.

SUMMARY = "Remote shell supporting roaming and high-latency connections"
DESCRIPTION = "Remote terminal application that allows roaming, supports \
intermittent connectivity, and provides intelligent local echo and line \
editing of user keystrokes. Mosh is a replacement for SSH. It's more \
robust and responsive, especially over Wi-Fi, cellular, and \
long-distance links."
HOMEPAGE = "http://mosh.mit.edu"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "protobuf-native protobuf ncurses zlib libio-pty-perl openssl libutempter abseil-cpp"

SRC_URI = "https://mosh.org/${BP}.tar.gz \
           file://0001-configure.ac-add-support-of-protobuf-4.22.x.patch \
           "

SRC_URI[sha256sum] = "872e4b134e5df29c8933dff12350785054d2fd2839b5ae6b5587b14db1465ddd"

inherit autotools pkgconfig

PACKAGE_BEFORE_PN += "${PN}-server"
FILES:${PN}-server = "${bindir}/mosh-server"

NEEDED_PERL_MODULES = "\
    perl-module-socket \
    perl-module-getopt-long \
    perl-module-errno \
    perl-module-io-socket-inet \
    perl-module-posix \
"

# mosh uses SSH to authenticate and the client uses OpenSSH-specific features
RDEPENDS:${PN} += "openssh-ssh ${NEEDED_PERL_MODULES}"
# The server seemed not to work with dropbear either
RDEPENDS:${PN}-server += "openssh-sshd"

# Fails to build with thumb-1 (qemuarm)
#| {standard input}: Assembler messages:
#| {standard input}:2100: Error: instruction not supported in Thumb16 mode -- `adds r4,r4,r4'
#| {standard input}:2101: Error: instruction not supported in Thumb16 mode -- `adcs r5,r5,r5'
#| {standard input}:2102: Error: instruction not supported in Thumb16 mode -- `adcs r6,r6,r6'
#| {standard input}:2103: Error: instruction not supported in Thumb16 mode -- `adcs r7,r7,r7'
#| {standard input}:2104: Error: selected processor does not support Thumb mode `it cs'
ARM_INSTRUCTION_SET = "arm"

CXXFLAGS:append = " -std=c++17"
