SUMMARY = "Generates a combined SBoM for all world recipes"
LICENSE = "MIT"

INHIBIT_DEFAULT_DEPS = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit nopackages
deltask do_fetch
deltask do_unpack
deltask do_patch
deltask do_configure
deltask do_compile
deltask do_install

do_prepare_recipe_sysroot[deptask] = ""

WORLD_SBOM_EXCLUDE ?= ""

EXCLUDE_FROM_WORLD = "1"
SPDX_RECIPE_SBOM_NAME = "world-recipe-sbom"

python calculate_extra_depends() {
    exclude = set('${WORLD_SBOM_EXCLUDE}'.split())
    exclude |= set(f"{v}-{self_pn}" for v in '${MULTILIB_VARIANTS}'.split())
    exclude.add(self_pn)

    deps.extend(p for p in world_target if p not in exclude)
}

python() {
    # Ensure that do_create_recipe_sbom is the only dependency of do_build,
    # since the sole purpose of this recipe is to produce the world recipe SBoM
    d.setVarFlag("do_build", "deps", ["do_create_recipe_sbom"])
    d.setVarFlag("do_build", "deptask", "")
    d.setVarFlag("do_build", "rdeptask", "")
    d.setVarFlag("do_build", "recrdeptask", "")
}
