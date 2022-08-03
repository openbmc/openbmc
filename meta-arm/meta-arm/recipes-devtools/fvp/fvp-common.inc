HOMEPAGE = "https://developer.arm.com/tools-and-software/simulation-models/fixed-virtual-platforms"

# FVP has an End User License Agreement. Add Arm-FVP-EULA to your
# LICENSE_FLAGS_ACCEPTED if you agree to these terms.
LICENSE_FLAGS = "Arm-FVP-EULA"

LICENSE = "Proprietary & Apache-2.0 & Python-2.0 & GPL-3.0-with-GCC-exception & Zlib & NCSA & LGPL-2.0-or-later & MIT & BSD-3-Clause"

COMPATIBLE_HOST = "x86_64.*-linux"

# The architecture-specific download filename suffix
FVP_ARCH:aarch64 = "Linux64_armv8l"
FVP_ARCH:x86-64 = "Linux64"

# The architecture-specific directory the binaries are installed under
FVP_ARCH_DIR = "${FVP_ARCH}"

def get_real_pv(d):
    # FVP versions are like 11.12_43
    pv = d.getVar("PV")
    return "%s.%s_%s" % tuple(pv.split("."))

# If PV is 1.2.3, VERSION=1.2, BUILD=3, PV_URL=1.2_3.
VERSION = "${@oe.utils.trim_version(d.getVar('PV', -1))}"
BUILD = "${@d.getVar('PV').split('.')[-1]}"
PV_URL = "${@get_real_pv(d)}"

# The directory the FVP is installed into
FVPDIR = "${libdir}/fvp/${BPN}"

# Used in do_install to create symlinks in $bindir to $FVPDIR
fvp_link_binaries() {
    DIR="${D}${FVPDIR}/models/${FVP_ARCH_DIR}*"

    stat $DIR/FVP_* >/dev/null 2>&1 || bbfatal Cannot find FVP binaries in $DIR

    for FVP in $DIR/FVP_*; do
        ln -rs $FVP ${D}${bindir}/$(basename $FVP)
    done
    # But not the .so files too
    rm -f ${D}${bindir}/*.so
}

FILES:${PN} = "${bindir} ${FVPDIR}"

# Prebuilt binaries are already stripped
INSANE_SKIP:${PN} += "already-stripped"
# FVP can optionally have a GUI, but we can use the host libraries in native/nativesdk
INSANE_SKIP:${PN} += "file-rdeps"
# GNU_HASH warnings
INSANE_SKIP:${PN} += "ldflags"

# FVP brings its own standard library so don't let it be used as a shlib provider
PRIVATE_LIBS = "libgcc_s.so.1 libstdc++.so.6"

BBCLASSEXTEND = "native nativesdk"
