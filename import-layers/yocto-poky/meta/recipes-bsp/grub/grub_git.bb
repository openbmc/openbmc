require grub2.inc

DEPENDS += "autogen-native"

DEFAULT_PREFERENCE = "-1"
DEFAULT_PREFERENCE_arm = "1"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/grub-git:"

PV = "2.00+${SRCPV}"
SRCREV = "b95e92678882f56056c64ae29092bc9cf129905f"
SRC_URI = "git://git.savannah.gnu.org/grub.git \
           file://0001-Disable-mfpmath-sse-as-well-when-SSE-is-disabled.patch \
           file://autogen.sh-exclude-pc.patch \
           file://0001-grub.d-10_linux.in-add-oe-s-kernel-name.patch \
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

EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'largefile', '--enable-largefile', '--disable-largefile', d)}"

do_configure_prepend() {
    ( cd ${S}
      ${S}/autogen.sh )
}

do_install_append () {
    install -d ${D}${sysconfdir}/grub.d
    rm -rf ${D}${libdir}/charset.alias
}

# debugedit chokes on bare metal binaries
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

RDEPENDS_${PN} = "diffutils freetype"

INSANE_SKIP_${PN} = "arch"
INSANE_SKIP_${PN}-dbg = "arch"
