require grub2.inc

DEPENDS += "autogen-native"

DEFAULT_PREFERENCE = "-1"
DEFAULT_PREFERENCE_arm = "1"

PV = "2.00+${SRCPV}"
SRCREV = "87de66d9d83446ecddb29cfbdf7369102c8e209e"
SRC_URI = "git://git.savannah.gnu.org/grub.git \
           file://grub-2.00-fpmath-sse-387-fix.patch \
           file://autogen.sh-exclude-pc.patch \
           file://grub-2.00-add-oe-kernel.patch \
           file://0001-Fix-build-with-glibc-2.20.patch \
          "

S = "${WORKDIR}/git"

COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*)-(linux.*|freebsd.*)'

inherit autotools gettext texinfo

# configure.ac has code to set this automagically from the target tuple
# but the OE freeform one (core2-foo-bar-linux) don't work with that.

GRUBPLATFORM_arm = "uboot"
GRUBPLATFORM_aarch64 = "efi"
GRUBPLATFORM ??= "pc"

EXTRA_OECONF = "--with-platform=${GRUBPLATFORM} --disable-grub-mkfont --program-prefix="" \
                --enable-liblzma=no --enable-device-mapper=no --enable-libzfs=no"

do_configure_prepend() {
    ( cd ${S}
      ${S}/autogen.sh )
}

do_install_append () {
    install -d ${D}${sysconfdir}/grub.d
 
}

# debugedit chokes on bare metal binaries
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

RDEPENDS_${PN} = "diffutils freetype"
FILES_${PN}-dbg += "${libdir}/${BPN}/*/.debug"

INSANE_SKIP_${PN} = "arch"
INSANE_SKIP_${PN}-dbg = "arch"
