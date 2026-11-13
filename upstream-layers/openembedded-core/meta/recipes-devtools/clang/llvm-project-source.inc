deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysroot
deltask do_populate_lic
RM_WORK_EXCLUDE += "${PN}"

inherit nopackages allarch

PN = "llvm-project-source-${PV}"
WORKDIR = "${TMPDIR}/work-shared/llvm-project-source-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:llvm-project-source::${PV}:${PR}::${SSTATE_VERSION}:"

STAMP = "${STAMPS_DIR}/work-shared/llvm-project-source-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/llvm-project-source-${PV}-*"

DEPENDS = ""
PACKAGES = ""
baselib = "lib"
PACKAGE_ARCH = "all"

B = "${WORKDIR}/build"

# space separated list of additional distro vendor values we want to support e.g.
# "yoe webos" or "-yoe -webos" '-' is optional
CLANG_EXTRA_OE_VENDORS ?= "${TARGET_VENDOR} ${SDK_VENDOR}"
# Extra OE DISTRO that want to support as build host. space separated list of additional distro.
# ":" separated the ID in "/etc/os-release" and the triple for finding gcc on this OE DISTRO.
# eg: "poky:poky wrlinux:wrs"
CLANG_EXTRA_OE_DISTRO ?= "poky:poky"
# Match with MULTILIB_GLOBAL_VARIANTS
ML_VARIANTS = "lib32 lib64 libx32"

# Insert lines before the //<anchor> comment in path. The anchor stays in
# place and every inserted line carries a comment naming it, so a later run
# of do_preconfigure on the same shared tree first drops what the previous
# run inserted: the result only depends on the current configuration.
def clang_oe_generate(path, anchor, lines):
    anchor_comment = "//" + anchor
    marker = "// do_preconfigure: " + anchor
    with open(path) as f:
        content = [line for line in f.read().split("\n") if not line.rstrip().endswith(marker)]
    anchors = [i for i, line in enumerate(content) if line.rstrip().endswith(anchor_comment)]
    if len(anchors) != 1:
        bb.fatal("Expected one %s anchor in %s, found %d" % (anchor_comment, path, len(anchors)))
    i = anchors[0]
    indent = content[i][:len(content[i]) - len(content[i].lstrip())]
    content[i:i] = [indent + line + " " + marker for line in lines]
    with open(path, "w") as f:
        f.write("\n".join(content))

python do_preconfigure() {
    source = d.getVar("S")
    multilib_variants = (d.getVar("ML_VARIANTS") or "").split()

    vendors_to_add = []
    for vendor in (d.getVar('CLANG_EXTRA_OE_VENDORS') or "").split():
        # convert -yoe into yoe
        vendor = vendor.lstrip('-')
        # generate possible multilib vendor names for yoe
        # such as yoemllib32
        vendors_to_add.extend([vendor + 'ml' + variant for variant in multilib_variants])
        # skip oe since already part of the cpp file
        if vendor != "oe":
            vendors_to_add.append(vendor)

    bb.note("Adding support following TARGET_VENDOR values")
    bb.note(str(vendors_to_add))
    bb.note("in llvm/lib/TargetParser/Triple.cpp and ${S}/clang/lib/Driver/ToolChains/Gnu.cpp")
    clang_oe_generate(source + '/llvm/lib/TargetParser/Triple.cpp', 'CLANG_EXTRA_OE_VENDORS_CASES',
                      ['.Case("%s", Triple::OpenEmbedded)' % v for v in vendors_to_add])
    clang_oe_generate(source + '/clang/lib/Driver/ToolChains/Gnu.cpp', 'CLANG_EXTRA_OE_VENDORS_TRIPLES',
                      ['"x86_64-%s-linux",' % v for v in vendors_to_add])

    names = []
    checks = []
    cases = []
    triples = []
    oe_checks = []
    for distro in (d.getVar('CLANG_EXTRA_OE_DISTRO') or "").split():
        distro_id, distro_triple = distro.split(":")
        # the ID doubles as a C++ enumerator name
        enum = distro_id.replace('-', '_').upper()
        names.append(enum + ",")
        checks.append("bool Is%s() const { return DistroVal == %s; }" % (enum, enum))
        cases.append('.Case("%s", Distro::%s)' % (distro_id, enum))
        triples.append('if (Distro.Is%s()) return "x86_64-%s-linux";' % (enum, distro_triple))
        oe_checks.append("DistroVal == " + enum)
    checks.append("bool IsOpenEmbedded() const { return %s; }" % (" || ".join(oe_checks) or "false"))

    clang_oe_generate(source + '/clang/include/clang/Driver/Distro.h', 'CLANG_EXTRA_OE_DISTRO_NAME', names)
    clang_oe_generate(source + '/clang/include/clang/Driver/Distro.h', 'CLANG_EXTRA_OE_DISTRO_CHECK', checks)
    clang_oe_generate(source + '/clang/lib/Driver/Distro.cpp', 'CLANG_EXTRA_OE_DISTRO_CASE', cases)
    clang_oe_generate(source + '/clang/lib/Driver/ToolChains/Linux.cpp', 'CLANG_EXTRA_OE_DISTRO_TRIPLE', triples)
}

do_patch[vardepsexclude] += "MULTILIBS MULTILIB_VARIANTS"
addtask do_preconfigure after do_patch
do_create_spdx[depends] += "${PN}:do_preconfigure"
