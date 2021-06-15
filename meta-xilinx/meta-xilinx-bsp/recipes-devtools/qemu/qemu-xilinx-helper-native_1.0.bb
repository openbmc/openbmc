
python () {
    if d.getVar("PREFERRED_PROVIDER_qemu-helper-native") != d.getVar("PN"):
        raise bb.parse.SkipRecipe("Set qemu-helper-native provider to use this recipe")
}

def get_filespath_extra(d, subpath):
    metaroot = next((p for p in d.getVar('BBPATH').split(':') if os.path.basename(p) == 'meta'), None)
    if metaroot:
        return os.path.join(metaroot, subpath) + ":"
    return ""

# TODO: improve this, since it is very hacky that this recipe need to build tunctl.
# include the existing qemu-helper-native
require recipes-devtools/qemu/qemu-helper-native_1.0.bb
# get the path to tunctl.c
FILESEXTRAPATHS_prepend := "${@get_filespath_extra(d, 'recipes-devtools/qemu/qemu-helper')}"

# provide it, to replace the existing
PROVIDES += "qemu-helper-native"

# replace qemu with qemu-xilinx
DEPENDS_remove = "qemu-system-native"
DEPENDS_append = " \
		qemu-xilinx-system-native \
		qemu-xilinx-multiarch-helper-native \
		"

RDEPENDS_${PN}_remove = "qemu-system-native"
RDEPENDS_${PN}_append = " qemu-xilinx-system-native"
