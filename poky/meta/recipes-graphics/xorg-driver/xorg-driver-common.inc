SUMMARY = "X driver"
HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org"
SECTION = "x11/drivers"
LICENSE = "MIT-X"

PE = "2"
INC_PR = "r21"

DEPENDS = "virtual/xserver xorgproto util-macros"

SRC_URI = "${XORG_MIRROR}/individual/driver/${BPN}-${PV}.tar.bz2"

FILES_${PN} += " ${libdir}/xorg/modules/drivers/*.so"

XORGBUILDCLASS ??= "autotools"
inherit ${XORGBUILDCLASS} pkgconfig features_check

# depends on virtual/xserver
REQUIRED_DISTRO_FEATURES = "x11"

# FIXME: We don't want to include the libtool archives (*.la) from modules
# directory, as they serve no useful purpose. Upstream should fix Makefile.am
do_install_append() {
	find ${D}${libdir}/xorg/modules -regex ".*\.la$" | xargs rm -f --
}

# Function to add the relevant ABI dependency to drivers, which should be called
# from a PACKAGEFUNC.
def _add_xorg_abi_depends(d, name):
    # Map of ABI names exposed in the dependencies to pkg-config variables
    abis = {
      "video": "abi_videodrv",
      "input": "abi_xinput"
    }

    output = os.popen("pkg-config xorg-server --variable=%s" % abis[name]).read()
    mlprefix = d.getVar('MLPREFIX') or ''
    abi = "%sxorg-abi-%s-%s" % (mlprefix, name, output.split(".")[0])

    pn = d.getVar("PN")
    d.appendVar('RDEPENDS_' + pn, ' ' + abi)

SECURITY_LDFLAGS = "${SECURITY_X_LDFLAGS}"
