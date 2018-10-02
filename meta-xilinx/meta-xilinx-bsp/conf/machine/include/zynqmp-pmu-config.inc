
# Enable the use of zynqmp-pmu for specific recipes only
def zynqmp_pmu_enable_for_bpns(d):
    bpn = d.getVar("BPN")
    crossbpns = ["binutils", "gcc"]
    targetbpns = ["libgcc", "newlib", "libgloss"]
    if bpn in targetbpns:
        return True
    if bpn in crossbpns and bb.data.inherits_class('cross', d):
        return True
    return False

BBCLASSEXTEND_append = "${@' zynqmp-pmu' if zynqmp_pmu_enable_for_bpns(d) else ''}"

# Add the pkgarch to the extra list, this is done to avoid warnings about
# missing manifests due to implicit dependency chains on populate_sysroot when
# a recipe/task depends on a IMAGE_EXTRADEPENDS/do_deploy task which is
# provided by a zynqmp-pmu-* target.
PACKAGE_EXTRA_ARCHS_append = " microblazeel-v9.2-bs-cmp"

